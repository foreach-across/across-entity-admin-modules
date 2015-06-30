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
package com.foreach.across.modules.entity.newviews.bootstrapui;

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiElements;
import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.newviews.ViewElementMode;
import com.foreach.across.modules.entity.newviews.ViewElementTypeLookupStrategy;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.meta.PropertyPersistenceMetadata;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Arne Vandamme
 */
public class BootstrapUiElementTypeLookupStrategy implements ViewElementTypeLookupStrategy
{
	@Autowired
	private EntityRegistry entityRegistry;

	@Override
	public String findElementType( EntityConfiguration entityConfiguration,
	                               EntityPropertyDescriptor descriptor,
	                               ViewElementMode viewElementMode ) {
		// todo: preferred type for mode!

		if ( descriptor.isHidden()
				|| ( ViewElementMode.FORM_READ.equals( viewElementMode ) && !descriptor.isReadable() )
				|| ( ViewElementMode.FORM_WRITE.equals( viewElementMode ) && !descriptor.isWritable() ) ) {
			return null;
		}

		if ( ViewElementMode.FORM_WRITE.equals( viewElementMode )
				|| ViewElementMode.FORM_READ.equals( viewElementMode ) ) {
			return BootstrapUiElements.FORM_GROUP;
		}

		if ( ViewElementMode.isValue( viewElementMode ) ) {
			return BootstrapUiElements.TEXT;
		}

		if ( ViewElementMode.isLabel( viewElementMode ) ) {
			return BootstrapUiElements.LABEL;
		}

/*
		if ( viewElementMode == ViewElementMode.FOR_READING ) {
			String preferredType = descriptor.getAttribute( EntityAttributes.ELEMENT_TYPE_READABLE );

			if ( preferredType != null ) {
				return preferredType;
			}

			return CommonViewElements.TEXT;
		}

		String preferredType = descriptor.getAttribute( EntityAttributes.ELEMENT_TYPE_WRITABLE );

		if ( preferredType != null ) {
			return preferredType;
		}
*/
		PropertyPersistenceMetadata metadata = descriptor.getAttribute(
				EntityAttributes.PROPERTY_PERSISTENCE_METADATA, PropertyPersistenceMetadata.class );

		/*
		if ( metadata != null && metadata.isEmbedded() ) {
			return BootstrapUiElements.FIELDSET;
		}*/

		if ( descriptor.isWritable() ) {
			Class propertyType = descriptor.getPropertyType();

			if ( propertyType != null ) {
				if ( propertyType.isArray() || Collection.class.isAssignableFrom( propertyType ) ) {
					return BootstrapUiElements.MULTI_CHECKBOX;
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

				if ( ClassUtils.isAssignable( Boolean.class, propertyType )
						|| ClassUtils.isAssignable( AtomicBoolean.class, propertyType ) ) {
					return BootstrapUiElements.CHECKBOX;
				}

				/*if ( ClassUtils.isAssignable( Date.class, propertyType ) ) {
					return BootstrapUiElements.DATE;
				}*/

				return BootstrapUiElements.TEXTBOX;
			}
		}

		return null;
	}
}
