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
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.foreach.across.modules.entity.query.EntityQueryOps.*;

/**
 * An {@link com.foreach.across.modules.entity.query.EntityQueryExecutor} implementation that executes
 * queries on a collection of custom objects, where a {@link EntityPropertyRegistry} is used to determine
 * the property type and actual value.
 * <p/>
 * The collection can be specified either as a static collection using {@link #CollectionEntityQueryExecutor(Collection, EntityPropertyRegistry)},
 * or via a {@link Supplier} using {@link #CollectionEntityQueryExecutor(Supplier, EntityPropertyRegistry)}.
 *
 * @author Arne Vandamme
 * @since 3.1.0
 */
@RequiredArgsConstructor
public class CollectionEntityQueryExecutor<T> extends AbstractEntityQueryExecutor<T>
{
	private final Supplier<Collection<T>> source;
	private final EntityPropertyRegistry propertyRegistry;

	public CollectionEntityQueryExecutor( Collection<T> source, EntityPropertyRegistry propertyRegistry ) {
		this.source = () -> source;
		this.propertyRegistry = propertyRegistry;
	}

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
		return source.get()
		             .stream()
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
				predicate = AND.equals( query.getOperand() ) ? predicate.and( expressionPredicate ) : predicate.or( expressionPredicate );
			}
			else {
				predicate = expressionPredicate;
			}
		}

		return predicate != null ? predicate : e -> true;
	}

	@SuppressWarnings("unchecked")
	private Predicate<CollectionEntityQueryItem<T>> buildPredicate( EntityQueryCondition condition ) {
		EntityQueryOps operand = condition.getOperand();
		Predicate predicate = CollectionEntityQueryPredicates.createPredicate( condition, propertyRegistry.getProperty( condition.getProperty() ) );
		if ( operand != IS_NULL && operand != IS_NOT_NULL ) {
			Predicate<CollectionEntityQueryItem<T>> nullPredicate = item -> item.getPropertyValue( condition.getProperty() ) != null;
			return nullPredicate.and( predicate );
		}
		return predicate;
	}

	private Page<T> buildPage( List<T> allItems, Pageable pageable ) {
		List<T> content = allItems.stream()
		                          .skip( pageable.getOffset() )
		                          .limit( pageable.getPageSize() )
		                          .collect( Collectors.toList() );

		return PageableExecutionUtils.getPage( content, pageable, allItems::size );
	}
}
