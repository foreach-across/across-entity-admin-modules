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

import static com.foreach.across.modules.entity.bind.EntityPropertyControlName.DEFAULT_BINDER_PREFIX;
import static com.foreach.across.modules.entity.bind.EntityPropertyControlName.forProperty;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arne Vandamme
 * @since 3.2.0
 */
public class TestEntityPropertyControlName
{
	@Test
	public void simplePropertyByName() {
		EntityPropertyControlName.ForProperty controlName = forProperty( "address" );
		assertThat( controlName ).isInstanceOf( EntityPropertyControlName.ForProperty.SingleValue.class );
		assertThat( controlName.asSingleValue() ).isSameAs( controlName );

		controlName( controlName ).is( "address" );
		controlName( controlName.asDirectProperty() ).is( "address" );
		controlName( controlName.asBinderItem() ).is( "properties[address].value" );
		controlName( controlName.asBinderItem().toItemPath() ).is("properties[address]" );
		controlName( controlName.asBinderItem().withValue() ).is( "properties[address].value" );
		controlName( controlName.asBinderItem().withInitializedValue() ).is( "properties[address].initializedValue" );
	}

	@Test
	public void childPropertyByName() {
		EntityPropertyControlName.ForProperty controlName = forProperty( "address" );
		EntityPropertyControlName.ForProperty child = controlName.forChildProperty( "street" );
		controlName( child ).is( "address.street" );
		controlName( child.asDirectProperty() ).is( "address.street" );
		controlName( child.asBinderItem() ).is( "properties[address].properties[street].value" );
		controlName( child.asBinderItem().toItemPath() ).is( "properties[address].properties[street]" );
		controlName( child.asBinderItem().withValue() ).is( "properties[address].properties[street].value" );
		controlName( child.asBinderItem().withInitializedValue() ).is( "properties[address].properties[street].initializedValue" );
	}

	@Test
	public void childFromDirectProperty() {
		EntityPropertyControlName address = forProperty( "address" ).asDirectProperty();
		EntityPropertyControlName.ForProperty child = address.forChildProperty( "street" );
		controlName( child ).is( "address.street" );
		controlName( child.asDirectProperty() ).is( "address.street" );
		controlName( child.asBinderItem() ).is( "properties[address].properties[street].value" );
		controlName( child.asBinderItem().toItemPath() ).is( "properties[address].properties[street]" );
		controlName( child.asBinderItem().withValue() ).is( "properties[address].properties[street].value" );
		controlName( child.asBinderItem().withInitializedValue() ).is( "properties[address].properties[street].initializedValue" );
	}

	@Test
	public void childFromBinderProperty() {
		EntityPropertyControlName address = forProperty( "address" ).asBinderItem();
		EntityPropertyControlName.ForProperty child = address.forChildProperty( "street" );

		controlName( child ).is( "properties[address].initializedValue.street" );
		controlName( child.asDirectProperty() ).is( "properties[address].initializedValue.street" );
		controlName( child.asBinderItem() ).is( "properties[address].properties[street].value" );
		controlName( child.asBinderItem().toItemPath() ).is( "properties[address].properties[street]" );
		controlName( child.asBinderItem().withValue() ).is( "properties[address].properties[street].value" );
		controlName( child.asBinderItem().withInitializedValue() ).is( "properties[address].properties[street].initializedValue" );
	}

	@Test
	public void childFromBinderPropertyWithInitializedValue() {
		EntityPropertyControlName address = forProperty( "address" ).asBinderItem().withInitializedValue();
		EntityPropertyControlName.ForProperty child = address.forChildProperty( "street" );

		controlName( child ).is( "properties[address].initializedValue.street" );
		controlName( child.asDirectProperty() ).is( "properties[address].initializedValue.street" );
		controlName( child.asBinderItem() ).is( "properties[address].properties[street].value" );
		controlName( child.asBinderItem().withValue() ).is( "properties[address].properties[street].value" );
		controlName( child.asBinderItem().withInitializedValue() ).is( "properties[address].properties[street].initializedValue" );
	}

