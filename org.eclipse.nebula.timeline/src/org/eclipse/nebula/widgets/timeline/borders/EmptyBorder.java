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

package org.eclipse.nebula.widgets.timeline.borders;

import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Insets;

/**
 * @author christian
 *
 */
public class EmptyBorder extends AbstractBorder {

	private final Insets fInsets;

	public EmptyBorder(Insets insets) {
		fInsets = insets;
	}

	public EmptyBorder(int indent) {
		this(new Insets(indent));
	}

	@Override
	public Insets getInsets(IFigure figure) {
		return fInsets;
	}

	@Override
	public void paint(IFigure figure, Graphics graphics, Insets insets) {
	}
}
