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
import org.springframework.core.Ordered;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.validation.Errors;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a property value backed by a {@link java.util.Collection} that is not a map.
 * The {@link #getValue()} will always return the current value of the collection. The {@link #getItems()}
 * returns the collection items as {@link EntityPropertyBinder} in a map structure.
 * The key of the map structure does not matter, it simply provides a unique identification for an
 * item in the collection, without relying on the actual index.
 * <p/>
 * The {@link EntityPropertyBinder#getSortIndex()} will determine the actual order of the resulting collection.
 * <p/>
 * By default when {@link #enableBinding(boolean)} is {@code true}, the list of items will be reset as it is
 * expected that binding will replace all the items (eg. all of them are submitted by form). This behaviour
 * can be customized by calling {@link #setUpdateItemsOnBinding(boolean)} with {@code true}.
 *
 * @author Arne Vandamme
 * @see SingleEntityPropertyBinder
 * @see MapEntityPropertyBinder
 * @since 3.1.0
 */
public class ListEntityPropertyBinder implements EntityPropertyBinder<Object>
{
	private final EntityPropertiesBinder binder;
	private final EntityPropertyDescriptor collectionDescriptor;
	private final TypeDescriptor collectionTypeDescriptor;
	private final EntityPropertyController<Object, Object> collectionController;

	private final EntityPropertyDescriptor memberDescriptor;
	private final TypeDescriptor memberTypeDescriptor;
	private final EntityPropertyController<Object, Object> memberController;

	private boolean itemsInitialized;

	private EntityPropertyBinder<Object> template;
	private boolean bindingBusy;

	@Getter
	@Setter
	private boolean bound;

	/**
	 * If set to {@code true}, the existing items will always be returned when performing data binding,
	 * and every item separately can be removed/modified. If {@code false}, then it is expected that all
	 * items are passed when data binding (replacing them). This is the default mode.
	 */
	@Getter
	@Setter
	private boolean updateItemsOnBinding;

	@Getter
	@Setter
	private int sortIndex;

	@Getter
	@Setter
	private boolean deleted;

	private Map<String, EntityPropertyBinder<Object>> items;

	@SuppressWarnings( "all" )
	private Optional<Object> originalValue;

	ListEntityPropertyBinder( EntityPropertiesBinder binder,
	                          EntityPropertyDescriptor collectionDescriptor,
	                          EntityPropertyDescriptor memberDescriptor ) {
		this.binder = binder;
		this.collectionDescriptor = collectionDescriptor;
		this.memberDescriptor = memberDescriptor;

		collectionTypeDescriptor = collectionDescriptor.getPropertyTypeDescriptor();
		memberTypeDescriptor = memberDescriptor.getPropertyTypeDescriptor();

		collectionController = collectionDescriptor.getController();
		memberController = memberDescriptor.getController();
	}

	@Override
	public Object getOriginalValue() {
		return loadOriginalValue();
	}

	/**
	 * Initialized template item for a single member of this list.
	 * Not meant to be modified but can be used to create blank values for a new member.
	 *
	 * @return template item controller
	 */
	public EntityPropertyBinder<Object> getTemplate() {
		if ( template == null ) {
			template = createItem();
		}
		return template;
	}

	@Override
	public boolean isModified() {
		return isDeleted() || ( ( isBound() || itemsInitialized ) && !Objects.equals( loadOriginalValue(), getValue() ) );
	}

	private EntityPropertyBinder<Object> createItem() {
		EntityPropertyBinder<Object> controller = binder.createPropertyBinder( memberDescriptor );
		controller.setValue( binder.createValue( memberController, memberTypeDescriptor ) );
		return controller;
	}

	@Override
	public Object createNewValue() {
		return binder.createValue( collectionController, collectionDescriptor.getPropertyTypeDescriptor() );
	}

	public Map<String, EntityPropertyBinder<Object>> getItems() {
		if ( items == null ) {
			val originalValue = loadOriginalValue();

			items = new Items();

			if ( ( !bindingBusy || updateItemsOnBinding ) && collectionController != null ) {
				setValue( originalValue );
			}
		}

		itemsInitialized = true;

		return items;
	}

	public Collection<EntityPropertyBinder<Object>> getItemList() {
		return Collections.unmodifiableList(
				getItems()
						.values()
						.stream()
						.sorted( Comparator.comparingInt( EntityPropertyBinder::getSortIndex ) )
						.collect( Collectors.toList() )
		);
	}

	@Override
	public Object getOrInitializeValue() {
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
			loadOriginalValue();

			items = getItems()
					.values()
					.stream()
					.filter( b -> !b.isDeleted() )
					.sorted( Comparator.comparingInt( EntityPropertyBinder::getSortIndex ) )
					.map( EntityPropertyBinder::getValue )
					.toArray( size -> (Object[]) Array.newInstance( memberTypeDescriptor.getObjectType(), size ) );

		}

		return binder.convertIfNecessary( items, collectionTypeDescriptor, binderPath( "items" ) );
	}

	@Override
	public void setValue( Object value ) {
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
			                                                binderPath( "value" ) );

			int index = 0;
			for ( Object v : values ) {
				String key = "" + index;
				EntityPropertyBinder<Object> item = binder.createPropertyBinder( memberDescriptor );
				item.setValue( v );
				item.setSortIndex( index++ );
				items.put( key, item );
			}
		}
	}

	private Object loadOriginalValue() {
		if ( originalValue == null ) {
			originalValue = Optional.ofNullable( collectionController.fetchValue( binder.getBindingContext() ) );
		}
		return originalValue.orElse( null );
	}

	@Override
	public boolean save() {
		if ( collectionController != null ) {
			return collectionController.save( binder.getBindingContext(), new EntityPropertyValue<>( loadOriginalValue(), getValue(), isDeleted() ) );
		}
		return false;
	}

	@Override
	public boolean validate( Errors errors, Object... validationHints ) {
		int beforeValidate = errors.getErrorCount();
		/*if ( valueController != null ) {
			getItems()
					.forEach( ( key, item ) -> {
						errors.pushNestedPath( "items[" + key + "].value" );
						valueController.validate( binder.getEntity(), item.getValue(), errors, validationHints );
						errors.popNestedPath();
					} );
		}*/
		return beforeValidate >= errors.getErrorCount();
	}

	private String binderPath( String property ) {
		return "[" + collectionDescriptor.getName() + "]" + ( StringUtils.isNotEmpty( property ) ? "." + property : "" );
	}

	@Override
	public boolean applyValue() {
		if ( collectionController != null ) {
			return collectionController.applyValue( binder.getBindingContext(), new EntityPropertyValue<>( loadOriginalValue(), getValue(), isDeleted() ) );
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

	/**
	 * Creates a new item for every key requested.
	 */
	class Items extends TreeMap<String, EntityPropertyBinder<Object>>
	{
		@Override
		public EntityPropertyBinder<Object> get( Object key ) {
			String itemKey = (String) key;
			EntityPropertyBinder<Object> item = super.get( itemKey );

			if ( item == null ) {
				item = createItem();
				super.put( itemKey, item );
			}

			return item;
		}
	}
}
