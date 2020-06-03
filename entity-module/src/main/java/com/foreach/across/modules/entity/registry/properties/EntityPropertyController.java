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

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Generic controller for a single property on an entity.
 * Allows reading and writing a property value, as well as validating a value.
 * <p/>
 * The difference between a {@link #save(EntityPropertyBindingContext, EntityPropertyValue)} and
 * a {@link #applyValue(EntityPropertyBindingContext, EntityPropertyValue)}* method, i
 * s that applying the value can be done several times during a binding process, and is usually
 * done on the entity itself before the entity itself is saved.
 * The {@link #save(EntityPropertyBindingContext, EntityPropertyValue)} method is more
 * useful for properties that are not actually present on the entity instance itself.
 * <p/>
 * The entity the property belongs to is represented in the {@link EntityPropertyBindingContext},
 * which holds the original entity and optionally a different target to which the changes should
 * be applied. The value of {@link EntityPropertyBindingContext#isReadonly()} can be used to
 * determine optimizations in case of an expected readonly situation.
 * <p/>
 * A property value is represented as {@link EntityPropertyValue}. Note that there is no guarantee
 * that {@link EntityPropertyValue#getOldValue()} actually represents the previous value. Especially
 * when working with custom object types it is quite possible that updates have been applied to the
 * same instance, and old value would be the same as the new value. Controllers should be aware of
 * the actual behaviour and should know if they can rely on the contents of the old value.
 * To determine if a property should be deleted, the value of {@link EntityPropertyValue#isDeleted()}
 * can be used.
 *
 * @param <T> type of the property that is being managed
 * @author Arne Vandamme
 * @see ConfigurableEntityPropertyController
 * @since 3.2.0
 */
public interface EntityPropertyController<T> extends Ordered
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
	 * Fetches the value of the property for a collection of owning entities.
	 * Default implementation does a sequential {@link #fetchValue(EntityPropertyBindingContext)} but custom
	 * implementations should provide a more optimal fetching approach.
	 *
	 * @param bindingContexts collection of binding contexts
	 * @return property values mapped by binding context
	 * @see #fetchValue(EntityPropertyBindingContext)
	 * @see #isOptimizedForBulkValueFetching()
	 */
	default Map<EntityPropertyBindingContext, Object> fetchValues( Collection<EntityPropertyBindingContext> bindingContexts ) {
		Map<EntityPropertyBindingContext, Object> values = new IdentityHashMap<>( bindingContexts.size() );
		bindingContexts.forEach( bindingContext -> values.put( bindingContext, fetchValue( bindingContext ) ) );
		return values;
	}

	/**
	 * Fetches the current value of the property for the owning entity.
	 * Depending on the type of property this might be a simple getter being called,
	 * or something like a database retrieval happening.
	 *
	 * @param context binding context
	 * @return property value
	 */
	default Object fetchValue( EntityPropertyBindingContext context ) {
		return null;
	}

	/**
	 * Check if this control has optimized support for bulk value fetching.
	 * If {@code true} calling {@link #fetchValues(Collection)} will usually execute a
	 * custom implementation. Can be used to determine eager loading of property values.
	 *
	 * @return true if this controller has optimal bulk value fetching
	 */
	default boolean isOptimizedForBulkValueFetching() {
		return false;
	}

	/**
	 * Create a DTO object for the property value. The default implementation returns the same instance.
	 * This method can be used to create detached transfer objects that should be updated instead of
	 * the existing instance. When available this should improve the reliability of {@link EntityPropertyValue#getOldValue()}
	 * and {@link EntityPropertyValue#getNewValue()} as the former will hold the original property value,
	 * and the latter the DTO.
	 *
	 * @param context binding context
	 * @param value   property value to create a DTO for
	 * @return property value DTO
	 */
	default Object createDto( EntityPropertyBindingContext context, Object value ) {
		return value != null ? value : createValue( context );
	}

	/**
	 * Create a new value for the property on the owning entity.
	 * This *only* creates a value instance but does not necessary assign the value to that property.
	 * Useful for initializing complex object values.
	 *
	 * @param context binding context
	 * @return valid property value
	 */
	default Object createValue( EntityPropertyBindingContext context ) {
		return null;
	}

	/**
	 * Validate a property value in the given binding context.
	 * Validation errors should be registered on the {@link Errors} argument.
	 *
	 * @param context         binding context
	 * @param propertyValue   to validate
	 * @param errors          where validation errors should be added to
	 * @param validationHints optional hints for validation (eg. validation groups)
	 * @see EntityPropertyValidator
	 */
	default void validate( EntityPropertyBindingContext context, EntityPropertyValue<T> propertyValue, Errors errors, Object... validationHints ) {
	}

	/**
	 * Apply the current value of the property to the owning entity.
	 * Usually relevant for properties that modify the entity itself,
	 * but do not persist any data outside of the entity.
	 * <p/>
	 * Applying the property value has different semantics than {@link #save(EntityPropertyBindingContext, EntityPropertyValue)}.
	 * The latter is meant for storing the actual property value whereas applying the value
	 * implies that the final store will happen transitively through the context (entity)
	 * to which the property value is applied.
	 * <p/>
	 * Applying a property value might happen several times during a binding process, so operations should be idempotent.
	 * Applying values usually happens right after regular data-binding but before validation, whereas
	 * {@link #save(EntityPropertyBindingContext, EntityPropertyValue)} is usually only called a single time after
	 * successful validation.
	 * <p/>
	 * A single controller implements usually either {@code applyValue(EntityPropertyBindingContext, EntityPropertyValue)}
	 * or {@link #save(EntityPropertyBindingContext, EntityPropertyValue)}.
	 *
	 * @param context       binding context
	 * @param propertyValue value context holder
	 * @return true if value has been set
	 * @see #save(EntityPropertyBindingContext, EntityPropertyValue)
	 */
	default boolean applyValue( EntityPropertyBindingContext context, EntityPropertyValue<T> propertyValue ) {
		return false;
	}

	/**
	 * Save the property value for the owning entity.
	 * Usually relevant for properties not present on the entity.
	 * If the property is to be deleted, {@link EntityPropertyValue#isDeleted()} will be {@code true},
	 * and the new value usually {@code null}.
	 *
	 * @param context       binding context
	 * @param propertyValue to save
	 * @return true if the property value has been saved
	 */
	default boolean save( EntityPropertyBindingContext context, EntityPropertyValue<T> propertyValue ) {
		return false;
	}

	/**
	 * The order in which this controller should be applied.
	 * Defaults to {@link #AFTER_ENTITY} meaning the controller methods will be executed after
	 * the equivalent entity methods. This is appropriate for custom properties that do not
	 * operate on the entity itself but require the entity to have been processed fully.
	 *
	 * @return order
	 */
	@Override
	default int getOrder() {
		return AFTER_ENTITY;
	}
}
