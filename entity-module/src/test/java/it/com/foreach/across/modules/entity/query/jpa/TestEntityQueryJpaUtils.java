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
import testmodules.springdata.business.Company;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Arne Vandamme
 */
public class TestEntityQueryJpaUtils extends AbstractQueryTest
{
	@Test
	public void companyByGroup() {
		EntityQuery query = EntityQuery.and( new EntityQueryCondition( "group.name", EntityQueryOps.EQ, "groupOne" ) );
		List<Company> found = companyRepository.findAll(
				EntityQueryJpaUtils.<Company>toSpecification( query )
		);

		assertEquals( 2, found.size() );
		assertTrue( found.contains( one ) );
		assertTrue( found.contains( two ) );
		assertFalse( found.contains( three ) );
	}

	@Test
	public void findAll() {
		EntityQuery query = new EntityQuery();
		List<Company> found = companyRepository.findAll( EntityQueryJpaUtils.<Company>toSpecification( query ) );

		assertNotNull( found );
		assertEquals( 3, found.size() );
		assertTrue( found.containsAll( Arrays.asList( one, two, three ) ) );
	}

	@Test
	public void eq() {
		EntityQuery query = EntityQuery.and( new EntityQueryCondition( "id", EntityQueryOps.EQ, "two" ) );
		Company found = companyRepository.findOne( EntityQueryJpaUtils.toSpecification( query ) );

		assertNotNull( found );
		assertEquals( two, found );
	}

	@Test
	public void neq() {
		EntityQuery query = EntityQuery.and( new EntityQueryCondition( "id", EntityQueryOps.NEQ, "two" ) );
		List<Company> found = companyRepository.findAll( EntityQueryJpaUtils.toSpecification( query ) );

		assertNotNull( found );
		assertEquals( 2, found.size() );
		assertTrue( found.containsAll( Arrays.asList( one, three ) ) );
	}

	@Test
	public void numericOperands() {
		EntityQuery query = EntityQuery.and( new EntityQueryCondition( "number", EntityQueryOps.GT, 1 ) );
		List<Company> found = companyRepository.findAll( EntityQueryJpaUtils.toSpecification( query ) );
		assertEquals( 2, found.size() );
		assertTrue( found.containsAll( Arrays.asList( two, three ) ) );

		query = EntityQuery.and( new EntityQueryCondition( "number", EntityQueryOps.GE, 1 ) );
		found = companyRepository.findAll( EntityQueryJpaUtils.toSpecification( query ) );
		assertEquals( 3, found.size() );
		assertTrue( found.containsAll( Arrays.asList( one, two, three ) ) );

		query = EntityQuery.and( new EntityQueryCondition( "number", EntityQueryOps.LT, 3 ) );
		found = companyRepository.findAll( EntityQueryJpaUtils.toSpecification( query ) );
		assertEquals( 2, found.size() );
		assertTrue( found.containsAll( Arrays.asList( one, two ) ) );

		query = EntityQuery.and( new EntityQueryCondition( "number", EntityQueryOps.LE, 3 ) );
		found = companyRepository.findAll( EntityQueryJpaUtils.toSpecification( query ) );
		assertEquals( 3, found.size() );
		assertTrue( found.containsAll( Arrays.asList( one, two, three ) ) );
	}

	@Test
	public void dateOperands() {
		EntityQuery query = EntityQuery.and(
				new EntityQueryCondition( "created", EntityQueryOps.EQ, asDate( "2015-01-17 13:30" ) ) );
		List<Company> found = companyRepository.findAll( EntityQueryJpaUtils.toSpecification( query ) );
		assertEquals( 1, found.size() );
		assertTrue( found.contains( one ) );

		query = EntityQuery.and(
				new EntityQueryCondition( "created", EntityQueryOps.NEQ, asDate( "2015-01-17 13:30" ) ) );
		found = companyRepository.findAll( EntityQueryJpaUtils.toSpecification( query ) );
		assertEquals( 2, found.size() );
		assertTrue( found.containsAll( Arrays.asList( two, three ) ) );

		query = EntityQuery.and(
				new EntityQueryCondition( "created", EntityQueryOps.GT, asDate( "2015-01-17 13:30" ) ) );
		found = companyRepository.findAll( EntityQueryJpaUtils.toSpecification( query ) );
		assertEquals( 2, found.size() );
		assertTrue( found.containsAll( Arrays.asList( two, three ) ) );

		query = EntityQuery.and(
				new EntityQueryCondition( "created", EntityQueryOps.GE, asDate( "2015-01-17 13:30" ) ) );
		found = companyRepository.findAll( EntityQueryJpaUtils.toSpecification( query ) );
		assertEquals( 3, found.size() );
		assertTrue( found.containsAll( Arrays.asList( one, two, three ) ) );

		query = EntityQuery.and(
				new EntityQueryCondition( "created", EntityQueryOps.LT, asDate( "2035-04-04 14:00" ) ) );
		found = companyRepository.findAll( EntityQueryJpaUtils.toSpecification( query ) );
		assertEquals( 2, found.size() );
		assertTrue( found.containsAll( Arrays.asList( one, two ) ) );

		query = EntityQuery.and(
				new EntityQueryCondition( "created", EntityQueryOps.LE, asDate( "2035-04-04 14:00" ) ) );
		found = companyRepository.findAll( EntityQueryJpaUtils.toSpecification( query ) );
		assertEquals( 3, found.size() );
		assertTrue( found.containsAll( Arrays.asList( one, two, three ) ) );
	}

