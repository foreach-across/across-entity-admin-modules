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
package com.foreach.across.modules.entity.registry;

import com.foreach.across.modules.entity.registry.properties.SimpleEntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import org.junit.Test;
import org.springframework.core.convert.TypeDescriptor;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
public class TestSimpleEntityPropertyDescriptor
{
	@Test
	public void nestedDescriptor() {
		SimpleEntityPropertyDescriptor parent = new SimpleEntityPropertyDescriptor( "parent" );
		assertFalse( parent.isNestedProperty() );
		assertNull( parent.getParentDescriptor() );

		SimpleEntityPropertyDescriptor child = new SimpleEntityPropertyDescriptor( "child", parent );
		assertFalse( child.isNestedProperty() );
		assertNull( child.getParentDescriptor() );

		child.setParentDescriptor( parent );
		assertTrue( child.isNestedProperty() );
		assertSame( parent, child.getParentDescriptor() );

		SimpleEntityPropertyDescriptor other = new SimpleEntityPropertyDescriptor( "other", child );
		assertTrue( other.isNestedProperty() );
		assertSame( parent, other.getParentDescriptor() );
	}

	@Test
	public void shadowingDescriptor() {
		ValueFetcher parentValueFetcher = mock( ValueFetcher.class );
		ValueFetcher childValueFetcher = mock( ValueFetcher.class );

		SimpleEntityPropertyDescriptor original = new SimpleEntityPropertyDescriptor( "name" );
		original.setDisplayName( "Name" );
		original.setHidden( true );
		original.setReadable( true );
		original.setWritable( true );
		original.setPropertyType( String.class );
		original.setPropertyTypeDescriptor( TypeDescriptor.valueOf( Long.class ) );
		original.setValueFetcher( parentValueFetcher );
		original.setAttribute( "test", 123L );

		SimpleEntityPropertyDescriptor descriptor = new SimpleEntityPropertyDescriptor( "newName", original );
		assertEquals( "newName", descriptor.getName() );
		assertEquals( "Name", descriptor.getDisplayName() );
		assertTrue( descriptor.isHidden() );
		assertTrue( descriptor.isReadable() );
		assertTrue( descriptor.isWritable() );
		assertEquals( Long.class, descriptor.getPropertyType() );
		assertEquals( TypeDescriptor.valueOf( Long.class ), descriptor.getPropertyTypeDescriptor() );
		assertSame( parentValueFetcher, descriptor.getValueFetcher() );
		assertEquals( 123L, descriptor.getAttribute( "test" ) );

		descriptor.setDisplayName( "New name" );
		descriptor.setHidden( false );
		descriptor.setReadable( true );
		descriptor.setWritable( false );
		descriptor.setPropertyTypeDescriptor( TypeDescriptor.valueOf( String.class ) );
		descriptor.setValueFetcher( childValueFetcher );
		descriptor.setAttribute( "test", 999L );

		assertEquals( "name", original.getName() );
		assertEquals( "Name", original.getDisplayName() );
		assertTrue( original.isHidden() );
		assertTrue( original.isReadable() );
		assertTrue( original.isWritable() );
		assertEquals( Long.class, original.getPropertyType() );
		assertEquals( TypeDescriptor.valueOf( Long.class ), original.getPropertyTypeDescriptor() );
		assertSame( parentValueFetcher, original.getValueFetcher() );
		assertEquals( 123L, original.getAttribute( "test" ) );

		assertEquals( "newName", descriptor.getName() );
		assertEquals( "New name", descriptor.getDisplayName() );
		assertFalse( descriptor.isHidden() );
		assertTrue( descriptor.isReadable() );
		assertFalse( descriptor.isWritable() );
		assertEquals( String.class, descriptor.getPropertyType() );
		assertEquals( TypeDescriptor.valueOf( String.class ), descriptor.getPropertyTypeDescriptor() );
		assertSame( childValueFetcher, descriptor.getValueFetcher() );
		assertEquals( 999L, descriptor.getAttribute( "test" ) );
	}

	@Test
	public void getPropertyValueIsNullIfEntityIsNull() {
		ValueFetcher fetcher = mock( ValueFetcher.class );
		SimpleEntityPropertyDescriptor descriptor = new SimpleEntityPropertyDescriptor( "name" );
		descriptor.setValueFetcher( fetcher );

		assertNull( descriptor.getPropertyValue( null ) );
		verifyNoMoreInteractions( fetcher );
	}

	@Test
	public void getPropertyValueIsNullIfNoValueFetcher() {
		SimpleEntityPropertyDescriptor descriptor = new SimpleEntityPropertyDescriptor( "name" );
		assertNull( descriptor.getPropertyValue( "some entity" ) );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getPropertyValueDispatchesToValueFetcher() {
		ValueFetcher<String> fetcher = mock( ValueFetcher.class );
		SimpleEntityPropertyDescriptor descriptor = new SimpleEntityPropertyDescriptor( "name" );
		descriptor.setValueFetcher( fetcher );

		when( fetcher.getValue( "some entity" ) ).thenReturn( 123 );

		assertEquals( 123, descriptor.getPropertyValue( "some entity" ) );
	}
}
