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
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
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
	@Override
	protected Iterable<Object> fetchItems( EntityViewRequest entityViewRequest, EntityView entityView, Pageable pageable ) {
		EntityViewContext entityViewContext = entityViewRequest.getEntityViewContext();

		if ( entityViewContext.isForAssociation() ) {
			return fetchItemsForEntityAssociation(
					entityViewContext.getEntityAssociation(), entityViewContext.getParentContext().getEntity( Object.class ), pageable
			);
		}

		return fetchItemsForEntityConfiguration( entityViewContext.getEntityConfiguration(), pageable );
	}

	@SuppressWarnings("unchecked")
	private Iterable<Object> fetchItemsForEntityConfiguration( EntityConfiguration entityConfiguration, Pageable pageable ) {
		Repository repository = entityConfiguration.getAttribute( Repository.class );

		if ( repository instanceof PagingAndSortingRepository ) {
			return ( (PagingAndSortingRepository) repository ).findAll( pageable );
		}

		EntityQueryExecutor entityQueryExecutor = entityConfiguration.getAttribute( EntityQueryExecutor.class );

		if ( entityQueryExecutor != null ) {
			return entityQueryExecutor.findAll( EntityQuery.all(), pageable );
		}

		// return all results - ignore paging
		if ( repository instanceof CrudRepository ) {
			return ( (CrudRepository) repository ).findAll();
		}

		throw new IllegalStateException(
				"Neither a CrudRepository nor an EntityQueryExecutor are configured on entity configuration " + entityConfiguration.getName() );
	}

	@SuppressWarnings("unchecked")
	private Iterable<Object> fetchItemsForEntityAssociation( EntityAssociation association, Object parentEntity, Pageable pageable ) {
		AssociatedEntityQueryExecutor associatedEntityQueryExecutor = association.getAttribute( AssociatedEntityQueryExecutor.class );

		if ( associatedEntityQueryExecutor != null ) {
			return associatedEntityQueryExecutor.findAll( parentEntity, EntityQuery.all(), pageable );
		}

		throw new IllegalStateException(
				"No AssociatedEntityQueryExecutor found for association " + association.getName()
		);
	}
}
