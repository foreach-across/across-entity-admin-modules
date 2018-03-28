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
import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyDescriptor;
import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.PropertyDescriptor;
import java.lang.annotation.Annotation;

/**
 * Will attempt to determine the value of the {@link com.foreach.across.modules.entity.EntityAttributes#PROPERTY_REQUIRED}
 * for every property, based on the presence of validation or persistence annotations:
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
@Component
@OrderInModule(4)
class RequiredAttributePropertyDescriptorEnhancer extends AbstractEntityPropertyDescriptorEnhancer
{
	private static final Class<?>[] ANNOTATION_TYPES = { NotNull.class, NotEmpty.class, NotBlank.class };

	@Override
	protected void enhance( Class<?> entityType, MutableEntityPropertyDescriptor descriptor ) {
		if ( !descriptor.hasAttribute( EntityAttributes.PROPERTY_REQUIRED ) ) {
			PropertyDescriptor validationDescriptor = descriptor.getAttribute( PropertyDescriptor.class );

			if ( validationDescriptor != null && validationDescriptor.hasConstraints() ) {
				for ( ConstraintDescriptor constraint : validationDescriptor.getConstraintDescriptors() ) {
					Annotation annotation = constraint.getAnnotation();

					if ( ArrayUtils.contains( ANNOTATION_TYPES, annotation.annotationType() ) ) {
						descriptor.setAttribute( EntityAttributes.PROPERTY_REQUIRED, true );
						break;
					}
				}
			}
		}
	}
}
