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

package com.foreach.across.modules.entity.registry.properties.registrars;

import com.foreach.across.modules.entity.registry.properties.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Scans a class and registers all its properties in the registry.
 * Also applies a default ordering based on the declaration order of the properties.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DefaultPropertiesRegistrar implements DefaultEntityPropertyRegistryProvider.PropertiesRegistrar
{
	private final EntityPropertyDescriptorFactory propertyDescriptorFactory;

	@Autowired
	public DefaultPropertiesRegistrar( EntityPropertyDescriptorFactory propertyDescriptorFactory ) {
		this.propertyDescriptorFactory = propertyDescriptorFactory;
	}

	@Override
	public void accept( Class<?> entityType, MutableEntityPropertyRegistry registry ) {
		Map<String, PropertyDescriptor> scannedDescriptors = new HashMap<>();

		for ( PropertyDescriptor descriptor : BeanUtils.getPropertyDescriptors( entityType ) ) {
			if ( descriptor.getReadMethod() != null || descriptor.getWriteMethod() != null ) {
				registry.register( propertyDescriptorFactory.create( descriptor, entityType ) );
			}
			scannedDescriptors.put( descriptor.getName(), descriptor );
		}

		if ( registry instanceof EntityPropertyRegistrySupport ) {
			registerDeclarationPropertyOrder(
					scannedDescriptors, entityType, (EntityPropertyRegistrySupport) registry
			);
		}
	}

	private void registerDeclarationPropertyOrder(
			Map<String, PropertyDescriptor> scannedDescriptors,
			Class<?> entityType,
			EntityPropertyRegistrySupport registry
	) {
		final Map<String, Integer> order = new HashMap<>();

		ReflectionUtils.doWithFields( entityType, new ReflectionUtils.FieldCallback()
		{
			private Class declaringClass;
			private int declaringClassOffset = 1000000;

			@Override
			public void doWith( Field field ) throws IllegalArgumentException, IllegalAccessException {
				if ( !field.getDeclaringClass().equals( declaringClass ) ) {
					declaringClass = field.getDeclaringClass();
					declaringClassOffset += 1000;
				}

				if ( registry.contains( field.getName() ) ) {
					order.put( field.getName(), declaringClassOffset + order.size() + 1 );
				}
			}
		} );

		// Determine method indices
		final Map<Method, Integer> methodIndex = new HashMap<>();

		ReflectionUtils.doWithMethods( entityType, new ReflectionUtils.MethodCallback()
		{
			private Class declaringClass;
			private int declaringClassOffset = 1000000;

			@Override
			public void doWith( Method method ) throws IllegalArgumentException, IllegalAccessException {
				if ( !method.getDeclaringClass().equals( declaringClass ) ) {
					declaringClass = method.getDeclaringClass();
					declaringClassOffset += 1000;
				}

				if ( !methodIndex.containsKey( method ) ) {
					methodIndex.put( method, declaringClassOffset + methodIndex.size() + 1 );
				}
			}
		} );

		// For every property without declared order, use the read method first, write method second to determine order
		for ( EntityPropertyDescriptor entityPropertyDescriptor : registry.getRegisteredDescriptors() ) {
			if ( !order.containsKey( entityPropertyDescriptor.getName() ) ) {
				PropertyDescriptor propertyDescriptor = scannedDescriptors.get( entityPropertyDescriptor.getName() );

				if ( propertyDescriptor != null ) {
					Method lookupMethod = propertyDescriptor.getReadMethod();

					if ( lookupMethod != null ) {
						order.put( entityPropertyDescriptor.getName(), methodIndex.get( lookupMethod ) );
					}
					else {
						lookupMethod = propertyDescriptor.getWriteMethod();

						if ( lookupMethod != null ) {
							order.put( entityPropertyDescriptor.getName(), methodIndex.get( lookupMethod ) );
						}
					}
				}
			}
		}

		order.forEach( registry::setPropertyOrder );
	}
}
