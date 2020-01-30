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

package org.eclipse.nebula.widgets.timeline.layouts;

import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * @author christian
 *
 */
public class CenterLayout extends AbstractLayout implements LayoutManager {

	private static final int PADDING_X = 10;
	private static final int PADDING_Y = 3;

	@Override
	public void layout(IFigure container) {

		for (final Object child : container.getChildren()) {
			final Dimension shrinked = container.getSize().getShrinked(PADDING_X * 2, PADDING_Y * 2);
			final Rectangle rectangle = container.getBounds().getTranslated(PADDING_X, PADDING_Y);
			rectangle.setSize(shrinked);
			((IFigure) child).setBounds(rectangle);
		}
	}

	@Override
	protected Dimension calculatePreferredSize(IFigure container, int wHint, int hHint) {
		for (final Object child : container.getChildren()) {
			final Dimension childDimension = ((IFigure) child).getPreferredSize();
			return childDimension.getExpanded(PADDING_X * 2, PADDING_Y * 2);
		}

		return new Dimension(PADDING_X * 2, PADDING_Y * 2);
	}
}
