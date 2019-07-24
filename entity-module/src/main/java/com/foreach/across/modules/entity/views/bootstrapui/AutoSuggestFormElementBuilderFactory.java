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

import com.foreach.across.core.support.ReadableAttributes;
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiElements;
import com.foreach.across.modules.bootstrapui.elements.autosuggest.AutoSuggestFormElementBuilder;
import com.foreach.across.modules.bootstrapui.elements.autosuggest.AutoSuggestFormElementConfiguration;
import com.foreach.across.modules.entity.autosuggest.AutoSuggestDataAttributeRegistrar;
import com.foreach.across.modules.entity.autosuggest.AutoSuggestDataEndpoint;
import com.foreach.across.modules.entity.autosuggest.AutoSuggestDataSet;
import com.foreach.across.modules.entity.conditionals.ConditionalOnBootstrapUI;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderFactorySupport;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.PropertyPlaceholderTextPostProcessor;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.RequiredControlPostProcessor;
import com.foreach.across.modules.entity.views.processors.EntityQueryFilterProcessor;
import com.foreach.across.modules.entity.views.processors.query.EntityQueryFilterControlUtils;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.modules.web.ui.ViewElementBuilderSupport;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import static com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders.autosuggest;
import static com.foreach.across.modules.bootstrapui.elements.autosuggest.AutoSuggestFormElementConfiguration.DEFAULT_DATASET;

