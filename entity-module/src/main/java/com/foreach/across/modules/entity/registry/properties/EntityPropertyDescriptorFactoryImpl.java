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

import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.util.EntityUtils;
import com.foreach.across.modules.entity.views.support.MethodValueFetcher;
import com.foreach.across.modules.entity.views.support.MethodValueWriter;
import org.springframework.core.convert.Property;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

/**
 * @author Arne Vandamme
 */
// todo: when creating from parent, dispatchToProcessors to attribute cloning
@Service
public class EntityPropertyDescriptorFactoryImpl implements EntityPropertyDescriptorFactory
{
	@Override
	public MutableEntityPropertyDescriptor create( PropertyDescriptor originalProperty, Class<?> entityType ) {
		Method writeMethod = originalProperty.getWriteMethod();
		Method readMethod = originalProperty.getReadMethod();
		Property customProperty = new Property( entityType, readMethod, writeMethod, originalProperty.getName() );
		MutableEntityPropertyDescriptor descriptor = create( customProperty );

		if ( StringUtils.equals( originalProperty.getName(), originalProperty.getDisplayName() ) ) {
			descriptor.setDisplayName( EntityUtils.generateDisplayName( originalProperty.getName() ) );
		}
		else {
			descriptor.setDisplayName( originalProperty.getDisplayName() );
		}

		descriptor.setAttribute( EntityAttributes.NATIVE_PROPERTY_DESCRIPTOR, originalProperty );

		return descriptor;
	}

	@Override
	public MutableEntityPropertyDescriptor create( Property property ) {
		SimpleEntityPropertyDescriptor descriptor = new SimpleEntityPropertyDescriptor( property.getName() );
		descriptor.setDisplayName( EntityUtils.generateDisplayName( property.getName() ) );

		descriptor.setWritable( property.getWriteMethod() != null );
		descriptor.setReadable( property.getReadMethod() != null );

		if ( !descriptor.isWritable() || !descriptor.isReadable() ) {
			descriptor.setHidden( true );
		}

		descriptor.setPropertyType( property.getType() );
		descriptor.setPropertyTypeDescriptor( new TypeDescriptor( property ) );

		descriptor.setController( createPropertyController( property ) );
		descriptor.setAttribute( EntityAttributes.NATIVE_PROPERTY_DESCRIPTOR, property );

		return descriptor;
	}

	private EntityPropertyController<?, ?> createPropertyController( Property property ) {
		GenericEntityPropertyController controller = new GenericEntityPropertyController();

		if ( property.getReadMethod() != null ) {
			controller.withTarget( Object.class, Object.class )
			          .valueFetcher( new MethodValueFetcher<>( property.getReadMethod() ) );
		}
		if ( property.getWriteMethod() != null ) {
			controller.withTarget( Object.class, Object.class )
			          .applyValueFunction( new MethodValueWriter<>( property.getWriteMethod() ) );
		}

		return controller;
	}

	@Override
	public MutableEntityPropertyDescriptor createWithParent( String name, EntityPropertyDescriptor parent ) {
		return new SimpleEntityPropertyDescriptor( name, parent );
	}
}
