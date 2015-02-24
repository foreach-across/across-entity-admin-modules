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
package com.foreach.across.modules.entity.registrars.repository.associations;

import com.foreach.across.modules.entity.registrars.repository.TestRepositoryEntityRegistrar;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.testmodules.springdata.business.Client;
import com.foreach.across.modules.entity.testmodules.springdata.business.Company;
import com.foreach.across.modules.entity.views.EntityListView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Verifies that a @ManyToOne is registered as a @OneToMany on the source entity.
 * If entity Client refers to a single Company, then an association should be created on Company that represents
 * all clients linked to that Company.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = TestRepositoryEntityRegistrar.Config.class)
public class TestManyToOneAssociations
{
	@Autowired
	private EntityRegistry entityRegistry;

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

		assertTrue( association.hasView( EntityListView.VIEW_NAME ) );
	}
}