	@Test
	public void in() {
		EntityQuery query = EntityQuery.and( new EntityQueryCondition( "id", EntityQueryOps.IN, "one", "two" ) );
		List<Company> found = companyRepository.findAll( EntityQueryJpaUtils.toSpecification( query ) );

		assertNotNull( found );
		assertEquals( 2, found.size() );
		assertTrue( found.containsAll( Arrays.asList( one, two ) ) );
	}

	@Test
	public void notIn() {
		EntityQuery query = EntityQuery.and( new EntityQueryCondition( "id", EntityQueryOps.NOT_IN, "one", "two" ) );
		List<Company> found = companyRepository.findAll( EntityQueryJpaUtils.toSpecification( query ) );

		assertNotNull( found );
		assertEquals( 1, found.size() );
		assertTrue( found.contains( three ) );
	}

	@Test
	public void contains() {
		EntityQuery query = EntityQuery.and(
				new EntityQueryCondition( "representatives", EntityQueryOps.CONTAINS, john )
		);
		List<Company> found = companyRepository.findAll( EntityQueryJpaUtils.toSpecification( query ) );

		assertNotNull( found );
		assertEquals( 2, found.size() );
		assertTrue( found.containsAll( Arrays.asList( one, two ) ) );
	}

	@Test
	public void notContains() {
		EntityQuery query = EntityQuery.and(
				new EntityQueryCondition( "representatives", EntityQueryOps.NOT_CONTAINS, john )
		);
		List<Company> found = companyRepository.findAll( EntityQueryJpaUtils.toSpecification( query ) );

		assertNotNull( found );
		assertEquals( 1, found.size() );
		assertTrue( found.contains( three ) );
	}

	@Test
	@SuppressWarnings("all")
	public void like() {
		EntityQuery query = EntityQuery.and( new EntityQueryCondition( "id", EntityQueryOps.LIKE, "on%" ) );
		List<Company> found = companyRepository.findAll( EntityQueryJpaUtils.toSpecification( query ) );
		assertEquals( Arrays.asList( one ), found );

		query = EntityQuery.and( new EntityQueryCondition( "id", EntityQueryOps.LIKE, "%wo" ) );
		found = companyRepository.findAll( EntityQueryJpaUtils.toSpecification( query ) );
		assertEquals( Arrays.asList( two ), found );

		query = EntityQuery.and( new EntityQueryCondition( "id", EntityQueryOps.LIKE, "%o%" ) );
		found = companyRepository.findAll( EntityQueryJpaUtils.toSpecification( query ) );
		assertEquals( Arrays.asList( one, two ), found );
	}

	@Test
	@SuppressWarnings("all")
	public void notLike() {
		EntityQuery query = EntityQuery.and( new EntityQueryCondition( "id", EntityQueryOps.NOT_LIKE, "on%" ) );
		List<Company> found = companyRepository.findAll( EntityQueryJpaUtils.toSpecification( query ) );
		assertEquals( 2, found.size() );
		assertTrue( found.containsAll( Arrays.asList( two, three ) ) );

		query = EntityQuery.and( new EntityQueryCondition( "id", EntityQueryOps.NOT_LIKE, "%wo" ) );
		found = companyRepository.findAll( EntityQueryJpaUtils.toSpecification( query ) );
		assertEquals( 2, found.size() );
		assertTrue( found.containsAll( Arrays.asList( one, three ) ) );

		query = EntityQuery.and( new EntityQueryCondition( "id", EntityQueryOps.NOT_LIKE, "%o%" ) );
		found = companyRepository.findAll( EntityQueryJpaUtils.toSpecification( query ) );
		assertEquals( 1, found.size() );
		assertTrue( found.contains( three ) );
	}

	@Test
	public void combined() {
		EntityQuery query = EntityQuery.and(
				new EntityQueryCondition( "id", EntityQueryOps.NEQ, "two" ),
				new EntityQueryCondition( "representatives", EntityQueryOps.CONTAINS, john )
		);
		List<Company> found = companyRepository.findAll( EntityQueryJpaUtils.toSpecification( query ) );

		assertNotNull( found );
		assertEquals( 1, found.size() );
		assertTrue( found.contains( one ) );
	}


}
