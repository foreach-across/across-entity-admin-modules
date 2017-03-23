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

package it.com.foreach.across.modules.entity.repository;

import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.annotations.EntityValidator;
import com.foreach.across.modules.entity.query.EntityQueryExecutor;
import com.foreach.across.modules.entity.query.jpa.EntityQueryJpaExecutor;
import com.foreach.across.modules.entity.query.querydsl.EntityQueryQueryDslExecutor;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityModel;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.testmodules.springdata.business.*;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.EntityViewFactory;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.test.AcrossTestConfiguration;
import com.foreach.across.test.AcrossWebAppConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryInformation;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.Validator;
import com.foreach.across.modules.entity.testmodules.springdata.SpringDataJpaModule;
import com.foreach.across.modules.entity.testmodules.springdata.repositories.ClientRepository;

import javax.validation.metadata.PropertyDescriptor;
import java.io.Serializable;

import static org.junit.Assert.*;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@AcrossWebAppConfiguration
public class TestRepositoryEntityRegistrar
{
	@Autowired
	private EntityRegistry entityRegistry;

	@Autowired
	private ConversionService mvcConversionService;

	@Autowired
	private ClientRepository clientRepository;

	@EntityValidator
	private SmartValidator entityValidator;

	@Test
	public void expectedEntitiesShouldBeRegisteredWithTheirAssociations() {
		verify( Client.class )
				.hasRepository()
				.hasAssociation( "client.groups", false ).from( "groups" ).to( ClientGroup.class, "id.client" ).and()
				.hasAssociation( "clientGroup.id.client", true ).from( null ).to( ClientGroup.class, "id.client" );

		verify( Company.class )
				.hasRepository()
				.hasAssociation( "client.company", true ).from( null ).to( Client.class, "company" ).and()
				.hasAssociation( "company.representatives", false ).from( "representatives" ).to( Representative.class );

		// not a JpaSpecificationExecutor so associations can't be built
		verify( Car.class )
				.hasRepository();

		verify( Group.class )
				.hasRepository()
				.hasAssociation( "company.group", true ).from( null ).to( Company.class, "group" ).and()
				.hasAssociation( "clientGroup.id.group", ClientGroup.class, true );

		verify( ClientGroup.class )
				.hasRepository();

		verify( Representative.class )
				.hasRepository()
				.hasAssociation( "company.representatives", true ).from( null ).to( Company.class, "representatives" );
	}

	private EntityVerifier verify( Class<?> entityType ) {
		return new EntityVerifier( entityType );
	}

	private class EntityVerifier
	{
		private final EntityConfiguration<?> configuration;

		public EntityVerifier( Class<?> entityType ) {
			configuration = entityRegistry.getEntityConfiguration( entityType );
			assertNotNull( configuration );
		}

		public EntityVerifier hasRepository() {
			assertTrue( "Repository not present", configuration.hasAttribute( Repository.class ) );
			return this;
		}

		public EntityAssociationVerifier hasAssociation( String associationName, boolean visible ) {
			return new EntityAssociationVerifier( this, configuration, associationName ).visible( visible );
		}

		public EntityVerifier hasAssociation( String associationName, Class<?> targetClass, boolean visible ) {
			EntityAssociation association = configuration.association( associationName );
			assertNotNull( "Association " + associationName + " not present", association );
			assertSame( entityRegistry.getEntityConfiguration( targetClass ), association.getTargetEntityConfiguration() );

			assertNotEquals( visible, association.isHidden() );
			assertTrue( association.hasView( EntityView.LIST_VIEW_NAME ) );
			assertTrue( association.hasView( EntityView.CREATE_VIEW_NAME ) );
			assertTrue( association.hasView( EntityView.UPDATE_VIEW_NAME ) );
			assertTrue( association.hasView( EntityView.DELETE_VIEW_NAME ) );

			return this;
		}
	}

	private class EntityAssociationVerifier
	{
		private final EntityAssociation association;
		private final EntityVerifier parent;

		public EntityAssociationVerifier( EntityVerifier parent, EntityConfiguration<?> configuration, String associationName ) {
			this.parent = parent;
			association = configuration.association( associationName );
			assertNotNull( "Association " + associationName + " not present", association );

			assertTrue( association.hasView( EntityView.LIST_VIEW_NAME ) );
			assertTrue( association.hasView( EntityView.CREATE_VIEW_NAME ) );
			assertTrue( association.hasView( EntityView.UPDATE_VIEW_NAME ) );
			assertTrue( association.hasView( EntityView.DELETE_VIEW_NAME ) );
		}

		public EntityAssociationVerifier from( String propertyName ) {
			if ( propertyName == null ) {
				assertNull( association.getSourceProperty() );
			}
			else {
				assertEquals( propertyName, association.getSourceProperty().getName() );
			}
			return this;
		}

