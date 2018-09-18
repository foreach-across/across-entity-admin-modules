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

import lombok.RequiredArgsConstructor;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arne Vandamme
 * @since 3.2.0
 */
public class TestEntityPropertyControlName
{
	@Test
	public void simplePropertyByName() {
		EntityPropertyControlName.ForPath controlName = EntityPropertyControlName.create( "address" );
		controlName( controlName ).shouldBe( "address" );
		controlName( controlName.asDirectProperty() ).shouldBe( "address" );
		controlName( controlName.asBinderItem() ).shouldBe( "properties[address]" );
		controlName( controlName.asBinderItem().asValue() ).shouldBe( "properties[address].value" );
		controlName( controlName.asBinderItem().asInitializedValue() ).shouldBe( "properties[address].initializedValue" );
	}

	@Test
	public void childPropertyByName() {
		EntityPropertyControlName.ForPath controlName = EntityPropertyControlName.create( "address" );
		EntityPropertyControlName.ForPath child = controlName.asChildProperty( "street" );
		controlName( child ).shouldBe( "address.street" );
		controlName( child.asDirectProperty() ).shouldBe( "address.street" );
		controlName( child.asBinderItem() ).shouldBe( "properties[address].properties[street]" );
		controlName( child.asBinderItem().asValue() ).shouldBe( "properties[address].properties[street].value" );
		controlName( child.asBinderItem().asInitializedValue() ).shouldBe( "properties[address].properties[street].initializedValue" );
	}

	@Test
	public void childFromDirectProperty() {
		EntityPropertyControlName address = EntityPropertyControlName.create( "address" ).asDirectProperty();
		EntityPropertyControlName.ForPath child = address.asChildProperty( "street" );
		controlName( child ).shouldBe( "address.street" );
		controlName( child.asDirectProperty() ).shouldBe( "address.street" );
		controlName( child.asBinderItem() ).shouldBe( "properties[address].properties[street]" );
		controlName( child.asBinderItem().asValue() ).shouldBe( "properties[address].properties[street].value" );
		controlName( child.asBinderItem().asInitializedValue() ).shouldBe( "properties[address].properties[street].initializedValue" );
	}

	@Test
	public void childFromBinderProperty() {
		EntityPropertyControlName address = EntityPropertyControlName.create( "address" ).asBinderItem();
		EntityPropertyControlName.ForPath child = address.asChildProperty( "street" );

		controlName( child ).shouldBe( "properties[address].value.street" );
		controlName( child.asDirectProperty() ).shouldBe( "properties[address].value.street" );
		controlName( child.asBinderItem() ).shouldBe( "properties[address].properties[street]" );
		controlName( child.asBinderItem().asValue() ).shouldBe( "properties[address].properties[street].value" );
		controlName( child.asBinderItem().asInitializedValue() ).shouldBe( "properties[address].properties[street].initializedValue" );
	}

	@Test
	public void childFromBinderPropertyWithInitializedValue() {
		EntityPropertyControlName address = EntityPropertyControlName.create( "address" ).asBinderItem().asInitializedValue();
		EntityPropertyControlName.ForPath child = address.asChildProperty( "street" );

		controlName( child ).shouldBe( "properties[address].initializedValue.street" );
		controlName( child.asDirectProperty() ).shouldBe( "properties[address].initializedValue.street" );
		controlName( child.asBinderItem() ).shouldBe( "properties[address].properties[street]" );
		controlName( child.asBinderItem().asValue() ).shouldBe( "properties[address].properties[street].value" );
		controlName( child.asBinderItem().asInitializedValue() ).shouldBe( "properties[address].properties[street].initializedValue" );
	}

	@Test
	public void emptyIndexerProperty() {
		EntityPropertyControlName.ForPath address = EntityPropertyControlName.create( "address[]" ).asCollectionItem();
		controlName( address ).shouldBe( "address[]" );
		controlName( address.asDirectProperty() ).shouldBe( "address[]" );
		controlName( address.asBinderItem() ).shouldBe( "properties[address].items[]" );
		controlName( address.asBinderItem().asValue() ).shouldBe( "properties[address].items[].value" );
		controlName( address.asBinderItem().asInitializedValue() ).shouldBe( "properties[address].items[].initializedValue" );
	}

