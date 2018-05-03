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

package com.foreach.across.modules.entity.views.processors.support;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyController;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyDescriptor;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.MethodInvocationException;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.convert.support.DefaultConversionService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 3.1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestEntityPropertiesBinder
{
	private static final String ENTITY = "some-entity";

	private ConversionService conversionService = new DefaultConversionService();

	private MutableEntityPropertyDescriptor descriptor;

	@Mock
	private EntityPropertyController<String, Long> controller;

	@Mock
	private EntityPropertyRegistry registry;

	@InjectMocks
	private EntityPropertiesBinder binder;

	@Before
	public void before() {
		binder.setConversionService( conversionService );
		binder.setEntity( ENTITY );

		descriptor = EntityPropertyDescriptor.builder( "id" )
		                                     .propertyType( Long.class )
		                                     .build();
		when( registry.getProperty( "id" ) ).thenReturn( descriptor );
		descriptor.setAttribute( EntityPropertyController.class, controller );
	}

	@Test
	public void rawExceptionIfPropertyDoesNotExistWithoutBinderPrefix() {
		assertThatExceptionOfType( IllegalArgumentException.class )
				.isThrownBy( () -> binder.get( "myprop" ) )
				.withMessage( "No such property descriptor: 'myprop'" );

		assertThat( binder.containsKey( "myprop" ) ).isFalse();
	}

	@Test
	public void wrappedExceptionIfPropertyDoesNotExistWithBinderPrefix() {
		binder.setBinderPrefix( "properties" );

		assertThatExceptionOfType( MethodInvocationException.class )
				.isThrownBy( () -> binder.get( "myprop" ) )
				.satisfies( e -> assertThat( e.getPropertyName() ).isEqualTo( "properties[myprop]" ) );
	}

	@Test
	public void singleValueHolderReturnedForExistingProperty() {
		val holder = single( "id" );
		assertThat( holder ).isNotNull();
		assertThat( binder.containsKey( "id" ) ).isTrue();
		assertThat( binder.get( "id" ) ).isSameAs( holder );
	}

	@Test
	public void singleValueLoadsExisting() {
		when( controller.getValue( ENTITY ) ).thenReturn( 456L );

		val holder = single( "id" );
		assertThat( holder.getValue() ).isEqualTo( 456L );
	}

	@Test
	public void singleValueIsConverted() {
		val holder = single( "id" );
		assertThat( holder.getValue() ).isNull();
		assertThat( holder.isModified() ).isFalse();

		holder.setValue( 123L );
		assertThat( holder.getValue() ).isEqualTo( 123L );
		assertThat( holder.isModified() ).isTrue();

		holder.setValue( "123" );
		assertThat( holder.getValue() ).isEqualTo( 123L );
	}

	@Test
	public void rawExceptionsIfNoBinderPrefix() {
		val holder = single( "id" );

		assertThatExceptionOfType( ConversionFailedException.class )
				.isThrownBy( () -> holder.setValue( "abc" ) );

		assertThatExceptionOfType( ConverterNotFoundException.class )
				.isThrownBy( () -> holder.setValue( holder ) );

		binder.setConversionService( null );
		assertThatExceptionOfType( ClassCastException.class )
				.isThrownBy( () -> holder.setValue( 123 ) );
	}

	@Test
	public void wrappedExceptionsIfBinderPrefix() {
		binder.setBinderPrefix( "properties" );

		val holder = single( "id" );

		assertThatExceptionOfType( ConversionNotSupportedException.class )
				.isThrownBy( () -> holder.setValue( "abc" ) )
				.satisfies( e -> {
					assertThat( e.getPropertyName() ).isEqualTo( "properties[id].value" );
					assertThat( e.getRequiredType() ).isEqualTo( Long.class );
					assertThat( e.getValue() ).isEqualTo( "abc" );
				} );

		assertThatExceptionOfType( ConversionNotSupportedException.class )
				.isThrownBy( () -> holder.setValue( holder ) )
				.satisfies( e -> {
					assertThat( e.getPropertyName() ).isEqualTo( "properties[id].value" );
					assertThat( e.getRequiredType() ).isEqualTo( Long.class );
					assertThat( e.getValue() ).isSameAs( holder );
				} );

		binder.setConversionService( null );
		assertThatExceptionOfType( ConversionNotSupportedException.class )
				.isThrownBy( () -> holder.setValue( 123 ) )
				.satisfies( e -> {
					assertThat( e.getPropertyName() ).isEqualTo( "properties[id].value" );
					assertThat( e.getRequiredType() ).isEqualTo( Long.class );
					assertThat( e.getValue() ).isEqualTo( 123 );
				} );
	}

	@SuppressWarnings("unchecked")
	@Test
	public void singleValueBind() {
		val holder = single( "id" );
		holder.bind();
		verify( controller ).setValue( ENTITY, null );

		reset( controller );

		holder.setValue( 678L );
		verifyZeroInteractions( controller );

		holder.bind();
		verify( controller ).setValue( ENTITY, 678L );
	}

	@Test
	public void onlyModifiedPropertiesAreBound() {
		val holder = single( "id" );
		holder.setValue( null );
		assertThat( holder.isModified() ).isFalse();

		binder.bind();
		verify( controller, never() ).setValue( any(), any() );

		holder.setValue( 444L );
		binder.bind();
		verify( controller ).setValue( ENTITY, 444L );
	}

	EntityPropertiesBinder.SingleValue single( String propertyName ) {
		return (EntityPropertiesBinder.SingleValue) binder.get( propertyName );
	}
}
