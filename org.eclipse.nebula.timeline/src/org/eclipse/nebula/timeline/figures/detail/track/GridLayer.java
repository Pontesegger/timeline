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

package org.eclipse.nebula.timeline.figures.detail.track;

import java.util.Map;

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.nebula.timeline.Helper;
import org.eclipse.nebula.timeline.figures.IStyledFigure;
import org.eclipse.nebula.timeline.figures.RootFigure;
import org.eclipse.nebula.timeline.figures.detail.DetailFigure;
import org.eclipse.nebula.timeline.jface.ITimelineStyleProvider;

public class GridLayer extends FreeformLayer implements IStyledFigure {

	public GridLayer(ITimelineStyleProvider styleProvider) {
		updateStyle(styleProvider);
	}

	private DetailFigure getDetailFigure() {
		return (DetailFigure) getParent().getParent();
	}

	@Override
	protected void paintClientArea(Graphics graphics) {
		paintGrid(graphics);

		super.paintClientArea(graphics);
	}

	private void paintGrid(Graphics graphics) {
		final ITimelineStyleProvider styleProvider = Helper.getFigure(this, RootFigure.class).getStyleProvider();
		graphics.setLineStyle(styleProvider.getGridLineStyle());

		final Rectangle bounds = getBounds();
		final Map<Long, Integer> markerPositions = getDetailFigure().getMarkerPositions();
		for (final int position : markerPositions.values())
			graphics.drawLine(position, bounds.y, position, bounds.y + bounds.height);
	}

	@Override
	public void updateStyle(ITimelineStyleProvider styleProvider) {
		setForegroundColor(styleProvider.getGridColor());
		setVisible(styleProvider.showGrid());
	}
}
