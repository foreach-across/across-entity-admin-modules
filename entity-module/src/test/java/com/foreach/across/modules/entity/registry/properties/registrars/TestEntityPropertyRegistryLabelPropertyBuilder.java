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
package com.foreach.across.modules.entity.registry.properties.registrars;

import com.foreach.across.modules.entity.registry.properties.DefaultEntityPropertyRegistryProvider;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyRegistry;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
public class TestEntityPropertyRegistryLabelPropertyBuilder
{
	private static final MutableEntityPropertyDescriptor EXISTING;

	private static final Answer<Void> EXISTING_ANSWER = invocation -> {
		MutableEntityPropertyDescriptor label = (MutableEntityPropertyDescriptor) invocation.getArguments()[0];
		assertNotNull( label );
		assertEquals( "Label", label.getDisplayName() );
		assertEquals( Sort.Order.by( "test" ), label.getAttribute( Sort.Order.class ) );
		assertEquals( Integer.class, label.getPropertyType() );
		assertEquals( TypeDescriptor.valueOf( Integer.class ), label.getPropertyTypeDescriptor() );
		assertTrue( label.isReadable() );
		assertFalse( label.isWritable() );
		assertTrue( label.isHidden() );

		return null;
	};

	static {
		EXISTING = mock( MutableEntityPropertyDescriptor.class );
		ValueFetcher valueFetcher = mock( ValueFetcher.class );

		Mockito.<Class<?>>when( EXISTING.getPropertyType() ).thenReturn( Integer.class );

		Map<String, Object> attributes = Collections.singletonMap( Sort.Order.class.getName(), Sort.Order.by(
				"test" ) );
		when( EXISTING.attributeMap() ).thenReturn( attributes );
		when( EXISTING.getValueFetcher() ).thenReturn( valueFetcher );
		when( EXISTING.getPropertyTypeDescriptor() ).thenReturn( TypeDescriptor.valueOf( Integer.class ) );
	}

	private DefaultEntityPropertyRegistryProvider.PropertiesRegistrar builder = new LabelPropertiesRegistrar();
	private MutableEntityPropertyRegistry propertyRegistry;

	@BeforeEach
	public void before() {
		propertyRegistry = mock( MutableEntityPropertyRegistry.class );
	}

	@Test
	public void defaultLabelIsToString() {
		doAnswer( invocation -> {
			MutableEntityPropertyDescriptor label = (MutableEntityPropertyDescriptor) invocation.getArguments()[0];
			assertNotNull( label );
			assertEquals( "Label", label.getDisplayName() );
			assertEquals( String.class, label.getPropertyType() );
			assertEquals( TypeDescriptor.valueOf( String.class ), label.getPropertyTypeDescriptor() );
			assertTrue( label.isReadable() );
			assertFalse( label.isWritable() );
			assertTrue( label.isHidden() );

			return null;
		} ).when( propertyRegistry ).register( any( MutableEntityPropertyDescriptor.class ) );

		builder.accept( Object.class, propertyRegistry );

		verify( propertyRegistry ).register( any( MutableEntityPropertyDescriptor.class ) );
	}

	@Test
	public void nameIsUsedBeforeTitleAndLabel() {
		when( propertyRegistry.contains( "name" ) ).thenReturn( true );
		when( propertyRegistry.contains( "title" ) ).thenReturn( true );
		when( propertyRegistry.contains( "label" ) ).thenReturn( true );
		when( propertyRegistry.getProperty( "name" ) ).thenReturn( EXISTING );

		doAnswer( EXISTING_ANSWER ).when( propertyRegistry ).register( any( MutableEntityPropertyDescriptor.class ) );
		builder.accept( Object.class, propertyRegistry );
		verify( propertyRegistry ).register( any( MutableEntityPropertyDescriptor.class ) );
	}

	@Test
	public void titleIsUsedBeforeLabel() {
		when( propertyRegistry.contains( "name" ) ).thenReturn( false );
		when( propertyRegistry.contains( "title" ) ).thenReturn( true );
		when( propertyRegistry.contains( "label" ) ).thenReturn( true );
		when( propertyRegistry.getProperty( "title" ) ).thenReturn( EXISTING );

		doAnswer( EXISTING_ANSWER ).when( propertyRegistry ).register( any( MutableEntityPropertyDescriptor.class ) );
		builder.accept( Object.class, propertyRegistry );
		verify( propertyRegistry ).register( any( MutableEntityPropertyDescriptor.class ) );
	}

	@Test
	public void labelIsUsedIfAvailable() {
		when( propertyRegistry.contains( "name" ) ).thenReturn( false );
		when( propertyRegistry.contains( "title" ) ).thenReturn( false );
		when( propertyRegistry.contains( "label" ) ).thenReturn( true );
		when( propertyRegistry.getProperty( "label" ) ).thenReturn( EXISTING );

		doAnswer( EXISTING_ANSWER ).when( propertyRegistry ).register( any( MutableEntityPropertyDescriptor.class ) );
		builder.accept( Object.class, propertyRegistry );
		verify( propertyRegistry ).register( any( MutableEntityPropertyDescriptor.class ) );
	}

}