	@Test
	public void emptyIndexerProperty() {
		EntityPropertyControlName.ForProperty address = forProperty( "address[]" );
		assertThat( address ).isInstanceOf( EntityPropertyControlName.ForProperty.CollectionItem.class );
		assertThat( address.asCollectionItem() ).isSameAs( address );

		controlName( address ).is( "address[]" );
		controlName( address.asDirectProperty() ).is( "address[]" );
		controlName( address.asBinderItem() ).is( "properties[address].items[].value" );
		controlName( address.asBinderItem().withValue() ).is( "properties[address].items[].value" );
		controlName( address.asBinderItem().withInitializedValue() ).is( "properties[address].items[].initializedValue" );

		controlName( address.asSingleValue() ).is( "address" );
		controlName( address.asSingleValue().asCollectionItem() ).is( "address[]" );
	}

	@Test
	public void indexerProperty() {
		EntityPropertyControlName.ForProperty address = forProperty( "address[]" ).asCollectionItem().withIndex( 10 );
		controlName( address ).is( "address[10]" );
		controlName( address.asDirectProperty() ).is( "address[10]" );
		controlName( address.asBinderItem() ).is( "properties[address].items[10].value" );
		controlName( address.asBinderItem().toItemPath() ).is( "properties[address].items[10]"  );
		controlName( address.asBinderItem().withValue() ).is( "properties[address].items[10].value" );
		controlName( address.asBinderItem().withInitializedValue() ).is( "properties[address].items[10].initializedValue" );

		address = forProperty( "address[]" ).asCollectionItem().withIndex( 5 ).withBinderItemKey( "abc" );
		controlName( address ).is( "address[5]" );
		controlName( address.asDirectProperty() ).is( "address[5]" );
		controlName( address.asBinderItem() ).is( "properties[address].items[abc].value" );
		controlName( address.asBinderItem().toItemPath() ).is( "properties[address].items[abc]"  );
		controlName( address.asBinderItem().withValue() ).is( "properties[address].items[abc].value" );
		controlName( address.asBinderItem().withInitializedValue() ).is( "properties[address].items[abc].initializedValue" );
	}

	@Test
	public void childOfIndexerProperty() {
		EntityPropertyControlName.ForProperty address = forProperty( "address[]" ).asCollectionItem().withIndex( 10 )
		                                                                          .withBinderItemKey(
				                                                                          "abc" );
		EntityPropertyControlName.ForProperty child = address.forChildProperty( "street" );
		controlName( child ).is( "address[10].street" );
		controlName( child.asDirectProperty() ).is( "address[10].street" );
		controlName( child.asBinderItem() ).is( "properties[address].items[abc].properties[street].value" );
		controlName( child.asBinderItem().toItemPath() ).is( "properties[address].items[abc].properties[street]" );
		controlName( child.asBinderItem().withValue() ).is( "properties[address].items[abc].properties[street].value" );
		controlName( child.asBinderItem().withInitializedValue() ).is( "properties[address].items[abc].properties[street].initializedValue" );

	}

	@Test
	public void childOfIndexerPropertyAsBinderProperty() {
		EntityPropertyControlName address = forProperty( "address" )
				.asCollectionItem()
				.withIndex( 10 )
				.withBinderItemKey( "abc" )
				.asBinderItem();
		EntityPropertyControlName.ForProperty child = address.forChildProperty( "street" );

		controlName( child ).is( "properties[address].items[abc].initializedValue.street" );
		controlName( child.asDirectProperty() ).is( "properties[address].items[abc].initializedValue.street" );
		controlName( child.asBinderItem() ).is( "properties[address].items[abc].properties[street].value" );
		controlName( child.asBinderItem().toItemPath() ).is( "properties[address].items[abc].properties[street]" );
		controlName( child.asBinderItem().withValue() ).is( "properties[address].items[abc].properties[street].value" );
		controlName( child.asBinderItem().withInitializedValue() ).is( "properties[address].items[abc].properties[street].initializedValue" );
	}

	@Test
	public void childOfIndexerPropertyAsBinderPropertyWithInitializedValue() {
		EntityPropertyControlName address = forProperty( "address" )
				.asCollectionItem()
				.withIndex( 10 )
				.withBinderItemKey( "abc" )
				.asBinderItem()
				.withInitializedValue();
		EntityPropertyControlName.ForProperty child = address.forChildProperty( "street" );

		controlName( child ).is( "properties[address].items[abc].initializedValue.street" );
		controlName( child.asDirectProperty() ).is( "properties[address].items[abc].initializedValue.street" );
		controlName( child.asBinderItem() ).is( "properties[address].items[abc].properties[street].value" );
		controlName( child.asBinderItem().withValue() ).is( "properties[address].items[abc].properties[street].value" );
		controlName( child.asBinderItem().withInitializedValue() ).is( "properties[address].items[abc].properties[street].initializedValue" );
	}

