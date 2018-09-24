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

import com.foreach.across.modules.entity.registry.properties.EntityPropertyBindingContext;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyController;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyValue;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.convert.TypeDescriptor;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 3.2.0
 */
@SuppressWarnings("Duplicates")
@RunWith(MockitoJUnitRunner.class)
public class TestSingleEntityPropertyBinder
{
	private static final EntityPropertyBindingContext<Object, Object> BINDING_CONTEXT = new EntityPropertyBindingContext<>( "entity" );

	@Mock
	private EntityPropertiesBinder binder;

	@Mock
	private EntityPropertyController<Object, Object> controller;

	private EntityPropertyDescriptor descriptor;
	private SingleEntityPropertyBinder property;

	@Before
	@SuppressWarnings("unchecked")
	public void resetMocks() {
		reset( binder, controller );

		when( binder.getBindingContext() ).thenReturn( BINDING_CONTEXT );
		when( controller.fetchValue( BINDING_CONTEXT ) ).thenReturn( 1 );

		doAnswer( i -> i.getArgument( 0 ) )
				.when( binder )
				.convertIfNecessary( any(), eq( TypeDescriptor.valueOf( Integer.class ) ), eq( "[prop].value" ) );

		descriptor = spy(
				EntityPropertyDescriptor
						.builder( "prop" )
						.controller( controller )
						.propertyType( Integer.class )
						.build()
		);

		property = new SingleEntityPropertyBinder( binder, descriptor );
	}

	@Test
	public void defaultProperties() {
		when( controller.getOrder() ).thenReturn( 33 );

		assertThat( property.isBound() ).isFalse();
		assertThat( property.isModified() ).isFalse();
		assertThat( property.getControllerOrder() ).isEqualTo( 33 );

		assertThat( property.getSortIndex() ).isEqualTo( 0 );
		property.setSortIndex( 123 );
		assertThat( property.getSortIndex() ).isEqualTo( 123 );
	}

	@Test
	public void initialValueGetsLazyLoadedFromTheController() {
		verify( controller, never() ).fetchValue( any() );
		assertThat( property.getValue() ).isEqualTo( 1 );
	}

	@Test
	public void updatingTheValueDoesNotUseTheController() {
		property.setValue( 123 );
		verify( controller ).fetchValue( BINDING_CONTEXT );

		assertThat( property.getValue() ).isEqualTo( 123 );
		verifyNoMoreInteractions( controller );
		property.setValue( 567 );
		assertThat( property.getValue() ).isEqualTo( 567 );
		verifyNoMoreInteractions( controller );
	}

	@Test
	public void originalValueVsCurrentValue() {
		assertThat( property.getOriginalValue() ).isEqualTo( 1 );
		assertThat( property.getValue() ).isEqualTo( 1 );
		property.setValue( 123 );
		assertThat( property.getOriginalValue() ).isEqualTo( 1 );
		assertThat( property.getValue() ).isEqualTo( 123 );
	}

	@Test
	public void updatingWithSameValueDoesNotModify() {
		assertThat( property.isModified() ).isFalse();
		property.setValue( 1 );
		assertThat( property.isModified() ).isFalse();
	}

	@Test
	public void updatingWithOtherValueModifies() {
		assertThat( property.isModified() ).isFalse();
		property.setValue( 123 );
		assertThat( property.isModified() ).isTrue();
	}

	@Test
	public void boundButNotSetValueModifies() {
		property.setBound( true );
		assertThat( property.isModified() ).isTrue();
	}

	@Test
	public void boundButSameValueDoesNotModify() {
		property.setBound( true );
		property.setValue( 1 );
		assertThat( property.isModified() ).isFalse();

		resetMocks();

		property.setValue( 1 );
		property.setBound( true );
		assertThat( property.isModified() ).isFalse();
	}

	@Test
	public void boundButNotSetIsConsideredDeleted() {
		property.setBound( true );
		assertThat( property.getValue() ).isNull();
		assertThat( property.isDeleted() ).isTrue();
	}

	@Test
	public void notBoundButExplicitlyDeleted() {
		assertThat( property.isDeleted() ).isFalse();
		property.setDeleted( true );
		assertThat( property.isDeleted() ).isTrue();
	}

	@Test
	public void settingNullValueIsNotSameAsDeleted() {
		property.setValue( null );
		assertThat( property.getValue() ).isNull();
		assertThat( property.isDeleted() ).isFalse();
	}

