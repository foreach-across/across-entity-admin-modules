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
import lombok.Setter;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.validation.Errors;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a property value backed by a {@link java.util.Collection} that is not a map.
 * The {@link #getValue()} will always return the current value of the collection. The {@link #getItems()}
 * returns the collection items wrapped as {@link EntityPropertyBinder} in a map structure.
 * The key of the map structure does not matter, it simply provides a unique identification for an
 * item in the collection, without relying on the actual index.
 * <p/>
 * The {@link EntityPropertyBinder#getSortIndex()} or every single item
 * will determine the actual order of the resulting collection.
 * <p/>
 * By default when {@link #enableBinding(boolean)} is {@code true}, the list of items will be reset as it is
 * expected that binding will replace all the items (eg. all of them are submitted by form). This behaviour
 * can be customized by calling {@link #setUpdateItemsOnBinding(boolean)} with {@code true}.
 *
 * @author Arne Vandamme
 * @see SingleEntityPropertyBinder
 * @see MapEntityPropertyBinder
 * @since 3.2.0
 */
@SuppressWarnings("Duplicates")
public final class ListEntityPropertyBinder extends AbstractEntityPropertyBinder
{
	private final EntityPropertiesBinder binder;
	private final EntityPropertyDescriptor collectionDescriptor;
	private final TypeDescriptor collectionTypeDescriptor;
	private final EntityPropertyController collectionController;

	private final EntityPropertyDescriptor memberDescriptor;
	private final TypeDescriptor memberTypeDescriptor;

	private boolean bindingBusy;
	private boolean itemsInitialized;
	private boolean initializedValuePathWasUsed;

	private AbstractEntityPropertyBinder itemTemplate;
	private Map<String, EntityPropertyBinder> items;

	/**
	 * If set to {@code true}, the existing items will always be returned when performing data binding,
	 * and every item separately can be removed/modified. If {@code false}, then it is expected that all
	 * items are passed when data binding (replacing them). This is the default mode.
	 */
	@Getter
	@Setter
	private boolean updateItemsOnBinding;

	ListEntityPropertyBinder( EntityPropertiesBinder binder,
	                          EntityPropertyDescriptor collectionDescriptor,
	                          EntityPropertyDescriptor memberDescriptor ) {
		super( binder, collectionDescriptor.getController() );

		this.binder = binder;
		this.collectionDescriptor = collectionDescriptor;
		this.memberDescriptor = memberDescriptor;

		collectionTypeDescriptor = collectionDescriptor.getPropertyTypeDescriptor();
		memberTypeDescriptor = memberDescriptor.getPropertyTypeDescriptor();

		collectionController = collectionDescriptor.getController();
	}

	/**
	 * Initialized template item for a single member of this list.
	 * Not meant to be modified but can be used to create blank values for a new member.
	 *
	 * @return template item controller
	 */
	public EntityPropertyBinder getItemTemplate() {
		if ( itemTemplate == null ) {
			itemTemplate = createItem();
			itemTemplate.setBinderPath( getBinderPath( "itemTemplate" ) );
		}
		return itemTemplate;
	}

	@Override
	public boolean isModified() {
		return isDeleted() || ( ( isBound() || itemsInitialized ) && !Objects.equals( loadOriginalValue(), getValue() ) );
	}

	@Override
	public boolean isDirty() {
		if ( items != null ) {
			for ( EntityPropertyBinder value : items.values() ) {
				if ( value.isDirty() ) {
					return true;
				}
			}
		}

		return super.isDirty();
	}

	private AbstractEntityPropertyBinder createItem() {
		return binder.createPropertyBinder( memberDescriptor );
	}

	public Map<String, EntityPropertyBinder> getItems() {
		if ( items == null ) {
			val originalValue = loadOriginalValue();

			items = new Items();

			if ( ( !bindingBusy || updateItemsOnBinding ) && collectionController != null ) {
				setValueInternal( originalValue );
			}
		}

		itemsInitialized = true;

		return items;
	}

	public Collection<EntityPropertyBinder> getItemList() {
		return Collections.unmodifiableList(
				getItems()
						.values()
						.stream()
						.sorted( Comparator.comparingInt( EntityPropertyBinder::getSortIndex ) )
						.collect( Collectors.toList() )
		);
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
	public Object getValue() {
		Object[] items = new Object[0];

		if ( !isDeleted() ) {
			items = getItems()
					.values()
					.stream()
					.filter( b -> !b.isDeleted() )
					.sorted( Comparator.comparingInt( EntityPropertyBinder::getSortIndex ) )
					.map( EntityPropertyBinder::getValue )
					.toArray( size -> (Object[]) Array.newInstance( memberTypeDescriptor.getObjectType(), size ) );

		}

		return binder.convertIfNecessary( items, collectionTypeDescriptor, getBinderPath( "items" ) );
	}

	@Override
	public void setValue( Object value ) {
		markDirty();
		setValueInternal( value );
	}

	private void setValueInternal( Object value ) {
		itemsInitialized = true;

		if ( items == null ) {
			loadOriginalValue();
			items = new Items();
		}
		items.clear();

		if ( value != null ) {
			List values = (List) binder.convertIfNecessary( value,
			                                                TypeDescriptor.collection( ArrayList.class, memberTypeDescriptor ),
			                                                collectionTypeDescriptor.getObjectType(),
			                                                getBinderPath( "value" ) );

			int index = 0;
			for ( Object v : values ) {
				String key = "" + index;
				EntityPropertyBinder item = binder.createPropertyBinder( memberDescriptor );
				item.setValue( v );
				item.setSortIndex( index++ );
				items.put( key, item );
			}
		}
	}

	@Override
	public boolean validate( Errors errors, Object... validationHints ) {
		int beforeValidate = errors.getErrorCount();

		getItems()
				.forEach( ( key, item ) -> {
					try {
						errors.pushNestedPath( "items[" + key + "]" );
						item.validate( errors, validationHints );
					}
					finally {
						errors.popNestedPath();
					}
				} );

		if ( collectionController != null ) {
			try {
				errors.pushNestedPath( initializedValuePathWasUsed ? "initializedValue" : "value" );
				collectionController.validate(
						binder.getValueBindingContext(), new EntityPropertyValue<>( loadOriginalValue(), getValue(), isDeleted() ), errors, validationHints
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
				if ( items == null ) {
					getItems();
				}
				items.clear();
			}
			else if ( items != null ) {
				items.entrySet().removeIf( e -> e.getValue().isDeleted() );
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

	/**
	 * Creates a new item for every key requested.
	 */
	class Items extends TreeMap<String, EntityPropertyBinder>
	{
		@Override
		public EntityPropertyBinder get( Object key ) {
			String itemKey = (String) key;
			EntityPropertyBinder item = super.get( itemKey );

			if ( item == null ) {
				AbstractEntityPropertyBinder itemBinder = createItem();
				itemBinder.setBinderPath( getBinderPath( "items[" + itemKey + "]" ) );

				markDirty();

				item = itemBinder;
				super.put( itemKey, item );
			}

			return item;
		}
	}
}
