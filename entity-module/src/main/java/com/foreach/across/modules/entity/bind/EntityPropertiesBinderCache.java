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
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import lombok.Builder;
import lombok.NonNull;
import org.springframework.util.Assert;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Arne Vandamme
 * @since 4.0.0
 */
public class EntityPropertiesBinderCache
{
	private final Map<EntityPropertyController, Map<EntityPropertyBindingContext, Object>> cachedPropertyValues = new IdentityHashMap<>();

	@NonNull
	private final EntityPropertyRegistry propertyRegistry;

	private final Map<Object, EntityPropertiesBinder> binders;
	private final Set<EntityPropertyBindingContext> bindingContexts;

	@Builder
	private EntityPropertiesBinderCache( EntityPropertyRegistry propertyRegistry, Collection<Object> entities ) {
		this.propertyRegistry = propertyRegistry;

		binders = entities.stream()
		                  .collect( Collectors.toMap( Function.identity(), this::createPropertiesBinder ) );
		Assert.isTrue( binders.size() == entities.size(),
		               "Unable to use EntityPropertiesBinderCache for entities - equals/hashcode mismatch? " +
				               "The number of binding contexts did not match the number of original entities." );

		bindingContexts = binders.values().stream().map( EntityPropertiesBinder::asBindingContext ).collect( Collectors.toSet() );
	}

	private EntityPropertiesBinder createPropertiesBinder( Object entity ) {
		EntityPropertiesBinder binder = new EntityPropertiesBinder( propertyRegistry );
		binder.setEntity( entity );
		binder.setCache( this );
		return binder;
	}

	Object retrieveCachedValue( EntityPropertyController controller, EntityPropertyBindingContext entityPropertyBindingContext ) {
		return cachedPropertyValues.computeIfAbsent( controller, c -> c.fetchValues( bindingContexts ) )
		                           .get( entityPropertyBindingContext );
	}

	/**
	 * Get the binding context for an entity. A binding context will be returned
	 * if the entity was registered in this cache.
	 *
	 * @param entity to fetch the context for
	 * @return binding context
	 */
	public Optional<EntityPropertiesBinder> getPropertiesBinder( @NonNull Object entity ) {
		return Optional.ofNullable( binders.get( entity ) );
	}

	/**
	 * Remove a single binding context from the cache. Afterwards it can no longer be used
	 * and it will also not be included anymore for bulk fetching of values.
	 *
	 * @param entity remove the binding context for
	 */
	public void remove( @NonNull Object entity ) {
		binders.remove( entity );
	}

	/**
	 * Clear the entire cache, both the bulk fetched values
	 * and the separate binding contexts.
	 */
	public void clear() {
		binders.clear();
	}
}
