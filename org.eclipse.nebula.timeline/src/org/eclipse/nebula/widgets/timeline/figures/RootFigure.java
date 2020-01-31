/*******************************************************************************
 * Copyright (c) 2020 christian and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     christian - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.timeline.figures;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.nebula.widgets.timeline.Helper;
import org.eclipse.nebula.widgets.timeline.ICursor;
import org.eclipse.nebula.widgets.timeline.ITimelineEvent;
import org.eclipse.nebula.widgets.timeline.ITimelineFactory;
import org.eclipse.nebula.widgets.timeline.TimeBaseConverter;
import org.eclipse.nebula.widgets.timeline.figures.detail.DetailFigure;
import org.eclipse.nebula.widgets.timeline.figures.detail.cursor.CursorFigure;
import org.eclipse.nebula.widgets.timeline.figures.detail.cursor.CursorLayer;
import org.eclipse.nebula.widgets.timeline.figures.detail.track.TrackFigure;
import org.eclipse.nebula.widgets.timeline.figures.detail.track.TracksLayer;
import org.eclipse.nebula.widgets.timeline.figures.detail.track.lane.EventFigure;
import org.eclipse.nebula.widgets.timeline.figures.detail.track.lane.LaneFigure;
import org.eclipse.nebula.widgets.timeline.figures.overview.OverviewCursorFigure;
import org.eclipse.nebula.widgets.timeline.figures.overview.OverviewCursorLayer;
import org.eclipse.nebula.widgets.timeline.figures.overview.OverviewEventFigure;
import org.eclipse.nebula.widgets.timeline.figures.overview.OverviewFigure;
import org.eclipse.nebula.widgets.timeline.figures.overview.OverviewLayer;
import org.eclipse.nebula.widgets.timeline.figures.overview.OverviewSelectionLayer;
import org.eclipse.nebula.widgets.timeline.jface.DefaultTimelineStyleProvider;
import org.eclipse.nebula.widgets.timeline.jface.ITimelineStyleProvider;
import org.eclipse.nebula.widgets.timeline.listeners.ICursorListener;
import org.eclipse.swt.graphics.Color;

public class RootFigure extends Figure implements IStyledFigure {

	private final TimeBaseConverter fTimeViewDetails;

	private ITimelineStyleProvider fStyleProvider;

	private EventFigure fSelection;

	/** Maps a given detail figure to its counterpart in the overview area. */
	private final Map<IFigure, IFigure> fDetailToOverviewMap = new HashMap<>();

	private final ListenerList<ICursorListener> fCursorListener = new ListenerList<>();

	private final ResourceManager fResourceManager;

	private final DetailFigure fDetailFigure;

	private final OverviewFigure fOverviewFigure;

	public RootFigure(ResourceManager resourceManager) {
		fResourceManager = resourceManager;
		setStyleProvider(null);

		fTimeViewDetails = new TimeBaseConverter(this);

		final BorderLayout layout = new BorderLayout();
		layout.setVerticalSpacing(0);
		setLayoutManager(layout);

		setOpaque(true);
		updateStyle(fStyleProvider);

		fDetailFigure = new DetailFigure(getStyleProvider());
		add(fDetailFigure, BorderLayout.CENTER);

		fOverviewFigure = new OverviewFigure(getStyleProvider());
		add(fOverviewFigure, BorderLayout.BOTTOM);
	}

	public void setStyleProvider(ITimelineStyleProvider styleProvider) {
		fStyleProvider = (styleProvider != null) ? styleProvider : new DefaultTimelineStyleProvider(fResourceManager);

		fireStyleChanged();
	}

	public ResourceManager getResourceManager() {
		return fResourceManager;
	}

	/**
	 * Registers the given listener as a ICursorListener of this IFigure. Will be notified of cursor creation and deletion.
	 *
	 * @param listener
	 *            listener to register
	 */
	public void addCursorListener(ICursorListener listener) {
		fCursorListener.add(listener);
	}

	/**
	 * Unregisters the given listener, so that it will no longer receive notifications of cursor events
	 *
	 * @param listener
	 *            listener to unregister
	 */
	public void removeCursorListener(ICursorListener listener) {
		fCursorListener.remove(listener);
	}

	/**
	 * The style provider changed. Update style of all child elements
	 */
	private void fireStyleChanged() {
		final Set<Object> children = new HashSet<>();
		children.add(this);

		while (!children.isEmpty()) {
			final Object child = children.iterator().next();
			children.remove(child);
			children.addAll(((IFigure) child).getChildren());

			if (child instanceof IStyledFigure)
				((IStyledFigure) child).updateStyle(getStyleProvider());
		}
	}

	public ITimelineStyleProvider getStyleProvider() {
		return fStyleProvider;
	}

	/**
	 * Remove all tracks and cursors. Leaves the view empty.
	 */
	public void clear() {
		Helper.getFigure(this, TracksLayer.class).removeAll();
		Helper.getFigure(this, OverviewLayer.class).removeAll();

		Helper.getFigure(this, CursorLayer.class).removeAll();
		Helper.getFigure(this, OverviewCursorLayer.class).removeAll();
	}

	public TimeBaseConverter getTimeViewDetails() {
		return fTimeViewDetails;
	}

	/**
	 * The offset or the scaling (or both) changed. We need to update the detail area and the damaged part of the overview area.
	 */
	public void fireTimebaseChanged() {

		// fresh layout for the detail area
		for (final LaneFigure lane : Helper.getLanes(this))
			lane.revalidate();

		Helper.getFigure(this, CursorLayer.class).revalidate();

		getUpdateManager().addDirtyRegion(fDetailFigure, fDetailFigure.getBounds());

		Helper.getFigure(this, OverviewSelectionLayer.class).revalidate();
	}

	public void zoomIn(int zoomCenterX) {

		zoom(getStyleProvider().getZoomFactor(), zoomCenterX);
	}

	public void zoomOut(int zoomCenterX) {
		zoom(1 / getStyleProvider().getZoomFactor(), zoomCenterX);
	}

	public void zoom(double factor, int zoomCenterX) {
		getTimeViewDetails().zoom(factor, zoomCenterX);
	}

	@Override
	public void updateStyle(ITimelineStyleProvider styleProvider) {
		setBackgroundColor(styleProvider.getBackgroundColor());
	}

	public TrackFigure createTrackFigure(String title) {
		return new TrackFigure(title, getStyleProvider());
	}

	/**
	 * Create a new cursor model instance.
	 *
	 * @param eventTime
	 *            time to set cursor to
	 * @return cursor instance
	 */
	public ICursor createCursor(long eventTime) {
		final ICursor cursor = ITimelineFactory.eINSTANCE.createCursor();
		cursor.setTimestamp(eventTime);

		final CursorFigure cursorFigure = createCursorFigure(cursor);

		for (final ICursorListener listener : fCursorListener) {
			try {
				listener.notifyCursorCreated(cursor, cursorFigure);
			} catch (final Throwable t) {
				// silently ignore
			}
		}

		return cursor;
	}

	/**
	 * Delete a cursor.
	 *
	 * @param cursor
	 *            cursor to delete
	 */
	public void deleteCursor(ICursor cursor) {
		final IFigure cursorLayer = Helper.getFigure(this, CursorLayer.class);
		final LayoutManager layoutManager = cursorLayer.getLayoutManager();
		for (final Object child : cursorLayer.getChildren()) {
			if (cursor.equals(layoutManager.getConstraint((IFigure) child))) {
				deleteCursorFigure((CursorFigure) child);

				for (final ICursorListener listener : fCursorListener) {
					try {
						listener.notifyCursorDeleted(cursor);
					} catch (final Throwable t) {
						// silently ignore
					}
				}

				break;
			}
		}
	}

	/**
	 * Set the selection figure. Stores the selected element and highlights the figure.
	 *
	 * @param eventFigure
	 *            event figure to select
	 */
	public void setSelection(EventFigure eventFigure) {
		if (fSelection != null)
			getStyleProvider().unselectEvent(fSelection);

		fSelection = eventFigure;

		if (fSelection != null)
			getStyleProvider().selectEvent(fSelection);
	}

	/**
	 * Get the selected figure.
	 *
	 * @return selected figure or null
	 */
	public EventFigure getSelection() {
		return fSelection;
	}

	/**
	 * Create a new figure for the given event.
	 *
	 * @param parent
	 *            parent figure for new eventFigure
	 * @param event
	 *            event to create figure for
	 * @return created eventFigure in detail area
	 */
	public EventFigure createEventFigure(LaneFigure parent, ITimelineEvent event) {

		final EventFigure eventFigure = new EventFigure(event);
		parent.add(eventFigure, event);

		Color eventColor = parent.getForegroundColor();
		if (event.getColorCode() != null)
			eventColor = getStyleProvider().getColor(event.getRgb());

		eventFigure.setEventColor(eventColor);

		Helper.getTimeViewDetails(parent).addEvent(event);

		final OverviewLayer overview = Helper.getFigure(parent, OverviewLayer.class);
		final OverviewEventFigure overviewEventFigure = overview.addEvent(eventFigure);

		fDetailToOverviewMap.put(eventFigure, overviewEventFigure);

		return eventFigure;
	}

	/**
	 * Delete an eventFigure from its lane and the overview are.
	 *
	 * @param eventFigure
	 *            figure to delete
	 */
	public void deleteEventFigure(EventFigure eventFigure) {
		removeFigure(eventFigure);

		final IFigure overvievFigure = fDetailToOverviewMap.remove(eventFigure);
		if (overvievFigure != null)
			removeFigure(overvievFigure);

		// TODO recalculate total area in TimeViewDetails
	}

	private void removeFigure(IFigure figure) {
		final IFigure parent = figure.getParent();
		parent.remove(figure);

		// TODO rather mark this area as damaged
		parent.revalidate();
	}

	/**
	 * Create a cursor figure.
	 *
	 * @param cursor
	 *            cursor to create
	 * @return created figure in detail area
	 */
	public CursorFigure createCursorFigure(ICursor cursor) {
		final CursorFigure cursorFigure = new CursorFigure(getStyleProvider());
		final CursorLayer cursorLayer = Helper.getFigure(this, CursorLayer.class);
		cursorLayer.add(cursorFigure, cursor);

		Helper.getTimeViewDetails(this).addEvent(cursor);

		final OverviewCursorFigure overviewCursorFigure = new OverviewCursorFigure(getStyleProvider());
		final OverviewCursorLayer overviewCursorLayer = Helper.getFigure(this, OverviewCursorLayer.class);
		overviewCursorLayer.add(overviewCursorFigure, cursor);

		fDetailToOverviewMap.put(cursorFigure, overviewCursorFigure);

		return cursorFigure;
	}

	/**
	 * Delete a cursor figure from the detail and overview area.
	 *
	 * @param cursorFigure
	 *            figure to delete
	 */
	public void deleteCursorFigure(CursorFigure cursorFigure) {
		removeFigure(cursorFigure);

		final IFigure overviewCursorFigure = fDetailToOverviewMap.remove(cursorFigure);
		if (overviewCursorFigure != null)
			removeFigure(overviewCursorFigure);
	}

	/**
	 * Update a provided cursor figure with fresh cursor data.
	 *
	 * @param figure
	 *            figure to update
	 * @param cursor
	 *            new cursor data
	 */
	public void updateCursorFigure(IFigure figure, ICursor cursor) {
		updateFigureConstraint(figure, cursor);
	}

	/**
	 * Update a provided event figure with fresh event data.
	 *
	 * @param figure
	 *            figure to update
	 * @param event
	 *            new cursor data
	 */
	public void updateEventFigure(IFigure figure, ITimelineEvent event) {
		updateFigureConstraint(figure, event);
	}

	/**
	 * Set a new constraint for a given figure.
	 *
	 * @param figure
	 *            figure to update
	 * @param constraint
	 *            new constraint
	 */
	private void updateFigureConstraint(IFigure figure, Object constraint) {
		figure.getParent().getLayoutManager().setConstraint(figure, constraint);
		figure.revalidate();
	}

	/**
	 * Update the title of a given track figure.
	 *
	 * @param figure
	 *            figure to update
	 * @param title
	 *            title to set
	 */
	public void updateTrackFigure(IFigure figure, String title) {
		if (figure instanceof TrackFigure) {
			((TrackFigure) figure).setTitle(title);
			((TrackFigure) figure).updateStyle(getStyleProvider());
		}
	}
}
