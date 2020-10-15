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
import com.foreach.across.testmodules.springdata.business.Company;
import com.foreach.across.testmodules.springdata.business.Representative;
import it.com.foreach.across.modules.entity.query.AbstractQueryTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Arne Vandamme
 */
public class ITEntityQueryJpaUtils extends AbstractQueryTest
{
	@Test
	public void companyByGroup() {
		EntityQuery query = EntityQuery.and( new EntityQueryCondition( "group.name", EntityQueryOps.EQ, "groupOne" ) );
		assertCompanyResults( query, one, two );
	}

	@Test
	public void findAll() {
		EntityQuery query = new EntityQuery();
		assertCompanyResults( query, one, two, three );
	}

	@Test
	public void eq() {
		EntityQuery query = EntityQuery.and( new EntityQueryCondition( "id", EntityQueryOps.EQ, "two" ) );
		assertCompanyResults( query, two );
	}

	@Test
	public void neq() {
		EntityQuery query = EntityQuery.and( new EntityQueryCondition( "id", EntityQueryOps.NEQ, "two" ) );
		assertCompanyResults( query, one, three );
	}

	@Test
	public void numericOperands() {
		assertCompanyResults(
				EntityQuery.and( new EntityQueryCondition( "number", EntityQueryOps.GT, 1 ) ),
				two, three
		);

		assertCompanyResults(
				EntityQuery.and( new EntityQueryCondition( "number", EntityQueryOps.GE, 1 ) ),
				one, two, three
		);

		assertCompanyResults(
				EntityQuery.and( new EntityQueryCondition( "number", EntityQueryOps.LT, 3 ) ),
				one, two
		);

		assertCompanyResults(
				EntityQuery.and( new EntityQueryCondition( "number", EntityQueryOps.LE, 3 ) ),
				one, two, three
		);
	}

	@Test
	public void dateOperands() {
		EntityQuery query = EntityQuery.and(
				new EntityQueryCondition( "created", EntityQueryOps.EQ, asDate( "2015-01-17 13:30" ) ) );
		assertCompanyResults( query, one );

		query = EntityQuery.and(
				new EntityQueryCondition( "created", EntityQueryOps.NEQ, asDate( "2015-01-17 13:30" ) ) );
		assertCompanyResults( query, two, three );

		query = EntityQuery.and(
				new EntityQueryCondition( "created", EntityQueryOps.GT, asDate( "2015-01-17 13:30" ) ) );
		assertCompanyResults( query, two, three );

		query = EntityQuery.and(
				new EntityQueryCondition( "created", EntityQueryOps.GE, asDate( "2015-01-17 13:30" ) ) );
		assertCompanyResults( query, one, two, three );

		query = EntityQuery.and(
				new EntityQueryCondition( "created", EntityQueryOps.LT, asDate( "2035-04-04 14:00" ) ) );
		assertCompanyResults( query, one, two );

		query = EntityQuery.and(
				new EntityQueryCondition( "created", EntityQueryOps.LE, asDate( "2035-04-04 14:00" ) ) );
		assertCompanyResults( query, one, two, three );
	}

	@Test
	public void in() {
		EntityQuery query = EntityQuery.and( new EntityQueryCondition( "id", EntityQueryOps.IN, "one", "two" ) );
		assertCompanyResults( query, one, two );
	}

	@Test
	public void notIn() {
		EntityQuery query = EntityQuery.and( new EntityQueryCondition( "id", EntityQueryOps.NOT_IN, "one", "two" ) );
		assertCompanyResults( query, three );
	}

	@Test
	public void contains() {
		EntityQuery query = EntityQuery.and(
				new EntityQueryCondition( "representatives", EntityQueryOps.CONTAINS, john )
		);
		assertCompanyResults( query, one, two );
	}

	@Test
	public void notContains() {
		EntityQuery query = EntityQuery.and(
				new EntityQueryCondition( "representatives", EntityQueryOps.NOT_CONTAINS, john )
		);
		assertCompanyResults( query, three );
	}

	@Test
	@SuppressWarnings("all")
	public void like() {
		EntityQuery query = EntityQuery.and( new EntityQueryCondition( "id", EntityQueryOps.LIKE, "on%" ) );
		assertCompanyResults( query, one );

		query = EntityQuery.and( new EntityQueryCondition( "id", EntityQueryOps.LIKE, "%wo" ) );
		assertCompanyResults( query, two );

		query = EntityQuery.and( new EntityQueryCondition( "id", EntityQueryOps.LIKE, "%o%" ) );
		assertCompanyResults( query, one, two );
	}

	@Test
	@SuppressWarnings("all")
	public void likeIgnoreCase() {
		final EntityQuery q = EntityQuery.and( new EntityQueryCondition( "id", EntityQueryOps.LIKE, "On%" ) );
		fallback( () -> assertCompanyResults( q ), () -> assertCompanyResults( q, one ) );

		EntityQuery query = EntityQuery.and( new EntityQueryCondition( "id", EntityQueryOps.LIKE_IC, "On%" ) );
		assertCompanyResults( query, one );

		query = EntityQuery.and( new EntityQueryCondition( "id", EntityQueryOps.LIKE_IC, "%WO" ) );
		assertCompanyResults( query, two );

		query = EntityQuery.and( new EntityQueryCondition( "id", EntityQueryOps.LIKE_IC, "%O%" ) );
		assertCompanyResults( query, one, two );
	}

