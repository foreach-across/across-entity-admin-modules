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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.data.domain.Sort;

import static org.junit.Assert.*;

/**
 * @author Arne Vandamme
 */
public class TestEntityQuery
{
	@Test
	public void nullExpressionsInAnEntityQueryAreSimplyIgnored() {
		EntityQuery query = EntityQuery.and();
		query.add( null );

		assertFalse( query.hasExpressions() );
	}

	@Test
	public void singlePropertyToString() {
		EntityQuery query = EntityQuery.and( new EntityQueryCondition( "name", EntityQueryOps.EQ, "myName" ) );
		assertEquals( "name = 'myName'", query.toString() );
		assertTrue( query.hasExpressions() );
	}

	@Test
	public void nestedExpressionsToString() {
		EntityQuery query = new EntityQuery( EntityQueryOps.AND );
		query.add( new EntityQueryCondition( "name", EntityQueryOps.EQ, "someName" ) );
		query.add( new EntityQueryCondition( "city", EntityQueryOps.NEQ, 217 ) );

		EntityQuery subQuery = EntityQuery.or(
				new EntityQueryCondition( "email", EntityQueryOps.CONTAINS, "emailOne" ),
				new EntityQueryCondition( "email", EntityQueryOps.EQ, "emailTwo" )
		);

		query.add( subQuery );

		String eql = "name = 'someName' and city != 217 and (email contains 'emailOne' or email = 'emailTwo')";
		assertEquals( eql, query.toString() );
	}

	@Test
	public void allToString() {
		Sort sort = new Sort( new Sort.Order( Sort.Direction.ASC, "name" ), new Sort.Order( Sort.Direction.DESC, "city" ) );

		assertEquals( "", EntityQuery.all().toString() );
		assertEquals( "order by name ASC, city DESC", EntityQuery.all( sort ).toString() );
	}

	@Test
	public void queryWithOrderingToString() {
		Sort sort = new Sort( new Sort.Order( Sort.Direction.ASC, "name" ), new Sort.Order( Sort.Direction.DESC, "city" ) );

		EntityQuery query = new EntityQuery( EntityQueryOps.AND );
		query.setSort( sort );
		query.add( new EntityQueryCondition( "name", EntityQueryOps.EQ, "someName" ) );
		query.add( new EntityQueryCondition( "city", EntityQueryOps.NEQ, 217 ) );

		assertEquals( "name = 'someName' and city != 217 order by name ASC, city DESC", query.toString() );
	}

	@Test
	public void parseRawQuery() {
		String eql = "(name = 'someName' and city != 217) order by name ASC, city DESC";

		Sort sort = new Sort( new Sort.Order( Sort.Direction.ASC, "name" ), new Sort.Order( Sort.Direction.DESC, "city" ) );

		EntityQuery query = new EntityQuery( EntityQueryOps.AND );
		query.setSort( sort );
		query.add(
				EntityQuery.and(
						new EntityQueryCondition( "name", EntityQueryOps.EQ, new EQString( "someName" ) ),
						new EntityQueryCondition( "city", EntityQueryOps.NEQ, new EQValue( "217" ) )
				)
		);

		assertEquals( query, EntityQuery.parse( eql ) );
	}

	@Test
	public void equalsTakesTheSortOrderIntoAccount() {
		EntityQuery query = new EntityQuery( EntityQueryOps.AND );
		query.add( new EntityQueryCondition( "name", EntityQueryOps.EQ, "someName" ) );

		EntityQuery other = new EntityQuery( EntityQueryOps.AND );
		other.add( new EntityQueryCondition( "name", EntityQueryOps.EQ, "someName" ) );

		assertEquals( query, other );

		other.setSort( new Sort( Sort.Direction.ASC, "name" ) );
		assertNotEquals( query, other );
	}

	@Test
	public void andOfNullValuesIsSameAsAll() {
		assertEquals( EntityQuery.all(), EntityQuery.and( null, null, null ) );
	}

	@Test
	public void onlyFirstSortIsKept() {
		EntityQuery merged = EntityQuery.and(
				EntityQuery.parse( "country = BE order by name ASC" ), null, EntityQuery.parse( "name = 'john' order by country DESC" )
		);

		assertEquals( EntityQuery.parse( "country = BE and name = 'john' order by name ASC" ), merged );
	}

	@Test
	public void mergeWithAllDoesNothing() {
		EntityQuery merged = EntityQuery.and( EntityQuery.parse( "country = BE" ), EntityQuery.all( new Sort( Sort.Direction.DESC, "name" ) ) );
		assertEquals( EntityQuery.parse( "country = BE order by name DESC" ), merged );
	}

	@Test
	public void toAndFromJson() throws Exception {
		Sort sort = new Sort( new Sort.Order( Sort.Direction.ASC, "name", Sort.NullHandling.NULLS_LAST ),
		                      new Sort.Order( Sort.Direction.DESC, "city" ).ignoreCase() );

		EntityQuery query = new EntityQuery( EntityQueryOps.AND );
		query.add( new EntityQueryCondition( "name", EntityQueryOps.EQ, "someName" ) );
		query.add( new EntityQueryCondition( "city", EntityQueryOps.NEQ, 217 ) );

		EntityQuery subQuery = EntityQuery.or(
				new EntityQueryCondition( "email", EntityQueryOps.CONTAINS, "emailOne" ),
				new EntityQueryCondition( "email", EntityQueryOps.EQ, "emailTwo" )
		);
		query.add( subQuery );

		String value = new ObjectMapper().writeValueAsString( query );

		EntityQuery q = new ObjectMapper().readValue( value, EntityQuery.class );
		assertEquals( query, q );
		assertEquals( "name = 'someName' and city != 217 and (email contains 'emailOne' or email = 'emailTwo')",
		              q.toString() );

		query.setSort( sort );
		value = new ObjectMapper().writeValueAsString( query );
		q = new ObjectMapper().readValue( value, EntityQuery.class );

		assertEquals( query, q );
		assertEquals( "name = 'someName' and city != 217 and (email contains 'emailOne' or email = 'emailTwo') order by name ASC, city DESC",
		              q.toString() );
	}

	@Test
	public void parseAndToString() {
		String eql =
				"name = 'john' and id > 10 and value is NULL and groups not in (A,B,'C') and date < tomorrow(+1) and year = years(2017,currentYear())";
		assertEquals( eql, EntityQuery.parse( eql ).toString() );
		assertEquals( eql, EntityQuery.of( eql ).toString() );
	}

	@Test
	public void duplicateAllowsNull() {
		EntityQuery query = EntityQuery.parse( "id = 1 and name contains 'x' order by x asc, z desc" );
		EntityQuery duplicate = EntityQuery.of( query );
		assertNotSame( duplicate, query );
		assertEquals( duplicate, query );

		assertEquals( EntityQuery.all(), EntityQuery.of( (EntityQuery) null ) );
	}

}