	@Test
	public void emptyMapEntryProperty() {
		EntityPropertyControlName.ForProperty.MapEntry address = forProperty( "address" ).asMapEntry();
		controlName( address ).is( "address[]" );
		controlName( address.asDirectProperty() ).is( "address[]" );
		controlName( address.toBinderEntryPath() ).is( "properties[address].entries[]" );
		controlName( address.asBinderEntryKey() ).is( "properties[address].entries[].key.value" );
		controlName( address.asBinderEntryKey().withValue() ).is( "properties[address].entries[].key.value" );
		controlName( address.asBinderEntryKey().withInitializedValue() ).is( "properties[address].entries[].key.initializedValue" );
		controlName( address.asBinderEntryKey().toItemPath() ).is( "properties[address].entries[].key" );
		controlName( address.asBinderEntryValue() ).is( "properties[address].entries[].value.value" );
		controlName( address.asBinderEntryValue().withValue() ).is( "properties[address].entries[].value.value" );
		controlName( address.asBinderEntryValue().withInitializedValue() ).is( "properties[address].entries[].value.initializedValue" );
		controlName( address.asBinderEntryValue().toItemPath() ).is( "properties[address].entries[].value" );
	}

	@Test
	public void mapEntryProperty() {
		EntityPropertyControlName.ForProperty.MapEntry address = forProperty( "address" )
				.asMapEntry()
				.withMapKey( "abc" )
				.withBinderEntryKey( "def" );

		controlName( address ).is( "address[abc]" );
		controlName( address.asDirectProperty() ).is( "address[abc]" );
		controlName( address.toBinderEntryPath() ).is( "properties[address].entries[def]" );
		controlName( address.asBinderEntryKey() ).is( "properties[address].entries[def].key.value" );
		controlName( address.asBinderEntryKey().withValue() ).is( "properties[address].entries[def].key.value" );
		controlName( address.asBinderEntryKey().withInitializedValue() ).is( "properties[address].entries[def].key.initializedValue" );
		controlName( address.asBinderEntryValue() ).is( "properties[address].entries[def].value.value" );
		controlName( address.asBinderEntryValue().withValue() ).is( "properties[address].entries[def].value.value" );
		controlName( address.asBinderEntryValue().withInitializedValue() ).is( "properties[address].entries[def].value.initializedValue" );
	}

	@Test
	public void childOfMapEntryPropertyDefaultsToValue() {
		EntityPropertyControlName.ForProperty.MapEntry address = forProperty( "address" )
				.asMapEntry()
				.withMapKey( "abc" )
				.withBinderEntryKey( "def" );
		EntityPropertyControlName.ForProperty child = address.forChildProperty( "street" );
		controlName( child ).is( "address[abc].street" );
		controlName( child.asDirectProperty() ).is( "address[abc].street" );
		controlName( child.asBinderItem() ).is( "properties[address].entries[def].value.properties[street].value" );
		controlName( child.asBinderItem().withValue() ).is( "properties[address].entries[def].value.properties[street].value" );
		controlName( child.asBinderItem().withInitializedValue() ).is( "properties[address].entries[def].value.properties[street].initializedValue" );

		child = address.asBinderEntryValue().forChildProperty( "street" );
		controlName( child ).is( "properties[address].entries[def].value.initializedValue.street" );
		controlName( child.asDirectProperty() ).is( "properties[address].entries[def].value.initializedValue.street" );
		controlName( child.asBinderItem() ).is( "properties[address].entries[def].value.properties[street].value" );
		controlName( child.asBinderItem().withValue() ).is( "properties[address].entries[def].value.properties[street].value" );
		controlName( child.asBinderItem().withInitializedValue() ).is( "properties[address].entries[def].value.properties[street].initializedValue" );

		child = address.asBinderEntryKey().forChildProperty( "street" );
		controlName( child ).is( "properties[address].entries[def].key.initializedValue.street" );
		controlName( child.asDirectProperty() ).is( "properties[address].entries[def].key.initializedValue.street" );
		controlName( child.asBinderItem() ).is( "properties[address].entries[def].key.properties[street].value" );
		controlName( child.asBinderItem().withValue() ).is( "properties[address].entries[def].key.properties[street].value" );
		controlName( child.asBinderItem().withInitializedValue() ).is( "properties[address].entries[def].key.properties[street].initializedValue" );
	}

