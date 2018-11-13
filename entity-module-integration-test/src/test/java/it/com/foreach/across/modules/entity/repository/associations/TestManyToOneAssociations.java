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
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.test.AcrossWebAppConfiguration;
import com.foreach.across.testmodules.springdata.business.*;
import com.foreach.across.testmodules.springdata.repositories.ClientRepository;
import com.foreach.across.testmodules.springdata.repositories.CompanyRepository;
import it.com.foreach.across.modules.entity.repository.TestRepositoryEntityRegistrar;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Verifies that a @ManyToOne is registered as a @OneToMany on the source entity.
 * If entity Client refers to a single Company, then an association should be created on Company that represents
 * all clients linked to that Company.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@AcrossWebAppConfiguration
@ContextConfiguration(classes = TestRepositoryEntityRegistrar.Config.class)
public class TestManyToOneAssociations
{
	private static boolean inserted = false;

	private static Company one, two, three;
	private static Client john, joe, peter;

	@Autowired
	private EntityRegistry entityRegistry;

	@Autowired
	private CompanyRepository companyRepository;

	@Autowired
	private ClientRepository clientRepository;

	@Before
	public void insertTestData() {
		if ( !inserted ) {
			inserted = true;

			one = new Company( "one", 1 );
			two = new Company( "two", 2 );
			three = new Company( "three", 3 );

			companyRepository.save( Arrays.asList( one, two, three ) );

			john = new Client( "john", one );
			joe = new Client( "joe", two );
			peter = new Client( "peter", two );

			clientRepository.save( Arrays.asList( john, joe, peter ) );
		}
	}

	@Test
	public void companyShouldHaveAnAssociationToItsClients() {
		EntityConfiguration company = entityRegistry.getEntityConfiguration( Company.class );
		EntityConfiguration client = entityRegistry.getEntityConfiguration( Client.class );

		EntityAssociation association = company.association( "client.company" );

		assertNotNull( association );
		assertEquals(
				"Association name should be target entity name joined with target property name",
				"client.company", association.getName()
		);

		assertSame( company, association.getSourceEntityConfiguration() );
		assertSame( client, association.getTargetEntityConfiguration() );

		assertNull(
				"Regular ManyToOne should not have a source property as the association starts at the other end",
				association.getSourceProperty()
		);
		assertNotNull( association.getTargetProperty() );
		assertSame( client.getPropertyRegistry().getProperty( "company" ), association.getTargetProperty() );

		assertTrue( association.hasView( EntityView.LIST_VIEW_NAME ) );
	}

	@Test
	public void companyHasClients() {
		EntityConfiguration company = entityRegistry.getEntityConfiguration( Company.class );
		EntityAssociation association = company.association( "client.company" );

		assertNotNull( association );

		AssociatedEntityQueryExecutor<Client> executor
				= association.getAttribute( AssociatedEntityQueryExecutor.class );

		verifyClients( executor, one, john );
		verifyClients( executor, two, joe, peter );
		verifyClients( executor, three );
	}

	@Test
	public void groupShouldHaveAnAssociationToItsClientGroups() {
		EntityConfiguration group = entityRegistry.getEntityConfiguration( Group.class );
		EntityConfiguration clientGroup = entityRegistry.getEntityConfiguration( ClientGroup.class );

		EntityAssociation association = group.association( "clientGroup.id.group" );

		assertNotNull( association );
		assertEquals(
				"Association name should be target entity name joined with target property name",
				"clientGroup.id.group", association.getName()
		);

		assertSame( group, association.getSourceEntityConfiguration() );
		assertSame( clientGroup, association.getTargetEntityConfiguration() );

		assertNull(
				"Regular ManyToOne should not have a source property as the association starts at the other end",
				association.getSourceProperty()
		);
		assertNotNull( association.getTargetProperty() );
		assertSame( clientGroup.getPropertyRegistry().getProperty( "id.group" ), association.getTargetProperty() );

		assertTrue( association.hasView( EntityView.LIST_VIEW_NAME ) );
	}

	@Test
	public void companyShouldNotHaveAssociationToItsCarsAsTheRepositoryDoesNotSupportSpecifications() {
		EntityConfiguration company = entityRegistry.getEntityConfiguration( Company.class );
		EntityConfiguration car = entityRegistry.getEntityConfiguration( Car.class );

		assertNotNull( car );

		EntityAssociation association = company.association( "car.company" );
		assertNull( association );
	}

	@Test
	public void companyClientDefaultViewsView() {
		EntityConfiguration company = entityRegistry.getEntityConfiguration( Company.class );
		EntityAssociation association = company.association( "client.company" );

		assertNotNull( association );
		assertTrue( association.hasView( EntityView.LIST_VIEW_NAME ) );
		assertTrue( association.hasView( EntityView.CREATE_VIEW_NAME ) );
		assertTrue( association.hasView( EntityView.UPDATE_VIEW_NAME ) );
		assertTrue( association.hasView( EntityView.DELETE_VIEW_NAME ) );
	}

	private void verifyClients( AssociatedEntityQueryExecutor<Client> executor, Company company, Client... clients ) {
		assertNotNull( executor );

		List<Client> result = executor.findAll( company, EntityQuery.all() );
		assertNotNull( result );
		assertEquals( clients.length, result.size() );
		assertTrue( result.containsAll( Arrays.asList( clients ) ) );
	}
}
