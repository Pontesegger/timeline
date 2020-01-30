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

package org.eclipse.nebula.widgets.timeline;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.nebula.widgets.timeline.figures.RootFigure;
import org.eclipse.nebula.widgets.timeline.figures.detail.track.TracksLayer;
import org.eclipse.nebula.widgets.timeline.figures.detail.track.lane.EventFigure;
import org.eclipse.nebula.widgets.timeline.figures.detail.track.lane.LaneFigure;
import org.eclipse.nebula.widgets.timeline.figures.overview.OverviewCursorLayer;
import org.eclipse.nebula.widgets.timeline.figures.overview.OverviewLayer;

/**
 * @author christian
 *
 */
public class Helper {

	@Deprecated
	private Helper() {
	}

	public static RootFigure getRootFigure(IFigure figure) {
		if ((figure instanceof RootFigure) || (figure == null))
			return (RootFigure) figure;

		return getRootFigure(figure.getParent());
	}

	public static TimeViewDetails getTimeViewDetails(IFigure figure) {
		return getRootFigure(figure).getTimeViewDetails();
	}

	public static List<LaneFigure> getLanes(IFigure figure) {
		final List<LaneFigure> lanes = new ArrayList<>();

		final TracksLayer tracksLayer = getFigure(figure, TracksLayer.class);

		for (final Object trackFigure : tracksLayer.getChildren())
			lanes.addAll(((IFigure) trackFigure).getChildren());

		return lanes;
	}

	/**
	 * Get the absolute index of the lane where figure belongs to.
	 *
	 * @param figure
	 *            EventFigure of the lane to retrieve index from
	 * @return lane index
	 */
	public static int getLaneIndex(EventFigure figure) {
		final List<LaneFigure> lanes = getLanes(figure);
		return lanes.indexOf(figure.getParent());
	}

	/**
	 * Get total number of lanes. Sums up lanes of all tracks.
	 *
	 * @param figure
	 *            any figure of the timeline diagram
	 * @return total amount of lanes
	 */
	public static int getLaneCount(IFigure figure) {
		return getLanes(figure).size();
	}

	public static <T> T getFigure(IFigure figure, Class<T> clazz) {
		final RootFigure rootFigure = getRootFigure(figure);

		return findFigure(rootFigure, clazz);
	}

	private static <T> T findFigure(IFigure figure, Class<T> clazz) {
		if (clazz.isAssignableFrom(figure.getClass()))
			return (T) figure;

		if (figure instanceof TracksLayer)
			return null;

		if (figure instanceof OverviewLayer)
			return null;

		if (figure instanceof OverviewCursorLayer)
			return null;

		for (final Object child : figure.getChildren()) {
			final Object candidate = findFigure((IFigure) child, clazz);
			if (candidate != null)
				return (T) candidate;
		}

		return null;
	}
}
