package com.foreach.across.modules.properties.installers;

import com.foreach.across.core.annotations.Installer;
import com.foreach.across.core.installers.AcrossLiquibaseInstaller;

/**
 * @author Arne Vandamme
 */
@Installer(description = "Installs the property tracking table", version = 1)
public class PropertyTrackingSchemaInstaller extends AcrossLiquibaseInstaller
{
}
