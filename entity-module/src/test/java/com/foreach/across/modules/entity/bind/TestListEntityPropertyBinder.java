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
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 3.2.0
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings({ "Duplicates", "unchecked" })
public class TestListEntityPropertyBinder
{
	private static final EntityPropertyBindingContext BINDING_CONTEXT = EntityPropertyBindingContext.forReading( "entity" );
	private static final TypeDescriptor COLLECTION = TypeDescriptor.collection( ArrayList.class, TypeDescriptor.valueOf( Integer.class ) );
	private static final TypeDescriptor MEMBER = TypeDescriptor.valueOf( Integer.class );
	private static final List<Integer> ORIGINAL_VALUE = Arrays.asList( 1, 2 );

	@Mock
	private EntityPropertiesBinder binder;

	@Mock
	private EntityPropertyController collectionController;

	@Mock
	private EntityPropertyController memberController;

	@Mock
	private AbstractEntityPropertyBinder itemOne;

	@Mock
	private AbstractEntityPropertyBinder itemTwo;

	private EntityPropertyDescriptor collectionDescriptor;
	private EntityPropertyDescriptor memberDescriptor;
	private ListEntityPropertyBinder property;

	@Before
	public void resetMocks() {
		reset( binder, collectionController, memberController );

		when( binder.getBindingContext() ).thenReturn( BINDING_CONTEXT );
		when( collectionController.fetchValue( BINDING_CONTEXT ) ).thenReturn( ORIGINAL_VALUE );

		doAnswer( i -> Arrays.asList( (Object[]) i.getArgument( 0 ) ) )
				.when( binder )
				.convertIfNecessary( any(), eq( COLLECTION ), eq( "properties[collection].items" ) );
		doAnswer( i -> i.getArgument( 0 ) )
				.when( binder )
				.convertIfNecessary( any(), eq( COLLECTION ), eq( COLLECTION.getObjectType() ), eq( "properties[collection].value" ) );

		collectionDescriptor = spy(
				EntityPropertyDescriptor
						.builder( "collection" )
						.controller( collectionController )
						.propertyType( COLLECTION )
						.build()
		);

		memberDescriptor = spy(
				EntityPropertyDescriptor
						.builder( "member" )
						.controller( memberController )
						.propertyType( MEMBER )
						.build()
		);

		property = new ListEntityPropertyBinder( binder, collectionDescriptor, memberDescriptor );
		property.setBinderPath( "properties[collection]" );

		when( binder.createPropertyBinder( memberDescriptor ) )
				.thenReturn( itemOne )
				.thenReturn( itemTwo );
		when( itemOne.getValue() ).thenReturn( 1 );
		when( itemTwo.getValue() ).thenReturn( 2 );

		itemOne.setSortIndex( 1 );
		itemTwo.setSortIndex( 2 );
		assertThat( itemOne.getSortIndex() ).isEqualTo( 1 );
		assertThat( itemTwo.getSortIndex() ).isEqualTo( 2 );

		when( binder.getProperties() ).thenReturn( binder );
		when( binder.get( "prop" ) ).thenReturn( property );
	}

	@Test
	public void defaultProperties() {
		when( collectionController.getOrder() ).thenReturn( 33 );

		assertThat( property.isBound() ).isFalse();
		assertThat( property.isModified() ).isFalse();
		assertThat( property.isDeleted() ).isFalse();
		assertThat( property.getControllerOrder() ).isEqualTo( 33 );

		assertThat( property.getSortIndex() ).isEqualTo( 0 );
		property.setSortIndex( 123 );
		assertThat( property.getSortIndex() ).isEqualTo( 123 );
	}

	@Test
	public void originalValueVsValue() {
		assertThat( property.getOriginalValue() ).isEqualTo( ORIGINAL_VALUE );
		assertThat( property.getValue() ).isEqualTo( ORIGINAL_VALUE );

		val one = mock( AbstractEntityPropertyBinder.class );
		val two = mock( AbstractEntityPropertyBinder.class );
		when( binder.createPropertyBinder( memberDescriptor ) ).thenReturn( one )
		                                                       .thenReturn( two );
		property.setValue( Arrays.asList( 3, 4 ) );

		when( one.getValue() ).thenReturn( 3 );
		when( two.getValue() ).thenReturn( 4 );

		assertThat( property.getOriginalValue() ).isEqualTo( ORIGINAL_VALUE );
		assertThat( property.getValue() ).isEqualTo( Arrays.asList( 3, 4 ) );
	}

