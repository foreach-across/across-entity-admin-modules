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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

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
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class EntityPropertyBindingContext<T, U>
{
	private final T entity;
	private final U target;
	private final EntityPropertyBindingContext<?, ?> parent;
	private EntityPropertyController controller;

	private final Map<String, EntityPropertyBindingContext<?, ?>> childContexts = new LinkedHashMap<>();

	@SuppressWarnings("unchecked")
	@Deprecated
	public EntityPropertyBindingContext( T entity ) {
		this( entity, (U) entity );
	}

	@Deprecated
	public EntityPropertyBindingContext( T entity, U target ) {
		this.entity = entity;
		this.target = target;
		this.parent = null;
	}

	public boolean hasParent() {
		return parent != null;
	}

	public boolean isReadonly() {
		return false;
	}

	public EntityPropertyValue toPropertyValue() {
		return new EntityPropertyValue( entity, target, target == null && entity != null );
	}

	@SuppressWarnings("unchecked")
	public EntityPropertyBindingContext retrieveNamedChildContext(
			@NonNull String name,
			@NonNull Function<EntityPropertyBindingContext, EntityPropertyBindingContext> factory ) {
		return childContexts.computeIfAbsent( name, contextName -> factory.apply( this ).withParent( this ) );
	}

	@Deprecated
	public EntityPropertyBindingContext<T, U> withParent( @NonNull EntityPropertyBindingContext<?, ?> parentContext ) {
		return new EntityPropertyBindingContext<>( entity, target, parentContext, controller );
	}

	@Deprecated
	public EntityPropertyBindingContext<T, U> withController( @NonNull EntityPropertyController controller ) {
		return new EntityPropertyBindingContext<>( entity, target, parent, controller );
	}

	/**
	 * Create a simple binding context for an entity.
	 * Both the target and the entity will refer to the same instance.
	 *
	 * @param entity for the context
	 * @param <T>    entity type
	 * @return binding context
	 */
	public static <T> EntityPropertyBindingContext<T, T> of( T entity ) {
		return new EntityPropertyBindingContext<>( entity );
	}
}
