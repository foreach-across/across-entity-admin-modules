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

import com.foreach.across.modules.entity.registry.properties.*;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.MethodInvocationException;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.DefaultConversionService;

import java.beans.PropertyChangeEvent;
import java.util.HashMap;

import static com.foreach.across.modules.entity.registry.properties.support.EntityPropertyDescriptorUtils.findDirectChild;
import static com.foreach.across.modules.entity.registry.properties.support.EntityPropertyDescriptorUtils.getRootDescriptor;

/**
 * Wrapper for binding values to custom properties. Much like a {@link org.springframework.beans.BeanWrapper}
 * except it uses an {@link EntityPropertyRegistry} to determine which properties exist and what their type is.
 * Each existing descriptor should have a {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyController}
 * with a value reader/writer for correct use.
 * <p/>
 * Implemented as a {@code Map} for data binder purposes, but expects every key to correspond with a registered property
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
 * @see EntityPropertiesBinderController
 * @see EntityPropertyControlName
 * @since 3.2.0
 */
public class EntityPropertiesBinder extends HashMap<String, EntityPropertyBinder>
{
	private final EntityPropertiesBinderAsBindingContext bindingContextMapper = new EntityPropertiesBinderAsBindingContext();

	private Object entity;
	private Object target;

	@Getter(value = AccessLevel.PACKAGE)
	@Setter(value = AccessLevel.PACKAGE)
	private EntityPropertyDescriptor parentProperty;

	@Setter(value = AccessLevel.PACKAGE)
	protected EntityPropertyRegistry propertyRegistry;

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
	@Getter
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

	@Getter
	private boolean bindingEnabled;

	/**
	 * This is the internal binding context that should be used for applying the actual values.
	 * Note that this is different from the value of {@link #asBindingContext()}. The latter is meant
	 * to be used outside the binder for value retrieval, the {@code valueBindingContext} is only
	 * for internal use within the binder when applying the actual values.
	 */
	@Getter(value = AccessLevel.PACKAGE)
	private final EntityPropertyBindingContext valueBindingContext = new EntityPropertiesBinderValueBindingContext();

	public EntityPropertiesBinder( @NonNull EntityPropertyRegistry propertyRegistry ) {
		this.propertyRegistry = propertyRegistry;
	}

	private EntityPropertiesBinder() {
	}

	/**
	 * The original entity that this binder is attached to.
	 * If only an {@code entity} but no {@code target} set, this binder will behave as readonly.
	 */
	public void setEntity( Object entity ) {
		this.entity = entity;
	}

