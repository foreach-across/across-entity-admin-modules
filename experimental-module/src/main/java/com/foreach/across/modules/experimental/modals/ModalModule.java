package com.foreach.across.modules.experimental.modals;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.core.context.bootstrap.AcrossBootstrapConfig;
import com.foreach.across.core.context.bootstrap.ModuleBootstrapConfig;
import com.foreach.across.core.context.configurer.ApplicationContextConfigurer;
import com.foreach.across.core.context.configurer.ComponentScanConfigurer;
import com.foreach.across.modules.bootstrapui.BootstrapUiModule;
import com.foreach.across.modules.entity.EntityModule;

import java.util.Set;

@AcrossDepends(required = { BootstrapUiModule.NAME, EntityModule.NAME })
public class ModalModule extends AcrossModule
{
	public static final String NAME = "ModalModule";
	public static final String RESOURCES = "modal";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return "Enables modal support, including ease of configuration for default CUD views";
	}

	@Override
	public String getResourcesKey() {
		return RESOURCES;
	}

	@Override
	protected void registerDefaultApplicationContextConfigurers( Set<ApplicationContextConfigurer> contextConfigurers ) {
	}

	@Override
	public void prepareForBootstrap( ModuleBootstrapConfig currentModule, AcrossBootstrapConfig contextConfig ) {
		contextConfig.extendModule( EntityModule.NAME, ComponentScanConfigurer.forAcrossModule( getClass() ) );
	}
}
