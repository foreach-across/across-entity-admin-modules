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

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Registry that allows overriding properties from a parent registry.
 * Any properties registered directly in this registry will shadow the ones from the parent registry.
 *
 * @author Arne Vandamme
 * @see DefaultEntityPropertyRegistry
 */
public class MergingEntityPropertyRegistry extends EntityPropertyRegistrySupport
{
	private final EntityPropertyRegistry parent;
	private final EntityPropertyDescriptorFactory descriptorFactory;

	public MergingEntityPropertyRegistry( EntityPropertyRegistry parent,
	                                      EntityPropertyRegistryProvider registryProvider,
	                                      EntityPropertyDescriptorFactory descriptorFactory ) {
		super( registryProvider );
		this.parent = parent;
		this.descriptorFactory = descriptorFactory;
	}

	@Override
	public MutableEntityPropertyDescriptor getProperty( String propertyName ) {
		MutableEntityPropertyDescriptor localProperty = super.getProperty( propertyName );

		if ( localProperty == null ) {
			EntityPropertyDescriptor parentProperty = parent.getProperty( propertyName );

			if ( parentProperty != null ) {
				MutableEntityPropertyDescriptor mutable
						= descriptorFactory.createWithParent( propertyName, parentProperty );
				register( mutable );

				localProperty = mutable;
			}
		}

		return localProperty;
	}

	@Override
	public Collection<EntityPropertyDescriptor> getRegisteredDescriptors() {
		Map<String, EntityPropertyDescriptor> actual = new HashMap<>();

		for ( EntityPropertyDescriptor descriptor : super.getRegisteredDescriptors() ) {
			actual.put( descriptor.getName(), descriptor );
		}

		for ( EntityPropertyDescriptor descriptor : parent.getRegisteredDescriptors() ) {
			EntityPropertyDescriptor local = actual.get( descriptor.getName() );

			if ( local == null ) {
				MutableEntityPropertyDescriptor mutable
						= descriptorFactory.createWithParent( descriptor.getName(), descriptor );
				register( mutable );

				actual.put( mutable.getName(), mutable );
			}
		}

		return actual.values();
	}

	@Override
	public Comparator<EntityPropertyDescriptor> getDefaultOrder() {
		return super.getDefaultOrder().thenComparing( parent.getDefaultOrder() );
	}

	@Override
	public Predicate<EntityPropertyDescriptor> getDefaultFilter() {
		Predicate<EntityPropertyDescriptor> configured = super.getDefaultFilter();
		return configured != null ? configured : parent.getDefaultFilter();
	}
}
