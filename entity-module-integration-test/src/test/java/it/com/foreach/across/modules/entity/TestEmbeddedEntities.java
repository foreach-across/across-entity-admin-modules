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

package it.com.foreach.across.modules.entity;

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.bootstrapui.elements.NumericFormElement;
import com.foreach.across.modules.bootstrapui.elements.SelectFormElement;
import com.foreach.across.modules.bootstrapui.elements.TextboxFormElement;
import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistryProvider;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderService;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.elements.ViewElementFieldset;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.test.support.AbstractViewElementTemplateTest;
import com.foreach.across.testmodules.springdata.SpringDataJpaModule;
import com.foreach.across.testmodules.springdata.business.Client;
import com.foreach.across.testmodules.springdata.business.ClientGroup;
import com.foreach.across.testmodules.springdata.business.Company;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Arne Vandamme
 */
@ContextConfiguration(classes = TestEmbeddedEntities.Config.class)
public class TestEmbeddedEntities extends AbstractViewElementTemplateTest
{
	@Autowired
	private EntityRegistry entityRegistry;

	@Autowired
	private EntityPropertyRegistryProvider entityPropertyRegistryProvider;

	@Autowired
	private EntityViewElementBuilderService viewElementBuilderService;

	@Test
	public void persistenceMetadataShouldBeSet() {
		EntityPropertyRegistry registry = entityPropertyRegistryProvider.get( Company.class );
		assertTrue( isEmbeddedProperty( registry.getProperty( "address" ) ) );
		assertFalse( isEmbeddedProperty( registry.getProperty( "group" ) ) );
		assertFalse( isEmbeddedProperty( registry.getProperty( "class" ) ) );

		registry = entityPropertyRegistryProvider.get( ClientGroup.class );
		assertTrue( isEmbeddedProperty( registry.getProperty( "id" ) ) );

		registry = entityPropertyRegistryProvider.get( Client.class );
		assertTrue( isEmbeddedProperty( registry.getProperty( "phones" ) ) );
	}

	@Test
	public void embeddedObjectAttributeShouldNotBeSetOnNonEmbeddedTypes() {
		EntityPropertyRegistry registry = entityPropertyRegistryProvider.get( Company.class );
		assertFalse( registry.getProperty( "group" ).hasAttribute( EntityAttributes.IS_EMBEDDED_OBJECT ) );
		assertFalse( registry.getProperty( "class" ).hasAttribute( EntityAttributes.IS_EMBEDDED_OBJECT ) );

		registry = entityPropertyRegistryProvider.get( Client.class );
		assertFalse( registry.getProperty( "aliases" ).hasAttribute( EntityAttributes.IS_EMBEDDED_OBJECT ) );
	}

	@Test
	public void primitiveTypesShouldBehaveAsNonEmbedded() {
		EntityPropertyRegistry registry = entityPropertyRegistryProvider.get( Client.class );
		assertFalse( isEmbeddedProperty( registry.getProperty( "aliases" ) ) );
	}

	private boolean isEmbeddedProperty( EntityPropertyDescriptor descriptor ) {
		return Boolean.TRUE.equals( descriptor.getAttribute( EntityAttributes.IS_EMBEDDED_OBJECT ) );
	}

	@Test
	public void fieldsetForAddress() {
		EntityConfiguration entityConfiguration = entityRegistry.getEntityConfiguration( Company.class );
		EntityPropertyDescriptor address = entityConfiguration.getPropertyRegistry().getProperty( "address" );

		ViewElementFieldset fieldset = (ViewElementFieldset) viewElementBuilderService
				.getElementBuilder( address, ViewElementMode.FORM_WRITE )
				.build( new DefaultViewElementBuilderContext() );

		assertNotNull( fieldset );

		assertTrue( fieldset.find( "address.street", TextboxFormElement.class ).isPresent() );
		assertTrue( fieldset.find( "address.zipCode", NumericFormElement.class ).isPresent() );
		assertTrue( fieldset.find( "address.country", SelectFormElement.class ).isPresent() );
	}

	@Configuration
	protected static class Config implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			context.addModule( new SpringSecurityModule() );
			context.addModule( new AdminWebModule() );
			context.addModule( new EntityModule() );

			AcrossHibernateJpaModule hibernateModule = new AcrossHibernateJpaModule();
			hibernateModule.setHibernateProperty( "hibernate.hbm2ddl.auto", "create" );
			context.addModule( hibernateModule );

			context.addModule( new SpringDataJpaModule() );
		}
	}
}
