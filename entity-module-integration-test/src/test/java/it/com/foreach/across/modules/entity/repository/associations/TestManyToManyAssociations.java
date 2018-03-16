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

package it.com.foreach.across.modules.entity.repository.associations;

import com.foreach.across.modules.entity.query.AssociatedEntityQueryExecutor;
import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.testmodules.springdata.business.Company;
import com.foreach.across.testmodules.springdata.business.Representative;
import com.foreach.across.testmodules.springdata.repositories.CompanyRepository;
import com.foreach.across.testmodules.springdata.repositories2.RepresentativeRepository;
import it.com.foreach.across.modules.entity.repository.TestRepositoryEntityRegistrar;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.Assert.*;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@WebAppConfiguration
@ContextConfiguration(classes = TestRepositoryEntityRegistrar.Config.class)
public class TestManyToManyAssociations
{
	private static boolean inserted = false;

	private static Company one, two, three;
	private static Representative john, joe, peter;

	@Autowired
	private EntityRegistry entityRegistry;

	@Autowired
	private RepresentativeRepository representativeRepository;

	@Autowired
	private CompanyRepository companyRepository;

	@Before
	public void insertTestData() {
		if ( !inserted ) {
			inserted = true;

			john = new Representative( "john", "John" );
			joe = new Representative( "joe", "Joe" );
			peter = new Representative( "peter", "Peter" );

			representativeRepository.save( Arrays.asList( john, joe, peter ) );

			one = new Company( "one", 1 );
			two = new Company( "two", 2 );
			three = new Company( "three", 3 );
			one.setRepresentatives( Collections.singleton( john ) );
			two.setRepresentatives( new HashSet<>( Arrays.asList( john, joe, peter ) ) );
			three.setRepresentatives( Collections.singleton( peter ) );

			companyRepository.save( Arrays.asList( one, two, three ) );
		}
	}

	@Test
	public void companyHasRepresentatives() {
		EntityConfiguration company = entityRegistry.getEntityConfiguration( Company.class );
		EntityAssociation association = company.association( "company.representatives" );

		assertNotNull( association );
		assertEquals(
				"Association name should be source entity name joined with source property name",
				"company.representatives", association.getName()
		);

		AssociatedEntityQueryExecutor<Representative> queryExecutor = association.getAttribute( AssociatedEntityQueryExecutor.class );

		verifyRelatedItems( queryExecutor, one, john );
		verifyRelatedItems( queryExecutor, two, john, joe, peter );
		verifyRelatedItems( queryExecutor, three, peter );
	}

	@Test
	public void companyRepresentativesShouldBeHiddenByDefault() {
		EntityConfiguration company = entityRegistry.getEntityConfiguration( Company.class );
		EntityAssociation association = company.association( "company.representatives" );

		assertNotNull( association );
		assertTrue( association.isHidden() );
	}

	@Test
	public void representativeHasCompanies() {
		EntityConfiguration representative = entityRegistry.getEntityConfiguration( Representative.class );
		EntityAssociation association = representative.association( "company.representatives" );

		assertNotNull( association );
		assertEquals(
				"Association name should be the reverse source entity name joined with source property name",
				"company.representatives", association.getName()
		);

		AssociatedEntityQueryExecutor<Company> queryExecutor = association.getAttribute( AssociatedEntityQueryExecutor.class );

		verifyRelatedItems( queryExecutor, john, one, two );
		verifyRelatedItems( queryExecutor, joe, two );
		verifyRelatedItems( queryExecutor, peter, two, three );
	}

	@Test
	public void representativeCompaniesShouldNotBeHidden() {
		EntityConfiguration representative = entityRegistry.getEntityConfiguration( Representative.class );
		EntityAssociation association = representative.association( "company.representatives" );

		assertNotNull( association );
		assertFalse( association.isHidden() );
	}

	@SuppressWarnings("unchecked")
	private void verifyRelatedItems( AssociatedEntityQueryExecutor queryExecutor, Object parent, Object... reps ) {
		assertNotNull( queryExecutor );

		Page page = queryExecutor.findAll( parent, EntityQuery.all(), new PageRequest( 0, 100 ) );
		assertNotNull( page );
		assertEquals( reps.length, page.getTotalElements() );
		assertTrue( page.getContent().containsAll( Arrays.asList( reps ) ) );
	}
}
