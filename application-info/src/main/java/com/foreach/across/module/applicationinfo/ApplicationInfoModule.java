package com.foreach.across.module.applicationinfo;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.AcrossRole;
import com.foreach.across.core.context.AcrossModuleRole;
import org.springframework.core.Ordered;

import java.util.Date;

@AcrossRole(value = AcrossModuleRole.APPLICATION, order = Ordered.HIGHEST_PRECEDENCE)
public class ApplicationInfoModule extends AcrossModule
{
	public final static String NAME = "ApplicationInfoModule";

	private final Date configurationDate;

	public ApplicationInfoModule() {
		configurationDate = new Date();
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return "Provides support for configuring both the running application and synchronizing remote application information.";
	}

	/**
	 * If no startup date is specified, the configuration timestamp for the module will be considered
	 * the initial startup date.
	 *
	 * @return Timestamp when the module was configured.
	 */
	public Date getConfigurationDate() {
		return configurationDate;
	}
}