	@Test
	public void indexerProperty() {
		EntityPropertyControlName.ForPath address = EntityPropertyControlName.create( "address[]" ).asCollectionItem().withIndex( 10 );
		controlName( address ).shouldBe( "address[10]" );
		controlName( address.asDirectProperty() ).shouldBe( "address[10]" );
		controlName( address.asBinderItem() ).shouldBe( "properties[address].items[10]" );
		controlName( address.asBinderItem().asValue() ).shouldBe( "properties[address].items[10].value" );
		controlName( address.asBinderItem().asInitializedValue() ).shouldBe( "properties[address].items[10].initializedValue" );

		address = EntityPropertyControlName.create( "address[]" ).asCollectionItem().withIndex( 5 ).withBinderItemKey( "abc" );
		controlName( address ).shouldBe( "address[5]" );
		controlName( address.asDirectProperty() ).shouldBe( "address[5]" );
		controlName( address.asBinderItem() ).shouldBe( "properties[address].items[abc]" );
		controlName( address.asBinderItem().asValue() ).shouldBe( "properties[address].items[abc].value" );
		controlName( address.asBinderItem().asInitializedValue() ).shouldBe( "properties[address].items[abc].initializedValue" );
	}

	@Test
	public void childOfIndexerProperty() {
		EntityPropertyControlName.ForPath address = EntityPropertyControlName.create( "address[]" ).asCollectionItem().withIndex( 10 ).withBinderItemKey(
				"abc" );
		EntityPropertyControlName.ForPath child = address.asChildProperty( "street" );
		controlName( child ).shouldBe( "address[10].street" );
		controlName( child.asDirectProperty() ).shouldBe( "address[10].street" );
		controlName( child.asBinderItem() ).shouldBe( "properties[address].items[abc].properties[street]" );
		controlName( child.asBinderItem().asValue() ).shouldBe( "properties[address].items[abc].properties[street].value" );
		controlName( child.asBinderItem().asInitializedValue() ).shouldBe( "properties[address].items[abc].properties[street].initializedValue" );

	}

	@Test
	public void childOfIndexerPropertyAsBinderProperty() {
		EntityPropertyControlName address = EntityPropertyControlName.create( "address" )
		                                                             .asCollectionItem()
		                                                             .withIndex( 10 )
		                                                             .withBinderItemKey( "abc" )
		                                                             .asBinderItem();
		EntityPropertyControlName.ForPath child = address.asChildProperty( "street" );

		controlName( child ).shouldBe( "properties[address].items[abc].value.street" );
		controlName( child.asDirectProperty() ).shouldBe( "properties[address].items[abc].value.street" );
		controlName( child.asBinderItem() ).shouldBe( "properties[address].items[abc].properties[street]" );
		controlName( child.asBinderItem().asValue() ).shouldBe( "properties[address].items[abc].properties[street].value" );
		controlName( child.asBinderItem().asInitializedValue() ).shouldBe( "properties[address].items[abc].properties[street].initializedValue" );
	}

	@Test
	public void childOfIndexerPropertyAsBinderPropertyWithInitializedValue() {
		EntityPropertyControlName address = EntityPropertyControlName.create( "address" )
		                                                             .asCollectionItem()
		                                                             .withIndex( 10 )
		                                                             .withBinderItemKey( "abc" )
		                                                             .asBinderItem()
		                                                             .asInitializedValue();
		EntityPropertyControlName.ForPath child = address.asChildProperty( "street" );

		controlName( child ).shouldBe( "properties[address].items[abc].initializedValue.street" );
		controlName( child.asDirectProperty() ).shouldBe( "properties[address].items[abc].initializedValue.street" );
		controlName( child.asBinderItem() ).shouldBe( "properties[address].items[abc].properties[street]" );
		controlName( child.asBinderItem().asValue() ).shouldBe( "properties[address].items[abc].properties[street].value" );
		controlName( child.asBinderItem().asInitializedValue() ).shouldBe( "properties[address].items[abc].properties[street].initializedValue" );
	}

	@Test
	public void emptyMapEntryProperty() {
		EntityPropertyControlName.ForPath.MapEntry address = EntityPropertyControlName.create( "address" ).asMapEntry();
		controlName( address ).shouldBe( "address[]" );
		controlName( address.asDirectProperty() ).shouldBe( "address[]" );
		controlName( address.asBinderEntryKey() ).shouldBe( "properties[address].entries[].key" );
		controlName( address.asBinderEntryKey().asValue() ).shouldBe( "properties[address].entries[].key.value" );
		controlName( address.asBinderEntryKey().asInitializedValue() ).shouldBe( "properties[address].entries[].key.initializedValue" );
		controlName( address.asBinderEntryValue() ).shouldBe( "properties[address].entries[].value" );
		controlName( address.asBinderEntryValue().asValue() ).shouldBe( "properties[address].entries[].value.value" );
		controlName( address.asBinderEntryValue().asInitializedValue() ).shouldBe( "properties[address].entries[].value.initializedValue" );
	}

