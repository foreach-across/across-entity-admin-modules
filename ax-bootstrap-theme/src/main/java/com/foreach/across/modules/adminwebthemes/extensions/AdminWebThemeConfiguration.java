package com.foreach.across.modules.adminwebthemes.extensions;

import com.foreach.across.core.annotations.ModuleConfiguration;
import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.adminwebthemes.layout.AdminWebClassicLayoutTemplate;
import com.foreach.across.modules.adminwebthemes.layout.AdminWebFixedSidebarLayoutTemplate;
import com.foreach.across.modules.adminwebthemes.layout.AdminWebSidebarLayoutTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * Configures the actual theme to be used by AdminWebModule.
 *
 * @author Arne Vandamme
 * @since 0.0.1
 */
@Slf4j
@ModuleConfiguration(AdminWebModule.NAME)
public class AdminWebThemeConfiguration
{
	/**
	 * Name of the template bean whose bean definition should be overridden.
	 */
	private static final String TEMPLATE_BEAN_NAME = "adminWebLayoutTemplate";

	@Bean(TEMPLATE_BEAN_NAME)
	@ConditionalOnProperty(value = "admin-web-module.theme", havingValue = "sidebar", matchIfMissing = true)
	public AdminWebSidebarLayoutTemplate sidebarLayoutTemplate() {
		LOG.info( "Using AdminWebModule theme: sidebar" );
		return new AdminWebSidebarLayoutTemplate();
	}

	@Bean(TEMPLATE_BEAN_NAME)
	@ConditionalOnProperty(value = "admin-web-module.theme", havingValue = "classic")
	public AdminWebClassicLayoutTemplate classicLayoutTemplate() {
		LOG.info( "Using AdminWebModule theme: classic" );
		return new AdminWebClassicLayoutTemplate();
	}

	@Bean(TEMPLATE_BEAN_NAME)
	@ConditionalOnProperty(value = "admin-web-module.theme", havingValue = "sidebar-fixed")
	public AdminWebFixedSidebarLayoutTemplate fixedSidebarLayoutTemplate() {
		LOG.info( "Using AdminWebModule theme: sidebar-fixed" );
		return new AdminWebFixedSidebarLayoutTemplate();
	}
}