package com.across.samples.bootstrap.application.config;

import com.foreach.across.modules.adminweb.resource.AdminWebWebResources;
import com.foreach.across.modules.web.resource.WebResourcePackageManager;
import com.foreach.across.modules.web.resource.WebResourceRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;

import static com.foreach.across.modules.web.resource.WebResource.CSS;
import static com.foreach.across.modules.web.resource.WebResource.css;

@Configuration
public class BootstrapThemeConfiguration
{
	@Autowired
	void autoRegisterPackage( @Qualifier("adminWebResourcePackageManager") WebResourcePackageManager resourcePackageManager ) {
		resourcePackageManager.extendPackage( AdminWebWebResources.NAME,
		                                      WebResourceRule.add(
				                                      css( "@static:/bootstrapSample/theme/css/adminweb-bootstrap.css" ) )
		                                                     .toBucket( CSS )
		);
	}
}
