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
package com.foreach.across.modules.properties.registries;

import com.foreach.across.core.context.AcrossModuleEntity;
import com.foreach.across.modules.properties.config.EntityPropertiesDescriptor;
import com.foreach.across.modules.properties.repositories.PropertyTrackingRepository;
import com.foreach.common.spring.properties.PropertyFactory;
import com.foreach.common.spring.properties.PropertyTypeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.Assert;

import java.util.Collection;

/**
 * Allows modules to register extension properties.  An EntityPropertiesRegistry requires
 * the {@link com.foreach.across.modules.properties.repositories.PropertyTrackingRepository} to be active
 * and exposed.  The latter is automatically the case if the PropertiesModule is configured correctly.
 * <p/>
 * The backing implementation uses a {@link com.foreach.common.spring.properties.PropertyTypeRegistry} and
 * {@link com.foreach.common.spring.properties.TypedPropertyMap} supporting both simple and parameterized types.
 *
 * @author Arne Vandamme
 * @see com.foreach.common.spring.properties.PropertyTypeRegistry
 * @see com.foreach.common.spring.properties.TypedPropertyMap
 * @see org.springframework.core.convert.TypeDescriptor
 */
public abstract class EntityPropertiesRegistry
{
	private final Logger LOG = LoggerFactory.getLogger( getClass() );

	private final PropertyTypeRegistry<String> propertyTypeRegistry;

	private final EntityPropertiesDescriptor descriptor;
	private final PropertyTrackingRepository propertyTrackingRepository;

	protected EntityPropertiesRegistry( EntityPropertiesDescriptor descriptor ) {
		this( descriptor, null );
	}

	protected EntityPropertiesRegistry( EntityPropertiesDescriptor descriptor, Class classForUnknownProperties ) {
		this.descriptor = descriptor;
		this.propertyTypeRegistry = classForUnknownProperties != null
				? new PropertyTypeRegistry<String>( classForUnknownProperties, descriptor.conversionService() )
				: new PropertyTypeRegistry<String>( descriptor.conversionService() );
		this.propertyTrackingRepository = descriptor.trackingRepository();

		Assert.notNull( descriptor.conversionService(), "EntityPropertiesRegistry requires a valid ConversionService" );
	}

	/**
	 * Get the actual PropertyTypeRegistry implementation being managed.
	 *
	 * @return PropertyTypeRegistry instance.
	 */
	public PropertyTypeRegistry<String> getPropertyTypeRegistry() {
		return propertyTypeRegistry;
	}

	/**
	 * Get the default ConversionService that is being used for all properties that do not
	 * have their own ConversionService registered.
	 *
	 * @return ConversionService instance.
	 */
	public ConversionService getDefaultConversionService() {
		return propertyTypeRegistry.getDefaultConversionService();
	}

	/**
	 * Register a custom entity property.
	 *
	 * @param acrossModule  The module adding the property to the registry.
	 * @param propertyKey   Property key.
	 * @param propertyClass Property type.
	 */
	public void register( AcrossModuleEntity acrossModule, String propertyKey, Class<?> propertyClass ) {
		register( acrossModule, propertyKey, TypeDescriptor.valueOf( propertyClass ) );
	}

	/**
	 * Register a custom entity property.
	 *
	 * @param acrossModule The module adding the property to the registry.
	 * @param propertyKey  Property key.
	 * @param propertyType Property type.
	 */
	public void register( AcrossModuleEntity acrossModule, String propertyKey, TypeDescriptor propertyType ) {
		register( acrossModule.getName(), propertyKey, propertyType, null, null );
	}

	/**
	 * Register a custom entity property.
	 *
	 * @param acrossModule  The module adding the property to the registry.
	 * @param propertyKey   Property key.
	 * @param propertyClass Property type.
	 * @param defaultValue  Factory creating a default value when fetching unset property.
	 */
	public <A> void register( AcrossModuleEntity acrossModule,
	                          String propertyKey,
	                          Class<A> propertyClass,
	                          PropertyFactory<String, A> defaultValue ) {
		register( acrossModule, propertyKey, TypeDescriptor.valueOf( propertyClass ), defaultValue );
	}

	/**
	 * Register a custom entity property.
	 *
	 * @param acrossModule The module adding the property to the registry.
	 * @param propertyKey  Property key.
	 * @param propertyType Property type.
	 * @param defaultValue Factory creating a default value when fetching unset property.
	 */

	public void register( AcrossModuleEntity acrossModule,
	                      String propertyKey,
	                      TypeDescriptor propertyType,
	                      PropertyFactory<String, ?> defaultValue ) {
		register( acrossModule.getName(), propertyKey, propertyType, defaultValue, null );
	}

