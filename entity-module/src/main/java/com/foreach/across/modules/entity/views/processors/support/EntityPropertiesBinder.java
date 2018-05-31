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

package com.foreach.across.modules.entity.views.processors.support;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyController;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.MethodInvocationException;
import org.springframework.core.Ordered;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.validation.Errors;

import java.beans.PropertyChangeEvent;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Wrapper for binding values to custom properties. Much like a {@link org.springframework.beans.BeanWrapper}
 * except it uses an {@link EntityPropertyRegistry} to determine which properties exist and what their type is.
 * Each existing descriptor should have a {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyController}
 * with a value reader/writer for correct use.
 * <p/>
 * Implemented as a {@code Map} for data binder puposes, but expects every key to correspond with a registered property
 * in the {@link EntityPropertyRegistry}. When fetched, will create an intermediate target
 * for that property and apply type conversion based on the registered property type.
 * Should not be used as a regular {@link java.util.Map} implementation.
 * <p/>
 * When a {@link ConversionService} is specified, type conversion will occur when setting a property value.
 * <p/>
 * WARNING: How properties are accessed can be relevant in how they are treated.
 * You can access for example {@code properties[user].value.name} or {@code properties[user.name].value} and both might
 * have different access semantics because the latter uses an entirely separate {@link EntityPropertyDescriptor}
 * whereas in the former {@code name} is a direct bean path.
 *
 * @author Arne Vandamme
 * @since 3.1.0
 */
@RequiredArgsConstructor
public class EntityPropertiesBinder extends HashMap<String, EntityPropertyValueHolder> implements EntityPropertyValues
{
	@NonNull
	private final EntityPropertyRegistry propertyRegistry;

	/**
	 * Prefix when the map is being used for data binding.
	 * If set and an exception occurs when setting a property value, it will be
	 * rethrown as a {@link ConversionNotSupportedException} with {@link PropertyChangeEvent} information.
	 * <p/>
	 * The binder prefix would usually be the name of a property where this {@code EntityPropertiesBinder}
	 * is available on the {@link org.springframework.validation.DataBinder} target.
	 * When using
	 */
	@Setter
	private String binderPrefix = "";

	/**
	 * Optionally set a {@link ConversionService} that should be used to convert the input
	 * value to the required field type. It no {@code ConversionService} is set, the actual value
	 * must match the expected field type or a {@link ClassCastException} will occur.
	 * <p/>
	 * If a {@code ConversionService} is set, type conversion will be attempted and exceptions
	 * will only be thrown if conversion failes.
	 */
	@Setter
	private ConversionService conversionService;

	/**
	 * Set the supplier for fetching the entity to which the properties should be bound.
	 * Note that the supplier will be called for every property that needs the entity.
	 */
	@Setter
	private Supplier<Object> entitySupplier;

	/**
	 * Returns self so that this binder could be used as direct {@link org.springframework.validation.DataBinder} target.
	 * The {@link #setBinderPrefix(String)} is usually set to {@code properties} in this case.
	 *
	 * @return self
	 */
	public final EntityPropertiesBinder getProperties() {
		return this;
	}

	/**
	 * Set a fixed entity to which the properties should be bound.
	 * If the actual entity might depend on outside configuration (for example use a DTO
	 * if there is one and else use the original entity), use {@link #setEntitySupplier(Supplier)} instead.
	 *
	 * @param entity to use
	 * @see #setEntitySupplier(Supplier)
	 */
	public void setEntity( Object entity ) {
		setEntitySupplier( () -> entity );
	}

	/**
	 * Get the entity this binder is attached to.
	 *
	 * @return entity (can be {@code null})
	 */
	public Object getEntity() {
		return entitySupplier != null ? entitySupplier.get() : null;
	}

	@Override
	public EntityPropertyValueHolder getOrDefault( Object key, EntityPropertyValueHolder defaultValue ) {
		return get( key );
	}

