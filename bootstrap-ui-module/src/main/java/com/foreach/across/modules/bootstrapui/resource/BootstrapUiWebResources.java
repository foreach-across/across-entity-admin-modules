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
package com.foreach.across.modules.bootstrapui.resource;

import com.foreach.across.modules.bootstrapui.BootstrapUiModule;
import com.foreach.across.modules.web.resource.SimpleWebResourcePackage;
import com.foreach.across.modules.web.resource.WebResource;

/**
 * Responsible for adding the basic bootstrap css and javascript classes.
 *
 * @author Arne Vandamme
 */
public class BootstrapUiWebResources extends SimpleWebResourcePackage
{
	public static final String VERSION = "3.3.7";
	public static final String NAME = "bootstrap";

	public BootstrapUiWebResources() {
		setDependencies( JQueryWebResources.NAME );
		setWebResources(
				new WebResource( WebResource.CSS, NAME,
				                 "//maxcdn.bootstrapcdn.com/bootstrap/" + VERSION + "/css/bootstrap.min.css",
				                 WebResource.EXTERNAL ),
				new WebResource( WebResource.JAVASCRIPT_PAGE_END, NAME,
				                 "//maxcdn.bootstrapcdn.com/bootstrap/" + VERSION + "/js/bootstrap.min.js",
				                 WebResource.EXTERNAL ),

				// Custom javascript
				new WebResource( WebResource.JAVASCRIPT_PAGE_END, BootstrapUiModule.NAME,
				                 "/static/" + BootstrapUiModule.NAME + "/js/bootstrapui.js",
				                 WebResource.VIEWS )
		);
	}
}