	@Test
	public void childOfMapEntryPropertyAsBinderProperty() {
		EntityPropertyControlName address = forProperty( "address" )
				.asMapEntry()
				.withMapKey( "abc" )
				.withBinderEntryKey( "def" )
				.asBinderItem();
		EntityPropertyControlName.ForProperty child = address.forChildProperty( "street" );
		controlName( child ).is( "properties[address].entries[def].value.initializedValue.street" );
		controlName( child.asDirectProperty() ).is( "properties[address].entries[def].value.initializedValue.street" );
		controlName( child.asBinderItem() ).is( "properties[address].entries[def].value.properties[street].value" );
		controlName( child.asBinderItem().withValue() ).is( "properties[address].entries[def].value.properties[street].value" );
		controlName( child.asBinderItem().withInitializedValue() ).is( "properties[address].entries[def].value.properties[street].initializedValue" );

		address = forProperty( "address" )
				.asMapEntry()
				.withMapKey( "abc" )
				.withBinderEntryKey( "def" )
				.asBinderEntryValue();
		child = address.forChildProperty( "street" );
		controlName( child ).is( "properties[address].entries[def].value.initializedValue.street" );
		controlName( child.asDirectProperty() ).is( "properties[address].entries[def].value.initializedValue.street" );
		controlName( child.asBinderItem() ).is( "properties[address].entries[def].value.properties[street].value" );
		controlName( child.asBinderItem().withValue() ).is( "properties[address].entries[def].value.properties[street].value" );
		controlName( child.asBinderItem().withInitializedValue() ).is( "properties[address].entries[def].value.properties[street].initializedValue" );

		address = forProperty( "address" )
				.asMapEntry()
				.withMapKey( "abc" )
				.withBinderEntryKey( "def" )
				.asBinderEntryKey();
		child = address.forChildProperty( "street" );
		controlName( child ).is( "properties[address].entries[def].key.initializedValue.street" );
		controlName( child.asDirectProperty() ).is( "properties[address].entries[def].key.initializedValue.street" );
		controlName( child.asBinderItem() ).is( "properties[address].entries[def].key.properties[street].value" );
		controlName( child.asBinderItem().withValue() ).is( "properties[address].entries[def].key.properties[street].value" );
		controlName( child.asBinderItem().withInitializedValue() ).is( "properties[address].entries[def].key.properties[street].initializedValue" );
	}

	@Test
	public void childOfMapEntryPropertyAsBinderPropertyWithInitializedValue() {
		EntityPropertyControlName address = forProperty( "address" )
				.asMapEntry()
				.withMapKey( "abc" )
				.withBinderEntryKey( "def" )
				.asBinderItem()
				.withInitializedValue();
		EntityPropertyControlName.ForProperty child = address.forChildProperty( "street" );
		controlName( child ).is( "properties[address].entries[def].value.initializedValue.street" );
		controlName( child.asDirectProperty() ).is( "properties[address].entries[def].value.initializedValue.street" );
		controlName( child.asBinderItem() ).is( "properties[address].entries[def].value.properties[street].value" );
		controlName( child.asBinderItem().withValue() ).is( "properties[address].entries[def].value.properties[street].value" );
		controlName( child.asBinderItem().withInitializedValue() ).is( "properties[address].entries[def].value.properties[street].initializedValue" );

		address = forProperty( "address" )
				.asMapEntry()
				.withMapKey( "abc" )
				.withBinderEntryKey( "def" )
				.asBinderEntryValue()
				.withInitializedValue();
		child = address.forChildProperty( "street" );
		controlName( child ).is( "properties[address].entries[def].value.initializedValue.street" );
		controlName( child.asDirectProperty() ).is( "properties[address].entries[def].value.initializedValue.street" );
		controlName( child.asBinderItem() ).is( "properties[address].entries[def].value.properties[street].value" );
		controlName( child.asBinderItem().withValue() ).is( "properties[address].entries[def].value.properties[street].value" );
		controlName( child.asBinderItem().withInitializedValue() ).is( "properties[address].entries[def].value.properties[street].initializedValue" );

		address = forProperty( "address" )
				.asMapEntry()
				.withMapKey( "abc" )
				.withBinderEntryKey( "def" )
				.asBinderEntryKey()
				.withInitializedValue();
		child = address.forChildProperty( "street" );
		controlName( child ).is( "properties[address].entries[def].key.initializedValue.street" );
		controlName( child.asDirectProperty() ).is( "properties[address].entries[def].key.initializedValue.street" );
		controlName( child.asBinderItem() ).is( "properties[address].entries[def].key.properties[street].value" );
		controlName( child.asBinderItem().withValue() ).is( "properties[address].entries[def].key.properties[street].value" );
		controlName( child.asBinderItem().withInitializedValue() ).is( "properties[address].entries[def].key.properties[street].initializedValue" );
	}

