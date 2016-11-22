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

import com.foreach.across.modules.entity.registry.EntityConfigurationProvider;
import com.foreach.across.modules.entity.registry.MutableEntityConfiguration;
import com.foreach.across.modules.entity.registry.MutableEntityRegistry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("unchecked")
public class TestEntitiesConfigurationBuilder
{
	private EntitiesConfigurationBuilder builder;

	@Mock
	private MutableEntityRegistry entityRegistry;

	@Mock
	private AutowireCapableBeanFactory beanFactory;

	@Mock
	private EntityConfigurationProvider configurationProvider;

	@Before
	public void reset() {
		builder = new EntitiesConfigurationBuilder( beanFactory );
	}

	@Test
	public void newBuilders() {
		EntityConfigurationBuilder configBuilder = mockConfigurationBuilder();
		when( beanFactory.getBean( EntityConfigurationBuilder.class ) ).thenReturn( configBuilder );

		MutableEntityConfiguration cfg = mock( MutableEntityConfiguration.class );
		when( configBuilder.build( false ) ).thenReturn( cfg );
		builder.create().name( "test" );

		builder.apply( entityRegistry );

		InOrder inOrder = inOrder( configBuilder );
		inOrder.verify( configBuilder ).name( "test" );
		inOrder.verify( configBuilder ).build( false );
		inOrder.verify( configBuilder ).postProcess( cfg );
		verify( entityRegistry ).register( cfg );
	}

	@Test
	public void applyToAllEntities() {
		EntityConfigurationBuilder configBuilder = mockConfigurationBuilder();
		when( beanFactory.getBean( EntityConfigurationBuilder.class ) ).thenReturn( configBuilder );

		MutableEntityConfiguration string = mock( MutableEntityConfiguration.class );
		when( string.getName() ).thenReturn( "string" );
		when( string.getEntityType() ).thenReturn( String.class );

		when( entityRegistry.getEntities() ).thenReturn( Collections.singletonList( string ) );
		when( entityRegistry.getEntityConfiguration( "string" ) ).thenReturn( string );

		builder.all()
		       .displayName( "hello there" );

		builder.apply( entityRegistry );

		InOrder inOrder = inOrder( configBuilder );
		inOrder.verify( configBuilder ).apply( string, false );
		inOrder.verify( configBuilder ).postProcess( string );
	}

	@Test
	public void entityByType() {
		EntityConfigurationBuilder stringBuilder = mockConfigurationBuilder();
		EntityConfigurationBuilder objectBuilder = mockConfigurationBuilder();
		when( beanFactory.getBean( EntityConfigurationBuilder.class ) )
				.thenReturn( stringBuilder )
				.thenReturn( objectBuilder );

		builder.withType( String.class ).name( "meh" );
		builder.withType( Object.class ).label( "some-label" );

		MutableEntityConfiguration existing = mock( MutableEntityConfiguration.class );
		when( entityRegistry.getEntityConfiguration( String.class ) ).thenReturn( existing );

		MutableEntityConfiguration created = mock( MutableEntityConfiguration.class );
		when( entityRegistry.getEntityConfiguration( Object.class ) )
				.thenReturn( null )
				.thenReturn( created );
		when( objectBuilder.build( false ) ).thenReturn( created );

		builder.apply( entityRegistry );

		InOrder inOrder = inOrder( stringBuilder, objectBuilder );
		inOrder.verify( stringBuilder ).name( "meh" );
		inOrder.verify( objectBuilder ).label( "some-label" );
		inOrder.verify( objectBuilder ).build( false );
		inOrder.verify( stringBuilder ).apply( existing, false );
		inOrder.verify( objectBuilder ).apply( created, false );
		inOrder.verify( stringBuilder ).postProcess( existing );
		inOrder.verify( objectBuilder ).postProcess( created );

		verify( entityRegistry ).register( created );
		verify( entityRegistry, never() ).register( existing );
	}

	@Test
	public void entityByName() {
		EntityConfigurationBuilder stringBuilder = mockConfigurationBuilder();
		EntityConfigurationBuilder objectBuilder = mockConfigurationBuilder();
		when( beanFactory.getBean( EntityConfigurationBuilder.class ) )
				.thenReturn( stringBuilder )
				.thenReturn( objectBuilder );

		builder.withName( "string" ).name( "meh" );
		builder.withName( "object" ).label( "some-label" );
		builder.withName( "object" ).label( "other-label" );

		MutableEntityConfiguration existing = mock( MutableEntityConfiguration.class );
		when( entityRegistry.getEntityConfiguration( "string" ) ).thenReturn( existing );

		MutableEntityConfiguration created = mock( MutableEntityConfiguration.class );
		when( entityRegistry.getEntityConfiguration( "object" ) )
				.thenReturn( null )
				.thenReturn( created );
		when( objectBuilder.build( false ) ).thenReturn( created );

		builder.apply( entityRegistry );

		InOrder inOrder = inOrder( stringBuilder, objectBuilder );
		inOrder.verify( stringBuilder ).name( "meh" );
		inOrder.verify( objectBuilder ).label( "some-label" );
		inOrder.verify( objectBuilder ).label( "other-label" );
		inOrder.verify( objectBuilder ).build( false );
		inOrder.verify( stringBuilder ).apply( existing, false );
		inOrder.verify( objectBuilder ).apply( created, false );
		inOrder.verify( stringBuilder ).postProcess( existing );
		inOrder.verify( objectBuilder ).postProcess( created );

		verify( entityRegistry ).register( created );
		verify( entityRegistry, never() ).register( existing );
	}

