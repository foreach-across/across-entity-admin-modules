package com.foreach.across.modules.it.properties.extendingmodule;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.modules.it.properties.extendingmodule.installers.ClientPropertiesInstaller;

/**
 * @author Arne Vandamme
 */
@AcrossDepends(required = "DefiningModule")
public class ExtendingModule extends AcrossModule
{
	@Override
	public String getName() {
		return "ExtendingModule";
	}

	@Override
	public String getDescription() {
		return "Extends the UserProperties with some custom properties.";
	}

	@Override
	public Object[] getInstallers() {
		return new Object[] {
				ClientPropertiesInstaller.class
		};
	}
}
