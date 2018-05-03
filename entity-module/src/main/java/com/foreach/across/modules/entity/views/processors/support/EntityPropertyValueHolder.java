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

import org.springframework.validation.Errors; /**
 * Marker interface representing the value of a property described by
 * an {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor} for an entity.
 *
 * @author Arne Vandamme
 * @since 3.1.0
 */
public interface EntityPropertyValueHolder<T>
{
	/**
	 * @return the value
	 */
	T getValue();

	/**
	 * Update the value.
	 *
	 * @param value to set
	 */
	void setValue( T value );

	/**
	 * Calls the {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyController#save(Object, Object)} method
	 * for the given property.
	 *
	 * @return true if save has been executed
	 */
	boolean save();

	boolean validate( Errors errors, Object... validationHints );
}
