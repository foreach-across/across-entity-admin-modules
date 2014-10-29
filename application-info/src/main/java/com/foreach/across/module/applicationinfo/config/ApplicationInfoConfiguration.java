package com.foreach.across.module.applicationinfo.config;

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.module.applicationinfo.ApplicationInfoModule;
import com.foreach.across.module.applicationinfo.business.ApplicationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationInfoConfiguration
{
	@Autowired
	private ApplicationInfoModule module;

	@Bean
	@Exposed
	public ApplicationInfo runningApplicationInfo() {
		return module.getApplicationInfo();
	}
}
