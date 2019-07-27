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

import com.foreach.across.core.EmptyAcrossModule;
import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.views.*;
import com.foreach.across.modules.entity.views.processors.TemplateViewProcessor;
import com.foreach.across.modules.entity.views.processors.support.EntityViewProcessorRegistry;
import com.foreach.across.modules.entity.web.EntityConfigurationLinkBuilder;
import com.foreach.across.modules.entity.web.EntityLinkBuilder;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.test.AcrossTestConfiguration;
import com.foreach.across.test.AcrossWebAppConfiguration;
import com.foreach.across.testmodules.springdata.SpringDataJpaModule;
import com.foreach.across.testmodules.springdata.business.Car;
import com.foreach.across.testmodules.springdata.business.Client;
import com.foreach.across.testmodules.springdata.business.Group;
import com.foreach.across.testmodules.springdata.repositories.ClientRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Persistable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@AcrossWebAppConfiguration(classes = TestCustomizingEntityConfiguration.Config.class)
public class TestCustomizingEntityConfiguration
{
	@Autowired
	private EntityRegistry entityRegistry;

	private EntityConfiguration configuration;

	@Before
	public void retrieveEntityConfiguration() {
		configuration = entityRegistry.getEntityConfiguration( Client.class );
	}

	@Test
	public void clientShouldBeRegistered() {
		assertTrue( entityRegistry.contains( Client.class ) );

		EntityConfiguration configuration = entityRegistry.getEntityConfiguration( Client.class );
		assertNotNull( configuration );
		assertEquals( "client", configuration.getName() );
		assertEquals( "client", configuration.getPropertyRegistry().getId() );
	}

	@Test
	public void allPersistableEntitiesShouldHaveCustomViewElementBuilderForId() {
		for ( EntityConfiguration configuration : entityRegistry.getEntities() ) {
			if ( Persistable.class.isAssignableFrom( configuration.getEntityType() ) ) {
				ViewElementLookupRegistry lookupRegistry
						= configuration.getPropertyRegistry().getProperty( "id" )
						               .getAttribute( ViewElementLookupRegistry.class );

				assertNotNull( lookupRegistry );
				assertNotNull( lookupRegistry.getViewElementBuilder( ViewElementMode.LIST_VALUE ) );
			}
		}
	}

	@Test
	public void attributesShouldBeSet() {
		EntityConfiguration configuration = entityRegistry.getEntityConfiguration( Client.class );

		assertNotNull( configuration.getAttribute( EntityLinkBuilder.class ) );
	}

	@Test
	public void customPropertiesOnEntity() {
		EntityPropertyRegistry registry = configuration.getPropertyRegistry();
		assertNotNull( registry );

		EntityPropertyDescriptor descriptor = registry.getProperty( "someprop" );
		assertNotNull( descriptor );
		assertEquals( "someprop", descriptor.getName() );
		assertEquals( "Some property", descriptor.getDisplayName() );
	}

	@Test
	public void crudListViewShouldBeModified() {
		EntityViewFactory viewFactory = configuration.getViewFactory( EntityView.LIST_VIEW_NAME );
		assertNotNull( viewFactory );
	}

	@Test
	public void extraViewsShouldExist() {
		assertTrue( configuration.hasView( "some-extra-view" ) );

		EntityViewFactory viewFactory = configuration.getViewFactory( "some-extra-view" );
		assertNotNull( viewFactory );
		assertTrue( viewFactory instanceof DispatchingEntityViewFactory );

		EntityViewProcessorRegistry processors = ( (DispatchingEntityViewFactory) viewFactory ).getProcessorRegistry();
		Optional<TemplateViewProcessor> templateViewProcessor = processors.getProcessor( TemplateViewProcessor.class.getName(), TemplateViewProcessor.class );
		assertTrue( templateViewProcessor.isPresent() );
		templateViewProcessor.ifPresent( p -> assertEquals( new TemplateViewProcessor( "th/someTemplate" ), p ) );
	}

	@Test
	public void customizedClientLabel() {
		EntityConfiguration<Client> config = entityRegistry.getEntityConfiguration( Client.class );
		assertEquals( "fixed", config.getLabel( new Client() ) );
	}

	@Test
	public void customizedPersistableLabel() {
		EntityConfiguration<Car> config = entityRegistry.getEntityConfiguration( Car.class );
		Car c = new Car();
		assertEquals( "false", config.getLabel( c ) );

		c.setNew( true );
		assertEquals( "true", config.getLabel( c ) );
	}

	@Test
	public void everyEntityShouldHaveASeparateViewElementLookupRegistry() {
		EntityConfiguration groupConfig = entityRegistry.getEntityConfiguration( Group.class );
		EntityConfiguration clientConfig = entityRegistry.getEntityConfiguration( Client.class );

		ViewElementLookupRegistry groupLookupRegistry = groupConfig.getAttribute( ViewElementLookupRegistry.class );
		assertThat( groupLookupRegistry ).isNotNull();
		ViewElementLookupRegistry clientLookupRegistry = clientConfig.getAttribute( ViewElementLookupRegistry.class );
		assertThat( clientLookupRegistry ).isNotNull();

		assertThat( groupLookupRegistry ).isNotSameAs( clientLookupRegistry );
	}

	@Configuration
	@AcrossTestConfiguration(modules = { EntityModule.NAME, AdminWebModule.NAME, SpringSecurityModule.NAME })
	protected static class Config
	{
		@Bean
		public AcrossHibernateJpaModule acrossHibernateJpaModule() {
			AcrossHibernateJpaModule hibernateModule = new AcrossHibernateJpaModule();
			hibernateModule.setHibernateProperty( "hibernate.hbm2ddl.auto", "create" );
			return hibernateModule;
		}

		@Bean
		public SpringDataJpaModule springDataJpaModule() {
			SpringDataJpaModule springDataJpaModule = new SpringDataJpaModule();
			springDataJpaModule.expose( ClientRepository.class );
			return springDataJpaModule;
		}

		@Bean
		public EmptyAcrossModule customModule() {
			EmptyAcrossModule customModule = new EmptyAcrossModule( "customizingModule" );
			customModule.addApplicationContextConfigurer( ModuleConfig.class );
			return customModule;
		}
	}

	@Configuration
	protected static class ModuleConfig implements EntityConfigurer
	{
		@Override
		@SuppressWarnings("unchecked")
		public void configure( EntitiesConfigurationBuilder entities ) {
			entities.all()
			        .attribute( EntityLinkBuilder.class, mock( EntityConfigurationLinkBuilder.class ) );

			entities.assignableTo( Persistable.class )
			        .label( "new" )
			        .properties(
					        props -> props.property( "id" )
					                      .viewElementBuilder( ViewElementMode.LIST_VALUE,
					                                           mock( ViewElementBuilder.class ) )
			        );

			entities.withType( Client.class )
			        .properties(
					        props -> props.label( "someprop" ).and()
					                      .property( "someprop" ).displayName( "Some property" )
					                      .spelValueFetcher( "'fixed'" )
			        )
			        .view(
					        "some-extra-view",
					        vb -> vb.template( "th/someTemplate" )
					                .properties(
							                props -> props
									                .property( "calculated" ).displayName( "Calculated" )
									                .and()
									                .property( "group-membership" ).displayName( "Group membership" )
									                .spelValueFetcher( "groups.size()" )
					                )
			        );
		}
	}
}
