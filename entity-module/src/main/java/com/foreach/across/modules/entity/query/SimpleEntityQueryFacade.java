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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Basic implementation of {@link EntityQueryFacade} that wraps around an
 * {@link EntityQueryParser} and {@link EntityQueryExecutor}.
 *
 * @author Arne Vandamme
 * @since 3.1.0
 */
@RequiredArgsConstructor
public class SimpleEntityQueryFacade<T> implements EntityQueryFacade<T>
{
	@NonNull
	private final EntityQueryParser parser;

	@NonNull
	private final EntityQueryExecutor<T> executor;

	@Override
	public EntityQuery convertToExecutableQuery( EntityQuery rawQuery ) {
		return parser.prepare( rawQuery );
	}

	@Override
	public List<T> findAll( EntityQuery query ) {
		return executor.findAll( query );
	}

	@Override
	public List<T> findAll( EntityQuery query, Sort sort ) {
		return executor.findAll( query, sort );
	}

	@Override
	public Page<T> findAll( EntityQuery query, Pageable pageable ) {
		return executor.findAll( query, pageable );
	}
}
