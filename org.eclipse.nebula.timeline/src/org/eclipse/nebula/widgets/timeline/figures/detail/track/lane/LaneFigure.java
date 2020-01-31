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

package org.eclipse.nebula.widgets.timeline.figures.detail.track.lane;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.nebula.widgets.timeline.Helper;
import org.eclipse.nebula.widgets.timeline.ITimelineEvent;
import org.eclipse.nebula.widgets.timeline.TimeBaseConverter;
import org.eclipse.nebula.widgets.timeline.figures.IStyledFigure;
import org.eclipse.nebula.widgets.timeline.jface.ITimelineStyleProvider;

public class LaneFigure extends Figure implements IStyledFigure {

	private int fPreferredHeight;

	public LaneFigure(ITimelineStyleProvider styleProvider) {
		setLayoutManager(new LaneLayout());

		updateStyle(styleProvider);
	}

	@Override
	public void updateStyle(ITimelineStyleProvider styleProvider) {
		setForegroundColor(styleProvider.getLaneColor());
		fPreferredHeight = styleProvider.getLaneHeight();
	}

	@Override
	public Dimension getPreferredSize(int wHint, int hHint) {
		return new Dimension(wHint, fPreferredHeight);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void add(IFigure figure, Object constraint, int index) {
		super.add(figure, constraint, index);

		getChildren().sort((o1, o2) -> {
			return ((EventFigure) o1).compareTo((EventFigure) o2);
		});
	}

	public List<EventFigure> getChildEventFigures() {
		return ((List<?>) getChildren()).stream().filter(p -> p instanceof EventFigure).map(p -> (EventFigure) p).collect(Collectors.toList());
	}

	private class LaneLayout extends XYLayout {

		private Rectangle getContraintAsRectangle(IFigure figure) {
			final ITimelineEvent event = (ITimelineEvent) getConstraint(figure);

			return new PrecisionRectangle(event.getStartTimestamp(), 0, event.getDuration(), 1);
		}

		@Override
		public void layout(IFigure parent) {
			final TimeBaseConverter timeViewDetails = Helper.getRootFigure(parent).getTimeViewDetails();

			final Iterator<?> children = parent.getChildren().iterator();
			final Point offset = getOrigin(parent);
			IFigure f;
			while (children.hasNext()) {
				f = (IFigure) children.next();
				final Rectangle bounds = getContraintAsRectangle(f);

				// now bounds refers to the original bounds (unscaled and unmoved)
				bounds.performTranslate((int) -timeViewDetails.getOffset(), 0);
				bounds.performScale(timeViewDetails.getScaleFactor());

				bounds.performTranslate(offset.x(), offset.y());

				bounds.setHeight(parent.getBounds().height()); // height got scaled, revert

				if (bounds.width() == 0)
					bounds.setWidth(1);

				f.setBounds(bounds);
			}
		}

		/**
		 * This is a copy of the parent method. Only change is that we call getContraintAsRectangle() instead of directly accessing the constraints member.
		 */
		@Override
		protected Dimension calculatePreferredSize(IFigure f, int wHint, int hHint) {
			final Rectangle rect = new Rectangle();
			final ListIterator children = f.getChildren().listIterator();
			while (children.hasNext()) {
				final IFigure child = (IFigure) children.next();
				Rectangle r = getContraintAsRectangle(child);
				if (r == null)
					continue;

				if ((r.width == -1) || (r.height == -1)) {
					final Dimension preferredSize = child.getPreferredSize(r.width, r.height);
					r = r.getCopy();
					if (r.width == -1)
						r.width = preferredSize.width;
					if (r.height == -1)
						r.height = preferredSize.height;
				}
				rect.union(r);
			}
			final Dimension d = rect.getSize();
			final Insets insets = f.getInsets();
			return new Dimension(d.width + insets.getWidth(), d.height + insets.getHeight()).union(getBorderPreferredSize(f));
		}
	}
}
