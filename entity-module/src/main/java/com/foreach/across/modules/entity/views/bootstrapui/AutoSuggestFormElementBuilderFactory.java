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
import com.foreach.across.modules.bootstrapui.elements.*;
import com.foreach.across.modules.bootstrapui.elements.autosuggest.AutoSuggestFormElement;
import com.foreach.across.modules.bootstrapui.elements.autosuggest.AutoSuggestFormElementBuilder;
import com.foreach.across.modules.bootstrapui.elements.autosuggest.AutoSuggestFormElementConfiguration;
import com.foreach.across.modules.entity.autosuggest.AutoSuggestDataAttributeRegistrar;
import com.foreach.across.modules.entity.autosuggest.AutoSuggestDataEndpoint;
import com.foreach.across.modules.entity.autosuggest.AutoSuggestDataSet;
import com.foreach.across.modules.entity.bind.EntityPropertyBinder;
import com.foreach.across.modules.entity.bind.ListEntityPropertyBinder;
import com.foreach.across.modules.entity.conditionals.ConditionalOnBootstrapUI;
import com.foreach.across.modules.entity.query.EQGroup;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.util.EntityTypeDescriptor;
import com.foreach.across.modules.entity.util.EntityUtils;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderFactorySupport;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.PropertyPlaceholderTextPostProcessor;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.RequiredControlPostProcessor;
import com.foreach.across.modules.entity.views.processors.EntityQueryFilterProcessor;
import com.foreach.across.modules.entity.views.processors.query.EntityQueryFilterControlUtils;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementBuilderSupport;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import com.foreach.across.modules.web.ui.elements.AbstractNodeViewElement;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import static com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders.*;
import static com.foreach.across.modules.bootstrapui.elements.autosuggest.AutoSuggestFormElementConfiguration.DEFAULT_DATASET;
import static com.foreach.across.modules.entity.views.processors.query.EntityQueryFilterControlUtils.setAttribute;

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
public class AutoSuggestFormElementBuilderFactory extends EntityViewElementBuilderFactorySupport<ViewElementBuilder>
{
	private final EntityRegistry entityRegistry;
	private final ObjectProvider<AutoSuggestDataEndpoint> autoSuggestDataEndpoint;

	@Override
	public boolean supports( String viewElementType ) {
		return BootstrapUiElements.AUTOSUGGEST.equals( viewElementType );
	}

	@Override
	protected ViewElementBuilder createInitialBuilder( EntityPropertyDescriptor propertyDescriptor,
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

		AutoSuggestFormElementBuilder autoSuggestControl
				= createSingleValueControl( propertyDescriptor, viewElementMode, controlSettings, controlConfiguration );

		if ( controlSettings.multiValue || viewElementMode.isForMultiple() ) {
			return createMultiValueControl( propertyDescriptor, autoSuggestControl, controlSettings, viewElementMode );
		}

		return autoSuggestControl
				.postProcessor( ( viewElementBuilderContext, autoSuggestFormElement ) -> {
					Object propertyValue = EntityViewElementUtils.currentPropertyValue( viewElementBuilderContext, propertyDescriptor.getPropertyType() );

					if ( propertyValue != null ) {
						AutoSuggestDataSet.ResultTransformer resultTransformer = controlSettings.resultTransformer;
						AutoSuggestDataSet.Result result = resultTransformer.transformToResult( propertyValue );
						autoSuggestFormElement.setValue( result.getId() );
						autoSuggestFormElement.setText( result.getLabel() );
					}
				} )
				.postProcessor( addEntityQueryAttributes( propertyDescriptor, viewElementMode ) );
	}

	private ViewElementBuilder createMultiValueControl( EntityPropertyDescriptor propertyDescriptor,
	                                                    AutoSuggestFormElementBuilder autoSuggestControl,
	                                                    Settings controlSettings,
	                                                    ViewElementMode viewElementMode ) {
		return div()
				.data( "bootstrapui-adapter-type", "multi-value-autosuggest" )
				.css( "multi-value-autosuggest", "js-multi-value-autosuggest" )
				.add( autoSuggestControl.data( "role", "control" ) )
				.postProcessor( addEntityQueryAttributes( propertyDescriptor, viewElementMode ) )
				.postProcessor( ( ( builderContext, wrapper ) -> {
					String removeItemMessage = builderContext.getMessage( "properties." + propertyDescriptor.getName() + "[removeItem]", "" );
					Collection<?> items = retrieveItems( builderContext );

					AutoSuggestDataSet.ResultTransformer resultTransformer = controlSettings.resultTransformer;

					AutoSuggestFormElement autoSuggest = wrapper.find( propertyDescriptor.getName(), AutoSuggestFormElement.class )
					                                            .orElseThrow( () -> new IllegalStateException(
							                                            "Multi-value auto-suggest requires an AutoSuggestFormElement" ) );
					String controlName = autoSuggest.getControlName();
					autoSuggest.setControlName( "_" + controlName );

					TableViewElement table = new TableViewElement();
					table.setStyles( Collections.singleton( Style.Table.STRIPED ) );
					table.addCssClass( "multi-value-autosuggest-selected" );
					table.setAttribute( "data-role", "items" );

					if ( items.isEmpty() ) {
						table.addCssClass( "hidden" );
					}

					TableViewElement.Row hidden = new TableViewElement.Row();
					hidden.addCssClass( "hidden" );
					table.addChild( hidden );

					items.forEach( item -> {
						AutoSuggestDataSet.Result result = resultTransformer.transformToResult( item );
						table.addChild( createResultRow( controlName, result.getId(), result.getLabel(), removeItemMessage ) );
					} );

					wrapper.addChild( table );

					wrapper.addChild(
							BootstrapUiBuilders.script( MediaType.TEXT_HTML )
							                   .data( "role", "edit-item-template" )
							                   .data( "next-item-index", System.currentTimeMillis() )
							                   .add( createResultRow( controlName, "{{id}}", "{{label}}", removeItemMessage ) )
							                   .build( builderContext )
					);
				} ) );
	}

