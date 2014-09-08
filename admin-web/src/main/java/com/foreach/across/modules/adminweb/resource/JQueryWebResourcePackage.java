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

import com.foreach.across.modules.web.resource.SimpleWebResourcePackage;
import com.foreach.across.modules.web.resource.WebResource;

import java.util.Arrays;

public class JQueryWebResourcePackage extends SimpleWebResourcePackage
{
	public static final String NAME = "jquery";

	public JQueryWebResourcePackage( boolean minified ) {
		this( minified, "1.11.0" );
	}

	public JQueryWebResourcePackage( boolean minified, String version ) {
		if ( minified ) {
			setWebResources( Arrays.asList( new WebResource( WebResource.JAVASCRIPT_PAGE_END, NAME,
			                                                 "//ajax.googleapis.com/ajax/libs/jquery/" + version + "/jquery.min.js",
			                                                 WebResource.EXTERNAL )
			                 )
			);
		}
		else {
			setWebResources( Arrays.asList( new WebResource( WebResource.JAVASCRIPT_PAGE_END, NAME,
			                                                 "//ajax.googleapis.com/ajax/libs/jquery/" + version + "/jquery.js",
			                                                 WebResource.EXTERNAL )
			                 )
			);
		}
	}
}