	/**
	 * Get the property with the given name.
	 * Will fetch the property descriptor and create the value holder with the current value.
	 * If there is no descriptor for that property, an {@link IllegalArgumentException} will be thrown.
	 *
	 * @param key property name
	 * @return value holder
	 */
	@Override
	public EntityPropertyValueHolder get( Object key ) {
		EntityPropertyValueHolder valueHolder = super.get( key );
		String propertyName = (String) key;

		if ( valueHolder == null ) {
			try {
				val descriptor = propertyRegistry.getProperty( propertyName );
				if ( descriptor == null ) {
					throw new IllegalArgumentException( "No such property descriptor: '" + propertyName + "'" );
				}

				valueHolder = createValueHolder( descriptor );
				put( propertyName, valueHolder );
			}
			catch ( IllegalArgumentException iae ) {
				if ( !StringUtils.isEmpty( binderPrefix ) ) {
					PropertyChangeEvent pce = new PropertyChangeEvent( this, binderPrefix + "[" + key + "]", null, null );
					throw new MethodInvocationException( pce, iae );
				}
				throw iae;
			}
		}

		return valueHolder;
	}

	private EntityPropertyValueHolder createValueHolder( EntityPropertyDescriptor descriptor ) {
		TypeDescriptor typeDescriptor = descriptor.getPropertyTypeDescriptor();

		if ( typeDescriptor.isMap() ) {
			val keyDescriptor = propertyRegistry.getProperty( descriptor.getName() + EntityPropertyRegistry.MAP_KEY );
			val valueDescriptor = propertyRegistry.getProperty( descriptor.getName() + EntityPropertyRegistry.MAP_VALUE );

			return new MultiValue( descriptor, valueDescriptor, keyDescriptor );
		}
		else if ( typeDescriptor.isCollection() || typeDescriptor.isArray() ) {
			val memberDescriptor = propertyRegistry.getProperty( descriptor.getName() + EntityPropertyRegistry.INDEXER );

			if ( memberDescriptor != null ) {
				return new MultiValue( descriptor, memberDescriptor, null );
			}
		}

		return new SingleValue( descriptor );
	}

	private Object createValue( EntityPropertyController<Object,Object> controller, Object entity, TypeDescriptor descriptor ) {
		if ( controller != null ) {
			return controller.createValue( entity );
		}

		if ( descriptor != null ) {
			val constructor = ConstructorUtils.getAccessibleConstructor( descriptor.getObjectType() );
			if ( constructor != null ) {
				try {
					return constructor.newInstance();
				}
				catch ( Exception ignore ) { /* ignore instantiation exceptions */ }
			}
		}

		return null;
	}

	private Object convertIfNecessary( Object source, TypeDescriptor targetType, String path ) {
		return convertIfNecessary( source, targetType, targetType.getObjectType(), path );
	}

	private Object convertIfNecessary( Object source, TypeDescriptor targetType, Class<?> typeToReport, String path ) {
		if ( source == null || source.getClass().equals( Object.class ) ) {
			return null;
		}

		try {
			if ( conversionService != null ) {
				return conversionService.convert( source, TypeDescriptor.forObject( source ), targetType );
			}

			return targetType.getObjectType().cast( source );
		}
		catch ( ClassCastException | ConversionFailedException | ConverterNotFoundException cce ) {
			if ( !StringUtils.isEmpty( binderPrefix ) ) {
				PropertyChangeEvent pce = new PropertyChangeEvent( this, binderPrefix + path, null, source );
				throw new ConversionNotSupportedException( pce, typeToReport, cce );
			}
			throw cce;
		}
	}

	public void validate( Object... validationHints ) {

	}

	/**
	 * Bind the registered properties to the target entity.
	 * This will only be done for all properties that have been modified.
	 */
	public void bind() {
		values().forEach( v -> {
			if ( v instanceof SingleValue ) {
				val holder = ( (SingleValue) v );
				if ( holder.isModified() ) {
					holder.applyValue();
				}
			}
		} );
	}

	public void save() {

	}

	/**
	 * Reset the different binding related properties (eg. was a property expected to be bound).
	 * Useful if you want to bind multiple times on the same entity using the same binder instance.
	 */
	public void resetForBinding() {
		values().forEach( EntityPropertyValueHolder::resetBindStatus );
	}

