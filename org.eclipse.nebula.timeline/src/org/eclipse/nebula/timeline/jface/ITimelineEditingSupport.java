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

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.nebula.timeline.ICursor;

public interface ITimelineEditingSupport extends IContentProvider {

	/**
	 * Add a cursor to the model. After addition, {@link #getCursors(Object)} should contain this cursor.
	 *
	 * @param cursor
	 *            cursor to add
	 * @return cursor object stored in model
	 */
	Object addCursor(ICursor cursor);

	/**
	 * Remove a cursor from the model. After removal, {@link #getCursors(Object)} should not contain this cursor.
	 *
	 * @param cursorObject
	 *            cursor object (model representation) to remove
	 */
	void removeCursor(Object cursorObject);
}
