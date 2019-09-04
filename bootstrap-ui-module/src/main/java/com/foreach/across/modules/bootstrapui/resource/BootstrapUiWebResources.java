/*
 * Copyright 2019 the original author or authors
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
package com.foreach.across.modules.bootstrapui.resource;

import com.foreach.across.modules.bootstrapui.BootstrapUiModule;
import com.foreach.across.modules.web.resource.WebResource;
import com.foreach.across.modules.web.resource.WebResourcePackage;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import lombok.RequiredArgsConstructor;

import static com.foreach.across.modules.web.resource.WebResource.*;
import static com.foreach.across.modules.web.resource.WebResourceRule.add;
import static com.foreach.across.modules.web.resource.WebResourceRule.addPackage;

/**
 * Responsible for adding the basic bootstrap css and javascript classes.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@RequiredArgsConstructor
public class BootstrapUiWebResources implements WebResourcePackage
{
	public static final String NAME = "bootstrap";
	public static final String POPPER = "popper";
	public static final String FONT_AWESOME = "font-awesome";

	private static final String BOOTSTRAP_VERSION = "4.3.1";
	private static final String POPPER_VERSION = "1.14.3";
	private static final String FONT_AWESOME_VERSION = "5.10.1";

	private final boolean minified;

	@Override
	public void install( WebResourceRegistry registry ) {
		registry.apply(
				addPackage( JQueryWebResources.NAME ),

				// Bootstrap CSS & Javascript
				add( css( "@webjars:/bootstrap/" + BOOTSTRAP_VERSION + "/css/bootstrap" + ( minified ? ".min" : "" ) + ".css" ) )
						.withKey( NAME )
						.toBucket( WebResource.CSS ),

				// Font Awesome CSS
				add( css( "@webjars:/font-awesome/" + FONT_AWESOME_VERSION + "/css/all" + ( minified ? ".min" : "" ) + ".css" ) )
						.withKey( FONT_AWESOME )
						.toBucket( WebResource.CSS ),

				//add( javascript( "@webjars:/popper.js/" + POPPER_VERSION + "/umd/popper" + ( minified ? ".min" : "" ) + ".js" ) )
				//		.withKey( NAME )
				//			.toBucket( WebResource.JAVASCRIPT_PAGE_END ),
				add( javascript( "@webjars:/bootstrap/" + BOOTSTRAP_VERSION + "/js/bootstrap.bundle" + ( minified ? ".min" : "" ) + ".js" ) )
						.withKey( NAME )
						.toBucket( WebResource.JAVASCRIPT_PAGE_END ),

				// BootstrapUiModule main javascript
				add( javascript( "@static:/" + BootstrapUiModule.NAME + "/js/bootstrapui.js" ) )
						.withKey( BootstrapUiModule.NAME )
						.toBucket( WebResource.JAVASCRIPT_PAGE_END ),

				add( WebResource.css( "@static:/" + BootstrapUiModule.NAME + "/css/bootstrapui.css" ) )
						.withKey( NAME + "ui" )
						.toBucket( CSS )
		);
	}
}
