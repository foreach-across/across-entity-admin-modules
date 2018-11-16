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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Steven Gentens
 * @since 2.2.0
 */
public class TestEntityQueryUtils
{
	private EntityQuery query = EntityQuery.parse( "id = 1" );

	@Test
	public void appendSinglePredicateToQuery() {
		assertEqlEquals(
				"id = 1 and name ilike 'john'",
				EntityQueryUtils.and( query, new EntityQueryCondition( "name", EntityQueryOps.LIKE_IC, "john" ) )
		);
		assertEqlEquals(
				"id = 1 or name ilike 'john'",
				EntityQueryUtils.or( query, new EntityQueryCondition( "name", EntityQueryOps.LIKE_IC, "john" ) )
		);
	}

	@Test
	public void appendQueryToQuery() {
		assertEqlEquals(
				"id = 1 and (name ilike 'john' or number <= 3)",
				EntityQueryUtils.and( query, EntityQuery.parse( "name ilike 'john' or number <= 3" ) )
		);
		assertEqlEquals(
				"id = 1 or (name ilike 'john' or number <= 3)",
				EntityQueryUtils.or( query, EntityQuery.parse( "name ilike 'john' or number <= 3" ) )
		);
	}

	@Test
	public void appendNullLeavesQueryUntouched() {
		assertEqlEquals( "id = 1", EntityQueryUtils.and( query, null ) );
		assertEqlEquals( "id = 1", EntityQueryUtils.or( query, null ) );
	}

	@Test
	public void appendPredicateAsString() {
		assertEqlEquals(
				"id = 1 and (name ilike 'john' or number <= 3)",
				EntityQueryUtils.and( query, "name ilike 'john' or number <= 3" )
		);
		assertEqlEquals(
				"id = 1 or (name ilike 'john' or number <= 3)",
				EntityQueryUtils.or( query, "name ilike 'john' or number <= 3" )
		);
	}

	@Test(expected = EntityQueryParsingException.class)
	public void parsingExceptionIfInvalidStringValue() {
		EntityQueryUtils.or( query, "name ilike 'john' or number <= " );
	}

	@Test(expected = EntityQueryParsingException.class)
	public void parsingExceptionIfInvalidObject() {
		EntityQueryUtils.or( query, 123 );
	}

	@Test
	public void simplify() {
		assertEqlEquals(
				"id = 1 and name like 'test'",
				EntityQueryUtils.simplify( EntityQuery.parse( "id = 1 and name like 'test'" ) )
		);

		assertEqlEquals(
				"id = 1 and name like 'test'",
				EntityQueryUtils.simplify( EntityQuery.parse( "id = 1 and (name like 'test')" ) )
		);

		assertEqlEquals(
				"id = 1 and name like 'test'",
				EntityQueryUtils.simplify( EntityQuery.parse( "(id = 1 and name like 'test')" ) )
		);

		assertEqlEquals(
				"id = 1 or name like 'test'",
				EntityQueryUtils.simplify( EntityQuery.parse( "(id = 1 or name like 'test')" ) )
		);

		assertEqlEquals(
				"id = 1 and name like 'test'",
				EntityQueryUtils.simplify( EntityQuery.parse( "(id = 1 and (name like 'test'))" ) )
		);

		assertEqlEquals(
				"id = 1 and name like 'test' order by x ASC, y DESC",
				EntityQueryUtils.simplify( EntityQuery.parse( "((id = 1 and ((name like 'test')))) order by x asc, y desc" ) )
		);

		assertEqlEquals(
				"id = 1 and name like 'test' and city contains X",
				EntityQueryUtils.simplify( EntityQuery.parse( "(id = 1 and (name like 'test' and (city contains X)))" ) )
		);

		assertEqlEquals(
				"id = 1 and (name like 'test' or city contains X)",
				EntityQueryUtils.simplify( EntityQuery.parse( "(id = 1 and (name like 'test' or (city contains X)))" ) )
		);

		assertEqlEquals(
				"id = 1 or (name like 'test' and (city contains X or y = z))",
				EntityQueryUtils.simplify( EntityQuery.parse( "(id = 1 or (name like 'test' and (city contains X or (y = z))))" ) )
		);

		assertEqlEquals(
				"(id = 1 or name like 'test') and (city contains X or y = z)",
				EntityQueryUtils.simplify( EntityQuery.parse( "(id = 1 or name like 'test') and (city contains X or y = z)" ) )
		);
	}

	@Test
	public void translateConditions() {
		EntityQuery query = EntityQuery.of( "id = 1 and x = y and (x = z or name contains 'test' or 1 = 2) order by name ASC" );

		assertEqlEquals(
				"id != 1 and x != y and (x != z or name != 'test' or 1 != 2) order by name ASC",
				EntityQueryUtils.translateConditions( query, c -> new EntityQueryCondition( c.getProperty(), EntityQueryOps.NEQ, c.getArguments() ) )
		);

		assertEqlEquals(
				"id = 1 and (name contains 'test' or 1 = 2) order by name ASC",
				EntityQueryUtils.translateConditions( query, c -> null, "x" )
		);

		assertEqlEquals(
				"id = 1 and (name contains 'test' or one = 2) order by name ASC",
				EntityQueryUtils.translateConditions(
						query,
						c -> c.getProperty().equals( "x" ) ? null : new EntityQueryCondition( "one", c.getOperand(), c.getArguments() ),
						"x", "1"
				)
		);
	}

	private void assertEqlEquals( String expectedEql, EntityQuery entityQuery ) {
		assertEquals( expectedEql, entityQuery.toString() );
	}

}
