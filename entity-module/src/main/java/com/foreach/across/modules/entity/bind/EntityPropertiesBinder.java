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
import com.foreach.across.modules.entity.registry.properties.binding.SimpleEntityPropertyBindingContext;
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
import java.util.Map;

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
 * WARNING: The current implementation has some limitations. Especially the fact that a single {@link EntityPropertiesBinder}
 * is strongly attached to a single entity or target. You cannot use the same binder and apply its values to multiple targets.
 * Resetting the binder is done manually by calling {@link #clear()}, or automatically by updating either {@link #setEntity(Object)}
 * or {@link #setTarget(Object)}.
 *
 * @author Arne Vandamme
 * @see EntityPropertiesBinderController
 * @see EntityPropertyControlName
 * @since 3.2.0
 */
public class EntityPropertiesBinder extends HashMap<String, EntityPropertyBinder>
{
	private final EntityPropertiesBinderAsBindingContext bindingContextMapper = new EntityPropertiesBinderAsBindingContext();
	private final Map<String, EntityPropertyBinderHolder> proxyBinders = new HashMap<>();

	@Getter
	private Object entity;

	@Getter
	private Object target;

	/**
	 * Have any property binders had their values updated?
	 */
	@Getter(value = AccessLevel.PACKAGE)
	@Setter(value = AccessLevel.PACKAGE)
	private boolean dirty;

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
	 * The resolver that should be used for determining template values of properties: list item and map entry templates.
	 */
	@Setter
	@Getter
	@NonNull
	private EntityPropertyTemplateValueResolver templateValueResolver = new DefaultEntityPropertyTemplateValueResolver();

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
	 * Optionally set a {@link EntityPropertiesBinderCache} that dealt out this instance.
	 * If a cache is set, properties will be inspected for optimized bulk fetching and will
	 * be fetched for all items in the cache.
	 */
	@Setter(value = AccessLevel.PACKAGE)
	@Getter(value = AccessLevel.PACKAGE)
	private EntityPropertiesBinderCache cache;

	@Getter
	private boolean bindingEnabled;

	/**
	 * This is the internal binding context that should be used for applying the actual values.
	 * Note that this is different from the value of {@link #asBindingContext()}. The latter is meant
	 * to be used outside the binder for value retrieval, the {@code valueBindingContext} is only
	 * for internal use within the binder when applying the actual values.
	 */
	@Getter(value = AccessLevel.PACKAGE)
	private final EntityPropertiesBinderValueBindingContext valueBindingContext = new EntityPropertiesBinderValueBindingContext( true );

	public EntityPropertiesBinder( @NonNull EntityPropertyRegistry propertyRegistry ) {
		this.propertyRegistry = propertyRegistry;
	}

	private EntityPropertiesBinder() {
	}

	/**
	 * The original entity that this binder is attached to.
	 * If only an {@code entity} but no {@code target} set, this binder will behave as readonly.
	 * <p/>
	 * <strong>WARNING:</strong> Updating the entity will reset the entire binder as if for a new instance.
	 */
	public void setEntity( Object entity ) {
		this.entity = entity;
		clear();
	}

	/**
	 * The target for binding. If not set values can be fetched but binding-related controller methods will fail:
	 * applying values, validating and saving.
	 * <p/>
	 * <strong>WARNING:</strong> Updating the target will reset the entire binder as if for a new instance.
	 */
	public void setTarget( Object target ) {
		this.target = target;
		clear();
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
		return get( key, true );
	}

	/**
	 * Get the property binder for the descriptor. This will automatically register the binder.
	 *
	 * @param descriptor property descriptor
	 * @return property binder
	 */
	public EntityPropertyBinder get( @NonNull EntityPropertyDescriptor descriptor ) {
		return get( descriptor, true );
	}

	EntityPropertyBinder get( Object key, boolean autoRegister ) {
		EntityPropertyBinder valueHolder = super.get( key );

		if ( valueHolder == null && !autoRegister ) {
			valueHolder = proxyBinders.get( key );
		}

		if ( valueHolder == null ) {
			try {
				String propertyName = (String) key;
				String fqPropertyName = parentProperty != null ? parentProperty.getName() + "." + propertyName : propertyName;

				val descriptor = propertyRegistry.getProperty( fqPropertyName );

				if ( descriptor == null ) {
					throw new IllegalArgumentException( "No such property descriptor: '" + fqPropertyName + "'" );
				}

				return get( descriptor, autoRegister );
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

	private EntityPropertyBinder get( EntityPropertyDescriptor descriptor, boolean autoRegister ) {
		try {
			if ( descriptor.isNestedProperty() ) {
				EntityPropertyDescriptor firstChild = parentProperty != null ? findDirectChild( descriptor, parentProperty ) : getRootDescriptor( descriptor );

				if ( firstChild == null ) {
					throw new IllegalArgumentException( "No such property descriptor: " + descriptor.getName() );
				}

				EntityPropertyBinder binder = getBinder( firstChild.getTargetPropertyName(), firstChild, autoRegister );

				if ( firstChild == descriptor ) {
					return binder;
				}

				if ( binder instanceof SingleEntityPropertyBinder ) {
					return ( (SingleEntityPropertyBinder) binder ).getProperties().get( descriptor, autoRegister );
				}

				return binder.resolvePropertyBinder( descriptor );
			}

			return getBinder( descriptor.getName(), descriptor, autoRegister );
		}
		catch ( IllegalArgumentException iae ) {
			if ( !StringUtils.isEmpty( binderPrefix ) ) {
				PropertyChangeEvent pce = new PropertyChangeEvent( this, binderPrefix + "[" + descriptor.getName() + "]", null, null );
				throw new MethodInvocationException( pce, iae );
			}
			throw iae;
		}
	}

	private EntityPropertyBinder getBinder( String propertyName, EntityPropertyDescriptor descriptor, boolean autoRegister ) {
		EntityPropertyBinder propertyBinder = super.get( propertyName );

		if ( propertyBinder == null ) {
			if ( autoRegister || EntityPropertyHandlingType.forProperty( descriptor ) != EntityPropertyHandlingType.DIRECT ) {
				AbstractEntityPropertyBinder binder = createPropertyBinder( descriptor );
				binder.setBinderPath( getPropertyBinderPath( propertyName ) );
				binder.enableBinding( bindingEnabled );
				put( propertyName, binder );

				EntityPropertyBinderHolder holder = proxyBinders.get( propertyName );

				// upgrade a previously dealt out binder
				if ( holder != null ) {
					holder.setTarget( binder );
					proxyBinders.remove( propertyName );
				}

				return binder;
			}

			return proxyBinders.computeIfAbsent( propertyName, p -> {
				EntityPropertyBinderHolder holder = new EntityPropertyBinderHolder();
				holder.setTarget( new DirectPropertyEntityPropertyBinder( getValueBindingContext().forDirectProperties(), descriptor ) );
				return holder;
			} );
		}

		return propertyBinder;
	}

	/**
	 * Signal that actual binding is enabled, this allows individual property binders to determine how they
	 * should interpret the current value. For example: if binding is enabled and a list value is requested,
	 * the list will only return the items that have been bound before, disregarding the currently stored value.
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

	@Override
	public void clear() {
		super.clear();
		setDirty( false );
	}

	void markDirty() {
		dirty = true;
	}

	/**
	 * @return true if the binder does not have a target set
	 */
	public boolean isReadonly() {
		return valueBindingContext.isReadonly();
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
		childBinder.setTemplateValueResolver( templateValueResolver );
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

		@Setter
		private SingleEntityPropertyBinder owningPropertyBinder;

		@Override
		EntityPropertiesBinderValueBindingContext getValueBindingContext() {
			if ( useLocalBindingContext ) {
				return super.getValueBindingContext();
			}
			return EntityPropertiesBinder.this.getValueBindingContext();
		}

		@Override
		void markDirty() {
			super.markDirty();
			owningPropertyBinder.markDirty();
		}

		@Override
		public EntityPropertiesBinderController createController() {
			if ( EntityPropertiesBinder.this.isReadonly() ) {
				throw new ReadonlyBindingContextException();
			}
			return super.createController();
		}

		@Override
		public boolean isReadonly() {
			return EntityPropertiesBinder.this.isReadonly();
		}
	}

	@RequiredArgsConstructor
	class EntityPropertiesBinderValueBindingContext implements EntityPropertyBindingContext
	{
		private final boolean autoRegisterPropertyBinders;

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
			EntityPropertyDescriptor parentProperty = getParentProperty();

			if ( parentProperty != null && StringUtils.equals( parentProperty.getName(), propertyDescriptor.getName() ) ) {
				return this;
			}

			EntityPropertyBinder propertyBinder = get( propertyDescriptor, autoRegisterPropertyBinders );

			EntityPropertyBinder targetBinder = propertyBinder instanceof EntityPropertyBinderHolder
					? ( (EntityPropertyBinderHolder) propertyBinder ).getTarget() : propertyBinder;

			if ( targetBinder instanceof SingleEntityPropertyBinder ) {
				return ( (SingleEntityPropertyBinder) targetBinder ).getProperties().getLocalValueBindingContext();
			}
			else if ( targetBinder instanceof DirectPropertyEntityPropertyBinder ) {
				return SimpleEntityPropertyBindingContext.builder()
				                                         .entity( propertyBinder.getValue() ).target( propertyBinder.getValue() ).readonly( isReadonly() )
				                                         .build();
			}

			return null;
		}

		/**
		 * Create a new binding context that will not automatically register property binders, but can be
		 * used to resolve a nested binding context for a direct property binder.
		 */
		EntityPropertyBindingContext forDirectProperties() {
			return new EntityPropertiesBinderValueBindingContext( false );
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
			return createPropertyValue( get( propertyDescriptor, false ) );
		}

		@SuppressWarnings("unchecked")
		private <U> EntityPropertyValue<U> createPropertyValue( EntityPropertyBinder propertyBinder ) {
			if ( propertyBinder != null ) {
				if ( isReadonly() ) {
					U originalValue = (U) propertyBinder.getOriginalValue();
					return new EntityPropertyValue<>( originalValue, originalValue, false );
				}

				return new EntityPropertyValue<>( (U) propertyBinder.getOriginalValue(), (U) propertyBinder.getValue(), propertyBinder.isDeleted() );
			}

			return null;
		}

		@Override
		public EntityPropertyBindingContext resolvePropertyBindingContext( EntityPropertyDescriptor propertyDescriptor ) {
			EntityPropertyBinder propertyBinder = get( propertyDescriptor, false );
			return new EntityPropertyBinderBindingContext( this, propertyBinder );
		}
	}

	static class ReadonlyBindingContextException extends IllegalStateException
	{
		ReadonlyBindingContextException() {
			super( "Unable to perform EntityPropertiesBinderController actions - no target has been set" );
		}
	}
}
