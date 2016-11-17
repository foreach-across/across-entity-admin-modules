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

import com.foreach.across.modules.entity.registry.DefaultEntityModel;
import com.foreach.across.modules.entity.registry.EntityConfigurationProvider;
import com.foreach.across.modules.entity.registry.EntityModel;
import com.foreach.across.modules.entity.registry.MutableEntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyRegistry;
import com.foreach.across.modules.entity.views.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.io.Serializable;
import java.util.Collections;
import java.util.function.Consumer;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("unchecked")
public class TestEntityConfigurationBuilder
{
	@Mock
	private AutowireCapableBeanFactory beanFactory;

	@Mock
	private EntityConfigurationProvider configurationProvider;

	@Mock
	private MutableEntityConfiguration<String> config;

	private EntityConfigurationBuilder<String> builder;

	@Before
	public void before() {
		builder = spy( new EntityConfigurationBuilder<>( beanFactory ) );
		when( beanFactory.getBean( EntityConfigurationProvider.class ) ).thenReturn( configurationProvider );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void propertiesAreAppliedInOrder() {
		Consumer<EntityPropertyRegistryBuilder> one = mock( Consumer.class );
		Consumer<EntityPropertyRegistryBuilder> two = mock( Consumer.class );

		InOrder inOrder = inOrder( one, two );

		builder.properties( one ).properties( two );

		builder.apply( config );

		inOrder.verify( one ).accept( any( EntityPropertyRegistryBuilder.class ) );
		inOrder.verify( two ).accept( any( EntityPropertyRegistryBuilder.class ) );
	}

	@Test
	public void simpleBuildShouldExecutePostProcessors() {
		MutableEntityConfiguration expected = mock( MutableEntityConfiguration.class );

		doReturn( expected ).when( builder ).build( true );
		assertSame( expected, builder.build() );
	}

	@Test
	public void simpleApplyShouldExecutePostProcessors() {
		doAnswer( invocation -> null ).when( builder ).apply( config, true );
		builder.apply( config );
		verify( builder ).apply( config, true );
	}

	@Test
	public void buildWithPostProcessors() {
		MutableEntityConfiguration expected = mock( MutableEntityConfiguration.class );
		when( configurationProvider.create( "myEntity", String.class, false ) )
				.thenReturn( expected );
		doAnswer( invocation -> null ).when( builder ).apply( expected, true );

		MutableEntityConfiguration<String> config
				= builder.name( "myEntity" )
				         .displayName( "My entity" )
				         .entityType( String.class, false )
				         .build( true );

		assertSame( expected, config );
		verify( expected, never() ).setDisplayName( anyString() );
		verify( builder ).apply( expected, true );
	}

	@Test
	public void buildWithoutPostProcessors() {
		MutableEntityConfiguration expected = mock( MutableEntityConfiguration.class );
		when( configurationProvider.create( "myEntity", String.class, false ) )
				.thenReturn( expected );
		doAnswer( invocation -> null ).when( builder ).apply( expected, false );

		MutableEntityConfiguration<String> config
				= builder.name( "myEntity" )
				         .displayName( "My entity" )
				         .entityType( String.class, false )
				         .build( false );

		assertSame( expected, config );
		verify( expected, never() ).setDisplayName( anyString() );
		verify( builder ).apply( expected, false );
	}

	@Test
	public void postProcessorsAreAppliedInOrder() {
		Consumer<MutableEntityConfiguration<String>> one = mock( Consumer.class );
		Consumer<MutableEntityConfiguration<String>> two = mock( Consumer.class );
		InOrder inOrder = inOrder( one, two );

		builder.postProcessor( one )
		       .postProcessor( two );

		builder.postProcess( config );

		inOrder.verify( one ).accept( config );
		inOrder.verify( two ).accept( config );
	}

	@Test
	public void applyWithPostProcessors() {
		EntityModel model = mock( EntityModel.class );

		MutableEntityPropertyRegistry propertyRegistry = mock( MutableEntityPropertyRegistry.class );
		when( config.getPropertyRegistry() ).thenReturn( propertyRegistry );

		builder.name( "myEntity" )
		       .displayName( "My entity" )
		       .entityType( String.class, false )
		       .hidden( true )
		       .label( "name" )
		       .entityModel( model )
		       .attribute( "someAttribute", "someAttributeValue" )
		       .apply( config, true );

		verify( config ).setDisplayName( "My entity" );
		verify( config ).setHidden( true );
		verify( config ).setEntityModel( model );
		verify( config ).setAttributes( Collections.singletonMap( "someAttribute", "someAttributeValue" ) );
		verify( propertyRegistry ).getProperty( "name" );

		verify( builder ).postProcess( config );
	}

	@Test
	public void applyWithoutPostProcessors() {
		EntityModel model = mock( EntityModel.class );

		builder.name( "myEntity" )
		       .displayName( "My entity" )
		       .entityType( String.class, false )
		       .hidden( true )
		       .entityModel( model )
		       .attribute( "someAttribute", "someAttributeValue" )
		       .apply( config, false );

		verify( config ).setDisplayName( "My entity" );
		verify( config ).setHidden( true );
		verify( config ).setEntityModel( model );
		verify( config ).setAttributes( Collections.singletonMap( "someAttribute", "someAttributeValue" ) );

		verify( builder, never() ).postProcess( config );
	}

	@Test
	public void modifyEntityModel() {
		Consumer delete = mock( Consumer.class );
		Consumer<EntityModelBuilder<String>> one = mock( Consumer.class );
		Consumer<EntityModelBuilder<String>> two = mock( Consumer.class );
		InOrder inOrder = inOrder( one, two );

		EntityModel model = new DefaultEntityModel();
		when( config.getEntityModel() ).thenReturn( model );

		assertSame(
				builder,
				builder.entityModel( one )
				       .entityModel( two )
				       .entityModel( c -> c.deleteMethod( delete ) )
		);

		builder.apply( config, false );

		inOrder.verify( one ).accept( any( EntityModelBuilder.class ) );
		inOrder.verify( two ).accept( any( EntityModelBuilder.class ) );

		model.delete( "test" );
		verify( delete ).accept( "test" );
	}

	@Test
	public void modifyEntityModelShouldApplyToTheCustomizedEntityModel() {
		Consumer delete = mock( Consumer.class );

		EntityModel model = mock( EntityModel.class );
		when( config.getEntityModel() ).thenReturn( model );

		EntityModel<String, Serializable> otherModel = new DefaultEntityModel<>();

		doAnswer( invocation -> {
			when( config.getEntityModel() ).thenReturn( otherModel );
			return null;
		} ).when( config ).setEntityModel( otherModel );

		assertSame(
				builder,
				builder.entityModel( otherModel )
				       .entityModel( c -> c.deleteMethod( delete ) )
		);

		builder.apply( config, false );

		assertSame( otherModel, config.getEntityModel() );
		String c = "";
		otherModel.delete( c );
		verify( delete ).accept( c );
	}

	@Test
	public void noEntityModelIsCreatedIfNoConsumers() {
		builder.apply( config, false );

		verify( config, never() ).setEntityModel( any( EntityModel.class ) );
	}

	@Test
	public void newEntityModelCreated() {
		assertSame( builder, builder.entityModel( mock( Consumer.class ) ) );
		builder.apply( config, false );

		verify( config ).setEntityModel( notNull( EntityModel.class ) );
	}

	@Test
	public void existingListView() {
		builder.listView( lvb -> lvb.template( "hello" ) );

		when( config.hasView( EntityListView.VIEW_NAME ) ).thenReturn( true );

		EntityListViewFactory listViewFactory = mock( EntityListViewFactory.class );
		when( config.getViewFactory( EntityListView.VIEW_NAME ) ).thenReturn( listViewFactory );

		builder.apply( config, false );
		verify( listViewFactory ).setTemplate( "hello" );
	}

	@Test
	public void newViewsOfRightType() {
		builder.view( "customView", vb -> vb.template( "custom-template" ) )
		       .formView( "formView", fvb -> fvb.template( "form-template" ) )
		       .listView( "listView", lvb -> lvb.template( "list-template" ) );

		EntityViewFactoryProvider viewFactoryProvider = mock( EntityViewFactoryProvider.class );
		when( beanFactory.getBean( EntityViewFactoryProvider.class ) ).thenReturn( viewFactoryProvider );

		EntityListViewFactory listViewFactory = mock( EntityListViewFactory.class );
		when( viewFactoryProvider.create( config, EntityListViewFactory.class ) ).thenReturn( listViewFactory );

		EntityFormViewFactory formViewFactory = mock( EntityFormViewFactory.class );
		when( viewFactoryProvider.create( config, EntityFormViewFactory.class ) ).thenReturn( formViewFactory );

		EntityViewViewFactory customViewFactory = mock( EntityViewViewFactory.class );
		when( viewFactoryProvider.create( config, EntityViewViewFactory.class ) ).thenReturn( customViewFactory );

		builder.apply( config, false );

		InOrder inOrder = inOrder( config, listViewFactory, formViewFactory, customViewFactory );
		inOrder.verify( listViewFactory ).setTemplate( "list-template" );
		inOrder.verify( config ).registerView( "listView", listViewFactory );
		inOrder.verify( formViewFactory ).setTemplate( "form-template" );
		inOrder.verify( config ).registerView( "formView", formViewFactory );
		inOrder.verify( customViewFactory ).setTemplate( "custom-template" );
		inOrder.verify( config ).registerView( "customView", customViewFactory );
	}

	@Test
	public void associationBuilders() {
		EntityAssociationBuilder one = mock( EntityAssociationBuilder.class );
		EntityAssociationBuilder two = mock( EntityAssociationBuilder.class );

		when( beanFactory.getBean( EntityAssociationBuilder.class ) )
				.thenReturn( one )
				.thenReturn( two );

		builder.association( avb -> avb.hidden( true ) )
		       .association( avb -> avb.hidden( false ) );

		builder.apply( config, false );

		InOrder inOrder = inOrder( one, two );
		inOrder.verify( one ).hidden( true );
		inOrder.verify( one ).apply( config );
		inOrder.verify( two ).hidden( false );
		inOrder.verify( two ).apply( config );
	}
}
