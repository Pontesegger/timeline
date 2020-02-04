/*******************************************************************************
 * Copyright (c) 2020 ponteseg and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ponteseg - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.timeline.jface;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.nebula.widgets.timeline.borders.LeftRightBorder;
import org.eclipse.nebula.widgets.timeline.borders.RoundedRectangleBorder;
import org.eclipse.nebula.widgets.timeline.borders.TrackBorder;
import org.eclipse.nebula.widgets.timeline.figures.detail.track.lane.EventFigure;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;

public class DefaultTimelineStyleProvider implements ITimelineStyleProvider {

	private static final Color[] LANE_COLORS = new Color[] { ColorConstants.lightBlue, ColorConstants.yellow, ColorConstants.red, ColorConstants.lightGreen,
			ColorConstants.lightGray, ColorConstants.orange };

	private static int fNextLaneColor = 0;

	/** Default font, managed by Draw2D. */
	private Font fDefaultFont = null;

	/** Custom font for selected events. Managed by styleProvider. */
	private Font fSelectedFont = null;

	/** Resource manager shared with widget. */
	private final ResourceManager fResourceManager;

	public DefaultTimelineStyleProvider(ResourceManager resourceManager) {
		fResourceManager = resourceManager;
	}

	@Override
	public Color getBackgroundColor() {
		return ColorConstants.black;
	}

	@Override
	public Border getDetailAreaBorder() {
		final RoundedRectangleBorder border = new RoundedRectangleBorder(10);
		border.setWidth(2);
		border.setColor(getGridColor());

		return border;
	}

	@Override
	public Border getOverviewAreaBorder() {
		return getDetailAreaBorder();
	}

	@Override
	public Color getGridColor() {
		return ColorConstants.darkGray;
	}

	@Override
	public int getGridLineStyle() {
		return SWT.LINE_DOT;
	}

	@Override
	public Border getOverviewSelectionBorder() {
		return new LeftRightBorder(ColorConstants.red);
	}

	@Override
	public Color getOverviewSelectionBackgroundColor() {
		return ColorConstants.white;
	}

	@Override
	public int getOverviewSelectionBackgroundAlpha() {
		return 40;
	}

	@Override
	public Border getTrackBorder(String title) {
		return new TrackBorder(title);
	}

	@Override
	public Color getCursorColor() {
		return ColorConstants.yellow;
	}

	@Override
	public Color getSelectedCursorColor() {
		return ColorConstants.red;
	}

	@Override
	public boolean showGrid() {
		return true;
	}

	@Override
	public boolean showTimeAxis() {
		return true;
	}

	@Override
	public Color getLaneColor() {
		final Color color = LANE_COLORS[fNextLaneColor];
		fNextLaneColor = (fNextLaneColor + 1) % LANE_COLORS.length;

		return color;
	}

	@Override
	public int getLaneHeight() {
		return 40;
	}

	@Override
	public void selectEvent(EventFigure eventFigure) {
		eventFigure.setFont(getSelectedFont(eventFigure));
		((IFigure) eventFigure.getChildren().get(0)).revalidate();

		// change colors after changing the font. Otherwise revalidate will overwrite the colors
		eventFigure.setForegroundColor(ColorConstants.red);
		eventFigure.setLineWidth(3);
		eventFigure.setAlpha(255);
	}

	@Override
	public void unselectEvent(EventFigure eventFigure) {
		eventFigure.setEventColor(eventFigure.getEventColor());
		eventFigure.setLineWidth(2);

		if (getDefaultFont() != null) {
			eventFigure.setFont(getDefaultFont());
			((IFigure) eventFigure.getChildren().get(0)).revalidate();
		}
	}

	/**
	 * Get the default font used for events.
	 *
	 * @return default font
	 */
	private Font getDefaultFont() {
		return fDefaultFont;
	}

	/**
	 * Get the font for a selected figure.
	 *
	 * @param figure
	 *            figure to get font for
	 * @return bold font
	 */
	private Font getSelectedFont(IFigure figure) {
		if (fDefaultFont == null)
			fDefaultFont = figure.getFont();

		if (fSelectedFont == null) {
			FontDescriptor fontDescriptor = FontDescriptor.createFrom(fDefaultFont).setStyle(SWT.BOLD);
			fontDescriptor = fontDescriptor.setHeight((int) (fDefaultFont.getFontData()[0].getHeight() * 1.4));
			fSelectedFont = fResourceManager.createFont(fontDescriptor);
		}

		return fSelectedFont;
	}

	@Override
	public double getZoomFactor() {
		return 1.2d;
	}

	@Override
	public Color getColor(RGB rgb) {
		return fResourceManager.createColor(rgb);
	}

	@Override
	public boolean showOverview() {
		return true;
	}

	@Override
	public int getOverviewLaneHeight() {
		return 4;
	}

	@Override
	public Insets getOverviewInsets() {
		return new Insets(0, 5, 0, 5);
	}
}
