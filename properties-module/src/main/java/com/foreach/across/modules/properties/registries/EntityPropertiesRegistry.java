package com.foreach.across.modules.properties.registries;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.context.info.AcrossModuleInfo;
import com.foreach.spring.util.PropertyTypeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.Assert;

import java.util.Collection;

/**
 * Allows modules to register extension properties.
 *
 * @author Arne Vandamme
 */
public abstract class EntityPropertiesRegistry
{
	private final Logger LOG = LoggerFactory.getLogger( getClass() );

	private final PropertyTypeRegistry<String> propertyTypeRegistry;
	private final ConversionService conversionService;

	protected EntityPropertiesRegistry( ConversionService conversionService ) {
		this( null, conversionService );
	}

	protected EntityPropertiesRegistry( Class classForUnknownProperties, ConversionService conversionService ) {
		Assert.notNull( conversionService, "EntityPropertiesRegistry requires a valid ConversionService" );
		this.propertyTypeRegistry = classForUnknownProperties != null
				? new PropertyTypeRegistry<String>( classForUnknownProperties )
				: new PropertyTypeRegistry<String>();
		this.conversionService = conversionService;
	}

	public PropertyTypeRegistry<String> getPropertyTypeRegistry() {
		return propertyTypeRegistry;
	}

	public ConversionService getConversionService() {
		return conversionService;
	}

	public void register( AcrossModuleInfo acrossModule, String propertyKey, Class propertyClass ) {
		register( acrossModule.getName(), propertyKey, propertyClass );
	}

	public void register( AcrossModule acrossModule, String propertyKey, Class propertyClass ) {
		register( acrossModule.getName(), propertyKey, propertyClass );
	}

	private void register( String owner, String propertyKey, Class propertyClass ) {
		Assert.notNull( owner, "Only AcrossModules can register properties." );
		LOG.debug( "Registering property {} for module {}", propertyKey, owner );

		propertyTypeRegistry.register( propertyKey, propertyClass );
	}

	public <A> void register( AcrossModuleInfo acrossModule,
	                          String propertyKey,
	                          Class<A> propertyClass,
	                          A propertyValue ) {
		register( acrossModule.getName(), propertyKey, propertyClass, propertyValue );
	}

	public <A> void register( AcrossModule acrossModule, String propertyKey, Class<A> propertyClass, A propertyValue ) {
		register( acrossModule.getName(), propertyKey, propertyClass, propertyValue );
	}

	private <A> void register( String owner, String propertyKey, Class<A> propertyClass, A propertyValue ) {
		Assert.notNull( owner, "Only AcrossModules can register properties." );
		LOG.debug( "Registering property {} for module {}", propertyKey, owner );

		propertyTypeRegistry.register( propertyKey, propertyClass, propertyValue );
	}

	public void unregister( String propertyKey ) {
		propertyTypeRegistry.unregister( propertyKey );
	}

	public Class getClassForProperty( String propertyKey ) {
		return propertyTypeRegistry.getClassForProperty( propertyKey );
	}

	public Object getDefaultValueForProperty( String propertyKey ) {
		return propertyTypeRegistry.getDefaultValueForProperty( propertyKey );
	}

	public Class getClassForUnknownProperties() {
		return propertyTypeRegistry.getClassForUnknownProperties();
	}

	public void setClassForUnknownProperties( Class classForUnknownProperties ) {
		propertyTypeRegistry.setClassForUnknownProperties( classForUnknownProperties );
	}

	public boolean isRegistered( String propertyKey ) {
		return propertyTypeRegistry.isRegistered( propertyKey );
	}

	public Collection<String> getRegisteredProperties() {
		return propertyTypeRegistry.getRegisteredProperties();
	}

	public boolean isEmpty() {
		return propertyTypeRegistry.isEmpty();
	}

	public void clear() {
		propertyTypeRegistry.clear();
	}
}