	@SuppressWarnings("SuspiciousSystemArraycopy")
	private Collection retrieveItems( ViewElementBuilderContext builderContext ) {
		EntityPropertyBinder binder = EntityViewElementUtils.currentPropertyBinder( builderContext );

		if ( binder instanceof ListEntityPropertyBinder ) {
			return ( (ListEntityPropertyBinder) binder ).getItemList().stream().map( EntityPropertyBinder::getValue ).collect( Collectors.toList() );
		}

		Object items = EntityViewElementUtils.currentPropertyValue( builderContext );

		if ( items instanceof Collection ) {
			return (Collection) items;
		}
		else if ( items != null && items.getClass().isArray() ) {
			int length = Array.getLength( items );
			Object[] copy = new Object[length];
			System.arraycopy( items, 0, copy, 0, length );
			return Arrays.asList( copy );
		}

		return Collections.emptyList();
	}

	private TableViewElement.Row createResultRow( String controlName, Object id, String label, String removeItemMessage ) {
		TableViewElement.Row row = new TableViewElement.Row();
		row.setAttribute( "data-role", "item" );
		TableViewElement.Cell cell = new TableViewElement.Cell();
		cell.addChild( TextViewElement.text( label ) );
		HiddenFormElement valueHolder = new HiddenFormElement();
		valueHolder.setControlName( controlName );
		valueHolder.setValue( id );
		cell.addChild( valueHolder );
		row.addChild( cell );

		TableViewElement.Cell actions = new TableViewElement.Cell();
		actions.addCssClass( "row-actions" );

		LinkViewElement link = new LinkViewElement();
		link.setAttribute( "data-action", "remove-item" );
		link.setTitle( removeItemMessage );
		link.addChild( glyphIcon( GlyphIcon.REMOVE ) );
		actions.addChild( link );

		row.addChild( actions );
		return row;
	}

	private AutoSuggestFormElementBuilder createSingleValueControl( EntityPropertyDescriptor propertyDescriptor,
	                                                                ViewElementMode viewElementMode,
	                                                                Settings controlSettings,
	                                                                AutoSuggestFormElementConfiguration controlConfiguration ) {
		return autosuggest()
				.name( propertyDescriptor.getName() )
				.controlName( propertyDescriptor.getName() )
				.configuration( controlConfiguration )
				.postProcessor( new RequiredControlPostProcessor<>() )
				.postProcessor( new PropertyPlaceholderTextPostProcessor<>() )
				.postProcessor( EntityViewElementUtils.controlNamePostProcessor( propertyDescriptor ) );
	}

	private <U extends AbstractNodeViewElement> ViewElementPostProcessor<U> addEntityQueryAttributes( EntityPropertyDescriptor propertyDescriptor,
	                                                                                                  ViewElementMode viewElementMode ) {
		return ( builderContext, element ) -> {
			if ( ViewElementMode.FILTER_CONTROL.equals( viewElementMode.forSingle() ) ) {
				element.addCssClass( EntityQueryFilterProcessor.ENTITY_QUERY_CONTROL_MARKER );
				ViewElementBuilderSupport.ElementOrBuilder wrappedElement = ViewElementBuilderSupport.ElementOrBuilder.wrap( element );
				EntityQueryFilterControlUtils.configureControlSettings( wrappedElement, propertyDescriptor );
				if ( viewElementMode.isForMultiple() ) {
					setAttribute( wrappedElement, EntityQueryFilterControlUtils.FilterControlAttributes.TYPE, EQGroup.class.getSimpleName() );
				}
			}
		};
	}

	private Settings buildAutoSuggestControlSettings( EntityPropertyDescriptor descriptor ) {
		EntityTypeDescriptor entityTypeDescriptor = EntityUtils.resolveEntityTypeDescriptor( descriptor.getPropertyTypeDescriptor(), entityRegistry );

		Settings settings = Settings.from( descriptor );
		settings.multiValue = entityTypeDescriptor.isCollection();

		if ( !settings.isComplete() ) {
			Class<?> entityType = entityTypeDescriptor.isTargetTypeResolved() ? entityTypeDescriptor.getSimpleTargetType() : descriptor.getPropertyType();

			EntityConfiguration<?> targetEntityConfiguration = entityRegistry.getEntityConfiguration( entityType );

			Settings entitySettings = null;

			if ( targetEntityConfiguration != null ) {
				entitySettings = Settings.from( targetEntityConfiguration );
			}

			if ( entitySettings != null ) {
				settings.merge( entitySettings );
			}
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
		private boolean multiValue;
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
