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

import com.foreach.across.modules.entity.registry.properties.binding.SimpleEntityPropertyBindingContext;
import lombok.NonNull;

/**
 * Represents the binding context information for an entity.
 * Usually contains at least the owning entity (often the original entity), and
 * the target on which it should be bound (often a DTO of the original entity).
 *
 * @author Arne Vandamme
 * @see EntityPropertyController
 * @see SimpleEntityPropertyBindingContext
 * @see com.foreach.across.modules.entity.registry.properties.binding.ChildEntityPropertyBindingContext
 * @since 3.2.0
 */
public interface EntityPropertyBindingContext
{
	/**
	 * Get the original entity value.
	 *
	 * @return original entity cast to specific type
	 */
	<U> U getEntity();

	/**
	 * Get the target to which updates should be applied.
	 * Usually only different from {@link #getEntity()} in a non-readonly context.
	 *
	 * @return target cast to specific type
	 */
	<U> U getTarget();

	/**
	 * Convert this binding context to an equivalent {@link EntityPropertyValue}.
	 * The {@link #getEntity()} will be considered the {@link EntityPropertyValue#getOldValue()},
	 * whereas {@link #getTarget()} will be the {@link EntityPropertyValue#getNewValue()}.
	 * In case this is not a readonly context and the {@code target} is {@code null}, but the
	 * {@code entity} is not, the property value will be flagged as {@code deleted}.
	 *
	 * @return entity property value
	 */
	default EntityPropertyValue toPropertyValue() {
		Object entity = getEntity();
		Object target = getTarget();
		return new EntityPropertyValue<>( entity, target, !isReadonly() && ( target == null && entity != null ) );
	}

	/**
	 * Indicates if this binding context is meant solely for reading.
	 * In this case {@link #getTarget()} will usually return the same as {@link #getEntity()}.
	 *
	 * @return true if readonly
	 */
	boolean isReadonly();

	/**
	 * Resolve the property value for a given property.
	 *
	 * @param propertyDescriptor representing the property
	 * @param <U>                type of the property
	 * @return property value, {@code null} if not found
	 */
	default <U> EntityPropertyValue<U> resolvePropertyValue( @NonNull EntityPropertyDescriptor propertyDescriptor ) {
		EntityPropertyBindingContext propertyBindingContext = resolvePropertyBindingContext( propertyDescriptor );
		return propertyBindingContext != null ? propertyBindingContext.toPropertyValue() : null;
	}

	/**
	 * Resolve a {@link EntityPropertyBindingContext} for a specific child property.
	 *
	 * @param propertyDescriptor representing the property
	 * @return binding context for the property, {@code null} if not found
	 */
	EntityPropertyBindingContext resolvePropertyBindingContext( @NonNull EntityPropertyDescriptor propertyDescriptor );

	/**
	 * Create a readonly binding context for an entity.
	 * Both the target and the entity will refer to the same instance, and {@link #isReadonly()} will return {@code true}.
	 *
	 * @param entity for the context
	 * @return binding context
	 */
	static EntityPropertyBindingContext forReading( Object entity ) {
		return SimpleEntityPropertyBindingContext.builder().entity( entity ).target( entity ).readonly( true ).build();
	}

	/**
	 * Create a binding context for updating an entity, where {@code entity} represents the current value,
	 * and {@code target} the target (usually a DTO) to which the updates should be applied.
	 * Property {@link #isReadonly()} will return {@code false}.
	 *
	 * @param entity for the context
	 * @param target for applying the updates to
	 * @return binding context
	 */
	static EntityPropertyBindingContext forUpdating( Object entity, Object target ) {
		return SimpleEntityPropertyBindingContext.builder().entity( entity ).target( target ).readonly( false ).build();
	}
}
