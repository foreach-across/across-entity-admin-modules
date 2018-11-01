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

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

/**
 * Registry containing a collection of property information.  Usually for a particular entity type,
 * although this is not required.
 *
 * @see DefaultEntityPropertyRegistry
 * @see EntityPropertyRegistryProvider
 */
public interface EntityPropertyRegistry
{
	/**
	 * Suffix indicator that a property descriptor represents the specific member of a
	 * the collection property (having the same name but without the indexer).
	 */
	String INDEXER = "[]";

	/**
	 * Suffix indicator that a property descriptor represents the key of a map property
	 * (having the same name but without this suffix).
	 */
	String MAP_KEY = "[key]";

	/**
	 * Suffix indicator that a property descriptor represents the value of a map property
	 * (having the same name but without this suffix).
	 */
	String MAP_VALUE = "[value]";

	/**
	 * Name of the label property.
	 *
	 * @see com.foreach.across.modules.entity.registry.properties.registrars.LabelPropertiesRegistrar
	 */
	String LABEL = "#label";

	/**
	 * @param propertyName Name of the property.
	 * @return True if a property with that name is registered.
	 */
	boolean contains( String propertyName );

	EntityPropertyDescriptor getProperty( String propertyName );

	List<EntityPropertyDescriptor> getProperties();

	List<EntityPropertyDescriptor> getProperties( Predicate<EntityPropertyDescriptor> predicate );

	List<EntityPropertyDescriptor> select( EntityPropertySelector selector );

	Collection<EntityPropertyDescriptor> getRegisteredDescriptors();

	Comparator<EntityPropertyDescriptor> getDefaultOrder();

	Predicate<EntityPropertyDescriptor> getDefaultFilter();

	/**
	 * Check if a property represents an indexer property.
	 *
	 * @param descriptor property descriptor
	 * @return true if indexer
	 */
	@Deprecated
	static boolean isIndexerPropertyDescriptor( EntityPropertyDescriptor descriptor ) {
		return StringUtils.endsWith( descriptor.getName(), INDEXER );
	}

	/**
	 * Check if a property represents a map key property.
	 *
	 * @param descriptor property descriptor
	 * @return true if map key
	 */
	@Deprecated
	static boolean isMapKeyPropertyDescriptor( EntityPropertyDescriptor descriptor ) {
		return StringUtils.endsWith( descriptor.getName(), MAP_KEY );
	}

	/**
	 * Check if a property represents a map value property.
	 *
	 * @param descriptor property descriptor
	 * @return true if map value
	 */
	@Deprecated
	static boolean isMapValuePropertyDescriptor( EntityPropertyDescriptor descriptor ) {
		return StringUtils.endsWith( descriptor.getName(), MAP_VALUE );
	}

	/**
	 * Check if a property represents a member type: either an indexer, map key or map value.
	 *
	 * @param descriptor property descriptor
	 * @return true if member property descriptor
	 */
	@Deprecated
	static boolean isMemberPropertyDescriptor( EntityPropertyDescriptor descriptor ) {
		return isIndexerPropertyDescriptor( descriptor ) || isMapKeyPropertyDescriptor( descriptor ) || isMapValuePropertyDescriptor( descriptor );
	}
}
