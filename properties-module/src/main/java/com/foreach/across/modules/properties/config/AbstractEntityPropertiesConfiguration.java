package com.foreach.across.modules.properties.config;

import com.foreach.across.core.annotations.Module;
import com.foreach.across.core.context.info.AcrossModuleInfo;
import com.foreach.across.modules.properties.PropertiesModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;

/**
 * Base configuration that provides access to the ConversionService configured on the PropertiesModule.
 *
 * @author Arne Vandamme
 */
public abstract class AbstractEntityPropertiesConfiguration
{
	@Autowired
	@Module(PropertiesModule.NAME)
	private AcrossModuleInfo propertiesModule;

	/**
	 * @return The ConversionService attached to the properties module.
	 */
	protected ConversionService conversionService() {
		return (ConversionService) propertiesModule.getApplicationContext().getBean(
				ConversionServiceConfiguration.CONVERSION_SERVICE_BEAN );
	}
}
