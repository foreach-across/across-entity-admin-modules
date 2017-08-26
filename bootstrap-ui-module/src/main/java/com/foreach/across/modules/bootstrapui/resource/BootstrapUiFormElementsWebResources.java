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
 * Adds resources for the following:
 * <ul>
 * <li><a href="http://momentjs.com">moment js</a></li>
 * <li><a href="https://github.com/Eonasdan/bootstrap-datetimepicker">Eonasdan Bootstrap datepicker</a></li>
 * <li><a href="https://github.com/BobKnothe/autoNumeric">autoNumeric</a></li>
 * <li><a href="http://www.jacklmoore.com/autosize/">Autosize</a></li>
 * </ul>
 *
 * @author Arne Vandamme
 */
public class
BootstrapUiFormElementsWebResources extends SimpleWebResourcePackage
{
	public static final String NAME = "bootstrapui-formelements";

	public BootstrapUiFormElementsWebResources() {
		setDependencies( BootstrapUiWebResources.NAME );
		setWebResources(
				new WebResource( WebResource.JAVASCRIPT_PAGE_END, NAME + "-momentjs",
				                 "//cdn.jsdelivr.net/webjars/momentjs/2.10.6/moment-with-locales.js",
				                 WebResource.EXTERNAL ),
				new WebResource( WebResource.JAVASCRIPT_PAGE_END, NAME + "-momentjs-locale-nl-BE",
				                 "/static/" + BootstrapUiModule.NAME + "/js/moment/locale-nl-BE.js",
				                 WebResource.VIEWS ),
				new WebResource( WebResource.JAVASCRIPT_PAGE_END, NAME + "-datetimepicker",
				                 "//cdn.jsdelivr.net/webjars/org.webjars/Eonasdan-bootstrap-datetimepicker/4.14.30/bootstrap-datetimepicker.min.js",
				                 WebResource.EXTERNAL ),
				new WebResource( WebResource.CSS, NAME + "-datetimepicker-css",
				                 "//cdn.jsdelivr.net/webjars/org.webjars/Eonasdan-bootstrap-datetimepicker/4.14.30/bootstrap-datetimepicker.css",
				                 WebResource.EXTERNAL ),
				new WebResource( WebResource.JAVASCRIPT_PAGE_END, NAME + "-numeric",
				                 "//cdn.jsdelivr.net/webjars/org.webjars.bower/autoNumeric/1.9.30/autoNumeric.js",
				                 WebResource.EXTERNAL ),
				new WebResource( WebResource.JAVASCRIPT_PAGE_END, NAME + "-autosize",
				                 "/static/" + BootstrapUiModule.NAME + "/js/autosize.min.js",
				                 WebResource.VIEWS ),

				// Bootstrap select
				new WebResource( WebResource.JAVASCRIPT_PAGE_END, BootstrapUiModule.NAME + "-select",
				                 "//cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.12.2/js/bootstrap-select.min.js",
				                 WebResource.EXTERNAL ),
				new WebResource( WebResource.CSS, BootstrapUiModule.NAME + "-select",
				                 "//cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.12.2/css/bootstrap-select.min.css",
				                 WebResource.EXTERNAL ),

				// Form elements initializer javascript
				new WebResource( WebResource.JAVASCRIPT_PAGE_END, NAME,
				                 "/static/" + BootstrapUiModule.NAME + "/js/bootstrapui-formelements.js",
				                 WebResource.VIEWS )
		);
	}
}
