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

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.nebula.timeline.ICursor;
import org.eclipse.nebula.timeline.ITimelineEvent;

public interface ITimelineContentProvider extends IStructuredContentProvider {

	Object[] getTracks(Object input);

	Object[] getLanes(Object track);

	Object[] getEvents(Object lane);

	Object[] getCursors(Object input);

	ITimelineEvent toEvent(Object eventElement);

	ICursor toCursor(Object element);
}
