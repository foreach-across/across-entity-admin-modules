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
package com.foreach.across.modules.entity.views.bootstrapui;

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiElements;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.meta.PropertyPersistenceMetadata;
import com.foreach.across.modules.entity.util.EntityTypeDescriptor;
import com.foreach.across.modules.entity.util.EntityUtils;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.ViewElementTypeLookupStrategy;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Default {@link ViewElementTypeLookupStrategy} that maps the default {@link ViewElementMode} to a known view element type.
 *
 * @author Arne Vandamme
 */
@ConditionalOnClass(BootstrapUiElements.class)
@Component
@RequiredArgsConstructor
public class BootstrapUiElementTypeLookupStrategy implements ViewElementTypeLookupStrategy
{
	private final EntityRegistry entityRegistry;

	@SuppressWarnings("unchecked")
	@Override
	public String findElementType( EntityPropertyDescriptor descriptor, ViewElementMode viewElementMode ) {
		ViewElementMode singleMode = viewElementMode.forSingle();

		if ( ViewElementMode.FILTER_CONTROL.equals( singleMode ) ) {
			return findFilterControlElementType( descriptor, viewElementMode.isForMultiple() );
		}

		boolean isForWriting = ViewElementMode.FORM_WRITE.equals( singleMode ) || ViewElementMode.isControl( singleMode );

		if ( isForWriting && !descriptor.isWritable() && descriptor.isReadable() ) {
			if ( ViewElementMode.FORM_WRITE.equals( singleMode ) ) {
				viewElementMode = ViewElementMode.FORM_READ;
			}
			else {
				viewElementMode = ViewElementMode.VALUE;
			}
		}

		boolean isForReading = ViewElementMode.FORM_READ.equals( singleMode ) || ViewElementMode.isValue( viewElementMode );

		if ( ( !descriptor.isWritable() && !descriptor.isReadable() ) && isForWriting ) {
			return null;
		}

		if ( !descriptor.isReadable() && isForReading ) {
			return null;
		}

		boolean isEmbedded = PropertyPersistenceMetadata.isEmbeddedProperty( descriptor );

		if ( ViewElementMode.FORM_WRITE.equals( singleMode ) || ViewElementMode.FORM_READ.equals( singleMode ) ) {
			if ( isEmbedded ) {
				return BootstrapUiElements.FIELDSET;
			}

			return BootstrapUiElements.FORM_GROUP;
		}

		if ( ViewElementMode.isLabel( singleMode ) ) {
			return BootstrapUiElements.LABEL;
		}

		if ( isEmbedded ) {
			return null;
		}

		Class propertyType = descriptor.getPropertyType();

		if ( propertyType != null ) {
			if ( ClassUtils.isAssignable( propertyType, Number.class ) ) {
				return BootstrapUiElements.NUMERIC;
			}

			if ( isDateType( propertyType ) ) {
				return BootstrapUiElements.DATETIME;
			}
		}

		if ( ViewElementMode.isValue( viewElementMode ) ) {
			return BootstrapUiElements.TEXT;
		}

		if ( descriptor.isWritable() ) {
			if ( propertyType != null ) {
				TypeDescriptor typeDescriptor = descriptor.getPropertyTypeDescriptor();

				if ( typeDescriptor != null
						&& typeDescriptor.isCollection()
						&& Set.class.isAssignableFrom( typeDescriptor.getObjectType() )
						&& String.class.equals( typeDescriptor.getElementTypeDescriptor().getObjectType() ) ) {
					return MultiValueElementBuilderFactory.ELEMENT_TYPE;
				}

				if ( propertyType.isArray() || Collection.class.isAssignableFrom( propertyType ) ) {
					return OptionsFormElementBuilderFactory.OPTIONS;
				}

				if ( propertyType.isEnum() ) {
					return BootstrapUiElements.SELECT;
				}

				if ( !ClassUtils.isPrimitiveOrWrapper( propertyType ) ) {
					EntityConfiguration member = entityRegistry.getEntityConfiguration( propertyType );

					if ( member != null ) {
						return BootstrapUiElements.SELECT;
					}
				}

				if ( ClassUtils.isAssignable( propertyType, Boolean.class ) || ClassUtils.isAssignable( propertyType, AtomicBoolean.class ) ) {
					return BootstrapUiElements.CHECKBOX;
				}

				return BootstrapUiElements.TEXTBOX;
			}
		}

		return null;
	}

	/**
	 * Checks whether the given type is a supported date type.
	 * Currently {@link Date}, {@link LocalDate}, {@link LocalTime} and {@link LocalDateTime} are supported.
	 *
	 * @param propertyType to check
	 * @return whether it is a supported date type
	 */
	private boolean isDateType( Class propertyType ) {
		return ClassUtils.isAssignable( propertyType, Date.class ) || ClassUtils.isAssignable( propertyType, LocalDate.class )
				|| ClassUtils.isAssignable( propertyType, LocalTime.class ) || ClassUtils.isAssignable( propertyType, LocalDateTime.class );
	}

	/**
	 * Filter controls use a different lookup strategy.  Only the member property descriptor type is used in case of a collection,
	 * it is the multiple boolean that determines if a control for multiple values should be built or not.  For example:
	 * if a property is a set of users, the fact that it is a set will determine the filter operand that should be used, but the actual
	 * filtering would still be done on a single user.
	 */
	@SuppressWarnings("all")
	private String findFilterControlElementType( EntityPropertyDescriptor descriptor, boolean multiple ) {
		EntityTypeDescriptor typeDescriptor = EntityUtils.resolveEntityTypeDescriptor( descriptor.getPropertyTypeDescriptor(), entityRegistry );

		if ( typeDescriptor.isTargetTypeResolved()
				&& ( entityRegistry.contains( typeDescriptor.getSimpleTargetType() ) || typeDescriptor.getSimpleTargetType().isEnum()
				|| Boolean.class.equals( typeDescriptor.getTargetTypeDescriptor().getObjectType() ) ) ) {
			return BootstrapUiElements.SELECT;
		}

		return BootstrapUiElements.TEXTBOX;
	}
}