	@Test
	public void forHandlingType() {
		EntityPropertyControlName.ForProperty user = forProperty( "user" );
		EntityPropertyControlName.ForProperty address = user.forChildProperty( "address[]" );

		controlName( user.forHandlingType( EntityPropertyHandlingType.DIRECT ) ).is( "user" );
		controlName( user.forHandlingType( EntityPropertyHandlingType.EXTENSION ) ).is( "properties[user].value" );
		controlName( user.forHandlingType( EntityPropertyHandlingType.MANUAL ) ).is( "user" );

		controlName( address.forHandlingType( EntityPropertyHandlingType.DIRECT ) ).is( "user.address[]" );
		controlName( address.forHandlingType( EntityPropertyHandlingType.EXTENSION ) ).is( "properties[user].properties[address].items[].value" );
		controlName( address.forHandlingType( EntityPropertyHandlingType.MANUAL ) ).is( "address[]" );
	}

	@Test
	public void generateForDescriptor() {
		SimpleEntityPropertyDescriptor userDescriptor = new SimpleEntityPropertyDescriptor( "user" );
		userDescriptor.setAttribute( EntityPropertyHandlingType.class, EntityPropertyHandlingType.DIRECT );
		SimpleEntityPropertyDescriptor addressDescriptor = new SimpleEntityPropertyDescriptor( "user.address" );

		EntityPropertyControlName.ForProperty user = forProperty( userDescriptor, "props" );
		controlName( user ).is( "user" );
		controlName( user.asDirectProperty() ).is( "user" );
		controlName( user.asBinderItem() ).is( "props[user].value" );
		controlName( user.asBinderItem().withValue() ).is( "props[user].value" );
		controlName( user.asBinderItem().withInitializedValue() ).is( "props[user].initializedValue" );

		EntityPropertyControlName.ForProperty address = forProperty( addressDescriptor );
		controlName( address ).is( "user.address" );
		controlName( address.asDirectProperty() ).is( "user.address" );
		controlName( address.asBinderItem() ).is( "properties[user.address].value" );
		controlName( address.asBinderItem().withValue() ).is( "properties[user.address].value" );
		controlName( address.asBinderItem().withInitializedValue() ).is( "properties[user.address].initializedValue" );

		// turn it into a nested descriptor
		addressDescriptor.setParentDescriptor( userDescriptor );
		address = forProperty( addressDescriptor );
		controlName( address ).is( "user.address" );
		controlName( address.asDirectProperty() ).is( "user.address" );
		controlName( address.asBinderItem() ).is( "properties[user].properties[address].value" );
		controlName( address.asBinderItem().withValue() ).is( "properties[user].properties[address].value" );
		controlName( address.asBinderItem().withInitializedValue() ).is( "properties[user].properties[address].initializedValue" );

		// as child of specified control name
		controlName( user.asBinderItem().withInitializedValue().forChildProperty( addressDescriptor ) )
				.is( "props[user].initializedValue.address" );
	}

