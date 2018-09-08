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

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders;
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiElements;
import com.foreach.across.modules.bootstrapui.elements.builder.FieldsetFormElementBuilder;
import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.conditionals.ConditionalOnBootstrapUI;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertySelector;
import com.foreach.across.modules.entity.registry.properties.meta.PropertyPersistenceMetadata;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderFactorySupport;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderService;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.FieldsetDescriptionTextPostProcessor;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.FieldsetHelpTextPostProcessor;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.FieldsetTooltipTextPostProcessor;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Builds a {@link com.foreach.across.modules.bootstrapui.elements.builder.FieldsetFormElementBuilder}
 * for a property.  The property can have a {@link EntityAttributes#FIELDSET_PROPERTY_SELECTOR} attribute
 * specifying the selector that should be used to fetch the members of the fieldset.
 * If none is available and the property is embedded, a default will be created for all properties of the embedded type.
 *
 * @author Arne Vandamme
 * @see com.foreach.across.modules.entity.EntityAttributes#FIELDSET_PROPERTY_SELECTOR
 */
@ConditionalOnBootstrapUI
@Component
@RequiredArgsConstructor
public class FieldsetFormElementBuilderFactory extends EntityViewElementBuilderFactorySupport<FieldsetFormElementBuilder>
{
	private final EntityViewElementBuilderService entityViewElementBuilderService;

	@Override
	public boolean supports( String viewElementType ) {
		return BootstrapUiElements.FIELDSET.equals( viewElementType );
	}

	@Override
	protected FieldsetFormElementBuilder createInitialBuilder( EntityPropertyDescriptor propertyDescriptor,
	                                                           ViewElementMode viewElementMode,
	                                                           String viewElementType ) {
		ViewElementBuilder labelText = entityViewElementBuilderService.getElementBuilder( propertyDescriptor, ViewElementMode.LABEL );

		FieldsetFormElementBuilder fieldset = BootstrapUiBuilders.fieldset()
		                                                         .name( propertyDescriptor.getName() )
		                                                         .legend()
		                                                         .add( labelText )
		                                                         .and();

		if ( ViewElementMode.FORM_WRITE.equals( viewElementMode.forSingle() ) ) {
			fieldset.postProcessor( new FieldsetDescriptionTextPostProcessor<>() )
			        .postProcessor( new FieldsetTooltipTextPostProcessor<>() )
			        .postProcessor( new FieldsetHelpTextPostProcessor<>() );
		}

		EntityPropertySelector selector = retrieveMembersSelector( propertyDescriptor );
		EntityPropertyRegistry propertyRegistry = propertyDescriptor.getPropertyRegistry();

		if ( selector != null && propertyRegistry != null ) {
			for ( EntityPropertyDescriptor member : propertyRegistry.select( selector ) ) {
				ViewElementBuilder memberBuilder = entityViewElementBuilderService.getElementBuilder( member, viewElementMode );

				if ( memberBuilder != null ) {
					fieldset.add( memberBuilder );
				}
			}
		}

		return fieldset;
	}

	private EntityPropertySelector retrieveMembersSelector( EntityPropertyDescriptor descriptor ) {
		EntityPropertySelector selector = descriptor.getAttribute( EntityAttributes.FIELDSET_PROPERTY_SELECTOR, EntityPropertySelector.class );

		if ( selector == null && PropertyPersistenceMetadata.isEmbeddedProperty( descriptor ) ) {
			selector = new EntityPropertySelector( descriptor.getName() + ".*" );
		}

		return selector;
	}
}
