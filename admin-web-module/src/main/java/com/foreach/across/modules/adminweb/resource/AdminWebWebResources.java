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
import com.foreach.across.modules.web.resource.WebResource;
import com.foreach.across.modules.web.resource.WebResourcePackage;
import com.foreach.across.modules.web.resource.WebResourcePackageManager;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.foreach.across.modules.web.resource.WebResource.*;
import static com.foreach.across.modules.web.resource.WebResourceRule.add;
import static com.foreach.across.modules.web.resource.WebResourceRule.addPackage;

/**
 * Boostrap css, requires jquery as well.
 * Also registers some application paths as javascript data.
 */
@Component
public class AdminWebWebResources implements WebResourcePackage
{
	public static final String NAME = "bootstrap-adminweb";
	public static final String TOASTR = "toastr";
	public static final String FONT_AWESOME = "FontAwesome";

	private final WebAppPathResolver pathResolver;

	public AdminWebWebResources( WebAppPathResolver pathResolver ) {
		this.pathResolver = pathResolver;
	}

	@Autowired
	void autoRegisterPackage( @Qualifier("adminWebResourcePackageManager") WebResourcePackageManager adminWebResourcePackageManager ) {
		adminWebResourcePackageManager.register( AdminWebWebResources.NAME, this );
	}

	@Override
	public void install( WebResourceRegistry registry ) {
		Map<String, String> acrossWebPathVariables = new HashMap<>();
		acrossWebPathVariables.put( "resourcePath", StringUtils.removeEnd( pathResolver.path( "@resource:/" ), "/" ) );
		acrossWebPathVariables.put( "staticPath", StringUtils.removeEnd( pathResolver.path( "@static:/" ), "/" ) );

		Map<String, String> adminWebPathVariables = Collections.singletonMap( "rootPath", StringUtils.removeEnd( pathResolver.path( "@adminWeb:/" ), "/" ) );

		registry.apply(
				// global data
				add( WebResource.globalJsonData( "Across.AcrossWebModule", acrossWebPathVariables ) ).withKey( AcrossWebModule.NAME ).toBucket( JAVASCRIPT ),
				add( WebResource.globalJsonData( "Across.AdminWebModule", adminWebPathVariables ) ).withKey( AdminWebModule.NAME ).toBucket( JAVASCRIPT ),

				// javascript/css
				addPackage( BootstrapUiWebResources.NAME ),
//				add( WebResource.css( "@static:/adminweb/css/admin-web-bootstrap.css" ) ).withKey( NAME ).toBucket( CSS ),
				add( WebResource.javascript( "@static:/adminweb/js/ie10-viewport-bug-workaround.js" ) )
						.withKey( NAME + "-ie10-viewport" )
						.toBucket( JAVASCRIPT_PAGE_END ),

				// Add FontAwesome icons
//				add( WebResource.css( "@static:/adminweb/css/font-awesome.min.css" ) ).withKey( FONT_AWESOME ).toBucket( CSS ),

				// Use toastr for notifications
				add( WebResource.javascript( "@webjars:/toastr/2.1.2/toastr.min.js" ) )
						.withKey( TOASTR )
						.toBucket( JAVASCRIPT_PAGE_END ),
				add( WebResource.css( "@webjars:/toastr/2.1.2/toastr.min.css" ) ).withKey( TOASTR ).toBucket( CSS ),

				// Core admin web javascript
				add( WebResource.javascript( "@static:/adminweb/js/admin-web-module.js" ) )
						.withKey( NAME )
						.toBucket( JAVASCRIPT_PAGE_END )
		);
	}
}
