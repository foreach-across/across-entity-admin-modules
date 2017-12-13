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

package com.foreach.across.modules.entity.config.builders;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertySelector;
import com.foreach.across.modules.entity.views.EntityViewFactory;
import com.foreach.across.modules.entity.views.EntityViewProcessor;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.processors.*;
import com.foreach.across.modules.entity.views.processors.query.EntityQueryFilterConfiguration;
import com.foreach.across.modules.entity.views.processors.support.EntityViewProcessorRegistry;
import com.foreach.across.modules.spring.security.actions.AllowableAction;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Extends the default {@link EntityViewFactoryBuilder} with properties for a list view of entities instead of a single entity view.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EntityListViewFactoryBuilder extends EntityViewFactoryBuilder
{
	private final Collection<Consumer<EntityQueryFilterConfiguration.EntityQueryFilterConfigurationBuilder>> filterConfigurationConsumers = new ArrayDeque<>();

	private Boolean showResultNumber, entityQueryFilter;
	private Integer pageSize;
	private Sort defaultSort;
	private Collection<String> sortableProperties;
	private BiFunction<EntityViewContext, Pageable, Iterable<?>> pageFetcher;

	@Autowired
	public EntityListViewFactoryBuilder( AutowireCapableBeanFactory beanFactory ) {
		super( beanFactory );
	}

	@Override
	public EntityListViewFactoryBuilder factoryType( Class<? extends EntityViewFactory> factoryType ) {
		return (EntityListViewFactoryBuilder) super.factoryType( factoryType );
	}

	@Override
	public EntityListViewFactoryBuilder template( String template ) {
		return (EntityListViewFactoryBuilder) super.template( template );
	}

	@Override
	public EntityListViewFactoryBuilder transactionManager( String transactionManagerName ) {
		return (EntityListViewFactoryBuilder) super.transactionManager( transactionManagerName );
	}

	@Override
	public EntityListViewFactoryBuilder transactionManager( PlatformTransactionManager transactionManager ) {
		return (EntityListViewFactoryBuilder) super.transactionManager( transactionManager );
	}

	@Override
	public EntityListViewFactoryBuilder transactionTemplate( TransactionTemplate transactionTemplate ) {
		return (EntityListViewFactoryBuilder) super.transactionTemplate( transactionTemplate );
	}

	@Override
	public EntityListViewFactoryBuilder properties( Consumer<EntityPropertyRegistryBuilder> registryConsumer ) {
		return (EntityListViewFactoryBuilder) super.properties( registryConsumer );
	}

	@Override
	public EntityListViewFactoryBuilder showProperties( String... propertyNames ) {
		return (EntityListViewFactoryBuilder) super.showProperties( propertyNames );
	}

	@Override
	public EntityListViewFactoryBuilder viewProcessor( EntityViewProcessor processor ) {
		return (EntityListViewFactoryBuilder) super.viewProcessor( processor );
	}

	@Override
	public EntityListViewFactoryBuilder factory( EntityViewFactory factory ) {
		return (EntityListViewFactoryBuilder) super.factory( factory );
	}

	@Override
	public EntityListViewFactoryBuilder propertyRegistry( EntityPropertyRegistry propertyRegistry ) {
		return (EntityListViewFactoryBuilder) super.propertyRegistry( propertyRegistry );
	}

	@Override
	public EntityListViewFactoryBuilder viewElementMode( ViewElementMode viewElementMode ) {
		return (EntityListViewFactoryBuilder) super.viewElementMode( viewElementMode );
	}

	@Override
	public EntityListViewFactoryBuilder viewProcessor( String processorName, EntityViewProcessor processor ) {
		return (EntityListViewFactoryBuilder) super.viewProcessor( processorName, processor );
	}

	@Override
	public EntityListViewFactoryBuilder messagePrefix( String... messagePrefixes ) {
		return (EntityListViewFactoryBuilder) super.messagePrefix( messagePrefixes );
	}

	@Override
	public EntityListViewFactoryBuilder requiredAllowableAction( AllowableAction action ) {
		return (EntityListViewFactoryBuilder) super.requiredAllowableAction( action );
	}

	@Override
	public EntityListViewFactoryBuilder removeViewProcessor( String processorName ) {
		return (EntityListViewFactoryBuilder) super.removeViewProcessor( processorName );
	}

	@Override
	public EntityListViewFactoryBuilder postProcess( BiConsumer<EntityViewFactory, EntityViewProcessorRegistry> postProcessor ) {
		return (EntityListViewFactoryBuilder) super.postProcess( postProcessor );
	}

	/**
	 * Configure a page fetching function that will retrieve the entities requested by a specific {@link Pageable}.
	 * This will result in a {@link com.foreach.across.modules.entity.views.processors.DelegatingEntityFetchingViewProcessor} being added.
	 *
	 * @param pageFetcher function - may not be null
	 * @return current builder
	 */
	public EntityListViewFactoryBuilder pageFetcher( @NonNull Function<Pageable, Iterable<?>> pageFetcher ) {
		return pageFetcher( ( ctx, pageable ) -> pageFetcher.apply( pageable ) );
	}

	/**
	 * Configure a page fetching function that will retrieve the entities requested by a specific {@link Pageable}.
	 * This will result in a {@link com.foreach.across.modules.entity.views.processors.DelegatingEntityFetchingViewProcessor} being added.
	 *
	 * @param pageFetcher function - may not be null
	 * @return current builder
	 */
	public EntityListViewFactoryBuilder pageFetcher( @NonNull BiFunction<EntityViewContext, Pageable, Iterable<?>> pageFetcher ) {
		this.pageFetcher = pageFetcher;
		return this;
	}

	/**
	 * @param pageSize number of results per page.
	 * @return current builder
	 */
	public EntityListViewFactoryBuilder pageSize( int pageSize ) {
		this.pageSize = pageSize;
		return this;
	}

	/**
	 * @param propertyNames of properties that can be sorted on
	 * @return current builder
	 */
	public EntityListViewFactoryBuilder sortableOn( String... propertyNames ) {
		this.sortableProperties = Arrays.asList( propertyNames );
		return this;
	}

	/**
	 * Set the default sort to ascending order of the property specified.
	 *
	 * @param property name
	 * @return current builder
	 * @see #defaultSort(Sort)
	 */
	public EntityListViewFactoryBuilder defaultSort( String property ) {
		return defaultSort( new Sort( Sort.Direction.ASC, property ) );
	}

	/**
	 * @param sort default sort instance that should be applied when fetching
	 * @return current builder
	 */
	public EntityListViewFactoryBuilder defaultSort( Sort sort ) {
		this.defaultSort = sort;
		return this;
	}

	/**
	 * @param showResultNumber true if result numbers should be shown in the list
	 * @return current builder
	 */
	public EntityListViewFactoryBuilder showResultNumber( boolean showResultNumber ) {
		this.showResultNumber = showResultNumber;
		return this;
	}

	/**
	 * Configure {@link com.foreach.across.modules.entity.query.EntityQuery} based filtering on this list view.
	 * This registers the {@link EntityQueryFilterProcessor} and allows you to customize the {@link EntityQueryFilterConfiguration}.
	 *
	 * @param consumer to modify the configuration
	 * @return current builder
	 */
	public EntityListViewFactoryBuilder entityQueryFilter( Consumer<EntityQueryFilterConfiguration.EntityQueryFilterConfigurationBuilder> consumer ) {
		filterConfigurationConsumers.add( consumer );
		return entityQueryFilter( true );
	}

	/**
	 * Enable default {@link com.foreach.across.modules.entity.query.EntityQuery} based filtering for this list.
	 * Amounts to the same as manually registering a {@link EntityQueryFilterProcessor} using {@link #viewProcessor(EntityViewProcessor)}.
	 * <p/>
	 * Calling with {@code false} will remove the processor if it was activated before.
	 *
	 * @param enabled true if filter should be added, false if not (or removed again)
	 * @return current builder
	 */
	public EntityListViewFactoryBuilder entityQueryFilter( boolean enabled ) {
		entityQueryFilter = enabled;
		return this;
	}

	/**
	 * Configure a fixed default predicate that should be used when fetching the items.
	 * This requires a valid {@link com.foreach.across.modules.entity.query.EntityQueryParser} to be available for that
	 * {@link com.foreach.across.modules.entity.registry.EntityConfiguration} as the {@link com.foreach.across.modules.entity.query.EntityQueryExecutor}
	 * will be favoured over the default repository.
	 * <p/>
	 * The default predicate can not be removed and will always be applied.
	 * Even if {@link #entityQueryFilter(boolean)} is enabled.
	 * <p/>
	 * Note: using {@link #entityQueryFilter(Consumer)} allows for more advanced configuration options like appending base queries.
	 *
	 * @param predicate as EQL statement
	 * @return current builder
	 */
	public EntityListViewFactoryBuilder entityQueryPredicate( String predicate ) {
		filterConfigurationConsumers.add( eqb -> eqb.basePredicate( predicate ) );

		postProcess( ( factory, processorRegistry ) -> {
			processorRegistry.getProcessor( DefaultEntityFetchingViewProcessor.class.getName(), DefaultEntityFetchingViewProcessor.class )
			                 .ifPresent( processor -> processor.setBaseEqlPredicate( predicate ) );
		} );

		return this;
	}

	@Override
	protected void configureRenderingProcessors( EntityViewProcessorRegistry processorRegistry,
	                                             EntityPropertySelector propertiesToShow,
	                                             ViewElementMode viewElementMode ) {
		configurePageableProcessor( processorRegistry );
		configureSortableTableProcessor( processorRegistry, propertiesToShow, viewElementMode );
		configureEntityQueryFilter( processorRegistry );
		configurePageFetcher( processorRegistry );
	}

	private void configurePageFetcher( EntityViewProcessorRegistry processorRegistry ) {
		if ( pageFetcher != null ) {
			processorRegistry.remove( DelegatingEntityFetchingViewProcessor.class.getName() );
			processorRegistry.addProcessor(
					DelegatingEntityFetchingViewProcessor.class.getName(),
					new DelegatingEntityFetchingViewProcessor( pageFetcher ),
					DelegatingEntityFetchingViewProcessor.DEFAULT_ORDER
			);
		}
	}

	private void configureEntityQueryFilter( EntityViewProcessorRegistry processorRegistry ) {
		if ( entityQueryFilter != null ) {
			if ( entityQueryFilter ) {
				if ( !processorRegistry.contains( EntityQueryFilterProcessor.class.getName() ) ) {
					processorRegistry.addProcessor( createBean( EntityQueryFilterProcessor.class ) );
				}
				processorRegistry.getProcessor( EntityQueryFilterProcessor.class.getName(), EntityQueryFilterProcessor.class )
				                 .ifPresent( p -> {
					                 EntityQueryFilterConfiguration.EntityQueryFilterConfigurationBuilder configurationBuilder =
							                 p.getFilterConfiguration().toBuilder();
					                 filterConfigurationConsumers.forEach( c -> c.accept( configurationBuilder ) );
					                 p.setFilterConfiguration( configurationBuilder.build() );
				                 } );
			}
			else {
				processorRegistry.remove( EntityQueryFilterProcessor.class.getName() );
			}
		}
	}

	private void configurePageableProcessor( EntityViewProcessorRegistry processorRegistry ) {
		if ( pageSize != null || defaultSort != null ) {
			PageableExtensionViewProcessor pageableExtensionViewProcessor = processorRegistry
					.getProcessor( PageableExtensionViewProcessor.class.getName(), PageableExtensionViewProcessor.class )
					.orElseGet( () -> {
						PageableExtensionViewProcessor pageableProcessor = createBean( PageableExtensionViewProcessor.class );
						processorRegistry.addProcessor( pageableProcessor );
						return pageableProcessor;
					} );

			Pageable currentPageable = pageableExtensionViewProcessor.getDefaultPageable();
			pageableExtensionViewProcessor.setDefaultPageable(
					new PageRequest(
							currentPageable.getPageNumber(),
							pageSize != null ? pageSize : currentPageable.getPageSize(),
							defaultSort != null ? defaultSort : currentPageable.getSort()
					)
			);
		}
	}

	private void configureSortableTableProcessor( EntityViewProcessorRegistry processorRegistry,
	                                              EntityPropertySelector propertiesToShow,
	                                              ViewElementMode viewElementMode ) {
		if ( propertiesToShow != null || viewElementMode != null || showResultNumber != null || sortableProperties != null ) {
			SortableTableRenderingViewProcessor tableRenderingViewProcessor = processorRegistry
					.getProcessor( SortableTableRenderingViewProcessor.class.getName(), SortableTableRenderingViewProcessor.class )
					.orElseGet( () -> {
						SortableTableRenderingViewProcessor processor = createBean( SortableTableRenderingViewProcessor.class );
						processorRegistry.addProcessor( processor );
						return processor;
					} );

			if ( propertiesToShow != null ) {
				tableRenderingViewProcessor.setPropertySelector( propertiesToShow );
			}

			if ( showResultNumber != null ) {
				tableRenderingViewProcessor.setShowResultNumber( showResultNumber );
			}

			if ( sortableProperties != null ) {
				tableRenderingViewProcessor.setSortableProperties( sortableProperties );
			}

			if ( viewElementMode != null ) {
				tableRenderingViewProcessor.setViewElementMode( viewElementMode );
			}
		}
	}
}
