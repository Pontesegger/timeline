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

package org.eclipse.nebula.timeline.figures.overview;

import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.nebula.timeline.ITimelineEvent;
import org.eclipse.nebula.timeline.figures.detail.track.lane.EventFigure;

public class OverviewEventFigure extends RectangleFigure {

	private final EventFigure fBaseFigure;

	public OverviewEventFigure(EventFigure baseFigure) {
		fBaseFigure = baseFigure;

		setForegroundColor(fBaseFigure.getBackgroundColor());
		setBackgroundColor(fBaseFigure.getBackgroundColor());

		setLineWidth(2);
	}

	public ITimelineEvent getEvent() {
		return fBaseFigure.getEvent();
	}
}
