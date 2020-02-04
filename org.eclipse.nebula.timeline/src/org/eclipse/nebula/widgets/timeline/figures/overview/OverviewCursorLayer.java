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

package org.eclipse.nebula.widgets.timeline.figures.overview;

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.nebula.widgets.timeline.Helper;
import org.eclipse.nebula.widgets.timeline.ICursor;
import org.eclipse.nebula.widgets.timeline.ITimed;
import org.eclipse.nebula.widgets.timeline.TimeBaseConverter;
import org.eclipse.nebula.widgets.timeline.Timing;

public class OverviewCursorLayer extends FreeformLayer {

	public OverviewCursorLayer() {
		setLayoutManager(new OverviewLayout());
	}

	@Override
	protected boolean useLocalCoordinates() {
		return true;
	}

	private class OverviewLayout extends XYLayout {

		@Override
		public Rectangle getConstraint(IFigure figure) {
			final TimeBaseConverter timeViewDetails = Helper.getTimeViewDetails(figure);

			// get border insets from OverviewLayer
			final OverviewEventLayer layer = Helper.getFigure(figure, OverviewEventLayer.class);
			final Insets insets = layer.getInsets();

			final ITimed cursor = (ICursor) super.getConstraint(figure);
			final Timing screenCoordinates = timeViewDetails.toOverviewCoordinates(cursor.getTiming());

			return new PrecisionRectangle(screenCoordinates.left(), insets.top, 1, getBounds().height() - insets.getHeight());
		}
	}
}
