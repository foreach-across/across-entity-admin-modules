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
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 3.2.0
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings({ "Duplicates", "unchecked" })
public class TestMapEntityPropertyBinder
{
	private static final EntityPropertyBindingContext BINDING_CONTEXT = EntityPropertyBindingContext.forReading( "entity" );
	private static final TypeDescriptor KEY = TypeDescriptor.valueOf( String.class );
	private static final TypeDescriptor VALUE = TypeDescriptor.valueOf( Integer.class );
	private static final TypeDescriptor COLLECTION = TypeDescriptor.map( LinkedHashMap.class, KEY, VALUE );
	private static final Map<String, Integer> ORIGINAL_VALUE = Collections.singletonMap( "one", 1 );

	@Mock
	private EntityPropertiesBinder binder;

	@Mock
	private EntityPropertyController<Object, Object> collectionController;

	@Mock
	private EntityPropertyController<Object, Object> keyController;

	@Mock
	private EntityPropertyController<Object, Object> valueController;

	@Mock
	private EntityPropertyBinder<Object> key;

	@Mock
	private EntityPropertyBinder<Object> value;

	private EntityPropertyDescriptor collectionDescriptor;
	private EntityPropertyDescriptor keyDescriptor;
	private EntityPropertyDescriptor valueDescriptor;
	private MapEntityPropertyBinder property;

