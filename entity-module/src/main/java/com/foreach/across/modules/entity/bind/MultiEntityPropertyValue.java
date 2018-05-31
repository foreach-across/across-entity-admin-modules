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

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a multi value property. Any collection type is actually bound as a map.
 *
 * @author Arne Vandamme
 * @since 3.1.0
 */
public final class MultiEntityPropertyValue implements EntityPropertyValueController<Object>
{
	private EntityPropertiesBinder binder;
	private final EntityPropertyDescriptor collectionDescriptor;
	private final TypeDescriptor collectionTypeDescriptor;
	private final EntityPropertyController<Object, Object> collectionController;

	private final EntityPropertyDescriptor valueDescriptor;
	private final TypeDescriptor valueTypeDescriptor;
	private final EntityPropertyController<Object, Object> valueController;

	private final EntityPropertyDescriptor keyDescriptor;
	private final TypeDescriptor keyTypeDescriptor;
	private final EntityPropertyController<Object, Object> keyController;

	private final boolean isMap;

	private boolean itemsInitialized;

	@Getter
	private final Item template;

	@Getter
	@Setter
	private boolean bound;

	private Map<String, Item> items;

	MultiEntityPropertyValue( EntityPropertiesBinder binder,
	                          EntityPropertyDescriptor collectionDescriptor,
	                          EntityPropertyDescriptor valueDescriptor,
	                          EntityPropertyDescriptor keyDescriptor ) {
		this.binder = binder;
		this.collectionDescriptor = collectionDescriptor;
		this.valueDescriptor = valueDescriptor;
		this.keyDescriptor = keyDescriptor;

		isMap = collectionDescriptor.getPropertyTypeDescriptor().isMap();

		collectionTypeDescriptor = collectionDescriptor.getPropertyTypeDescriptor();
		valueTypeDescriptor = valueDescriptor != null
				? valueDescriptor.getPropertyTypeDescriptor()
				: ( isMap ? collectionTypeDescriptor.getMapValueTypeDescriptor() : null );
		keyTypeDescriptor = keyDescriptor != null
				? keyDescriptor.getPropertyTypeDescriptor()
				: ( isMap ? collectionTypeDescriptor.getMapKeyTypeDescriptor() : null );

		collectionController = collectionDescriptor.getController();
		valueController = valueDescriptor != null ? valueDescriptor.getController() : null;
		keyController = keyDescriptor != null ? keyDescriptor.getController() : null;

		template = createItem( "" );
	}

	@Override
	public Object initializeValue() {
		return null;
	}

	public Map<String, Item> getItems() {
		if ( items == null ) {
			items = new Items();
			if ( !isBound() && collectionController != null ) {
				setValue( collectionController.fetchValue( binder.getEntity() ) );
			}
			itemsInitialized = true;
		}
		else if ( !itemsInitialized && isBound() ) {
			itemsInitialized = true;
			items.clear();
		}

		return items;
	}

	public Collection<Item> getItemList() {
		return getItems()
				.values()
				.stream()
				.sorted( Comparator.comparingInt( Item::getSortIndex ) )
				.collect( Collectors.toList() );
	}

	@Override
	public Object getValue() {
		if ( isMap ) {
			LinkedHashMap<Object, Object> map = new LinkedHashMap<>();

			// not using Collectors.toMap() here as that one does not allow null values
			getItems()
					.values()
					.stream()
					.sorted( Comparator.comparingInt( Item::getSortIndex ) )
					.forEach( item -> map.put( item.getKey(), item.getValue() ) );

			return binder.convertIfNecessary( map, collectionTypeDescriptor, "" );
		}

		return binder.convertIfNecessary(
				getItems()
						.values()
						.stream()
						.sorted( Comparator.comparingInt( Item::getSortIndex ) )
						.map( Item::getValue )
						.toArray( size -> (Object[]) Array.newInstance( valueTypeDescriptor.getObjectType(), size ) ),
				collectionTypeDescriptor,
				""
		);
	}

	@Override
	public void setValue( Object value ) {
		itemsInitialized = true;

		if ( items == null ) {
			items = new Items();
		}
		items.clear();

		if ( value != null ) {
			List values = (List) binder.convertIfNecessary( value,
			                                                TypeDescriptor.collection( ArrayList.class, valueTypeDescriptor ),
			                                                collectionTypeDescriptor.getObjectType(),
			                                                collectionBinderPath() + ".value" );

			int index = 0;
			for ( Object v : values ) {
				String key = "" + index;
				Item item = new Item();
				item.setKey( key );
				item.setValue( v );
				item.setSortIndex( index++ );
				items.put( key, item );
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
			getItems()
					.forEach( ( key, item ) -> {
						errors.pushNestedPath( "items[" + key + "].value" );
						valueController.validate( binder.getEntity(), item.getValue(), errors, validationHints );
						errors.popNestedPath();
					} );
		}
		return beforeValidate >= errors.getErrorCount();
	}

	@Override
	public boolean applyValue() {
		if ( collectionController != null ) {
			return collectionController.applyValue( binder.getEntity(), getValue() );
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
		Item item = new Item();

		if ( !isMap || String.class.equals( keyTypeDescriptor.getObjectType() ) ) {
			item.setKey( key );
		}
		else {
			item.setKey( binder.createValue( keyController, binder.getEntity(), keyTypeDescriptor ) );
		}

		item.setValue( binder.createValue( valueController, binder.getEntity(), valueTypeDescriptor ) );

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
	@Getter
	@Setter
	@RequiredArgsConstructor
	public class Item
	{
		private Object key;

		private Object value;
		private int sortIndex;

		public void setKey( Object key ) {
			if ( keyTypeDescriptor != null ) {
				if ( "".equals( key ) && !String.class.equals( keyTypeDescriptor.getObjectType() ) ) {
					this.key = null;
				}
				else {
					this.key = binder.convertIfNecessary( key, keyTypeDescriptor, memberBinderPath() );
				}
			}
			else {
				this.key = key;
			}
		}

		public void setValue( Object value ) {
			if ( "".equals( value ) && !String.class.equals( valueTypeDescriptor.getObjectType() ) ) {
				this.value = null;
			}
			else {
				this.value = binder.convertIfNecessary( value, valueTypeDescriptor, memberBinderPath() );
			}
		}

		private String memberBinderPath() {
			return this == template ? collectionBinderPath() + ".template" : collectionBinderPath() + ".items[" + key + "].value";
		}
	}
}
