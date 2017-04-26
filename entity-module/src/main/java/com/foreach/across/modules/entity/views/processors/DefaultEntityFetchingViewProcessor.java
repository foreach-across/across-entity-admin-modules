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

package com.foreach.across.modules.entity.views.processors;

import com.foreach.across.modules.entity.query.AssociatedEntityQueryExecutor;
import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.query.EntityQueryExecutor;
import com.foreach.across.modules.entity.query.EntityQueryParser;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import lombok.Setter;
import org.springframework.core.Ordered;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.Repository;

/**
 * Default implementation that attempts to fetch the items based on the {@link com.foreach.across.modules.entity.views.context.EntityViewContext}.
 * Will use the repository attached to the {@link com.foreach.across.modules.entity.registry.EntityConfiguration} that is being used, and will
 * attempt to resolve association properties.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public final class DefaultEntityFetchingViewProcessor extends AbstractEntityFetchingViewProcessor
{
	/**
	 * Can hold an optional EQL statement that should be applied to the query being executed.
	 */
	public static final String EQL_PREDICATE_ATTRIBUTE_NAME = EntityQueryFilterProcessor.EQL_PREDICATE_ATTRIBUTE_NAME;

	/**
	 * Default order that this processor will have if it has been added through the {@link com.foreach.across.modules.entity.views.builders.ListViewInitializer}.
	 */
	public static final int DEFAULT_ORDER = Ordered.LOWEST_PRECEDENCE;

	/**
	 * The basic filter that should always be applied.
	 * This can only be an EQL statement and will force the {@link EntityQueryExecutor} to be used.
	 */
	@Setter
	private String baseEqlPredicate;

	@Override
	protected Iterable fetchItems( EntityViewRequest entityViewRequest, EntityView entityView, Pageable pageable ) {
		EntityViewContext entityViewContext = entityViewRequest.getEntityViewContext();

		String additionalPredicate = entityView.getAttribute( EQL_PREDICATE_ATTRIBUTE_NAME, String.class );

		// set to null so we would favour regular repository if no specific query necessary
		EntityQuery entityQuery = null;

		if ( baseEqlPredicate != null || additionalPredicate != null ) {
			entityQuery = buildEntityQuery( entityViewContext.getEntityConfiguration(), additionalPredicate );
		}

		if ( entityViewContext.isForAssociation() ) {
			return fetchItemsForEntityAssociation(
					entityViewContext.getEntityAssociation(),
					entityViewContext.getParentContext().getEntity( Object.class ),
					entityQuery,
					pageable
			);
		}

		return fetchItemsForEntityConfiguration( entityViewContext.getEntityConfiguration(), entityQuery, pageable );
	}

	@SuppressWarnings("unchecked")
	private Iterable<Object> fetchItemsForEntityConfiguration( EntityConfiguration entityConfiguration, EntityQuery entityQuery, Pageable pageable ) {
		Repository repository = entityConfiguration.getAttribute( Repository.class );

		if ( entityQuery == null ) {
			if ( repository instanceof PagingAndSortingRepository ) {
				return ( (PagingAndSortingRepository) repository ).findAll( pageable );
			}
		}

		EntityQueryExecutor entityQueryExecutor = entityConfiguration.getAttribute( EntityQueryExecutor.class );

		if ( entityQueryExecutor != null ) {
			return entityQueryExecutor.findAll( entityQuery != null ? entityQuery : EntityQuery.all(), pageable );
		}

		if ( entityQuery != null ) {
			throw new IllegalStateException(
					"An EntityQuery predicate was specified but the EntityConfiguration does not have a valid EntityQueryParser setup: " + entityConfiguration
							.getName() );
		}

		// return all results - ignore paging
		if ( repository instanceof CrudRepository ) {
			return ( (CrudRepository) repository ).findAll();
		}

		throw new IllegalStateException(
				"Neither a CrudRepository nor an EntityQueryExecutor are configured on entity configuration " + entityConfiguration.getName() );
	}

	@SuppressWarnings("unchecked")
	private Iterable<Object> fetchItemsForEntityAssociation( EntityAssociation association,
	                                                         Object parentEntity,
	                                                         EntityQuery entityQuery,
	                                                         Pageable pageable ) {
		AssociatedEntityQueryExecutor associatedEntityQueryExecutor = association.getAttribute( AssociatedEntityQueryExecutor.class );

		if ( associatedEntityQueryExecutor != null ) {
			return associatedEntityQueryExecutor.findAll( parentEntity, entityQuery != null ? entityQuery : EntityQuery.all(), pageable );
		}

		throw new IllegalStateException(
				"No AssociatedEntityQueryExecutor found for association " + association.getName()
		);
	}

	private EntityQuery buildEntityQuery( EntityConfiguration entityConfiguration, String additionalPredicate ) {
		EntityQuery query = EntityQuery.all();
		EntityQueryParser parser = entityConfiguration.getAttribute( EntityQueryParser.class );

		if ( baseEqlPredicate != null ) {
			query = EntityQuery.and( query, parser.parse( baseEqlPredicate ) );
		}

		if ( additionalPredicate != null ) {
			query = EntityQuery.and( query, parser.parse( additionalPredicate ) );
		}

		return query;
	}
}
