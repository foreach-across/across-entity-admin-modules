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
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyValue;
import lombok.NonNull;
import org.springframework.validation.Errors;

/**
 * Basic binder interface for a single property of an entity, allowing
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
public interface EntityPropertyBinder
{
	/**
	 * Contains the item key for this property binder. Only set and relevant in case
	 * the binder is a member of a collection, represented by a parent {@link ListEntityPropertyBinder}.
	 *
	 * @return item key - only set when part of a {@link ListEntityPropertyBinder}
	 */
	String getItemKey();

	/**
	 * @return Sort index value, only relevant when the property value is part of a (sorted) collection.
	 */
	long getSortIndex();

	/**
	 * Set the sort index of a property, only relevant when part of a sorted collection.
	 *
	 * @param sortIndex of this property
	 */
	void setSortIndex( long sortIndex );

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
	Object getOriginalValue();

	/**
	 * @return the current value
	 */
	Object getValue();

	/**
	 * Get the current value or initialize a new value if it has not been set.
	 * Use this method if you want to create intermediate bean paths because you want to update
	 * underlying properties of the entity.
	 * <p/>
	 * Using {@code getInitializedValue()} will also delegate any existing value to
	 * {@link EntityPropertyController#createDto(EntityPropertyBindingContext, Object)} for creating a DTO
	 * object that can be safely updated without directly applying the changes to the underlying entity it possibly represents.
	 *
	 * @return the value and initialize a new value if necessary
	 * @see #createNewValue()
	 * @see EntityPropertyController#createDto(EntityPropertyBindingContext, Object)
	 */
	Object getInitializedValue();

	/**
	 * Update the value.
	 *
	 * @param value to set
	 */
	void setValue( Object value );

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
	 * Check if the property that this binder represents has been modified.
	 *
	 * @return true if the property has been modified, a deleted property is usually also considered modified
	 */
	boolean isModified();

	/**
	 * Check if this property has possibly been modified since the previous call to {@link #applyValue()}.
	 *
	 * @return true if the property is dirty
	 */
	boolean isDirty();

	/**
	 * Initialize a new value for this property.
	 * Will not actually update the property value itself but will attempt to return a new instance that can be set as the value.
	 *
	 * @return new value, can be {@code null}
	 */
	Object createNewValue();

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
	 * Attempt to resolve a child property binder for the given property descriptor.
	 * The descriptor is expected to represent either the same property the current binder is for,
	 * or a child property.
	 *
	 * @param descriptor for the property you want to resolve
	 * @return property binder
	 */
	EntityPropertyBinder resolvePropertyBinder( @NonNull EntityPropertyDescriptor descriptor );

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
}
