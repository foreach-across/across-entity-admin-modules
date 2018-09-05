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

import com.foreach.across.modules.spring.security.actions.AllowableAction;
import com.foreach.across.modules.spring.security.actions.AllowableActions;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.support.PageableExecutionUtils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Wrapping {@link EntityQueryExecutor} that filters the result set on whether the {@link AllowableActionFilteringEntityQueryExecutor#requestedAction} is present.
 *
 * @author Steven Gentens
 * @since 3.1.1-SNAPSHOT
 */
@RequiredArgsConstructor
public class AllowableActionFilteringEntityQueryExecutor<T> implements EntityQueryExecutor<T>
{
	private final Function<T, AllowableActions> allowableActionsResolver;
	private final EntityQueryExecutor<T> parentExecutor;
	private final AllowableAction requestedAction;

	@Override
	public List<T> findAll( EntityQuery query ) {
		return filter( parentExecutor.findAll( query ) );
	}

	@Override
	public List<T> findAll( EntityQuery query, Sort sort ) {
		return filter( parentExecutor.findAll( query, sort ) );
	}

	@Override
	public Page<T> findAll( EntityQuery query, Pageable pageable ) {
		List<T> elements = findAll( query, pageable.getSort() );
		return buildPage( elements, pageable );
	}

	private List<T> filter( List<T> elements ) {
		return elements.stream()
		               .filter( entity -> allowableActionsResolver.apply( entity ).contains( requestedAction ) )
		               .collect( Collectors.toList() );
	}

	private Page<T> buildPage( List<T> allItems, Pageable pageable ) {
		List<T> content = allItems.stream()
		                          .skip( pageable.getOffset() )
		                          .limit( pageable.getPageSize() )
		                          .collect( Collectors.toList() );

		return PageableExecutionUtils.getPage( content, pageable, allItems::size );
	}
}
