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
import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistryProvider;
import com.foreach.across.modules.entity.util.EntityTypeDescriptor;
import com.foreach.across.modules.entity.util.EntityUtils;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.ViewElementTypeLookupStrategy;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.Temporal;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.foreach.across.modules.entity.views.bootstrapui.BootstrapUiElementTypeLookupStrategy.ORDER;

/**
 * Default {@link ViewElementTypeLookupStrategy} that maps the default {@link ViewElementMode} to a known view element type.
 * Has a specific {@link #ORDER} assigned making sure this default strategy comes late, other strategies without an order assigned
 * will always come before unless explicitly ordered after.
 *
 * @author Arne Vandamme
 */
@ConditionalOnClass(BootstrapUiElements.class)
@Component
@RequiredArgsConstructor
@Order(ORDER)
public class BootstrapUiElementTypeLookupStrategy implements ViewElementTypeLookupStrategy
{
	public static final int ORDER = Ordered.LOWEST_PRECEDENCE - 1000;

	private final EntityRegistry entityRegistry;
	private final EntityPropertyRegistryProvider propertyRegistryProvider;

	@SuppressWarnings("unchecked")
	@Override
	public String findElementType( EntityPropertyDescriptor descriptor, ViewElementMode viewElementMode ) {
		ViewElementMode singleMode = viewElementMode.forSingle();

		if ( ViewElementMode.FILTER_FORM.matchesTypeOf( singleMode )) {
			return FilterFormGroupElementBuilderFactory.VIEW_ELEMENT_TYPE;
		}

		if ( ViewElementMode.FILTER_CONTROL.matchesTypeOf( singleMode ) ) {
			return findFilterControlElementType( descriptor, viewElementMode.isForMultiple() );
		}

		boolean isForWriting = ViewElementMode.FORM_WRITE.matchesTypeOf( singleMode ) || ViewElementMode.isControl( singleMode );

		if ( isForWriting && !descriptor.isWritable() && descriptor.isReadable() ) {
			if ( ViewElementMode.FORM_WRITE.matchesTypeOf( singleMode ) ) {
				viewElementMode = ViewElementMode.FORM_READ;
			}
			else {
				viewElementMode = ViewElementMode.VALUE;
			}
		}

		boolean isForReading = ViewElementMode.FORM_READ.matchesTypeOf( singleMode ) || ViewElementMode.isValue( viewElementMode );

		if ( ( !descriptor.isWritable() && !descriptor.isReadable() ) && isForWriting ) {
			return null;
		}

		if ( !descriptor.isReadable() && isForReading ) {
			return null;
		}

		Class propertyType = descriptor.getPropertyType();
		EntityConfiguration entityConfiguration = propertyType != null ? entityRegistry.getEntityConfiguration( propertyType ) : null;

		boolean isRegisteredEntity = entityConfiguration != null && entityConfiguration.hasEntityModel();
		Boolean isEmbedded = isEmbedded( descriptor, entityConfiguration );
		boolean isEmbeddedCandidate = propertyType != null && isEmbeddedCandidate( propertyType );
		boolean hasVisibleProperties = isEmbedded == null && isEmbeddedCandidate && hasVisibleProperties( propertyType, entityConfiguration );

		if ( ViewElementMode.FORM_WRITE.matchesTypeOf( singleMode ) || ViewElementMode.FORM_READ.matchesTypeOf( singleMode ) ) {
			if ( propertyType != null
					&& isSingularType( propertyType )
					&& ( ( isEmbedded == null && !isRegisteredEntity && isEmbeddedCandidate
					&& hasVisibleProperties ) || Boolean.TRUE.equals( isEmbedded ) ) ) {
				return BootstrapUiElements.FIELDSET;
			}

			return BootstrapUiElements.FORM_GROUP;
		}

		if ( ViewElementMode.isLabel( singleMode ) ) {
			return BootstrapUiElements.LABEL;
		}

		if ( propertyType != null ) {
			if ( ClassUtils.isAssignable( propertyType, Number.class ) ) {
				return BootstrapUiElements.NUMERIC;
			}

			if ( isTemporalType( propertyType ) ) {
				return BootstrapUiElements.DATETIME;
			}
		}

		boolean isEmbeddedCollection = isEmbeddedCollection( descriptor );

		if ( isEmbeddedCollection && ViewElementMode.VALUE.matchesTypeOf( viewElementMode.forSingle() ) ) {
			return EmbeddedCollectionOrMapElementBuilderFactory.ELEMENT_TYPE;
		}

		if ( ViewElementMode.isValue( viewElementMode ) ) {
			return BootstrapUiElements.TEXT;
		}

		if ( descriptor.isWritable() ) {
			if ( propertyType != null ) {
				if ( ClassUtils.isAssignable( propertyType, Duration.class ) ) {
					return BootstrapUiElements.TEXTBOX;
				}

				TypeDescriptor typeDescriptor = descriptor.getPropertyTypeDescriptor();

				if ( typeDescriptor != null
						&& typeDescriptor.isCollection()
						&& Set.class.isAssignableFrom( typeDescriptor.getObjectType() ) ) {
					Class<?> elementType = typeDescriptor.getElementTypeDescriptor().getObjectType();
					if ( String.class.equals( elementType ) ) {
						return MultiValueElementBuilderFactory.ELEMENT_TYPE;
					}
					else if ( elementType.isEnum() ) {
						return OptionsFormElementBuilderFactory.OPTIONS;
					}
				}

				if ( propertyType.isArray() || Collection.class.isAssignableFrom( propertyType ) || Map.class.isAssignableFrom( propertyType ) ) {
					if ( isEmbeddedCollection ) {
						return EmbeddedCollectionOrMapElementBuilderFactory.ELEMENT_TYPE;
					}
					else {
						return OptionsFormElementBuilderFactory.OPTIONS;
					}
				}

				if ( propertyType.isEnum() ) {
					return BootstrapUiElements.SELECT;
				}

				if ( ClassUtils.isAssignable( propertyType, Boolean.class ) || ClassUtils.isAssignable( propertyType, AtomicBoolean.class ) ) {
					return BootstrapUiElements.CHECKBOX;
				}

				if ( CharSequence.class.isAssignableFrom( propertyType ) ) {
					return BootstrapUiElements.TEXTBOX;
				}

				if ( !ClassUtils.isPrimitiveOrWrapper( propertyType ) ) {
					if ( isRegisteredEntity ) {
						return BootstrapUiElements.SELECT;
					}
					else if ( hasVisibleProperties || Boolean.TRUE.equals( isEmbedded ) ) {
						return BootstrapUiElements.FIELDSET;
					}
				}

				return BootstrapUiElements.TEXTBOX;
			}
		}

		return null;
	}

