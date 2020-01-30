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

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.nebula.widgets.timeline.Helper;

public class TracksLayer extends FreeformLayer {

	public TracksLayer() {
		final ToolbarLayout layout = new ToolbarLayout(false);
		layout.setStretchMinorAxis(true);
		setLayoutManager(layout);
	}

	@Override
	public void paint(Graphics graphics) {
		super.paint(graphics);

		Helper.getTimeViewDetails(this).setScreenArea(getBounds());
	}
}
