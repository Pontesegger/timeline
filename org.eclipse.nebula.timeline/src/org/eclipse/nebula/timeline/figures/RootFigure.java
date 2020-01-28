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

package org.eclipse.nebula.timeline.figures;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.nebula.timeline.Helper;
import org.eclipse.nebula.timeline.ICursor;
import org.eclipse.nebula.timeline.ITimelineFactory;
import org.eclipse.nebula.timeline.TimeViewDetails;
import org.eclipse.nebula.timeline.figures.detail.DetailFigure;
import org.eclipse.nebula.timeline.figures.detail.cursor.CursorFigure;
import org.eclipse.nebula.timeline.figures.detail.cursor.CursorLayer;
import org.eclipse.nebula.timeline.figures.detail.track.TrackFigure;
import org.eclipse.nebula.timeline.figures.detail.track.TracksLayer;
import org.eclipse.nebula.timeline.figures.detail.track.lane.LaneFigure;
import org.eclipse.nebula.timeline.figures.overview.OverviewCursorFigure;
import org.eclipse.nebula.timeline.figures.overview.OverviewCursorLayer;
import org.eclipse.nebula.timeline.figures.overview.OverviewFigure;
import org.eclipse.nebula.timeline.figures.overview.OverviewLayer;
import org.eclipse.nebula.timeline.jface.DefaultTimelineStyleProvider;
import org.eclipse.nebula.timeline.jface.ITimelineStyleProvider;
import org.eclipse.nebula.timeline.jface.TimelineViewer;

public class RootFigure extends Figure implements IStyledFigure {

	private static final double ZOOM_FACTOR = 1.2d;

	private final TimeViewDetails fTimeViewDetails;

	private ITimelineStyleProvider fStyleProvider = new DefaultTimelineStyleProvider();

	private TimelineViewer fViewer;

	public RootFigure() {
		fTimeViewDetails = new TimeViewDetails(this);

		final BorderLayout layout = new BorderLayout();
		layout.setVerticalSpacing(0);
		setLayoutManager(layout);

		setOpaque(true);
		updateStyle(fStyleProvider);

		add(new DetailFigure(getStyleProvider()), BorderLayout.CENTER);
		add(new OverviewFigure(getStyleProvider()), BorderLayout.BOTTOM);
	}

	public void setStyleProvider(ITimelineStyleProvider styleProvider) {
		fStyleProvider = styleProvider;

		fireStyleChanged();
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
	}

	public TimeViewDetails getTimeViewDetails() {
		return fTimeViewDetails;
	}

	public void fireTimebaseChanged() {

		for (final LaneFigure lane : Helper.getLanes(this))
			lane.revalidate();

		Helper.getFigure(this, OverviewLayer.class).revalidate();
		Helper.getFigure(this, CursorLayer.class).revalidate();

		// FIXME sets the whole canvas dirty. The dirty region is actually smaller and we might be more performant if we narrow down the area to redraw
		getUpdateManager().addDirtyRegion(this, getBounds());
	}

	public void zoomIn(int zoomCenterX) {
		zoom(ZOOM_FACTOR, zoomCenterX);
	}

	public void zoomOut(int zoomCenterX) {
		zoom(1 / ZOOM_FACTOR, zoomCenterX);
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

	public CursorFigure addCursorFigure(ICursor cursor) {
		final CursorFigure cursorFigure = new CursorFigure(getStyleProvider());

		final CursorLayer cursorLayer = Helper.getFigure(this, CursorLayer.class);
		cursorLayer.add(cursorFigure, cursor);

		return cursorFigure;
	}

	public OverviewCursorFigure addOverviewCursorFigure(ICursor cursor) {
		final OverviewCursorFigure cursorFigure = new OverviewCursorFigure(getStyleProvider());

		final OverviewCursorLayer cursorLayer = Helper.getFigure(this, OverviewCursorLayer.class);
		cursorLayer.add(cursorFigure, cursor);

		return cursorFigure;
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

		if ((fViewer != null) && (fViewer.getEditingSupport() != null)) {
			// let viewer take care of cursor addition
			fViewer.createCursor(cursor);

		} else {
			// manually add cursor
			addCursorFigure(cursor);
			addOverviewCursorFigure(cursor);
		}

		return cursor;
	}

	/**
	 * Remove a cursor.
	 *
	 * @param cursor
	 *            cursor to remove
	 */
	public void removeCursor(ICursor cursor) {
		if ((fViewer != null) && (fViewer.getEditingSupport() != null)) {
			// let viewer take care of cursor removal
			fViewer.removeCursor(cursor);

		} else {
			// manually remove figures
			for (final Class clazz : new Class[] { CursorLayer.class, OverviewCursorLayer.class }) {
				final IFigure cursorLayer = (IFigure) Helper.getFigure(this, clazz);
				final LayoutManager layoutManager = cursorLayer.getLayoutManager();
				for (final Object child : cursorLayer.getChildren()) {
					if (cursor.equals(layoutManager.getConstraint((IFigure) child))) {
						cursorLayer.remove((IFigure) child);
						break;
					}
				}
			}
		}
	}

	/**
	 * Set the JFace viewer.
	 *
	 * @param viewer
	 *            JFace viewer
	 */
	public void setViewer(TimelineViewer viewer) {
		fViewer = viewer;
	}
}