		public EntityAssociationVerifier to( Class<?> targetClass ) {
			return to( targetClass, null );
		}

		public EntityAssociationVerifier to( Class<?> targetClass, String targetPropertyName ) {
			assertSame( entityRegistry.getEntityConfiguration( targetClass ), association.getTargetEntityConfiguration() );
			if ( targetPropertyName == null ) {
				assertNull( "No target property was expected", association.getTargetProperty() );

			}
			else {
				assertSame(
						association.getTargetProperty(),
						association.getTargetEntityConfiguration().getPropertyRegistry().getProperty( targetPropertyName )
				);
			}
			return this;
		}

		public EntityAssociationVerifier visible( boolean visible ) {
			assertNotEquals( visible, association.isHidden() );
			return this;
		}

		public EntityVerifier and() {
			return parent;
		}
	}

	@Test
	public void clientShouldBeRegisteredWithRepositoryInformation() {
		assertEquals( 6, entityRegistry.getEntities().size() );
		assertTrue( entityRegistry.contains( Client.class ) );

		EntityConfiguration<?> configuration = entityRegistry.getEntityConfiguration( Client.class );
		assertNotNull( configuration );

		CrudRepository<Client, Long> repository = configuration.getAttribute( Repository.class );
		assertNotNull( repository );

		RepositoryFactoryInformation<Client, Long> repositoryFactoryInformation
				= configuration.getAttribute( RepositoryFactoryInformation.class );
		assertNotNull( repositoryFactoryInformation );

		PersistentEntity persistentEntity = configuration.getAttribute( PersistentEntity.class );
		assertNotNull( persistentEntity );
		assertEquals( persistentEntity.getType(), Client.class );

		EntityPropertyDescriptor propertyDescriptor = configuration.getPropertyRegistry().getProperty( "name" );
		PersistentProperty persistentProperty = propertyDescriptor.getAttribute( PersistentProperty.class );
		assertNotNull( persistentProperty );
		assertSame( persistentProperty, persistentEntity.getPersistentProperty( "name" ) );

		EntityModel model = configuration.getEntityModel();
		assertNotNull( model );

		EntityViewFactory viewFactory = configuration.getViewFactory( EntityView.LIST_VIEW_NAME );
		assertNotNull( viewFactory );
	}

	@Test
	public void companyShouldHaveAnAssociationToItsRepresentatives() throws Exception {
		EntityConfiguration configuration = entityRegistry.getEntityConfiguration( Company.class );
		EntityAssociation association = configuration.association( "company.representatives" );

		assertNotNull( association );
		assertTrue( association.hasView( EntityView.LIST_VIEW_NAME ) );
	}

	@Test
	public void representativeShouldHaveAnAssociationToItsCompanies() throws Exception {
		EntityConfiguration configuration = entityRegistry.getEntityConfiguration( Representative.class );
		EntityAssociation association = configuration.association( "company.representatives" );

		assertNotNull( association );
		assertTrue( association.hasView( EntityView.LIST_VIEW_NAME ) );
	}

	@Test
	public void verifyPropertyRegistry() {
		EntityConfiguration configuration = entityRegistry.getEntityConfiguration( Client.class );
		EntityPropertyRegistry registry = configuration.getPropertyRegistry();

		assertProperty( registry, "name", "Name", true, true );
		assertProperty( registry, "id", "Id", true, false );
		assertProperty( registry, "company", "Company", true, false );
		assertProperty( registry, "newEntityId", "New entity id", false, false );
		assertProperty( registry, "nameWithId", "Name with id", false, false );
		assertProperty( registry, "class", "Class", false, false );
	}

	@Test
	public void validatorShouldBeRegistered() {
		EntityConfiguration configuration = entityRegistry.getEntityConfiguration( Client.class );
		Validator validator = configuration.getAttribute( Validator.class );

		assertNotNull( validator );
		assertSame( entityValidator, validator );

		configuration = entityRegistry.getEntityConfiguration( Company.class );
		assertNotNull( configuration );

		validator = configuration.getAttribute( Validator.class );
		assertNotNull( validator );
		assertNotSame( entityValidator, validator );

		validator.validate( new Company(), null );
	}

	private void assertProperty( EntityPropertyRegistry registry,
	                             String propertyName,
	                             String displayName,
	                             boolean sortable,
	                             boolean hasValidators ) {
		EntityPropertyDescriptor descriptor = registry.getProperty( propertyName );
		assertNotNull( propertyName );
		assertEquals( propertyName, descriptor.getName() );
		assertEquals( displayName, descriptor.getDisplayName() );

		if ( sortable ) {
			assertEquals( propertyName, descriptor.getAttribute( Sort.Order.class ).getProperty() );
		}
		else {
			assertFalse( descriptor.hasAttribute( Sort.Order.class ) );
		}

		if ( hasValidators ) {
			assertNotNull( descriptor.getAttribute( PropertyDescriptor.class ) );
		}
		else {
			assertFalse( descriptor.hasAttribute( PropertyDescriptor.class ) );
		}
	}

