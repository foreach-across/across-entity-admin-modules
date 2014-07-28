package com.foreach.across.modules.it.properties.extendingmodule.installers;

import com.foreach.across.core.annotations.Installer;
import com.foreach.across.modules.properties.installers.EntityPropertiesInstaller;

/**
 * @author Arne Vandamme
 */
@Installer(description = "Creates client properties table", version = 1)
public class ClientPropertiesInstaller extends EntityPropertiesInstaller
{
	@Override
	protected String getTableName() {
		return "client_properties";
	}

	@Override
	protected String getKeyColumnName() {
		return "client_id";
	}
}
