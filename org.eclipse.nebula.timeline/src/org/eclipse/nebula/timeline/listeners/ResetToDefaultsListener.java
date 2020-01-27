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
import org.eclipse.nebula.timeline.Helper;

public class ResetToDefaultsListener extends MouseListener.Stub {

	private final Figure fFigure;

	public ResetToDefaultsListener(Figure figure) {
		fFigure = figure;

		figure.addMouseListener(this);
	}

	@Override
	public void mouseDoubleClicked(MouseEvent me) {
		Helper.getRootFigure(fFigure).getTimeViewDetails().resetDisplaySettings();
		me.consume();
	}
}
