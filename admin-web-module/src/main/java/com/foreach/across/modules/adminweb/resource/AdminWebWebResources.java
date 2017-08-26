/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.foreach.across.modules.adminweb.resource;

import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.bootstrapui.resource.BootstrapUiWebResources;
import com.foreach.across.modules.web.AcrossWebModule;
import com.foreach.across.modules.web.context.WebAppPathResolver;
import com.foreach.across.modules.web.resource.SimpleWebResourcePackage;
import com.foreach.across.modules.web.resource.WebResource;
import com.foreach.across.modules.web.resource.WebResourcePackageManager;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Boostrap css, requires jquery as well.
 * Also registers some application paths as javascript data.
 */
@Component
public class AdminWebWebResources extends SimpleWebResourcePackage
{
	public static final String NAME = "bootstrap-adminweb";
	public static final String TOASTR = "toastr";
	public static final String FONT_AWESOME = "FontAwesome";

	private final WebAppPathResolver pathResolver;

	public AdminWebWebResources( WebAppPathResolver pathResolver ) {
		this.pathResolver = pathResolver;

		setDependencies( BootstrapUiWebResources.NAME );

		setWebResources(
				// Admin web overrides default bootstrap
				new WebResource( WebResource.CSS, NAME, "/static/adminweb/css/admin-web-bootstrap.css", WebResource.VIEWS ),
				new WebResource( WebResource.JAVASCRIPT_PAGE_END, NAME + "-ie10-viewport", "/static/adminweb/js/ie10-viewport-bug-workaround.js",
				                 WebResource.VIEWS ),

				// Add FontAwesome icons
				new WebResource( WebResource.CSS, FONT_AWESOME, "/static/adminweb/css/font-awesome.min.css", WebResource.VIEWS ),

				// Use toastr for notifications
				new WebResource( WebResource.JAVASCRIPT_PAGE_END, TOASTR, "//cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/js/toastr.min.js",
				                 WebResource.EXTERNAL ),
				new WebResource( WebResource.CSS, TOASTR, "//cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/css/toastr.min.css",
				                 WebResource.EXTERNAL ),
				new WebResource( WebResource.JAVASCRIPT_PAGE_END, NAME, "/static/adminweb/js/admin-web-module.js", WebResource.VIEWS )
		);
	}

	@Autowired
	void autoRegisterPackage( @Qualifier("adminWebResourcePackageManager") WebResourcePackageManager adminWebResourcePackageManager ) {
		adminWebResourcePackageManager.register( AdminWebWebResources.NAME, this );
	}

	@Override
	public void install( WebResourceRegistry registry ) {
		super.install( registry );

		Map<String, String> acrossWebPathVariables = new HashMap<>();
		acrossWebPathVariables.put( "resourcePath", StringUtils.removeEnd( pathResolver.path( "@resource:/" ), "/" ) );
		acrossWebPathVariables.put( "staticPath", StringUtils.removeEnd( pathResolver.path( "@static:/" ), "/" ) );
		registry.addWithKey( WebResource.JAVASCRIPT, AcrossWebModule.NAME, acrossWebPathVariables, WebResource.DATA );

		Map<String, String> adminWebPathVariables = Collections.singletonMap( "rootPath", StringUtils.removeEnd( pathResolver.path( "@adminWeb:/" ), "/" ) );
		registry.addWithKey( WebResource.JAVASCRIPT, AdminWebModule.NAME, adminWebPathVariables, WebResource.DATA );
	}
}
