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

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * An {@link EntityQueryExecutor} implementation backed by a {@link PagingAndSortingRepository}.
 * Does not support any actual filter expressions, {@link #canExecute(EntityQuery)} will return {@code false}
 * if a query with expressions is passed in.
 *
 * @author Arne Vandamme
 * @since 3.3.0
 */
@RequiredArgsConstructor
public class PagingAndSortingEntityQueryExecutor<T> extends AbstractEntityQueryExecutor<T>
{
	private final PagingAndSortingRepository<T, ?> repository;

	@Override
	public boolean canExecute( EntityQuery query ) {
		return !query.hasExpressions();
	}

	@Override
	protected Iterable<T> executeQuery( EntityQuery query ) {
		return repository.findAll();
	}

	@Override
	protected Iterable<T> executeQuery( EntityQuery query, Sort sort ) {
		return repository.findAll( sort );
	}

	@Override
	protected Page<T> executeQuery( EntityQuery query, Pageable pageable ) {
		return repository.findAll( pageable );
	}
}
