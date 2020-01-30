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

package org.eclipse.nebula.widgets.timeline.figures.detail;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.nebula.widgets.timeline.figures.IStyledFigure;
import org.eclipse.nebula.widgets.timeline.jface.ITimelineStyleProvider;
import org.eclipse.swt.SWT;

public class TimeAxisFigure extends Figure implements IStyledFigure {

	public TimeAxisFigure(ITimelineStyleProvider styleProvider) {
		setOpaque(false);
		updateStyle(styleProvider);
	}

	@Override
	public void updateStyle(ITimelineStyleProvider styleProvider) {
		setVisible(styleProvider.showTimeAxis());
	}

	@Override
	public Dimension getPreferredSize(int wHint, int hHint) {
		return new Dimension(wHint, 20);
	}

	@Override
	protected void paintClientArea(Graphics graphics) {
		paintMarkers(graphics);

		super.paintClientArea(graphics);
	}

	private DetailFigure getDetailFigure() {
		return (DetailFigure) getParent();
	}

	private void paintMarkers(Graphics graphics) {

		final Rectangle bounds = getBounds();
		graphics.setForegroundColor(ColorConstants.darkGray);
		graphics.setLineStyle(SWT.LINE_SOLID);

		final Map<Long, Integer> markerPositions = getDetailFigure().getMarkerPositions();
		for (final Entry<Long, Integer> entry : markerPositions.entrySet()) {
			final String label = getLabelForTime(entry.getKey(), TimeUnit.NANOSECONDS);
			final int textWidth = FigureUtilities.getTextWidth(label, graphics.getFont());

			graphics.drawLine(entry.getValue(), bounds.y, entry.getValue(), bounds.y + 5);
			graphics.drawText(label, entry.getValue() - (textWidth / 2), bounds.y + 5);
		}
	}

	private String getLabelForTime(double timestamp, TimeUnit unit) {
		switch (unit) {
		case NANOSECONDS:
			if (timestamp >= 1000)
				return getLabelForTime(timestamp / 1000, TimeUnit.MICROSECONDS);

			return Double.toString(Math.round(timestamp * 100) / 100D) + " ns";

		case MICROSECONDS:
			if (timestamp >= 1000)
				return getLabelForTime(timestamp / 1000, TimeUnit.MILLISECONDS);

			return Double.toString(Math.round(timestamp * 100) / 100D) + " µs";

		case MILLISECONDS:
			if (timestamp >= 1000)
				return getLabelForTime(timestamp / 1000, TimeUnit.SECONDS);

			return Double.toString(Math.round(timestamp * 100) / 100D) + " ms";

		case SECONDS:
			if (timestamp >= 60)
				return getLabelForTime(timestamp / 60, TimeUnit.MINUTES);

			return Double.toString(Math.round(timestamp * 100) / 100D) + " s";

		case MINUTES:
			if (timestamp >= 60)
				return getLabelForTime(timestamp / 60, TimeUnit.HOURS);

			return Double.toString(Math.round(timestamp * 100) / 100D) + " min";

		case HOURS:
			if (timestamp >= 24)
				return getLabelForTime(timestamp / 24, TimeUnit.DAYS);

			return Double.toString(Math.round(timestamp * 100) / 100D) + " h";

		case DAYS:
			return Double.toString(Math.round(timestamp * 100) / 100D) + " days";

		default:
			return Double.toString(Math.round(timestamp * 100) / 100D);
		}
	}
}
