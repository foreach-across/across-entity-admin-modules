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
import com.foreach.across.modules.entity.registry.properties.EntityPropertyValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.validation.Errors;

import java.util.*;

/**
 * Represents a property value that is a {@link java.util.Map} implementation.
 *
 * @author Arne Vandamme
 * @see SingleEntityPropertyBinder
 * @see ListEntityPropertyBinder
 * @since 3.2.0
 */
@SuppressWarnings("Duplicates")
public final class MapEntityPropertyBinder extends AbstractEntityPropertyBinder
{
	private final EntityPropertiesBinder binder;
	private final EntityPropertyDescriptor collectionDescriptor;
	private final TypeDescriptor collectionTypeDescriptor;
	private final EntityPropertyController collectionController;

	private final EntityPropertyDescriptor valueDescriptor;
	private final TypeDescriptor valueTypeDescriptor;

	private final EntityPropertyDescriptor keyDescriptor;
	private final TypeDescriptor keyTypeDescriptor;

	private boolean bindingBusy;
	private boolean itemsInitialized;
	private boolean initializedValuePathWasUsed;

	private Item template;
	private Map<String, Item> entries;

	/**
	 * If set to {@code true}, the existing items will always be returned when performing data binding,
	 * and every item separately can be removed/modified. If {@code false}, then it is expected that all
	 * items are passed when data binding (replacing them). This is the default mode.
	 */
	@Getter
	@Setter
	private boolean updateItemsOnBinding;

	MapEntityPropertyBinder( EntityPropertiesBinder binder,
	                         EntityPropertyDescriptor collectionDescriptor,
	                         EntityPropertyDescriptor keyDescriptor,
	                         EntityPropertyDescriptor valueDescriptor ) {
		super( binder, collectionDescriptor.getController() );
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
	}

	/**
	 * Initialized template item for a single member of this map.
	 * Not meant to be modified but can be used to create blank values for a new member.
	 *
	 * @return template item controller
	 */
	public Item getTemplate() {
		if ( template == null ) {
			template = createItem( "" );
		}
		return template;
	}

	@Override
	public Object getInitializedValue() {
		initializedValuePathWasUsed = true;

		Object originalValue = loadOriginalValue();

		if ( !itemsInitialized && originalValue == null ) {
			setValue( createNewValue() );
		}

		return getValue();
	}

	@Override
	public boolean isModified() {
		return isDeleted() || ( ( isBound() || itemsInitialized ) && !Objects.equals( loadOriginalValue(), getValue() ) );
	}

	public Map<String, Item> getEntries() {
		if ( entries == null ) {
			val originalValue = loadOriginalValue();

			entries = new Items();

			if ( ( !bindingBusy || updateItemsOnBinding ) && collectionController != null ) {
				setValue( originalValue );
			}
		}

		itemsInitialized = true;

		return entries;
	}

	@Override
	public Object getValue() {
		LinkedHashMap<Object, Object> map = new LinkedHashMap<>();

		if ( !isDeleted() ) {
			// not using Collectors.toMap() here as that one does not allow null values
			getEntries()
					.values()
					.stream()
					.filter( e -> !e.isDeleted() )
					.sorted( Comparator.comparingInt( Item::getSortIndex ) )
					.forEach( item -> map.put( item.getEntryKey(), item.getEntryValue() ) );
		}

		return binder.convertIfNecessary( map, collectionTypeDescriptor, getBinderPath( "entries" ) );
	}

	@Override
	public void setValue( Object value ) {
		itemsInitialized = true;

		if ( entries == null ) {
			loadOriginalValue();
			entries = new Items();
		}
		entries.clear();

		if ( value != null ) {
			Map<?, ?> values = (Map) binder.convertIfNecessary( value,
			                                                    TypeDescriptor.map( LinkedHashMap.class, keyTypeDescriptor, valueTypeDescriptor ),
			                                                    LinkedHashMap.class,
			                                                    getBinderPath( "value" ) );

			int index = 0;
			for ( val entry : values.entrySet() ) {
				String key = "" + index;
				Item item = new Item( binder.createPropertyBinder( keyDescriptor ), binder.createPropertyBinder( valueDescriptor ) );
				item.setEntryKey( entry.getKey() );
				item.setEntryValue( entry.getValue() );
				item.setSortIndex( index++ );
				entries.put( key, item );
			}
		}
	}

	@Override
	public boolean validate( Errors errors, Object... validationHints ) {
		int beforeValidate = errors.getErrorCount();

		getEntries()
				.forEach( ( key, entry ) -> {
					try {
						errors.pushNestedPath( "entries[" + key + "].key" );
						entry.getKey().validate( errors, validationHints );
					}
					finally {
						errors.popNestedPath();
					}
					try {
						errors.pushNestedPath( "entries[" + key + "].value" );
						entry.getValue().validate( errors, validationHints );
					}
					finally {
						errors.popNestedPath();
					}
				} );

		if ( collectionController != null ) {
			try {
				errors.pushNestedPath( initializedValuePathWasUsed ? "initializedValue" : "value" );
				collectionController.validate(
						binder.getBindingContext(), new EntityPropertyValue<>( loadOriginalValue(), getValue(), isDeleted() ), errors, validationHints
				);
			}
			finally {
				errors.popNestedPath();
			}
		}

		return beforeValidate >= errors.getErrorCount();
	}

	/**
	 * While binding is enabled, the items collection will not remove any (possibly) deleted items.
	 * When explicitly disabling binding, the items will be cleared of any deleted items and will be
	 * fully cleared if the property itself is deleted.
	 *
	 * @param enabled true if in binding mode, false if not expecting any more changes
	 */
	@Override
	public void enableBinding( boolean enabled ) {
		bindingBusy = enabled;

		if ( !enabled ) {
			if ( isDeleted() ) {
				if ( entries == null ) {
					getEntries();
				}
				entries.clear();
			}
			else if ( entries != null ) {
				entries.entrySet().removeIf( e -> e.getValue().isDeleted() );
			}
		}
	}

	@Override
	public EntityPropertyBinder resolvePropertyBinder( EntityPropertyDescriptor descriptor ) {
		if ( StringUtils.equals( descriptor.getName(), collectionDescriptor.getName() ) ) {
			return this;
		}

		return null;
	}

	private Item createItem( String key ) {
		Item item = new Item( binder.createPropertyBinder( keyDescriptor ), binder.createPropertyBinder( valueDescriptor ) );

		if ( String.class.equals( keyTypeDescriptor.getObjectType() ) ) {
			item.setEntryKey( key );
		}

		return item;
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
		private boolean deleted;

		@Getter
		@Setter
		private int sortIndex;

		@Getter
		private final EntityPropertyBinder key;

		@Getter
		private final EntityPropertyBinder value;

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
	}
}
