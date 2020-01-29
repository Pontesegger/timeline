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

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.nebula.timeline.figures.IStyledFigure;
import org.eclipse.nebula.timeline.jface.ITimelineStyleProvider;
import org.eclipse.nebula.timeline.layouts.LaneLayout;

public class LaneFigure extends Figure implements IStyledFigure {

	private int fPreferredHeight;

	public LaneFigure(ITimelineStyleProvider styleProvider) {
		setLayoutManager(new LaneLayout());

		updateStyle(styleProvider);
	}

	@Override
	public void updateStyle(ITimelineStyleProvider styleProvider) {
		setForegroundColor(styleProvider.getLaneColor());
		fPreferredHeight = styleProvider.getLaneHeight();
	}

	@Override
	public Dimension getPreferredSize(int wHint, int hHint) {
		return new Dimension(wHint, fPreferredHeight);
	}

	@Override
	public void add(IFigure figure, Object constraint, int index) {
		super.add(figure, constraint, index);

		getChildren().sort((o1, o2) -> {
			return ((EventFigure) o1).compareTo((EventFigure) o2);
		});
	}

}
