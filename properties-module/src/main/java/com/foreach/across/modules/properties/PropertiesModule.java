package com.foreach.across.modules.properties;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.core.context.configurer.AnnotatedClassConfigurer;
import com.foreach.across.core.context.configurer.ApplicationContextConfigurer;
import com.foreach.across.modules.properties.config.ConversionServiceConfiguration;

import java.util.Set;

/**
 * @author Arne Vandamme
 */
@AcrossDepends(optional = "AcrossWebModule")
public class PropertiesModule extends AcrossModule
{
	public static final String NAME = "PropertiesModule";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return "Provides facilities for both global and entity linked property maps that can be extended by other modules.";
	}

	@Override
	protected void registerDefaultApplicationContextConfigurers( Set<ApplicationContextConfigurer> contextConfigurers ) {
		contextConfigurers.add( new AnnotatedClassConfigurer( ConversionServiceConfiguration.class ) );
	}
}
