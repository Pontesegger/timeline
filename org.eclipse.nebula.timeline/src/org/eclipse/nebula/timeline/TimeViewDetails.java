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

package org.eclipse.nebula.timeline;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.nebula.timeline.figures.RootFigure;

public class TimeViewDetails {

	/** Scaling for detail area. A factor > 1 mean we are zooming in. */
	private double fScaleFactor = 1;
	/** Physical size of the available screen area to draw on. */
	private Rectangle fScreenArea = new PrecisionRectangle();

	/** Minimal area needed to contain all events. From start imestamp of first event until end timestamp of last event. */
	private Rectangle fRequiredEventArea = new PrecisionRectangle();
	/** Offset in eventtime where to start drawing. */
	private Point fOffset = new PrecisionPoint(0, 0);

	//
	// private boolean fEmpty = true;
	//
	// /**
	// * Area containing all events. x is the start timestamp of the first event, x + width is the end timestamp of the last event.
	// */
	// private Rectangle fEventsArea = new Rectangle();
	// private Rectangle fScreenBounds;
	private final RootFigure fRootFigure;

	public TimeViewDetails(RootFigure rootFigure) {
		fRootFigure = rootFigure;
	}

	/**
	 * Reset scale factor and screen offset to defaults.
	 */
	public void resetDisplaySettings() {
		fScaleFactor = 1;
		fOffset = new PrecisionPoint(fRequiredEventArea.x(), 0);

		fRootFigure.fireTimebaseChanged();
	}

	public Rectangle getVisibleEventArea() {
		final Rectangle visibleArea = new PrecisionRectangle(0, 0, fScreenArea.width(), fScreenArea.height());
		visibleArea.performScale(1 / fScaleFactor);
		visibleArea.setX(fOffset.x());

		return visibleArea;
	}

	public long screenOffsetToEventTime(int screenOffset) {
		double offset = screenOffset - fScreenArea.x();
		offset /= getScaleFactor();
		return Math.round((offset + getOffset().preciseX()));
	}

	public int eventTimeToScreenOffset(long eventTime) {
		eventTime -= getOffset().x();
		final double scaledEventTime = eventTime * fScaleFactor;

		return (int) ((fScreenArea.x() + scaledEventTime));
	}

	public Point getOffset() {
		return fOffset;
	}

	/**
	 * Total area to be displayed in eventTime. Unit is [ns] as we get them directly from the events.
	 *
	 * @return area containing all events (y/height are not valid)
	 */
	public Rectangle getEventArea() {
		final Rectangle eventArea = fRequiredEventArea.getCopy().expand(fRequiredEventArea.width() * 0.06, 0);

		// return eventArea;

		return fRequiredEventArea.getCopy();
	}

	public void resetEventArea() {
		fRequiredEventArea = new PrecisionRectangle();
	}

	public Rectangle getScreenArea() {
		return fScreenArea;
	}

	public void addEvent(ITimelineEvent event) {
		final PrecisionRectangle eventDimensions = new PrecisionRectangle(event.getStartTimestamp(), 0, event.getDuration(), 1);
		if (fRequiredEventArea.isEmpty()) {
			fRequiredEventArea = eventDimensions;
			fOffset = new PrecisionPoint(event.getStartTimestamp(), 0);
		} else
			fRequiredEventArea.union(eventDimensions);
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
		final double scaleFactor = getEventArea().preciseWidth() / fScreenArea.preciseWidth();

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
		final Rectangle eventArea = getEventArea();
		if (eventTime < 0) {
			if (fOffset.x() > eventArea.x()) {
				fOffset.performTranslate((int) eventTime, 0);
				adjustIvalidOffset();
				fRootFigure.fireTimebaseChanged();
			}

		} else if (eventTime > 0) {
			if (fOffset.x() < ((eventArea.x() + eventArea.width()) - getVisibleEventArea().width())) {
				fOffset.performTranslate((int) eventTime, 0);
				adjustIvalidOffset();
				fRootFigure.fireTimebaseChanged();
			}
		} else
			return false;

		return true;
	}

	public void setScreenArea(Rectangle screenBounds) {
		if (fScreenArea.isEmpty())
			fRootFigure.fireTimebaseChanged();

		fScreenArea = screenBounds;
	}

	public void zoom(double factor, int zoomCenterX) {
		final long eventTimeAtZoomCenter = screenOffsetToEventTime(zoomCenterX);

		if (factor < 1) {
			// zoom out
			if (getVisibleEventArea().width() < getEventArea().width()) {
				// only zoom out when not the whole area is visible
				fScaleFactor *= factor;

				final long updatedEventTime = screenOffsetToEventTime(zoomCenterX);
				fOffset.performTranslate((int) (eventTimeAtZoomCenter - updatedEventTime), 0);
				adjustIvalidOffset();

				fRootFigure.fireTimebaseChanged();
			}

		} else {
			// zoom in
			fScaleFactor *= factor;

			final long updatedEventTime = screenOffsetToEventTime(zoomCenterX);
			fOffset.performTranslate((int) (eventTimeAtZoomCenter - updatedEventTime), 0);
			adjustIvalidOffset();

			fRootFigure.fireTimebaseChanged();
		}
	}

	/**
	 * Scale a rectangle given in eventTime to screen coordinates for the overview figure.
	 *
	 * @param eventArea
	 * @return area on screen representing eventArea
	 */
	public Rectangle scaleToOverview(Rectangle eventArea) {
		final double scaleFactor = fScreenArea.preciseWidth() / getEventArea().preciseWidth();
		final Rectangle scaledArea = eventArea.getCopy();
		scaledArea.performScale(scaleFactor);

		return scaledArea;
	}

	/**
	 * Calculate the event time for a given coordinate on the overview figure.
	 *
	 * @param screenOffset
	 *            x offset on the overview figure
	 * @return coordinate in eventTime
	 */
	public long overviewScreenOffsetToEventTime(int screenOffset) {
		double offset = screenOffset - fScreenArea.x();

		final double scaleFactor = getEventArea().preciseWidth() / fScreenArea.preciseWidth();
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
	public void setOffset(long eventTime) {
		getOffset().setX((int) eventTime);
		adjustIvalidOffset();

		fRootFigure.fireTimebaseChanged();
	}

	private void adjustIvalidOffset() {
		if (fOffset.x() > ((getEventArea().x() + getEventArea().width()) - getVisibleEventArea().width()))
			fOffset.setX((getEventArea().x() + getEventArea().width()) - getVisibleEventArea().width());

		if (fOffset.x() < getEventArea().x())
			fOffset.setX(getEventArea().x());
	}

	/**
	 * Reveal a given area on screen and center it. Zoom is adjusted in case the event cannot be displayed as a whole on screen.
	 *
	 * @param revealArea
	 *            area (x dimensions) to reveal
	 */
	public void revealEvent(PrecisionRectangle revealArea) {
		if (getVisibleEventArea().width() <= revealArea.width())
			fScaleFactor = getScreenArea().width() / (revealArea.width() * 3.0d);

		setOffset(revealArea.x() - ((getVisibleEventArea().width() - revealArea.width()) / 2));

		fRootFigure.fireTimebaseChanged();
	}
}
