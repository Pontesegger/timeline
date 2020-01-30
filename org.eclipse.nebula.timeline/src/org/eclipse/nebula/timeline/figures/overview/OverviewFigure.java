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

import org.eclipse.draw2d.LayeredPane;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.nebula.timeline.Helper;
import org.eclipse.nebula.timeline.borders.EmptyBorder;
import org.eclipse.nebula.timeline.figures.IStyledFigure;
import org.eclipse.nebula.timeline.jface.ITimelineStyleProvider;

public class OverviewFigure extends LayeredPane implements IStyledFigure {

	private static final int DEFAULT_HEIGHT = 80;

	public static final int HORIZONTAL_INDENT = 8;
	public static final int VERTICAL_INDENT = 8;
	public static final int EVENT_HEIGHT = 4;
	public static final int Y_PADDING = 3;

	private final int fPreferredHeight = DEFAULT_HEIGHT;

	public OverviewFigure(ITimelineStyleProvider styleProvider) {

		setBorder(new EmptyBorder(new Insets(0, 10, 10, 10)));

		add(new OverviewLayer(styleProvider));
		add(new OverviewCursorLayer());
		add(new OverviewSelectionLayer(styleProvider));

		updateStyle(styleProvider);
	}

	@Override
	public void updateStyle(ITimelineStyleProvider styleProvider) {
		setVisible(styleProvider.showOverview());
	}

	@Override
	protected boolean useLocalCoordinates() {
		return true;
	}

	@Override
	public Dimension getPreferredSize(int wHint, int hHint) {
		final int laneCount = Helper.getLaneCount(this);
		final int requiredHeight = (VERTICAL_INDENT * 2) + (laneCount * EVENT_HEIGHT) + ((laneCount - 1) * Y_PADDING) + getInsets().top + getInsets().bottom;

		return new Dimension(wHint, Math.max(requiredHeight, getPreferredHeight()));
	}

	private int getPreferredHeight() {
		return fPreferredHeight;
	}
}
