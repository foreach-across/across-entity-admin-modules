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

package it.com.foreach.across.modules.entity.query.jpa;

import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.query.EntityQueryCondition;
import com.foreach.across.modules.entity.query.EntityQueryOps;
import com.foreach.across.modules.entity.query.jpa.EntityQueryJpaUtils;
import it.com.foreach.across.modules.entity.query.AbstractQueryTest;
import org.junit.Test;
import com.foreach.across.modules.entity.testmodules.springdata.business.Company;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Arne Vandamme
 */
public class TestEntityQueryJpaUtils extends AbstractQueryTest
{
	@Test
	public void companyByGroup() {
		EntityQuery query = EntityQuery.and( new EntityQueryCondition( "group.name", EntityQueryOps.EQ, "groupOne" ) );
		assertQueryResults( query, one, two );
	}

	@Test
	public void findAll() {
		EntityQuery query = new EntityQuery();
		assertQueryResults( query, one, two, three );
	}

	@Test
	public void eq() {
		EntityQuery query = EntityQuery.and( new EntityQueryCondition( "id", EntityQueryOps.EQ, "two" ) );
		assertQueryResults( query, two );
	}

	@Test
	public void neq() {
		EntityQuery query = EntityQuery.and( new EntityQueryCondition( "id", EntityQueryOps.NEQ, "two" ) );
		assertQueryResults( query, one, three );
	}

	@Test
	public void numericOperands() {
		assertQueryResults(
				EntityQuery.and( new EntityQueryCondition( "number", EntityQueryOps.GT, 1 ) ),
				two, three
		);

		assertQueryResults(
				EntityQuery.and( new EntityQueryCondition( "number", EntityQueryOps.GE, 1 ) ),
				one, two, three
		);

		assertQueryResults(
				EntityQuery.and( new EntityQueryCondition( "number", EntityQueryOps.LT, 3 ) ),
				one, two
		);

		assertQueryResults(
				EntityQuery.and( new EntityQueryCondition( "number", EntityQueryOps.LE, 3 ) ),
				one, two, three
		);
	}

	@Test
	public void dateOperands() {
		EntityQuery query = EntityQuery.and(
				new EntityQueryCondition( "created", EntityQueryOps.EQ, asDate( "2015-01-17 13:30" ) ) );
		assertQueryResults( query, one );

		query = EntityQuery.and(
				new EntityQueryCondition( "created", EntityQueryOps.NEQ, asDate( "2015-01-17 13:30" ) ) );
		assertQueryResults( query, two, three );

		query = EntityQuery.and(
				new EntityQueryCondition( "created", EntityQueryOps.GT, asDate( "2015-01-17 13:30" ) ) );
		assertQueryResults( query, two, three );

		query = EntityQuery.and(
				new EntityQueryCondition( "created", EntityQueryOps.GE, asDate( "2015-01-17 13:30" ) ) );
		assertQueryResults( query, one, two, three );

		query = EntityQuery.and(
				new EntityQueryCondition( "created", EntityQueryOps.LT, asDate( "2035-04-04 14:00" ) ) );
		assertQueryResults( query, one, two );

		query = EntityQuery.and(
				new EntityQueryCondition( "created", EntityQueryOps.LE, asDate( "2035-04-04 14:00" ) ) );
		assertQueryResults( query, one, two, three );
	}

	@Test
	public void in() {
		EntityQuery query = EntityQuery.and( new EntityQueryCondition( "id", EntityQueryOps.IN, "one", "two" ) );
		assertQueryResults( query, one, two );
	}

	@Test
	public void notIn() {
		EntityQuery query = EntityQuery.and( new EntityQueryCondition( "id", EntityQueryOps.NOT_IN, "one", "two" ) );
		assertQueryResults( query, three );
	}

	@Test
	public void contains() {
		EntityQuery query = EntityQuery.and(
				new EntityQueryCondition( "representatives", EntityQueryOps.CONTAINS, john )
		);
		assertQueryResults( query, one, two );
	}

	@Test
	public void notContains() {
		EntityQuery query = EntityQuery.and(
				new EntityQueryCondition( "representatives", EntityQueryOps.NOT_CONTAINS, john )
		);
		assertQueryResults( query, three );
	}

	@Test
	@SuppressWarnings("all")
	public void like() {
		EntityQuery query = EntityQuery.and( new EntityQueryCondition( "id", EntityQueryOps.LIKE, "on%" ) );
		assertQueryResults( query, one );

		query = EntityQuery.and( new EntityQueryCondition( "id", EntityQueryOps.LIKE, "%wo" ) );
		assertQueryResults( query, two );

		query = EntityQuery.and( new EntityQueryCondition( "id", EntityQueryOps.LIKE, "%o%" ) );
		assertQueryResults( query, one, two );
	}

	@Test
	@SuppressWarnings("all")
	public void notLike() {
		EntityQuery query = EntityQuery.and( new EntityQueryCondition( "id", EntityQueryOps.NOT_LIKE, "on%" ) );
		assertQueryResults( query, two, three );

		query = EntityQuery.and( new EntityQueryCondition( "id", EntityQueryOps.NOT_LIKE, "%wo" ) );
		assertQueryResults( query, one, three );

		query = EntityQuery.and( new EntityQueryCondition( "id", EntityQueryOps.NOT_LIKE, "%o%" ) );
		assertQueryResults( query, three );
	}

	@Test
	public void nullValues() {
		EntityQuery query = EntityQuery.and( new EntityQueryCondition( "status", EntityQueryOps.IS_NULL ) );
		assertQueryResults( query, three );

		query = EntityQuery.and( new EntityQueryCondition( "status", EntityQueryOps.IS_NOT_NULL ) );
		assertQueryResults( query, one, two );
	}

	@Test
	public void emptyCollections() {
		EntityQuery query = EntityQuery.and( new EntityQueryCondition( "representatives", EntityQueryOps.IS_EMPTY ) );
		assertQueryResults( query, three );

		query = EntityQuery.and( new EntityQueryCondition( "representatives", EntityQueryOps.IS_NOT_EMPTY ) );
		assertQueryResults( query, one, two );
	}

	@Test
	public void combined() {
		EntityQuery query = EntityQuery.and(
				new EntityQueryCondition( "id", EntityQueryOps.NEQ, "two" ),
				new EntityQueryCondition( "representatives", EntityQueryOps.CONTAINS, john )
		);
		assertQueryResults( query, one );
	}

	protected void assertQueryResults( EntityQuery query, Company... companies ) {
		List<Company> found = companyRepository.findAll( EntityQueryJpaUtils.toSpecification( query ) );
		assertEquals( companies.length, found.size() );
		assertTrue( found.containsAll( Arrays.asList( companies ) ) );
	}
}
