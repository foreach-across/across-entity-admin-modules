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
package com.foreach.across.modules.entity.config.builders;

import com.foreach.across.modules.entity.registry.*;
import com.foreach.across.modules.entity.testmodules.springdata.business.Client;
import com.foreach.across.modules.entity.testmodules.springdata.business.Company;
import com.foreach.across.modules.entity.views.EntityFormView;
import com.foreach.across.modules.entity.views.EntityListView;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
public class TestEntityConfigurationBuilder
{
	private EntitiesConfigurationBuilder entities;
	private MutableEntityRegistry entityRegistry;
	private AutowireCapableBeanFactory beanFactory;

	private EntityConfigurationBuilder<Client> builder;
	private MutableEntityConfiguration client, company;

	@Before
	public void before() {
		entities = new EntitiesConfigurationBuilder();

		entityRegistry = new EntityRegistryImpl();
		beanFactory = mock( AutowireCapableBeanFactory.class );

		client = mock( MutableEntityConfiguration.class );
		when( client.getEntityType() ).thenReturn( Client.class );
		when( client.getName() ).thenReturn( "client" );

		company = mock( MutableEntityConfiguration.class );
		when( company.getEntityType() ).thenReturn( Company.class );
		when( company.getName() ).thenReturn( "company" );

		entityRegistry.register( client );
		entityRegistry.register( company );

		builder = entities.entity( Client.class );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void propertiesAreAppliedInOrder() {
		Consumer<EntityPropertyRegistryBuilder> one = mock( Consumer.class );
		Consumer<EntityPropertyRegistryBuilder> two = mock( Consumer.class );

		InOrder inOrder = inOrder( one, two );

		builder.properties( one ).properties( two );

		builder.apply( entityRegistry, beanFactory );

		inOrder.verify( one ).accept( any( EntityPropertyRegistryBuilder.class ) );
		inOrder.verify( two ).accept( any( EntityPropertyRegistryBuilder.class ) );
	}

	@Test
	public void andReturnsParent() {
		assertSame( entities, builder.and() );
	}

	@Test
	public void attributesAreAdded() {
		Company companyAttribute = new Company();

		builder.attribute( Company.class, companyAttribute );
		builder.attribute( "attributeKey", 123 );

		builder.apply( entityRegistry, beanFactory );

		verify( client ).setAttribute( Company.class, companyAttribute );
		verify( client ).setAttribute( "attributeKey", 123 );
	}

	@Test
	public void postProcessorsAreAppliedInOrder() {
		final List<String> processors = new ArrayList<>( 2 );

		builder.addPostProcessor( configuration -> {
			assertSame( client, configuration );
			processors.add( "one" );
		} );

		builder.addPostProcessor( configuration -> {
			assertSame( client, configuration );
			processors.add( "two" );
		} );

		builder.postProcess( entityRegistry );

		assertEquals( Arrays.asList( "one", "two" ), processors );
	}

	@Test
	public void hidden() {
		builder.hidden( true );
		builder.apply( entityRegistry, beanFactory );

		verify( client ).setHidden( true );
		verify( company, never() ).setHidden( true );
	}

	@Test
	public void show() {
		builder.show();
		builder.apply( entityRegistry, beanFactory );

		verify( client ).setHidden( false );
		verify( company, never() ).setHidden( false );
	}

	@Test
	public void assignableToBuilder() {
		EntityConfigurationBuilder<Persistable> persistableBuilder
				= entities.assignableTo( Persistable.class )
				          .hide();

		persistableBuilder.apply( entityRegistry, beanFactory );

		verify( client ).setHidden( true );
		verify( company ).setHidden( true );
	}

	@Test
	public void hide() {
		builder.hide();
		builder.apply( entityRegistry, beanFactory );

		verify( client ).setHidden( true );
	}

	@Test
	public void viewBuildersAreSpecificType() {
		AbstractEntityViewBuilder one = builder.view( "someView" );
		assertNotNull( one );

		AbstractEntityViewBuilder listOne = builder.listView();
		assertNotNull( listOne );

		AbstractEntityViewBuilder listTwo = builder.listView( EntityListView.VIEW_NAME );
		assertSame( listOne, listTwo );

		listTwo = builder.listView( "someListView" );
		assertNotNull( listTwo );
		assertNotSame( listOne, listTwo );

		one = builder.createFormView();
		assertNotNull( one );
		assertSame( one, builder.formView( EntityFormView.CREATE_VIEW_NAME ) );

		one = builder.updateFormView();
		assertNotNull( one );
		assertSame( one, builder.formView( EntityFormView.UPDATE_VIEW_NAME ) );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void customEntityModel() {
		EntityModel model = mock( EntityModel.class );

		assertSame( builder, builder.entityModel( model ) );
		builder.apply( entityRegistry, beanFactory );

		verify( client ).setEntityModel( model );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void noEntityModelIsCreatedIfNoConsumers() {
		builder.apply( entityRegistry, beanFactory );

		verify( client, never() ).setEntityModel( any( EntityModel.class ) );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void newEntityModelCreated() {
		assertSame( builder, builder.entityModel( mock( Consumer.class ) ) );
		builder.apply( entityRegistry, beanFactory );

		verify( client ).setEntityModel( notNull( EntityModel.class ) );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void modifyEntityModel() {
		Consumer delete = mock( Consumer.class );
		Consumer<EntityModelBuilder<Client>> one = mock( Consumer.class );
		Consumer<EntityModelBuilder<Client>> two = mock( Consumer.class );
		InOrder inOrder = inOrder( one, two );

		EntityModel model = new DefaultEntityModel();
		when( client.getEntityModel() ).thenReturn( model );

		assertSame(
				builder,
				builder.entityModel( one )
				       .entityModel( two )
				       .entityModel( c -> c.deleteMethod( delete ) )
		);

		builder.apply( entityRegistry, beanFactory );

		inOrder.verify( one ).accept( any( EntityModelBuilder.class ) );
		inOrder.verify( two ).accept( any( EntityModelBuilder.class ) );

		model.delete( "test" );
		verify( delete ).accept( "test" );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void modifyEntityModelShouldApplyToTheCustomizedEntityModel() {
		Consumer delete = mock( Consumer.class );

		EntityModel model = mock( EntityModel.class );
		when( client.getEntityModel() ).thenReturn( model );

		EntityModel<Client, Serializable> otherModel = new DefaultEntityModel<>();

		doAnswer( invocation -> {
			when( client.getEntityModel() ).thenReturn( otherModel );
			return null;
		} ).when( client ).setEntityModel( otherModel );

		assertSame(
				builder,
				builder.entityModel( otherModel )
				       .entityModel( c -> c.deleteMethod( delete ) )
		);

		builder.apply( entityRegistry, beanFactory );

		assertSame( otherModel, client.getEntityModel() );
		Client c = mock( Client.class );
		otherModel.delete( c );
		verify( delete ).accept( c );
	}
}
