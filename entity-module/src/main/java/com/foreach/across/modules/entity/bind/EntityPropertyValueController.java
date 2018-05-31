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

package com.foreach.across.modules.entity.bind;

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
 * @see EntityPropertiesBinder
 * @since 3.1.0
 */
public interface EntityPropertyValueController<T>
{
	/**
	 * Set the value of this property as bound using a data binder.
	 * Usually setting this to {@code true} means that the property value should be deleted
	 * if no actual value has been set.
	 */
	void setBound( boolean bound );

	/**
	 * @return the value
	 */
	T getValue();

	/**
	 * Get the current value or initialize a new value if has not been set.
	 * What initializing entails is context dependent but this method is useful
	 * for intermediate bean paths where you want to ensure that the intermediate property value is set.
	 *
	 * @return the value and initialize a new value if necessary
	 */
	default T getInitializedValue() {
		T currentValue = getValue();

		if ( currentValue == null ) {
			T newValue = initializeValue();
			if ( newValue != null ) {
				setValue( newValue );
				return newValue;
			}
		}

		return currentValue;
	}

	/**
	 * Update the value.
	 *
	 * @param value to set
	 */
	void setValue( T value );

	/**
	 * Initialize a new value for this property.
	 * Will not actually update the property value itself but will attempt to return a new instance that can be set as the value.
	 *
	 * @return new value, can be {@code null}
	 */
	T initializeValue();

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

	/**
	 * Reset bind status for this property. This will reset tracking properties related
	 * to detecting if a property value has been removed.
	 */
	void resetBindStatus();
}
