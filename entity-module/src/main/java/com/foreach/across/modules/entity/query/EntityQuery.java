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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.foreach.across.modules.entity.query.support.SortDeserializer;
import com.foreach.across.modules.entity.query.support.SortSerializer;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Abstraction layer for *simple* query construction.  An EntityQuery is a simple structure that has the support classes
 * to be constructed from maps, de-serialized to and from JSON and later converted to specific query structures like
 * JPA or QueryDSL.
 * <p/>
 * An EntityQuery is limited in what it supports because it provides a common denominator for different query types.
 *
 * @author Arne Vandamme
 * @see com.foreach.across.modules.entity.query.jpa.EntityQueryJpaUtils
 * @see com.foreach.across.modules.entity.query.querydsl.EntityQueryQueryDslUtils
 */
public class EntityQuery implements EntityQueryExpression
{
	/**
	 * Ordering of the results.
	 */
	@JsonDeserialize(using = SortDeserializer.class)
	@JsonSerialize(using = SortSerializer.class)
	@Getter
	@Setter
	private Sort sort;

	private EntityQueryOps operand = EntityQueryOps.AND;
	private List<EntityQueryExpression> expressions = new ArrayList<>();

	public EntityQuery() {
	}

	public EntityQuery( EntityQueryOps operand ) {
		setOperand( operand );
	}

	public EntityQuery( EntityQuery entityQuery ) {
		this.operand = entityQuery.operand;
		this.expressions = new ArrayList<>( entityQuery.expressions );
		this.sort = entityQuery.sort;
	}

	public final void add( EntityQueryExpression expression ) {
		if ( expression instanceof EntityQuery ) {
			EntityQuery subQuery = new EntityQuery( (EntityQuery) expression );
			if ( subQuery.hasSort() && !hasSort() ) {
				setSort( subQuery.getSort() );
			}
			subQuery.sort = null;

			if ( !subQuery.expressions.isEmpty() ) {
				if ( subQuery.expressions.size() == 1 ) {
					expressions.add( subQuery.expressions.get( 0 ) );
				}
				else {
					expressions.add( subQuery );
				}
			}
		}
		else if ( expression != null ) {
			expressions.add( expression );
		}
	}

	public EntityQueryOps getOperand() {
		return operand;
	}

	public void setOperand( EntityQueryOps operand ) {
		Assert.notNull( operand, "A valid operand is required" );
		if ( operand != EntityQueryOps.AND && operand != EntityQueryOps.OR ) {
			throw new IllegalArgumentException( "EntityQuery operand type must be either AND or OR" );
		}
		this.operand = operand;
	}

	public List<EntityQueryExpression> getExpressions() {
		return expressions;
	}

	public void setExpressions( List<EntityQueryExpression> expressions ) {
		Assert.notNull( expressions, "expression collection may not be null" );
		this.expressions = expressions;
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}

		EntityQuery that = (EntityQuery) o;

		if ( expressions != null ? !expressions.equals( that.expressions ) : that.expressions != null ) {
			return false;
		}
		if ( operand != that.operand ) {
			return false;
		}
		if ( sort != null ? !sort.equals( that.sort ) : that.sort != null ) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = operand != null ? operand.hashCode() : 0;
		result = 31 * result + ( expressions != null ? expressions.hashCode() : 0 );
		return result;
	}

	@Override
	public String toString() {
		return StringUtils.trim( operand.toString( null, expressions.toArray() ) + ( sort != null ? " " + toString( sort ) : "" ) );
	}

	private String toString( Sort sort ) {
		return "order by " + StreamSupport.stream( sort.spliterator(), false )
		                                  .map( o -> o.getProperty() + " " + o.getDirection() )
		                                  .collect( Collectors.joining( ", " ) );
	}

	/**
	 * @return true if a sort has been set on this query and it contains orders
	 */
	public boolean hasSort() {
		return sort != null && sort.isSorted();
	}

	/**
	 * @return new EntityQuery instance that will return all entities.
	 */
	public static EntityQuery all() {
		return new EntityQuery();
	}

	/**
	 * @return new EntityQuery instance that will return all entities, sorted accordingly
	 */
	public static EntityQuery all( Sort sort ) {
		final EntityQuery entityQuery = new EntityQuery();
		entityQuery.setSort( sort );
		return entityQuery;
	}

	/**
	 * Converts an EQL statement into a (raw) {@link EntityQuery}.  Not validation or translation of any
	 * kind will be done on the elements, this will simply convert the {@link String} tokens into an {@link EntityQuery} object.
	 * <p/>
	 * You should use {@link EntityQueryParser#parse(String)} of the relevant entity if you want to parse an EQL into a fully
	 * executable {@link EntityQuery}.  You can use {@link EntityQueryParser#prepare(EntityQuery)} to convert the raw query
	 * object into an executable one.
	 * <p/>
	 * Exceptions will be thrown if parsing fails.
	 *
	 * @param eql to convert
	 * @return EntityQuery
	 */
	public static EntityQuery parse( String eql ) {
		return EntityQueryParser.parseRawQuery( eql );
	}

	/**
	 * Merges one or more entity queries expressions together using {@link EntityQueryOps#AND}.
	 * If expressions are sub-queries that define sort values, only the first non-null sort value will be kept.
	 * <p/>
	 * This method is null safe: {@code null} values will simply be ignored and a query will always be returned.
	 *
	 * @param expressions to merge
	 * @return merged query
	 */
	public static EntityQuery and( EntityQueryExpression... expressions ) {
		return create( EntityQueryOps.AND, expressions );
	}

	/**
	 * Merges one or more entity queries expressions together using {@link EntityQueryOps#OR}.
	 * If expressions are sub-queries that define sort values, only the first non-null sort value will be kept.
	 * <p/>
	 * This method is null safe: {@code null} values will simply be ignored and a query will always be returned.
	 *
	 * @param expressions to merge
	 * @return merged query
	 */
	public static EntityQuery or( EntityQueryExpression... expressions ) {
		return create( EntityQueryOps.OR, expressions );
	}

	public static EntityQuery create( EntityQueryOps operand, EntityQueryExpression... expressions ) {
		EntityQuery query = new EntityQuery( operand );

		Stream.of( expressions )
		      .filter( Objects::nonNull )
		      .forEach( query::add );

		return query;
	}

	/**
	 * @param eql eql statement
	 * @return entity query
	 * @see EntityQuery#parse(String)
	 */
	public static EntityQuery of( String eql ) {
		return EntityQuery.parse( eql );
	}

	/**
	 * Create a duplicate of an existing query. If the value passed in is {@code null}, a query instance for all values will be returned.
	 * As such, this method can be used to return the default query if you have a null value.
	 *
	 * @param query to duplicate
	 * @return query - never null
	 */
	public static EntityQuery of( EntityQuery query ) {
		if ( query == null ) {
			return EntityQuery.all();
		}
		return new EntityQuery( query );
	}
}
