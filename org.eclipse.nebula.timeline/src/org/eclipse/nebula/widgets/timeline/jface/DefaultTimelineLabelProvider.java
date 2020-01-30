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

package org.eclipse.nebula.widgets.timeline.jface;

import org.eclipse.jface.viewers.IToolTipProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.nebula.timeline.ITimelineEvent;

public class DefaultTimelineLabelProvider extends LabelProvider implements IToolTipProvider {

	@Override
	public String getToolTipText(Object element) {
		if (element instanceof ITimelineEvent)
			return ((ITimelineEvent) element).getMessage();

		return null;
	}
}
