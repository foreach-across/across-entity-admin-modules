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

/**
 * @author Steven Gentens
 * @since 2.2.0
 */
public abstract class EntityQueryUtils
{
	private EntityQueryUtils() {
	}

	/**
	 * Appends an optional predicate to an existing query using an AND operand.
	 * If the predicate is not an {@link EntityQueryExpression} then it will be parsed as an EQL statement.
	 *
	 * @param existing  query to append to
	 * @param predicate to append
	 * @return new query instance
	 */
	public static EntityQuery and( EntityQuery existing, Object predicate ) {
		return appendToQuery( existing, EntityQueryOps.AND, predicate );
	}

	/**
	 * Appends an optional predicate to an existing query using an OR operand.
	 * If the predicate is not an {@link EntityQueryExpression} then it will be parsed as an EQL statement.
	 *
	 * @param existing  query to append to
	 * @param predicate to append
	 * @return new query instance
	 */
	public static EntityQuery or( EntityQuery existing, Object predicate ) {
		return appendToQuery( existing, EntityQueryOps.OR, predicate );
	}

	/**
	 * Appends an optional predicate to an existing query using the specified operand.
	 * If the predicate is not an {@link EntityQueryExpression} then it will be parsed as an EQL statement.
	 *
	 * @param existing  query to append to
	 * @param operand   operand to use
	 * @param predicate to append
	 * @return new query instance
	 */
	public static EntityQuery appendToQuery( EntityQuery existing, EntityQueryOps operand, Object predicate ) {
		if ( predicate != null ) {
			if ( predicate instanceof String ) {
				return EntityQuery.create( operand, existing, EntityQuery.parse( (String) predicate ) );
			}
			if ( predicate instanceof EntityQueryExpression ) {
				return EntityQuery.create( operand, existing, (EntityQueryExpression) predicate );
			}
			return EntityQuery.create( operand, existing, EntityQuery.parse( predicate.toString() ) );
		}

		return existing;
	}
}
