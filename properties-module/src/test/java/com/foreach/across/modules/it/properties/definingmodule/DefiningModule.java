package com.foreach.across.modules.it.properties.definingmodule;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.modules.it.properties.definingmodule.installers.UserPropertiesInstaller;

/**
 * @author Arne Vandamme
 */
public class DefiningModule extends AcrossModule
{
	@Override
	public String getName() {
		return "DefiningModule";
	}

	@Override
	public String getDescription() {
		return "Defines a custom property map.";
	}

	@Override
	public Object[] getInstallers() {
		return new Object[] {
				UserPropertiesInstaller.class
		};
	}
}
