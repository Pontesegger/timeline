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

package org.eclipse.nebula.timeline.figures.overview;

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.TreeSearch;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.nebula.timeline.Helper;
import org.eclipse.nebula.timeline.ITimelineEvent;
import org.eclipse.nebula.timeline.TimeViewDetails;
import org.eclipse.nebula.timeline.figures.IStyledFigure;
import org.eclipse.nebula.timeline.figures.detail.track.lane.EventFigure;
import org.eclipse.nebula.timeline.jface.ITimelineStyleProvider;
import org.eclipse.nebula.timeline.listeners.OverviewSelector;

public class OverviewLayer extends FreeformLayer implements IStyledFigure {

	public OverviewLayer(ITimelineStyleProvider styleProvider) {
		updateStyle(styleProvider);

		setLayoutManager(new OverviewLayout());

		new OverviewSelector(this);
	}

	public void addEvent(EventFigure eventFigure) {
		final OverviewEventFigure overviewFigure = new OverviewEventFigure(eventFigure);
		overviewFigure.setForegroundColor(eventFigure.getForegroundColor());

		add(overviewFigure, eventFigure);
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
			final TimeViewDetails timeViewDetails = Helper.getTimeViewDetails(figure);

			final EventFigure eventFigure = (EventFigure) super.getConstraint(figure);
			final ITimelineEvent event = eventFigure.getEvent();
			final Rectangle eventRectangle = new PrecisionRectangle(event.getStartTimestamp(), 0, event.getDuration(), 1);

			final Rectangle overviewEventArea = timeViewDetails.scaleToOverview(eventRectangle);
			overviewEventArea.setHeight(OverviewFigure.EVENT_HEIGHT);
			overviewEventArea
					.setY(OverviewFigure.VERTICAL_INDENT + ((OverviewFigure.EVENT_HEIGHT + OverviewFigure.Y_PADDING) * Helper.getLaneIndex(eventFigure)));
			if (overviewEventArea.width() == 0)
				overviewEventArea.setWidth(1);

			return overviewEventArea;
		}
	}
}
