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

package org.eclipse.nebula.timeline.listeners;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.nebula.timeline.Helper;
import org.eclipse.nebula.timeline.TimeViewDetails;

public class OverviewSelectionMover extends MouseMotionListener.Stub implements MouseListener, MouseMotionListener {

	private Point fLocation = null;
	private final Figure fFigure;

	public OverviewSelectionMover(Figure figure) {
		fFigure = figure;

		figure.addMouseListener(this);
		figure.addMouseMotionListener(this);
	}

	@Override
	public void mousePressed(MouseEvent me) {
		fLocation = me.getLocation();
		me.consume();
	}

	@Override
	public void mouseReleased(MouseEvent me) {
		if (fLocation != null) {
			fLocation = null;
			me.consume();
		}
	}

	@Override
	public void mouseDoubleClicked(MouseEvent me) {
		Helper.getRootFigure(fFigure).getTimeViewDetails().resetDisplaySettings();
		me.consume();
	}

	@Override
	public void mouseDragged(MouseEvent me) {
		if (fLocation != null) {
			final Point targetLocation = me.getLocation();

			final Dimension offset = targetLocation.getDifference(fLocation);
			if (offset.width() != 0) {
				final TimeViewDetails timeDetails = Helper.getRootFigure(fFigure).getTimeViewDetails();
				if (timeDetails.translateOverviewAreaOffset(offset.width()))
					fLocation = targetLocation;

				me.consume();
			}
		}
	}
}
