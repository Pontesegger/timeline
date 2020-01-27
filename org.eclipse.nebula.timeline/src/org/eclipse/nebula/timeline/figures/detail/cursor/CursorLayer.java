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

package org.eclipse.nebula.timeline.figures.detail.cursor;

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.nebula.timeline.layouts.CursorLayout;

public class CursorLayer extends FreeformLayer {

	public CursorLayer() {
		setLayoutManager(new CursorLayout());
	}

	public void createCursor(long eventTime) {
		add(new CursorFigure(), new PrecisionRectangle(eventTime, 0, 1, 1));
	}
}
