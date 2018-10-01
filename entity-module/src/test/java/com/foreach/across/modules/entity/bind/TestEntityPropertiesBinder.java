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

package com.foreach.across.modules.entity.bind;

import com.foreach.across.modules.entity.registry.properties.*;
import lombok.val;
import org.junit.Before;
import org.junit.Ignore;
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

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 3.2.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestEntityPropertiesBinder
{
	private static final String ENTITY = "some-entity";

	private ConversionService conversionService = new DefaultConversionService();

	@Mock
	private EntityPropertyController<String, Long> singleValueController;

	@Mock
	private EntityPropertyController<String, Long[]> listValueController;

	@Mock
	private EntityPropertyController<String, Map<Long, Long>> mapValueController;

	@Mock
	private EntityPropertyRegistry registry;

	private EntityPropertyBindingContext bindingContext;

	@InjectMocks
	private EntityPropertiesBinder binder;

	@Before
	public void before() {
		bindingContext = EntityPropertyBindingContext.forReading( ENTITY );

		binder.setConversionService( conversionService );
		binder.setBindingContext( bindingContext );

		MutableEntityPropertyDescriptor singleValue = EntityPropertyDescriptor.builder( "id" )
		                                                                      .propertyType( Long.class )
		                                                                      .controller( singleValueController )
		                                                                      .build();
		when( registry.getProperty( "id" ) ).thenReturn( singleValue );

		MutableEntityPropertyDescriptor listValue = EntityPropertyDescriptor.builder( "members" )
		                                                                    .propertyType( Long[].class )
		                                                                    .controller( listValueController )
		                                                                    .build();
		when( registry.getProperty( "members" ) ).thenReturn( listValue );
		when( registry.getProperty( "members[]" ) ).thenReturn( singleValue );

//		MutableEntityPropertyDescriptor mapValue = EntityPropertyDescriptor.builder( "memberMap" )
//		                                                                   .propertyType(
//				                                                                   TypeDescriptor
//						                                                                   .map( LinkedHashMap.class,
//						                                                                         TypeDescriptor.valueOf( Long.class ),
//						                                                                         TypeDescriptor.valueOf( Long.class )
//						                                                                   )
//		                                                                   )
//		                                                                   .controller( mapValueController )
//		                                                                   .build();
//		when( registry.getProperty( "memberMap" ) ).thenReturn( mapValue );
//		when( registry.getProperty( "memberMap[key]" ) ).thenReturn( singleValue );
//		when( registry.getProperty( "memberMap[value]" ) ).thenReturn( singleValue );
	}

	@Test
	public void binderPrefixIsUsedForPropertyPath() {
		assertThat( binder.getPropertyBinderPath( "some.prop" ) ).isEqualTo( "properties[some.prop]" );
		binder.setBinderPrefix( "my.props" );
		assertThat( binder.getPropertyBinderPath( "some.prop.other" ) ).isEqualTo( "my.props[some.prop.other]" );
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
		SingleEntityPropertyBinder holder = single( "id" );
		assertThat( holder ).isNotNull();
		assertThat( binder.containsKey( "id" ) ).isTrue();
		assertThat( binder.get( "id" ) ).isSameAs( holder );
	}

	@Test
	public void listValueHolderReturnedForExistingProperty() {
		ListEntityPropertyBinder holder = list( "members" );
		assertThat( holder ).isNotNull();
		assertThat( binder.containsKey( "members" ) ).isTrue();
		assertThat( binder.get( "members" ) ).isSameAs( holder );
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
	public void setGetEntireCollectionOnMultiValue() {
		val holder = list( "members" );
		assertThat( holder.getValue() )
				.isNotNull()
				.isInstanceOf( Long[].class );
		assertThat( (Long[]) holder.getValue() ).isEmpty();
		assertThat( holder.getItems() ).isEmpty();

		holder.setValue( new Long[] { 123L, 456L } );
		assertThat( holder.getValue() )
				.isEqualTo( new Long[] { 123L, 456L } );

		assertThat( holder.getItems() )
				.hasSize( 2 )
				.containsKeys( "0", "1" );
		assertThat( holder.getItems().get( "0" ) )
				.matches( item -> item.getValue().equals( 123L ) )
				.matches( item -> item.getSortIndex() == 0 );
		assertThat( holder.getItems().get( "1" ) )
				.matches( item -> item.getValue().equals( 456L ) )
				.matches( item -> item.getSortIndex() == 1 );
	}

	@Test
	public void updateItemOnMultiValue() {
		val holder = list( "members" );
		holder.setValue( new Long[] { 123L, 456L } );

		holder.getItems().get( "0" ).setValue( 777L );
		holder.getItems().get( "1" ).setSortIndex( -1 );
		assertThat( holder.getValue() )
				.isEqualTo( new Long[] { 456L, 777L } );
	}

	@Test
	public void multiValueSettingSupportsTypeConversion() {
		val holder = list( "members" );
		holder.setValue( new LinkedHashSet<>( Arrays.asList( "456", "123" ) ) );
		assertThat( holder.getValue() )
				.isEqualTo( new Long[] { 456L, 123L } );

		holder.getItems().get( "0" ).setValue( "777" );
		assertThat( holder.getItems().get( "0" ).getValue() ).isEqualTo( 777L );
		assertThat( holder.getValue() )
				.isEqualTo( new Long[] { 777L, 123L } );
	}

	@Test
	public void rawExceptionsIfNoBinderPrefix() {
		val singleValueHolder = single( "id" );
		assertThatExceptionOfType( ConversionFailedException.class )
				.isThrownBy( () -> singleValueHolder.setValue( "abc" ) );
		assertThatExceptionOfType( ConverterNotFoundException.class )
				.isThrownBy( () -> singleValueHolder.setValue( singleValueHolder ) );

		val multiValueHolder = list( "members" );
		assertThatExceptionOfType( ConversionFailedException.class )
				.isThrownBy( () -> multiValueHolder.setValue( "abc" ) );
		assertThatExceptionOfType( ConverterNotFoundException.class )
				.isThrownBy( () -> multiValueHolder.setValue( singleValueHolder ) );

		assertThatExceptionOfType( ConversionFailedException.class )
				.isThrownBy( () -> multiValueHolder.getItems().get( "0" ).setValue( "abc" ) );
		assertThatExceptionOfType( ConverterNotFoundException.class )
				.isThrownBy( () -> multiValueHolder.getItems().get( "0" ).setValue( singleValueHolder ) );

		binder.setConversionService( null );
		assertThatExceptionOfType( ClassCastException.class )
				.isThrownBy( () -> singleValueHolder.setValue( 123 ) );
		assertThatExceptionOfType( ClassCastException.class )
				.isThrownBy( () -> multiValueHolder.setValue( 123 ) );
		assertThatExceptionOfType( ClassCastException.class )
				.isThrownBy( () -> multiValueHolder.getItems().get( "0" ).setValue( 123 ) );
	}

	@Test
	@Ignore
	public void wrappedExceptionsIfBinderPrefix() {
		binder.setBinderPrefix( "properties" );

		val singleValueHolder = single( "id" );
		assertThatExceptionOfType( ConversionNotSupportedException.class )
				.isThrownBy( () -> singleValueHolder.setValue( "abc" ) )
				.satisfies( e -> {
					assertThat( e.getPropertyName() ).isEqualTo( "properties[id].value" );
					assertThat( e.getRequiredType() ).isEqualTo( Long.class );
					assertThat( e.getValue() ).isEqualTo( "abc" );
				} );
		assertThatExceptionOfType( ConversionNotSupportedException.class )
				.isThrownBy( () -> singleValueHolder.setValue( singleValueHolder ) )
				.satisfies( e -> {
					assertThat( e.getPropertyName() ).isEqualTo( "properties[id].value" );
					assertThat( e.getRequiredType() ).isEqualTo( Long.class );
					assertThat( e.getValue() ).isSameAs( singleValueHolder );
				} );

		val multiValueHolder = list( "members" );
		assertThatExceptionOfType( ConversionNotSupportedException.class )
				.isThrownBy( () -> multiValueHolder.setValue( "abc" ) )
				.satisfies( e -> {
					assertThat( e.getPropertyName() ).isEqualTo( "properties[members].value" );
					assertThat( e.getRequiredType() ).isEqualTo( Long[].class );
					assertThat( e.getValue() ).isEqualTo( "abc" );
				} );
		assertThatExceptionOfType( ConversionNotSupportedException.class )
				.isThrownBy( () -> multiValueHolder.setValue( singleValueHolder ) )
				.satisfies( e -> {
					assertThat( e.getPropertyName() ).isEqualTo( "properties[members].value" );
					assertThat( e.getRequiredType() ).isEqualTo( Long[].class );
					assertThat( e.getValue() ).isSameAs( singleValueHolder );
				} );

		assertThatExceptionOfType( ConversionNotSupportedException.class )
				.isThrownBy( () -> multiValueHolder.getItems().get( "0" ).setValue( "abc" ) )
				.satisfies( e -> {
					assertThat( e.getPropertyName() ).isEqualTo( "properties[members].items[0].value" );
					assertThat( e.getRequiredType() ).isEqualTo( Long.class );
					assertThat( e.getValue() ).isEqualTo( "abc" );
				} );
		assertThatExceptionOfType( ConversionNotSupportedException.class )
				.isThrownBy( () -> multiValueHolder.getItems().get( "item-2" ).setValue( singleValueHolder ) )
				.satisfies( e -> {
					assertThat( e.getPropertyName() ).isEqualTo( "properties[members].items[item-2].value" );
					assertThat( e.getRequiredType() ).isEqualTo( Long.class );
					assertThat( e.getValue() ).isSameAs( singleValueHolder );
				} );

		binder.setConversionService( null );
		assertThatExceptionOfType( ConversionNotSupportedException.class )
				.isThrownBy( () -> singleValueHolder.setValue( 123 ) )
				.satisfies( e -> {
					assertThat( e.getPropertyName() ).isEqualTo( "properties[id].value" );
					assertThat( e.getRequiredType() ).isEqualTo( Long.class );
					assertThat( e.getValue() ).isEqualTo( 123 );
				} );
		assertThatExceptionOfType( ConversionNotSupportedException.class )
				.isThrownBy( () -> multiValueHolder.setValue( 123 ) )
				.satisfies( e -> {
					assertThat( e.getPropertyName() ).isEqualTo( "properties[members].value" );
					assertThat( e.getRequiredType() ).isEqualTo( Long[].class );
					assertThat( e.getValue() ).isEqualTo( 123 );
				} );
		assertThatExceptionOfType( ConversionNotSupportedException.class )
				.isThrownBy( () -> multiValueHolder.getItems().get( "0" ).setValue( 123 ) )
				.satisfies( e -> {
					assertThat( e.getPropertyName() ).isEqualTo( "properties[members].items[0].value" );
					assertThat( e.getRequiredType() ).isEqualTo( Long.class );
					assertThat( e.getValue() ).isEqualTo( 123 );
				} );
	}

	@SuppressWarnings("unchecked")
	@Test
	public void onlyModifiedPropertiesAndChildContextsAreBound() {
		val holder = single( "id" );
		holder.setValue( 123L );
		val listValue = list( "members" );
		assertThat( listValue.isModified() ).isFalse();

		val parentController = mock( EntityPropertyController.class );

		bindingContext.getOrCreateChildContext( "test", ( p, child ) ->
				child.entity( "original" )
				     .target( "dto" )
				     .controller( parentController )
		);

		binder.bind();

		verify( singleValueController ).applyValue( any(), eq( new EntityPropertyValue<>( null, 123L, false ) ) );
		verifyZeroInteractions( listValueController, mapValueController );

		verify( parentController ).applyValue( bindingContext, new EntityPropertyValue( "original", "dto", false ) );
	}

	@Test
	public void accessingNestedPropertyAsDirectOrViaChildPropertiesUseSameController() {
		// todo: binder.get( "user.name" ) and binder.get("user").getProperties().get("name")
	}

	private SingleEntityPropertyBinder single( String propertyName ) {
		return (SingleEntityPropertyBinder) binder.get( propertyName );
	}

	private ListEntityPropertyBinder list( String propertyName ) {
		return (ListEntityPropertyBinder) binder.get( propertyName );
	}
}
