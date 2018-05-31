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
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.MethodInvocationException;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.convert.TypeDescriptor;

import java.beans.PropertyChangeEvent;
import java.util.HashMap;
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
public class EntityPropertiesBinder extends HashMap<String, EntityPropertyValueController> implements EntityPropertyValues
{
	@NonNull
	private final EntityPropertyRegistry propertyRegistry;

	private EntityPropertyDescriptor parentProperty;

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
	public EntityPropertyValueController getOrDefault( Object key, EntityPropertyValueController defaultValue ) {
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
	public EntityPropertyValueController get( Object key ) {
		EntityPropertyValueController valueHolder = super.get( key );
		String propertyName = (String) key;

		if ( valueHolder == null ) {
			try {
				String fqPropertyName = parentProperty != null ? parentProperty.getName() + "." + propertyName : propertyName;
				val descriptor = propertyRegistry.getProperty( fqPropertyName );
				if ( descriptor == null ) {
					throw new IllegalArgumentException( "No such property descriptor: '" + fqPropertyName + "'" );
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

	private EntityPropertyValueController createValueHolder( EntityPropertyDescriptor descriptor ) {
		TypeDescriptor typeDescriptor = descriptor.getPropertyTypeDescriptor();

		if ( typeDescriptor.isMap() ) {
			val keyDescriptor = propertyRegistry.getProperty( descriptor.getName() + EntityPropertyRegistry.MAP_KEY );
			val valueDescriptor = propertyRegistry.getProperty( descriptor.getName() + EntityPropertyRegistry.MAP_VALUE );

			return new MultiEntityPropertyValue( this, descriptor, valueDescriptor, keyDescriptor );
		}
		else if ( typeDescriptor.isCollection() || typeDescriptor.isArray() ) {
			val memberDescriptor = propertyRegistry.getProperty( descriptor.getName() + EntityPropertyRegistry.INDEXER );

			if ( memberDescriptor != null ) {
				return new MultiEntityPropertyValue( this, descriptor, memberDescriptor, null );
			}
		}

		return new SingleEntityPropertyValue( this, descriptor );
	}

	public void validate( Object... validationHints ) {

	}

	/**
	 * Bind the registered properties to the target entity.
	 * This will only be done for all properties that have been modified.
	 */
	public void bind() {
		values().forEach( v -> {
			if ( v instanceof SingleEntityPropertyValue ) {
				val holder = ( (SingleEntityPropertyValue) v );
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
		values().forEach( EntityPropertyValueController::resetBindStatus );
	}

	EntityPropertiesBinder createChildBinder( EntityPropertyDescriptor parent, Object propertyValue ) {
		EntityPropertiesBinder childBinder = new EntityPropertiesBinder( propertyRegistry );
		childBinder.parentProperty = parent;
		childBinder.setEntity( propertyValue );
		childBinder.setConversionService( conversionService );

		return childBinder;
	}

	Object createValue( EntityPropertyController<Object, Object> controller, Object entity, TypeDescriptor descriptor ) {
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

	Object convertIfNecessary( Object source, TypeDescriptor targetType, String path ) {
		return convertIfNecessary( source, targetType, targetType.getObjectType(), path );
	}

	Object convertIfNecessary( Object source, TypeDescriptor targetType, Class<?> typeToReport, String path ) {
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
}
