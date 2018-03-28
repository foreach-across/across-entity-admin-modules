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

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.SimpleEntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.ViewElementLookupRegistry;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.support.SpelValueFetcher;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.convert.TypeDescriptor;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class TestEntityPropertyDescriptorBuilder
{
	private EntityPropertyDescriptorBuilder builder;

	private EntityPropertyDescriptor descriptor;

	@Before
	public void before() {
		builder = new EntityPropertyDescriptorBuilder( "myprop" );
		descriptor = null;
	}

	@Test(expected = NullPointerException.class)
	public void nameIsRequired() {
		builder = new EntityPropertyDescriptorBuilder( null );
	}

	@Test
	public void builderMethodOnInterfaceShouldReturnEmptyBuilder() {
		EntityPropertyDescriptorBuilder newBuilder = EntityPropertyDescriptor.builder( "myprop" );
		assertNotNull( newBuilder );
		descriptor = newBuilder.build();
		assertEquals( "myprop", descriptor.getName() );
	}

	@Test
	public void defaultPropertiesOnBlankDescriptor() {
		build();

		assertEquals( "myprop", descriptor.getName() );
		assertEquals( "myprop", descriptor.getDisplayName() );
		assertNull( descriptor.getPropertyType() );
		assertNull( descriptor.getPropertyTypeDescriptor() );
		assertNull( descriptor.getPropertyRegistry() );
		assertTrue( descriptor.getValueFetcher() instanceof SpelValueFetcher );
		assertFalse( descriptor.isHidden() );
		assertFalse( descriptor.isWritable() );
		assertTrue( descriptor.isReadable() );
		assertEquals( 1, descriptor.attributeMap().size() );
		assertTrue( descriptor.hasAttribute( ViewElementLookupRegistry.class ) );
	}

	@Test
	public void descriptorWithParent() {
		EntityPropertyDescriptor parent = mock( EntityPropertyDescriptor.class );
		builder.parent( parent );

		build();

		when( parent.getDisplayName() ).thenReturn( "parentDisplayName" );

		assertEquals( "myprop", descriptor.getName() );
		assertEquals( "parentDisplayName", descriptor.getDisplayName() );
	}

	@Test
	public void customProperties() {
		ValueFetcher vf = mock( ValueFetcher.class );
		ViewElementBuilder veb = mock( ViewElementBuilder.class );

		builder.displayName( "My Property" )
		       .valueFetcher( vf )
		       .propertyType( String.class )
		       .propertyType( TypeDescriptor.valueOf( Long.class ) )
		       .hidden( true )
		       .writable( true )
		       .readable( false )
		       .viewElementType( ViewElementMode.CONTROL, "testControl" )
		       .viewElementBuilder( ViewElementMode.FORM_READ, veb )
		       .viewElementModeCaching( ViewElementMode.FORM_READ, false )
		       .attribute( "someAttribute", "someAttributeValue" );

		build();

		assertEquals( "myprop", descriptor.getName() );
		assertEquals( "My Property", descriptor.getDisplayName() );
		assertEquals( Long.class, descriptor.getPropertyType() );
		assertEquals( TypeDescriptor.valueOf( Long.class ), descriptor.getPropertyTypeDescriptor() );
		assertNull( descriptor.getPropertyRegistry() );
		assertSame( vf, descriptor.getValueFetcher() );
		assertTrue( descriptor.isHidden() );
		assertTrue( descriptor.isWritable() );
		assertFalse( descriptor.isReadable() );
		assertEquals( "someAttributeValue", descriptor.getAttribute( "someAttribute" ) );

		ViewElementLookupRegistry lookupRegistry = descriptor.getAttribute( ViewElementLookupRegistry.class );
		assertEquals( "testControl", lookupRegistry.getViewElementType( ViewElementMode.CONTROL ) );
		assertSame( veb, lookupRegistry.getViewElementBuilder( ViewElementMode.FORM_READ ) );
		assertFalse( lookupRegistry.isCacheable( ViewElementMode.FORM_READ ) );
	}

	@Test
	public void updateExistingDescriptor() {
		SimpleEntityPropertyDescriptor existing = new SimpleEntityPropertyDescriptor( "otherprop" );
		existing.setAttribute( "originalAttribute", "originalAttributeValue" );

		ValueFetcher vf = mock( ValueFetcher.class );
		ViewElementBuilder veb = mock( ViewElementBuilder.class );

		builder.displayName( "My Property" )
		       .valueFetcher( vf )
		       .propertyType( String.class )
		       .propertyType( TypeDescriptor.valueOf( Long.class ) )
		       .hidden( true )
		       .writable( true )
		       .readable( false )
		       .viewElementType( ViewElementMode.CONTROL, "testControl" )
		       .viewElementBuilder( ViewElementMode.FORM_READ, veb )
		       .viewElementModeCaching( ViewElementMode.FORM_READ, false )
		       .attribute( "someAttribute", "someAttributeValue" )
		       .apply( existing );

		// name cannot be updated
		assertEquals( "otherprop", existing.getName() );

		assertEquals( "My Property", existing.getDisplayName() );
		assertEquals( Long.class, existing.getPropertyType() );
		assertEquals( TypeDescriptor.valueOf( Long.class ), existing.getPropertyTypeDescriptor() );
		assertNull( existing.getPropertyRegistry() );
		assertSame( vf, existing.getValueFetcher() );
		assertTrue( existing.isHidden() );
		assertTrue( existing.isWritable() );
		assertFalse( existing.isReadable() );
		assertEquals( "originalAttributeValue", existing.getAttribute( "originalAttribute" ) );
		assertEquals( "someAttributeValue", existing.getAttribute( "someAttribute" ) );

		ViewElementLookupRegistry lookupRegistry = existing.getAttribute( ViewElementLookupRegistry.class );
		assertEquals( "testControl", lookupRegistry.getViewElementType( ViewElementMode.CONTROL ) );
		assertSame( veb, lookupRegistry.getViewElementBuilder( ViewElementMode.FORM_READ ) );
		assertFalse( lookupRegistry.isCacheable( ViewElementMode.FORM_READ ) );
	}

	@Test
	public void onlySimplePropertyType() {
		builder.propertyType( Long.class );

		build();

		assertEquals( Long.class, descriptor.getPropertyType() );
		assertEquals( TypeDescriptor.valueOf( Long.class ), descriptor.getPropertyTypeDescriptor() );
	}

	@Test
	public void onlyTypeDescriptorPropertyType() {
		builder.propertyType( TypeDescriptor.collection( List.class, TypeDescriptor.valueOf( Long.class ) ) );

		build();

		assertEquals( List.class, descriptor.getPropertyType() );
		assertEquals( TypeDescriptor.collection( List.class, TypeDescriptor.valueOf( Long.class ) ),
		              descriptor.getPropertyTypeDescriptor() );
	}

	private void build() {
		descriptor = builder.build();
	}

}
