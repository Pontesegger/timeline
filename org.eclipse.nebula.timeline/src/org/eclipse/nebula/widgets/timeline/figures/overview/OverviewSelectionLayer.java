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

package org.eclipse.nebula.widgets.timeline.figures.overview;

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.nebula.widgets.timeline.Helper;
import org.eclipse.nebula.widgets.timeline.TimeBaseConverter;
import org.eclipse.nebula.widgets.timeline.Timing;
import org.eclipse.nebula.widgets.timeline.jface.ITimelineStyleProvider;

public class OverviewSelectionLayer extends FreeformLayer {

	private static final int MINIMUM_WIDTH = 5;

	public OverviewSelectionLayer(ITimelineStyleProvider styleProvider) {
		setLayoutManager(new OverviewSelectionLayerLayout());

		add(new OverviewSelectionFigure(styleProvider));
	}

	private class OverviewSelectionLayerLayout extends XYLayout {
		@Override
		public Object getConstraint(IFigure figure) {
			final TimeBaseConverter timeViewDetails = Helper.getTimeViewDetails(figure);

			final Timing visibleEventArea = timeViewDetails.getVisibleEventArea();
			final Timing scaledTiming = timeViewDetails.scaleToOverview(visibleEventArea);
			scaledTiming.translate(getBounds().x());

			final Rectangle bounds = new PrecisionRectangle(scaledTiming.left(), getBounds().y(), scaledTiming.getDuration(), getBounds().height());
			if (bounds.width() < MINIMUM_WIDTH)
				bounds.setWidth(MINIMUM_WIDTH);

			return bounds;
		}
	}
}
