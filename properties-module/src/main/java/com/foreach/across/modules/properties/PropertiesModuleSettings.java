package com.foreach.across.modules.properties;

/**
 * @author Arne Vandamme
 */
public interface PropertiesModuleSettings
{
	/**
	 * Explicitly specify the default ConversionService instance to use for property maps.
	 *
	 * Value: {@link org.springframework.core.convert.ConversionService}
	 */
	public static String CONVERSION_SERVICE = "propertiesModule.conversionService";

	/**
	 * Specify the name fo the ConversionService bean to use for the property maps.
	 * If a bean name is specified but the bean is not found, the module will not bootstrap.
	 *
	 * Value: String
	 */
	public static String CONVERSION_SERVICE_BEAN = "propertiesModule.conversionService.beanName";
}
