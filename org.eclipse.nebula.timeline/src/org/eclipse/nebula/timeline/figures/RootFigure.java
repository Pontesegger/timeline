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

package org.eclipse.nebula.timeline.figures;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.nebula.timeline.Helper;
import org.eclipse.nebula.timeline.TimeViewDetails;
import org.eclipse.nebula.timeline.figures.detail.DetailFigure;
import org.eclipse.nebula.timeline.figures.detail.cursor.CursorLayer;
import org.eclipse.nebula.timeline.figures.detail.track.TracksLayer;
import org.eclipse.nebula.timeline.figures.detail.track.lane.LaneFigure;
import org.eclipse.nebula.timeline.figures.overview.OverviewFigure;
import org.eclipse.nebula.timeline.figures.overview.OverviewLayer;

public class RootFigure extends Figure {

	private static final int LAYOUT_SPACING = 10;

	private static final double ZOOM_FACTOR = 1.2d;

	private final DetailFigure fDetailFigure;
	private final TimeViewDetails fTimeViewDetails;
	private final OverviewFigure fOverviewFigure;

	public RootFigure() {
		fTimeViewDetails = new TimeViewDetails(this);

		final BorderLayout layout = new BorderLayout();
		layout.setVerticalSpacing(0);
		setLayoutManager(layout);

		setOpaque(true);
		setBackgroundColor(ColorConstants.black);

		fDetailFigure = new DetailFigure();
		add(fDetailFigure, BorderLayout.CENTER);

		fOverviewFigure = new OverviewFigure();
		add(fOverviewFigure, BorderLayout.BOTTOM);
	}

	/**
	 * Remove all tracks and cursors. Leaves the view empty.
	 */
	public void clear() {
		Helper.getFigure(this, TracksLayer.class).removeAll();
		Helper.getFigure(this, OverviewLayer.class).removeAll();
	}

	public OverviewFigure getOverviewFigure() {
		return fOverviewFigure;
	}

	public TimeViewDetails getTimeViewDetails() {
		return fTimeViewDetails;
	}

	public void fireTimebaseChanged() {

		for (final LaneFigure lane : Helper.getLanes(this))
			lane.revalidate();

		Helper.getFigure(this, OverviewLayer.class).revalidate();
		Helper.getFigure(this, CursorLayer.class).revalidate();

		// FIXME sets the whole canvas dirty. The dirty region is actually smaller and we might be more performant if we narrow down the area to redraw
		getUpdateManager().addDirtyRegion(this, getBounds());
	}

	public void zoomIn(int zoomCenterX) {
		zoom(ZOOM_FACTOR, zoomCenterX);
	}

	public void zoomOut(int zoomCenterX) {
		zoom(1 / ZOOM_FACTOR, zoomCenterX);
	}

	public void zoom(double factor, int zoomCenterX) {
		getTimeViewDetails().zoom(factor, zoomCenterX);
	}
}
