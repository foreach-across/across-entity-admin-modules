package com.foreach.across.modules.adminwebthemes;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.core.context.configurer.ApplicationContextConfigurer;
import com.foreach.across.modules.adminweb.AdminWebModule;

import java.util.Set;

/**
 * Contains the default AdminWebModule themes.
 */
@AcrossDepends(required = AdminWebModule.NAME)
public class AdminWebThemesModule extends AcrossModule
{
	public static final String NAME = "AdminWebThemesModule";
	public static final String RESOURCES_KEY = "adminweb-themes";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getResourcesKey() {
		return RESOURCES_KEY;
	}

	@Override
	protected void registerDefaultApplicationContextConfigurers( Set<ApplicationContextConfigurer> contextConfigurers ) {
	}
}
