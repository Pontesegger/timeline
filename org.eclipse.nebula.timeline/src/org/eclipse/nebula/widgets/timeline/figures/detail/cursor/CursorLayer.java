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

package org.eclipse.nebula.widgets.timeline.figures.detail.cursor;

import java.util.Iterator;

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.nebula.timeline.ICursor;
import org.eclipse.nebula.widgets.timeline.Helper;
import org.eclipse.nebula.widgets.timeline.TimeViewDetails;

public class CursorLayer extends FreeformLayer {

	public CursorLayer() {
		setLayoutManager(new CursorLayout());
	}

	private class CursorLayout extends XYLayout {

		@Override
		public void layout(IFigure parent) {
			final TimeViewDetails timeViewDetails = Helper.getTimeViewDetails(parent);

			final Iterator<?> children = parent.getChildren().iterator();
			final Point offset = getOrigin(parent);
			IFigure f;
			while (children.hasNext()) {
				f = (IFigure) children.next();
				final ICursor cursor = (ICursor) getConstraint(f);

				final Rectangle bounds = new PrecisionRectangle(cursor.getTimestamp(), 0, 1, 1);
				bounds.performTranslate(-timeViewDetails.getOffset().x(), 0);
				bounds.performScale(timeViewDetails.getScaleFactor());
				bounds.performTranslate(offset.x(), 0);

				bounds.setWidth(CursorFigure.CURSOR_WIDTH);
				bounds.setY(parent.getBounds().y());
				bounds.setHeight(parent.getBounds().height());

				bounds.performTranslate(-CursorFigure.CURSOR_WIDTH / 2, 0);

				f.setBounds(bounds);
			}
		}
	}
}
