package com.foreach.across.modules.it.properties.definingmodule.installers;

import com.foreach.across.core.annotations.Installer;
import com.foreach.across.modules.properties.installers.RevisionBasedEntityPropertiesInstaller;

/**
 * @author Arne Vandamme
 */
@Installer(description = "Creates revision properties table", version = 1)
public class RevisionPropertiesInstaller extends RevisionBasedEntityPropertiesInstaller
{
	@Override
	protected String getTableName() {
		return "revision_properties";
	}

	@Override
	protected String getKeyColumnName() {
		return "revision_id";
	}
}
