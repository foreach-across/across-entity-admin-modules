package com.foreach.across.modules.properties.config;

import com.foreach.across.core.AcrossException;
import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.Module;
import com.foreach.across.core.context.info.AcrossModuleInfo;
import com.foreach.across.core.filters.ClassBeanFilter;
import com.foreach.across.modules.properties.PropertiesModuleSettings;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.Environment;
import org.springframework.format.support.DefaultFormattingConversionService;

/**
 * The PropertiesModule requires a ConversionService to be present.
 * If no ConversionService is detected or configured as property, one will be created and exposed.
 *
 * @author Arne Vandamme
 */
@Configuration
public class ConversionServiceConfiguration
{
	@Autowired
	@Module(AcrossModule.CURRENT_MODULE)
	private AcrossModuleInfo currentModuleInfo;

	@Autowired
	private Environment environment;

	@Autowired
	private ApplicationContext applicationContext;

	@Bean
	public ConversionService propertiesConversionService() {
		ConversionService conversionServiceToUse = environment.getProperty(
				PropertiesModuleSettings.CONVERSION_SERVICE,
				ConversionService.class
		);

		if ( conversionServiceToUse == null ) {
			String beanName = environment.getProperty( PropertiesModuleSettings.CONVERSION_SERVICE_BEAN );

			if ( beanName != null ) {
				try {
					conversionServiceToUse = (ConversionService) applicationContext.getBean( beanName );
				}
				catch ( NoSuchBeanDefinitionException nsbde ) {
					throw new AcrossException(
							"A ConversionService bean name was specified but there was a problem wiring it", nsbde );
				}
			}
			else {
				conversionServiceToUse = getConversionServiceBeanFromParent();

				if ( conversionServiceToUse == null ) {
					conversionServiceToUse = createDefaultConversionService();

				}
			}
		}

		return conversionServiceToUse;
	}

	private ConversionService createDefaultConversionService() {
		// If we are creating the default conversion service, make sure we expose it
		currentModuleInfo.getBootstrapConfiguration().addExposeFilter( new ClassBeanFilter( ConversionService.class ) );

		return new DefaultFormattingConversionService( true );
	}

	private ConversionService getConversionServiceBeanFromParent() {
		try {
			return applicationContext.getParent().getBean( ConversionService.class );
		}
		catch ( BeansException be ) {
			return null;
		}
	}

}
