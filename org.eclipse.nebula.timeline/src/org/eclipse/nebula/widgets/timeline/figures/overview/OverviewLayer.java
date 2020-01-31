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

package org.eclipse.nebula.widgets.timeline.figures.overview;

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.TreeSearch;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.nebula.widgets.timeline.Helper;
import org.eclipse.nebula.widgets.timeline.ITimelineEvent;
import org.eclipse.nebula.widgets.timeline.TimeBaseConverter;
import org.eclipse.nebula.widgets.timeline.Timing;
import org.eclipse.nebula.widgets.timeline.figures.IStyledFigure;
import org.eclipse.nebula.widgets.timeline.figures.detail.track.lane.EventFigure;
import org.eclipse.nebula.widgets.timeline.jface.ITimelineStyleProvider;
import org.eclipse.nebula.widgets.timeline.listeners.OverviewSelector;

public class OverviewLayer extends FreeformLayer implements IStyledFigure {

	private static final int MINIMUM_WIDTH = 1;

	public OverviewLayer(ITimelineStyleProvider styleProvider) {
		updateStyle(styleProvider);

		setLayoutManager(new OverviewLayout());

		new OverviewSelector(this);
	}

	public OverviewEventFigure addEvent(EventFigure eventFigure) {
		final OverviewEventFigure overviewEventFigure = new OverviewEventFigure(eventFigure);
		overviewEventFigure.setForegroundColor(eventFigure.getForegroundColor());

		add(overviewEventFigure, eventFigure);

		return overviewEventFigure;
	}

	@Override
	public boolean containsPoint(int x, int y) {
		return getBounds().contains(x, y);
	}

	@Override
	public void updateStyle(ITimelineStyleProvider styleProvider) {
		setBorder(styleProvider.getOverviewAreaBorder());
	}

	@Override
	protected IFigure findDescendantAtExcluding(int x, int y, TreeSearch search) {
		// do not dig deeper in the figure hierarchy
		return null;
	}

	private class OverviewLayout extends XYLayout {

		@Override
		public Rectangle getConstraint(IFigure figure) {
			final TimeBaseConverter timeViewDetails = Helper.getTimeViewDetails(figure);

			final EventFigure eventFigure = (EventFigure) super.getConstraint(figure);
			final ITimelineEvent event = eventFigure.getEvent();

			final Timing scaledTiming = timeViewDetails.scaleToOverview(event.getTiming());
			final Rectangle overviewEventArea = new PrecisionRectangle(scaledTiming.left(),
					OverviewFigure.VERTICAL_INDENT + ((OverviewFigure.EVENT_HEIGHT + OverviewFigure.Y_PADDING) * Helper.getLaneIndex(eventFigure)),
					scaledTiming.getDuration(), OverviewFigure.EVENT_HEIGHT);

			if (overviewEventArea.width() < MINIMUM_WIDTH)
				overviewEventArea.setWidth(MINIMUM_WIDTH);

			return overviewEventArea;
		}
	}
}
