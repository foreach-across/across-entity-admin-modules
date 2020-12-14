package com.foreach.across.modules.experimental.webutility;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.core.context.configurer.ApplicationContextConfigurer;
import com.foreach.across.core.context.configurer.ComponentScanConfigurer;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.experimental.entitycontrols.EntityControlsModule;

import java.util.Set;

@AcrossDepends(required = EntityModule.NAME, optional = EntityControlsModule.NAME)
public class WebUtilityModule extends AcrossModule
{
	public static final String NAME = "WebUtilityModule";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	protected void registerDefaultApplicationContextConfigurers( Set<ApplicationContextConfigurer> contextConfigurers ) {
		contextConfigurers.add( ComponentScanConfigurer.forAcrossModule( WebUtilityModule.class ) );
	}
}