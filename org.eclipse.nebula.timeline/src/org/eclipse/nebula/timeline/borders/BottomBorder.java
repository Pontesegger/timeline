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

package org.eclipse.nebula.timeline.borders;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

/**
 * @author christian
 *
 */
public class BottomBorder extends LineBorder {

	public BottomBorder() {
		super();
	}

	/**
	 * @param color
	 * @param width
	 * @param style
	 */
	public BottomBorder(Color color, int width, int style) {
		super(color, width, style);
	}

	/**
	 * @param color
	 * @param width
	 */
	public BottomBorder(Color color, int width) {
		super(color, width);
	}

	/**
	 * @param color
	 */
	public BottomBorder(Color color) {
		super(color);
	}

	/**
	 * @param width
	 */
	public BottomBorder(int width) {
		super(width);
	}

	/**
	 * @see org.eclipse.draw2d.Border#paint(IFigure, Graphics, Insets)
	 */
	@Override
	public void paint(IFigure figure, Graphics graphics, Insets insets) {
		tempRect.setBounds(getPaintRectangle(figure, insets));
		if ((getWidth() % 2) == 1) {
			tempRect.width--;
			tempRect.height--;
		}
		tempRect.shrink(getWidth() / 2, getWidth() / 2);
		graphics.setLineWidth(getWidth());
		graphics.setLineStyle(getStyle());
		if (getColor() != null)
			graphics.setForegroundColor(getColor());

		graphics.drawLine(tempRect.getBottomLeft(), tempRect.getBottomRight());
	}
}
