package com.foreach.across.modules.experimental.export;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.core.context.configurer.ApplicationContextConfigurer;
import com.foreach.across.core.context.configurer.ComponentScanConfigurer;
import com.foreach.across.modules.entity.EntityModule;

import java.util.Set;

@AcrossDepends(required = EntityModule.NAME)
public class EntityExportModule extends AcrossModule
{
	public static final String NAME = "EntityExportModule";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	protected void registerDefaultApplicationContextConfigurers( Set<ApplicationContextConfigurer> contextConfigurers ) {
		contextConfigurers.add( ComponentScanConfigurer.forAcrossModule( this.getClass() ) );
	}
}