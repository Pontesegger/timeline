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

package org.eclipse.nebula.widgets.timeline;

import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.nebula.widgets.timeline.figures.RootFigure;

public class TimeBaseConverter {

	/** Scaling for detail area. A factor > 1 mean we are zooming in. */
	private double fScaleFactor = 1;

	/** Physical size of the available screen area to draw on. */
	private Timing fScreenArea = new Timing(0);

	/** Minimal area needed to contain all events. From start timestamp of first event until end timestamp of last event. */
	private Timing fRequiredEventArea = null;

	/** Offset in eventTime where to start drawing. */
	private double fOffset = 0;

	private final RootFigure fRootFigure;

	public TimeBaseConverter(RootFigure rootFigure) {
		fRootFigure = rootFigure;
	}

	/**
	 * Reset scale factor and screen offset to defaults.
	 */
	public void resetDisplaySettings() {
		fScaleFactor = 1;
		fOffset = getEventArea().left();

		fRootFigure.fireTimebaseChanged();
	}

	public Timing getVisibleEventArea() {
		final Timing visibleArea = getScreenArea().copy();
		visibleArea.scale(1 / fScaleFactor);
		visibleArea.translate(fOffset - visibleArea.left());

		return visibleArea;
	}

	public long screenOffsetToEventTime(int screenOffset) {
		double offset = screenOffset - fScreenArea.left();
		offset /= getScaleFactor();
		return Math.round((offset + fOffset));
	}

	public int eventTimeToScreenOffset(double eventTime) {
		eventTime -= fOffset;
		final double scaledEventTime = eventTime * fScaleFactor;

		return (int) ((fScreenArea.left() + scaledEventTime));
	}

	/**
	 * Total area to be displayed in eventTime. Unit is [ns] as we get them directly from the events.
	 *
	 * @return area containing all events in eventTime
	 */
	public Timing getEventArea() {
		return (fRequiredEventArea != null) ? fRequiredEventArea.copy() : new Timing(0);
	}

	public void resetEventArea() {
		fRequiredEventArea = null;
	}

	public void addEvent(ITimed event) {
		if (fRequiredEventArea == null)
			fRequiredEventArea = event.getTiming().copy();

		else
			fRequiredEventArea.union(event.getTiming());
	}

	public Timing getScreenArea() {
		return fScreenArea;
	}

	/**
	 * Translate the offset by pixels on the screen of the detail area.
	 *
	 * @param screenPixels
	 *            pixels on the screen to translate
	 * @return <code>true</code> when offset got changed
	 */
	public boolean translateDetailAreaOffset(int screenPixels) {
		return translateEventTime(Math.round(screenPixels / getScaleFactor()));
	}

	/**
	 * Translate the offset by pixels on the screen of the overview area.
	 *
	 * @param screenPixels
	 *            pixels on the screen to translate
	 * @return <code>true</code> when offset got changed
	 */
	public boolean translateOverviewAreaOffset(int screenPixels) {
		final double scaleFactor = getEventArea().getDuration() / fScreenArea.getDuration();

		return translateEventTime(Math.round(scaleFactor * screenPixels));
	}

	/**
	 * Translate the event time. The time cannot be translated past the overall display area.
	 *
	 * @param eventTime
	 *            eventTime to change
	 * @return
	 */
	private boolean translateEventTime(long eventTime) {
		final Timing eventArea = getEventArea();
		if (eventTime < 0) {
			if (fOffset > eventArea.left())
				setOffset(fOffset + eventTime);

		} else if (eventTime > 0) {
			if (fOffset < ((eventArea.right()) - getVisibleEventArea().getDuration()))
				setOffset(fOffset + eventTime);

		} else
			return false;

		return true;
	}

	public void setScreenArea(Rectangle screenBounds) {
		final boolean screenNeedsUpdate = fScreenArea.isEmpty();
		fScreenArea = new Timing(screenBounds.x(), screenBounds.width());

		if (screenNeedsUpdate)
			fRootFigure.fireTimebaseChanged();
	}

	public void zoom(double factor, int zoomCenterX) {
		final long eventTimeAtZoomCenter = screenOffsetToEventTime(zoomCenterX);

		if (factor < 1) {
			// zoom out
			if (getVisibleEventArea().getDuration() < getEventArea().getDuration()) {
				// only zoom out when not the whole area is visible
				fScaleFactor *= factor;

				final long updatedEventTime = screenOffsetToEventTime(zoomCenterX);
				fOffset += eventTimeAtZoomCenter - updatedEventTime;
				adjustInvalidOffset();

				fRootFigure.fireTimebaseChanged();
			}

		} else {
			// zoom in
			fScaleFactor *= factor;

			final long updatedEventTime = screenOffsetToEventTime(zoomCenterX);
			fOffset += eventTimeAtZoomCenter - updatedEventTime;
			adjustInvalidOffset();

			fRootFigure.fireTimebaseChanged();
		}
	}

	/**
	 * Scale a rectangle given in eventTime to screen coordinates for the overview figure.
	 *
	 * @param visibleEventArea
	 * @return area on screen representing eventArea
	 */
	public Timing scaleToOverview(Timing timing) {
		final double scaleFactor = fScreenArea.getDuration() / getEventArea().getDuration();
		final Timing scaledTiming = timing.copy();
		scaledTiming.scale(scaleFactor);

		return scaledTiming;
	}

	/**
	 * Calculate the event time for a given coordinate on the overview figure.
	 *
	 * @param screenOffset
	 *            x offset on the overview figure
	 * @return coordinate in eventTime
	 */
	public long overviewScreenOffsetToEventTime(int screenOffset) {
		double offset = screenOffset - fScreenArea.left();

		final double scaleFactor = getEventArea().getDuration() / fScreenArea.getDuration();
		offset *= scaleFactor;

		return Math.round(offset);
	}

	public double getScaleFactor() {
		return fScaleFactor;
	}

	/**
	 * Set the offset to a defined value. The offset will automatically be adjusted regarding the total visible area.
	 *
	 * @param eventTime
	 *            time to set offset to
	 */
	public void setOffset(double eventTime) {
		fOffset = eventTime;
		adjustInvalidOffset();

		fRootFigure.fireTimebaseChanged();
	}

	private void adjustInvalidOffset() {
		if (fOffset > (getEventArea().right() - getVisibleEventArea().getDuration()))
			fOffset = getEventArea().right() - getVisibleEventArea().getDuration();

		if (fOffset < getEventArea().left())
			fOffset = getEventArea().left();
	}

	/**
	 * Reveal a given area on screen and center it. Zoom is adjusted in case the event cannot be displayed as a whole on screen.
	 *
	 * @param revealArea
	 *            area (x dimensions) to reveal
	 */
	public void revealEvent(PrecisionRectangle revealArea) {
		if (getVisibleEventArea().getDuration() <= revealArea.width())
			fScaleFactor = getScreenArea().getDuration() / (revealArea.width() * 3.0d);

		setOffset(revealArea.x() - ((getVisibleEventArea().getDuration() - revealArea.width()) / 2));

		fRootFigure.fireTimebaseChanged();
	}

	public double getOffset() {
		// FIXME remove method
		return fOffset;
	}
}
