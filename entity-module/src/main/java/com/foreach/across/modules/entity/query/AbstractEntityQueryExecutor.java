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

import com.foreach.across.modules.entity.util.EntityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Base class for an {@link EntityQueryExecutor} that supports {@link Sort} specifications on
 * both the {@link EntityQuery} and as method parameter.  If both are present, they will be
 * combined with the method specified sort applied first.
 *
 * @author Arne Vandamme
 * @since 2.2.0
 */
public abstract class AbstractEntityQueryExecutor<T> implements EntityQueryExecutor<T>
{
	@Override
	public final Page<T> findAll( EntityQuery query, Pageable pageable ) {
		if ( pageable == null ) {
			return EntityUtils.asPage( findAll( query ) );
		}
		Pageable pageableToUse = query.hasSort() ? combinePageable( pageable, query.getSort() ) : pageable;
		return executeQuery( query, pageableToUse );
	}

	@Override
	public final List<T> findAll( EntityQuery query ) {
		return EntityUtils.asList( query.hasSort() ? executeQuery( query, query.getSort() ) : executeQuery( query ) );
	}

	@Override
	public final List<T> findAll( EntityQuery query, Sort sort ) {
		Sort sortToUse = query.hasSort() ? EntityUtils.combineSortSpecifiers( sort, query.getSort() ) : sort;
		return EntityUtils.asList( sortToUse != null ? executeQuery( query, sortToUse ) : executeQuery( query ) );
	}

	private Pageable combinePageable( Pageable pageable, Sort sort ) {
		Sort combinedSort = EntityUtils.combineSortSpecifiers( pageable.getSort(), sort );
		return PageRequest.of( pageable.getPageNumber(), pageable.getPageSize(), combinedSort );
	}

	/**
	 * Implementations of this method should ignore the {@link EntityQuery#getSort()} value.
	 */
	protected abstract Iterable<T> executeQuery( EntityQuery query );

	/**
	 * Implementations of this method should ignore the {@link EntityQuery#getSort()} value.
	 */
	protected abstract Iterable<T> executeQuery( EntityQuery query, Sort sort );

	/**
	 * Implementations of this method should ignore the {@link EntityQuery#getSort()} value.
	 */
	protected abstract Page<T> executeQuery( EntityQuery query, Pageable pageable );
}
