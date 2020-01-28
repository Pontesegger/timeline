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

import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.nebula.timeline.Helper;
import org.eclipse.nebula.timeline.ICursor;
import org.eclipse.nebula.timeline.TimeViewDetails;
import org.eclipse.nebula.timeline.figures.RootFigure;
import org.eclipse.nebula.timeline.figures.detail.cursor.CursorFigure;
import org.eclipse.nebula.timeline.figures.detail.cursor.CursorTimingsLayer;
import org.eclipse.nebula.timeline.figures.detail.track.lane.EventFigure;
import org.eclipse.nebula.timeline.figures.detail.track.lane.LaneFigure;
import org.eclipse.nebula.timeline.figures.overview.OverviewCursorLayer;

public class CursorListener extends MouseMotionListener.Stub implements MouseListener, MouseMotionListener {

	private static final int SNAP_TO_FIGURE_OFFSET = 10;

	private final CursorFigure fFigure;

	private Point fLocation = null;

	public CursorListener(CursorFigure figure) {
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
		if (me.button == 3) {
			hideCursorTimings();
			final ICursor cursor = (ICursor) fFigure.getParent().getLayoutManager().getConstraint(fFigure);
			Helper.getFigure(fFigure, RootFigure.class).removeCursor(cursor);
		}

		if (fLocation != null) {
			fLocation = null;
			me.consume();
		}
	}

	@Override
	public void mouseDragged(MouseEvent me) {
		if (fLocation != null) {
			final Point targetLocation = me.getLocation();

			Long targetEventTime = snapToEvent(targetLocation);

			if (targetEventTime == null) {
				final Dimension offset = targetLocation.getDifference(fLocation);
				if (offset.width() != 0) {
					final TimeViewDetails timeDetails = Helper.getRootFigure(fFigure).getTimeViewDetails();
					targetEventTime = timeDetails.screenOffsetToEventTime(targetLocation.x());
				}

				fLocation = targetLocation;
			}

			if (targetEventTime != null) {
				final ICursor cursor = (ICursor) fFigure.getParent().getLayoutManager().getConstraint(fFigure);
				cursor.setTimestamp(targetEventTime);

				fFigure.getParent().revalidate();

				Helper.getFigure(fFigure, OverviewCursorLayer.class).revalidate();
			}
		}

		me.consume();
	}

	/**
	 * Try to snap to an event for the current lane the cursor is in.
	 *
	 * @param mouseCursorLocation
	 *
	 * @return timestamp in eventTime to set cursor to or <code>null</code>
	 */
	private Long snapToEvent(Point mouseCursorLocation) {
		final List<LaneFigure> lanes = Helper.getLanes(fFigure);
		LaneFigure laneUnderCursor = null;
		for (final LaneFigure lane : lanes) {
			if (lane.getBounds().contains(mouseCursorLocation)) {
				laneUnderCursor = lane;
				break;
			}
		}

		if (laneUnderCursor != null) {
			if (laneUnderCursor != null) {
				EventFigure figureToSnapTo = null;
				for (int offset = 0; offset <= SNAP_TO_FIGURE_OFFSET; offset += 2) {
					IFigure figure = laneUnderCursor.findFigureAt(mouseCursorLocation.x() - offset, mouseCursorLocation.y());
					if (figure instanceof EventFigure) {
						figureToSnapTo = (EventFigure) figure;
						break;
					}

					figure = laneUnderCursor.findFigureAt(mouseCursorLocation.x() + offset, mouseCursorLocation.y());
					if (figure instanceof EventFigure) {
						figureToSnapTo = (EventFigure) figure;
						break;
					}
				}

				if (figureToSnapTo != null) {
					final Rectangle figureBounds = figureToSnapTo.getBounds();
					final int diffToStart = Math.abs(figureBounds.x() - mouseCursorLocation.x());
					final int diffToEnd = Math.abs((figureBounds.x() + figureBounds.width()) - mouseCursorLocation.x());

					if (diffToStart <= diffToEnd) {
						// snap to start of figure
						return figureToSnapTo.getEvent().getStartTimestamp();
					} else {
						// snap to end of figure
						return figureToSnapTo.getEvent().getEndTimestamp();
					}
				}
			}
		}

		return null;
	}

	@Override
	public void mouseEntered(MouseEvent me) {
		fFigure.setForegroundColor(ColorConstants.red);
		fFigure.setBackgroundColor(ColorConstants.red);

		final CursorTimingsLayer cursorTimingsLayer = Helper.getFigure(fFigure, CursorTimingsLayer.class);
		cursorTimingsLayer.showTimingsFor(fFigure, me);
	}

	@Override
	public void mouseExited(MouseEvent me) {
		fFigure.setForegroundColor(ColorConstants.yellow);
		fFigure.setBackgroundColor(ColorConstants.yellow);

		hideCursorTimings();
	}

	@Override
	public void mouseMoved(MouseEvent me) {
		final CursorTimingsLayer cursorTimingsLayer = Helper.getFigure(fFigure, CursorTimingsLayer.class);
		cursorTimingsLayer.moveTimingsTo(me.getLocation());
	}

	@Override
	public void mouseDoubleClicked(MouseEvent me) {
		// nothing to do
	}

	private void hideCursorTimings() {
		final CursorTimingsLayer cursorTimingsLayer = Helper.getFigure(fFigure, CursorTimingsLayer.class);
		cursorTimingsLayer.setVisible(false);
	}
}
