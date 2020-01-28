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
import org.eclipse.nebula.timeline.ICursor;
import org.eclipse.nebula.timeline.ITimeline;

public class DefaultTimelineEditingSupport implements ITimelineEditingSupport {

	private ITimeline fInput;

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof ITimeline)
			fInput = (ITimeline) newInput;
	}

	@Override
	public Object addCursor(ICursor cursor) {
		if (fInput != null) {
			fInput.getCursors().add(cursor);
			return cursor;
		}

		return null;
	}

	@Override
	public void removeCursor(Object cursor) {
		if (fInput != null)
			fInput.getCursors().remove(cursor);
	}
}
