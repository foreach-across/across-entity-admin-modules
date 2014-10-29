package com.foreach.across.module.applicationinfo;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.core.annotations.AcrossRole;
import com.foreach.across.core.context.AcrossModuleRole;
import com.foreach.across.module.applicationinfo.business.ApplicationInfo;
import com.foreach.across.modules.properties.PropertiesModule;

@AcrossRole(AcrossModuleRole.INFRASTRUCTURE)
@AcrossDepends(
		required = PropertiesModule.NAME
)
public class ApplicationInfoModule extends AcrossModule
{
	public final static String NAME = "ApplicationInfoModule";

	private ApplicationInfo applicationInfo;

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return "Provides support for configuring both the running application and synchronizing remote application information.";
	}

	public ApplicationInfo getApplicationInfo() {
		return applicationInfo;
	}

	public void setApplicationInfo( ApplicationInfo applicationInfo ) {
		this.applicationInfo = applicationInfo;
	}
}
