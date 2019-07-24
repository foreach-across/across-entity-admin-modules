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

package com.foreach.across.modules.entity.autosuggest;

import com.foreach.across.core.support.ReadableAttributes;
import com.foreach.across.modules.bootstrapui.elements.autosuggest.AutoSuggestFormElementConfiguration;
import com.foreach.across.modules.entity.config.AttributeRegistrar;
import com.foreach.across.modules.entity.query.EntityQueryFacadeResolver;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.function.Consumer;

/**
 * Helper that allows registering simple auto-suggest datasets and attaching them
 * immediately to the scope of an entity configuration or property.
 * <p/>
 * This will register the dataset in the {@link AutoSuggestDataEndpoint} (which must be
 * available) and attache the id of the dataset as attribute on the descriptor or configuration.
 *
 * @author Arne Vandamme
 * @since 3.4.0
 */
@Service
@Lazy
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class AutoSuggestDataAttributeRegistrar
{
	/**
	 * The id of the auto-suggest dataset that should be used when building a
	 * {@link com.foreach.across.modules.bootstrapui.elements.autosuggest.AutoSuggestFormElement}.
	 *
	 * @see com.foreach.across.modules.entity.views.bootstrapui.AutoSuggestFormElementBuilderFactory
	 */
	public static final String DATASET_ID = "autoSuggestDataSetId";

	private final ObjectProvider<AutoSuggestDataEndpoint> autoSuggestDataEndpoint;
	private final EntityRegistry entityRegistry;
	private final EntityQueryFacadeResolver entityQueryFacadeResolver;
	private final ConversionService mvcConversionService;

	/**
	 * Register a simple dataset that uses an EQL statement to fetch the results.
	 * The entity configuration of the target type wil be used for query execution
	 * and result transformation (using the available entity model). The target entity
	 * configuration will be derived from the context on which the attribute is being
	 * registered (either {@link EntityConfiguration} or {@link EntityPropertyDescriptor}.
	 * <p/>
	 * The EQL statement can hold a {@code \{0\} token} where the actual search string will be inserted.
	 *
	 * @param suggestionsEql eql to fetch the suggestions
	 * @return attribute registrar
	 * @see EntityQueryAutoSuggestData
	 */
	public <U extends ReadableAttributes> AttributeRegistrar<U> entityQuery( @NonNull String suggestionsEql ) {
		return entityQuery( ds -> ds.setSuggestionsEql( suggestionsEql ) );
	}

	/**
	 * Register an {@link EntityQueryAutoSuggestData} dataset that will be initialized upon first use.
	 *
	 * @param dataSetConsumer to customize the dataset properties
	 * @return attribute registrar
	 * @see EntityQueryAutoSuggestData
	 */
	public <U extends ReadableAttributes> AttributeRegistrar<U> entityQuery( @NonNull Consumer<EntityQueryAutoSuggestData> dataSetConsumer ) {
		return ( owner, attributes ) -> {
			EntityQueryAutoSuggestData autoSuggestData = new EntityQueryAutoSuggestData()
					.setInitializer( dataSet -> initializeEntityQueryAutoSuggestData( owner, dataSet ) );

			dataSetConsumer.accept( autoSuggestData );

			assignDataSetIdIfNecessary( autoSuggestData, owner );

			AutoSuggestDataEndpoint endpoint = autoSuggestDataEndpoint.getIfAvailable();
			Assert.notNull( endpoint, "No AutoSuggestEndpoint is available for registering datasets" );
			endpoint.registerDataSet( autoSuggestData.getDataSetId(), autoSuggestData );

			attributes.setAttribute( DATASET_ID, autoSuggestData.getDataSetId() );
		};
	}

	/**
	 * Registers or customizes an {@link AutoSuggestFormElementConfiguration} which determines the default settings
	 * for the auto-suggest control (for example the minimum length before fetching results).
	 *
	 * @param controlConsumer consumer to customize the configuration
	 * @return attribute registrar
	 */
	public <U extends ReadableAttributes> AttributeRegistrar<U> control( @NonNull Consumer<AutoSuggestFormElementConfiguration> controlConsumer ) {
		return ( owner, attributes ) -> {
			AutoSuggestFormElementConfiguration cfg = attributes.getAttribute( AutoSuggestFormElementConfiguration.class );
			if ( cfg == null ) {
				cfg = new AutoSuggestFormElementConfiguration();
			}
			controlConsumer.accept( cfg );

			attributes.setAttribute( AutoSuggestFormElementConfiguration.class, cfg );
		};
	}

	private void assignDataSetIdIfNecessary( EntityQueryAutoSuggestData autoSuggestData, Object owner ) {
		if ( StringUtils.isEmpty( autoSuggestData.getDataSetId() ) ) {
			autoSuggestData.setDataSetId( resolveDataSetId( owner ) );
		}
	}

	private String resolveDataSetId( Object owner ) {
		if ( owner instanceof EntityConfiguration ) {
			return "entity-" + ( (EntityConfiguration) owner ).getName();
		}

		if ( owner instanceof EntityPropertyDescriptor ) {
			EntityPropertyDescriptor descriptor = (EntityPropertyDescriptor) owner;
			return "property-" + descriptor.getPropertyRegistry().getId() + "." + descriptor.getName();
		}

		throw new IllegalArgumentException( "Only entity configurations or properties can have auto-suggest data attributes configured" );
	}

	/**
	 * Attempts to resolve all unknown properties and activate the dataset correctly.
	 */
	private void initializeEntityQueryAutoSuggestData( Object owner, EntityQueryAutoSuggestData dataSet ) {
		if ( dataSet.getConversionService() == null ) {
			dataSet.setConversionService( mvcConversionService );
		}

		if ( dataSet.getEntityConfiguration() == null ) {
			if ( owner instanceof EntityPropertyDescriptor ) {
				EntityPropertyDescriptor propertyDescriptor = (EntityPropertyDescriptor) owner;
				dataSet.setEntityConfiguration( entityRegistry.getEntityConfiguration( propertyDescriptor.getPropertyType() ) );
			}
			else if ( owner instanceof EntityConfiguration ) {
				dataSet.setEntityConfiguration( (EntityConfiguration) owner );
			}
			else {
				throw new IllegalArgumentException( "AutoSuggestData attributes are only supported on EntityConfiguration or EntityPropertyDescriptor" );
			}
		}

		if ( dataSet.getEntityQueryFacade() == null ) {
			dataSet.setEntityQueryFacade( entityQueryFacadeResolver.forEntityConfiguration( dataSet.getEntityConfiguration() ) );
		}

		dataSet.setInitializer( null );
	}
}
