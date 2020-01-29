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

import org.eclipse.nebula.timeline.ICursor;
import org.eclipse.nebula.timeline.ILane;
import org.eclipse.nebula.timeline.ITimeline;
import org.eclipse.nebula.timeline.ITimelineEvent;
import org.eclipse.nebula.timeline.ITrack;

/**
 * Default content provider for timeline viewer. Expects the input to be of type {@link ITimeline}.
 */
public class DefaultTimelineContentProvider implements ITimelineContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {
		return getTracks(inputElement);
	}

	@Override
	public Object[] getTracks(Object input) {
		if (input instanceof ITimeline)
			return ((ITimeline) input).getTracks().toArray();

		return new Object[0];
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
	public Object[] getCursors(Object input) {
		if (input instanceof ITimeline)
			return ((ITimeline) input).getCursors().toArray();

		return new Object[0];
	}

	@Override
	public ITimelineEvent toEvent(Object eventElement) {
		if (eventElement instanceof ITimelineEvent)
			return (ITimelineEvent) eventElement;

		return null;
	}

	@Override
	public ICursor toCursor(Object element) {
		if (element instanceof ICursor)
			return (ICursor) element;

		return null;
	}
}
