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
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.MethodInvocationException;
import org.springframework.core.Ordered;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.validation.Errors;

import java.beans.PropertyChangeEvent;
import java.util.*;
import java.util.function.Supplier;

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

		if ( typeDescriptor.isCollection() || typeDescriptor.isArray() ) {
			val memberDescriptor = propertyRegistry.getProperty( descriptor.getName() + EntityPropertyRegistry.INDEXER );

			if ( memberDescriptor != null ) {
				return new MultiValue( descriptor, memberDescriptor );
			}
		}

		return new SingleValue( descriptor );
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
	 * Represents a single value property.
	 */
	public class SingleValue implements EntityPropertyValueHolder<Object>
	{
		private final EntityPropertyDescriptor descriptor;
		private final EntityPropertyController<Object, Object> controller;

		/**
		 * Has {@link #setValue(Object)} been called with a new value.
		 */
		@Getter
		private boolean modified;

		/**
		 * The actual value held for that property.
		 */
		@Getter
		private Object value;

		private SingleValue( EntityPropertyDescriptor descriptor ) {
			this.descriptor = descriptor;
			controller = descriptor.getAttribute( EntityPropertyController.class );

			if ( controller != null ) {
				value = controller.fetchValue( getEntity() );
			}
		}

		/**
		 * Set the value for this property, can be {@code null}.
		 * The source value will be converted to the expected type defined by the descriptor.
		 *
		 * @param value to set
		 */
		@Override
		public void setValue( Object value ) {
			Object newValue = convertIfNecessary( value, descriptor.getPropertyTypeDescriptor(), binderPath() );
			if ( !Objects.equals( this.value, newValue ) ) {
				modified = true;
			}
			this.value = newValue;
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

		private String binderPath() {
			return "[" + descriptor.getName() + "].value";
		}

		@Override
		public int getControllerOrder() {
			return controller != null ? controller.getOrder() : Ordered.LOWEST_PRECEDENCE;
		}
	}

	/**
	 * Represents a multi value property. Any collection type is actually bound as a map.
	 */
	public class MultiValue implements EntityPropertyValueHolder<Object>
	{
		private final EntityPropertyDescriptor collectionDescriptor;
		private final EntityPropertyDescriptor memberDescriptor;
		private final EntityPropertyController<Object, Object> collectionController;
		private final EntityPropertyController<Object, Object> memberController;

		@Getter
		private final Map<String, Item> items = new Items();

		@Getter
		private final Item template;

		private MultiValue( EntityPropertyDescriptor collectionDescriptor, EntityPropertyDescriptor memberDescriptor ) {
			this.collectionDescriptor = collectionDescriptor;
			this.memberDescriptor = memberDescriptor;

			collectionController = collectionDescriptor.getAttribute( EntityPropertyController.class );
			memberController = memberDescriptor.getAttribute( EntityPropertyController.class );

			template = createItem( "" );
		}

		@Override
		public Object getValue() {
			return convertIfNecessary(
					items.values()
					     .stream()
					     .sorted( Comparator.comparingInt( Item::getSortIndex ) )
					     .map( Item::getValue )
					     .toArray(),
					collectionDescriptor.getPropertyTypeDescriptor(),
					""
			);
		}

		@Override
		public void setValue( Object value ) {
			items.clear();

			List values = (List) convertIfNecessary( value,
			                                         TypeDescriptor.collection( ArrayList.class, memberDescriptor.getPropertyTypeDescriptor() ),
			                                         collectionDescriptor.getPropertyType(),
			                                         collectionBinderPath() + ".value" );

			int index = 0;
			for ( Object v : values ) {
				Item item = new Item( "" + index );
				item.setValue( v );
				item.setSortIndex( index++ );
				items.put( item.key, item );
			}
		}

		@Override
		public boolean save() {
			return false;
		}

		@Override
		public boolean validate( Errors errors, Object... validationHints ) {
			return false;
		}

		@Override
		public boolean applyValue() {
			return false;
		}

		@Override
		public int getControllerOrder() {
			return collectionController != null ? collectionController.getOrder() : Ordered.LOWEST_PRECEDENCE;
		}

		private Item createItem( String key ) {
			Item item = new Item( key );
			item.setValue( memberController.fetchValue( getEntity() ) );
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
			private final String key;

			private Object value;
			private int sortIndex;

			public void setValue( Object value ) {
				this.value = convertIfNecessary( value, memberDescriptor.getPropertyTypeDescriptor(), memberBinderPath() );
			}

			private String memberBinderPath() {
				return this == template ? collectionBinderPath() + ".template" : collectionBinderPath() + ".items[" + key + "].value";
			}
		}
	}
}