	@Before
	public void resetMocks() {
		reset( binder, collectionController, keyController, valueController );

		when( binder.getBindingContext() ).thenReturn( BINDING_CONTEXT );
		when( collectionController.fetchValue( BINDING_CONTEXT ) ).thenReturn( ORIGINAL_VALUE );

		doAnswer( i -> i.getArgument( 0 ) )
				.when( binder )
				.convertIfNecessary( any(), eq( COLLECTION ), eq( "[collection].entries" ) );

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

		keyDescriptor = spy(
				EntityPropertyDescriptor
						.builder( "key" )
						.controller( keyController )
						.propertyType( KEY )
						.build()
		);

		valueDescriptor = spy(
				EntityPropertyDescriptor
						.builder( "value" )
						.controller( valueController )
						.propertyType( VALUE )
						.build()
		);

		property = new MapEntityPropertyBinder( binder, collectionDescriptor, keyDescriptor, valueDescriptor );

		when( binder.createPropertyBinder( keyDescriptor ) ).thenReturn( key );
		when( binder.createPropertyBinder( valueDescriptor ) ).thenReturn( value );

		when( key.getValue() ).thenReturn( "one" );
		when( value.getValue() ).thenReturn( 1 );

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
	public void templateGetsLazilyCreatedOnce() {
		EntityPropertyBinder<Object> templateKey = mock( EntityPropertyBinder.class );
		EntityPropertyBinder<Object> templateValue = mock( EntityPropertyBinder.class );

		when( binder.createPropertyBinder( keyDescriptor ) ).thenReturn( templateKey );
		when( binder.createPropertyBinder( valueDescriptor ) ).thenReturn( templateValue );

		assertThat( property.getTemplate().getKey() ).isSameAs( templateKey );
		assertThat( property.getTemplate().getValue() ).isSameAs( templateValue );
	}

	@Test
	public void initialValueGetsLazyLoadedFromTheController() {
		verify( collectionController, never() ).fetchValue( any() );
		assertThat( property.getOriginalValue() ).isEqualTo( ORIGINAL_VALUE );
	}

	@Test
	public void itemsAreLoaded() {
		val items = property.getEntries();
		assertThat( items )
				.isNotNull()
				.hasSize( 1 );

		assertThat( items.get( "0" ).getSortIndex() ).isEqualTo( 0 );

		assertThat( items.get( "0" ).getKey() ).isSameAs( key );
		verify( key ).setValue( "one" );

		assertThat( items.get( "0" ).getValue() ).isSameAs( value );
		verify( value ).setValue( 1 );
	}

	@Test
	public void valueIsInitializedEmptyIfNotIncrementalBindingAndBindingBusy() {
		property.enableBinding( true );

		assertThat( property.getEntries() ).isEmpty();
		assertThat( property.getValue() ).isEqualTo( Collections.emptyMap() );
	}

	@Test
	public void originalValueVsValue() {
		assertThat( property.getOriginalValue() ).isEqualTo( ORIGINAL_VALUE );
		assertThat( property.getValue() ).isEqualTo( ORIGINAL_VALUE );

		val keyOne = mock( EntityPropertyBinder.class );
		val keyTwo = mock( EntityPropertyBinder.class );
		when( binder.createPropertyBinder( keyDescriptor ) ).thenReturn( keyOne ).thenReturn( keyTwo );

		val valueOne = mock( EntityPropertyBinder.class );
		val valueTwo = mock( EntityPropertyBinder.class );
		when( binder.createPropertyBinder( valueDescriptor ) ).thenReturn( valueOne ).thenReturn( valueTwo );

		Map<String, Integer> values = new LinkedHashMap<>();
		values.put( "two", 2 );
		values.put( "three", 3 );
		property.setValue( values );

		when( keyOne.getValue() ).thenReturn( "two" );
		when( valueOne.getValue() ).thenReturn( 2 );
		when( keyTwo.getValue() ).thenReturn( "three" );
		when( valueTwo.getValue() ).thenReturn( 3 );

		assertThat( property.getOriginalValue() ).isEqualTo( ORIGINAL_VALUE );
		assertThat( property.getValue() ).isEqualTo( values );
	}

	@Test
	public void itemCanBeAddedByKeyAndInitialKeyIsTheEntryKey() {
		assertThat( property.getValue() ).isEqualTo( ORIGINAL_VALUE );

		val key = mock( EntityPropertyBinder.class );
		when( binder.createPropertyBinder( keyDescriptor ) ).thenReturn( key );

		val value = mock( EntityPropertyBinder.class );
		when( binder.createPropertyBinder( valueDescriptor ) ).thenReturn( value );

		property.getEntries().clear();
		property.getEntries().get( "random" ).getKey().setValue( "test" );
		property.getEntries().get( "random" ).getValue().setValue( 33 );

		InOrder inOrder = inOrder( key, value );
		inOrder.verify( key ).setValue( "random" );
		inOrder.verify( key ).setValue( "test" );
		inOrder.verify( value ).setValue( 33 );

		when( key.getValue() ).thenReturn( "test" );
		when( value.getValue() ).thenReturn( 33 );

		assertThat( property.getValue() ).isEqualTo( Collections.singletonMap( "test", 33 ) );
	}

	@Test
	public void valueIsInitializedWithOriginalValueWithIncrementalBinding() {
		property.enableBinding( true );
		property.setUpdateItemsOnBinding( true );

		assertThat( property.getEntries() ).hasSize( 1 );
		assertThat( property.getValue() ).isEqualTo( ORIGINAL_VALUE );
	}

	@Test
	public void deletedItemsArePresentInTheItemsButNotInTheValueWhileBindingIsBusy() {
		property.getEntries().get( "0" ).setDeleted( true );

		assertThat( property.getEntries() ).hasSize( 1 );
		assertThat( property.getValue() ).isEqualTo( Collections.emptyMap() );
	}

	@Test
	public void deletedItemsAreRemovedWhenBindingIsDisabled() {
		property.getEntries().get( "0" ).setDeleted( true );
		assertThat( property.getEntries() ).hasSize( 1 );
		property.enableBinding( false );

		assertThat( property.getEntries() ).isEmpty();
		assertThat( property.getValue() ).isEqualTo( Collections.emptyMap() );
	}

	@Test
	public void valueSortsTheItems() {
		val keyOne = mock( EntityPropertyBinder.class );
		val keyTwo = mock( EntityPropertyBinder.class );
		when( binder.createPropertyBinder( keyDescriptor ) ).thenReturn( keyOne ).thenReturn( keyTwo );

		val valueOne = mock( EntityPropertyBinder.class );
		val valueTwo = mock( EntityPropertyBinder.class );
		when( binder.createPropertyBinder( valueDescriptor ) ).thenReturn( valueOne ).thenReturn( valueTwo );

		Map<String, Integer> values = new LinkedHashMap<>();
		values.put( "two", 2 );
		values.put( "three", 3 );
		property.setValue( values );

		when( keyOne.getValue() ).thenReturn( "two" );
		when( valueOne.getValue() ).thenReturn( 2 );
		when( keyTwo.getValue() ).thenReturn( "three" );
		when( valueTwo.getValue() ).thenReturn( 3 );

		property.getEntries().get( "0" ).setSortIndex( -1 );
		property.getEntries().get( "1" ).setSortIndex( 1 );
		assertThat( ( (Map) property.getValue() ).values() ).containsExactly( 2, 3 );

		property.getEntries().get( "0" ).setSortIndex( 2 );
		assertThat( ( (Map) property.getValue() ).values() ).containsExactly( 3, 2 );
	}

	@Test
	public void itemsGetRecreatedIfValueHasChanged() {
		assertThat( property.getEntries() ).hasSize( 1 );

		val item = property.getEntries().get( "0" );

		property.setValue( Collections.singletonMap( "two", 2 ) );
		assertThat( property.getEntries().get( "0" ) ).isNotSameAs( item );
		assertThat( property.getEntries() ).hasSize( 1 );

		property.setValue( null );
		assertThat( property.getEntries() ).isEmpty();
	}

	@Test
	public void updatingTheValueDoesNotUseTheController() {
		property.setValue( Collections.singletonMap( "two", 2 ) );
		verify( collectionController ).fetchValue( BINDING_CONTEXT );
		verifyZeroInteractions( collectionController );
	}

	@Test
	public void modifiedIfTheNewValueCollectionIsDifferentFromTheOriginal() {
		assertThat( property.isModified() ).isFalse();

		assertThat( property.getOriginalValue() ).isEqualTo( ORIGINAL_VALUE );
		assertThat( property.getValue() ).isEqualTo( ORIGINAL_VALUE );
		assertThat( property.isModified() ).isFalse();

		when( binder.convertIfNecessary( any(), eq( COLLECTION ), eq( "[collection].entries" ) ) ).thenReturn( Collections.emptyMap() );
		assertThat( property.isModified() ).isTrue();
	}

	@Test
	public void boundButNotSetValueModifies() {
		property.enableBinding( true );
		property.setBound( true );
		assertThat( property.isModified() ).isTrue();

		property.enableBinding( false );
		assertThat( property.isModified() ).isTrue();
		assertThat( property.getValue() ).isEqualTo( Collections.emptyMap() );
	}

	@Test
	public void boundButItemsFetchedDoesNotModify() {
		property.setBound( true );
		assertThat( property.getEntries() ).hasSize( 1 );
		assertThat( property.getValue() ).isEqualTo( ORIGINAL_VALUE );
		assertThat( property.isModified() ).isFalse();
	}

	@Test
	public void boundDuringBindingButValueNotSetIsEmptyButNotDeleted() {
		property.enableBinding( true );
		property.setBound( true );
		assertThat( property.isDeleted() ).isFalse();
		assertThat( property.getValue() ).isEqualTo( Collections.emptyMap() );
		assertThat( property.getEntries() ).isEmpty();
	}

	@Test
	public void boundDuringBindingButUpdatingDoesNotClearItems() {
		property.enableBinding( true );
		property.setBound( true );
		property.setUpdateItemsOnBinding( true );
		assertThat( property.isDeleted() ).isFalse();
		assertThat( property.getValue() ).isEqualTo( ORIGINAL_VALUE );
		assertThat( property.getEntries() ).hasSize( 1 );
	}

	@Test
	public void explicitlyDeletedDoesNotClearTheItemsImmediately() {
		property.setDeleted( true );
		assertThat( property.isDeleted() ).isTrue();
		assertThat( property.getValue() ).isEqualTo( Collections.emptyMap() );
		assertThat( property.getEntries() ).hasSize( 1 );
	}

	@Test
	public void itemsAreClearedOnDeletedPropertyWhenBindingDisabled() {
		property.setDeleted( true );
		property.enableBinding( false );
		assertThat( property.isDeleted() ).isTrue();
		assertThat( property.getValue() ).isEqualTo( Collections.emptyMap() );
		assertThat( property.getEntries() ).isEmpty();
	}

	@Test
	public void boundButValueAssignedIsNotDeleted() {
		property.enableBinding( true );
		property.setBound( true );
		property.setValue( ORIGINAL_VALUE );
		assertThat( property.isDeleted() ).isFalse();
		assertThat( property.isModified() ).isFalse();
		assertThat( property.getEntries() ).hasSize( 1 );
	}

	@Test
	public void settingNullValueIsNotSameAsDeleted() {
		property.setValue( null );
		assertThat( property.isDeleted() ).isFalse();
	}

	@Test
	public void settingEmptyCollectionValueIsNotSameAsDeleted() {
		property.setValue( Collections.emptyMap() );
		assertThat( property.isDeleted() ).isFalse();
	}

	@Test
	public void createNewValueCreatesNewValueButDoesNotUpdatePropertyItself() {
		when( binder.createValue( collectionController, COLLECTION ) ).thenReturn( Collections.singletonMap( "two", 2 ) );
		Object value = property.createNewValue();
		assertThat( value ).isEqualTo( Collections.singletonMap( "two", 2 ) );

		assertThat( property.getValue() ).isEqualTo( ORIGINAL_VALUE );
	}

	@Test
	public void getInitializedValueDoesNotModifyCurrentValue() {
		assertThat( property.getInitializedValue() ).isEqualTo( ORIGINAL_VALUE );
		verify( binder, never() ).createValue( any(), any() );
	}

	@Test
	public void getInitializedValueInitializesNewValueIfNullFetched() {
		reset( collectionController );
		when( binder.createValue( collectionController, COLLECTION ) ).thenReturn( Collections.singletonMap( "two", 2 ) );

		when( key.getValue() ).thenReturn( "two" );
		when( value.getValue() ).thenReturn( 2 );
		assertThat( property.getInitializedValue() ).isEqualTo( Collections.singletonMap( "two", 2 ) );
		verify( binder ).createValue( collectionController, COLLECTION );
		verify( key ).setValue( "two" );
		verify( value ).setValue( 2 );

		assertThat( property.getValue() ).isEqualTo( Collections.singletonMap( "two", 2 ) );
		assertThat( property.getOriginalValue() ).isNull();
	}

	@Test
	public void applyValueFlushesToTheControllerWithTheOriginalValue() {
		when( collectionController.applyValue( BINDING_CONTEXT, new EntityPropertyValue<>( ORIGINAL_VALUE, ORIGINAL_VALUE, false ) ) ).thenReturn( true );
		assertThat( property.applyValue() ).isTrue();

		when( value.getValue() ).thenReturn( 3 );
		assertThat( property.applyValue() ).isFalse();
		verify( collectionController ).applyValue( BINDING_CONTEXT, new EntityPropertyValue<>( ORIGINAL_VALUE, Collections.singletonMap( "one", 3 ), false ) );
	}

	@Test
	public void applyValueFlagsDelete() {
		when( collectionController.applyValue( BINDING_CONTEXT, new EntityPropertyValue<>( ORIGINAL_VALUE, Collections.emptyMap(), true ) ) )
				.thenReturn( true );
		property.setDeleted( true );
		assertThat( property.applyValue() ).isTrue();
		verify( collectionController ).applyValue( BINDING_CONTEXT, new EntityPropertyValue<>( ORIGINAL_VALUE, Collections.emptyMap(), true ) );
	}

	@Test
	public void saveFlushesToTheControllerWithTheOriginalValue() {
		when( collectionController.save( BINDING_CONTEXT, new EntityPropertyValue<>( ORIGINAL_VALUE, ORIGINAL_VALUE, false ) ) ).thenReturn( true );
		assertThat( property.save() ).isTrue();

		when( value.getValue() ).thenReturn( 3 );
		assertThat( property.save() ).isFalse();
		verify( collectionController ).save( BINDING_CONTEXT, new EntityPropertyValue<>( ORIGINAL_VALUE, Collections.singletonMap( "one", 3 ), false ) );
	}

	@Test
	public void saveWithNullIfDeleted() {
		when( collectionController.save( BINDING_CONTEXT, new EntityPropertyValue<>( ORIGINAL_VALUE, Collections.emptyMap(), true ) ) )
				.thenReturn( true );
		property.setDeleted( true );
		assertThat( property.save() ).isTrue();
		verify( collectionController ).save( BINDING_CONTEXT, new EntityPropertyValue<>( ORIGINAL_VALUE, Collections.emptyMap(), true ) );
	}

	@Test
	public void validate() {
		Errors errors = new BeanPropertyBindingResult( binder, "" );
		errors.pushNestedPath( "properties[prop]" );

		assertThat( property.validate( errors, "hint" ) ).isTrue();
		assertThat( errors.getErrorCount() ).isEqualTo( 0 );

		property.getEntries().get( "0" ).setEntryKey( "do nothing" );

		doAnswer( invocation -> {
			Errors err = invocation.getArgument( 0 );
			err.rejectValue( "value", "bad-key" );

			return null;
		} )
				.when( key )
				.validate( errors, "hint" );

		doAnswer( invocation -> {
			Errors err = invocation.getArgument( 0 );
			err.rejectValue( "value", "bad-value" );

			return null;
		} )
				.when( value )
				.validate( errors, "hint" );

		doAnswer( invocation -> {
			Errors err = invocation.getArgument( 2 );
			err.rejectValue( "", "bad" );

			return null;
		} )
				.when( collectionController )
				.validate( any(), any(), eq( errors ), eq( "hint" ) );

		assertThat( property.validate( errors, "hint" ) ).isFalse();

		errors.popNestedPath();

		assertThat( errors.getErrorCount() ).isEqualTo( 3 );
		assertThat( errors.getFieldError( "properties[prop].entries[0].key.value" ) )
				.isNotNull()
				.satisfies( fe -> {
					assertThat( fe.isBindingFailure() ).isFalse();
					assertThat( fe.getField() ).isEqualTo( "properties[prop].entries[0].key.value" );
					assertThat( fe.getCode() ).isEqualTo( "bad-key" );
				} );
		assertThat( errors.getFieldError( "properties[prop].entries[0].value.value" ) )
				.isNotNull()
				.satisfies( fe -> {
					assertThat( fe.isBindingFailure() ).isFalse();
					assertThat( fe.getField() ).isEqualTo( "properties[prop].entries[0].value.value" );
					assertThat( fe.getCode() ).isEqualTo( "bad-value" );
				} );
		assertThat( errors.getFieldError( "properties[prop].value" ) )
				.isNotNull()
				.satisfies( fe -> {
					assertThat( fe.isBindingFailure() ).isFalse();
					assertThat( fe.getField() ).isEqualTo( "properties[prop].value" );
					assertThat( fe.getCode() ).isEqualTo( "bad" );
				} );
	}
}
