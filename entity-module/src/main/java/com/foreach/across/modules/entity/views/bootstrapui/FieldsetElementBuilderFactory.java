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
import com.foreach.across.modules.entity.conditionals.ConditionalOnBootstrapUI;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertySelector;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderFactorySupport;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderService;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.elements.ViewElementFieldset;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.FieldsetDescriptionTextPostProcessor;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.FieldsetHelpTextPostProcessor;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.FieldsetTooltipTextPostProcessor;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementBuilderSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Builds a {@link ViewElementFieldset} for a property.
 * <p>
 * The property can have a {@link EntityAttributes#FIELDSET_PROPERTY_SELECTOR} attribute
 * specifying the selector that should be used to fetch the members of the fieldset.
 * If none is available a default will be created for all child properties.
 * </p>
 * <p>
 * In addition the {@link EntityPropertyDescriptor} may contain a custom template function
 * as the {@link ViewElementFieldset#TEMPLATE} attribute value. If this is the case, that
 * template will be used for rendering the fieldset.
 * </p>
 * When requesting a fieldset in {@link ViewElementMode#FORM_WRITE}, tooltip, description and help
 * text for the property will be added to {@link ViewElementFieldset#getTitle()}, {@link ViewElementFieldset#getHeader()}
 * and {@link ViewElementFieldset#getFooter()} respectively.
 *
 * @author Arne Vandamme
 * @see ViewElementFieldset
 * @see com.foreach.across.modules.entity.EntityAttributes#FIELDSET_PROPERTY_SELECTOR
 * @see FieldsetTooltipTextPostProcessor
 * @see FieldsetDescriptionTextPostProcessor
 * @see FieldsetHelpTextPostProcessor
 */
@ConditionalOnBootstrapUI
@Component
@RequiredArgsConstructor
@Slf4j
public class FieldsetElementBuilderFactory extends EntityViewElementBuilderFactorySupport<ViewElementBuilder<ViewElementFieldset>>
{
	private final EntityViewElementBuilderService entityViewElementBuilderService;

	@Override
	public boolean supports( String viewElementType ) {
		return BootstrapUiElements.FIELDSET.equals( viewElementType );
	}

	@Override
	protected ViewElementBuilder<ViewElementFieldset> createInitialBuilder( EntityPropertyDescriptor propertyDescriptor,
	                                                                        ViewElementMode viewElementMode,
	                                                                        String viewElementType ) {
		ViewElementBuilder labelText = entityViewElementBuilderService.getElementBuilder( propertyDescriptor, ViewElementMode.LABEL );

		FieldsetBuilder fieldset = new FieldsetBuilder().name( propertyDescriptor.getName() );

		fieldset.postProcessor( ( ctx, f ) -> {
			Function<ViewElementFieldset, ? extends ViewElement> template = resolveTemplate( propertyDescriptor );

			if ( template != null ) {
				f.setTemplate( template );
			}
			if ( labelText != null ) {
				f.getTitle().addChild( labelText.build( ctx ) );
			}
		} );

		fieldset.postProcessor( ( ctx, f ) -> {
			EntityPropertySelector selector = retrieveMembersSelector( propertyDescriptor );
			EntityPropertyRegistry propertyRegistry = propertyDescriptor.getPropertyRegistry();

			if ( selector != null && propertyRegistry != null ) {
				for ( EntityPropertyDescriptor member : propertyRegistry.select( selector ) ) {
					ViewElementBuilder memberBuilder = entityViewElementBuilderService.getElementBuilder( member, viewElementMode );

					if ( memberBuilder != null ) {
						f.getBody().addChild( memberBuilder.build( ctx ) );
					}
				}
			}
		} );

		if ( ViewElementMode.FORM_WRITE.equals( viewElementMode.forSingle() ) ) {
			fieldset.postProcessor( new FieldsetDescriptionTextPostProcessor<>() )
			        .postProcessor( new FieldsetTooltipTextPostProcessor<>() )
			        .postProcessor( new FieldsetHelpTextPostProcessor<>() );
		}

		return fieldset;
	}

	private Function<ViewElementFieldset, ? extends ViewElement> resolveTemplate( EntityPropertyDescriptor descriptor ) {
		Function<ViewElementFieldset, ? extends ViewElement> template = descriptor.getAttribute( ViewElementFieldset.TEMPLATE, Function.class );

		if ( template == null ) {
			return EntityPropertyRegistry.isMemberPropertyDescriptor( descriptor )
					? ViewElementFieldset.TEMPLATE_BODY_ONLY : ViewElementFieldset.TEMPLATE_FIELDSET;
		}
		return template;
	}

	private EntityPropertySelector retrieveMembersSelector( EntityPropertyDescriptor descriptor ) {
		EntityPropertySelector selector = descriptor.getAttribute( EntityAttributes.FIELDSET_PROPERTY_SELECTOR, EntityPropertySelector.class );

		if ( selector == null ) {
			// create a default selector - assume the requested type has child properties
			LOG.trace( "No fieldset property selector specified for '{}' - selecting all child properties", descriptor.getName() );
			selector = new EntityPropertySelector( descriptor.getName() + ".*" );
		}

		return selector;
	}

	private static class FieldsetBuilder extends ViewElementBuilderSupport<ViewElementFieldset, FieldsetBuilder>
	{
		@Override
		protected ViewElementFieldset createElement( ViewElementBuilderContext builderContext ) {
			return apply( new ViewElementFieldset(), builderContext );
		}
	}
}
