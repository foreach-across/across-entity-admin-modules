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

package com.foreach.across.modules.entity.query.collections;

import com.foreach.across.modules.entity.query.*;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.support.PageableExecutionUtils;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * An {@link com.foreach.across.modules.entity.query.EntityQueryExecutor} implementation that executes
 * queries on a collection of custom objects, where a {@link EntityPropertyRegistry} is used to determine
 * the property type and actual value.
 *
 * @author Arne Vandamme
 * @since 3.1.0
 */
@RequiredArgsConstructor
public class CollectionEntityQueryExecutor<T> extends AbstractEntityQueryExecutor<T>
{
	private final Collection<T> source;
	private final EntityPropertyRegistry propertyRegistry;

	@Override
	protected Iterable<T> executeQuery( EntityQuery query ) {
		return filterAndSort( query, null );
	}

	@Override
	protected Iterable<T> executeQuery( EntityQuery query, Sort sort ) {
		return filterAndSort( query, sort );
	}

	@Override
	protected Page<T> executeQuery( EntityQuery query, Pageable pageable ) {
		return buildPage( filterAndSort( query, pageable.getSort() ), pageable );
	}

	private List<T> filterAndSort( EntityQuery query, Sort sort ) {
		return source.stream()
		             .map( item -> new CollectionEntityQueryItem<>( item, propertyRegistry ) )
		             .filter( buildPredicate( query ) )
		             .sorted( buildComparator( sort ) )
		             .map( CollectionEntityQueryItem::getItem )
		             .collect( Collectors.toList() );
	}

	private Comparator<CollectionEntityQueryItem<T>> buildComparator( Sort sort ) {
		Comparator<CollectionEntityQueryItem<T>> comparator = null;

		if ( sort != null ) {
			for ( Sort.Order order : sort ) {
				Comparator<CollectionEntityQueryItem<T>> propertyComparator
						= CollectionEntityQueryComparators.createComparator( order, propertyRegistry.getProperty( order.getProperty() ) );
				comparator = comparator != null ? comparator.thenComparing( propertyComparator ) : propertyComparator;
			}
		}

		return comparator != null ? comparator : ( x, y ) -> 0;
	}

	private Predicate<CollectionEntityQueryItem<T>> buildPredicate( EntityQuery query ) {
		Predicate<CollectionEntityQueryItem<T>> predicate = null;

		for ( EntityQueryExpression e : query.getExpressions() ) {
			Predicate<CollectionEntityQueryItem<T>> expressionPredicate
					= e instanceof EntityQuery ? buildPredicate( (EntityQuery) e ) : buildPredicate( (EntityQueryCondition) e );

			if ( predicate != null ) {
				predicate = EntityQueryOps.AND.equals( query.getOperand() ) ? predicate.and( expressionPredicate ) : predicate.or( expressionPredicate );
			}
			else {
				predicate = expressionPredicate;
			}
		}

		return predicate != null ? predicate : e -> true;
	}

	private Predicate<CollectionEntityQueryItem<T>> buildPredicate( EntityQueryCondition condition ) {
		return CollectionEntityQueryPredicates.createPredicate( condition, propertyRegistry.getProperty( condition.getProperty() ) );
	}

	private Page<T> buildPage( List<T> allItems, Pageable pageable ) {
		List<T> content = allItems.stream()
		                          .skip( pageable.getOffset() )
		                          .limit( pageable.getPageSize() )
		                          .collect( Collectors.toList() );

		return PageableExecutionUtils.getPage( content, pageable, allItems::size );
	}
}