	private boolean hasVisibleProperties( Class propertyType, EntityConfiguration entityConfiguration ) {
		EntityPropertyRegistry propertyRegistry = entityConfiguration != null
				? entityConfiguration.getPropertyRegistry() : propertyRegistryProvider.get( propertyType );

		return propertyRegistry != null && propertyRegistry.getProperties().stream().anyMatch( d -> !d.isHidden() );
	}

	private boolean isEmbeddedCandidate( Class type ) {
		if ( type.isEnum() ) {
			return false;
		}
		if ( type.isArray() || Collection.class.isAssignableFrom( type ) || Map.class.isAssignableFrom( type ) ) {
			return false;
		}
		if ( CharSequence.class.isAssignableFrom( type ) ) {
			return false;
		}
		if ( ClassUtils.isPrimitiveOrWrapper( type ) ) {
			return false;
		}
		if ( ClassUtils.isAssignable( type, Number.class )
				|| ClassUtils.isAssignable( type, Temporal.class )
				|| ClassUtils.isAssignable( type, Duration.class ) ) {
			return false;
		}
		return !isTemporalType( type );
	}

	private boolean isSingularType( Class propertyType ) {
		return !propertyType.isArray() && !Collection.class.isAssignableFrom( propertyType ) && !Map.class.isAssignableFrom( propertyType );
	}

	private boolean isEmbeddedCollection( EntityPropertyDescriptor descriptor ) {
		TypeDescriptor typeDescriptor = descriptor.getPropertyTypeDescriptor();

		if ( typeDescriptor != null && typeDescriptor.isCollection() ) {
			EntityPropertyRegistry owningRegistry = descriptor.getPropertyRegistry();

			if ( owningRegistry != null ) {
				EntityPropertyDescriptor memberDescriptor = owningRegistry.getProperty( descriptor.getName() + EntityPropertyRegistry.INDEXER );

				if ( memberDescriptor != null ) {
					EntityConfiguration<?> target = entityRegistry.getEntityConfiguration( memberDescriptor.getPropertyType() );

					boolean isEnum = memberDescriptor.getPropertyType().isEnum();
					boolean isRegisteredEntity = target != null && target.hasEntityModel();
					Boolean memberIsEmbedded = isEmbedded( memberDescriptor, target );

					return ( memberIsEmbedded == null && !isRegisteredEntity && !isEnum ) || Boolean.TRUE.equals( memberIsEmbedded );
				}
			}
		}

		return false;
	}

	private Boolean isEmbedded( EntityPropertyDescriptor descriptor, EntityConfiguration target ) {
		Object attribute = descriptor.getAttribute( EntityAttributes.IS_EMBEDDED_OBJECT );

		if ( attribute == null && target != null ) {
			attribute = target.getAttribute( EntityAttributes.IS_EMBEDDED_OBJECT );
		}

		return attribute != null ? Boolean.TRUE.equals( attribute ) : null;
	}

	/**
	 * Checks whether the given type is a supported date type.
	 * Currently {@link Date}, {@link Temporal} are supported.
	 *
	 * @param propertyType to check
	 * @return whether it is a supported date type
	 */
	private boolean isTemporalType( Class propertyType ) {
		return ClassUtils.isAssignable( propertyType, Date.class ) || ClassUtils.isAssignable( propertyType, Temporal.class );
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
