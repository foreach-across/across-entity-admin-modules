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

import lombok.NonNull;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Can be implemented by a {@link EntityPropertyBindingContext} to query
 * for a property binding context and optionally load it if not available.
 *
 * @author Arne Vandamme
 * @since 4.0.0
 */
public interface EntityPropertyBindingContextResolver extends EntityPropertyBindingContext
{
	/**
	 * Resolve a {@link EntityPropertyBindingContext} for a specific child property.
	 * Optionally specify if the context should be created if it can be.
	 * When {@code false} only a pre-existing binding context would be returned.
	 * Useful for bulk fetching scenarios where an external process might create the
	 * binding context and then eagerly load the values.
	 *
	 * @param propertyDescriptor representing the property
	 * @param createIfPossible   true if a binding context should be created if it can be, false if
	 * @return binding context for the property, {@code null} if not found
	 */
	EntityPropertyBindingContext resolvePropertyBindingContext( @NonNull EntityPropertyDescriptor propertyDescriptor, boolean createIfPossible );

	/**
	 * Register a property binding context for the specific child property,
	 * with the initial property value.
	 *
	 * @param propertyDescriptor representing the property
	 * @param propertyValue      current property value
	 * @return binding context for the property
	 */
	EntityPropertyBindingContext registerPropertyBindingContext( @NonNull EntityPropertyDescriptor propertyDescriptor, Object propertyValue );

	/**
	 * Resolves the property binding contexts for a given property descriptor and a collection of
	 * entity binding contexts, and will do so in the most efficient way possible.
	 * Will use bulk fetching if the property controller claims it is optimized for that.
	 *
	 * @param parents            binding context list
	 * @param propertyDescriptor for the property
	 * @return map of original binding context (value) by resolved property binding context (key)
	 */
	static Map<EntityPropertyBindingContext, EntityPropertyBindingContext> resolvePropertyBindingContext(
			Collection<EntityPropertyBindingContext> parents, EntityPropertyDescriptor propertyDescriptor ) {
		boolean shouldUseBulkValueFetching = propertyDescriptor.getController().isOptimizedForBulkValueFetching();

		IdentityHashMap<EntityPropertyBindingContext, EntityPropertyBindingContext> originalByChildContext = new IdentityHashMap<>( parents.size() );

		if ( shouldUseBulkValueFetching ) {
			IdentityHashMap<EntityPropertyBindingContextResolver, Object> valuesToBulkFetch = new IdentityHashMap<>();

			for ( EntityPropertyBindingContext parent : parents ) {
				if ( parent instanceof EntityPropertyBindingContextResolver ) {
					EntityPropertyBindingContextResolver resolver = (EntityPropertyBindingContextResolver) parent;

					EntityPropertyBindingContext propertyBindingContext = resolver.resolvePropertyBindingContext( propertyDescriptor, false );
					if ( propertyBindingContext != null ) {
						originalByChildContext.put( propertyBindingContext, parent );
					}
					else {
						valuesToBulkFetch.put( resolver, null );
					}
				}
				else {
					originalByChildContext.put( resolvePropertyBindingContext( parent, propertyDescriptor ), parent );
				}
			}

			Map<?, ?> fetchedValues = propertyDescriptor.getController().fetchValues( valuesToBulkFetch.keySet() );
			valuesToBulkFetch.forEach( ( resolver, ignore ) -> {
				Object propertyValue = fetchedValues.get( resolver );
				originalByChildContext.put( resolver.registerPropertyBindingContext( propertyDescriptor, propertyValue ), resolver );
			} );
		}
		else {
			parents.forEach( parent -> originalByChildContext.put( resolvePropertyBindingContext( parent, propertyDescriptor ), parent ) );
		}

		return originalByChildContext;
	}

	/**
	 * Resolve a the binding context for a property of a parent binding context.
	 *
	 * @param parent             binding context
	 * @param propertyDescriptor for which the binding context should be resolved
	 * @return binding context for the property
	 */
	static EntityPropertyBindingContext resolvePropertyBindingContext( EntityPropertyBindingContext parent, EntityPropertyDescriptor propertyDescriptor ) {
		return parent.resolvePropertyBindingContext( propertyDescriptor );
	}
}
