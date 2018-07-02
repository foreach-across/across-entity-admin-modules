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
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.convert.TypeDescriptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 3.1.0
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("Duplicates")
public class TestListEntityPropertyValueController
{
	private static final EntityPropertyBindingContext<Object, Object> BINDING_CONTEXT = new EntityPropertyBindingContext<>( "entity" );
	private static final TypeDescriptor COLLECTION = TypeDescriptor.collection( ArrayList.class, TypeDescriptor.valueOf( Integer.class ) );
	private static final TypeDescriptor MEMBER = TypeDescriptor.valueOf( Integer.class );

	@Mock
	private EntityPropertiesBinder binder;

	@Mock
	private EntityPropertyController<Object, Object> collectionController;

	@Mock
	private EntityPropertyController<Object, Object> memberController;

	@Mock
	private EntityPropertyValueController<Object> itemOne;

	@Mock
	private EntityPropertyValueController<Object> itemTwo;

	private EntityPropertyDescriptor collectionDescriptor;
	private EntityPropertyDescriptor memberDescriptor;
	private ListEntityPropertyValueController property;

	@Before
	@SuppressWarnings("unchecked")
	public void resetMocks() {
		reset( binder, collectionController, memberController );

		when( binder.getBindingContext() ).thenReturn( BINDING_CONTEXT );
		when( collectionController.fetchValue( BINDING_CONTEXT ) ).thenReturn( Arrays.asList( 1, 2 ) );

		doAnswer( i -> Arrays.asList( (Object[]) i.getArgument( 0 ) ) )
				.when( binder )
				.convertIfNecessary( any(), eq( COLLECTION ), eq( "[collection].items" ) );
		doAnswer( i -> i.getArgument( 0 ) )
				.when( binder )
				.convertIfNecessary( any(), eq( COLLECTION ), eq( COLLECTION.getObjectType() ), eq( "[collection].value" ) );

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

		property = new ListEntityPropertyValueController( binder, collectionDescriptor, memberDescriptor );

		when( binder.createValueController( memberDescriptor ) )
				.thenReturn( itemOne )
				.thenReturn( itemTwo );
		when( itemOne.getValue() ).thenReturn( 1 );
		when( itemTwo.getValue() ).thenReturn( 2 );
		when( itemOne.getSortIndex() ).thenReturn( 1 );
		when( itemTwo.getSortIndex() ).thenReturn( 2 );
	}

	@Test
	public void defaultProperties() {
		when( collectionController.getOrder() ).thenReturn( 33 );

		assertThat( property.isBound() ).isFalse();
		assertThat( property.isModified() ).isFalse();
		assertThat( property.getControllerOrder() ).isEqualTo( 33 );

		assertThat( property.getSortIndex() ).isEqualTo( 0 );
		property.setSortIndex( 123 );
		assertThat( property.getSortIndex() ).isEqualTo( 123 );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void templateGetsLazilyCreatedOnce() {
		EntityPropertyValueController<Object> template = mock( EntityPropertyValueController.class );

		when( binder.createValueController( memberDescriptor ) ).thenReturn( template );
		when( binder.createValue( memberController, TypeDescriptor.valueOf( Integer.class ) ) ).thenReturn( 123 );

		assertThat( property.getTemplate() ).isSameAs( template );
		assertThat( property.getTemplate() ).isSameAs( template );
		verify( binder, times( 1 ) ).createValueController( any() );
		verify( template, times( 1 ) ).setValue( 123 );
	}

	@Test
	public void initialValueGetsLazyLoadedFromTheController() {
		verify( collectionController, never() ).fetchValue( any() );
		assertThat( property.getValue() ).isEqualTo( Arrays.asList( 1, 2 ) );
	}

	@Test
	public void itemsAreLoaded() {
		val items = property.getItems();
		assertThat( items )
				.isNotNull()
				.hasSize( 2 );

		assertThat( items.get( "0" ) ).isSameAs( itemOne );
		verify( itemOne ).setValue( 1 );
		verify( itemOne ).setSortIndex( 0 );

		assertThat( items.get( "1" ) ).isSameAs( itemTwo );
		verify( itemTwo ).setValue( 2 );
		verify( itemTwo ).setSortIndex( 1 );
	}

	@Test
	public void valueSortsTheItems() {
		assertThat( property.getValue() ).isEqualTo( Arrays.asList( 1, 2 ) );

		when( itemTwo.getSortIndex() ).thenReturn( -1 );
		assertThat( property.getValue() ).isEqualTo( Arrays.asList( 2, 1 ) );
	}

	@Test
	public void itemListIsSorted() {
		assertThat( property.getItemList() )
				.isNotNull()
				.hasSize( 2 )
				.isEqualTo( Arrays.asList( itemOne, itemTwo ) );

		when( itemTwo.getSortIndex() ).thenReturn( -1 );
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

		val original = Arrays.asList( 1, 2 );
		assertThat( property.getValue() ).isEqualTo( original );
		assertThat( property.isModified() ).isFalse();

		when( binder.convertIfNecessary( any(), eq( COLLECTION ), eq( "[collection].items" ) ) ).thenReturn( Collections.emptyList() );
		assertThat( property.isModified() ).isTrue();
	}

	@Test
	public void boundButNotSetValueModifies() {
		property.setBound( true );
		assertThat( property.isModified() ).isTrue();
	}

	@Test
	public void boundButItemsFetchedDoesNotModify() {
		property.setBound( true );
		assertThat( property.getItems() ).hasSize( 2 );
		assertThat( property.getValue() ).isEqualTo( Arrays.asList( 1, 2 ) );
		assertThat( property.isModified() ).isFalse();
	}

	@Test
	public void boundButNotSetIsConsideredDeleted() {
		property.setBound( true );
		assertThat( property.isDeleted() ).isTrue();
		assertThat( property.getValue() ).isEqualTo( Collections.emptyList() );
		assertThat( property.isDeleted() ).isTrue();

		// items should be cleared, after accessing the value
		assertThat( property.getItems() ).isEmpty();
	}

	@Test
	public void propertyNoLongerDeletedIfItemsAccessed() {
		property.setBound( true );
		assertThat( property.isDeleted() ).isTrue();
		assertThat( property.getItems() ).isNotEmpty();
		assertThat( property.isDeleted() ).isFalse();
	}

	@Test
	public void boundButValueAssignedIsNotDeleted() {
		property.setBound( true );
		assertThat( property.isDeleted() ).isTrue();
		property.setValue( Arrays.asList( 1, 2 ) );
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
	public void deletedItemsAreFilteredOutInTheFinalValueButPresentInTheItemList() {

	}

	/*
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
	public void propertyNoLongerDeletedIfPropertiesAccessed() {
		EntityPropertiesBinder childBinder = mock( EntityPropertiesBinder.class );
		when( binder.createChildBinder( descriptor, 1 ) ).thenReturn( childBinder );

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
	*/
}
