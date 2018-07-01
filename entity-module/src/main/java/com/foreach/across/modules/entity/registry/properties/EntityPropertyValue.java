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

package com.foreach.across.modules.entity.registry.properties;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents a property value that should be applied.
 *
 * @param <T> property type
 * @author Arne Vandamme
 * @see EntityPropertyController
 * @since 3.1.0
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public final class EntityPropertyValue<T>
{
	/**
	 * -- GETTER --
	 * Previous value of the property - if relevant and could be determined.
	 */
	private final T oldValue;

	/**
	 * -- GETTER --
	 * New value for the property.
	 */
	private final T newValue;

	/**
	 * -- GETTER --
	 * Should the property value be deleted.
	 * If {@code true} then the value of {@link #getNewValue()} should be {@code null}.
	 */
	private final boolean deleted;
}
