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

package org.eclipse.nebula.timeline.layouts;

import java.util.Iterator;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.nebula.timeline.Helper;
import org.eclipse.nebula.timeline.TimeViewDetails;
import org.eclipse.nebula.timeline.figures.detail.cursor.CursorFigure;

public class CursorLayout extends XYLayout {

	@Override
	public void layout(IFigure parent) {
		final TimeViewDetails timeViewDetails = Helper.getRootFigure(parent).getTimeViewDetails();

		final Iterator<?> children = parent.getChildren().iterator();
		final Point offset = getOrigin(parent);
		IFigure f;
		while (children.hasNext()) {
			f = (IFigure) children.next();
			final Rectangle bounds = (Rectangle) getConstraint(f);

			// now bounds refers to the original bounds (unscaled and unmoved)
			final Rectangle adjustedBounds = bounds.getCopy();
			adjustedBounds.performTranslate(-timeViewDetails.getOffset().x(), 0);
			adjustedBounds.performScale(timeViewDetails.getScaleFactor());
			adjustedBounds.performTranslate(offset.x(), 0);

			adjustedBounds.setWidth(CursorFigure.CURSOR_WIDTH);
			adjustedBounds.setY(parent.getBounds().y());
			adjustedBounds.setHeight(parent.getBounds().height());

			adjustedBounds.performTranslate(-CursorFigure.CURSOR_WIDTH / 2, 0);

			f.setBounds(adjustedBounds);
		}
	}
}
