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

import com.foreach.across.core.annotations.OrderInModule;
import com.foreach.across.modules.entity.registry.properties.DefaultEntityPropertyRegistryProvider;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ValidatorFactory;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.PropertyDescriptor;

/**
 * @author niels
 * @since 4/02/2015
 */
@Component
@OrderInModule(3)
public class ValidationMetadataPropertiesRegistrar implements DefaultEntityPropertyRegistryProvider.PropertiesRegistrar
{
	@Autowired
	private ValidatorFactory validatorFactory;

	public void accept( Class<?> entityType, MutableEntityPropertyRegistry registry ) {
		BeanDescriptor beanDescriptor = validatorFactory.getValidator().getConstraintsForClass( entityType );

		if ( beanDescriptor != null ) {
			for ( EntityPropertyDescriptor descriptor : registry.getRegisteredDescriptors() ) {
				PropertyDescriptor validatorDescriptor
						= beanDescriptor.getConstraintsForProperty( descriptor.getName() );

				if ( validatorDescriptor != null ) {
					MutableEntityPropertyDescriptor mutable = registry.getProperty( descriptor.getName() );

					if ( mutable != null ) {
						mutable.setAttribute( PropertyDescriptor.class, validatorDescriptor );
					}
				}
			}
		}
	}
}
