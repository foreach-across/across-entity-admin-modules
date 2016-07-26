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

import com.foreach.across.modules.entity.registry.EntityRegistryImpl;
import com.foreach.across.modules.entity.registry.MutableEntityConfiguration;
import com.foreach.across.modules.entity.registry.MutableEntityRegistry;
import com.foreach.across.modules.entity.testmodules.springdata.business.Client;
import com.foreach.across.modules.entity.testmodules.springdata.business.Company;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
public class TestEntitiesConfigurationBuilder
{
	private EntitiesConfigurationBuilder builder;
	private MutableEntityRegistry entityRegistry;

	private MutableEntityConfiguration client, company;
	private AutowireCapableBeanFactory beanFactory;

	@Before
	public void reset() {
		builder = new EntitiesConfigurationBuilder();

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
	}

	@Test
	public void attributesAreAddedToAllConfigurations() {
		Company companyAttribute = new Company();

		builder.attribute( Company.class, companyAttribute );
		builder.attribute( "attributeKey", 123 );

		builder.apply( entityRegistry, beanFactory );

		verify( client ).setAttribute( Company.class, companyAttribute );
		verify( company ).setAttribute( Company.class, companyAttribute );

		verify( client ).setAttribute( "attributeKey", 123 );
		verify( company ).setAttribute( "attributeKey", 123 );
	}

	@Test
	public void separateEntityBuilders() {
		EntityConfigurationBuilder clientBuilder = builder.entity( Client.class );
		assertNotNull( clientBuilder );

		EntityConfigurationBuilder companyBuilder = builder.entity( Company.class );
		assertNotNull( companyBuilder );
		assertNotSame( clientBuilder, companyBuilder );
	}

	@Test
	public void sameEntityBuilderIsReturnedOnMultipleRequests() {
		EntityConfigurationBuilder clientBuilder = builder.entity( Client.class );
		assertNotNull( clientBuilder );

		EntityConfigurationBuilder sameClientBuilder = builder.entity( Client.class );
		assertSame( clientBuilder, sameClientBuilder );
	}

	@Test
	public void globalIsAppliedBeforeSeparateConfigurations() {
		builder.attribute( "someAttribute", 1000 )
		       .entity( Client.class ).attribute( "someAttribute", 2000 );

		builder.apply( entityRegistry, beanFactory );

		InOrder order = inOrder( client );
		order.verify( client ).setAttribute( "someAttribute", 1000 );
		order.verify( client ).setAttribute( "someAttribute", 2000 );

		verify( company ).setAttribute( "someAttribute", 1000 );
		verify( company, never() ).setAttribute( "someAttribute", 2000 );
	}

	@Test
	public void globalPostProcessorsRunBeforeSeparateConfigurations() {
		final List<String> processors = new ArrayList<>( 5 );

		builder.addPostProcessor( configuration -> processors.add( "one" ) );
		builder.addPostProcessor( configuration -> processors.add( "two" ) );
		builder.entity( Client.class )
		       .addPostProcessor( configuration -> processors.add( "three" ) );

		builder.postProcess( entityRegistry );

		assertEquals( Arrays.asList( "one", "one", "two", "two", "three" ), processors );
	}
//
//	@Test
//	public void ifPostProcessorReturnsDifferentConfigurationThatOneIsUsed() {
//		final MutableEntityConfiguration newClient = mock( MutableEntityConfiguration.class );
//		when( newClient.getEntityType() ).thenReturn( Client.class );
//		when( newClient.getName() ).thenReturn( "client" );
//
//		builder.addPostProcessor( new PostProcessor<MutableEntityConfiguration>()
//		{
//			@Override
//			public void process( MutableEntityConfiguration configuration ) {
//				if ( configuration.getEntityType().equals( Client.class ) ) {
//					return newClient;
//				}
//				return configuration;
//			}
//		} );
//
//		builder.postProcess( entityRegistry );
//
//		assertSame( company, entityRegistry.getEntityConfiguration( Company.class ) );
//		assertSame( newClient, entityRegistry.getEntityConfiguration( Client.class ) );
//		assertNotSame( client, entityRegistry.getEntityConfiguration( Client.class ) );
//	}
//
//	@Test
//	public void ifPostProcessorReturnsNullTheConfigurationIsRemoved() {
//		builder.addPostProcessor( new PostProcessor<MutableEntityConfiguration<?>>()
//		{
//			@Override
//			public void process( MutableEntityConfiguration<?> configuration ) {
//				if ( configuration.getEntityType().equals( Client.class ) ) {
//					return null;
//				}
//				return configuration;
//			}
//		} );
//
//		builder.postProcess( entityRegistry );
//
//		assertSame( company, entityRegistry.getEntityConfiguration( Company.class ) );
//		assertNull( entityRegistry.getEntityConfiguration( Client.class ) );
//	}
}
