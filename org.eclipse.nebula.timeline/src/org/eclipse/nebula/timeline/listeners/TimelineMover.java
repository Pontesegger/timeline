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
import org.eclipse.nebula.timeline.ICursor;
import org.eclipse.nebula.timeline.TimeViewDetails;
import org.eclipse.nebula.timeline.figures.RootFigure;

public class TimelineMover extends MouseMotionListener.Stub implements MouseListener, MouseMotionListener {

	private static final int CLICK_TIMEOUT = 300;

	private Point fLocation = null;
	private final Figure fFigure;
	private long fPressTimeStamp;

	public TimelineMover(Figure figure) {
		fFigure = figure;

		figure.addMouseListener(this);
		figure.addMouseMotionListener(this);
	}

	@Override
	public void mousePressed(MouseEvent me) {
		fLocation = me.getLocation();
		me.consume();

		fPressTimeStamp = System.currentTimeMillis();
	}

	@Override
	public void mouseReleased(MouseEvent me) {
		if ((System.currentTimeMillis() - fPressTimeStamp) < CLICK_TIMEOUT) {
			// create new cursor
			final RootFigure rootFigure = Helper.getFigure(fFigure, RootFigure.class);
			final long eventTime = Helper.getTimeViewDetails(fFigure).screenOffsetToEventTime(me.x);
			final ICursor cursor = rootFigure.createCursor(eventTime);

			rootFigure.addCursorFigure(cursor);
			rootFigure.addOverviewCursorFigure(cursor);
		}

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
			// invalidate timestamp for cursor creation
			fPressTimeStamp = 0;

			final Point targetLocation = me.getLocation();

			final Dimension offset = fLocation.getDifference(targetLocation);
			if (offset.width() != 0) {
				final TimeViewDetails timeDetails = Helper.getRootFigure(fFigure).getTimeViewDetails();
				if (timeDetails.translateDetailAreaOffset(offset.width()))
					fLocation = targetLocation;

				me.consume();
			}
		}
	}
}
