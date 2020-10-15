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

import com.foreach.across.modules.entity.config.builders.EntityPropertyRegistryBuilder.PropertyDescriptorBuilder;
import com.foreach.across.modules.entity.registry.properties.*;
import com.foreach.across.modules.entity.views.ViewElementLookupRegistry;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.springframework.core.convert.TypeDescriptor;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class TestEntityPropertyRegistryBuilder
{
	private EntityPropertyRegistryBuilder builder;

	private EntityPropertyRegistrySupport registry;

	@BeforeEach
	public void before() {
		builder = new EntityPropertyRegistryBuilder();
		registry = mock( EntityPropertyRegistrySupport.class );
	}

	@Test
	public void andAppliesAdditionalConsumer() {
		Consumer<EntityPropertyRegistryBuilder> consumer = mock( Consumer.class );
		assertSame( builder, builder.and( consumer ) );
		verify( consumer ).accept( builder );
	}

	@Test
	public void propertyAlwaysReturnsTheSameBuilder() {
		PropertyDescriptorBuilder propertyBuilder = builder.property( "test" );
		assertNotNull( propertyBuilder );

		assertSame( propertyBuilder, builder.property( "test" ) );
		assertNotSame( propertyBuilder, builder.property( "other" ) );
	}

	@Test
	public void parentPropertyDescriptorReturnsExceptionWhenNotFound() {
		PropertyDescriptorBuilder propertyBuilder = builder.property( "test" );
		propertyBuilder.parent( "unknown" );
		assertThatThrownBy( () -> builder.apply( registry ) ).isInstanceOf( IllegalArgumentException.class )
		                                                     .hasMessage( "Cannot find parent property [unknown] on property: test" );
	}

	@Test
	public void parentPropertyDescriptorSetsParentCorrectly() {
		PropertyDescriptorBuilder propertyBuilder = builder.property( "test" );
		propertyBuilder.parent( "unknown" );
		SimpleEntityPropertyDescriptor propertyDescriptor = new SimpleEntityPropertyDescriptor( "unknown" );
		when( registry.getProperty( "unknown" ) ).thenReturn( propertyDescriptor );
		ArgumentCaptor<MutableEntityPropertyDescriptor> argumentCaptor = ArgumentCaptor.forClass( MutableEntityPropertyDescriptor.class );
		builder.apply( registry );
		verify( registry ).register( argumentCaptor.capture() );

		assertSame( propertyDescriptor, argumentCaptor.getValue().getParentDescriptor() );
	}

	@Test
	public void propertiesAreAppliedInOrder() {
		builder.property( "manager" )
		       .and()
		       .property( "manager.email" );

		build();

		InOrder inOrder = inOrder( registry );
		inOrder.verify( registry ).getProperty( "manager" );
		inOrder.verify( registry ).getProperty( "manager.email" );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void processConsumersHappenBetweenProperties() {
		Consumer<MutableEntityPropertyRegistry> consumer = mock( Consumer.class );

		builder.property( "manager" )
		       .and()
		       .processRegistry( consumer )
		       .property( "manager.email" );

		build();

		InOrder inOrder = inOrder( registry, consumer );
		inOrder.verify( registry ).getProperty( "manager" );
		inOrder.verify( consumer ).accept( registry );
		inOrder.verify( registry ).getProperty( "manager.email" );
	}

	@Test
	public void registerNewPropertyAndVerifyAndReturnsParent() {
		ValueFetcher vf = mock( ValueFetcher.class );
		ViewElementBuilder veb = mock( ViewElementBuilder.class );

		EntityPropertyRegistryBuilder parent
				= builder.property( "myprop" )
				         .displayName( "My Property" )
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
				         .and();

		assertSame( builder, parent );

		AtomicBoolean verified = new AtomicBoolean( false );

		doAnswer( invocation -> {
			MutableEntityPropertyDescriptor descriptor = invocation.getArgument( 0 );

			assertEquals( "myprop", descriptor.getName() );
			assertEquals( "My Property", descriptor.getDisplayName() );
			assertEquals( Long.class, descriptor.getPropertyType() );
			assertEquals( TypeDescriptor.valueOf( Long.class ), descriptor.getPropertyTypeDescriptor() );
			assertNull( descriptor.getPropertyRegistry() );

			descriptor.getValueFetcher().getValue( "x" );
			verify( vf ).getValue( "x" );

			assertTrue( descriptor.isHidden() );
			assertTrue( descriptor.isWritable() );
			assertFalse( descriptor.isReadable() );
			assertEquals( "someAttributeValue", descriptor.getAttribute( "someAttribute" ) );

			ViewElementLookupRegistry lookupRegistry = descriptor.getAttribute( ViewElementLookupRegistry.class );
			assertEquals( "testControl", lookupRegistry.getViewElementType( ViewElementMode.CONTROL ) );
			assertSame( veb, lookupRegistry.getViewElementBuilder( ViewElementMode.FORM_READ ) );
			assertFalse( lookupRegistry.isCacheable( ViewElementMode.FORM_READ ) );

			verified.set( true );

			return null;
		} ).when( registry ).register( any( MutableEntityPropertyDescriptor.class ) );

		build();

		assertTrue( verified.get() );
	}

	@Test
	public void modifyExistingProperty() {
		MutableEntityPropertyDescriptor existing = mock( MutableEntityPropertyDescriptor.class );
		when( registry.getProperty( "myprop" ) ).thenReturn( existing );

		builder.property( "myprop" ).displayName( "Existing property" );

		build();

		verify( existing ).setDisplayName( "Existing property" );
	}

	@Test
	public void labelProperty() {
		MutableEntityPropertyDescriptor labelProperty = mock( MutableEntityPropertyDescriptor.class );
		when( registry.getProperty( EntityPropertyRegistry.LABEL ) ).thenReturn( labelProperty );

		MutableEntityPropertyDescriptor existing = mock( MutableEntityPropertyDescriptor.class );
		when( existing.attributeMap() ).thenReturn( Collections.singletonMap( "one", "value" ) );
		when( registry.getProperty( "myprop" ) ).thenReturn( existing );

		builder.label( "myprop" ).displayName( "New label" );

		build();

		verify( labelProperty ).setDisplayName( "New label" );
		verify( labelProperty ).setAttributes( Collections.singletonMap( "one", "value" ) );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void propertyOrder() {
		builder.property( "one" ).order( 1 ).and()
		       .property( "two" ).order( 2 );

		build();

		verify( registry ).setPropertyOrder( "one", 1 );
		verify( registry ).setPropertyOrder( "two", 2 );
	}

	private void build() {
		builder.apply( registry );
	}

}
