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
package com.foreach.across.modules.entity.query.querydsl;

import com.foreach.across.modules.entity.query.AbstractEntityQueryExecutor;
import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.query.EntityQueryExecutor;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/**
 * Implementation of {@link EntityQueryExecutor} that runs against a {@link QuerydslPredicateExecutor} instance.
 *
 * @author Arne Vandamme
 */
public class EntityQueryQueryDslExecutor<T> extends AbstractEntityQueryExecutor<T>
{
	private final QuerydslPredicateExecutor<T> queryDslPredicateExecutor;
	private final EntityConfiguration entityConfiguration;

	public EntityQueryQueryDslExecutor( QuerydslPredicateExecutor<T> queryDslPredicateExecutor,
	                                    EntityConfiguration entityConfiguration ) {
		this.queryDslPredicateExecutor = queryDslPredicateExecutor;
		this.entityConfiguration = entityConfiguration;
	}

	@Override
	protected Iterable<T> executeQuery( EntityQuery query ) {
		return queryDslPredicateExecutor.findAll( predicate( query ) );
	}

	@Override
	protected Iterable<T> executeQuery( EntityQuery query, Sort sort ) {
		return queryDslPredicateExecutor.findAll( predicate( query ), sort );
	}

	@Override
	protected Page<T> executeQuery( EntityQuery query, Pageable pageable ) {
		return queryDslPredicateExecutor.findAll( predicate( query ), pageable );
	}

	private Predicate predicate( EntityQuery query ) {
		return EntityQueryQueryDslUtils.toPredicate( query, entityConfiguration );
	}
}
