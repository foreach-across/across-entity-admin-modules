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
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyRegistry;
import com.foreach.across.modules.entity.views.DefaultEntityViewFactory;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.builders.EntityViewFactoryBuilderInitializer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.util.Collections;

import static com.foreach.across.modules.entity.registry.EntityAssociation.Type.EMBEDDED;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
@PrepareForTest(EntityViewFactoryBuilderInitializer.class)
@RunWith(PowerMockRunner.class)
@SuppressWarnings("unchecked")
public class TestEntityAssociationBuilder
{
	@Mock
	private EntityRegistry entityRegistry;

	@Mock
	private AutowireCapableBeanFactory beanFactory;

	@Mock
	private MutableEntityConfiguration configuration;

	private EntityAssociationBuilder builder;
	private EntityViewFactoryBuilderInitializer builderInitializer;

	@Before
	public void reset() {
		builderInitializer = PowerMockito.mock( EntityViewFactoryBuilderInitializer.class );

		when( beanFactory.getBean( EntityRegistry.class ) ).thenReturn( entityRegistry );
		builder = new EntityAssociationBuilder( beanFactory );

		when( beanFactory.getBean( EntityViewFactoryBuilderInitializer.class ) ).thenReturn( builderInitializer );
	}

	@Test
	public void newAssociation() {
		MutableEntityAssociation association = mock( MutableEntityAssociation.class );
		when( configuration.createAssociation( "users" ) ).thenReturn( association );
		when( association.getSourceEntityConfiguration() ).thenReturn( configuration );

		MutableEntityPropertyRegistry sourceRegistry = mock( MutableEntityPropertyRegistry.class );
		when( configuration.getPropertyRegistry() ).thenReturn( sourceRegistry );
		MutableEntityPropertyDescriptor sourceProperty = mock( MutableEntityPropertyDescriptor.class );
		when( sourceRegistry.getProperty( "users" ) ).thenReturn( sourceProperty );

		EntityConfiguration target = mock( EntityConfiguration.class );
		when( entityRegistry.getEntityConfiguration( String.class ) ).thenReturn( target );
		when( association.getTargetEntityConfiguration() ).thenReturn( target );
		EntityPropertyRegistry targetRegistry = mock( EntityPropertyRegistry.class );
		when( target.getPropertyRegistry() ).thenReturn( targetRegistry );
		EntityPropertyDescriptor targetProperty = mock( EntityPropertyDescriptor.class );
		when( targetRegistry.getProperty( "id" ) ).thenReturn( targetProperty );

		when( association.hasView( EntityView.LIST_VIEW_NAME ) ).thenReturn( false );
		DefaultEntityViewFactory listViewFactory = new DefaultEntityViewFactory();
		when( beanFactory.createBean( DefaultEntityViewFactory.class ) ).thenReturn( listViewFactory );

		builder.name( "users" )
		       .hidden( false )
		       .associationType( EMBEDDED )
		       .targetEntityType( String.class )
		       .targetProperty( "id" )
		       .sourceProperty( "users" )
		       .attribute( "someAttribute", "someAttributeValue" )
		       .listView( lvb -> lvb.template( "hello" ) )
		       .parentDeleteMode( EntityAssociation.ParentDeleteMode.SUPPRESS )
		       .apply( configuration );

		verify( configuration ).createAssociation( "users" );
		verify( association ).setAssociationType( EMBEDDED );
		verify( association ).setSourceProperty( sourceProperty );
		verify( association ).setTargetEntityConfiguration( target );
		verify( association ).setTargetProperty( targetProperty );
		verify( association ).setHidden( false );
		verify( association ).setAttributes( Collections.singletonMap( "someAttribute", "someAttributeValue" ) );
		verify( association ).setParentDeleteMode( EntityAssociation.ParentDeleteMode.SUPPRESS );
		verify( association ).registerView( EntityView.LIST_VIEW_NAME, listViewFactory );
	}

	@Test
	public void updateExisting() {
		MutableEntityAssociation association = mock( MutableEntityAssociation.class );
		when( configuration.association( "users" ) ).thenReturn( association );
		when( association.getSourceEntityConfiguration() ).thenReturn( configuration );

		MutableEntityPropertyRegistry sourceRegistry = mock( MutableEntityPropertyRegistry.class );
		when( configuration.getPropertyRegistry() ).thenReturn( sourceRegistry );
		MutableEntityPropertyDescriptor sourceProperty = mock( MutableEntityPropertyDescriptor.class );
		when( sourceRegistry.getProperty( "users" ) ).thenReturn( sourceProperty );

		EntityConfiguration target = mock( EntityConfiguration.class );
		when( entityRegistry.getEntityConfiguration( "string" ) ).thenReturn( target );
		when( association.getTargetEntityConfiguration() ).thenReturn( target );
		EntityPropertyRegistry targetRegistry = mock( EntityPropertyRegistry.class );
		when( target.getPropertyRegistry() ).thenReturn( targetRegistry );
		EntityPropertyDescriptor targetProperty = mock( EntityPropertyDescriptor.class );
		when( targetRegistry.getProperty( "id" ) ).thenReturn( targetProperty );

		when( association.hasView( EntityView.LIST_VIEW_NAME ) ).thenReturn( true );
		DefaultEntityViewFactory listViewFactory = mock( DefaultEntityViewFactory.class );
		when( beanFactory.createBean( DefaultEntityViewFactory.class ) ).thenReturn( listViewFactory );
		builder.name( "users" )
		       .hidden( false )
		       .associationType( EMBEDDED )
		       .targetEntityType( String.class )
		       .targetEntity( "string" )
		       .targetProperty( "id" )
		       .sourceProperty( "users" )
		       .attribute( "someAttribute", "someAttributeValue" )
		       .parentDeleteMode( EntityAssociation.ParentDeleteMode.WARN )
		       .listView( lvb -> lvb.template( "hello" ) )
		       .apply( configuration );

		verify( association ).setAssociationType( EMBEDDED );
		verify( association ).setSourceProperty( sourceProperty );
		verify( association ).setTargetEntityConfiguration( target );
		verify( association ).setTargetProperty( targetProperty );
		verify( association ).setHidden( false );
		verify( association ).setAttributes( Collections.singletonMap( "someAttribute", "someAttributeValue" ) );
		verify( association ).setParentDeleteMode( EntityAssociation.ParentDeleteMode.WARN );
	}
}
