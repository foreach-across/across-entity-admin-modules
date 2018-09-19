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

import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyHandlingType;
import com.foreach.across.modules.entity.registry.properties.SimpleEntityPropertyDescriptor;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
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
		EntityPropertyControlName.ForProperty controlName = EntityPropertyControlName.forProperty( "address" );
		controlName( controlName ).is( "address" );
		controlName( controlName.asDirectProperty() ).is( "address" );
		controlName( controlName.asBinderItem() ).is( "properties[address].value" );
		controlName( controlName.asBinderItem().withValue() ).is( "properties[address].value" );
		controlName( controlName.asBinderItem().withInitializedValue() ).is( "properties[address].initializedValue" );
	}

	@Test
	public void childPropertyByName() {
		EntityPropertyControlName.ForProperty controlName = EntityPropertyControlName.forProperty( "address" );
		EntityPropertyControlName.ForProperty child = controlName.asChildProperty( "street" );
		controlName( child ).is( "address.street" );
		controlName( child.asDirectProperty() ).is( "address.street" );
		controlName( child.asBinderItem() ).is( "properties[address].properties[street].value" );
		controlName( child.asBinderItem().withValue() ).is( "properties[address].properties[street].value" );
		controlName( child.asBinderItem().withInitializedValue() ).is( "properties[address].properties[street].initializedValue" );
	}

	@Test
	public void childFromDirectProperty() {
		EntityPropertyControlName address = EntityPropertyControlName.forProperty( "address" ).asDirectProperty();
		EntityPropertyControlName.ForProperty child = address.asChildProperty( "street" );
		controlName( child ).is( "address.street" );
		controlName( child.asDirectProperty() ).is( "address.street" );
		controlName( child.asBinderItem() ).is( "properties[address].properties[street].value" );
		controlName( child.asBinderItem().withValue() ).is( "properties[address].properties[street].value" );
		controlName( child.asBinderItem().withInitializedValue() ).is( "properties[address].properties[street].initializedValue" );
	}

	@Test
	public void childFromBinderProperty() {
		EntityPropertyControlName address = EntityPropertyControlName.forProperty( "address" ).asBinderItem();
		EntityPropertyControlName.ForProperty child = address.asChildProperty( "street" );

		controlName( child ).is( "properties[address].initializedValue.street" );
		controlName( child.asDirectProperty() ).is( "properties[address].initializedValue.street" );
		controlName( child.asBinderItem() ).is( "properties[address].properties[street].value" );
		controlName( child.asBinderItem().withValue() ).is( "properties[address].properties[street].value" );
		controlName( child.asBinderItem().withInitializedValue() ).is( "properties[address].properties[street].initializedValue" );
	}

	@Test
	public void childFromBinderPropertyWithInitializedValue() {
		EntityPropertyControlName address = EntityPropertyControlName.forProperty( "address" ).asBinderItem().withInitializedValue();
		EntityPropertyControlName.ForProperty child = address.asChildProperty( "street" );

		controlName( child ).is( "properties[address].initializedValue.street" );
		controlName( child.asDirectProperty() ).is( "properties[address].initializedValue.street" );
		controlName( child.asBinderItem() ).is( "properties[address].properties[street].value" );
		controlName( child.asBinderItem().withValue() ).is( "properties[address].properties[street].value" );
		controlName( child.asBinderItem().withInitializedValue() ).is( "properties[address].properties[street].initializedValue" );
	}

	@Test
	public void emptyIndexerProperty() {
		EntityPropertyControlName.ForProperty address = EntityPropertyControlName.forProperty( "address[]" ).asCollectionItem();
		controlName( address ).is( "address[]" );
		controlName( address.asDirectProperty() ).is( "address[]" );
		controlName( address.asBinderItem() ).is( "properties[address].items[].value" );
		controlName( address.asBinderItem().withValue() ).is( "properties[address].items[].value" );
		controlName( address.asBinderItem().withInitializedValue() ).is( "properties[address].items[].initializedValue" );
	}

	@Test
	public void indexerProperty() {
		EntityPropertyControlName.ForProperty address = EntityPropertyControlName.forProperty( "address[]" ).asCollectionItem().withIndex( 10 );
		controlName( address ).is( "address[10]" );
		controlName( address.asDirectProperty() ).is( "address[10]" );
		controlName( address.asBinderItem() ).is( "properties[address].items[10].value" );
		controlName( address.asBinderItem().withValue() ).is( "properties[address].items[10].value" );
		controlName( address.asBinderItem().withInitializedValue() ).is( "properties[address].items[10].initializedValue" );

		address = EntityPropertyControlName.forProperty( "address[]" ).asCollectionItem().withIndex( 5 ).withBinderItemKey( "abc" );
		controlName( address ).is( "address[5]" );
		controlName( address.asDirectProperty() ).is( "address[5]" );
		controlName( address.asBinderItem() ).is( "properties[address].items[abc].value" );
		controlName( address.asBinderItem().withValue() ).is( "properties[address].items[abc].value" );
		controlName( address.asBinderItem().withInitializedValue() ).is( "properties[address].items[abc].initializedValue" );
	}

	@Test
	public void childOfIndexerProperty() {
		EntityPropertyControlName.ForProperty address = EntityPropertyControlName.forProperty( "address[]" ).asCollectionItem().withIndex( 10 )
		                                                                         .withBinderItemKey(
				                                                                         "abc" );
		EntityPropertyControlName.ForProperty child = address.asChildProperty( "street" );
		controlName( child ).is( "address[10].street" );
		controlName( child.asDirectProperty() ).is( "address[10].street" );
		controlName( child.asBinderItem() ).is( "properties[address].items[abc].properties[street].value" );
		controlName( child.asBinderItem().withValue() ).is( "properties[address].items[abc].properties[street].value" );
		controlName( child.asBinderItem().withInitializedValue() ).is( "properties[address].items[abc].properties[street].initializedValue" );

	}

	@Test
	public void childOfIndexerPropertyAsBinderProperty() {
		EntityPropertyControlName address = EntityPropertyControlName.forProperty( "address" )
		                                                             .asCollectionItem()
		                                                             .withIndex( 10 )
		                                                             .withBinderItemKey( "abc" )
		                                                             .asBinderItem();
		EntityPropertyControlName.ForProperty child = address.asChildProperty( "street" );

		controlName( child ).is( "properties[address].items[abc].initializedValue.street" );
		controlName( child.asDirectProperty() ).is( "properties[address].items[abc].initializedValue.street" );
		controlName( child.asBinderItem() ).is( "properties[address].items[abc].properties[street].value" );
		controlName( child.asBinderItem().withValue() ).is( "properties[address].items[abc].properties[street].value" );
		controlName( child.asBinderItem().withInitializedValue() ).is( "properties[address].items[abc].properties[street].initializedValue" );
	}

	@Test
	public void childOfIndexerPropertyAsBinderPropertyWithInitializedValue() {
		EntityPropertyControlName address = EntityPropertyControlName.forProperty( "address" )
		                                                             .asCollectionItem()
		                                                             .withIndex( 10 )
		                                                             .withBinderItemKey( "abc" )
		                                                             .asBinderItem()
		                                                             .withInitializedValue();
		EntityPropertyControlName.ForProperty child = address.asChildProperty( "street" );

		controlName( child ).is( "properties[address].items[abc].initializedValue.street" );
		controlName( child.asDirectProperty() ).is( "properties[address].items[abc].initializedValue.street" );
		controlName( child.asBinderItem() ).is( "properties[address].items[abc].properties[street].value" );
		controlName( child.asBinderItem().withValue() ).is( "properties[address].items[abc].properties[street].value" );
		controlName( child.asBinderItem().withInitializedValue() ).is( "properties[address].items[abc].properties[street].initializedValue" );
	}

	@Test
	public void emptyMapEntryProperty() {
		EntityPropertyControlName.ForProperty.MapEntry address = EntityPropertyControlName.forProperty( "address" ).asMapEntry();
		controlName( address ).is( "address[]" );
		controlName( address.asDirectProperty() ).is( "address[]" );
		controlName( address.asBinderEntryKey() ).is( "properties[address].entries[].key.value" );
		controlName( address.asBinderEntryKey().withValue() ).is( "properties[address].entries[].key.value" );
		controlName( address.asBinderEntryKey().withInitializedValue() ).is( "properties[address].entries[].key.initializedValue" );
		controlName( address.asBinderEntryValue() ).is( "properties[address].entries[].value.value" );
		controlName( address.asBinderEntryValue().withValue() ).is( "properties[address].entries[].value.value" );
		controlName( address.asBinderEntryValue().withInitializedValue() ).is( "properties[address].entries[].value.initializedValue" );
	}

	@Test
	public void mapEntryProperty() {
		EntityPropertyControlName.ForProperty.MapEntry address = EntityPropertyControlName.forProperty( "address" )
		                                                                                  .asMapEntry()
		                                                                                  .withMapKey( "abc" )
		                                                                                  .withBinderEntryKey( "def" );

		controlName( address ).is( "address[abc]" );
		controlName( address.asDirectProperty() ).is( "address[abc]" );
		controlName( address.asBinderEntryKey() ).is( "properties[address].entries[def].key.value" );
		controlName( address.asBinderEntryKey().withValue() ).is( "properties[address].entries[def].key.value" );
		controlName( address.asBinderEntryKey().withInitializedValue() ).is( "properties[address].entries[def].key.initializedValue" );
		controlName( address.asBinderEntryValue() ).is( "properties[address].entries[def].value.value" );
		controlName( address.asBinderEntryValue().withValue() ).is( "properties[address].entries[def].value.value" );
		controlName( address.asBinderEntryValue().withInitializedValue() ).is( "properties[address].entries[def].value.initializedValue" );
	}

	@Test
	public void childOfMapEntryPropertyDefaultsToValue() {
		EntityPropertyControlName.ForProperty.MapEntry address = EntityPropertyControlName.forProperty( "address" )
		                                                                                  .asMapEntry()
		                                                                                  .withMapKey( "abc" )
		                                                                                  .withBinderEntryKey( "def" );
		EntityPropertyControlName.ForProperty child = address.asChildProperty( "street" );
		controlName( child ).is( "address[abc].street" );
		controlName( child.asDirectProperty() ).is( "address[abc].street" );
		controlName( child.asBinderItem() ).is( "properties[address].entries[def].value.properties[street].value" );
		controlName( child.asBinderItem().withValue() ).is( "properties[address].entries[def].value.properties[street].value" );
		controlName( child.asBinderItem().withInitializedValue() ).is( "properties[address].entries[def].value.properties[street].initializedValue" );

		child = address.asBinderEntryValue().asChildProperty( "street" );
		controlName( child ).is( "properties[address].entries[def].value.initializedValue.street" );
		controlName( child.asDirectProperty() ).is( "properties[address].entries[def].value.initializedValue.street" );
		controlName( child.asBinderItem() ).is( "properties[address].entries[def].value.properties[street].value" );
		controlName( child.asBinderItem().withValue() ).is( "properties[address].entries[def].value.properties[street].value" );
		controlName( child.asBinderItem().withInitializedValue() ).is( "properties[address].entries[def].value.properties[street].initializedValue" );

		child = address.asBinderEntryKey().asChildProperty( "street" );
		controlName( child ).is( "properties[address].entries[def].key.initializedValue.street" );
		controlName( child.asDirectProperty() ).is( "properties[address].entries[def].key.initializedValue.street" );
		controlName( child.asBinderItem() ).is( "properties[address].entries[def].key.properties[street].value" );
		controlName( child.asBinderItem().withValue() ).is( "properties[address].entries[def].key.properties[street].value" );
		controlName( child.asBinderItem().withInitializedValue() ).is( "properties[address].entries[def].key.properties[street].initializedValue" );
	}

	@Test
	public void childOfMapEntryPropertyAsBinderProperty() {
		EntityPropertyControlName address = EntityPropertyControlName.forProperty( "address" )
		                                                             .asMapEntry()
		                                                             .withMapKey( "abc" )
		                                                             .withBinderEntryKey( "def" )
		                                                             .asBinderItem();
		EntityPropertyControlName.ForProperty child = address.asChildProperty( "street" );
		controlName( child ).is( "properties[address].entries[def].value.initializedValue.street" );
		controlName( child.asDirectProperty() ).is( "properties[address].entries[def].value.initializedValue.street" );
		controlName( child.asBinderItem() ).is( "properties[address].entries[def].value.properties[street].value" );
		controlName( child.asBinderItem().withValue() ).is( "properties[address].entries[def].value.properties[street].value" );
		controlName( child.asBinderItem().withInitializedValue() ).is( "properties[address].entries[def].value.properties[street].initializedValue" );

		address = EntityPropertyControlName.forProperty( "address" )
		                                   .asMapEntry()
		                                   .withMapKey( "abc" )
		                                   .withBinderEntryKey( "def" )
		                                   .asBinderEntryValue();
		child = address.asChildProperty( "street" );
		controlName( child ).is( "properties[address].entries[def].value.initializedValue.street" );
		controlName( child.asDirectProperty() ).is( "properties[address].entries[def].value.initializedValue.street" );
		controlName( child.asBinderItem() ).is( "properties[address].entries[def].value.properties[street].value" );
		controlName( child.asBinderItem().withValue() ).is( "properties[address].entries[def].value.properties[street].value" );
		controlName( child.asBinderItem().withInitializedValue() ).is( "properties[address].entries[def].value.properties[street].initializedValue" );

		address = EntityPropertyControlName.forProperty( "address" )
		                                   .asMapEntry()
		                                   .withMapKey( "abc" )
		                                   .withBinderEntryKey( "def" )
		                                   .asBinderEntryKey();
		child = address.asChildProperty( "street" );
		controlName( child ).is( "properties[address].entries[def].key.initializedValue.street" );
		controlName( child.asDirectProperty() ).is( "properties[address].entries[def].key.initializedValue.street" );
		controlName( child.asBinderItem() ).is( "properties[address].entries[def].key.properties[street].value" );
		controlName( child.asBinderItem().withValue() ).is( "properties[address].entries[def].key.properties[street].value" );
		controlName( child.asBinderItem().withInitializedValue() ).is( "properties[address].entries[def].key.properties[street].initializedValue" );
	}

	@Test
	public void childOfMapEntryPropertyAsBinderPropertyWithInitializedValue() {
		EntityPropertyControlName address = EntityPropertyControlName.forProperty( "address" )
		                                                             .asMapEntry()
		                                                             .withMapKey( "abc" )
		                                                             .withBinderEntryKey( "def" )
		                                                             .asBinderItem()
		                                                             .withInitializedValue();
		EntityPropertyControlName.ForProperty child = address.asChildProperty( "street" );
		controlName( child ).is( "properties[address].entries[def].value.initializedValue.street" );
		controlName( child.asDirectProperty() ).is( "properties[address].entries[def].value.initializedValue.street" );
		controlName( child.asBinderItem() ).is( "properties[address].entries[def].value.properties[street].value" );
		controlName( child.asBinderItem().withValue() ).is( "properties[address].entries[def].value.properties[street].value" );
		controlName( child.asBinderItem().withInitializedValue() ).is( "properties[address].entries[def].value.properties[street].initializedValue" );

		address = EntityPropertyControlName.forProperty( "address" )
		                                   .asMapEntry()
		                                   .withMapKey( "abc" )
		                                   .withBinderEntryKey( "def" )
		                                   .asBinderEntryValue()
		                                   .withInitializedValue();
		child = address.asChildProperty( "street" );
		controlName( child ).is( "properties[address].entries[def].value.initializedValue.street" );
		controlName( child.asDirectProperty() ).is( "properties[address].entries[def].value.initializedValue.street" );
		controlName( child.asBinderItem() ).is( "properties[address].entries[def].value.properties[street].value" );
		controlName( child.asBinderItem().withValue() ).is( "properties[address].entries[def].value.properties[street].value" );
		controlName( child.asBinderItem().withInitializedValue() ).is( "properties[address].entries[def].value.properties[street].initializedValue" );

		address = EntityPropertyControlName.forProperty( "address" )
		                                   .asMapEntry()
		                                   .withMapKey( "abc" )
		                                   .withBinderEntryKey( "def" )
		                                   .asBinderEntryKey()
		                                   .withInitializedValue();
		child = address.asChildProperty( "street" );
		controlName( child ).is( "properties[address].entries[def].key.initializedValue.street" );
		controlName( child.asDirectProperty() ).is( "properties[address].entries[def].key.initializedValue.street" );
		controlName( child.asBinderItem() ).is( "properties[address].entries[def].key.properties[street].value" );
		controlName( child.asBinderItem().withValue() ).is( "properties[address].entries[def].key.properties[street].value" );
		controlName( child.asBinderItem().withInitializedValue() ).is( "properties[address].entries[def].key.properties[street].initializedValue" );
	}

	@Test
	public void forHandlingType() {
		EntityPropertyControlName.ForProperty user = EntityPropertyControlName.forProperty( "user" );
		EntityPropertyControlName.ForProperty address = user.asChildProperty( "address[]" );

		controlName( user.forHandlingType( EntityPropertyHandlingType.DIRECT ) ).is( "user" );
		controlName( user.forHandlingType( EntityPropertyHandlingType.EXTENSION ) ).is( "properties[user].value" );
		controlName( user.forHandlingType( EntityPropertyHandlingType.MANUAL ) ).is( "user" );

		controlName( address.forHandlingType( EntityPropertyHandlingType.DIRECT ) ).is( "user.address" );
		controlName( address.forHandlingType( EntityPropertyHandlingType.EXTENSION ) ).is( "properties[user].properties[address].value" );
		controlName( address.forHandlingType( EntityPropertyHandlingType.MANUAL ) ).is( "address[]" );
	}

	@Test
	public void generateForDescriptor() {
		SimpleEntityPropertyDescriptor userDescriptor = new SimpleEntityPropertyDescriptor( "user" );
		userDescriptor.setAttribute( EntityPropertyHandlingType.class, EntityPropertyHandlingType.DIRECT );
		SimpleEntityPropertyDescriptor addressDescriptor = new SimpleEntityPropertyDescriptor( "user.address" );

		EntityPropertyControlName.ForProperty user = EntityPropertyControlName.forProperty( userDescriptor, "props" );
		controlName( user ).is( "user" );
		controlName( user.asDirectProperty() ).is( "user" );
		controlName( user.asBinderItem() ).is( "props[user].value" );
		controlName( user.asBinderItem().withValue() ).is( "props[user].value" );
		controlName( user.asBinderItem().withInitializedValue() ).is( "props[user].initializedValue" );

		EntityPropertyControlName.ForProperty address = EntityPropertyControlName.forProperty( addressDescriptor );
		controlName( address ).is( "user.address" );
		controlName( address.asDirectProperty() ).is( "user.address" );
		controlName( address.asBinderItem() ).is( "properties[user.address].value" );
		controlName( address.asBinderItem().withValue() ).is( "properties[user.address].value" );
		controlName( address.asBinderItem().withInitializedValue() ).is( "properties[user.address].initializedValue" );

		// turn it into a nested descriptor
		addressDescriptor.setParentDescriptor( userDescriptor );
		address = EntityPropertyControlName.forProperty( addressDescriptor );
		controlName( address ).is( "user.address" );
		controlName( address.asDirectProperty() ).is( "user.address" );
		controlName( address.asBinderItem() ).is( "properties[user].properties[address].value" );
		controlName( address.asBinderItem().withValue() ).is( "properties[user].properties[address].value" );
		controlName( address.asBinderItem().withInitializedValue() ).is( "properties[user].properties[address].initializedValue" );

		// as child of specified control name
		controlName( user.asBinderItem().withInitializedValue().asChildProperty( addressDescriptor ) )
				.is( "props[user].initializedValue.address" );
	}

	@Test
	public void parentControlNameOnBuilderContextEnsuresAllControlNamesAreMappedAsChildProperty() {
		EntityPropertyDescriptor userDescriptor = new SimpleEntityPropertyDescriptor( "user" );
		SimpleEntityPropertyDescriptor addressDescriptor = new SimpleEntityPropertyDescriptor( "user.address" );
		EntityPropertyControlName.ForProperty user = EntityPropertyControlName.forProperty( userDescriptor, "props" );

		ViewElementBuilderContext bc = new DefaultViewElementBuilderContext();
		EntityPropertyControlName.ForProperty address = EntityPropertyControlName.forProperty( addressDescriptor, bc );
		controlName( address ).is( "user.address" );
		controlName( address.asDirectProperty() ).is( "user.address" );
		controlName( address.asBinderItem() ).is( "properties[user.address].value" );
		controlName( address.asBinderItem().withValue() ).is( "properties[user.address].value" );
		controlName( address.asBinderItem().withInitializedValue() ).is( "properties[user.address].initializedValue" );

		// use the parent control name and make it a nested descriptor
		addressDescriptor.setParentDescriptor( userDescriptor );
		bc.setAttribute( EntityPropertyControlName.class, user.asBinderItem().withInitializedValue() );

		address = EntityPropertyControlName.forProperty( addressDescriptor, bc );
		controlName( address ).is( "props[user].initializedValue.address" );
		controlName( address.asDirectProperty() ).is( "props[user].initializedValue.address" );
		controlName( address.asBinderItem() ).is( "props[user].properties[address].value" );
		controlName( address.asBinderItem().withValue() ).is( "props[user].properties[address].value" );
		controlName( address.asBinderItem().withInitializedValue() ).is( "props[user].properties[address].initializedValue" );
	}

	@Test
	public void generateForDescriptorWithHandlingType() {
		SimpleEntityPropertyDescriptor userDescriptor = new SimpleEntityPropertyDescriptor( "user" );
		userDescriptor.setAttribute( EntityPropertyHandlingType.class, EntityPropertyHandlingType.DIRECT );
		SimpleEntityPropertyDescriptor addressDescriptor = new SimpleEntityPropertyDescriptor( "user.address" );
		addressDescriptor.setParentDescriptor( userDescriptor );

		EntityPropertyControlName.ForProperty address = EntityPropertyControlName.forProperty( addressDescriptor );
		controlName( address ).is( "user.address" );
		controlName( address.asDirectProperty() ).is( "user.address" );
		controlName( address.asBinderItem() ).is( "properties[user].properties[address].value" );
		controlName( address.asBinderItem().withValue() ).is( "properties[user].properties[address].value" );
		controlName( address.asBinderItem().withInitializedValue() ).is( "properties[user].properties[address].initializedValue" );

		userDescriptor.setAttribute( EntityPropertyHandlingType.class, EntityPropertyHandlingType.EXTENSION );
		address = EntityPropertyControlName.forProperty( addressDescriptor );
		controlName( address ).is( "properties[user].initializedValue.address" );
		controlName( address.asDirectProperty() ).is( "properties[user].initializedValue.address" );
		controlName( address.asBinderItem() ).is( "properties[user].properties[address].value" );
		controlName( address.asBinderItem().withValue() ).is( "properties[user].properties[address].value" );
		controlName( address.asBinderItem().withInitializedValue() ).is( "properties[user].properties[address].initializedValue" );

		userDescriptor.setAttribute( EntityPropertyHandlingType.class, EntityPropertyHandlingType.MANUAL );
		userDescriptor.setAttribute( EntityAttributes.CONTROL_NAME, "extensions[user]" );
		address = EntityPropertyControlName.forProperty( addressDescriptor );
		controlName( address ).is( "extensions[user].user.address" );
		controlName( address.asDirectProperty() ).is( "extensions[user].user.address" );

		// for manual controls the binder paths are not valid
		controlName( address.asBinderItem() ).is( "properties[extensions[user]].properties[user.address].value" );
		controlName( address.asBinderItem().withValue() ).is( "properties[extensions[user]].properties[user.address].value" );
		controlName( address.asBinderItem().withInitializedValue() ).is( "properties[extensions[user]].properties[user.address].initializedValue" );

		EntityPropertyControlName.ForProperty user = EntityPropertyControlName.forProperty( userDescriptor );
		controlName( user ).is( "extensions[user]" );
		controlName( user.asDirectProperty() ).is( "extensions[user]" );
		controlName( user.asBinderItem() ).is( "properties[extensions[user]].value" );
		controlName( user.asBinderItem().withValue() ).is( "properties[extensions[user]].value" );
		controlName( user.asBinderItem().withInitializedValue() ).is( "properties[extensions[user]].initializedValue" );

		controlName( user.asMapEntry().withMapKey( "123" ) ).is( "extensions[user][123]" );
		controlName( user.asMapEntry().withMapKey( "123" ).asChildProperty( addressDescriptor ) ).is( "extensions[user][123].user.address" );
		controlName( user.asCollectionItem().withIndex( 10 ) ).is( "extensions[user][10]" );
		controlName( user.asCollectionItem().withIndex( 10 ).asChildProperty( addressDescriptor ) ).is( "extensions[user][10].user.address" );
	}

	// test equality

	private ControlNameTest controlName( Object controlName ) {
		return new ControlNameTest( controlName );
	}

	@RequiredArgsConstructor
	private class ControlNameTest
	{
		private final Object controlName;

		void is( String expected ) {
			assertThat( controlName.toString() ).isEqualTo( expected );
		}
	}
}