	/**
	 * Register a custom entity property.
	 *
	 * @param acrossModule      The module adding the property to the registry.
	 * @param propertyKey       Property key.
	 * @param propertyClass     Property type.
	 * @param defaultValue      Factory creating a default value when fetching unset property.
	 * @param conversionService ConversionService to use on set and get of this property.
	 */
	public <A> void register( AcrossModuleEntity acrossModule,
	                          String propertyKey,
	                          Class<A> propertyClass,
	                          PropertyFactory<String, A> defaultValue,
	                          ConversionService conversionService ) {
		register( acrossModule, propertyKey, TypeDescriptor.valueOf( propertyClass ), defaultValue, conversionService );
	}

	/**
	 * Register a custom entity property.
	 *
	 * @param acrossModule      The module adding the property to the registry.
	 * @param propertyKey       Property key.
	 * @param propertyType      Property type.
	 * @param defaultValue      Factory creating a default value when fetching unset property.
	 * @param conversionService ConversionService to use on set and get of this property.
	 */
	public <A> void register( AcrossModuleEntity acrossModule,
	                          String propertyKey,
	                          TypeDescriptor propertyType,
	                          PropertyFactory<String, A> defaultValue,
	                          ConversionService conversionService ) {
		register( acrossModule.getName(), propertyKey, propertyType, defaultValue, conversionService );
	}

	private void trackProperty( String owner, String propertyKey ) {
		if ( propertyTrackingRepository != null ) {
			try {
				propertyTrackingRepository.register( owner, descriptor, propertyKey );
			}
			catch ( Exception e ) {
				LOG.warn( "Tracking property registration failed", e );
			}
		}
	}

	private <A> void register( String owner,
	                           String propertyKey,
	                           TypeDescriptor typeDescriptor,
	                           PropertyFactory<String, A> propertyValue,
	                           ConversionService conversionService ) {
		Assert.notNull( owner, "An owner is required for a registered property." );

		trackProperty( owner, propertyKey );

		propertyTypeRegistry.register( propertyKey, typeDescriptor, propertyValue, conversionService );
	}

	/**
	 * Removes a property from the registry.  This will not delete any of the stored values, only remove
	 * the configured type and conversion configuration.  Any future actions done using this property key
	 * will revert to the default configuration being used.
	 *
	 * @param propertyKey Property key that should be unregistered.
	 */
	public void unregister( String propertyKey ) {
		propertyTypeRegistry.unregister( propertyKey );
	}

	/**
	 * Get the type associated with this property.  If the property is known the registered type will
	 * be returned, else the registered type for unknown properties will be used. Returns the
	 * {@link org.springframework.core.convert.TypeDescriptor} that can represent parameterized types.
	 *
	 * @param propertyKey Property key.
	 * @return TypeDescriptor for the given property.
	 */
	public TypeDescriptor getTypeForProperty( String propertyKey ) {
		return propertyTypeRegistry.getTypeForProperty( propertyKey );
	}

	/**
	 * Will return the default value for a given property.  If a PropertyFactory is registered,
	 * it will be invoked to create the value instance, else null will be returned.
	 *
	 * @param propertyKey Property key.
	 * @return Instance or null if none.
	 */
	public Object getDefaultValueForProperty( String propertyKey ) {
		return propertyTypeRegistry.getDefaultValueForProperty( propertyKey );
	}

	/**
	 * @return The class assumed for unknown properties.
	 */
	public Class getClassForUnknownProperties() {
		return propertyTypeRegistry.getClassForUnknownProperties();
	}

	/**
	 * Set the class that unknown properties are assumed to be.
	 * Unless a more specific type is specified, a property will always be
	 * converted to this type.
	 * <p/>
	 * Only supports simple types.
	 *
	 * @param classForUnknownProperties Class that unknown
	 */
	public void setClassForUnknownProperties( Class classForUnknownProperties ) {
		propertyTypeRegistry.setClassForUnknownProperties( classForUnknownProperties );
	}

	/**
	 * @param propertyKey Property key.
	 * @return True if a property is registered under that key.
	 */
	public boolean isRegistered( String propertyKey ) {
		return propertyTypeRegistry.isRegistered( propertyKey );
	}

	/**
	 * @return The set of known properties.
	 */
	public Collection<String> getRegisteredProperties() {
		return propertyTypeRegistry.getRegisteredProperties();
	}

	/**
	 * @return True if no properties are registered.
	 */
	public boolean isEmpty() {
		return propertyTypeRegistry.isEmpty();
	}

	/**
	 * Clears the entire registry, removing all known properties.
	 */
	public void clear() {
		propertyTypeRegistry.clear();
	}
}