	@Test
	public void entityByPredicate() {
		EntityConfigurationBuilder stringBuilder = mockConfigurationBuilder();
		EntityConfigurationBuilder matchedBuilder = mockConfigurationBuilder();
		EntityConfigurationBuilder unmatchedBuilder = mockConfigurationBuilder();
		when( beanFactory.getBean( EntityConfigurationBuilder.class ) )
				.thenReturn( stringBuilder )
				.thenReturn( matchedBuilder )
				.thenReturn( unmatchedBuilder );

		builder.assignableTo( String.class ).label( "assignable" );
		builder.matching( c -> true ).label( "matched" );
		builder.matching( c -> false ).label( "unmatched" );

		MutableEntityConfiguration string = mock( MutableEntityConfiguration.class );
		when( string.getName() ).thenReturn( "string" );
		when( string.getEntityType() ).thenReturn( String.class );

		MutableEntityConfiguration other = mock( MutableEntityConfiguration.class );
		when( other.getName() ).thenReturn( "other" );
		when( other.getEntityType() ).thenReturn( Object.class );

		when( entityRegistry.getEntities() ).thenReturn( Arrays.asList( string, other ) );
		when( entityRegistry.getEntityConfiguration( "string" ) ).thenReturn( string );
		when( entityRegistry.getEntityConfiguration( "other" ) ).thenReturn( other );

		builder.apply( entityRegistry );

		InOrder inOrder = inOrder( stringBuilder, matchedBuilder, unmatchedBuilder );
		inOrder.verify( stringBuilder ).label( "assignable" );
		inOrder.verify( matchedBuilder ).label( "matched" );
		inOrder.verify( unmatchedBuilder ).label( "unmatched" );
		inOrder.verify( stringBuilder ).apply( string, false );
		inOrder.verify( matchedBuilder ).apply( string, false );
		inOrder.verify( matchedBuilder ).apply( other, false );
		inOrder.verify( stringBuilder ).postProcess( string );
		inOrder.verify( matchedBuilder ).postProcess( string );
		inOrder.verify( matchedBuilder ).postProcess( other );

		verifyNoMoreInteractions( unmatchedBuilder );
	}

	@Test
	public void typeOrdering() {
		EntityConfigurationBuilder createBuilder = mockConfigurationBuilder();
		EntityConfigurationBuilder typeBuilder = mockConfigurationBuilder();
		EntityConfigurationBuilder nameBuilder = mockConfigurationBuilder();
		EntityConfigurationBuilder assignableToBuilder = mockConfigurationBuilder();
		EntityConfigurationBuilder matchingBuilder = mockConfigurationBuilder();
		EntityConfigurationBuilder allBuilder = mockConfigurationBuilder();
		when( beanFactory.getBean( EntityConfigurationBuilder.class ) )
				.thenReturn( createBuilder )
				.thenReturn( typeBuilder )
				.thenReturn( nameBuilder )
				.thenReturn( assignableToBuilder )
				.thenReturn( matchingBuilder )
				.thenReturn( allBuilder );

		builder.create().name( "newEntity" );
		builder.withType( String.class ).name( "string" );
		builder.withName( "string" ).name( "stringByName" );
		builder.assignableTo( String.class ).name( "assignableToString" );
		builder.matching( c -> true ).name( "matching" );
		builder.all().name( "all" );

		MutableEntityConfiguration created = mock( MutableEntityConfiguration.class );
		when( createBuilder.build( false ) ).thenReturn( created );

		MutableEntityConfiguration existing = mock( MutableEntityConfiguration.class );
		when( existing.getEntityType() ).thenReturn( String.class );
		when( entityRegistry.getEntities() ).thenReturn( Collections.singletonList( existing ) );
		when( entityRegistry.getEntityConfiguration( anyString() ) ).thenReturn( existing );
		when( entityRegistry.getEntityConfiguration( any( Class.class ) ) ).thenReturn( existing );

		builder.apply( entityRegistry );

		InOrder inOrder = inOrder( createBuilder, typeBuilder, nameBuilder, assignableToBuilder, matchingBuilder,
		                           allBuilder );
		inOrder.verify( createBuilder ).name( "newEntity" );
		inOrder.verify( typeBuilder ).name( "string" );
		inOrder.verify( nameBuilder ).name( "stringByName" );
		inOrder.verify( assignableToBuilder ).name( "assignableToString" );
		inOrder.verify( matchingBuilder ).name( "matching" );
		inOrder.verify( allBuilder ).name( "all" );
		inOrder.verify( createBuilder ).build( false );
		inOrder.verify( allBuilder ).apply( existing, false );
		inOrder.verify( assignableToBuilder ).apply( existing, false );
		inOrder.verify( matchingBuilder ).apply( existing, false );
		inOrder.verify( typeBuilder ).apply( existing, false );
		inOrder.verify( nameBuilder ).apply( existing, false );
		inOrder.verify( createBuilder ).postProcess( created );
		inOrder.verify( allBuilder ).postProcess( existing );
		inOrder.verify( assignableToBuilder ).postProcess( existing );
		inOrder.verify( matchingBuilder ).postProcess( existing );
		inOrder.verify( typeBuilder ).postProcess( existing );
		inOrder.verify( nameBuilder ).postProcess( existing );
	}

	private EntityConfigurationBuilder mockConfigurationBuilder() {
		EntityConfigurationBuilder builder = mock( EntityConfigurationBuilder.class );
		when( builder.as( any() ) ).thenReturn( builder );
		return builder;
	}
}
