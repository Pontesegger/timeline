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

package org.eclipse.nebula.timeline.jface;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.nebula.timeline.borders.LeftRightBorder;
import org.eclipse.nebula.timeline.borders.RoundedRectangleBorder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

public class DefaultTimelineStyleProvider implements ITimelineStyleProvider {

	@Override
	public Color getBackgroundColor() {
		return ColorConstants.black;
	}

	@Override
	public Border getDetailAreaBorder() {
		final RoundedRectangleBorder border = new RoundedRectangleBorder(10);
		border.setWidth(2);
		border.setColor(ColorConstants.darkGray);

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
}