	@Test
	public void settingEmptyStringOnNonStringPropertyResultsInNullValue() {
		property.setValue( "" );
		assertThat( property.getValue() ).isNull();
		verify( controller ).fetchValue( BINDING_CONTEXT );
		verifyNoMoreInteractions( controller );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void emptyStringOnStringPropertyIsAllowed() {
		when( descriptor.getPropertyType() ).thenReturn( (Class) String.class );
		when( descriptor.getPropertyTypeDescriptor() ).thenReturn( TypeDescriptor.valueOf( String.class ) );

		doAnswer( i -> i.getArgument( 0 ) )
				.when( binder )
				.convertIfNecessary( any(), eq( TypeDescriptor.valueOf( String.class ) ), eq( "[prop].value" ) );

		property.setValue( "" );
		assertThat( property.getValue() ).isEqualTo( "" );

		verify( controller ).fetchValue( BINDING_CONTEXT );
		verifyNoMoreInteractions( controller );
	}

	@Test
	public void initializeValueCreatesNewValueButDoesNotUpdatePropertyItself() {
		when( binder.createValue( controller, TypeDescriptor.valueOf( Integer.class ) ) )
				.thenReturn( "hello" );
		Object value = property.createNewValue();
		assertThat( value ).isEqualTo( "hello" );

		assertThat( property.getValue() ).isEqualTo( 1 );
	}

	@Test
	public void getInitializedValueDoesNotModifyCurrentValue() {
		assertThat( property.getInitializedValue() ).isEqualTo( 1 );
		verify( binder, never() ).createValue( any(), any() );
	}

	@Test
	public void getInitializedValueInitializesNewValueIfNull() {
		property.setValue( null );
		when( binder.createValue( controller, TypeDescriptor.valueOf( Integer.class ) ) )
				.thenReturn( 123 );
		assertThat( property.getInitializedValue() ).isEqualTo( 123 );
		assertThat( property.getValue() ).isEqualTo( 123 );
	}

	@Test
	public void applyValueFlushesToTheControllerWithTheOriginalValue() {
		when( controller.applyValue( BINDING_CONTEXT, new EntityPropertyValue<>( 1, 1, false ) ) ).thenReturn( true );
		assertThat( property.applyValue() ).isTrue();

		property.setValue( 123 );
		assertThat( property.applyValue() ).isFalse();
		verify( controller ).applyValue( BINDING_CONTEXT, new EntityPropertyValue<>( 1, 123, false ) );
	}

	@Test
	public void applyValueAppliesNullIfDeleted() {
		when( controller.applyValue( BINDING_CONTEXT, new EntityPropertyValue<>( 1, null, true ) ) ).thenReturn( true );
		property.setBound( true );
		assertThat( property.applyValue() ).isTrue();
		verify( controller ).applyValue( BINDING_CONTEXT, new EntityPropertyValue<>( 1, null, true ) );
	}

	@Test
	public void saveFlushesToTheControllerWithTheOriginalValue() {
		when( controller.save( BINDING_CONTEXT, new EntityPropertyValue<>( 1, 1, false ) ) ).thenReturn( true );
		assertThat( property.save() ).isTrue();

		property.setValue( 123 );
		assertThat( property.save() ).isFalse();
		verify( controller ).save( BINDING_CONTEXT, new EntityPropertyValue<>( 1, 123, false ) );
	}

	@Test
	public void saveWithNullIfDeleted() {
		when( controller.save( BINDING_CONTEXT, new EntityPropertyValue<>( 1, null, true ) ) ).thenReturn( true );
		property.setBound( true );
		assertThat( property.save() ).isTrue();
		verify( controller ).save( BINDING_CONTEXT, new EntityPropertyValue<>( 1, null, true ) );
	}

	@Test
	public void validate() {


		// todo: implement validation test
	}

	@Test
	public void propertiesGetCreatedFirstTime() {
		property.setValue( null );

		when( binder.createValue( controller, TypeDescriptor.valueOf( Integer.class ) ) )
				.thenReturn( "hello" );
		EntityPropertiesBinder childBinder = mock( EntityPropertiesBinder.class );
		when( binder.createChildBinder( eq( descriptor ), any(), eq( "hello" ) ) ).thenReturn( childBinder );

		assertThat( property.getProperties() ).isSameAs( childBinder );
		assertThat( property.getProperties() ).isSameAs( childBinder );
		verify( binder, times( 1 ) ).createChildBinder( any(), any(), any() );
	}

	@Test
	public void propertiesGetRecreatedIfValueHasChanged() {
		when( binder.createChildBinder( eq( descriptor ), any(), eq( 1 ) ) ).thenReturn( mock( EntityPropertiesBinder.class ) );
		when( binder.createChildBinder( eq( descriptor ), any(), eq( 123 ) ) ).thenReturn( mock( EntityPropertiesBinder.class ) );

		val first = property.getProperties();
		assertThat( property.getProperties() ).isNotNull().isSameAs( first );

		property.setValue( 1 );
		assertThat( property.getProperties() ).isSameAs( first );

		verify( binder, times( 1 ) ).createChildBinder( any(), any(), any() );

		property.setValue( 123 );
		assertThat( property.getProperties() ).isNotNull().isNotSameAs( first );
	}

	@Test
	public void propertiesAreAppliedBeforeReturningValue() {
		EntityPropertiesBinder childBinder = mock( EntityPropertiesBinder.class );
		EntityPropertyBinder childValue = mock( EntityPropertyBinder.class );
		when( childBinder.values() ).thenReturn( Collections.singleton( childValue ) );
		when( binder.createChildBinder( eq( descriptor ), any(), eq( 1 ) ) ).thenReturn( childBinder );

		assertThat( property.getValue() ).isEqualTo( 1 );
		verifyZeroInteractions( childBinder );

		assertThat( property.getProperties() ).isSameAs( childBinder );
		assertThat( property.getValue() ).isEqualTo( 1 );
		verify( childValue ).applyValue();
	}

	@Test
	public void propertyNoLongerDeletedIfPropertiesAccessed() {
		EntityPropertiesBinder childBinder = mock( EntityPropertiesBinder.class );
		when( binder.createChildBinder( eq( descriptor ), any(), eq( 1 ) ) ).thenReturn( childBinder );

		property.setBound( true );
		assertThat( property.isDeleted() ).isTrue();

		assertThat( property.getProperties() ).isSameAs( childBinder );
		assertThat( property.isDeleted() ).isFalse();
	}

	@Test
	public void resetBindStatus() {
		property.setBound( true );
		assertThat( property.isModified() ).isTrue();
		assertThat( property.isBound() ).isTrue();
		property.setValue( 123 );
		assertThat( property.isModified() ).isTrue();
		assertThat( property.isBound() ).isTrue();
		property.resetBindStatus();
		assertThat( property.isBound() ).isFalse();
		assertThat( property.isModified() ).isFalse();
	}
}