	@Test
	public void parentControlNameOnBuilderContextEnsuresAllControlNamesAreMappedAsChildProperty() {
		EntityPropertyDescriptor userDescriptor = new SimpleEntityPropertyDescriptor( "user" );
		SimpleEntityPropertyDescriptor addressDescriptor = new SimpleEntityPropertyDescriptor( "user.address" );
		EntityPropertyControlName.ForProperty user = forProperty( userDescriptor, "props" );

		ViewElementBuilderContext bc = new DefaultViewElementBuilderContext();
		EntityPropertyControlName.ForProperty address = forProperty( addressDescriptor, bc );
		controlName( address ).is( "user.address" );
		controlName( address.asDirectProperty() ).is( "user.address" );
		controlName( address.asBinderItem() ).is( "properties[user.address].value" );
		controlName( address.asBinderItem().withValue() ).is( "properties[user.address].value" );
		controlName( address.asBinderItem().withInitializedValue() ).is( "properties[user.address].initializedValue" );

		// use the parent control name and make it a nested descriptor
		addressDescriptor.setParentDescriptor( userDescriptor );
		bc.setAttribute( EntityPropertyControlName.class, user.asBinderItem().withInitializedValue() );

		address = forProperty( addressDescriptor, bc );
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

		EntityPropertyControlName.ForProperty address = forProperty( addressDescriptor );
		controlName( address ).is( "user.address" );
		controlName( address.asDirectProperty() ).is( "user.address" );
		controlName( address.asBinderItem() ).is( "properties[user].properties[address].value" );
		controlName( address.asBinderItem().withValue() ).is( "properties[user].properties[address].value" );
		controlName( address.asBinderItem().withInitializedValue() ).is( "properties[user].properties[address].initializedValue" );

		userDescriptor.setAttribute( EntityPropertyHandlingType.class, EntityPropertyHandlingType.EXTENSION );
		address = forProperty( addressDescriptor );
		controlName( address ).is( "properties[user].initializedValue.address" );
		controlName( address.asDirectProperty() ).is( "properties[user].initializedValue.address" );
		controlName( address.asBinderItem() ).is( "properties[user].properties[address].value" );
		controlName( address.asBinderItem().withValue() ).is( "properties[user].properties[address].value" );
		controlName( address.asBinderItem().withInitializedValue() ).is( "properties[user].properties[address].initializedValue" );

		userDescriptor.setAttribute( EntityPropertyHandlingType.class, EntityPropertyHandlingType.MANUAL );
		userDescriptor.setAttribute( EntityAttributes.CONTROL_NAME, "extensions[user]" );
		address = forProperty( addressDescriptor );
		controlName( address ).is( "extensions[user].user.address" );
		controlName( address.asDirectProperty() ).is( "extensions[user].user.address" );

		// for manual controls the binder paths are not valid
		controlName( address.asBinderItem() ).is( "properties[extensions[user]].properties[user.address].value" );
		controlName( address.asBinderItem().withValue() ).is( "properties[extensions[user]].properties[user.address].value" );
		controlName( address.asBinderItem().withInitializedValue() ).is( "properties[extensions[user]].properties[user.address].initializedValue" );

		EntityPropertyControlName.ForProperty user = forProperty( userDescriptor );
		controlName( user ).is( "extensions[user]" );
		controlName( user.asDirectProperty() ).is( "extensions[user]" );
		controlName( user.asBinderItem() ).is( "properties[extensions[user]].value" );
		controlName( user.asBinderItem().withValue() ).is( "properties[extensions[user]].value" );
		controlName( user.asBinderItem().withInitializedValue() ).is( "properties[extensions[user]].initializedValue" );

		controlName( user.asMapEntry().withMapKey( "123" ) ).is( "extensions[user][123]" );
		controlName( user.asMapEntry().withMapKey( "123" ).forChildProperty( addressDescriptor ) ).is( "extensions[user][123].user.address" );
		controlName( user.asCollectionItem().withIndex( 10 ) ).is( "extensions[user][10]" );
		controlName( user.asCollectionItem().withIndex( 10 ).forChildProperty( addressDescriptor ) ).is( "extensions[user][10].user.address" );
	}

	@Test
	public void rootControlNameImpactsSetsBinderPrefixAndDirectPrefix() {
		EntityPropertyControlName root = EntityPropertyControlName.root( "entity" );
		controlName( root ).is( "entity" );

		EntityPropertyControlName.ForProperty property = root.forChildProperty( "user" );
		controlName( property ).is( "entity.user" );
		controlName( property.asBinderItem() ).is( "properties[user].value" );
		controlName( property.asBinderItem().withInitializedValue() ).is( "properties[user].initializedValue" );
		controlName( property.asCollectionItem().withIndex( 5 ) ).is( "entity.user[5]" );
		controlName( property.asCollectionItem().withIndex( 0 ).asBinderItem() ).is( "properties[user].items[0].value" );

		root = EntityPropertyControlName.root( "entity.data", "binderTarget.fields" );
		controlName( root ).is( "entity.data" );

		property = root.forChildProperty( "user" );
		controlName( property ).is( "entity.data.user" );
		controlName( property.asBinderItem() ).is( "binderTarget.fields[user].value" );
		controlName( property.asBinderItem().withInitializedValue() ).is( "binderTarget.fields[user].initializedValue" );
		controlName( property.asCollectionItem().withIndex( 5 ) ).is( "entity.data.user[5]" );
		controlName( property.asCollectionItem().withIndex( 0 ).asBinderItem() ).is( "binderTarget.fields[user].items[0].value" );
	}

