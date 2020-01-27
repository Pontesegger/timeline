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
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.nebula.timeline.ITimelineEvent;
import org.eclipse.nebula.timeline.layouts.LaneLayout;
import org.eclipse.swt.graphics.Color;

public class LaneFigure extends Figure {

	private static final Color[] LANE_COLORS = new Color[] { ColorConstants.lightBlue, ColorConstants.yellow, ColorConstants.red, ColorConstants.lightGreen,
			ColorConstants.lightGray, ColorConstants.orange };

	private static final int DEFAULT_HEIGHT = 40;

	private static int fNextLaneColor = 0;

	private final LaneLayout fLaneLayout;

	private int fPreferredHeight = DEFAULT_HEIGHT;

	private final Rectangle fPreferredSize = new PrecisionRectangle();

	public LaneFigure() {
		fLaneLayout = new LaneLayout();
		setLayoutManager(fLaneLayout);

		setForegroundColor(LANE_COLORS[fNextLaneColor]);
		fNextLaneColor = (fNextLaneColor + 1) % LANE_COLORS.length;
	}

	@Override
	public Dimension getPreferredSize(int wHint, int hHint) {
		return new Dimension(Math.max(fPreferredSize.width(), wHint), fPreferredHeight);
	}

	public Rectangle getTotalTimeArea() {
		return fPreferredSize;
	}

	public void setHeight(int height) {
		fPreferredHeight = height;
	}
}
