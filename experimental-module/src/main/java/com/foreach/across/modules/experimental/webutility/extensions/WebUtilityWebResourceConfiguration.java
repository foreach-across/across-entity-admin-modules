package com.foreach.across.modules.experimental.webutility.extensions;

import com.foreach.across.core.annotations.ModuleConfiguration;
import com.foreach.across.core.annotations.PostRefresh;
import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.modules.experimental.webutility.resource.WebUtilityModuleWebResources;
import com.foreach.across.modules.web.AcrossWebModule;
import com.foreach.across.modules.web.resource.WebResourcePackageManager;
import org.springframework.beans.factory.annotation.Autowired;

@ModuleConfiguration(AcrossWebModule.NAME)
class WebUtilityWebResourceConfiguration
{
	@Autowired
	@PostRefresh
	void registerWebResourcePackages( AcrossContextBeanRegistry contextBeanRegistry ) {
		contextBeanRegistry.getBeansOfType( WebResourcePackageManager.class ).forEach(
				packageManager -> {
					packageManager.register( WebUtilityModuleWebResources.NAME, new WebUtilityModuleWebResources() );
				}
		);
	}
}
