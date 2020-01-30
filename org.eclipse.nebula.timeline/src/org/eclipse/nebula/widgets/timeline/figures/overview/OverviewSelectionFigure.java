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

package org.eclipse.nebula.widgets.timeline.figures.overview;

import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.nebula.widgets.timeline.figures.IStyledFigure;
import org.eclipse.nebula.widgets.timeline.jface.ITimelineStyleProvider;
import org.eclipse.nebula.widgets.timeline.listeners.OverviewSelectionMover;

public class OverviewSelectionFigure extends RectangleFigure implements IStyledFigure {

	public OverviewSelectionFigure(ITimelineStyleProvider styleProvider) {
		updateStyle(styleProvider);

		new OverviewSelectionMover(this);
	}

	@Override
	public void updateStyle(ITimelineStyleProvider styleProvider) {
		setBackgroundColor(styleProvider.getOverviewSelectionBackgroundColor());
		setAlpha(styleProvider.getOverviewSelectionBackgroundAlpha());
		setBorder(styleProvider.getOverviewSelectionBorder());
	}
}
