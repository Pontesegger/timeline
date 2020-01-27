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

package org.eclipse.nebula.timeline.layouts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.nebula.timeline.Helper;
import org.eclipse.nebula.timeline.ITimelineEvent;
import org.eclipse.nebula.timeline.TimeViewDetails;
import org.eclipse.nebula.timeline.figures.detail.track.lane.EventFigure;
import org.eclipse.nebula.timeline.figures.overview.OverviewCursorFigure;
import org.eclipse.nebula.timeline.figures.overview.OverviewFigure;

public class OverviewLayout extends XYLayout {

	@Override
	public Rectangle getConstraint(IFigure figure) {
		final TimeViewDetails timeViewDetails = Helper.getTimeViewDetails(figure);

		if (figure instanceof OverviewCursorFigure) {
			final Rectangle eventRectangle = (Rectangle) super.getConstraint(figure);
			final Rectangle parentBounds = figure.getParent().getBounds();

			final Rectangle overviewEventArea = timeViewDetails.scaleToOverview(eventRectangle.getCopy());
			overviewEventArea.setY(parentBounds.y());
			overviewEventArea.setHeight(parentBounds.height());
			overviewEventArea.setWidth(1);

			return overviewEventArea;

		} else {
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
