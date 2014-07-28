package com.foreach.across.modules.it.properties.definingmodule.installers;

import com.foreach.across.core.annotations.Installer;
import com.foreach.across.modules.properties.installers.EntityPropertiesInstaller;

/**
 * @author Arne Vandamme
 */
@Installer(description = "Creates user properties table", version = 1)
public class UserPropertiesInstaller extends EntityPropertiesInstaller
{
	@Override
	protected String getTableName() {
		return "user_properties";
	}

	@Override
	protected String getKeyColumnName() {
		return "user_id";
	}
}
