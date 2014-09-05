package com.foreach.across.modules.it.properties.definingmodule;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.modules.it.properties.definingmodule.installers.RevisionPropertiesInstaller;
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
		return "Defines a two custom property sets: one revision based, one not.";
	}

	@Override
	public Object[] getInstallers() {
		return new Object[] {
				UserPropertiesInstaller.class,
				RevisionPropertiesInstaller.class
		};
	}
}
