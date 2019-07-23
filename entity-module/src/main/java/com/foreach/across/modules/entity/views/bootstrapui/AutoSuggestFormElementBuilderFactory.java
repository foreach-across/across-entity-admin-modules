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
import com.foreach.across.modules.bootstrapui.elements.autosuggest.AutoSuggestFormElementBuilder;
import com.foreach.across.modules.bootstrapui.elements.autosuggest.AutoSuggestFormElementConfiguration;
import com.foreach.across.modules.entity.autosuggest.AutoSuggestDataEndpoint;
import com.foreach.across.modules.entity.autosuggest.AutoSuggestDataSet;
import com.foreach.across.modules.entity.conditionals.ConditionalOnBootstrapUI;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderFactorySupport;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.PropertyPlaceholderTextPostProcessor;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.RequiredControlPostProcessor;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

import static com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders.autosuggest;
import static com.foreach.across.modules.bootstrapui.elements.autosuggest.AutoSuggestFormElementConfiguration.withDataSet;

/**
 * Creates an autosuggest control for a property value.
 * Requires a valid autosuggest configuration to be present on the property descriptor or on the entity type it represents.
 *
 * @author Arne Vandamme
 * @see com.foreach.across.modules.bootstrapui.elements.autosuggest.AutoSuggestFormElementBuilder
 * @since 3.4.0
 */
@ConditionalOnBootstrapUI
@Component
@RequiredArgsConstructor
public class AutoSuggestFormElementBuilderFactory extends EntityViewElementBuilderFactorySupport<AutoSuggestFormElementBuilder>
{
	private final EntityRegistry entityRegistry;
	private final AutoSuggestDataEndpoint autoSuggestDataEndpoint;

	@Override
	public boolean supports( String viewElementType ) {
		return BootstrapUiElements.AUTOSUGGEST.equals( viewElementType );
	}

	@Override
	protected AutoSuggestFormElementBuilder createInitialBuilder( EntityPropertyDescriptor propertyDescriptor,
	                                                              ViewElementMode viewElementMode,
	                                                              String viewElementType ) {
		return autosuggest()
				.name( propertyDescriptor.getName() )
				.controlName( propertyDescriptor.getName() )
				.configuration( resolveAutoSuggestConfiguration( propertyDescriptor ) )
				.postProcessor( new RequiredControlPostProcessor<>() )
				.postProcessor( new PropertyPlaceholderTextPostProcessor<>() )
				.postProcessor( EntityViewElementUtils.controlNamePostProcessor( propertyDescriptor ) )
				.postProcessor( ( viewElementBuilderContext, autoSuggestFormElement ) -> {
					Object propertyValue = EntityViewElementUtils.currentPropertyValue( viewElementBuilderContext );

					if ( propertyValue != null ) {
						Function<Object, AutoSuggestDataSet.Result> transformer = resolveAutoSuggestResultTransformer( propertyDescriptor );
						AutoSuggestDataSet.Result result = transformer.apply( propertyValue );
						autoSuggestFormElement.setValue( result.getId() );
						autoSuggestFormElement.setText( result.getLabel() );
					}
				} );
	}

	private Function<Object, AutoSuggestDataSet.Result> resolveAutoSuggestResultTransformer( EntityPropertyDescriptor propertyDescriptor ) {
		return propertyDescriptor.getAttribute( AutoSuggestDataSet.ResultTransformer.class );
	}

	private AutoSuggestFormElementConfiguration resolveAutoSuggestConfiguration( EntityPropertyDescriptor propertyDescriptor ) {
		String dataSetId = propertyDescriptor.getAttribute( AutoSuggestDataSet.NAME, String.class );
		return withDataSet( ds -> ds.remoteUrl( autoSuggestDataEndpoint.getDataSet( dataSetId ).suggestionsUrl() ) );
	}
}