	@Test
	public void mapEntryProperty() {
		EntityPropertyControlName.ForPath.MapEntry address = EntityPropertyControlName.create( "address" )
		                                                                              .asMapEntry()
		                                                                              .withMapKey( "abc" )
		                                                                              .withBinderEntryKey( "def" );

		controlName( address ).shouldBe( "address[abc]" );
		controlName( address.asDirectProperty() ).shouldBe( "address[abc]" );
		controlName( address.asBinderEntryKey() ).shouldBe( "properties[address].entries[def].key" );
		controlName( address.asBinderEntryKey().asValue() ).shouldBe( "properties[address].entries[def].key.value" );
		controlName( address.asBinderEntryKey().asInitializedValue() ).shouldBe( "properties[address].entries[def].key.initializedValue" );
		controlName( address.asBinderEntryValue() ).shouldBe( "properties[address].entries[def].value" );
		controlName( address.asBinderEntryValue().asValue() ).shouldBe( "properties[address].entries[def].value.value" );
		controlName( address.asBinderEntryValue().asInitializedValue() ).shouldBe( "properties[address].entries[def].value.initializedValue" );
	}

	@Test
	public void childOfMapEntryPropertyDefaultsToValue() {
		EntityPropertyControlName.ForPath.MapEntry address = EntityPropertyControlName.create( "address" )
		                                                                              .asMapEntry()
		                                                                              .withMapKey( "abc" )
		                                                                              .withBinderEntryKey( "def" );
		EntityPropertyControlName.ForPath child = address.asChildProperty( "street" );
		controlName( child ).shouldBe( "address[abc].street" );
		controlName( child.asDirectProperty() ).shouldBe( "address[abc].street" );
		controlName( child.asBinderItem() ).shouldBe( "properties[address].entries[def].value.properties[street]" );
		controlName( child.asBinderItem().asValue() ).shouldBe( "properties[address].entries[def].value.properties[street].value" );
		controlName( child.asBinderItem().asInitializedValue() ).shouldBe( "properties[address].entries[def].value.properties[street].initializedValue" );

		child = address.asBinderEntryValue().asChildProperty( "street" );
		controlName( child ).shouldBe( "properties[address].entries[def].value.value.street" );
		controlName( child.asDirectProperty() ).shouldBe( "properties[address].entries[def].value.value.street" );
		controlName( child.asBinderItem() ).shouldBe( "properties[address].entries[def].value.properties[street]" );
		controlName( child.asBinderItem().asValue() ).shouldBe( "properties[address].entries[def].value.properties[street].value" );
		controlName( child.asBinderItem().asInitializedValue() ).shouldBe( "properties[address].entries[def].value.properties[street].initializedValue" );

		child = address.asBinderEntryKey().asChildProperty( "street" );
		controlName( child ).shouldBe( "properties[address].entries[def].key.value.street" );
		controlName( child.asDirectProperty() ).shouldBe( "properties[address].entries[def].key.value.street" );
		controlName( child.asBinderItem() ).shouldBe( "properties[address].entries[def].key.properties[street]" );
		controlName( child.asBinderItem().asValue() ).shouldBe( "properties[address].entries[def].key.properties[street].value" );
		controlName( child.asBinderItem().asInitializedValue() ).shouldBe( "properties[address].entries[def].key.properties[street].initializedValue" );
	}

