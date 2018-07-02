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
 *
 * @author Arne Vandamme
 * @see SingleEntityPropertyValueController
 * @see MapEntityPropertyValue
 * @since 3.1.0
 */
public class ListEntityPropertyValueController implements EntityPropertyValueController<Object>
{
	private final EntityPropertiesBinder binder;
	private final EntityPropertyDescriptor collectionDescriptor;
	private final TypeDescriptor collectionTypeDescriptor;
	private final EntityPropertyController<Object, Object> collectionController;

	private final EntityPropertyDescriptor memberDescriptor;
	private final TypeDescriptor memberTypeDescriptor;
	private final EntityPropertyController<Object, Object> memberController;

	private boolean itemsInitialized;

	private EntityPropertyValueController<Object> template;

	@Getter
	@Setter
	private boolean bound;

	/**
	 * If set to {@code true}, the existing items will always be returned when performing data binding,
	 * and every item separately can be removed/modified. If {@code false}, then it is expected that all
	 * items are passed when data binding. This is the default mode.
	 * <p/>
	 * This means that when {@link #getValue()} and {@link #getItems()} is called, an empty result will
	 * be returned if {@link #isBound()} is {@code true}. With incremental {@code true}, the original
	 * items will still be returned, except if they are removed.
	 */
	@Getter
	@Setter
	private boolean incrementalBinding;

	@Getter
	@Setter
	private int sortIndex;

	private Map<String, EntityPropertyValueController<Object>> items;

	private Optional<Object> originalValue;

	ListEntityPropertyValueController( EntityPropertiesBinder binder,
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

	/**
	 * Initialized template item for a single member of this list.
	 * Not meant to be modified but can be used to create blank values for a new member.
	 *
	 * @return template item controller
	 */
	public EntityPropertyValueController<Object> getTemplate() {
		if ( template == null ) {
			template = createItem();
		}
		return template;
	}

	@Override
	public boolean isModified() {
		return ( isBound() && !itemsInitialized ) || ( itemsInitialized && !Objects.equals( loadOriginalValue(), getValue() ) );
	}

	private EntityPropertyValueController<Object> createItem() {
		EntityPropertyValueController<Object> controller = binder.createValueController( memberDescriptor );
		controller.setValue( binder.createValue( memberController, memberTypeDescriptor ) );
		return controller;
	}

	@Override
	public Object createNewValue() {
		return null;
	}

	public Map<String, EntityPropertyValueController<Object>> getItems() {
		if ( items == null ) {
			val originalValue = loadOriginalValue();

			items = new Items();
			if ( collectionController != null ) {
				setValue( originalValue );
			}
		}

		itemsInitialized = true;
		/*else if ( !itemsInitialized && isBound() ) {
			itemsInitialized = true;
			items.clear();
		}*/

		return items;
	}

	public Collection<EntityPropertyValueController<Object>> getItemList() {
		return Collections.unmodifiableList(
				getItems()
						.values()
						.stream()
						.sorted( Comparator.comparingInt( EntityPropertyValueController::getSortIndex ) )
						.collect( Collectors.toList() )
		);
	}

	@Override
	public Object getValue() {
		Object[] items = new Object[0];

		if ( !isDeleted() ) {
			items = getItems()
					.values()
					.stream()
					.sorted( Comparator.comparingInt( EntityPropertyValueController::getSortIndex ) )
					.map( EntityPropertyValueController::getValue )
					.toArray( size -> (Object[]) Array.newInstance( memberTypeDescriptor.getObjectType(), size ) );
		}
		else {
			loadOriginalValue();
			this.items = new Items();
		}

		return binder.convertIfNecessary(
				items,
				collectionTypeDescriptor,
				binderPath( "items" )
		);
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
				EntityPropertyValueController<Object> item = binder.createValueController( memberDescriptor );
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

	public boolean isDeleted() {
		return isBound() && !itemsInitialized;
	}

	private String collectionBinderPath() {
		return "[" + collectionDescriptor.getName() + "]";
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

	/**
	 * Creates a new item for every key requested.
	 */
	class Items extends TreeMap<String, EntityPropertyValueController<Object>>
	{
		@Override
		public EntityPropertyValueController<Object> get( Object key ) {
			String itemKey = (String) key;
			EntityPropertyValueController<Object> item = super.get( itemKey );

			if ( item == null ) {
				item = createItem();
				super.put( itemKey, item );
			}

			return item;
		}
	}
}
