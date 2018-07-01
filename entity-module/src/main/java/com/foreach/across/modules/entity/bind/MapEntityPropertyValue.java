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

import com.foreach.across.modules.entity.registry.properties.EntityPropertyController;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.core.Ordered;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.validation.Errors;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a property value backed by a {@link java.util.Map}.
 *
 * @author Arne Vandamme
 * @see SingleEntityPropertyValue
 * @since 3.1.0
 */
public class MapEntityPropertyValue implements EntityPropertyValueController<Object>
{
	private final EntityPropertiesBinder binder;
	private final EntityPropertyDescriptor collectionDescriptor;
	private final TypeDescriptor collectionTypeDescriptor;
	private final EntityPropertyController<Object, Object> collectionController;

	private final EntityPropertyDescriptor valueDescriptor;
	private final TypeDescriptor valueTypeDescriptor;
	private final EntityPropertyController<Object, Object> valueController;

	private final EntityPropertyDescriptor keyDescriptor;
	private final TypeDescriptor keyTypeDescriptor;
	private final EntityPropertyController<Object, Object> keyController;

	private boolean itemsInitialized;

	@Getter
	private final Item template;

	@Getter
	@Setter
	private boolean bound;

	@Getter
	@Setter
	private int sortIndex;

	private Map<String, Item> entries;

	MapEntityPropertyValue( EntityPropertiesBinder binder,
	                        EntityPropertyDescriptor collectionDescriptor,
	                        EntityPropertyDescriptor valueDescriptor,
	                        EntityPropertyDescriptor keyDescriptor ) {
		this.binder = binder;
		this.collectionDescriptor = collectionDescriptor;
		this.valueDescriptor = valueDescriptor;
		this.keyDescriptor = keyDescriptor;

		collectionTypeDescriptor = collectionDescriptor.getPropertyTypeDescriptor();
		valueTypeDescriptor = valueDescriptor != null
				? valueDescriptor.getPropertyTypeDescriptor()
				: collectionTypeDescriptor.getMapValueTypeDescriptor();
		keyTypeDescriptor = keyDescriptor != null
				? keyDescriptor.getPropertyTypeDescriptor()
				: collectionTypeDescriptor.getMapKeyTypeDescriptor();

		collectionController = collectionDescriptor.getController();
		valueController = valueDescriptor != null ? valueDescriptor.getController() : null;
		keyController = keyDescriptor != null ? keyDescriptor.getController() : null;

		template = createItem( "" );
	}

	@Override
	public Object createNewValue() {
		return null;
	}

	public Map<String, Item> getEntries() {
		if ( entries == null ) {
			entries = new Items();
			if ( !isBound() && collectionController != null ) {
				setValue( collectionController.fetchValue( binder.getEntity() ) );
			}
			itemsInitialized = true;
		}
		else if ( !itemsInitialized && isBound() ) {
			itemsInitialized = true;
			entries.clear();
		}

		return entries;
	}

	public Collection<Item> getItemList() {
		return getEntries()
				.values()
				.stream()
				.sorted( Comparator.comparingInt( Item::getSortIndex ) )
				.collect( Collectors.toList() );
	}

	@Override
	public Object getValue() {
		LinkedHashMap<Object, Object> map = new LinkedHashMap<>();

		// not using Collectors.toMap() here as that one does not allow null values
		getEntries()
				.values()
				.stream()
				.sorted( Comparator.comparingInt( Item::getSortIndex ) )
				.forEach( item -> map.put( item.getEntryKey(), item.getEntryValue() ) );

		return binder.convertIfNecessary( map, collectionTypeDescriptor, "" );
	}

	@Override
	public void setValue( Object value ) {
		itemsInitialized = true;

		if ( entries == null ) {
			entries = new Items();
		}
		entries.clear();

		if ( value != null ) {
			List values = (List) binder.convertIfNecessary( value,
			                                                TypeDescriptor.collection( ArrayList.class, valueTypeDescriptor ),
			                                                collectionTypeDescriptor.getObjectType(),
			                                                collectionBinderPath() + ".value" );

			int index = 0;
			for ( Object v : values ) {
				String key = "" + index;
				Item item = new Item( binder.createValueController( keyDescriptor ), binder.createValueController( valueDescriptor ) );
				item.setEntryKey( key );
				item.setEntryValue( v );
				item.setSortIndex( index++ );
				entries.put( key, item );
			}
		}
	}

	@Override
	public boolean save() {
		if ( collectionController != null ) {
			return collectionController.save( binder.getEntity(), getValue() );
		}
		return false;
	}

	@Override
	public boolean validate( Errors errors, Object... validationHints ) {
		int beforeValidate = errors.getErrorCount();
		if ( valueController != null ) {
			getEntries()
					.forEach( ( key, item ) -> {
						errors.pushNestedPath( "entries[" + key + "].value" );
						valueController.validate( binder.getEntity(), item.getValue(), errors, validationHints );
						errors.popNestedPath();
					} );
		}
		return beforeValidate >= errors.getErrorCount();
	}

	@Override
	public boolean applyValue() {
		if ( collectionController != null ) {
			return collectionController.applyValue( binder.getEntity(), null, getValue() );
		}
		return false;
	}

	@Override
	public void resetBindStatus() {
		bound = false;
		itemsInitialized = false;
	}

	@Override
	public int getControllerOrder() {
		return collectionController != null ? collectionController.getOrder() : Ordered.LOWEST_PRECEDENCE;
	}

	private Item createItem( String key ) {
		Item item = new Item( binder.createValueController( keyDescriptor ), binder.createValueController( valueDescriptor ) );

		if ( String.class.equals( keyTypeDescriptor.getObjectType() ) ) {
			item.setEntryKey( key );
		}
		else {
			item.setEntryKey( binder.createValue( keyController, binder.getEntity(), keyTypeDescriptor ) );
		}

		item.setEntryValue( binder.createValue( valueController, binder.getEntity(), valueTypeDescriptor ) );

		return item;
	}

	private String collectionBinderPath() {
		return "[" + collectionDescriptor.getName() + "]";
	}

	/**
	 * Creates a new item for every key requested.
	 */
	class Items extends TreeMap<String, Item>
	{
		@Override
		public Item get( Object key ) {
			String itemKey = (String) key;
			Item item = super.get( itemKey );

			if ( item == null ) {
				item = createItem( itemKey );
				super.put( itemKey, item );
			}

			return item;
		}
	}

	/**
	 * Single item.
	 */
	@RequiredArgsConstructor
	public class Item
	{
		@Getter
		@Setter
		private int sortIndex;

		@Getter
		private final EntityPropertyValueController<Object> key;

		@Getter
		private final EntityPropertyValueController<Object> value;

		public void setEntryKey( Object key ) {
			this.key.setValue( key );
		}

		public void setEntryValue( Object value ) {
			this.value.setValue( value );
		}

		public Object getEntryKey() {
			return key.getValue();
		}

		public Object getEntryValue() {
			return value.getValue();
		}

		/*private String memberBinderPath() {
			return this == template ? collectionBinderPath() + ".template" : collectionBinderPath() + ".entries[" + key + "].value";
		}*/
	}
}
