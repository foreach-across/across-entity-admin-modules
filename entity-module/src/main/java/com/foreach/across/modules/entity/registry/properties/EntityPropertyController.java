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

import org.springframework.core.Ordered;
import org.springframework.validation.Errors;

/**
 * Generic controller for a single property on an entity.
 * Allows reading and writing a property value, as well as validating a value.
 * <p/>
 * The difference between a {@link #save(Object, Object)} and a {@link #applyValue(Object, Object, Object)}
 * method, is that setting the value is usually done on the entity before the entity itself is saved.
 * The {@link #save(Object, Object)} method is useful for properties that are not actually present
 * on the entity instance itself.
 * <p/>
 * Likewise the {@link #delete(Object)} method is only relevant for properties where delete means
 * something. For most properties this is simply a no-op.
 *
 * @param <T> entity type that the property belongs to
 * @param <U> property type
 * @author Arne Vandamme
 * @since 3.2.0
 */
public interface EntityPropertyController<T, U> extends Ordered
{
	/**
	 * Controller methods for this property should be executed before the equivalent method
	 * on the entity itself. Eg. {@code validate()} on the property controller should be done
	 * before the entity validation.
	 */
	int BEFORE_ENTITY = Ordered.HIGHEST_PRECEDENCE + 1000;

	/**
	 * Controller methods for this property should be executed after the equivalent method
	 * on the entity itself. Eg. {@code validate()} on the property controller should be done
	 * after the entity validation.
	 */
	int AFTER_ENTITY = Ordered.LOWEST_PRECEDENCE - 1000;

	/**
	 * Fetches the current value of the property for the owning entity.
	 * Depending on the type of property this might be a simple getter being called,
	 * or something like a database retrieval happening.
	 *
	 * @param owner entity
	 * @return property value
	 */
	@Deprecated
	default U fetchValue( T owner ) {
		return fetchValue( new EntityPropertyBindingContext<>( owner ) );
	}

	U fetchValue( EntityPropertyBindingContext<T, ?> context );

	/**
	 * Create a new value for the property on the owning entity.
	 * This *only* creates a value instance but does not necessary assign the value to that property.
	 * Useful for initializing complex object values.
	 *
	 * @param owner entity
	 * @return valid property value
	 */
	@Deprecated
	default U createValue( T owner ) {
		return createValue( new EntityPropertyBindingContext<>( owner ) );
	}

	default U createValue( EntityPropertyBindingContext<T, ?> context ) {
		return null;
	}

	/**
	 * Validate a property value for the given owner entity.
	 * Validation errors should be registered on the {@link Errors} argument.
	 * Validating should happen before {@link #applyValue(Object, Object, Object)} or {@link #save(Object, Object)}
	 * calls, to check that a property value can in fact be set.
	 *
	 * @param owner           entity
	 * @param propertyValue   to validate
	 * @param errors          where validation errors should be added to
	 * @param validationHints optional hints for validation (eg. validation groups)
	 * @see org.springframework.validation.SmartValidator
	 */
	@Deprecated
	default void validate( T owner, U propertyValue, Errors errors, Object... validationHints ) {
	}

	/**
	 * Apply the current value of the property to the owning entity.
	 * Usually relevant for properties that modify the entity itself,
	 * but do not persist any data outside of the entity.
	 * <p/>
	 * Applying the property value has different semantics than {@link #save(Object, Object)}.
	 * The latter is meant for storing the actual property value whereas applying the value
	 * implies that the final store will happen transitively through the context (entity)
	 * to which the property value is applied.
	 * <p/>
	 * Applying a property value usually happens immediately after successful validation,
	 * before the next property is validated.
	 * <p/>
	 * A single controller implements usually either {@code applyValue(Object, Object)} or {@link #save(Object, Object)}.
	 *
	 * @param owner            entity
	 * @param oldPropertyValue the original value that (can be {@code null} if unknown)
	 * @param newPropertyValue to set
	 * @return true if value has been set
	 * @see #save(Object, Object)
	 */
	@Deprecated
	default boolean applyValue( T owner, U oldPropertyValue, U newPropertyValue ) {
		return false;
	}

	default boolean applyValue( EntityPropertyBindingContext<T, ?> context, EntityPropertyValue<U> propertyValue ) {
		return false;
	}

	/**
	 * Save the property value for the owning entity.
	 * Usually relevant for properties not present on the entity.
	 *
	 * @param owner         entity
	 * @param propertyValue to save
	 * @return true if the property value has been saved
	 */
	@Deprecated
	default boolean save( T owner, U propertyValue ) {
		return false;
	}

	default boolean save( EntityPropertyBindingContext<T, ?> context, EntityPropertyValue<U> propertyValue ) {
		return false;
	}

	/**
	 * The order in which this controller should be applied.
	 * Defaults to {@link #AFTER_ENTITY} meaning the controller methods will be executed after
	 * the equivalent entity methods.
	 *
	 * @return order
	 */
	@Override
	default int getOrder() {
		return AFTER_ENTITY;
	}
}
