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

import com.foreach.across.modules.entity.registry.properties.EntityPropertyBindingContext;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyController;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyValue;
import org.springframework.validation.Errors;

/**
 * Helper class representing a single property of an entity, allowing
 * access to the property value, updating the value, validating it and saving it.
 * <p/>
 * Dispatches to the {@link EntityPropertyController} for the actual handling.
 * <p/>
 * Mainly for internal use in EntityModule.
 *
 * @author Arne Vandamme
 * @see EntityPropertiesBinder
 * @since 3.2.0
 */
public interface EntityPropertyBinder<T>
{
	/**
	 * @return Sort index value, only relevant when the property value is part of a (sorted) collection.
	 */
	int getSortIndex();

	/**
	 * Set the sort index of a property, only relevant when part of a sorted collection.
	 *
	 * @param sortIndex of this property
	 */
	void setSortIndex( int sortIndex );

	/**
	 * Set the value of this property as bound using a data binder.
	 * This usually signals that a property was present in a web form.
	 * Usually setting this to {@code true} means that the property value should be deleted if no actual value has been set.
	 */
	void setBound( boolean bound );

	/**
	 * @return has this property been bound
	 */
	boolean isBound();

	/**
	 * @return the order in which controller methods of this property should be executed relative to all other properties (and the base entity itself)
	 */
	int getControllerOrder();

	/**
	 * Get the original value which was present before binding changes.
	 *
	 * @return the original value that was present before
	 */
	T getOriginalValue();

	/**
	 * @return the current value
	 */
	T getValue();

	/**
	 * Get the current value or initialize a new value if it has not been set.
	 * What initializing entails is context dependent but this method is useful
	 * for intermediate bean paths where you want to ensure that the intermediate property value is set.
	 *
	 * @return the value and initialize a new value if necessary
	 * @see #createNewValue()
	 */
	default T getInitializedValue() {
		T currentValue = getValue();

		if ( currentValue == null ) {
			T newValue = createNewValue();
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
	 * Explicitly set this property as deleted. The value returned will usually be {@code null}.
	 *
	 * @param deleted true if property should be considered deleted
	 */
	void setDeleted( boolean deleted );

	/**
	 * @return true if the property is considered deleted
	 */
	boolean isDeleted();

	/**
	 * @return true if the property has been modified, a deleted property is usually also considered modified
	 */
	boolean isModified();

	/**
	 * Initialize a new value for this property.
	 * Will not actually update the property value itself but will attempt to return a new instance that can be set as the value.
	 *
	 * @return new value, can be {@code null}
	 */
	T createNewValue();

	/**
	 * Apply the current property value to the owning entity by calling {@link EntityPropertyController#applyValue(EntityPropertyBindingContext, EntityPropertyValue)}.
	 * If the property is considered deleted, the value of {@link EntityPropertyValue#isDeleted()} will be {@code true}.
	 *
	 * @return true if value has been applied
	 */
	boolean applyValue();

	/**
	 * Calls the {@link EntityPropertyController#save(EntityPropertyBindingContext, EntityPropertyValue)} for the given property.
	 *
	 * @return true if save has been executed
	 */
	boolean save();

	/**
	 * Validate this property value.
	 *
	 * @param errors          object in which to register the errors
	 * @param validationHints to customize validation rules
	 * @return true if validation was successful, no validation errors have been added for this property
	 */
	boolean validate( Errors errors, Object... validationHints );

	/**
	 * Reset bind status for this property. This will reset tracking properties related
	 * to detecting if a property value has been removed.
	 */
	void resetBindStatus();

	/**
	 * Enable binding for this property. This signals that property binding is in progress, which allows
	 * implementations (like {@link ListEntityPropertyBinder} and {@link MapEntityPropertyBinder}) to make decisions
	 * on how to consider the current state of its property value.
	 * <p/>
	 * For example when accessing {@link ListEntityPropertyBinder#getItems()} while binding is active, the item list might be empty
	 * as it should be reset. When binding is no longer active but no calls have been made to the property,
	 * the items collection will not have modified and the original value will be returned.
	 */
	default void enableBinding( boolean enabled ) {
	}

	/**
	 * @return the direct path to this property on the {@link EntityPropertiesBinder} it belongs to
	 */
	@Deprecated
	String getBinderPath();
}