	@Test
	public void itemCanBeAddedByKey() {
		assertThat( property.getValue() ).isEqualTo( ORIGINAL_VALUE );

		val random = mock( AbstractEntityPropertyBinder.class );
		random.setSortIndex( -1 );
		when( binder.createPropertyBinder( memberDescriptor ) ).thenReturn( random );

		property.getItems().get( "random" ).setValue( 33 );
		verify( random ).setValue( 33 );
		when( random.getValue() ).thenReturn( 33 );

		assertThat( property.getValue() ).isEqualTo( Arrays.asList( 33, 1, 2 ) );
	}

	@Test
	public void valueIsInitializedEmptyIfNotIncrementalBindingAndBindingBusy() {
		property.enableBinding( true );

		assertThat( property.getItemList() ).isEmpty();
		assertThat( property.getItems() ).isEmpty();
		assertThat( property.getValue() ).isEqualTo( Collections.emptyList() );
	}

	@Test
	public void valueIsInitializedWithOriginalValueWithIncrementalBinding() {
		property.enableBinding( true );
		property.setUpdateItemsOnBinding( true );

		assertThat( property.getItemList() ).hasSize( 2 );
		assertThat( property.getItems() ).hasSize( 2 );
		assertThat( property.getValue() ).isEqualTo( ORIGINAL_VALUE );
	}

	@Test
	public void deletedItemsArePresentInTheItemsButNotInTheValueWhileBindingIsBusy() {
		itemTwo.setDeleted( true );

		assertThat( property.getItemList() ).hasSize( 2 );
		assertThat( property.getItems() ).hasSize( 2 );
		assertThat( property.getValue() ).isEqualTo( Collections.singletonList( 1 ) );
	}

