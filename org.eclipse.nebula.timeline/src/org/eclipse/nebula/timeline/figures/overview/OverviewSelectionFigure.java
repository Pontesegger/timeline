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

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.nebula.timeline.borders.LeftRightBorder;
import org.eclipse.nebula.timeline.listeners.OverviewSelectionMover;

public class OverviewSelectionFigure extends RectangleFigure {

	public OverviewSelectionFigure() {
		setForegroundColor(ColorConstants.red);
		setBackgroundColor(ColorConstants.white);

		setAlpha(40);

		setLineWidth(1);
		setBorder(new LeftRightBorder(getForegroundColor()));

		new OverviewSelectionMover(this);
	}
}
