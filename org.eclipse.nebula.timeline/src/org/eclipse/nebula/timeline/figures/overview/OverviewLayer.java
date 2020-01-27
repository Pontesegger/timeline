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
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.nebula.timeline.figures.detail.track.lane.EventFigure;
import org.eclipse.nebula.timeline.jface.ITimelineStyleProvider;
import org.eclipse.nebula.timeline.layouts.OverviewLayout;
import org.eclipse.nebula.timeline.listeners.OverviewSelector;

public class OverviewLayer extends FreeformLayer {

	public OverviewLayer(ITimelineStyleProvider styleProvider) {
		setBorder(styleProvider.getOverviewAreaBorder());

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
		final Point pt = Point.SINGLETON;
		pt.setLocation(x, y);
		translateFromParent(pt);

		return getBounds().contains(pt);
	}

	public void createCursor(long eventTime) {
		add(new OverviewCursorFigure(), new PrecisionRectangle(eventTime, 0, 1, 1));
	}
}
