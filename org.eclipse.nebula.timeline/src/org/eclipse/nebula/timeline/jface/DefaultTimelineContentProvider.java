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

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.timeline.ILane;
import org.eclipse.nebula.timeline.ITimeline;
import org.eclipse.nebula.timeline.ITimelineEvent;
import org.eclipse.nebula.timeline.ITrack;

/**
 * Default content provider for timeline viewer. Expects the input to be of type {@link ITimeline}.
 */
public class DefaultTimelineContentProvider implements ITimelineContentProvider {

	private ITimeline fTimeline;

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (!(newInput instanceof ITimeline))
			throw new IllegalArgumentException("Input needs to implement ITimeline");

		fTimeline = (ITimeline) newInput;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getTracks();
	}

	@Override
	public Object[] getTracks() {
		return fTimeline.getTracks().toArray();
	}

	@Override
	public Object[] getLanes(Object track) {
		return ((ITrack) track).getLanes().toArray();
	}

	@Override
	public Object[] getEvents(Object lane) {
		return ((ILane) lane).getTimeEvents().toArray();
	}

	@Override
	public Object[] getCursors() {
		return fTimeline.getCursors().toArray();
	}

	@Override
	public ITimelineEvent toEvent(Object eventElement) {
		return (ITimelineEvent) eventElement;
	}
}
