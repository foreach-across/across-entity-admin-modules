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
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyDescriptor;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.PropertyDescriptor;
import java.lang.annotation.Annotation;

/**
 * Will attempt to determine the value of the {@link com.foreach.across.modules.entity.EntityAttributes#PROPERTY_REQUIRED}
 * for every property, based on the presence of validation or persistence annotations.
 * The following will make a property required:
 * <ul>
 * <li>{@link NotNull}, {@link NotEmpty} or {@link NotBlank}</li>
 * <li>a non-nullable {@link Column}</li>
 * <li>a non-optional {@link OneToOne} or {@link ManyToOne}</li>
 * </ul>
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
@Component
@OrderInModule(4)
class RequiredAttributePropertyDescriptorEnhancer extends AbstractEntityPropertyDescriptorEnhancer
{
	private static final Class<?>[] REQUIRED_ANNOTATION_TYPES = { NotNull.class, NotEmpty.class, NotBlank.class };
	private static final Class<?>[] PERSISTENCE_ANNOTATION_TYPES = { ManyToOne.class, OneToOne.class, Column.class };

	@Override
	protected void enhance( Class<?> entityType, MutableEntityPropertyDescriptor descriptor ) {
		if ( !descriptor.hasAttribute( EntityAttributes.PROPERTY_REQUIRED ) ) {
			boolean required = handleConstraintAnnotations( descriptor );

			if ( !required ) {
				required = handlePersistenceAnnotations( descriptor );
			}

			if ( required ) {
				descriptor.setAttribute( EntityAttributes.PROPERTY_REQUIRED, true );
			}
		}
	}

	private boolean handleConstraintAnnotations( EntityPropertyDescriptor descriptor ) {
		PropertyDescriptor validationDescriptor = descriptor.getAttribute( PropertyDescriptor.class );

		if ( validationDescriptor != null && validationDescriptor.hasConstraints() ) {
			for ( ConstraintDescriptor constraint : validationDescriptor.getConstraintDescriptors() ) {
				Annotation annotation = constraint.getAnnotation();

				if ( ArrayUtils.contains( REQUIRED_ANNOTATION_TYPES, annotation.annotationType() ) || isNonOptionalRelation( annotation ) ) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean handlePersistenceAnnotations( EntityPropertyDescriptor descriptor ) {
		PersistentProperty property = descriptor.getAttribute( PersistentProperty.class );

		if ( property != null ) {
			for ( Class<?> annotationType : PERSISTENCE_ANNOTATION_TYPES ) {
				if ( property.isAnnotationPresent( annotationType ) ) {
					if ( isNonOptionalRelation( property.findAnnotation( annotationType ) ) ) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean isNonOptionalRelation( Annotation annotation ) {
		val annotationAttributes = AnnotationUtils.getAnnotationAttributes( annotation );

		if ( ManyToOne.class.equals( annotation.annotationType() ) || OneToOne.class.equals( annotation.annotationType() ) ) {
			Boolean optional = (Boolean) annotationAttributes.get( "optional" );

			return optional != null && !optional;
		}
		else if ( Column.class.equals( annotation.annotationType() ) ) {
			Boolean nullable = (Boolean) annotationAttributes.get( "nullable" );

			return nullable != null && !nullable;
		}

		return false;
	}
}
