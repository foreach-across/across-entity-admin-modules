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

package com.foreach.across.modules.entity.query.collections;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Wraps a single item in a collection during {@link com.foreach.across.modules.entity.query.EntityQuery} execution.
 * Only for internal use.
 *
 * @author Arne Vandamme
 * @see CollectionEntityQueryExecutor
 * @since 3.1.0
 */
@RequiredArgsConstructor
class CollectionEntityQueryItem<T>
{
	@Getter
	private final T item;
	private final EntityPropertyRegistry propertyRegistry;
	private final Map<String, ValueHolder> propertyValues = new HashMap<>();

	@SuppressWarnings("unchecked")
	public <T> T getPropertyValue( String propertyName ) {
		return (T) propertyValues.computeIfAbsent( propertyName, k -> new ValueHolder( propertyRegistry.getProperty( k ).getPropertyValue( item ) ) )
		                         .getValue();
	}

	@RequiredArgsConstructor
	private static class ValueHolder
	{
		@Getter
		private final Object value;
	}
}
