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
import com.foreach.across.modules.web.resource.SimpleWebResourcePackage;
import com.foreach.across.modules.web.resource.WebResource;

/**
 * Boostrap css, requires jquery as well.
 */
public class AdminWebWebResources extends SimpleWebResourcePackage
{
	public static final String NAME = "bootstrap-adminweb";
	public static final String TOASTR = "toastr";

	public AdminWebWebResources() {
		setDependencies( BootstrapUiWebResources.NAME );

		setWebResources(
				// Admin web overrides default bootstrap
				new WebResource( WebResource.CSS, BootstrapUiWebResources.NAME, "/static/adminweb/css/admin-web-bootstrap.css", WebResource.VIEWS ),/*
					new WebResource( WebResource.JAVASCRIPT_PAGE_END, NAME + "-main",
					                 "/static/adminweb/js/main.js",
					                 WebResource.VIEWS ),*/
				new WebResource( WebResource.JAVASCRIPT_PAGE_END, NAME + "-ie10-viewport", "/static/adminweb/js/ie10-viewport-bug-workaround.js",
				                 WebResource.VIEWS ),

				// Use toastr for notifications
				new WebResource( WebResource.JAVASCRIPT_PAGE_END, TOASTR, "//cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/js/toastr.min.js",
				                 WebResource.EXTERNAL ),
				new WebResource( WebResource.CSS, TOASTR, "//cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/css/toastr.min.css",
				                 WebResource.EXTERNAL ),
				new WebResource( WebResource.JAVASCRIPT_PAGE_END, NAME, "/static/adminweb/js/admin-web-module.js", WebResource.VIEWS )
		);
	}
}