	/**
	 * The target for binding. If not set values can be fetched but binding-related controller methods will fail:
	 * applying values, validating and saving.
	 */
	public void setTarget( Object target ) {
		this.target = target;
	}

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
	 * @return always the exact {@link #valueBindingContext} field value
	 */
	private EntityPropertyBindingContext getLocalValueBindingContext() {
		return valueBindingContext;
	}

	/**
	 * @return a {@link EntityPropertyBindingContext} representing the target of this binder with changes applied
	 */
	public final EntityPropertyBindingContext asBindingContext() {
		return bindingContextMapper;
	}

	/**
	 * Get the property with the given name. The default value is ignored.
	 *
	 * @param key          property name
	 * @param defaultValue ignored
	 * @return property binder
	 */
	@Override
	public EntityPropertyBinder getOrDefault( Object key, EntityPropertyBinder defaultValue ) {
		return get( key );
	}

	/**
	 * Get the property with the given name.
	 * Will fetch the property descriptor and create the value holder with the current value.
	 * If there is no descriptor for that property, an {@link IllegalArgumentException} will be thrown.
	 *
	 * @param key property name
	 * @return property binder
	 */
	@Override
	public EntityPropertyBinder get( Object key ) {
		EntityPropertyBinder valueHolder = super.get( key );

		if ( valueHolder == null ) {
			try {
				String propertyName = (String) key;
				String fqPropertyName = parentProperty != null ? parentProperty.getName() + "." + propertyName : propertyName;

				val descriptor = propertyRegistry.getProperty( fqPropertyName );

				if ( descriptor == null ) {
					throw new IllegalArgumentException( "No such property descriptor: '" + fqPropertyName + "'" );
				}

				return get( descriptor );
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

	public EntityPropertyBinder get( @NonNull EntityPropertyDescriptor descriptor ) {
		try {
			if ( descriptor.isNestedProperty() ) {
				EntityPropertyDescriptor firstChild = parentProperty != null ? findDirectChild( descriptor, parentProperty ) : getRootDescriptor( descriptor );

				if ( firstChild == null ) {
					throw new IllegalArgumentException( "No such property descriptor: " + descriptor.getName() );
				}

				return getBinder( firstChild.getTargetPropertyName(), firstChild ).resolvePropertyBinder( descriptor );
			}

			return getBinder( descriptor.getName(), descriptor );
		}
		catch ( IllegalArgumentException iae ) {
			if ( !StringUtils.isEmpty( binderPrefix ) ) {
				PropertyChangeEvent pce = new PropertyChangeEvent( this, binderPrefix + "[" + descriptor.getName() + "]", null, null );
				throw new MethodInvocationException( pce, iae );
			}
			throw iae;
		}
	}

	private EntityPropertyBinder getBinder( String propertyName, EntityPropertyDescriptor descriptor ) {
		return computeIfAbsent( propertyName, p -> {
			AbstractEntityPropertyBinder binder = createPropertyBinder( descriptor );
			binder.setBinderPath( getPropertyBinderPath( propertyName ) );
			binder.enableBinding( bindingEnabled );
			return binder;
		} );
	}

	/**
	 * Signal that actual binding is enabled, this allows individual property binders to determine how they
	 * should interpret the current value. For example: if binding is enabled and a list value is requested,
	 * the list will only return the items that have been bound before, disregarding the possible currently
	 * stored value.
	 * <p/>
	 * In readonly mode this property will usually be kept {@code false}, simply returning the property values.
	 *
	 * @param bindingEnabled true to signal binding is enabled
	 */
	public void setBindingEnabled( boolean bindingEnabled ) {
		this.bindingEnabled = bindingEnabled;

		values().forEach( b -> b.enableBinding( bindingEnabled ) );
	}

	private void applyValuesIfNecessary() {
		if ( target != null ) {
			createController().applyValues();
		}
	}

	/**
	 * Create a new controller for this properties binder, allows for applying, validating and saving the configured binder properties.
	 *
	 * @return new controller instance
	 */
	public EntityPropertiesBinderController createController() {
		return new EntityPropertiesBinderController( this );
	}

	String getPropertyBinderPath( String propertyName ) {
		return StringUtils.defaultIfEmpty( binderPrefix, "properties" ) + "[" + propertyName + "]";
	}

	AbstractEntityPropertyBinder createPropertyBinder( EntityPropertyDescriptor descriptor ) {
		TypeDescriptor typeDescriptor = descriptor.getPropertyTypeDescriptor();
		EntityPropertyBindingType bindingType = EntityPropertyBindingType.forProperty( descriptor );

		switch ( bindingType ) {
			case MAP:
				val keyDescriptor = getOrCreateDescriptor( descriptor.getName() + EntityPropertyRegistry.MAP_KEY, typeDescriptor.getMapKeyTypeDescriptor() );
				val valueDescriptor = getOrCreateDescriptor( descriptor.getName() + EntityPropertyRegistry.MAP_VALUE,
				                                             typeDescriptor.getMapValueTypeDescriptor() );

				return new MapEntityPropertyBinder( this, descriptor, keyDescriptor, valueDescriptor );
			case COLLECTION:
				val memberDescriptor = getOrCreateDescriptor( descriptor.getName() + EntityPropertyRegistry.INDEXER,
				                                              typeDescriptor.getElementTypeDescriptor() );

				return new ListEntityPropertyBinder( this, descriptor, memberDescriptor );
			default:
				return new SingleEntityPropertyBinder( this, descriptor );
		}
	}

	private EntityPropertyDescriptor getOrCreateDescriptor( String name, TypeDescriptor expectedType ) {
		EntityPropertyDescriptor descriptor = propertyRegistry.getProperty( name );

		if ( descriptor == null ) {
			SimpleEntityPropertyDescriptor dummy = new SimpleEntityPropertyDescriptor( name );
			dummy.setPropertyTypeDescriptor( expectedType );
			dummy.setPropertyRegistry( propertyRegistry );
			descriptor = dummy;
		}

		return descriptor;
	}

	ChildPropertyPropertiesBinder createChildPropertyPropertiesBinder() {
		ChildPropertyPropertiesBinder childBinder = new ChildPropertyPropertiesBinder();
		childBinder.setPropertyRegistry( propertyRegistry );
		childBinder.setConversionService( conversionService );
		childBinder.setBindingEnabled( bindingEnabled );
		return childBinder;
	}

	Object createValue( EntityPropertyController controller ) {
		if ( controller != null ) {
			return controller.createValue( getValueBindingContext() );
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
			ConversionService conversionService = this.conversionService != null ? this.conversionService : DefaultConversionService.getSharedInstance();
			if ( conversionService != null ) {
				return conversionService.convert( source, TypeDescriptor.forObject( source ), targetType );
			}

			return targetType.getObjectType().cast( source );
		}
		catch ( ClassCastException | ConversionFailedException | ConverterNotFoundException cce ) {
			if ( !StringUtils.isEmpty( binderPrefix ) ) {
				PropertyChangeEvent pce = new PropertyChangeEvent( this, path, null, source );
				throw new ConversionNotSupportedException( pce, typeToReport, cce );
			}
			throw cce;
		}
	}

	/**
	 * Represents a child property binder, accessible through {@link SingleEntityPropertyBinder#getProperties()}.
	 */
	class ChildPropertyPropertiesBinder extends EntityPropertiesBinder
	{
		@Setter
		private boolean useLocalBindingContext;

		@Override
		EntityPropertyBindingContext getValueBindingContext() {
			if ( useLocalBindingContext ) {
				return super.getValueBindingContext();
			}
			return EntityPropertiesBinder.this.getValueBindingContext();
		}
	}

	class EntityPropertiesBinderValueBindingContext implements EntityPropertyBindingContext
	{
		@Override
		@SuppressWarnings("unchecked")
		public <U> U getEntity() {
			return (U) entity;
		}

		@Override
		@SuppressWarnings("unchecked")
		public <U> U getTarget() {
			return (U) ( target != null ? target : entity );
		}

		@Override
		public boolean isReadonly() {
			return target == null;
		}

		@Override
		public EntityPropertyBindingContext resolvePropertyBindingContext( EntityPropertyDescriptor propertyDescriptor ) {
			EntityPropertyBinder propertyBinder = get( propertyDescriptor );

			if ( propertyBinder instanceof SingleEntityPropertyBinder ) {
				return ( (SingleEntityPropertyBinder) propertyBinder ).getProperties().getLocalValueBindingContext();
			}

			return null;
		}

		@Override
		public EntityPropertyBindingContext resolvePropertyBindingContext( String propertyName, EntityPropertyController controller ) {
			EntityPropertyBinder propertyBinder = get( propertyName );

			if ( propertyBinder instanceof SingleEntityPropertyBinder ) {
				return ( (SingleEntityPropertyBinder) propertyBinder ).getProperties().getLocalValueBindingContext();
			}

			return null;
		}
	}

	/**
	 * Maps this binder as a valid {@link EntityPropertyBindingContext}. Using the binder as binding context
	 * will ensure that values will be retrieved from the binder when available.
	 */
	class EntityPropertiesBinderAsBindingContext implements EntityPropertyBindingContext
	{
		@Override
		@SuppressWarnings("unchecked")
		public <U> U getEntity() {
			return valueBindingContext.getEntity();
		}

		@Override
		@SuppressWarnings("unchecked")
		public <U> U getTarget() {
			applyValuesIfNecessary();
			return valueBindingContext.getTarget();
		}

		@Override
		public boolean isReadonly() {
			return valueBindingContext.isReadonly();
		}

		@Override
		@SuppressWarnings("unchecked")
		public <U> EntityPropertyValue<U> resolvePropertyValue( @NonNull EntityPropertyDescriptor propertyDescriptor ) {
			applyValuesIfNecessary();
			EntityPropertyBinder propertyBinder = get( propertyDescriptor );
			return propertyBinder != null
					? new EntityPropertyValue<U>( (U) propertyBinder.getOriginalValue(), (U) propertyBinder.getValue(), propertyBinder.isDeleted() )
					: null;
		}

		@Override
		@SuppressWarnings("unchecked")
		public <U> EntityPropertyValue<U> resolvePropertyValue( @NonNull String propertyName, EntityPropertyController<U> controller ) {
			applyValuesIfNecessary();
			EntityPropertyBinder propertyBinder = get( propertyName );
			return propertyBinder != null
					? new EntityPropertyValue<U>( (U) propertyBinder.getOriginalValue(), (U) propertyBinder.getValue(), propertyBinder.isDeleted() )
					: null;
		}

		@Override
		public EntityPropertyBindingContext resolvePropertyBindingContext( EntityPropertyDescriptor propertyDescriptor ) {
			EntityPropertyBinder propertyBinder = get( propertyDescriptor );
			return new EntityPropertyBinderBindingContext( this, propertyBinder );
		}

		@Override
		public EntityPropertyBindingContext resolvePropertyBindingContext( String propertyName, EntityPropertyController controller ) {
			EntityPropertyBinder propertyBinder = get( propertyName );
			return new EntityPropertyBinderBindingContext( this, propertyBinder );
		}
	}
}