	@Test
	public void rootControlNameShouldNotCountForNestedDescriptor() {
		EntityPropertyControlName root = EntityPropertyControlName.root( "entity.data", "binderTarget.fields" );

		SimpleEntityPropertyDescriptor userDescriptor = new SimpleEntityPropertyDescriptor( "user" );
		userDescriptor.setAttribute( EntityPropertyHandlingType.class, EntityPropertyHandlingType.DIRECT );
		SimpleEntityPropertyDescriptor addressDescriptor = new SimpleEntityPropertyDescriptor( "user.address[]" );

		EntityPropertyControlName.ForProperty user = root.forChildProperty( userDescriptor );
		controlName( user ).is( "entity.data.user" );
		controlName( user.asDirectProperty() ).is( "entity.data.user" );
		controlName( user.asBinderItem() ).is( "binderTarget.fields[user].value" );
		controlName( user.asBinderItem().withValue() ).is( "binderTarget.fields[user].value" );
		controlName( user.asBinderItem().withInitializedValue() ).is( "binderTarget.fields[user].initializedValue" );

		EntityPropertyControlName.ForProperty address = root.forChildProperty( addressDescriptor );
		controlName( address ).is( "entity.data.user.address[]" );
		controlName( address.asDirectProperty() ).is( "entity.data.user.address[]" );
		controlName( address.asBinderItem() ).is( "binderTarget.fields[user.address[]].value" );
		controlName( address.asBinderItem().withValue() ).is( "binderTarget.fields[user.address[]].value" );
		controlName( address.asBinderItem().withInitializedValue() ).is( "binderTarget.fields[user.address[]].initializedValue" );

		// turn it into a nested descriptor
		addressDescriptor.setParentDescriptor( userDescriptor );
		address = root.forChildProperty( addressDescriptor );
		controlName( address ).is( "entity.data.user.address[]" );
		controlName( address.asDirectProperty() ).is( "entity.data.user.address[]" );
		controlName( address.asBinderItem() ).is( "binderTarget.fields[user].properties[address].items[].value" );
		controlName( address.asBinderItem().withValue() ).is( "binderTarget.fields[user].properties[address].items[].value" );
		controlName( address.asBinderItem().withInitializedValue() ).is( "binderTarget.fields[user].properties[address].items[].initializedValue" );

		// as child of specified control name
		controlName( user.asBinderItem().withInitializedValue().forChildProperty( addressDescriptor ) )
				.is( "binderTarget.fields[user].initializedValue.address[]" );

		// additional child property
		SimpleEntityPropertyDescriptor streetDescriptor = new SimpleEntityPropertyDescriptor( "user.address[].street" );
		streetDescriptor.setParentDescriptor( addressDescriptor );

		EntityPropertyControlName.ForProperty street = address.asCollectionItem()
		                                                      .withBinderItemKey( 123 )
		                                                      .forChildProperty( "street" );
		controlName( street ).is( "entity.data.user.address[].street" );
		controlName( street.asBinderItem() ).is( "binderTarget.fields[user].properties[address].items[123].properties[street].value" );
		controlName( street.asBinderItem().withInitializedValue() ).is(
				"binderTarget.fields[user].properties[address].items[123].properties[street].initializedValue" );
		controlName( street.asCollectionItem().withIndex( 5 ) )
				.is( "entity.data.user.address[].street[5]" );
		controlName( street.asCollectionItem().withIndex( 0 ).asBinderItem() )
				.is( "binderTarget.fields[user].properties[address].items[123].properties[street].items[0].value" );

		street = address.asCollectionItem()
		                .withBinderItemKey( 123 )
		                .forChildProperty( streetDescriptor );
		controlName( street ).is( "entity.data.user.address[].street" );
		controlName( street.asBinderItem() ).is( "binderTarget.fields[user].properties[address].items[123].properties[street].value" );
		controlName( street.asBinderItem().withInitializedValue() ).is(
				"binderTarget.fields[user].properties[address].items[123].properties[street].initializedValue" );
		controlName( street.asCollectionItem().withIndex( 5 ) )
				.is( "entity.data.user.address[].street[5]" );
		controlName( street.asCollectionItem().withIndex( 0 ).asBinderItem() )
				.is( "binderTarget.fields[user].properties[address].items[123].properties[street].items[0].value" );
	}

