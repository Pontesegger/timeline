/*******************************************************************************
 * Copyright (c) 2019 christian and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     christian - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.timeline.figures.detail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.nebula.widgets.timeline.Helper;
import org.eclipse.nebula.widgets.timeline.TimeBaseConverter;
import org.eclipse.nebula.widgets.timeline.Timing;
import org.eclipse.nebula.widgets.timeline.borders.EmptyBorder;
import org.eclipse.nebula.widgets.timeline.figures.detail.track.TracksFigure;
import org.eclipse.nebula.widgets.timeline.jface.ITimelineStyleProvider;
import org.eclipse.nebula.widgets.timeline.listeners.DetailAreaListener;

public class DetailFigure extends Figure {

	private static final int MIN_STEP_SIZE = 120;

	private static final List<Integer> STEP_SIZE_CANDIDATES = Arrays.asList(1, 2, 5, 10, 20, 25, 50, 100);

	public DetailFigure(ITimelineStyleProvider styleProvider) {
		final BorderLayout layout = new BorderLayout();
		layout.setHorizontalSpacing(0);
		layout.setVerticalSpacing(0);
		setLayoutManager(layout);

		setBorder(new EmptyBorder(new Insets(10)));

		add(new TracksFigure(styleProvider), BorderLayout.CENTER);

		final TimeAxisFigure timeAxisFigure = new TimeAxisFigure(styleProvider);
		add(timeAxisFigure, BorderLayout.BOTTOM);

		new DetailAreaListener(this);
	}

	/**
	 * Return a map of marker positions to be drawn on the detail area.
	 *
	 * @return map (timeValue, pixelOffset)
	 */
	public Map<Long, Integer> getMarkerPositions() {

		final TimeBaseConverter timeViewDetails = Helper.getTimeViewDetails(this);

		final Map<Long, Integer> markerPositions = new HashMap<>();
		for (final Long eventTime : getEventTimeMarkerPositions())
			markerPositions.put(eventTime, timeViewDetails.eventTimeToScreenOffset(eventTime));

		return markerPositions;
	}

	/**
	 * Calculate the step size in eventTime. Therefore the screen with is divided in segments >= MIN_STEP_SIZE pixels. Then we try to find the closest candidate
	 * of STEP_SIZE_CANDIDATES * n that fits to the calculated screen interval.
	 *
	 * @return step size in eventTime
	 */
	private int getStepSize() {
		final TimeBaseConverter timeViewDetails = Helper.getTimeViewDetails(this);

		final double steps = timeViewDetails.getScreenArea().getDuration() / MIN_STEP_SIZE;
		double stepSizeInEventTime = timeViewDetails.getVisibleEventArea().getDuration() / steps;
		int factor = 1;
		while (stepSizeInEventTime >= 100) {
			stepSizeInEventTime /= 10;
			factor *= 10;
		}

		final long preliminarySize = Math.round(stepSizeInEventTime);
		final int niceStepSize = STEP_SIZE_CANDIDATES.stream().filter(c -> c >= preliminarySize).findFirst().get();

		return niceStepSize * factor;
	}

	/**
	 * Get timestamps in eventTime for markers that are visible on screen.
	 *
	 * @return list of timestamps (in eventTime) to draw markers for
	 */
	private List<Long> getEventTimeMarkerPositions() {
		final List<Long> positions = new ArrayList<>();

		final TimeBaseConverter timeViewDetails = Helper.getTimeViewDetails(this);
		final Timing visibleEventArea = timeViewDetails.getVisibleEventArea();

		final int stepSize = getStepSize();
		final long startValue = (long) ((Math.floor((visibleEventArea.left()) / stepSize) + 1) * stepSize);

		for (long pos = startValue; pos < visibleEventArea.right(); pos += stepSize)
			positions.add(pos);

		return positions;
	}
}
