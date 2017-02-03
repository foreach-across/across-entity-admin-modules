/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.foreach.across.modules.entity.views.processors.support;

import com.foreach.across.modules.web.ui.ViewElementBuilder;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Extension of a {@link HashMap} that reprensents a collection of {@link ViewElementBuilder}s
 * by name.  Adds type coercing methods.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@SuppressWarnings("unchecked")
public class ViewElementBuilderMap extends LinkedHashMap<String, ViewElementBuilder<?>>
{
	public <Y extends V, V extends ViewElementBuilder<?>> Y get( String name, Class<V> expectedType ) {
		return (Y) expectedType.cast( super.get( name ) );
	}

	public <Y extends V, V extends ViewElementBuilder<?>> Y remove( String name, Class<V> expectedType ) {
		return (Y) expectedType.cast( super.remove( name ) );
	}
}
