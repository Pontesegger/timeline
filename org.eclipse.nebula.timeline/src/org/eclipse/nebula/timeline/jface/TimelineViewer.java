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

package org.eclipse.nebula.timeline.jface;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.nebula.timeline.Helper;
import org.eclipse.nebula.timeline.ITimeline;
import org.eclipse.nebula.timeline.ITimelineEvent;
import org.eclipse.nebula.timeline.ITimelineFactory;
import org.eclipse.nebula.timeline.TimelineComposite;
import org.eclipse.nebula.timeline.figures.RootFigure;
import org.eclipse.nebula.timeline.figures.detail.track.TrackFigure;
import org.eclipse.nebula.timeline.figures.detail.track.TracksLayer;
import org.eclipse.nebula.timeline.figures.detail.track.lane.EventFigure;
import org.eclipse.nebula.timeline.figures.detail.track.lane.LaneFigure;
import org.eclipse.nebula.timeline.figures.overview.OverviewLayer;
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
		unregisterFigure(oldInput);
		registerFigure(input, getControl().getRootFigure());

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

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.viewers.StructuredViewer#doUpdateItem(org.eclipse.swt.widgets.Widget, java.lang.Object, boolean)
	 */
	@Override
	protected void doUpdateItem(Widget item, Object element, boolean fullMap) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.viewers.StructuredViewer#getSelectionFromWidget()
	 */
	@Override
	protected List getSelectionFromWidget() {
		// TODO fix implementation - never return null here!
		return Collections.EMPTY_LIST;
	}

	@Override
	protected void internalRefresh(Object element) {

		final IFigure figure = fElementToFigureMap.get(element);
		if (figure != null) {
			if (figure instanceof RootFigure) {
				unregisterModelElements(fElementToFigureMap.keySet());
				registerFigure(getInput(), getControl().getRootFigure());
				((RootFigure) figure).clear();

				final TracksLayer tracksLayer = Helper.getFigure(figure, TracksLayer.class);
				for (final Object track : getContentProvider().getTracks()) {
					final TrackFigure trackFigure = new TrackFigure(getLabelProvider().getText(track));

					tracksLayer.add(trackFigure);
					registerFigure(track, trackFigure);

					internalRefresh(track);
				}

			} else if (figure instanceof TrackFigure) {
				unregisterFigures(figure.getChildren());
				((TrackFigure) figure).removeAll();

				final Object track = getModelElementFor(figure);
				for (final Object lane : getContentProvider().getLanes(track)) {
					final LaneFigure laneFigure = new LaneFigure();

					figure.add(laneFigure);
					fElementToFigureMap.put(lane, laneFigure);

					internalRefresh(lane);
				}

			} else if (figure instanceof LaneFigure) {
				unregisterFigures(figure.getChildren());
				((LaneFigure) figure).removeAll();

				final Object lane = getModelElementFor(figure);
				for (final Object event : getContentProvider().getEvents(lane)) {
					final ITimelineEvent timelineEvent = getContentProvider().toEvent(event);

					final EventFigure eventFigure = new EventFigure(timelineEvent);
					eventFigure.setEventColor(figure.getForegroundColor());

					final PrecisionRectangle area = new PrecisionRectangle(timelineEvent.getStartTimestamp(), 0, timelineEvent.getDuration(), 1);

					figure.add(eventFigure, area);
					Helper.getTimeViewDetails(figure).addEvent(timelineEvent);
					fElementToFigureMap.put(event, eventFigure);

					final OverviewLayer overview = Helper.getFigure(figure, OverviewLayer.class);
					overview.addEvent(eventFigure);
				}
			}

			// TODO refresh cursors, timeevents
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

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.viewers.StructuredViewer#reveal(java.lang.Object)
	 */
	@Override
	public void reveal(Object element) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.viewers.StructuredViewer#setSelectionToWidget(java.util.List, boolean)
	 */
	@Override
	protected void setSelectionToWidget(List l, boolean reveal) {
		// TODO Auto-generated method stub

	}

	@Override
	public TimelineComposite getControl() {
		return fControl;
	}
}
