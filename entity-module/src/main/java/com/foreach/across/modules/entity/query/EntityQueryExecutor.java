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
package com.foreach.across.modules.entity.query;

import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Simple abstraction interface for defining simple generic queries (in the form of {@link EntityQuery})
 * that can be used to fetch one or more entities.  Used as an additional abstraction for the specific
 * JPA, QueryDsl repositories.
 * <p/>
 * Only accepts a translated and executable {@link EntityQuery}.
 *
 * @author Arne Vandamme
 * @see AbstractEntityQueryExecutor
 * @see com.foreach.across.modules.entity.query.jpa.EntityQueryJpaExecutor
 * @see com.foreach.across.modules.entity.query.querydsl.EntityQueryQueryDslExecutor
 */
public interface EntityQueryExecutor<T>
{
	List<T> findAll( EntityQuery query );

	List<T> findAll( EntityQuery query, Sort sort );

	Page<T> findAll( EntityQuery query, Pageable pageable );

	/**
	 * Can be implemented to check if this executor can actually work with this query.
	 * Required to be implemented when using {@link #createFallbackExecutor(EntityQueryExecutor, EntityQueryExecutor)}.
	 *
	 * @param query to execute
	 * @return true if the query can be executed by this executor
	 */
	default boolean canExecute( @NonNull EntityQuery query ) {
		return true;
	}

	/**
	 * Create a new executor that first tries an original executor and falls back to another one if the first
	 * is not able to execute the query passed in.
	 *
	 * @param initial  executor to try
	 * @param fallback to fall back to
	 * @return executor
	 */
	static <U> EntityQueryExecutor<U> createFallbackExecutor( @NonNull EntityQueryExecutor<U> initial, @NonNull EntityQueryExecutor<U> fallback ) {
		return new EntityQueryExecutor<U>()
		{
			@Override
			public List<U> findAll( EntityQuery query ) {
				return initial.canExecute( query ) ? initial.findAll( query ) : fallback.findAll( query );
			}

			@Override
			public List<U> findAll( EntityQuery query, Sort sort ) {
				return initial.canExecute( query ) ? initial.findAll( query, sort ) : fallback.findAll( query, sort );
			}

			@Override
			public Page<U> findAll( EntityQuery query, Pageable pageable ) {
				return initial.canExecute( query ) ? initial.findAll( query, pageable ) : fallback.findAll( query, pageable );
			}
		};
	}
}
