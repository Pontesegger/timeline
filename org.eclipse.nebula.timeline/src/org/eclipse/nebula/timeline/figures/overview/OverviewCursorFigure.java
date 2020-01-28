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

package org.eclipse.nebula.timeline.figures.overview;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.nebula.timeline.figures.IStyledFigure;
import org.eclipse.nebula.timeline.jface.ITimelineStyleProvider;
import org.eclipse.swt.SWT;

public class OverviewCursorFigure extends Shape implements IStyledFigure {

	public OverviewCursorFigure(ITimelineStyleProvider styleProvider) {
		updateStyle(styleProvider);
	}

	@Override
	protected void outlineShape(Graphics graphics) {
		final float lineInset = Math.max(1.0f, getLineWidthFloat()) / 2.0f;
		final int inset1 = (int) Math.floor(lineInset);
		final int inset2 = (int) Math.ceil(lineInset);

		final Rectangle r = Rectangle.SINGLETON.setBounds(getBounds());
		r.x += inset1;
		r.y += inset1;
		r.width -= inset1 + inset2;
		r.height -= inset1 + inset2;

		graphics.setLineStyle(SWT.LINE_DOT);
		graphics.drawLine(r.getTopLeft(), r.getBottomRight());
	}

	@Override
	protected void fillShape(Graphics graphics) {
		// nothing to do
	}

	@Override
	public void updateStyle(ITimelineStyleProvider styleProvider) {
		setForegroundColor(styleProvider.getCursorColor());
	}
}
