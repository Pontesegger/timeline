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

package org.eclipse.nebula.timeline.figures.detail.track;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.LayeredPane;
import org.eclipse.nebula.timeline.borders.RoundedRectangleBorder;
import org.eclipse.nebula.timeline.figures.detail.cursor.CursorLayer;
import org.eclipse.nebula.timeline.figures.detail.cursor.CursorTimingsLayer;

public class TracksFigure extends LayeredPane {

	private final TracksLayer fTracksLayer;
	private final GridLayer fGridLayer;
	private final CursorLayer fCursorLayer;
	private final CursorTimingsLayer fCursorTimingsLayer;

	public TracksFigure() {

		final RoundedRectangleBorder border = new RoundedRectangleBorder(10);
		border.setWidth(2);
		border.setColor(ColorConstants.darkGray);
		setBorder(border);

		fGridLayer = new GridLayer();
		add(fGridLayer);

		fTracksLayer = new TracksLayer();
		add(fTracksLayer);

		fCursorLayer = new CursorLayer();
		add(fCursorLayer);

		fCursorTimingsLayer = new CursorTimingsLayer();
		add(fCursorTimingsLayer);
	}
}
