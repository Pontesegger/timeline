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

package org.eclipse.nebula.timeline.figures.overview;

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.nebula.timeline.Helper;
import org.eclipse.nebula.timeline.TimeViewDetails;

/**
 * @author christian
 *
 */
public class OverviewSelectionLayer extends FreeformLayer {

	private final OverviewSelectionFigure fOverviewSelectionFigure;

	public OverviewSelectionLayer() {
		fOverviewSelectionFigure = new OverviewSelectionFigure();
		add(fOverviewSelectionFigure);
	}

	@Override
	protected void paintChildren(Graphics graphics) {
		final TimeViewDetails timeViewDetails = Helper.getTimeViewDetails(this);

		final Rectangle visibleEventArea = timeViewDetails.getVisibleEventArea();
		final Rectangle bounds = timeViewDetails.scaleToOverview(visibleEventArea);

		bounds.setY(getBounds().y());
		bounds.setHeight(getBounds().height());
		bounds.performTranslate(getBounds().x(), 0);

		fOverviewSelectionFigure.setBounds(bounds);

		super.paintChildren(graphics);
	}
}