	@Test
	public void entityConfigurationFromConversionService() {
		EntityConfiguration clientConfiguration = mvcConversionService.convert( "client", EntityConfiguration.class );

		assertNotNull( clientConfiguration );
		assertEquals( Client.class, clientConfiguration.getEntityType() );

		EntityConfiguration notExisting = mvcConversionService.convert( "someUnexistingEntity",
		                                                                EntityConfiguration.class );
		assertNull( notExisting );
	}

	@Test
	public void entityConverter() {
		Client client = new Client();
		client.setNewEntityId( 123L );
		client.setName( "Known client name" );

		clientRepository.save( client );

		Client converted = mvcConversionService.convert( "", Client.class );
		assertNull( converted );

		converted = mvcConversionService.convert( 123, Client.class );
		assertEquals( client, converted );

		converted = mvcConversionService.convert( "123", Client.class );
		assertEquals( client, converted );
	}

	@Test
	public void verifyEntityModel() {
		EntityConfiguration<Client> configuration = entityRegistry.getEntityConfiguration( Client.class );
		EntityModel<Client, Serializable> model = configuration.getEntityModel();

		Client existing = model.findOne( 10L );
		assertNull( existing );

		Client created = model.createNew();
		assertNotNull( created );
		assertTrue( model.isNew( created ) );

		created.setNewEntityId( 10L );
		created.setName( "Some name" );

		assertEquals( "Some name", model.getLabel( created ) );

		created = model.save( created );
		assertEquals( Long.valueOf( 10 ), created.getId() );
		assertFalse( model.isNew( created ) );

		existing = model.findOne( 10L );
		assertNotNull( existing );
		assertEquals( "Some name", existing.getName() );
		assertEquals( "Some name", model.getLabel( created ) );

		Client dto = model.createDto( created );
		assertNotSame( created, dto );
		assertEquals( created.getId(), dto.getId() );
		assertEquals( created.getName(), dto.getName() );

		dto.setName( "Modified name" );
		model.save( dto );

		existing = model.findOne( 10L );
		assertNotNull( existing );
		assertEquals( "Modified name", existing.getName() );
		assertEquals( "Modified name", model.getLabel( existing ) );

		model.delete( existing );
		assertNull( model.findOne( 10L ) );
	}

	@Test
	public void defaultViewsShouldBePresent() {
		EntityConfiguration<Client> configuration = entityRegistry.getEntityConfiguration( Client.class );
		assertTrue( configuration.hasView( EntityView.LIST_VIEW_NAME ) );
		assertTrue( configuration.hasView( EntityView.CREATE_VIEW_NAME ) );
		assertTrue( configuration.hasView( EntityView.UPDATE_VIEW_NAME ) );
		assertTrue( configuration.hasView( EntityView.DELETE_VIEW_NAME ) );
	}

	@Test
	public void clientShouldHaveAJpaExecutor() {
		EntityConfiguration<Client> configuration = entityRegistry.getEntityConfiguration( Client.class );
		EntityQueryExecutor<Client> queryExecutor = configuration.getAttribute( EntityQueryExecutor.class );

		assertNotNull( queryExecutor );
		assertTrue( queryExecutor instanceof EntityQueryJpaExecutor );
	}

	@Test
	public void companyShouldHaveAQueryDslExecutor() {
		EntityConfiguration<Company> configuration = entityRegistry.getEntityConfiguration( Company.class );
		EntityQueryExecutor<Company> queryExecutor = configuration.getAttribute( EntityQueryExecutor.class );

		assertNotNull( queryExecutor );
		assertTrue( queryExecutor instanceof EntityQueryQueryDslExecutor );
	}

	@Configuration
	@AcrossTestConfiguration(modules = { EntityModule.NAME, AdminWebModule.NAME, SpringSecurityModule.NAME })
	public static class Config
	{
		@Bean
		public AcrossHibernateJpaModule acrossHibernateJpaModule() {
			AcrossHibernateJpaModule hibernateModule = new AcrossHibernateJpaModule();
			hibernateModule.setHibernateProperty( "hibernate.hbm2ddl.auto", "create-drop" );
			return hibernateModule;
		}

		@Bean
		public SpringDataJpaModule springDataJpaModule() {
			SpringDataJpaModule springDataJpaModule = new SpringDataJpaModule();
			springDataJpaModule.expose( Repository.class );
			return springDataJpaModule;
		}
	}
}
