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
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Predicate;

/**
 * @author Arne Vandamme
 */
public abstract class EntityPropertyRegistrySupport implements MutableEntityPropertyRegistry
{
	private final Map<String, EntityPropertyDescriptor> descriptorMap = new HashMap<>();
	private final EntityPropertyComparators.Ordered propertyOrder = new EntityPropertyComparators.Ordered();

	private final EntityPropertyRegistryProvider registryProvider;
	private final EntityPropertySelectorExecutor selectorExecutor;

	private Predicate<EntityPropertyDescriptor> defaultFilter;
	private Comparator<EntityPropertyDescriptor> defaultOrder = null;

	protected EntityPropertyRegistrySupport( EntityPropertyRegistryProvider registryProvider ) {
		this.registryProvider = registryProvider;
		selectorExecutor = new EntityPropertySelectorExecutor( this, registryProvider );
	}

	protected EntityPropertyRegistryProvider getRegistryProvider() {
		return registryProvider;
	}

	@Override
	public Predicate<EntityPropertyDescriptor> getDefaultFilter() {
		return defaultFilter;
	}

	@Override
	public void setDefaultFilter( Predicate<EntityPropertyDescriptor> defaultFilter ) {
		this.defaultFilter = defaultFilter;
	}

	@Override
	public void setDefaultOrder( String... propertyNames ) {
		setDefaultOrder( new EntityPropertyComparators.Ordered( propertyNames ) );
	}

	@Override
	public void setDefaultOrder( Comparator<EntityPropertyDescriptor> defaultOrder ) {
		this.defaultOrder = defaultOrder;
	}

	@Override
	public Comparator<EntityPropertyDescriptor> getDefaultOrder() {
		return defaultOrder != null ? defaultOrder.thenComparing( propertyOrder ) : propertyOrder;
	}

	@Override
	public List<EntityPropertyDescriptor> getProperties() {
		return getProperties( getDefaultFilter() );
	}

	@Override
	public List<EntityPropertyDescriptor> getProperties( Predicate<EntityPropertyDescriptor> predicate ) {
		return fetchProperties( predicate, getDefaultOrder() );
	}

	public void setPropertyOrder( @NonNull String propertyName, int order ) {
		propertyOrder.put( propertyName, order );
	}

	private List<EntityPropertyDescriptor> fetchProperties( Predicate<EntityPropertyDescriptor> predicate,
	                                                        Comparator<EntityPropertyDescriptor> comparator ) {
		Predicate<EntityPropertyDescriptor> filterToUse = predicate != null ? predicate : getDefaultFilter();

		if ( filterToUse == null ) {
			filterToUse = entityPropertyDescriptor -> true;
		}

		List<EntityPropertyDescriptor> filtered = new ArrayList<>();
		for ( EntityPropertyDescriptor candidate : getRegisteredDescriptors() ) {
			if ( !isNestedProperty( candidate.getName() ) && filterToUse.test( candidate ) ) {
				filtered.add( candidate );
			}
		}

		if ( comparator != null ) {
			filtered.sort( comparator );
		}

		return filtered;
	}

	@Override
	public List<EntityPropertyDescriptor> select( EntityPropertySelector selector ) {
		return selectorExecutor.select( selector );
	}

	private boolean isNestedProperty( String name ) {
		return StringUtils.contains( name, "." );
	}

	/**
	 * @return The unfiltered list of all registered EntityPropertyDescriptors.
	 */
	@Override
	public Collection<EntityPropertyDescriptor> getRegisteredDescriptors() {
		return descriptorMap.values();
	}

	@Override
	public MutableEntityPropertyDescriptor getProperty( String propertyName ) {
		EntityPropertyDescriptor descriptor = descriptorMap.get( propertyName );

		if ( descriptor == null && StringUtils.length( propertyName ) > 1 && Character.isUpperCase( propertyName.charAt( 1 ) ) ) {
			char chars[] = propertyName.toCharArray();
			chars[0] = Character.toUpperCase( chars[0] );
			descriptor = descriptorMap.get( new String( chars ) );
		}

		return descriptor instanceof MutableEntityPropertyDescriptor ? (MutableEntityPropertyDescriptor) descriptor : null;
	}

	@Override
	public boolean contains( String propertyName ) {
		return getProperty( propertyName ) != null;
	}

	@Override
	public void register( MutableEntityPropertyDescriptor descriptor ) {
		if ( descriptor.getPropertyRegistry() != null && descriptor.getPropertyRegistry() != this ) {
			throw new IllegalArgumentException( "Descriptor already has a different property registry attached." );
		}

		descriptor.setPropertyRegistry( this );
		descriptorMap.put( descriptor.getName(), descriptor );
	}
}
