/*******************************************************************************
 * Copyright (c) 2020 ponteseg and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ponteseg - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.timeline.jface;

import org.eclipse.draw2d.Border;
import org.eclipse.swt.graphics.Color;

public interface ITimelineStyleProvider {

	/**
	 * Called when the composite gets disposed. Clean up colors and fonts.
	 */
	default void dispose() {
	};

	/**
	 * Get diagram background color.
	 *
	 * @return background color
	 */
	Color getBackgroundColor();

	/**
	 * Get the border for the detail area.
	 *
	 * @return detail area border
	 */
	Border getDetailAreaBorder();

	/**
	 * Get the border for the overview area.
	 *
	 * @return overview area border
	 */
	Border getOverviewAreaBorder();

	/**
	 * Display the time axis below the detail area.
	 *
	 * @return <code>true</code> to display the time axis, <code>false</code> to hide
	 */
	boolean showTimeAxis();

	/**
	 * Display the grid in the detail area.
	 *
	 * @return <code>true</code> to display the grid, <code>false</code> to hide
	 */
	boolean showGrid();

	/**
	 * Get the color of the grid.
	 *
	 * @return grid color
	 */
	Color getGridColor();

	/**
	 * Get the line style for the grid.
	 *
	 * @return grid line style
	 */
	int getGridLineStyle();

	/**
	 * Get the border for the overview selection figure.
	 *
	 * @return overview selection figure border
	 */
	Border getOverviewSelectionBorder();

	/**
	 * Get the background color for the overview selection.
	 *
	 * @return figure background color
	 */
	Color getOverviewSelectionBackgroundColor();

	/**
	 * Get the alpha value for the overview selection figure.
	 *
	 * @return alpha value
	 */
	int getOverviewSelectionBackgroundAlpha();

	/**
	 * Get the border for a track.
	 *
	 * @param title
	 *            track title
	 *
	 * @return track border
	 */
	Border getTrackBorder(String title);

	/**
	 * Get the color for a non-selected cursor.
	 *
	 * @return cursor color
	 */
	Color getCursorColor();

	/**
	 * Get the color for a selected cursor.
	 *
	 * @return selected cursor color
	 */
	Color getSelectedCursorColor();
}
