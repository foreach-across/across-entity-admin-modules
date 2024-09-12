package com.foreach.across.modules.experimental.webutility.config.modules;

import com.foreach.across.modules.entity.conditionals.ConditionalOnBootstrapUI;
import com.foreach.across.modules.experimental.webutility.icons.WebUtilityModuleIcons;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnBootstrapUI
public class BootstrapUiConfiguration
{
	@PostConstruct
	public void registerIconSets() {
		WebUtilityModuleIcons.registerIconSet();
	}
}
