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

import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.conditionals.ConditionalOnBootstrapUI;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertySelector;
import com.foreach.across.modules.entity.registry.properties.meta.PropertyPersistenceMetadata;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderFactory;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderService;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.elements.builder.EmbeddedCollectionViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;

/**
 * Experimental control for managing a {@link java.util.Collection} or {@link java.util.Map} of embedded elements.
 *
 * @author Arne Vandamme
 * @see EmbeddedCollectionViewElementBuilder
 * @since 3.2.0
 */
@ConditionalOnBootstrapUI
@Component
@RequiredArgsConstructor
public class EmbeddedCollectionOrMapElementBuilderFactory implements EntityViewElementBuilderFactory<ViewElementBuilder>
{
	public static final String ELEMENT_TYPE = "embeddedCollectionOrMap";

	private final EntityViewElementBuilderService entityViewElementBuilderService;

	@Override
	public boolean supports( String viewElementType ) {
		return ELEMENT_TYPE.equals( viewElementType );
	}

	@Override
	public ViewElementBuilder createBuilder( EntityPropertyDescriptor propertyDescriptor, ViewElementMode viewElementMode, String viewElementType ) {
		val embedded = new EmbeddedCollectionViewElementBuilder();
		embedded.itemTemplate( createItemTemplate( propertyDescriptor, viewElementMode ) );

		return embedded;
		/*




		return BootstrapUiBuilders.node( "div" )
		                          .customTemplate( "th/entity/elements :: embedded-collection" )
		                          .add( template );
		                          */
	}

	@SuppressWarnings("unchecked")
	private ViewElementBuilder<ViewElement> createItemTemplate( EntityPropertyDescriptor propertyDescriptor, ViewElementMode viewElementMode ) {
		EntityPropertyDescriptor memberDescriptor = propertyDescriptor.getPropertyRegistry().getProperty( propertyDescriptor.getName() + "[]" );

		return entityViewElementBuilderService.getElementBuilder( memberDescriptor, ViewElementMode.FORM_WRITE );
		/*
		EntityPropertySelector selector = retrieveMembersSelector( propertyDescriptor );
		EntityPropertyRegistry propertyRegistry = propertyDescriptor.getPropertyRegistry();

		val template = BootstrapUiBuilders.container();
		if ( selector != null && propertyRegistry != null ) {
			for ( EntityPropertyDescriptor member : propertyRegistry.select( selector ) ) {
				ViewElementBuilder memberBuilder = entityViewElementBuilderService.getElementBuilder( member, viewElementMode );

				if ( memberBuilder != null ) {
					template.add( memberBuilder );
				}
			}
		}

		return template;*/
	}

	private EntityPropertySelector retrieveMembersSelector( EntityPropertyDescriptor descriptor ) {
		EntityPropertySelector selector = descriptor.getAttribute( EntityAttributes.FIELDSET_PROPERTY_SELECTOR, EntityPropertySelector.class );

		if ( selector == null && PropertyPersistenceMetadata.isEmbeddedProperty( descriptor ) ) {
			selector = new EntityPropertySelector( descriptor.getName() + "[].*" );
		}

		return selector;
	}
}
