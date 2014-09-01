package com.foreach.across.modules.properties;

import com.foreach.across.core.AcrossModuleSettings;
import com.foreach.across.core.AcrossModuleSettingsRegistry;
import org.springframework.core.convert.ConversionService;

/**
 * @author Arne Vandamme
 */
public class PropertiesModuleSettings extends AcrossModuleSettings
{
	/**
	 * Explicitly specify the default ConversionService instance to use for property maps.
	 * <p/>
	 * Value: {@link org.springframework.core.convert.ConversionService}
	 */
	public static final String CONVERSION_SERVICE = "propertiesModule.conversionService";

	/**
	 * Specify the name fo the ConversionService bean to use for the property maps.
	 * If a bean name is specified but the bean is not found, the module will not bootstrap.
	 * <p/>
	 * Value: String
	 */
	public static final String CONVERSION_SERVICE_BEAN = "propertiesModule.conversionService.beanName";

	@Override
	protected void registerSettings( AcrossModuleSettingsRegistry registry ) {
		registry.register( CONVERSION_SERVICE, ConversionService.class, null,
		                   "Explicitly specify the default ConversionService instance to use for property maps." );
		registry.register( CONVERSION_SERVICE_BEAN, String.class, null,
		                   "Specify the name fo the ConversionService bean to use for the property maps. " +
				                   "If a bean name is specified but the bean is not found, the module will not bootstrap." );
	}
}