	@Test
	public void deletedItemsAreRemovedWhenBindingIsDisabled() {
		itemTwo.setDeleted( true );
		assertThat( property.getItems() ).hasSize( 2 );
		property.enableBinding( false );

		assertThat( property.getItemList() ).hasSize( 1 );
		assertThat( property.getItems() ).hasSize( 1 );
		assertThat( property.getValue() ).isEqualTo( Collections.singletonList( 1 ) );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void templateGetsLazilyCreatedOnce() {
		AbstractEntityPropertyBinder template = mock( AbstractEntityPropertyBinder.class );

		when( binder.createPropertyBinder( memberDescriptor ) ).thenReturn( template );

		assertThat( property.getItemTemplate() ).isSameAs( template );
		assertThat( property.getItemTemplate() ).isSameAs( template );
		verify( binder, times( 1 ) ).createPropertyBinder( any() );
	}

	@Test
	public void initialValueGetsLazyLoadedFromTheController() {
		verify( collectionController, never() ).fetchValue( any() );
		assertThat( property.getValue() ).isEqualTo( ORIGINAL_VALUE );
	}

	@Test
	public void itemsAreLoaded() {
		val items = property.getItems();
		assertThat( items )
				.isNotNull()
				.hasSize( 2 );

		assertThat( items.get( "0" ) ).isSameAs( itemOne );
		verify( itemOne ).setValue( 1 );
		assertThat( itemOne.getSortIndex() ).isEqualTo( 0 );

		assertThat( items.get( "1" ) ).isSameAs( itemTwo );
		verify( itemTwo ).setValue( 2 );
		assertThat( itemTwo.getSortIndex() ).isEqualTo( 1 );
	}

	@Test
	public void valueSortsTheItems() {
		assertThat( property.getValue() ).isEqualTo( ORIGINAL_VALUE );

		itemTwo.setSortIndex( -1 );
		assertThat( property.getValue() ).isEqualTo( Arrays.asList( 2, 1 ) );
	}

	@Test
	public void itemListIsSorted() {
		assertThat( property.getItemList() )
				.isNotNull()
				.hasSize( 2 )
				.isEqualTo( Arrays.asList( itemOne, itemTwo ) );

		itemTwo.setSortIndex( -1 );
		assertThat( property.getItemList() ).isEqualTo( Arrays.asList( itemTwo, itemOne ) );
	}

	@Test
	public void itemsGetRecreatedIfValueHasChanged() {
		assertThat( property.getItems() ).hasSize( 2 );

		property.setValue( Collections.singletonList( 3 ) );

		assertThat( property.getItems() ).hasSize( 1 ).containsKey( "0" );
		verify( property.getItems().get( "0" ) ).setValue( 3 );
	}

	@Test
	public void updatingTheValueDoesNotUseTheController() {
		property.setValue( Collections.singletonList( 3 ) );
		verify( collectionController ).fetchValue( BINDING_CONTEXT );
		verifyNoMoreInteractions( collectionController );
	}

	@Test
	public void modifiedIfTheNewValueCollectionIsDifferentFromTheOriginal() {
		assertThat( property.isModified() ).isFalse();

		val original = ORIGINAL_VALUE;
		assertThat( property.getValue() ).isEqualTo( original );
		assertThat( property.isModified() ).isFalse();

		when( binder.convertIfNecessary( any(), eq( COLLECTION ), eq( "properties[collection].items" ) ) ).thenReturn( Collections.emptyList() );
		assertThat( property.isModified() ).isTrue();
	}

	@Test
	public void boundButNotSetValueModifies() {
		property.enableBinding( true );
		property.setBound( true );
		assertThat( property.isModified() ).isTrue();

		property.enableBinding( false );
		assertThat( property.isModified() ).isTrue();
		assertThat( property.getValue() ).isEqualTo( Collections.emptyList() );
	}

	@Test
	public void boundButItemsFetchedDoesNotModify() {
		property.setBound( true );
		assertThat( property.getItems() ).hasSize( 2 );
		assertThat( property.getValue() ).isEqualTo( ORIGINAL_VALUE );
		assertThat( property.isModified() ).isFalse();
	}

	@Test
	public void boundDuringBindingButValueNotSetIsEmptyButNotDeleted() {
		property.enableBinding( true );
		property.setBound( true );
		assertThat( property.isDeleted() ).isFalse();
		assertThat( property.getValue() ).isEqualTo( Collections.emptyList() );
		assertThat( property.getItems() ).isEmpty();

	}

	@Test
	public void boundDuringBindingButUpdatingDoesNotClearItems() {
		property.enableBinding( true );
		property.setBound( true );
		property.setUpdateItemsOnBinding( true );
		assertThat( property.isDeleted() ).isFalse();
		assertThat( property.getValue() ).isEqualTo( ORIGINAL_VALUE );
		assertThat( property.getItems() ).hasSize( 2 );
	}

	@Test
	public void explicitlyDeletedDoesNotClearTheItemsImmediately() {
		property.setDeleted( true );
		assertThat( property.isDeleted() ).isTrue();
		assertThat( property.getValue() ).isEqualTo( Collections.emptyList() );
		assertThat( property.getItemList() ).hasSize( 2 );
		assertThat( property.getItems() ).hasSize( 2 );
	}

	@Test
	public void itemsAreClearedOnDeletedPropertyWhenBindingDisabled() {
		property.setDeleted( true );
		property.enableBinding( false );
		assertThat( property.isDeleted() ).isTrue();
		assertThat( property.getValue() ).isEqualTo( Collections.emptyList() );
		assertThat( property.getItemList() ).isEmpty();
		assertThat( property.getItems() ).isEmpty();
	}

	@Test
	public void boundButValueAssignedIsNotDeleted() {
		property.enableBinding( true );
		property.setBound( true );
		property.setValue( ORIGINAL_VALUE );
		assertThat( property.isDeleted() ).isFalse();
		assertThat( property.isModified() ).isFalse();
		assertThat( property.getItems() ).hasSize( 2 );
	}

	@Test
	public void settingNullValueIsNotSameAsDeleted() {
		property.setValue( null );
		assertThat( property.isDeleted() ).isFalse();
	}

	@Test
	public void settingEmptyCollectionValueIsNotSameAsDeleted() {
		property.setValue( Collections.emptyList() );
		assertThat( property.isDeleted() ).isFalse();
	}

	@Test
	public void createNewValueCreatesNewValueButDoesNotUpdatePropertyItself() {
		when( binder.createValue( collectionController ) )
				.thenReturn( Arrays.asList( 55, 66 ) );
		Object value = property.createNewValue();
		assertThat( value ).isEqualTo( Arrays.asList( 55, 66 ) );

		assertThat( property.getValue() ).isEqualTo( ORIGINAL_VALUE );
	}

	@Test
	public void getInitializedValueDoesNotModifyCurrentValue() {
		assertThat( property.getInitializedValue() ).isEqualTo( ORIGINAL_VALUE );
		verify( binder, never() ).createValue( any() );
	}

	@Test
	public void getInitializedValueInitializesNewValueIfNullFetched() {
		reset( collectionController );
		when( binder.createValue( collectionController ) )
				.thenReturn( Arrays.asList( 55, 66 ) );

		when( itemOne.getValue() ).thenReturn( 55 );
		when( itemTwo.getValue() ).thenReturn( 66 );
		assertThat( property.getInitializedValue() ).isEqualTo( Arrays.asList( 55, 66 ) );
		verify( binder ).createValue( collectionController );
		verify( itemOne ).setValue( 55 );
		verify( itemTwo ).setValue( 66 );

		assertThat( property.getValue() ).isEqualTo( Arrays.asList( 55, 66 ) );
		assertThat( property.getOriginalValue() ).isNull();
	}

	@Test
	public void applyValueFlushesToTheControllerWithTheOriginalValue() {
		when( collectionController.applyValue( BINDING_CONTEXT, new EntityPropertyValue<>( ORIGINAL_VALUE, ORIGINAL_VALUE, false ) ) ).thenReturn( true );
		assertThat( property.applyValue() ).isTrue();

		when( itemOne.getValue() ).thenReturn( 3 );
		assertThat( property.applyValue() ).isFalse();
		verify( collectionController ).applyValue( BINDING_CONTEXT, new EntityPropertyValue<>( ORIGINAL_VALUE, Arrays.asList( 3, 2 ), false ) );
	}

	@Test
	public void applyValueFlagsDelete() {
		when( collectionController.applyValue( BINDING_CONTEXT, new EntityPropertyValue<>( ORIGINAL_VALUE, Collections.emptyList(), true ) ) )
				.thenReturn( true );
		property.setDeleted( true );
		assertThat( property.applyValue() ).isTrue();
		verify( collectionController ).applyValue( BINDING_CONTEXT, new EntityPropertyValue<>( ORIGINAL_VALUE, Collections.emptyList(), true ) );
	}

	@Test
	public void saveFlushesToTheControllerWithTheOriginalValue() {
		when( collectionController.save( BINDING_CONTEXT, new EntityPropertyValue<>( ORIGINAL_VALUE, ORIGINAL_VALUE, false ) ) ).thenReturn( true );
		assertThat( property.save() ).isTrue();

		when( itemOne.getValue() ).thenReturn( 3 );
		assertThat( property.save() ).isFalse();
		verify( collectionController ).save( BINDING_CONTEXT, new EntityPropertyValue<>( ORIGINAL_VALUE, Arrays.asList( 3, 2 ), false ) );
	}

	@Test
	public void saveWithNullIfDeleted() {
		when( collectionController.save( BINDING_CONTEXT, new EntityPropertyValue<>( ORIGINAL_VALUE, Collections.emptyList(), true ) ) )
				.thenReturn( true );
		property.setDeleted( true );
		assertThat( property.save() ).isTrue();
		verify( collectionController ).save( BINDING_CONTEXT, new EntityPropertyValue<>( ORIGINAL_VALUE, Collections.emptyList(), true ) );
	}

	@Test
	public void validate() {
		Errors errors = new BeanPropertyBindingResult( binder, "" );
		errors.pushNestedPath( "properties[prop]" );

		assertThat( property.validate( errors, "hint" ) ).isTrue();
		assertThat( errors.getErrorCount() ).isEqualTo( 0 );

		property.getItems().get( "0" ).setValue( "do nothing" );

		doAnswer( invocation -> {
			Errors err = invocation.getArgument( 0 );
			err.rejectValue( "value", "bad-value" );

			return null;
		} )
				.when( itemOne )
				.validate( errors, "hint" );

		doAnswer( invocation -> {
			Errors err = invocation.getArgument( 2 );
			err.rejectValue( "", "bad-value" );

			return null;
		} )
				.when( collectionController )
				.validate( any(), any(), eq( errors ), eq( "hint" ) );

		assertThat( property.validate( errors, "hint" ) ).isFalse();

		errors.popNestedPath();

		assertThat( errors.getErrorCount() ).isEqualTo( 2 );
		assertThat( errors.getFieldError( "properties[prop].items[0].value" ) )
				.isNotNull()
				.satisfies( fe -> {
					assertThat( fe.isBindingFailure() ).isFalse();
					assertThat( fe.getField() ).isEqualTo( "properties[prop].items[0].value" );
					assertThat( fe.getCode() ).isEqualTo( "bad-value" );
				} );
		assertThat( errors.getFieldError( "properties[prop].value" ) )
				.isNotNull()
				.satisfies( fe -> {
					assertThat( fe.isBindingFailure() ).isFalse();
					assertThat( fe.getField() ).isEqualTo( "properties[prop].value" );
					assertThat( fe.getCode() ).isEqualTo( "bad-value" );
				} );
	}
}
