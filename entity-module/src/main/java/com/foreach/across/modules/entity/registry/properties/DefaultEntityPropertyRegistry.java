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

import com.foreach.across.modules.entity.views.ViewElementLookupRegistry;
import com.foreach.across.modules.entity.views.ViewElementLookupRegistryImpl;
import com.foreach.across.modules.entity.views.support.NestedValueFetcher;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.domain.Sort;

/**
 * Default implementation of a {@link MutableEntityPropertyRegistry}.
 * Holds a collection of registered property descriptors.
 * <p/>
 * Optionally supports a reference to a {@link EntityPropertyRegistryProvider}.
 * If one is present, nested properties can be requested as well.  For example, if a property <b>user</b> is
 * registered of type {@code User}.  When requesting a property <b>user.id</b> the property registry for type
 * {@code User} will be queried for the <b>id</b> property and a nested property will be built an stored inside
 * this registry.
 * <p/>
 * Usually created through a {@link EntityPropertyRegistryProvider}.
 *
 * @author Arne Vandamme
 * @see EntityPropertyRegistryProvider
 * @since 1.0.0
 */
public class DefaultEntityPropertyRegistry extends EntityPropertyRegistrySupport
{
	/**
	 * Create a new detached entity property registry.  Only properties registered directly will be available,
	 * no nested properties will be looked up.
	 */
	public DefaultEntityPropertyRegistry() {
		this( null );
	}

	/**
	 * Creates a new registry that will use the registry provider to look up nested properties of property types.
	 * For example, if no property <b>user.id</b> is registered directly, the property <b>id</b> will be fetched
	 * from the registry attached to the type of the <b>user</b> property.  A custom descriptor will be built
	 * and the <b>user.id</b> property will be created inside this one.
	 *
	 * @param registryProvider for looking up nested properties
	 */
	public DefaultEntityPropertyRegistry( EntityPropertyRegistryProvider registryProvider ) {
		super( registryProvider );
		setDefaultFilter( entityPropertyDescriptor -> !entityPropertyDescriptor.isHidden() );
	}

	@Override
	public MutableEntityPropertyDescriptor getProperty( String propertyName ) {
		MutableEntityPropertyDescriptor descriptor = super.getProperty( propertyName );

		if ( descriptor == null && getRegistryProvider() != null ) {
			String rootProperty = findRootProperty( propertyName );

			if ( rootProperty != null ) {
				EntityPropertyDescriptor rootDescriptor = super.getProperty( rootProperty );

				if ( rootDescriptor != null && rootDescriptor.getPropertyType() != null ) {
					EntityPropertyRegistry subRegistry = getRegistryProvider().get( findPropertyType( rootDescriptor ) );

					if ( subRegistry != null ) {
						EntityPropertyDescriptor childDescriptor = subRegistry.getProperty( findChildProperty( propertyName ) );

						if ( childDescriptor != null ) {
							descriptor = buildNestedDescriptor( propertyName, rootDescriptor, childDescriptor );
						}
					}
				}
			}
		}

		return descriptor;
	}

	private Class<?> findPropertyType( EntityPropertyDescriptor descriptor ) {
		Class<?> propertyType = descriptor.getPropertyType();

		TypeDescriptor typeDescriptor = descriptor.getPropertyTypeDescriptor();
		if ( typeDescriptor != null && ( typeDescriptor.isCollection() || typeDescriptor.isArray() ) ) {
			val elementType = typeDescriptor.getElementTypeDescriptor();
			if ( elementType != null ) {
				return elementType.getObjectType();
			}
		}

		return propertyType;
	}

	private MutableEntityPropertyDescriptor buildNestedDescriptor( String name,
	                                                               EntityPropertyDescriptor parent,
	                                                               EntityPropertyDescriptor child ) {
		SimpleEntityPropertyDescriptor descriptor = new SimpleEntityPropertyDescriptor( name );
		descriptor.setDisplayName( child.getDisplayName() );
		descriptor.setPropertyType( child.getPropertyType() );
		descriptor.setPropertyTypeDescriptor( child.getPropertyTypeDescriptor() );
		descriptor.setReadable( child.isReadable() );
		descriptor.setWritable( child.isWritable() );
		descriptor.setHidden( child.isHidden() );

		if ( descriptor.isReadable() ) {
			descriptor.setValueFetcher( new NestedValueFetcher( parent.getValueFetcher(), child.getValueFetcher() ) );
		}

		// todo: fixme decently
		ViewElementLookupRegistryImpl existingLookupRegistry = descriptor.getAttribute( ViewElementLookupRegistry.class );
		ViewElementLookupRegistry lookupRegistry = new ViewElementLookupRegistryImpl();
		if ( existingLookupRegistry != null ) {
			existingLookupRegistry.mergeInto( lookupRegistry );
		}

		descriptor.setAttributes( child.attributeMap() );
		descriptor.setAttribute( ViewElementLookupRegistry.class, lookupRegistry );

		if ( child.hasAttribute( Sort.Order.class ) ) {
			Sort.Order order = child.getAttribute( Sort.Order.class );

			Sort.Order nestedOrder = new Sort.Order( parent.getName() + "." + order.getProperty() )
					.with( order.getNullHandling() );

			if ( order.isIgnoreCase() ) {
				nestedOrder = nestedOrder.ignoreCase();
			}

			descriptor.setAttribute( Sort.Order.class, nestedOrder );
		}

		register( descriptor );

		return descriptor;
	}

	private String findChildProperty( String propertyName ) {
		return StringUtils.defaultIfEmpty( StringUtils.substringAfter( propertyName, "." ), null );
	}

	private String findRootProperty( String propertyName ) {
		return StringUtils.defaultIfEmpty( StringUtils.substringBefore( propertyName, "." ), null );
	}
}
