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
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.nebula.widgets.timeline.Helper;
import org.eclipse.nebula.widgets.timeline.TimeViewDetails;
import org.eclipse.nebula.widgets.timeline.jface.ITimelineStyleProvider;

public class OverviewSelectionLayer extends FreeformLayer {

	public OverviewSelectionLayer(ITimelineStyleProvider styleProvider) {
		setLayoutManager(new OverviewSelectionLayerLayout());

		add(new OverviewSelectionFigure(styleProvider));
	}

	private class OverviewSelectionLayerLayout extends XYLayout {
		@Override
		public Object getConstraint(IFigure figure) {
			final TimeViewDetails timeViewDetails = Helper.getTimeViewDetails(figure);

			final Rectangle visibleEventArea = timeViewDetails.getVisibleEventArea();
			final Rectangle bounds = timeViewDetails.scaleToOverview(visibleEventArea);

			bounds.setY(getBounds().y());
			bounds.setHeight(getBounds().height());
			bounds.performTranslate(getBounds().x(), 0);
			if (bounds.width() < 3)
				bounds.setWidth(3);

			return bounds;
		}
	}
}
