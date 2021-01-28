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

package com.foreach.across.modules.entity.registrars.repository.entityqueryexecutor;

import com.foreach.across.modules.entity.query.EntityQueryExecutor;
import com.foreach.across.modules.entity.query.PagingAndSortingEntityQueryExecutor;
import com.foreach.across.modules.entity.query.collections.CollectionEntityQueryExecutor;
import com.foreach.across.modules.entity.registrars.repository.EntityQueryExecutorRegistrar;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.MutableEntityConfiguration;
import org.springframework.core.annotation.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Component;

import static org.springframework.core.Ordered.LOWEST_PRECEDENCE;

@Order(LOWEST_PRECEDENCE)
@Component
public class DefaultEntityQueryExecutorRegistrar implements EntityQueryExecutorRegistrar
{
	@Override
	public boolean supports( EntityConfiguration entityConfiguration, Repository repository ) {
		return repository instanceof CrudRepository;
	}

	@Override
	@SuppressWarnings("unchecked")
	public EntityQueryExecutor resolveEntityQueryExecutor( MutableEntityConfiguration entityConfiguration, Repository repository ) {
		EntityQueryExecutor entityQueryExecutor = new CollectionEntityQueryExecutor<>( ( (CrudRepository) repository )::findAll,
		                                                                               entityConfiguration.getPropertyRegistry() );

		if ( repository instanceof PagingAndSortingRepository ) {
			entityQueryExecutor = EntityQueryExecutor.createFallbackExecutor(
					new PagingAndSortingEntityQueryExecutor<>( (PagingAndSortingRepository) repository ), entityQueryExecutor
			);
		}
		return entityQueryExecutor;
	}
}
