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

package it.com.foreach.across.modules.entity.views.bootstrapui;

import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderFactory;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.querydsl.core.util.ReflectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
public abstract class ViewElementBuilderFactoryTestSupport<T extends ViewElement>
{
	@Autowired
	protected EntityConfiguration entityConfiguration;

	@Autowired
	protected EntityPropertyRegistry registry;

	@Autowired
	protected EntityRegistry entityRegistry;

	@Autowired
	protected EntityViewElementBuilderFactory builderFactory;

	protected LocalValidatorFactoryBean validatorFactory = new LocalValidatorFactoryBean();
	protected ViewElementBuilderContext builderContext;
	protected Map<String, EntityPropertyDescriptor> properties = new HashMap<>();

	@Before
	@SuppressWarnings("unchecked")
	public void before() {
		validatorFactory.afterPropertiesSet();
		reset( entityConfiguration, registry );

		builderContext = spy( new DefaultViewElementBuilderContext() );

		EntityMessageCodeResolver codeResolver = mock( EntityMessageCodeResolver.class );

		when( entityConfiguration.getEntityMessageCodeResolver() ).thenReturn( codeResolver );
		when( builderContext.getAttribute( EntityMessageCodeResolver.class ) ).thenReturn( codeResolver );

		if ( properties.isEmpty() ) {
			BeanDescriptor beanDescriptor = validatorFactory.getValidator().getConstraintsForClass( getTestClass() );

			for ( Field field : ReflectionUtils.getFields( getTestClass() ) ) {
				String propertyName = field.getName();
				PropertyDescriptor validationDescriptor = beanDescriptor.getConstraintsForProperty( field.getName() );

				EntityPropertyDescriptor descriptor = mock( EntityPropertyDescriptor.class );
				when( descriptor.getPropertyRegistry() ).thenReturn( registry );
				when( descriptor.getName() ).thenReturn( propertyName );
				when( descriptor.getDisplayName() ).thenReturn( StringUtils.lowerCase( propertyName ) );
				when( descriptor.getAttribute( PropertyDescriptor.class ) ).thenReturn( validationDescriptor );
				when( descriptor.getPropertyType() ).thenReturn( (Class) field.getType() );
				when( descriptor.isWritable() ).thenReturn( true );
				TypeDescriptor typeDescriptor = new TypeDescriptor( field );
				when( descriptor.getPropertyTypeDescriptor() ).thenReturn( typeDescriptor );
				when( descriptor.hasAttribute( EntityAttributes.NATIVE_PROPERTY_DESCRIPTOR )).thenReturn( true );

				when( codeResolver.getMessageWithFallback(
						eq( "properties." + field.getName() ), any( String.class )
				      )
				)
						.thenReturn( "resolved: " + StringUtils.lowerCase( propertyName ) );

				properties.put( propertyName, descriptor );

				PersistentProperty persistentProperty = mock( PersistentProperty.class );

				for ( Annotation annotation : field.getAnnotations() ) {
					when( persistentProperty.isAnnotationPresent( annotation.annotationType() ) ).thenReturn( true );
					when( persistentProperty.findAnnotation( annotation.annotationType() ) ).thenReturn( annotation );

					if ( annotation.annotationType().getName().startsWith( "javax.persistence" ) ) {
						when( descriptor.getAttribute( PersistentProperty.class ) ).thenReturn( persistentProperty );
					}
				}
			}
		}
	}

	protected abstract Class getTestClass();

	protected <V extends T> V assemble( String propertyName, ViewElementMode viewElementMode ) {
		return assemble( propertyName, viewElementMode, null );
	}

	protected <V extends T> V assemble( String propertyName, ViewElementMode viewElementMode, String viewElementType ) {
		return assemble( properties.get( propertyName ), viewElementMode, viewElementType );
	}

	@SuppressWarnings("unchecked")
	protected <V extends T> V assemble( EntityPropertyDescriptor descriptor, ViewElementMode viewElementMode ) {
		return assemble( descriptor, viewElementMode, null );
	}

	@SuppressWarnings("unchecked")
	protected <V extends T> V assemble( EntityPropertyDescriptor descriptor,
	                                    ViewElementMode viewElementMode,
	                                    String viewElementType ) {
		return (V) builderFactory
				.createBuilder( descriptor, viewElementMode, viewElementType )
				.build( builderContext );
	}

}