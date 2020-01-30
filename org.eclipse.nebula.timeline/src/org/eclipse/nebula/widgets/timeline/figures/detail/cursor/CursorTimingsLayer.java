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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.nebula.widgets.timeline.Helper;
import org.eclipse.swt.SWT;

public class CursorTimingsLayer extends FreeformLayer {

	/** Location where to draw connections to. */
	private Point fConnectionsLocation;

	public CursorTimingsLayer() {
		setLayoutManager(new ToolbarLayout(false));

		setVisible(false);
	}

	@Override
	public void validate() {
		// cursor might have been deleted already
		for (final Object connection : getChildren())
			((CursorConnection) connection).updateDistance();

		super.validate();
	}

	public void showTimingsFor(CursorFigure cursorFigure, MouseEvent me) {

		removeAll();

		// get all cursors except the highlighted one
		final List<CursorFigure> cursors = getAllCursors();
		cursors.remove(cursorFigure);

		// sort cursors by distance to highlighted one
		Collections.sort(cursors,
				(o1, o2) -> (int) (Math.abs(cursorFigure.getEventTime() - o1.getEventTime()) - Math.abs(cursorFigure.getEventTime() - o2.getEventTime())));

		final Dimension distance = new Dimension(20, me.y);
		for (final CursorFigure cursor : cursors) {
			add(new CursorConnection(cursorFigure, cursor, distance));

			distance.expand(20, 30);
		}

		fConnectionsLocation = me.getLocation();
		setVisible(true);
	}

	public void hideTimings() {
		removeAll();
		setVisible(false);
	}

	private List<CursorFigure> getAllCursors() {
		final List<CursorFigure> cursors = new ArrayList<>();

		final CursorLayer cursorLayer = Helper.getFigure(this, CursorLayer.class);
		for (final Object cursor : cursorLayer.getChildren()) {
			if (cursor instanceof CursorFigure)
				cursors.add((CursorFigure) cursor);
		}

		return cursors;
	}

	public void moveTimingsTo(Point targetLocation) {
		final Dimension offset = targetLocation.getDifference(fConnectionsLocation);
		if (offset.height() != 0) {
			fConnectionsLocation = targetLocation;

			for (final Object connection : getChildren()) {
				if (connection instanceof PolylineConnection) {
					final CursorAnchor sourceAnchor = (CursorAnchor) ((PolylineConnection) connection).getSourceAnchor();
					sourceAnchor.translatePixelsFromTop(offset.height());

					final CursorAnchor targetAnchor = (CursorAnchor) ((PolylineConnection) connection).getTargetAnchor();
					targetAnchor.translatePixelsFromTop(offset.height());
				}
			}

			revalidate();
		}
	}

	private class CursorConnection extends PolylineConnection {
		private final CursorFigure fSorce;
		private final CursorFigure fTarget;
		private final CursorLabel fCursorLabel;

		public CursorConnection(CursorFigure sorce, CursorFigure target, Dimension distance) {
			fSorce = sorce;
			fTarget = target;
			setForegroundColor(ColorConstants.yellow);
			setLineStyle(SWT.LINE_DOT);

			setSourceAnchor(new CursorAnchor(sorce, distance.height()));
			setTargetAnchor(new CursorAnchor(target, distance.height()));
			setTargetDecoration(new PolygonDecoration());
			fCursorLabel = new CursorLabel(getDistanceAsText(sorce, target));
			add(fCursorLabel, new CursorConnectionLocator(this, distance.width()));
		}

		public void updateDistance() {
			fCursorLabel.setText(getDistanceAsText(fSorce, fTarget));
		}

		private String getDistanceAsText(CursorFigure cursorA, CursorFigure cursorB) {
			final double eventTime = Math.abs(cursorA.getEventTime() - cursorB.getEventTime());

			return eventTime + " ns";
		}
	}

	private class CursorAnchor extends ChopboxAnchor {

		private int fPixelsFromTop;

		public CursorAnchor(CursorFigure cursor, int pixelsFromTop) {
			super(cursor);

			setPixelsFromTop(pixelsFromTop);
		}

		public void setPixelsFromTop(int pixelsFromTop) {
			fPixelsFromTop = pixelsFromTop;
		}

		public void translatePixelsFromTop(int offset) {
			fPixelsFromTop += offset;
		}

		@Override
		protected Rectangle getBox() {
			final Rectangle box = super.getBox().getCopy();

			box.performTranslate(box.width() / 2, 0);
			box.setWidth(1);

			box.setY(box.y() + fPixelsFromTop);
			box.setHeight(1);

			return box;
		}
	}

	private class CursorLabel extends RoundedRectangle {
		private static final int VERTICAL_INDENT = 2;
		private static final int HORIZONTAL_INDENT = 5;

		public CursorLabel(String text) {
			setLayoutManager(new StackLayout());

			setForegroundColor(ColorConstants.yellow);
			setBackgroundColor(ColorConstants.yellow);
			setOpaque(true);

			setCornerDimensions(new Dimension(5, 5));

			setBorder(new MarginBorder(VERTICAL_INDENT, HORIZONTAL_INDENT, VERTICAL_INDENT, HORIZONTAL_INDENT));
			final Label label = new Label(text);
			label.setForegroundColor(ColorConstants.black);
			add(label);
		}

		public void setText(String text) {
			final Label label = (Label) getChildren().get(0);
			label.setText(text);
		}

		@Override
		public Dimension getPreferredSize(int w, int h) {
			Dimension dimension = super.getPreferredSize(w, h);
			if (dimension.width > 150)
				dimension = super.getPreferredSize(150, -1);

			return dimension;
		}
	}

	private class CursorConnectionLocator extends ConnectionLocator {
		private final int fDistance;

		public CursorConnectionLocator(PolylineConnection connection, int distance) {
			super(connection, ConnectionLocator.SOURCE);
			fDistance = distance;
		}

		@Override
		protected Point getLocation(PointList points) {
			final IFigure cursorLabel = (IFigure) getConnection().getChildren().get(1);
			final Dimension labelSize = cursorLabel.getPreferredSize();

			final Point firstPoint = points.getFirstPoint();
			final Point lastPoint = points.getLastPoint();
			final int direction = (lastPoint.x() > firstPoint.x()) ? 1 : -1;

			final Point point = super.getLocation(points);
			point.translate(((labelSize.width() / 2) + fDistance) * direction, 0);

			return point;
		}
	}
}