	/**
	 * Represents a single value property.
	 */
	public class SingleValue implements EntityPropertyValueHolder<Object>
	{
		private final EntityPropertyDescriptor descriptor;
		private final EntityPropertyController<Object, Object> controller;

		private boolean valueHasBeenSet;

		@Getter
		@Setter
		private boolean bound;

		/**
		 * Has {@link #setValue(Object)} been called with a new value.
		 */
		@Getter
		private boolean modified;

		/**
		 * The actual value held for that property.
		 */
		private Object value;

		@SuppressWarnings("unchecked")
		private SingleValue( EntityPropertyDescriptor descriptor ) {
			this.descriptor = descriptor;
			controller = descriptor.getController();

			if ( controller != null ) {
				value = controller.fetchValue( getEntity() );
			}
		}

		@Override
		public Object getValue() {
			if ( isDeleted() ) {
				return null;
			}
			return value;
		}

		/**
		 * Set the value for this property, can be {@code null}.
		 * The source value will be converted to the expected type defined by the descriptor.
		 *
		 * @param value to set
		 */
		@Override
		public void setValue( Object value ) {
			valueHasBeenSet = true;

			Object newValue = convertIfNecessary( value, descriptor.getPropertyTypeDescriptor(), binderPath() );
			if ( !Objects.equals( this.value, newValue ) ) {
				modified = true;
			}
			this.value = newValue;
		}

		@Override
		public Object initializeValue() {
			return createValue( controller, getEntity(), descriptor.getPropertyTypeDescriptor() );
		}

		/**
		 * Apply the property value to the target entity, can only be done if there is a controller.
		 */
		@Override
		public boolean applyValue() {
			// modified? mark as applied.
			if ( controller != null ) {
				return controller.applyValue( getEntity(), value );
			}
			return false;
		}

		@Override
		public boolean save() {
			if ( controller != null ) {
				return controller.save( getEntity(), value );
			}
			return false;
		}

		@Override
		public boolean validate( Errors errors, Object... validationHints ) {
			int beforeValidate = errors.getErrorCount();
			errors.pushNestedPath( "value" );
			if ( controller != null ) {
				controller.validate( getEntity(), value, errors, validationHints );
			}
			errors.popNestedPath();
			return beforeValidate >= errors.getErrorCount();
		}

		private boolean isDeleted() {
			return isBound() && !valueHasBeenSet;
		}

		private String binderPath() {
			return "[" + descriptor.getName() + "].value";
		}

		@Override
		public int getControllerOrder() {
			return controller != null ? controller.getOrder() : Ordered.LOWEST_PRECEDENCE;
		}

		public void resetBindStatus() {
			setBound( false );
			valueHasBeenSet = false;
		}
	}

	/**
	 * Represents a multi value property. Any collection type is actually bound as a map.
	 */
	public class MultiValue implements EntityPropertyValueHolder<Object>
	{
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

		private MultiValue( EntityPropertyDescriptor collectionDescriptor, EntityPropertyDescriptor valueDescriptor, EntityPropertyDescriptor keyDescriptor ) {
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
					setValue( collectionController.fetchValue( getEntity() ) );
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

				return convertIfNecessary( map, collectionTypeDescriptor, "" );
			}

			return convertIfNecessary(
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
				List values = (List) convertIfNecessary( value,
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
				return collectionController.save( getEntity(), getValue() );
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
							valueController.validate( getEntity(), item.getValue(), errors, validationHints );
							errors.popNestedPath();
						} );
			}
			return beforeValidate >= errors.getErrorCount();
		}

		@Override
		public boolean applyValue() {
			if ( collectionController != null ) {
				return collectionController.applyValue( getEntity(), getValue() );
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
				item.setKey( createValue( keyController, getEntity(), keyTypeDescriptor ) );
			}

			item.setValue( createValue( valueController, getEntity(), valueTypeDescriptor ) );

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
						this.key = convertIfNecessary( key, keyTypeDescriptor, memberBinderPath() );
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
					this.value = convertIfNecessary( value, valueTypeDescriptor, memberBinderPath() );
				}
			}

			private String memberBinderPath() {
				return this == template ? collectionBinderPath() + ".template" : collectionBinderPath() + ".items[" + key + "].value";
			}
		}
	}
}
