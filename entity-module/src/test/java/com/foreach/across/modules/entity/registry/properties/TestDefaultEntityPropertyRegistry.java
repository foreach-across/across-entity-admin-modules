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

package com.foreach.across.modules.entity.registry.properties;

import com.foreach.across.modules.entity.EntityAttributes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.collections.Sets;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.convert.TypeDescriptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestDefaultEntityPropertyRegistry
{
	@Mock
	private EntityPropertyRegistryProvider registryProvider;

	private DefaultEntityPropertyRegistry registry;

	@Before
	public void before() {
		registry = new DefaultEntityPropertyRegistry( registryProvider );

		register( "id", Long.class );
		register( "name", String.class );
		register( "created", Date.class );
		register( "aliases", TypeDescriptor.collection( ArrayList.class, TypeDescriptor.valueOf( String.class ) ) );
	}

	@Test
	public void javaBeansSpecificationAllowsAllCapsPropertyNames() {
		register( "URL", String.class );

		// if second char of property requested is uppercase, assume registered as all-caps property
		EntityPropertyDescriptor descriptor = registry.getProperty( "URL" );
		assertNotNull( descriptor );
		assertSame( descriptor, registry.getProperty( "uRL" ) );

		assertNull( registry.getProperty( "url" ) );
	}

	@Test
	public void initialOrderIsKeptAsFallback() {
		registry.setDefaultOrder( "created", "name", "id", "aliases" );
		assertEquals(
				Arrays.asList( "created", "name", "id", "aliases" ),
				registry.getProperties()
				        .stream().map( EntityPropertyDescriptor::getName ).collect( toList() )
		);

		registry.setDefaultOrder( "name" );
		assertEquals(
				Arrays.asList( "name", "aliases", "created", "id" ),
				registry.getProperties()
				        .stream().map( EntityPropertyDescriptor::getName ).collect( toList() )
		);
	}

	@Test
	public void defaultFilterReturnsNonHidden() {
		registry.getProperty( "id" ).setHidden( true );

		assertEquals(
				Sets.newSet( "name", "created", "aliases" ),
				registry.getProperties()
				        .stream().map( EntityPropertyDescriptor::getName ).collect( toSet() )
		);
	}

	@Test
	public void customDefaultFilter() {
		registry.setDefaultFilter( entityPropertyDescriptor -> true );
		assertEquals(
				Sets.newSet( "name", "created", "id", "aliases" ),
				registry.getProperties()
				        .stream().map( EntityPropertyDescriptor::getName ).collect( toSet() )
		);
	}

	@Test
	public void customIncludeFilterKeepsTheDefaultOrder() {
		registry.setDefaultOrder( "created", "name", "id" );

		assertEquals(
				Arrays.asList( "created", "name", "id" ),
				registry.getProperties( entityPropertyDescriptor -> Arrays.asList( "name", "created", "id" ).contains( entityPropertyDescriptor.getName() ) )
				        .stream().map( EntityPropertyDescriptor::getName ).collect( toList() )
		);
	}

	@Test
	public void customExcludeFilterKeepsTheDefaultOrder() {
		registry.setDefaultOrder( "created", "name", "id", "aliases" );

		assertEquals(
				Arrays.asList( "created", "id", "aliases" ),
				registry.getProperties( entityPropertyDescriptor -> !"name".equals( entityPropertyDescriptor.getName() ) )
				        .stream().map( EntityPropertyDescriptor::getName ).collect( toList() )
		);
	}

	@Test
	public void collectionPropertyHasHiddenAliasWithMemberType() {
		val descriptor = registry.getProperty( "aliases" );
		assertEquals( "aliases", descriptor.getName() );
		assertEquals( ArrayList.class, descriptor.getPropertyType() );
		assertEquals( TypeDescriptor.collection( ArrayList.class, TypeDescriptor.valueOf( String.class ) ), descriptor.getPropertyTypeDescriptor() );

		val member = registry.getProperty( "aliases[]" );
		assertNotNull( member );
		assertEquals( "aliases[]", member.getName() );
		assertEquals( String.class, member.getPropertyType() );
		assertEquals( TypeDescriptor.valueOf( String.class ), member.getPropertyTypeDescriptor() );
		assertSame( member, registry.getProperty( "aliases[]" ) );
		assertTrue( member.isHidden() );
		assertTrue( member.isNestedProperty() );
		assertSame( descriptor, member.getParentDescriptor() );
	}

	@Test
	public void nestedProperty() {
		MutableEntityPropertyRegistry nested = mock( MutableEntityPropertyRegistry.class );
		when( registryProvider.get( Date.class ) ).thenReturn( nested );

		MutableEntityPropertyDescriptor timestamp = mock( MutableEntityPropertyDescriptor.class );
		when( timestamp.getDisplayName() ).thenReturn( "Nested display name" );
		when( timestamp.getController() ).thenReturn( mock( EntityPropertyController.class ) );
		when( nested.getProperty( "timestamp" ) ).thenReturn( timestamp );

		SimpleEntityPropertyDescriptor descriptor = (SimpleEntityPropertyDescriptor) registry.getProperty( "created.timestamp" );
		assertNotNull( descriptor );
		assertTrue( descriptor.isNestedProperty() );
		assertSame( registry.getProperty( "created" ), descriptor.getParentDescriptor() );
		assertSame( timestamp, descriptor.getAttribute( EntityAttributes.TARGET_DESCRIPTOR, EntityPropertyDescriptor.class ) );
		assertEquals( "Nested display name", descriptor.getDisplayName() );

		assertSame( descriptor, registry.getProperty( "created.timestamp" ) );
	}

	@Test
	public void nestedPropertyImpossibleIfNoRegistryProvider() {
		registry = new DefaultEntityPropertyRegistry();

		register( "name", String.class );
		EntityPropertyDescriptor l = register( "name.length", String.class );
		register( "created", Date.class );

		assertNull( registry.getProperty( "created.timestamp" ) );
		assertSame( l, registry.getProperty( "name.length" ) );
	}

	@Test
	public void nestedPropertyUsesCustomTargetEntityPropertyRegistryIfPresent() {
		registry = new DefaultEntityPropertyRegistry( DefaultEntityPropertyRegistryProvider.INSTANCE );
		SimpleEntityPropertyDescriptor name = register( "name", String.class );
		assertNotNull( registry.getProperty( "name.class" ) );

		DefaultEntityPropertyRegistry subRegistry = new DefaultEntityPropertyRegistry();
		SimpleEntityPropertyDescriptor descriptor = new SimpleEntityPropertyDescriptor( "bytes" );
		descriptor.setPropertyType( String.class );
		descriptor.setHidden( false );
		subRegistry.register( descriptor );
		name.setAttribute( EntityPropertyRegistry.class, subRegistry );

		EntityPropertyDescriptor bytes = registry.getProperty( "name.bytes" );
		assertNotNull( bytes );
		assertEquals( String.class, bytes.getPropertyType() );
	}

	@Test
	public void nestedPropertyFormatWithoutBeingNestedProperty() {
		registry = new DefaultEntityPropertyRegistry( DefaultEntityPropertyRegistryProvider.INSTANCE );
		assertNull( registry.getProperty( "some.property.value" ) );
	}

	@Test
	public void defaultSharedRegistryProviderCreatesSampleControllers() {
		MutableEntityPropertyRegistry registry = DefaultEntityPropertyRegistry.forClass( Scanned.class );
		EntityPropertyDescriptor read = registry.getProperty( "read" );
		assertNotNull( read );
		assertTrue( read.isReadable() );
		assertFalse( read.isWritable() );
		assertTrue( read.isHidden() );

		EntityPropertyDescriptor write = registry.getProperty( "write" );
		assertNotNull( write );
		assertFalse( write.isReadable() );
		assertTrue( write.isWritable() );
		assertTrue( write.isHidden() );

		EntityPropertyDescriptor readWrite = registry.getProperty( "readWrite" );
		assertNotNull( readWrite );
		assertTrue( readWrite.isReadable() );
		assertTrue( readWrite.isWritable() );
		assertFalse( readWrite.isHidden() );

		Scanned instance = new Scanned( "read-write", "read-only", "write-only" );

		EntityPropertyBindingContext ctx = EntityPropertyBindingContext.forReading( instance );
		assertEquals( "read-only", read.getController().fetchValue( ctx ) );
		assertNull( write.getController().fetchValue( ctx ) );
		assertEquals( "read-write", readWrite.getController().fetchValue( ctx ) );

		assertEquals( EntityPropertyController.BEFORE_ENTITY, read.getController().getOrder() );
		assertFalse( read.getController().applyValue( ctx, new EntityPropertyValue<>( null, "updated-read", false ) ) );
		assertTrue( write.getController().applyValue( ctx, new EntityPropertyValue<>( null, "updated-write", false ) ) );
		assertTrue( readWrite.getController().applyValue( ctx, new EntityPropertyValue<>( null, "updated-read-write", false ) ) );

		assertEquals( "", read.getController().createValue( ctx ) );
		assertEquals( "", write.getController().createValue( ctx ) );
		assertEquals( "", readWrite.getController().createValue( ctx ) );

		assertEquals( "read-only", instance.read );
		assertEquals( "updated-write", instance.write );
		assertEquals( "updated-read-write", instance.readWrite );
	}

	private SimpleEntityPropertyDescriptor register( String name, Class type ) {
		SimpleEntityPropertyDescriptor descriptor = new SimpleEntityPropertyDescriptor( name );
		descriptor.setPropertyType( type );
		descriptor.setHidden( false );

		registry.register( descriptor );

		return descriptor;
	}

	private SimpleEntityPropertyDescriptor register( String name, TypeDescriptor type ) {
		SimpleEntityPropertyDescriptor descriptor = new SimpleEntityPropertyDescriptor( name );
		descriptor.setPropertyTypeDescriptor( type );
		descriptor.setHidden( false );

		registry.register( descriptor );

		return descriptor;
	}

	@AllArgsConstructor
	private static class Scanned
	{
		@Getter
		@Setter
		private String readWrite;

		@Getter
		private String read;

		@Setter
		private String write;
	}
}