	@Test
	public void mapTypeProperties() {
		SimpleEntityPropertyDescriptor parent = new SimpleEntityPropertyDescriptor( "user.map[key]" );
		SimpleEntityPropertyDescriptor child = new SimpleEntityPropertyDescriptor( "user.map[key].street" );
		child.setParentDescriptor( parent );

		EntityPropertyControlName.ForProperty parentControlName = forProperty( parent );
		assertThat( parentControlName ).isInstanceOf( EntityPropertyControlName.ForProperty.MapEntry.class );
		assertThat( parentControlName.asMapEntry() ).isSameAs( parentControlName );
		controlName( parentControlName ).is( "user.map[]" );

		EntityPropertyControlName.ForProperty street = parentControlName.forChildProperty( child );
		controlName( street ).is( "user.map[].street" );
		controlName( parentControlName.asMapEntry().withBinderEntryKey( "123" ).asBinderEntryKey().forChildProperty( child ) )
				.is( "properties[user.map].entries[123].key.initializedValue.street" );
	}

	@Test
	public void equalityForPropertyName() {
		assertThat( forProperty( "one" ) )
				.isEqualTo( forProperty( "one", DEFAULT_BINDER_PREFIX ) )
				.isNotEqualTo( forProperty( "one", "props" ) );

		assertThat( EntityPropertyControlName.root( "user" ).forChildProperty( "name" ) )
				.isEqualTo( EntityPropertyControlName.root( "user", DEFAULT_BINDER_PREFIX ).forChildProperty( "name" ) )
				.isNotEqualTo( forProperty( "user.name" ) );

		assertThat( forProperty( "one" ).asBinderItem().withValue() )
				.isNotEqualTo( "properties[one].value" )
				.isEqualTo( forProperty( "one" ).asBinderItem().withValue() )
				.isNotEqualTo( forProperty( "one" ).asBinderItem().withInitializedValue() );

		assertThat( forProperty( "one" ).asMapEntry().withBinderEntryKey( 123 ).asBinderEntryKey().withValue() )
				.isEqualTo( forProperty( "one" ).asMapEntry().withBinderEntryKey( 123 ).asBinderEntryKey().withValue() )
				.isNotEqualTo( forProperty( "one" ).asMapEntry().withBinderEntryKey( 123 ).withMapKey( 1 ).asBinderEntryKey().withValue() );
	}

	@Test
	public void withTerminationProperties() {
		EntityPropertyControlName.ForProperty controlName = forProperty( "user" );
		assertThat( controlName.asBinderItem().toDeleted() ).isEqualTo( "properties[user].deleted" );
		assertThat( controlName.asBinderItem().toBound() ).isEqualTo( "properties[user].bound" );
		assertThat( controlName.asBinderItem().toSortIndex() ).isEqualTo( "properties[user].sortIndex" );

		assertThat( controlName.asCollectionItem().withBinderItemKey( "123" ).asBinderItem().toDeleted() ).isEqualTo( "properties[user].items[123].deleted" );

		assertThat( controlName.asMapEntry().withBinderEntryKey( "123" ).toBinderEntrySortIndex() ).isEqualTo( "properties[user].entries[123].sortIndex" );
		assertThat( controlName.asMapEntry().withBinderEntryKey( "123" ).toBinderEntryDeleted() ).isEqualTo( "properties[user].entries[123].deleted" );
	}

	@Test
	public void turningControlNameIntoCollectionItemIsTheSameAsPassingIndexerControlName() {
		SimpleEntityPropertyDescriptor userDescriptor = new SimpleEntityPropertyDescriptor( "user[]" );
		SimpleEntityPropertyDescriptor addressDescriptor = new SimpleEntityPropertyDescriptor( "user[].address" );
		addressDescriptor.setParentDescriptor( userDescriptor );

		controlName(
				forProperty( userDescriptor )
						.asCollectionItem()
						.withBinderItemKey( 10 )
						.asBinderItem()
						.withInitializedValue()
						.forChildProperty( addressDescriptor )
		).is( "properties[user].items[10].initializedValue.address" );

		userDescriptor = new SimpleEntityPropertyDescriptor( "user" );
		addressDescriptor.setParentDescriptor( userDescriptor );

		controlName(
				forProperty( userDescriptor )
						.asCollectionItem()
						.withBinderItemKey( 10 )
						.asBinderItem()
						.withInitializedValue()
						.forChildProperty( addressDescriptor )
		).is( "properties[user].items[10].initializedValue.address" );
	}

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
