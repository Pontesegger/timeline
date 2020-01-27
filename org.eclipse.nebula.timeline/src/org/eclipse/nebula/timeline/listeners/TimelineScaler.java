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

import org.eclipse.nebula.timeline.figures.RootFigure;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;

public class TimelineScaler implements MouseWheelListener {

	private final RootFigure fRootFigure;

	public TimelineScaler(RootFigure rootFigure) {
		fRootFigure = rootFigure;
	}

	@Override
	public void mouseScrolled(MouseEvent e) {
		if (e.count > 0)
			fRootFigure.zoomIn(e.x);
		else
			fRootFigure.zoomOut(e.x);
	}
}
