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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw2d.IFigure;

public class ModelMap extends HashMap<Object, IFigure> {

	private final Map<IFigure, Object> fReverseMap = new HashMap<>();

	@Override
	public IFigure put(Object key, IFigure value) {
		fReverseMap.put(value, key);

		return super.put(key, value);
	}

	public Object getKey(IFigure value) {
		return fReverseMap.get(value);
	}

	@Override
	public IFigure remove(Object key) {
		final IFigure value = super.remove(key);
		fReverseMap.remove(value);

		return value;
	}

	public IFigure removeValue(Object modelElement) {
		return remove(fReverseMap.get(modelElement));
	}
}
