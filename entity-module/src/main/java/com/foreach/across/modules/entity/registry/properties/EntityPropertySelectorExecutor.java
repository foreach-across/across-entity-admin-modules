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

import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.TypeDescriptor;

import java.util.*;
import java.util.function.Predicate;

/**
 * @author Arne Vandamme
 */
public class EntityPropertySelectorExecutor
{
	private EntityPropertyRegistry current;
	private EntityPropertyRegistryProvider propertyRegistries;

	private static final Predicate<EntityPropertyDescriptor> ALL = entityPropertyDescriptor -> true;
	private static final Predicate<EntityPropertyDescriptor> WRITABLE = EntityPropertyDescriptor::isWritable;
	private static final Predicate<EntityPropertyDescriptor> READABLE = EntityPropertyDescriptor::isReadable;

	public EntityPropertySelectorExecutor( EntityPropertyRegistry current,
	                                       EntityPropertyRegistryProvider propertyRegistries ) {
		this.current = current;
		this.propertyRegistries = propertyRegistries;
	}

	public List<EntityPropertyDescriptor> select( EntityPropertySelector selector ) {
		LinkedHashSet<EntityPropertyDescriptor> properties = new LinkedHashSet<>();
		Set<String> excluded = new HashSet<>();

		Predicate<EntityPropertyDescriptor> predicate = selector.hasPredicate() ? selector.getPredicate() : ALL;

		for ( Map.Entry<String, Boolean> candidate : selector.propertiesToSelect().entrySet() ) {
			String propertyName = candidate.getKey();
			boolean include = candidate.getValue();

			if ( EntityPropertySelector.WRITABLE.equals( propertyName ) ) {
				current.getProperties()
				       .stream()
				       .filter( include ? WRITABLE : WRITABLE.negate() )
				       .forEach( properties::add );
			}
			else if ( EntityPropertySelector.READABLE.equals( propertyName ) ) {
				current.getProperties()
				       .stream()
				       .filter( include ? READABLE : READABLE.negate() )
				       .forEach( properties::add );
			}
			else if ( include ) {
				if ( EntityPropertySelector.ALL.equals( propertyName ) ) {
					properties.addAll( current.getProperties() );
				}
				else if ( EntityPropertySelector.ALL_REGISTERED.equals( propertyName ) ) {
					properties.addAll( current.getRegisteredDescriptors() );
				}
				else if ( propertyName.endsWith( "." + EntityPropertySelector.ALL ) ) {
					properties.addAll(
							selectNestedProperties(
									StringUtils.removeEnd( propertyName, "." + EntityPropertySelector.ALL ),
									EntityPropertySelector.ALL
							)
					);
				}
				else if ( propertyName.endsWith( "." + EntityPropertySelector.ALL_REGISTERED ) ) {
					properties.addAll(
							selectNestedProperties(
									StringUtils.removeEnd( propertyName, "." + EntityPropertySelector.ALL_REGISTERED ),
									EntityPropertySelector.ALL_REGISTERED
							)
					);
				}
				else {
					properties.add( current.getProperty( propertyName ) );
				}
			}
			else {
				excluded.add( propertyName );
			}
		}

		properties.removeIf( candidate -> excluded.contains( candidate.getName() ) || !predicate.test( candidate ) );

		return new ArrayList<>( properties );
	}

	private List<EntityPropertyDescriptor> selectNestedProperties( String propertyName, String selectorString ) {
		EntityPropertyDescriptor descriptor = current.getProperty( propertyName );

		// todo might cause problems with specific types implementing a collection
		// problem is that 'get' creates the registry
		Class<?> propertyType = descriptor.getPropertyType();

		TypeDescriptor typeDescriptor = descriptor.getPropertyTypeDescriptor();
		if ( typeDescriptor != null && ( typeDescriptor.isCollection() || typeDescriptor.isArray() ) ) {
			val elementType = typeDescriptor.getElementTypeDescriptor();
			if ( elementType != null ) {
				propertyType = elementType.getObjectType();
			}
		}

		if ( propertyType != null ) {
			EntityPropertyRegistry registry = propertyRegistries.get( propertyType );

			List<EntityPropertyDescriptor> subProperties = registry.select(
					new EntityPropertySelector( selectorString )
			);
			List<EntityPropertyDescriptor> properties = new ArrayList<>( subProperties.size() );

			for ( EntityPropertyDescriptor subProperty : subProperties ) {
				properties.add( current.getProperty( propertyName + "." + subProperty.getName() ) );
			}

			return properties;
		}

		return Collections.emptyList();
	}
}
