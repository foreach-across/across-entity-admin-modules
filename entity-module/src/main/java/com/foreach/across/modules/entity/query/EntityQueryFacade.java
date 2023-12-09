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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Central component for working with {@link EntityQuery} and EQL statements for a particular entity type.
 * Usually wraps around an {@link EntityQueryParser} that can translate EQL to {@link EntityQuery}, and
 * an {@link EntityQueryExecutor} that can execute the actual query.
 *
 * @author Arne Vandamme
 * @see EntityQueryParser
 * @see EntityQueryExecutor
 * @since 3.1.0
 */
public interface EntityQueryFacade<T> extends EntityQueryExecutor<T>
{
	/**
	 * Find all entities matching the eql statement.
	 *
	 * @param eql query
	 * @return results
	 */
	default List<T> findAll( String eql ) {
		return findAll( convertToExecutableQuery( eql ) );
	}

	/**
	 * Find all entities matching the eql statement and sort
	 * them using the sort parameter after applying the sort order
	 * present in the eql statement.
	 *
	 * @param eql query
	 * @return results
	 */
	default List<T> findAll( String eql, Sort sort ) {
		return findAll( convertToExecutableQuery( eql ), sort );
	}

	/**
	 * Find all entities matching the eql statement.
	 * Apply the pageable to the result set (page selection + optional sorting).
	 *
	 * @param eql query
	 * @return results
	 */
	default Page<T> findAll( String eql, Pageable pageable ) {
		return findAll( convertToExecutableQuery( eql ), pageable );
	}

	/**
	 * Parses an EQL statement into an executable query, performing all necessary
	 * validation and type conversion.
	 *
	 * @param eql statement
	 * @return executable query instance
	 */

	default EntityQuery convertToExecutableQuery( String eql ) {
		return convertToExecutableQuery( EntityQuery.parse( eql ) );
	}

	/**
	 * Convert a raw query to an executable query. This usually verifies the selected
	 * properties and converts the condition arguments to the correct types.
	 *
	 * @param rawQuery to convert
	 * @return executable query instance
	 */
	EntityQuery convertToExecutableQuery( EntityQuery rawQuery );

}
