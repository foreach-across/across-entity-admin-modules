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

import com.foreach.across.modules.bootstrapui.resource.BootstrapUiWebResources;
import com.foreach.across.modules.bootstrapui.resource.JQueryWebResources;
import com.foreach.across.modules.web.resource.SimpleWebResourcePackage;
import com.foreach.across.modules.web.resource.WebResource;
import com.foreach.across.modules.web.resource.WebResourceRegistry;

/**
 * Boostrap css, requires jquery as well.
 */
public class AdminBootstrapWebResourcePackage extends SimpleWebResourcePackage
{
	public static final String NAME = "bootstrap-adminweb";

	public AdminBootstrapWebResourcePackage( boolean minified ) {
		this( minified, /*"3.1.1"*/ "3.3.5" );
	}

	public AdminBootstrapWebResourcePackage( boolean minified, String version ) {
		setDependencies( BootstrapUiWebResources.NAME );
		if ( minified ) {
			setWebResources(
					// Admin web overrides default bootstrap
					new WebResource( WebResource.CSS, BootstrapUiWebResources.NAME,
					                 "/static/adminweb/css/admin-web-bootstrap.css",
					                 WebResource.VIEWS ),/*
					new WebResource( WebResource.JAVASCRIPT_PAGE_END, NAME + "-main",
					                 "/static/adminweb/js/main.js",
					                 WebResource.VIEWS ),*/
					new WebResource( WebResource.JAVASCRIPT_PAGE_END, NAME + "-ie10-viewport",
					                 "/static/adminweb/js/ie10-viewport-bug-workaround.js",
					                 WebResource.VIEWS )
			);
		}
		else {
			setWebResources(
					new WebResource( WebResource.CSS, NAME,
					                 "/static/adminweb/css/admin-web-bootstrap.css",
					                 WebResource.VIEWS ),/*
					new WebResource( WebResource.JAVASCRIPT_PAGE_END, NAME + "-main",
					                 "/static/adminweb/js/main.js",
					                 WebResource.VIEWS ),*/
					new WebResource( WebResource.JAVASCRIPT_PAGE_END, NAME + "-ie10-viewport",
					                 "/static/adminweb/js/ie10-viewport-bug-workaround.js",
					                 WebResource.VIEWS )
			);
		}

	}

	@Override
	public void install( WebResourceRegistry registry ) {
		registry.addPackage( JQueryWebResources.NAME );

		super.install( registry );
	}
}
