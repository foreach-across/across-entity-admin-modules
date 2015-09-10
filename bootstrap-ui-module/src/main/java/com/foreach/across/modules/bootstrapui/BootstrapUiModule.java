package com.foreach.across.modules.bootstrapui;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.modules.web.AcrossWebModule;

@AcrossDepends(required = AcrossWebModule.NAME)
public class BootstrapUiModule extends AcrossModule
{
	public static final String NAME = "BootstrapUiModule";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return "Provides infrastructure and components for building a Bootstrap based web interface.";
	}
}