	@Test
	public void childOfMapEntryPropertyAsBinderProperty() {
		EntityPropertyControlName address = EntityPropertyControlName.create( "address" )
		                                                             .asMapEntry()
		                                                             .withMapKey( "abc" )
		                                                             .withBinderEntryKey( "def" )
		                                                             .asBinderItem();
		EntityPropertyControlName.ForPath child = address.asChildProperty( "street" );
		controlName( child ).shouldBe( "properties[address].entries[def].value.value.street" );
		controlName( child.asDirectProperty() ).shouldBe( "properties[address].entries[def].value.value.street" );
		controlName( child.asBinderItem() ).shouldBe( "properties[address].entries[def].value.properties[street]" );
		controlName( child.asBinderItem().asValue() ).shouldBe( "properties[address].entries[def].value.properties[street].value" );
		controlName( child.asBinderItem().asInitializedValue() ).shouldBe( "properties[address].entries[def].value.properties[street].initializedValue" );

		address = EntityPropertyControlName.create( "address" )
		                                   .asMapEntry()
		                                   .withMapKey( "abc" )
		                                   .withBinderEntryKey( "def" )
		                                   .asBinderEntryValue();
		child = address.asChildProperty( "street" );
		controlName( child ).shouldBe( "properties[address].entries[def].value.value.street" );
		controlName( child.asDirectProperty() ).shouldBe( "properties[address].entries[def].value.value.street" );
		controlName( child.asBinderItem() ).shouldBe( "properties[address].entries[def].value.properties[street]" );
		controlName( child.asBinderItem().asValue() ).shouldBe( "properties[address].entries[def].value.properties[street].value" );
		controlName( child.asBinderItem().asInitializedValue() ).shouldBe( "properties[address].entries[def].value.properties[street].initializedValue" );

		address = EntityPropertyControlName.create( "address" )
		                                   .asMapEntry()
		                                   .withMapKey( "abc" )
		                                   .withBinderEntryKey( "def" )
		                                   .asBinderEntryKey();
		child = address.asChildProperty( "street" );
		controlName( child ).shouldBe( "properties[address].entries[def].key.value.street" );
		controlName( child.asDirectProperty() ).shouldBe( "properties[address].entries[def].key.value.street" );
		controlName( child.asBinderItem() ).shouldBe( "properties[address].entries[def].key.properties[street]" );
		controlName( child.asBinderItem().asValue() ).shouldBe( "properties[address].entries[def].key.properties[street].value" );
		controlName( child.asBinderItem().asInitializedValue() ).shouldBe( "properties[address].entries[def].key.properties[street].initializedValue" );
	}

	@Test
	public void childOfMapEntryPropertyAsBinderPropertyWithInitializedValue() {
		EntityPropertyControlName address = EntityPropertyControlName.create( "address" )
		                                                             .asMapEntry()
		                                                             .withMapKey( "abc" )
		                                                             .withBinderEntryKey( "def" )
		                                                             .asBinderItem()
		                                                             .asInitializedValue();
		EntityPropertyControlName.ForPath child = address.asChildProperty( "street" );
		controlName( child ).shouldBe( "properties[address].entries[def].value.initializedValue.street" );
		controlName( child.asDirectProperty() ).shouldBe( "properties[address].entries[def].value.initializedValue.street" );
		controlName( child.asBinderItem() ).shouldBe( "properties[address].entries[def].value.properties[street]" );
		controlName( child.asBinderItem().asValue() ).shouldBe( "properties[address].entries[def].value.properties[street].value" );
		controlName( child.asBinderItem().asInitializedValue() ).shouldBe( "properties[address].entries[def].value.properties[street].initializedValue" );

		address = EntityPropertyControlName.create( "address" )
		                                   .asMapEntry()
		                                   .withMapKey( "abc" )
		                                   .withBinderEntryKey( "def" )
		                                   .asBinderEntryValue()
		                                   .asInitializedValue();
		child = address.asChildProperty( "street" );
		controlName( child ).shouldBe( "properties[address].entries[def].value.initializedValue.street" );
		controlName( child.asDirectProperty() ).shouldBe( "properties[address].entries[def].value.initializedValue.street" );
		controlName( child.asBinderItem() ).shouldBe( "properties[address].entries[def].value.properties[street]" );
		controlName( child.asBinderItem().asValue() ).shouldBe( "properties[address].entries[def].value.properties[street].value" );
		controlName( child.asBinderItem().asInitializedValue() ).shouldBe( "properties[address].entries[def].value.properties[street].initializedValue" );

		address = EntityPropertyControlName.create( "address" )
		                                   .asMapEntry()
		                                   .withMapKey( "abc" )
		                                   .withBinderEntryKey( "def" )
		                                   .asBinderEntryKey()
		                                   .asInitializedValue();
		child = address.asChildProperty( "street" );
		controlName( child ).shouldBe( "properties[address].entries[def].key.initializedValue.street" );
		controlName( child.asDirectProperty() ).shouldBe( "properties[address].entries[def].key.initializedValue.street" );
		controlName( child.asBinderItem() ).shouldBe( "properties[address].entries[def].key.properties[street]" );
		controlName( child.asBinderItem().asValue() ).shouldBe( "properties[address].entries[def].key.properties[street].value" );
		controlName( child.asBinderItem().asInitializedValue() ).shouldBe( "properties[address].entries[def].key.properties[street].initializedValue" );
	}

	private ControlNameTest controlName( Object controlName ) {
		return new ControlNameTest( controlName );
	}

	@RequiredArgsConstructor
	private class ControlNameTest
	{
		private final Object controlName;

		void shouldBe( String expected ) {
			assertThat( controlName.toString() ).isEqualTo( expected );
		}
	}
}
