/*******************************************************************************
 * Copyright (c) 2020 ponteseg and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ponteseg - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.timeline.jface;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.nebula.timeline.ICursor;
import org.eclipse.nebula.timeline.ITimeline;
import org.eclipse.nebula.timeline.ITimelineEvent;
import org.eclipse.nebula.timeline.ITimelineFactory;
import org.eclipse.nebula.widgets.timeline.Helper;
import org.eclipse.nebula.widgets.timeline.TimeViewDetails;
import org.eclipse.nebula.widgets.timeline.TimelineComposite;
import org.eclipse.nebula.widgets.timeline.figures.RootFigure;
import org.eclipse.nebula.widgets.timeline.figures.detail.cursor.CursorFigure;
import org.eclipse.nebula.widgets.timeline.figures.detail.cursor.CursorLayer;
import org.eclipse.nebula.widgets.timeline.figures.detail.track.TrackFigure;
import org.eclipse.nebula.widgets.timeline.figures.detail.track.TracksLayer;
import org.eclipse.nebula.widgets.timeline.figures.detail.track.lane.EventFigure;
import org.eclipse.nebula.widgets.timeline.figures.detail.track.lane.LaneFigure;
import org.eclipse.nebula.widgets.timeline.figures.overview.OverviewCursorLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;

public class TimelineViewer extends StructuredViewer {

	private final TimelineComposite fControl;

	private final ModelMap fElementToFigureMap = new ModelMap();

	/**
	 * Create a timeline viewer. The viewer will automatically populate input, a content provider and a label provider. To get the model, use
	 * {@link #getModel()}. When replacing the input, make sure to also replace content and label providers according to your used datatypes.
	 *
	 * @param parent
	 *            parent composite
	 * @param flags
	 *            SWT flags
	 */
	public TimelineViewer(Composite parent, int flags) {

		fControl = new TimelineComposite(parent, flags);

		setContentProvider(new DefaultTimelineContentProvider());
		setLabelProvider(new DefaultTimelineLabelProvider());
		setInput(ITimelineFactory.eINSTANCE.createTimeline());
	}

	/**
	 * Create a timeline viewer. The viewer will automatically populate input, a content provider and a label provider. To get the model, use
	 * {@link #getModel()}. When replacing the input, make sure to also replace content and label providers according to your used datatypes.
	 *
	 * @param parent
	 *            parent composite
	 */
	public TimelineViewer(Composite parent) {
		this(parent, SWT.NONE);
	}

	public void setStyleProvider(ITimelineStyleProvider styleProvider) {
		getControl().getRootFigure().setStyleProvider(styleProvider);
	}

	public ITimelineStyleProvider getStyleProvider() {
		return getControl().getRootFigure().getStyleProvider();
	}

	@Override
	protected void inputChanged(Object input, Object oldInput) {
		// TODO keep set of damaged figures as small as possible
		fElementToFigureMap.clear();
		registerFigure(input, getControl().getRootFigure());

		final ITimelineContentProvider contentProvider = getContentProvider();
		if (contentProvider != null)
			contentProvider.inputChanged(this, oldInput, input);

		super.inputChanged(input, oldInput);
	}

	/**
	 * Get the timeline model backing this viewer. In case a custom model type is used, this method returns <code>null</code>.
	 *
	 * @return timeline model or null
	 */
	public ITimeline getModel() {
		final Object input = getInput();

		return (input instanceof ITimeline) ? (ITimeline) input : null;
	}

	@Override
	public void setContentProvider(IContentProvider provider) {
		if (!(provider instanceof ITimelineContentProvider))
			throw new IllegalArgumentException("Content provider needs to implement ITimelineContentProvider");

		super.setContentProvider(provider);
	}

	@Override
	public ITimelineContentProvider getContentProvider() {
		return (ITimelineContentProvider) super.getContentProvider();
	}

	@Override
	public void setLabelProvider(IBaseLabelProvider labelProvider) {
		if (!(labelProvider instanceof ILabelProvider))
			throw new IllegalArgumentException("Label provider needs to implement ILabelProvider");

		super.setLabelProvider(labelProvider);
	}

	@Override
	public ILabelProvider getLabelProvider() {
		return (ILabelProvider) super.getLabelProvider();
	}

	@Override
	protected Widget doFindInputItem(Object element) {
		return null;
	}

	@Override
	protected Widget doFindItem(Object element) {
		return null;
	}

	private boolean isRootElement(Object element) {
		return getInput().equals(element);
	}

	private boolean isTrackElement(Object element) {
		return getTrackElements().contains(element);
	}

	private boolean isLaneElement(Object element) {
		return getLaneElements().contains(element);
	}

	private boolean isEventElement(Object element) {
		return geEventElements().contains(element);
	}

	private boolean isCursorElement(Object element) {
		return getCursorElements().contains(element);
	}

	private Collection<Object> getCursorElements() {
		return Arrays.asList(getContentProvider().getCursors(getInput()));
	}

	private Collection<Object> getTrackElements() {
		return Arrays.asList(getContentProvider().getTracks(getInput()));
	}

	private Collection<Object> getLaneElements() {
		final Collection<Object> lanes = new HashSet<>();
		for (final Object track : getTrackElements())
			lanes.addAll(Arrays.asList(getContentProvider().getLanes(track)));

		return lanes;
	}

	private Collection<Object> geEventElements() {
		final Collection<Object> events = new HashSet<>();
		for (final Object track : getLaneElements())
			events.addAll(Arrays.asList(getContentProvider().getEvents(track)));

		return events;
	}

	@Override
	public void update(Object element, String[] properties) {
		final IFigure figure = fElementToFigureMap.get(element);
		if (figure != null) {

			if (isCursorElement(element))
				getControl().getRootFigure().updateCursorFigure(figure, getContentProvider().toCursor(element));

			if (isTrackElement(element))
				getControl().getRootFigure().updateTrackFigure(figure, element.toString());

			else if (isEventElement(element))
				getControl().getRootFigure().updateEventFigure(figure, getContentProvider().toEvent(element));
		}
	}

	@Override
	protected void internalRefresh(Object element) {

		final IFigure figure = fElementToFigureMap.get(element);
		if (figure != null) {
			if (figure instanceof RootFigure) {
				unregisterModelElements(new HashSet<>(fElementToFigureMap.keySet()));
				registerFigure(getInput(), getControl().getRootFigure());
				((RootFigure) figure).clear();
				Helper.getTimeViewDetails(figure).resetEventArea();

				final TracksLayer tracksLayer = Helper.getFigure(figure, TracksLayer.class);
				for (final Object track : getContentProvider().getTracks(getInput())) {
					final TrackFigure trackFigure = ((RootFigure) figure).createTrackFigure(getLabelProvider().getText(track));

					tracksLayer.add(trackFigure);
					registerFigure(track, trackFigure);

					internalRefresh(track);
				}

				for (final Object cursorElement : getContentProvider().getCursors(getInput())) {
					final ICursor cursor = getContentProvider().toCursor(cursorElement);

					final CursorFigure cursorFigure = ((RootFigure) figure).createCursorFigure(cursor);
					registerFigure(cursorElement, cursorFigure);
				}

			} else if (figure instanceof TrackFigure) {
				unregisterFigures(figure.getChildren());
				((TrackFigure) figure).removeAll();

				final Object track = getModelElementFor(figure);
				for (final Object lane : getContentProvider().getLanes(track)) {
					final LaneFigure laneFigure = new LaneFigure(getStyleProvider());

					figure.add(laneFigure);
					fElementToFigureMap.put(lane, laneFigure);

					internalRefresh(lane);
				}

			} else if (figure instanceof LaneFigure) {
				unregisterFigures(figure.getChildren());
				for (final EventFigure eventFigure : ((LaneFigure) figure).getChildEventFigures())
					getControl().getRootFigure().deleteEventFigure(eventFigure);

				final Object lane = getModelElementFor(figure);
				for (final Object event : getContentProvider().getEvents(lane)) {
					final ITimelineEvent timelineEvent = getContentProvider().toEvent(event);

					final EventFigure eventFigure = getControl().getRootFigure().createEventFigure(((LaneFigure) figure), timelineEvent);
					fElementToFigureMap.put(event, eventFigure);
				}

			} else if (figure instanceof CursorFigure) {

				if (Arrays.asList(getContentProvider().getCursors(getInput())).contains(element)) {
					// this cursor is still available in the model
					// TODO refresh cursor style

				} else {
					// cursor got deleted from the model
					Helper.getFigure(figure, CursorLayer.class).remove(figure);
					unregisterModelElement(element);
				}

				final RootFigure rootFigure = Helper.getRootFigure(figure);
				Helper.getFigure(rootFigure, CursorLayer.class).revalidate();
				Helper.getFigure(rootFigure, OverviewCursorLayer.class).revalidate();
			}

			// TODO refresh cursors, timeevents

		} else {
			// the object does not have a figure representation
			if (Arrays.asList(getContentProvider().getCursors(getInput())).contains(element)) {
				// this is a new cursor
				final ICursor cursor = getContentProvider().toCursor(element);
				final CursorFigure cursorFigure = getControl().getRootFigure().createCursorFigure(cursor);
				registerFigure(element, cursorFigure);
			}
		}
	}

	private Object getModelElementFor(IFigure figure) {
		return fElementToFigureMap.getKey(figure);
	}

	private void unregisterModelElements(Collection<?> modelElements) {
		for (final Object element : modelElements)
			unregisterModelElement(element);
	}

	private void unregisterModelElement(Object modelElement) {
		fElementToFigureMap.remove(modelElement);
	}

	private void unregisterFigures(Collection<?> figures) {
		for (final Object element : figures)
			unregisterFigure(element);
	}

	private void unregisterFigure(Object modelElement) {
		fElementToFigureMap.removeValue(modelElement);
	}

	private void registerFigure(Object modelElement, IFigure figure) {
		fElementToFigureMap.put(modelElement, figure);
	}

	public void createCursor(ICursor cursor) {
		getControl().getRootFigure().createCursorFigure(cursor);
	}

	public void deleteCursor(ICursor cursor) {
		getControl().getRootFigure().deleteCursor(cursor);
	}

	@Override
	public void reveal(Object element) {
		element = getContentProvider().toEvent(element);
		if (element == null)
			element = getContentProvider().toCursor(element);

		if (element instanceof ITimelineEvent) {
			final TimeViewDetails timeViewDetails = Helper.getTimeViewDetails(getControl().getRootFigure());
			timeViewDetails.revealEvent(new PrecisionRectangle(((ITimelineEvent) element).getStartTimestamp(), 0, ((ITimelineEvent) element).getDuration(), 1));

		} else if (element instanceof ICursor) {
			final TimeViewDetails timeViewDetails = Helper.getTimeViewDetails(getControl().getRootFigure());
			timeViewDetails.revealEvent(new PrecisionRectangle(((ICursor) element).getTimestamp(), 0, 1, 1));
		}
	}

	@Override
	protected List getSelectionFromWidget() {
		final EventFigure selectedFigure = getControl().getRootFigure().getSelection();
		if (selectedFigure != null) {
			final Object modelElement = fElementToFigureMap.getKey(selectedFigure);
			if (modelElement != null)
				return Arrays.asList(modelElement);
		}

		return Collections.EMPTY_LIST;
	}

	@Override
	protected void setSelectionToWidget(List l, boolean reveal) {
		if (!l.isEmpty()) {
			final ITimelineEvent event = getContentProvider().toEvent(l.get(0));
			final IFigure eventFigure = fElementToFigureMap.get(event);
			if (eventFigure instanceof EventFigure)
				getControl().getRootFigure().setSelection((EventFigure) eventFigure);

		} else
			getControl().getRootFigure().setSelection(null);
	}

	@Override
	public TimelineComposite getControl() {
		return fControl;
	}

	@Override
	protected void doUpdateItem(Widget item, Object element, boolean fullMap) {
		// not needed for this viewer
	}

	private class ModelMap extends HashMap<Object, IFigure> {

		private static final long serialVersionUID = 6568720330224087046L;

		private final Map<IFigure, Object> fReverseMap = new HashMap<>();

		@Override
		public IFigure put(Object key, IFigure value) {
			fReverseMap.put(value, key);

			return super.put(key, value);
		}

		public Object getKey(IFigure value) {
			return fReverseMap.get(value);
		}

		@Override
		public IFigure remove(Object key) {
			final IFigure value = super.remove(key);
			fReverseMap.remove(value);

			return value;
		}

		public IFigure removeValue(Object modelElement) {
			return remove(fReverseMap.get(modelElement));
		}
	}
}
