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
 * Adds resources to support <a href="http://momentjs.com">moment js</a> and the
 * <a href="https://github.com/Eonasdan/bootstrap-datetimepicker">Eonasdan Bootstrap datepicker</a>.
 *
 * @author Arne Vandamme
 */
public class DateTimePickerWebResourcePackage extends SimpleWebResourcePackage
{
	public static final String NAME = "bootstrap-datetimepicker";

	public DateTimePickerWebResourcePackage() {
		setDependencies( BootstrapUiWebResourcePackage.NAME );
		setWebResources(
				new WebResource( WebResource.JAVASCRIPT_PAGE_END, NAME + "-momentjs",
				                 "//cdn.jsdelivr.net/webjars/momentjs/2.10.6/moment-with-locales.js",
				                 WebResource.EXTERNAL ),
				new WebResource( WebResource.JAVASCRIPT_PAGE_END, NAME + "-datetimepicker",
				                 "//cdn.jsdelivr.net/webjars/org.webjars/Eonasdan-bootstrap-datetimepicker/4.14.30/bootstrap-datetimepicker.min.js",
				                 WebResource.EXTERNAL ),
				new WebResource( WebResource.CSS, NAME + "-datetimepicker-css",
				                 "//cdn.jsdelivr.net/webjars/org.webjars/Eonasdan-bootstrap-datetimepicker/4.14.30/bootstrap-datetimepicker.css",
				                 WebResource.EXTERNAL ),
				new WebResource( WebResource.JAVASCRIPT_PAGE_END, NAME + "-viewelements",
				                 "/js/" + BootstrapUiModule.NAME + "/bootstrapui.js",
				                 WebResource.VIEWS )
		);
	}
}
