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
import com.foreach.across.testmodules.springdata.repositories.ClientGroupRepository;
import com.foreach.across.testmodules.springdata.repositories.ClientRepository;
import com.foreach.across.testmodules.springdata.repositories.CompanyRepository;
import com.foreach.across.testmodules.springdata.repositories.GroupRepository;
import it.com.foreach.across.modules.entity.repository.TestRepositoryEntityRegistrar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies that a @ManyToOne is registered as a @OneToMany on the source entity.
 * If entity Client refers to a single Company, then an association should be created on Company that represents
 * all clients linked to that Company.
 */
@ExtendWith(SpringExtension.class)
@DirtiesContext
@AcrossWebAppConfiguration
@ContextConfiguration(classes = TestRepositoryEntityRegistrar.Config.class)
public class TestOneToManyAssociations
{
	private static boolean inserted = false;

	private static Group groupOne, groupTwo;
	private static Company one, two, three;
	private static Client john, joe, peter;
	private static ClientGroup clientGroup;

	@Autowired
	private EntityRegistry entityRegistry;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private ClientGroupRepository clientGroupRepository;

	@Autowired
	private CompanyRepository companyRepository;

	@BeforeEach
	public void insertTestData() {
		if ( !inserted ) {
			inserted = true;

			groupOne = new Group( "groupOne" );
			groupTwo = new Group( "groupTwo" );
			groupRepository.saveAll( Arrays.asList( groupOne, groupTwo ) );

			one = new Company( "one", 1 );
			two = new Company( "two", 2 );
			three = new Company( "three", 3 );

			companyRepository.saveAll( Arrays.asList( one, two, three ) );

			john = new Client( "john", one );
			joe = new Client( "joe", two );
			peter = new Client( "peter", two );

			clientRepository.saveAll( Arrays.asList( john, joe, peter ) );

			ClientGroupId clientGroupId = new ClientGroupId();
			clientGroupId.setGroup( groupOne );
			clientGroupId.setClient( john );

			clientGroup = new ClientGroup();
			clientGroup.setId( clientGroupId );
			clientGroup.setRole( "client-group-role" );

			clientGroupRepository.save( clientGroup );

			john.setGroups( new HashSet<>( Arrays.asList( clientGroup ) ) );
			clientRepository.save( john );
		}
	}

	@Test
	public void clientHasAssociationToClientGroups() {
		EntityConfiguration clientGroup = entityRegistry.getEntityConfiguration( ClientGroup.class );
		EntityConfiguration client = entityRegistry.getEntityConfiguration( Client.class );

		EntityAssociation association = client.association( "client.groups" );

		assertNotNull( association );
		assertEquals(
				"client.groups", association.getName(), "Association name should be source entity name joined with target property name"
		);

		assertSame( client, association.getSourceEntityConfiguration() );
		assertSame( clientGroup, association.getTargetEntityConfiguration() );

		assertNull( association.getSourceProperty(), "OneToMany should have only the target property set" );
		assertNotNull( association.getTargetProperty(), "OneToMany should have only the target property set" );

		assertEquals( "id.client", association.getTargetProperty().getName() );

		assertTrue( association.hasView( EntityView.LIST_VIEW_NAME ) );
	}

	@Test
	public void clientGroupsShouldBeHiddenByDefault() {
		EntityConfiguration client = entityRegistry.getEntityConfiguration( Client.class );
		EntityAssociation association = client.association( "client.groups" );

		assertNotNull( association );
		assertTrue( association.isHidden() );
	}

	@Test
	public void clientHasGroup() {
		EntityConfiguration client = entityRegistry.getEntityConfiguration( Client.class );
		EntityAssociation association = client.association( "client.groups" );

		assertNotNull( association );
		AssociatedEntityQueryExecutor<ClientGroup> executor = association.getAttribute( AssociatedEntityQueryExecutor.class );

		verifyClientGroups( executor, john, clientGroup );
	}

	private void verifyClientGroups( AssociatedEntityQueryExecutor<ClientGroup> executor,
	                                 Client client,
	                                 ClientGroup... groups ) {
		assertNotNull( executor );

		List<ClientGroup> result = executor.findAll( client, EntityQuery.all() );
		assertNotNull( result );
		assertEquals( groups.length, result.size() );
		assertTrue( result.containsAll( Arrays.asList( groups ) ) );
	}
}
