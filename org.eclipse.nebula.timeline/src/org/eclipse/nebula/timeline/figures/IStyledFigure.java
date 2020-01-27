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

package org.eclipse.nebula.timeline.figures;

import org.eclipse.nebula.timeline.jface.ITimelineStyleProvider;

public interface IStyledFigure {

	void updateStyle(ITimelineStyleProvider styleProvider);
}