/**
 * Creates an auto-suggest control for a property value.
 * Requires a valid auto-suggest configuration to be present on the property descriptor or on the entity type it represents.
 * <p/>
 * Any {@link AutoSuggestDataAttributeRegistrar#DATASET_ID} attribute on the descriptor will reconfigure the default dataset
 * attached to the control configuration. If you do not want this, set the attribute explicitly to an empty string value, this
 * will ensure that datasets configured on target entity configuration will also be ignored.
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
	private final ObjectProvider<AutoSuggestDataEndpoint> autoSuggestDataEndpoint;

	@Override
	public boolean supports( String viewElementType ) {
		return BootstrapUiElements.AUTOSUGGEST.equals( viewElementType );
	}

	@Override
	protected AutoSuggestFormElementBuilder createInitialBuilder( EntityPropertyDescriptor propertyDescriptor,
	                                                              ViewElementMode viewElementMode,
	                                                              String viewElementType ) {
		Settings controlSettings = buildAutoSuggestControlSettings( propertyDescriptor );

		AutoSuggestFormElementConfiguration controlConfiguration = controlSettings.controlConfiguration != null
				? controlSettings.controlConfiguration.translate( s -> s )
				: new AutoSuggestFormElementConfiguration();

		if ( controlSettings.hasDataSet() ) {
			controlConfiguration.withDataSet( DEFAULT_DATASET, ds -> ds.remoteUrl( controlSettings.dataSet.suggestionsUrl() ) );

			if ( controlSettings.dataSet.isPrefetchSupported() ) {
				controlConfiguration.withDataSet( DEFAULT_DATASET, ds -> ds.prefetchUrl( controlSettings.dataSet.prefetchUrl() ) );
			}
		}

		return autosuggest()
				.name( propertyDescriptor.getName() )
				.controlName( propertyDescriptor.getName() )
				.configuration( controlConfiguration )
				.postProcessor( new RequiredControlPostProcessor<>() )
				.postProcessor( new PropertyPlaceholderTextPostProcessor<>() )
				.postProcessor( EntityViewElementUtils.controlNamePostProcessor( propertyDescriptor ) )
				.postProcessor( ( viewElementBuilderContext, autoSuggestFormElement ) -> {
					Object propertyValue = EntityViewElementUtils.currentPropertyValue( viewElementBuilderContext, propertyDescriptor.getPropertyType() );

					if ( propertyValue != null ) {
						AutoSuggestDataSet.ResultTransformer resultTransformer = controlSettings.resultTransformer;
						AutoSuggestDataSet.Result result = resultTransformer.transformToResult( propertyValue );
						autoSuggestFormElement.setValue( result.getId() );
						autoSuggestFormElement.setText( result.getLabel() );
					}
				} )
				.postProcessor(
						( ( builderContext, element ) -> {
							if ( ViewElementMode.FILTER_CONTROL.equals( viewElementMode.forSingle() ) ) {
								element.addCssClass( EntityQueryFilterProcessor.ENTITY_QUERY_CONTROL_MARKER );
								EntityQueryFilterControlUtils.configureControlSettings( ViewElementBuilderSupport.ElementOrBuilder.wrap( element ), propertyDescriptor );
							}
						} )
				);
	}

	private Settings buildAutoSuggestControlSettings( EntityPropertyDescriptor descriptor ) {
		Settings settings = Settings.from( descriptor );

		if ( settings.isComplete() ) {
			return settings;
		}

		EntityConfiguration<?> targetEntityConfiguration = entityRegistry.getEntityConfiguration( descriptor.getPropertyType() );

		Settings entitySettings = null;

		if ( targetEntityConfiguration != null ) {
			entitySettings = Settings.from( targetEntityConfiguration );
		}

		if ( entitySettings != null ) {
			settings.merge( entitySettings );
		}

		if ( settings.hasDataSetId() && StringUtils.isNotEmpty( settings.dataSetId ) ) {
			AutoSuggestDataEndpoint endpoint = autoSuggestDataEndpoint.getIfAvailable();
			Assert.notNull( endpoint, "An AutoSuggestDataEndpoint is required if using attributes to specify the dataset" );

			settings.dataSet = endpoint.getDataSet( settings.dataSetId );
			Assert.notNull( settings.dataSet, "No registered AutoSuggestDataSet with id " + settings.dataSetId );
		}

		Assert.isTrue( settings.hasControlConfiguration() || settings.hasDataSet(),
		               "Either data set id or AutoSuggestFormElementConfiguration attribute is required for using auto-suggest controls" );

		if ( !settings.hasResultTransformer() ) {
			if ( settings.dataSet != null && settings.dataSet.getDataSet() instanceof AutoSuggestDataSet.ResultTransformer ) {
				settings.resultTransformer = (AutoSuggestDataSet.ResultTransformer) settings.dataSet.getDataSet();
			}
			Assert.notNull( settings.resultTransformer, "No AutoSuggestDataSet.ResultTransformer could be resolved" );
		}

		return settings;
	}

	private static class Settings
	{
		private AutoSuggestFormElementConfiguration controlConfiguration;
		private String dataSetId;
		private AutoSuggestDataSet.ResultTransformer resultTransformer;
		private AutoSuggestDataEndpoint.MappedDataSet dataSet;

		boolean hasDataSetId() {
			return dataSetId != null;
		}

		boolean hasDataSet() {
			return dataSet != null;
		}

		boolean hasResultTransformer() {
			return resultTransformer != null;
		}

		boolean hasControlConfiguration() {
			return controlConfiguration != null;
		}

		boolean isComplete() {
			return ( hasControlConfiguration() || hasDataSetId() ) && hasResultTransformer();
		}

		static Settings from( ReadableAttributes attributes ) {
			Settings settings = new Settings();
			settings.controlConfiguration = attributes.getAttribute( AutoSuggestFormElementConfiguration.class );
			settings.dataSetId = attributes.getAttribute( AutoSuggestDataAttributeRegistrar.DATASET_ID, String.class );
			settings.resultTransformer = attributes.getAttribute( AutoSuggestDataSet.ResultTransformer.class );
			return settings;
		}

		public void merge( Settings entitySettings ) {
			if ( !hasResultTransformer() ) {
				resultTransformer = entitySettings.resultTransformer;
			}

			if ( !hasDataSetId() && !hasControlConfiguration() ) {
				// control configuration is only copied if no dataset
				controlConfiguration = entitySettings.controlConfiguration;
			}

			if ( !hasDataSetId() ) {
				dataSetId = entitySettings.dataSetId;
			}
		}
	}
}