	@Test
	@SuppressWarnings("all")
	public void notLike() {
		EntityQuery query = EntityQuery.and( new EntityQueryCondition( "id", EntityQueryOps.NOT_LIKE, "on%" ) );
		assertCompanyResults( query, two, three );

		query = EntityQuery.and( new EntityQueryCondition( "id", EntityQueryOps.NOT_LIKE, "%wo" ) );
		assertCompanyResults( query, one, three );

		query = EntityQuery.and( new EntityQueryCondition( "id", EntityQueryOps.NOT_LIKE, "%o%" ) );
		assertCompanyResults( query, three );
	}

	@Test
	@SuppressWarnings("all")
	public void notLikeIgnoreCase() {
		final EntityQuery q = EntityQuery.and( new EntityQueryCondition( "id", EntityQueryOps.NOT_LIKE, "On%" ) );
		fallback( () -> assertCompanyResults( q, one, two, three ), () -> assertCompanyResults( q, two, three ) );

		EntityQuery query = EntityQuery.and( new EntityQueryCondition( "id", EntityQueryOps.NOT_LIKE_IC, "On%" ) );
		assertCompanyResults( query, two, three );

		query = EntityQuery.and( new EntityQueryCondition( "id", EntityQueryOps.NOT_LIKE_IC, "%WO" ) );
		assertCompanyResults( query, one, three );

		query = EntityQuery.and( new EntityQueryCondition( "id", EntityQueryOps.NOT_LIKE_IC, "%O%" ) );
		assertCompanyResults( query, three );
	}

	@Test
	public void nullValues() {
		EntityQuery query = EntityQuery.and( new EntityQueryCondition( "status", EntityQueryOps.IS_NULL ) );
		assertCompanyResults( query, three );

		query = EntityQuery.and( new EntityQueryCondition( "status", EntityQueryOps.IS_NOT_NULL ) );
		assertCompanyResults( query, one, two );
	}

	@Test
	public void emptyCollections() {
		EntityQuery query = EntityQuery.and( new EntityQueryCondition( "representatives", EntityQueryOps.IS_EMPTY ) );
		assertCompanyResults( query, three );

		query = EntityQuery.and( new EntityQueryCondition( "representatives", EntityQueryOps.IS_NOT_EMPTY ) );
		assertCompanyResults( query, one, two );
	}

	@Test
	public void combined() {
		EntityQuery query = EntityQuery.and(
				new EntityQueryCondition( "id", EntityQueryOps.NEQ, "two" ),
				new EntityQueryCondition( "representatives", EntityQueryOps.CONTAINS, john )
		);
		assertCompanyResults( query, one );
	}

	@Test
	public void specialCharactersLookup() {
		EntityQuery query = EntityQuery.and( new EntityQueryCondition( "name", EntityQueryOps.IN, "John % Surname", "Joe ' Surname", "Peter \\ Surname" ) );
		assertRepresentativeResults( query, john, joe, peter );

		query = EntityQuery.and( new EntityQueryCondition( "name", EntityQueryOps.LIKE_IC, "% surname" ) );
		assertRepresentativeResults( query, john, joe, peter );

		query = EntityQuery.and( new EntityQueryCondition( "name", EntityQueryOps.LIKE, "%\\% Surname" ) );
		assertRepresentativeResults( query, john );

		query = EntityQuery.and( new EntityQueryCondition( "name", EntityQueryOps.LIKE_IC, "%' SURNAME" ) );
		assertRepresentativeResults( query, joe );

		query = EntityQuery.and( new EntityQueryCondition( "name", EntityQueryOps.LIKE_IC, "%\\\\ surname" ) );
		assertRepresentativeResults( query, peter );

		query = EntityQuery.and( new EntityQueryCondition( "name", EntityQueryOps.LIKE, "!\"#\\%-_&/()=;?´`|/\\\\'" ) );
		assertRepresentativeResults( query, weirdo );

		query = EntityQuery.and( new EntityQueryCondition( "name", EntityQueryOps.LIKE, "%_%" ) );
		assertRepresentativeResults( query, weirdo );

		query = EntityQuery.and( new EntityQueryCondition( "name", EntityQueryOps.LIKE, "%\\\\%" ) );
		assertRepresentativeResults( query, weirdo, peter );
	}

	protected boolean assertCompanyResults( EntityQuery query, Company... companies ) {
		List<Company> found = companyRepository.findAll( EntityQueryJpaUtils.toSpecification( query ) );
		assertEquals( companies.length, found.size() );
		assertTrue( found.containsAll( Arrays.asList( companies ) ) );
		return true;
	}

	protected void assertRepresentativeResults( EntityQuery query, Representative... representatives ) {
		List<Representative> found = representativeRepository.findAll( EntityQueryJpaUtils.toSpecification( query ) );
		assertEquals( representatives.length, found.size() );
		assertTrue( found.containsAll( Arrays.asList( representatives ) ) );
	}
}
