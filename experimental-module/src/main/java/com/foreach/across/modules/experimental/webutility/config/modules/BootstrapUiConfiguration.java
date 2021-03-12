package com.foreach.across.modules.experimental.webutility.config.modules;

import com.foreach.across.modules.entity.conditionals.ConditionalOnBootstrapUI;
import com.foreach.across.modules.experimental.webutility.icons.WebUtilityModuleIcons;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@ConditionalOnBootstrapUI
public class BootstrapUiConfiguration
{
	@PostConstruct
	public void registerIconSets() {
		WebUtilityModuleIcons.registerIconSet();
	}
}
