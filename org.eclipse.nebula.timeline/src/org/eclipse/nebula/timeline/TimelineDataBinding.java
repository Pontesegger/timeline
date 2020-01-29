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

package org.eclipse.nebula.timeline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.nebula.timeline.figures.detail.cursor.CursorFigure;
import org.eclipse.nebula.timeline.jface.TimelineViewer;
import org.eclipse.nebula.timeline.listeners.ICursorListener;
import org.eclipse.ui.progress.UIJob;

/**
 * Data binding that automatically updates the viewer on model updates. The model needs to be an instance of the ITimeline EMF model to correctly receive
 * notifications.
 */
public class TimelineDataBinding extends AdapterImpl implements ICursorListener, ISelectionChangedListener {

	private final TimelineViewer fViewer;
	private final ITimeline fModel;

	private ViewerRefresher fViewerRefresher = null;

	private volatile boolean fIgnoreModelChanges = false;

	public TimelineDataBinding(TimelineViewer viewer, ITimeline model) {
		fViewer = viewer;
		fModel = model;

		fViewer.getControl().getRootFigure().addCursorListener(this);
		fViewer.addSelectionChangedListener(this);

		fModel.eAdapters().add(this);
	}

	@Override
	public synchronized void notifyCursorCreated(ICursor cursor, CursorFigure figure) {
		fIgnoreModelChanges = true;
		fModel.getCursors().add(cursor);
		fIgnoreModelChanges = false;
	}

	@Override
	public synchronized void notifyCursorDeleted(ICursor cursor) {
		fIgnoreModelChanges = true;
		fModel.getCursors().remove(cursor);
		fIgnoreModelChanges = false;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		fIgnoreModelChanges = true;

		if (event.getStructuredSelection().isEmpty())
			fModel.setSelectedEvent(null);
		
		else if (event.getStructuredSelection().getFirstElement() instanceof ITimelineEvent)
			fModel.setSelectedEvent((ITimelineEvent) event.getStructuredSelection().getFirstElement());

		fIgnoreModelChanges = false;
	}

	private synchronized void refreshElement(Object element) {
		if (!fIgnoreModelChanges)
			getUIRefreshJob().addElement(element);
	}

	private ViewerRefresher getUIRefreshJob() {
		if (fViewerRefresher == null)
			fViewerRefresher = new ViewerRefresher();

		return fViewerRefresher;
	}

	@Override
	public void notifyChanged(Notification msg) {
		if (!msg.isTouch()) {
			System.out.println("Changed: " + msg.getNotifier());

			if (msg.getEventType() == Notification.ADD) {
				final Object value = msg.getNewValue();
				if (value instanceof Notifier)
					((Notifier) value).eAdapters().add(this);

			} else if (msg.getEventType() == Notification.REMOVE) {
				final Object value = msg.getOldValue();
				if (value instanceof Notifier)
					((Notifier) value).eAdapters().remove(this);

			} else if (msg.getEventType() == Notification.SET) {
				// TODO check for selection and update
			}

			refreshElement(msg.getNotifier());
		}
	}

	private class ViewerRefresher extends UIJob {

		/** Execution delay in [ms]. */
		private static final long DISPLAY_DELAY = 300;

		private final Collection<Object> fElementsToRefresh = new ArrayList<>();

		public ViewerRefresher() {
			super("Refresh timeline control");

			setSystem(true);
		}

		public void addElement(Object element) {
			synchronized (fElementsToRefresh) {
				fElementsToRefresh.add(element);
			}

			schedule(DISPLAY_DELAY);
		}

		@Override
		public IStatus runInUIThread(IProgressMonitor monitor) {
			Collection<Object> elementsToRefresh;
			synchronized (fElementsToRefresh) {
				elementsToRefresh = new HashSet<>(fElementsToRefresh);
				fElementsToRefresh.clear();
			}

			// TODO potentially we do not need to update child objects if parent object is already part of the list
			for (final Object element : elementsToRefresh) {
				if (monitor.isCanceled())
					return Status.CANCEL_STATUS;

				fViewer.refresh(element);
			}

			return Status.OK_STATUS;
		}
	}
}
