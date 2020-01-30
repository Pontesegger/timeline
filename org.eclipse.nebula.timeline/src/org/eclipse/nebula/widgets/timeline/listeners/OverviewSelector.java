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

package org.eclipse.nebula.widgets.timeline.listeners;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.nebula.widgets.timeline.Helper;
import org.eclipse.nebula.widgets.timeline.TimeViewDetails;

public class OverviewSelector extends MouseListener.Stub {

	private final Figure fFigure;

	public OverviewSelector(Figure figure) {
		fFigure = figure;

		figure.addMouseListener(this);
	}

	@Override
	public void mouseReleased(MouseEvent me) {
		if (me.button == 1) {
			final TimeViewDetails timeViewDetails = Helper.getTimeViewDetails(fFigure);
			final long eventTime = timeViewDetails.overviewScreenOffsetToEventTime(me.x);

			timeViewDetails.revealEvent(new PrecisionRectangle(eventTime, 0, 1, 1));
		}
	}
}
