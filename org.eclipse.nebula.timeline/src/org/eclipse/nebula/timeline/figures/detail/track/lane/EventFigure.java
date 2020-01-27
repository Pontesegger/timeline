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

package org.eclipse.nebula.timeline.figures.detail.track.lane;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.OrderedLayout;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.nebula.timeline.ITimelineEvent;
import org.eclipse.nebula.timeline.layouts.CenterLayout;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class EventFigure extends RoundedRectangle implements Comparable<EventFigure> {

	private final ITimelineEvent fEvent;

	public EventFigure(ITimelineEvent event) {
		fEvent = event;

		final ToolbarLayout layout = new ToolbarLayout(false);
		layout.setMinorAlignment(OrderedLayout.ALIGN_CENTER);
		layout.setStretchMinorAxis(true);
		setLayoutManager(layout);
		setLayoutManager(new CenterLayout());

		setLineWidth(2);

		final Label label = new Label(event.getTitle());
		label.setForegroundColor(ColorConstants.black);
		add(label);

		if (fEvent.getMessage() != null)
			setToolTip(new EventTooltip(fEvent.getMessage()));
	}

	public void setEventColor(Color color) {
		setBackgroundColor(color);

		final float[] hsb = color.getRGB().getHSB();
		setForegroundColor(new Color(Display.getDefault(), new RGB(hsb[0], Math.min(1, hsb[1] * 2f), hsb[2] * 0.8f)));
		setAlpha(150);
	}

	public ITimelineEvent getEvent() {
		return fEvent;
	}

	@Override
	public int compareTo(EventFigure eventFigure) {
		final long difference = getEvent().getStartTimestamp() - eventFigure.getEvent().getStartTimestamp();
		if (difference < 0)
			return -1;
		else if (difference > 0)
			return 1;
		else
			return 0;
	}
}
