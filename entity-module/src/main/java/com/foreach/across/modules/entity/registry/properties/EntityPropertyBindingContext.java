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

import lombok.*;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Holds the binding context information for an entity.
 * Usually contains at least the owning entity (often the original entity), and
 * the target on which it should be bound (often a DTO of the original entity).
 *
 * @author Arne Vandamme
 * @see EntityPropertyController
 * @see com.foreach.across.modules.entity.bind.EntityPropertiesBinder
 * @since 3.2.0
 */
@EqualsAndHashCode(exclude = "childContexts")
@Getter
@Builder
@AllArgsConstructor
@Accessors(chain = true)
public final class EntityPropertyBindingContext
{
	private final Object entity;
	private final Object target;

	/**
	 * Indicates if this binding context is meant solely for reading.
	 * This can help property controllers to apply optimizations.
	 */
	private final boolean readonly;

	/**
	 * Represents the optional parent {@link EntityPropertyBindingContext}.
	 * See also {@link #getParent()} and {@link #getOrCreateChildContext(String, BiConsumer)}.
	 */
	private final EntityPropertyBindingContext parent;

	/**
	 * If this binding context represents an intermediate property value,
	 * the controller for the property can be attached. This is often the case
	 * for child contexts which might then automatically be handled by
	 * an {@link com.foreach.across.modules.entity.bind.EntityPropertiesBinder}.
	 */
	private final EntityPropertyController controller;

	/**
	 * List of child contexts registered. A child context represents an intermediate property value
	 * for nested property descriptors and often has a value for {@link #getController()}.
	 * The only way to create child contexts is by using {@link #getOrCreateChildContext(String, BiConsumer)}.
	 */
	private final Map<String, EntityPropertyBindingContext> childContexts = new LinkedHashMap<>();

	/**
	 * Resolves a {@link EntityPropertyBindingContext} based on a given name.
	 */
	@Setter
	private ChildContextResolver childContextResolver;

	/**
	 * Get the original entity value.
	 *
	 * @return original entity cast to specific type
	 */
	@SuppressWarnings("unchecked")
	public <U> U getEntity() {
		return (U) entity;
	}

	/**
	 * Get the target to which updates should be applied.
	 * Usually only different from {@link #getEntity()} in a non-readonly context.
	 *
	 * @return target cast to specific type
	 */
	@SuppressWarnings("unchecked")
	public <U> U getTarget() {
		return (U) target;
	}

	/**
	 * Convert this binding context to an equivalent {@link EntityPropertyValue}.
	 * The {@link #getEntity()} will be considered the {@link EntityPropertyValue#getOldValue()},
	 * whereas {@link #getTarget()} will be the {@link EntityPropertyValue#getNewValue()}.
	 * In case this is not a readonly context and the {@code target} is {@code null}, but the
	 * {@code entity} is not, the property value will be flagged as {@code deleted}.
	 *
	 * @return entity property value
	 */
	public EntityPropertyValue toPropertyValue() {
		return new EntityPropertyValue<>( entity, target, !readonly && ( target == null && entity != null ) );
	}

	/**
	 * @return the collection of child contexts (immutable)
	 */
	public Map<String, EntityPropertyBindingContext> getChildContexts() {
		return Collections.unmodifiableMap( childContexts );
	}

	/**
	 * Retrieve the child {@link EntityPropertyBindingContext} with the given name.
	 * If none is registered, create and register it.
	 * This is the only way to register a child context.
	 * <p/>
	 * The second parameter is a {@link BiConsumer} to customize the {@link EntityPropertyBindingContextBuilder}
	 * that should be used for the child context. The first argument is the current context. The child context
	 * builder will be pre-initialized with the readonly flag of the parent.
	 *
	 * @param name     of the child context
	 * @param consumer to set entity & target on the child context builder
	 * @return child context
	 */
	@SuppressWarnings("unchecked")
	public EntityPropertyBindingContext getOrCreateChildContext(
			@NonNull String name, @NonNull BiConsumer<EntityPropertyBindingContext, EntityPropertyBindingContextBuilder> consumer
	) {
		if ( childContexts.containsKey( name ) ) {
			return childContexts.get( name );
		}
		if ( childContextResolver != null ) {
			EntityPropertyBindingContext resolvedContext = childContextResolver.resolve( name );
			if ( resolvedContext != null ) {
				return resolvedContext;
			}
		}
		return childContexts.computeIfAbsent( name, contextName -> {
			EntityPropertyBindingContextBuilder builder = builder().parent( this ).readonly( isReadonly() );
			consumer.accept( this, builder );
			return builder.build();
		} );
	}

	/**
	 * Check if a child context with the given name is present.
	 *
	 * @param name of the child context
	 * @return {@code true} if child context is registered
	 */
	public boolean hasChildContext( @NonNull String name ) {
		return childContexts.containsKey( name );
	}

	/**
	 * Remove the child context with the given name.
	 *
	 * @param name of the child context
	 * @return child context removed or {@code null} if none
	 */
	public EntityPropertyBindingContext removeChildContext( @NonNull String name ) {
		return childContexts.remove( name );
	}

	/**
	 * Create a readonly binding context for an entity.
	 * Both the target and the entity will refer to the same instance, and {@link #isReadonly()} will return {@code true}.
	 *
	 * @param entity for the context
	 * @return binding context
	 */
	public static EntityPropertyBindingContext forReading( Object entity ) {
		return builder().entity( entity ).target( entity ).readonly( true ).build();
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
	public static EntityPropertyBindingContext forUpdating( Object entity, Object target ) {
		return builder().entity( entity ).target( target ).readonly( false ).build();
	}

	@SuppressWarnings("unused")
	public static class EntityPropertyBindingContextBuilder
	{
		private EntityPropertyBindingContext parent;

		private EntityPropertyBindingContextBuilder parent( EntityPropertyBindingContext parent ) {
			this.parent = parent;
			return this;
		}
	}
}
