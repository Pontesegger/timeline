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

package org.eclipse.nebula.widgets.timeline.figures.detail.track;

import org.eclipse.draw2d.LayeredPane;
import org.eclipse.nebula.widgets.timeline.figures.IStyledFigure;
import org.eclipse.nebula.widgets.timeline.figures.detail.cursor.CursorLayer;
import org.eclipse.nebula.widgets.timeline.figures.detail.cursor.CursorTimingsLayer;
import org.eclipse.nebula.widgets.timeline.jface.ITimelineStyleProvider;

public class TracksFigure extends LayeredPane implements IStyledFigure {

	public TracksFigure(ITimelineStyleProvider styleProvider) {
		updateStyle(styleProvider);

		add(new GridLayer(styleProvider));
		add(new TracksLayer());
		add(new CursorLayer());
		add(new CursorTimingsLayer());
	}

	@Override
	public void updateStyle(ITimelineStyleProvider styleProvider) {
		setBorder(styleProvider.getDetailAreaBorder());
	}
}
