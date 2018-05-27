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

import org.springframework.validation.Errors;

/**
 * Helper class representing a single property of an entity, allowing
 * access to the property value, updating the value, validating it and saving it.
 * <p/>
 * Dispatches to the {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyController}
 * for the property
 * <p/>
 * Mainly for internal use in EntityModule.
 *
 * @author Arne Vandamme
 * @since 3.1.0
 * @see EntityPropertiesBinder
 */
public interface EntityPropertyValueHolder<T>
{
	/**
	 * @return the value
	 */
	T getValue();

	/**
0	 * Update the value.
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

	boolean applyValue();

	/**
	 * @return the order in which controller methods of this property should be executed relative to all other properties (and the base entity itself)
	 */
	int getControllerOrder();
}
