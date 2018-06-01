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
 * @since 3.1.0
 */
public class ListEntityPropertyValue implements EntityPropertyValueController<Object>
{
	private final EntityPropertiesBinder binder;
	private final EntityPropertyDescriptor collectionDescriptor;
	private final TypeDescriptor collectionTypeDescriptor;
	private final EntityPropertyController<Object, Object> collectionController;

	private final EntityPropertyDescriptor memberDescriptor;
	private final TypeDescriptor memberTypeDescriptor;
	private final EntityPropertyController<Object, Object> memberController;

	private boolean itemsInitialized;

	@Getter
	private final EntityPropertyValueController<Object> template;

	@Getter
	@Setter
	private boolean bound;

	@Getter
	@Setter
	private int sortIndex;

	private Map<String, EntityPropertyValueController<Object>> items;

	ListEntityPropertyValue( EntityPropertiesBinder binder,
	                         EntityPropertyDescriptor collectionDescriptor,
	                         EntityPropertyDescriptor memberDescriptor ) {
		this.binder = binder;
		this.collectionDescriptor = collectionDescriptor;
		this.memberDescriptor = memberDescriptor;

		collectionTypeDescriptor = collectionDescriptor.getPropertyTypeDescriptor();
		memberTypeDescriptor = memberDescriptor.getPropertyTypeDescriptor();

		collectionController = collectionDescriptor.getController();
		memberController = memberDescriptor.getController();

		template = createItem();
	}

	private EntityPropertyValueController<Object> createItem() {
		EntityPropertyValueController<Object> holder = binder.createValueHolder( memberDescriptor );
		holder.setValue( binder.createValue( memberController, binder.getEntity(), memberTypeDescriptor ) );
		return holder;
	}

	@Override
	public Object initializeValue() {
		return null;
	}

	public Map<String, EntityPropertyValueController<Object>> getItems() {
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

	public Collection<EntityPropertyValueController<Object>> getItemList() {
		return getItems()
				.values()
				.stream()
				.sorted( Comparator.comparingInt( EntityPropertyValueController::getSortIndex ) )
				.collect( Collectors.toList() );
	}

	@Override
	public Object getValue() {
		return binder.convertIfNecessary(
				getItems()
						.values()
						.stream()
						.sorted( Comparator.comparingInt( EntityPropertyValueController::getSortIndex ) )
						.map( EntityPropertyValueController::getValue )
						.toArray( size -> (Object[]) Array.newInstance( memberTypeDescriptor.getObjectType(), size ) ),
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
			                                                TypeDescriptor.collection( ArrayList.class, memberTypeDescriptor ),
			                                                collectionTypeDescriptor.getObjectType(),
			                                                collectionBinderPath() + ".value" );

			int index = 0;
			for ( Object v : values ) {
				String key = "" + index;
				EntityPropertyValueController<Object> item = binder.createValueHolder( memberDescriptor );
				item.setValue( v );
				item.setSortIndex( index++ );
				items.put( key, item );
			}
		}
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
