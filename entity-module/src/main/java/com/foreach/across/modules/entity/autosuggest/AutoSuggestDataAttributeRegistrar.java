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
import com.foreach.across.core.support.WritableAttributes;
import com.foreach.across.modules.bootstrapui.elements.autosuggest.AutoSuggestFormElementConfiguration;
import com.foreach.across.modules.entity.config.AttributeRegistrar;
import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.query.EntityQueryFacade;
import com.foreach.across.modules.entity.query.EntityQueryFacadeResolver;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityModel;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.util.EntityTypeDescriptor;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.foreach.across.modules.entity.util.EntityUtils.resolveEntityTypeDescriptor;

/**
 * Helper that allows registering simple entity (query) based auto-suggest datasets
 * and attaching them immediately to the scope of an entity configuration or property.
 * <p/>
 * This will register the dataset in the {@link AutoSuggestDataEndpoint} (which must be
 * available) and attach the id of the dataset as attribute on the descriptor or configuration.
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
	 * Registers the dataset with the given id as auto-suggest datasource.
	 * This is the equivalent of directly setting the {@link #DATASET_ID} attribute.
	 *
	 * @param dataSetId id of the dataset
	 * @return attribute registrar
	 */
	public <U extends ReadableAttributes> DataSetRegistrar<U> dataSetId( @NonNull String dataSetId ) {
		return new DataSetRegistrar<>( ( owner, attributes ) -> attributes.setAttribute( DATASET_ID, dataSetId ) );
	}

	/**
	 * Register a simple dataset that uses an EQL statement to fetch the results.
	 * The entity configuration of the target type wil be used for query execution
	 * and result transformation (using the available entity model). The target entity
	 * configuration will be derived from the context on which the attribute is being
	 * registered (either {@link EntityConfiguration} or {@link EntityPropertyDescriptor}.
	 * <p/>
	 * The EQL statement can hold a {@code \{0\} token} where the actual search string will be inserted.
	 * <p/>
	 * The default number of results will be limited to 50.
	 *
	 * @param suggestionsEql eql to fetch the suggestions
	 * @return attribute registrar
	 * @see EntityDataSetConfiguration
	 */
	public <U extends ReadableAttributes> DataSetRegistrar<U> entityQuery( @NonNull String suggestionsEql ) {
		return entityQuery( ds -> ds.suggestionsEql( suggestionsEql ) );
	}

	/**
	 * Register an entity configuration based {@link InitializingAutoSuggestDataSet} dataset
	 * that will be initialized upon first use. The default number of results will be limited to 50.
	 * If no {@link EntityDataSetConfiguration#entityType(Class)} is specified manually, it will be
	 * derived from the context on which the attribute is being registered.
	 *
	 * @param dataSetConsumer to customize the dataset properties
	 * @return attribute registrar
	 * @see EntityDataSetConfiguration
	 */
	@SuppressWarnings("unchecked")
	public <U extends ReadableAttributes> DataSetRegistrar<U> entityQuery( @NonNull Consumer<EntityDataSetConfiguration<? super Object>> dataSetConsumer ) {
		return new DataSetRegistrar<>( ( owner, attributes ) -> {
			EntityDataSetConfiguration definition = new EntityDataSetConfiguration<>().maximumResults( 50 );
			dataSetConsumer.accept( definition );

			InitializingAutoSuggestDataSet autoSuggestData = new InitializingAutoSuggestDataSet();
			autoSuggestData.setInitializer( dataSet -> initializeEntityConfigurationAutoSuggestData( owner, dataSet, definition ) );

			String dataSetId = definition.dataSetId;
			if ( dataSetId == null ) {
				dataSetId = resolveDataSetId( owner );
			}

			AutoSuggestDataEndpoint endpoint = autoSuggestDataEndpoint.getIfAvailable();
			Assert.notNull( endpoint, "No AutoSuggestEndpoint is available for registering datasets" );
			endpoint.registerDataSet( dataSetId, autoSuggestData );

			attributes.setAttribute( DATASET_ID, dataSetId );
		} );
	}

	/**
	 * Registers or customizes only the {@link AutoSuggestFormElementConfiguration} which determines the default settings
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
	private void initializeEntityConfigurationAutoSuggestData( Object owner,
	                                                           InitializingAutoSuggestDataSet dataSet,
	                                                           EntityDataSetConfiguration<? super Object> configuration ) {
		DataSetConfigurationContext context = new DataSetConfigurationContext( owner, configuration );

		if ( configuration.resultTransformer != null ) {
			dataSet.setResultTransformer( configuration.resultTransformer::apply );
		}
		else {
			EntityModel entityModel = context.entityConfiguration().getEntityModel();
			dataSet.setResultTransformer( entity -> transformToSimpleResult( entity, entityModel ) );
		}

		BiFunction<String, String, Iterable<?>> suggestionsLoader = configuration.suggestions;

		if ( suggestionsLoader == null && configuration.suggestionsEntityQuery != null ) {
			suggestionsLoader = createEntityQuerySuggestionsLoader( configuration, context );
		}

		if ( suggestionsLoader != null ) {
			dataSet.setSuggestionsLoader( createTransformingSuggestionsLoader( dataSet, configuration, suggestionsLoader ) );
		}

		Function<String, Iterable<?>> prefetchLoader = configuration.prefetch;

		if ( prefetchLoader == null && configuration.prefetchEntityQuery != null ) {
			prefetchLoader = createEntityQueryPrefetchLoader( configuration, context );
		}

		if ( prefetchLoader != null ) {
			dataSet.setPrefetchLoader( createTransformingPrefetchLoader( dataSet, prefetchLoader ) );
		}
	}

	private BiFunction<String, String, Object> createTransformingSuggestionsLoader(
			InitializingAutoSuggestDataSet dataSet,
			EntityDataSetConfiguration<? super Object> configuration,
			BiFunction<String, String, Iterable<?>> suggestionsLoader ) {
		return ( query, controlName ) -> {
			Integer maximumResults = configuration.maximumResults;
			Iterable<?> results = suggestionsLoader.apply( query, controlName );

			return StreamSupport.stream( results.spliterator(), false )
			                    .limit( maximumResults != null ? maximumResults : Integer.MAX_VALUE )
			                    .map( dataSet.getResultTransformer()::transformToResult )
			                    .collect( Collectors.toList() );
		};
	}

	private Function<String, Object> createTransformingPrefetchLoader( InitializingAutoSuggestDataSet dataSet,
	                                                                   Function<String, Iterable<?>> prefetchLoader ) {
		return query -> {
			Iterable<?> results = prefetchLoader.apply( query );

			return StreamSupport.stream( results.spliterator(), false )
			                    .map( dataSet.getResultTransformer()::transformToResult )
			                    .collect( Collectors.toList() );
		};
	}

	private BiFunction<String, String, Iterable<?>> createEntityQuerySuggestionsLoader( EntityDataSetConfiguration<? super Object> configuration,
	                                                                                    DataSetConfigurationContext context ) {
		return ( query, controlName ) -> {
			Integer maximumResults = configuration.maximumResults;
			EntityQueryFacade entityQueryFacade = context.entityQueryFacade();

			EntityQuery entityQuery = configuration.suggestionsEntityQuery.apply( query, controlName );
			EntityQuery executableQuery = entityQueryFacade.convertToExecutableQuery( entityQuery );

			if ( maximumResults != null ) {
				return entityQueryFacade.findAll( executableQuery, PageRequest.of( 0, configuration.maximumResults ) );
			}

			return entityQueryFacade.findAll( executableQuery );
		};
	}

	private Function<String, Iterable<?>> createEntityQueryPrefetchLoader( EntityDataSetConfiguration<? super Object> configuration,
	                                                                       DataSetConfigurationContext context ) {
		return query -> {
			EntityQueryFacade entityQueryFacade = context.entityQueryFacade();

			EntityQuery entityQuery = configuration.prefetchEntityQuery.apply( query );
			EntityQuery executableQuery = entityQueryFacade.convertToExecutableQuery( entityQuery );

			return entityQueryFacade.findAll( executableQuery );
		};
	}

	@SuppressWarnings("unchecked")
	private AutoSuggestDataSet.Result transformToSimpleResult( Object entity, EntityModel entityModel ) {
		String id = mvcConversionService.convert( entityModel.getId( entity ), String.class );
		String label = entityModel.getLabel( entity );

		return new SimpleAutoSuggestDataSet.Result( id, label );
	}

	private EntityConfiguration loadEntityConfiguration( Object owner, EntityDataSetConfiguration<?> configuration ) {
		if ( configuration.entityType != null ) {
			return entityRegistry.getEntityConfiguration( configuration.entityType );
		}
		if ( owner instanceof EntityPropertyDescriptor ) {
			EntityPropertyDescriptor propertyDescriptor = (EntityPropertyDescriptor) owner;
			EntityTypeDescriptor entityTypeDescriptor = resolveEntityTypeDescriptor( propertyDescriptor.getPropertyTypeDescriptor(), entityRegistry );
			if ( !entityTypeDescriptor.isTargetTypeResolved() ) {
				throw new IllegalArgumentException( "Unable to resolve EntityConfiguration for property type " + propertyDescriptor );
			}
			return entityRegistry.getEntityConfiguration( entityTypeDescriptor.getSimpleTargetType() );
		}
		else if ( owner instanceof EntityConfiguration ) {
			return ( (EntityConfiguration) owner );
		}
		else {
			throw new IllegalArgumentException( "AutoSuggestData attributes are only supported on EntityConfiguration or EntityPropertyDescriptor" );
		}
	}

	@RequiredArgsConstructor
	private class DataSetConfigurationContext
	{
		private final Object owner;
		private final EntityDataSetConfiguration<? super Object> configuration;

		private EntityConfiguration entityConfiguration;
		private EntityQueryFacade entityQueryFacade;

		EntityConfiguration entityConfiguration() {
			if ( entityConfiguration == null ) {
				entityConfiguration = loadEntityConfiguration( owner, configuration );
			}
			return entityConfiguration;
		}

		EntityQueryFacade entityQueryFacade() {
			if ( entityQueryFacade == null ) {
				entityQueryFacade = entityQueryFacadeResolver.forEntityConfiguration( entityConfiguration() );
			}
			return entityQueryFacade;
		}
	}

	/**
	 * Holds the configuration for an {@link InitializingAutoSuggestDataSet} based on an entity configuration.
	 * An {@link #entityType(Class)} can be specified manually but if missing will be resolved from the context.
	 * Likewise if no {@link #resultTransformer(Function)} is set, the available entity model will be used.
	 * <p/>
	 * Suggestions and prefetch queries can be specified as either entity query or a function reference.
	 * <p/>
	 * The {@link #maximumResults(Integer)} property has impact on the number of suggestions
	 * that will be returned. When using an entity query, setting maximum results will also fetch results
	 * using a page request with that size.
	 */
	@Accessors(fluent = true, chain = true)
	@Setter
	public static class EntityDataSetConfiguration<T>
	{
		private String dataSetId;
		private Integer maximumResults;
		private Class<? extends T> entityType;
		private BiFunction<String, String, Iterable<? extends T>> suggestions;
		private Function<String, Iterable<? extends T>> prefetch;
		private BiFunction<String, String, EntityQuery> suggestionsEntityQuery;
		private Function<String, EntityQuery> prefetchEntityQuery;
		private Function<T, AutoSuggestDataSet.Result> resultTransformer;

		/**
		 * Specify the entity type that this dataset fetches. If not set, the entity type
		 * will be derived from the context (either the configuration or property type).
		 */
		public <U extends T> EntityDataSetConfiguration<U> entityType( Class<U> entityType ) {
			this.entityType = entityType;
			return as( entityType );
		}

		/**
		 * Case this configuration for the specific entity type.
		 * Provided for readability and less explicit casting in setter methods.
		 */
		@SuppressWarnings({ "unchecked", "unused" })
		public <U extends T> EntityDataSetConfiguration<U> as( Class<U> entityType ) {
			return (EntityDataSetConfiguration<U>) this;
		}

		public EntityDataSetConfiguration<T> suggestionsEql( @NonNull String eql ) {
			return suggestionsEntityQuery( ( search, controlName ) -> {
				String actual = replaceSearchParameter( eql, search );
				return EntityQuery.parse( actual );
			} );
		}

		public EntityDataSetConfiguration<T> prefetchEql( @NonNull String eql ) {
			return prefetchEntityQuery( ( search ) -> {
				String actual = replaceSearchParameter( eql, search );
				return EntityQuery.parse( actual );
			} );
		}

		private String replaceSearchParameter( @NonNull String eql, String search ) {
			return StringUtils.replace( eql, "{0}",
			                            StringUtils.replace(
					                            StringUtils.replace( search, "%", "\\%" ),
					                            "'", "\\'"
			                            ) );
		}
	}

	/**
	 * Alias to allow direct control specification after dataset attribute.
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public final class DataSetRegistrar<T extends ReadableAttributes> implements AttributeRegistrar<T>
	{
		private final AttributeRegistrar<T> dataSetRegistrar;

		@Override
		public void accept( T owner, WritableAttributes attributes ) {
			dataSetRegistrar.accept( owner, attributes );
		}

		public <U extends T> AttributeRegistrar<U> control( @NonNull Consumer<AutoSuggestFormElementConfiguration> controlConsumer ) {
			return ( owner, attributes ) -> {
				accept( owner, attributes );
				AutoSuggestDataAttributeRegistrar.this.control( controlConsumer ).accept( owner, attributes );
			};
		}
	}
}
