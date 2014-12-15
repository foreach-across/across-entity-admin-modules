package com.foreach.across.module.applicationinfo.config.modules;

import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.module.applicationinfo.controllers.ApplicationInfoController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AcrossDepends(required = "DebugWebModule")
@Configuration
public class DebugWebConfiguration
{
	@Bean
	public ApplicationInfoController applicationInfoController() {
		return new ApplicationInfoController();
	}
}
