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

import lombok.EqualsAndHashCode;
import lombok.Getter;

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
@EqualsAndHashCode
@Getter
public class EntityPropertyBindingContext<T, U>
{
	private final T entity;
	private final U target;

	@SuppressWarnings("unchecked")
	public EntityPropertyBindingContext( T entity ) {
		this( entity, (U) entity );
	}

	public EntityPropertyBindingContext( T entity, U target ) {
		this.entity = entity;
		this.target = target;
	}
}
