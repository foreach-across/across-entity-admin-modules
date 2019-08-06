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

import static com.foreach.across.modules.web.resource.WebResource.CSS;
import static com.foreach.across.modules.web.resource.WebResource.JAVASCRIPT_PAGE_END;
import static com.foreach.across.modules.web.resource.WebResourceRule.add;
import static com.foreach.across.modules.web.resource.WebResourceRule.addPackage;

/**
 * Adds resources for the following:
 * <ul>
 * <li><a href="http://momentjs.com">moment js</a></li>
 * <li><a href="https://github.com/Eonasdan/bootstrap-datetimepicker">Eonasdan Bootstrap datepicker</a></li>
 * <li><a href="https://github.com/BobKnothe/autoNumeric">autoNumeric</a></li>
 * <li><a href="http://www.jacklmoore.com/autosize/">Autosize</a></li>
 * <li><a href="https://github.com/corejavascript/typeahead.js">Twitter Typeahead</a></li>
 * </ul>
 *
 * @author Arne Vandamme
 */
@SuppressWarnings("WeakerAccess")
@RequiredArgsConstructor
public class BootstrapUiFormElementsWebResources implements WebResourcePackage
{
	public static final String NAME = "bootstrapui-formelements";
	public static final String MOMENTJS = "momentjs";
	public static final String EONASDAN_DATETIME = "eonasdan-datetime";
	public static final String AUTO_NUMERIC = "autoNumeric";
	public static final String AUTOSIZE = "autosize";
	public static final String BOOTSTRAP_SELECT = "bootstrap-select";
	public static final String TYPEAHEAD = "typeahead";
	public static final String HANDLEBARS = "handlebars";

	private static final String MOMENT_VERSION = "2.10.6";
	private static final String EONASDAN_VERSION = "4.14.30";
	private static final String AUTO_NUMERIC_VERSION = "1.9.30";
	private static final String AUTOSIZE_VERSION = "3.0.20";
	private static final String BOOTSTRAP_SELECT_VERSION = "1.12.2";
	private static final String TYPEAHEAD_VERSION = "1.2.1";
	private static final String HANDLEBARS_VERSION = "4.0.14";

	private final boolean minified;

	@Override
	public void install( WebResourceRegistry registry ) {
		registry.apply(
				addPackage( BootstrapUiWebResources.NAME ),

				// momentjs with locales
				add( WebResource.javascript( "@webjars:/momentjs/" + MOMENT_VERSION + "/min/moment-with-locales" + minified( ".js" ) ) )
						.withKey( MOMENTJS )
						.toBucket( JAVASCRIPT_PAGE_END ),

				// Eonasdan - datetimepicker
				add( WebResource.javascript(
						"@webjars:/Eonasdan-bootstrap-datetimepicker/" + EONASDAN_VERSION + "/bootstrap-datetimepicker.min.js" ) )
						.withKey( EONASDAN_DATETIME )
						.toBucket( JAVASCRIPT_PAGE_END ),
				add( WebResource.css( "@webjars:/Eonasdan-bootstrap-datetimepicker/" + EONASDAN_VERSION + "/bootstrap-datetimepicker.min.css" ) )
						.withKey( EONASDAN_DATETIME )
						.toBucket( CSS ),

				// autoNumeric
				add( WebResource.javascript( "@webjars:org.webjars.bower/autoNumeric/" + AUTO_NUMERIC_VERSION + "/autoNumeric.js" ) )
						.withKey( AUTO_NUMERIC )
						.toBucket( JAVASCRIPT_PAGE_END ),

				// autosize
				add( WebResource.javascript( "@webjars:org.webjars.bower/autosize/" + AUTOSIZE_VERSION + "/dist/autosize" + minified( ".js" ) ) )
						.withKey( AUTOSIZE )
						.toBucket( JAVASCRIPT_PAGE_END ),

				// bootstrap select
				add( WebResource.javascript( "@webjars:/bootstrap-select/" + BOOTSTRAP_SELECT_VERSION + "/js/bootstrap-select" + minified( ".js" ) ) )
						.withKey( BOOTSTRAP_SELECT )
						.toBucket( JAVASCRIPT_PAGE_END ),
				add( WebResource.css( "@webjars:/bootstrap-select/" + BOOTSTRAP_SELECT_VERSION + "/css/bootstrap-select" + minified( ".css" ) ) )
						.withKey( BOOTSTRAP_SELECT )
						.toBucket( CSS ),

				// typeahead
				add( WebResource.javascript( "@webjars:/handlebars/" + HANDLEBARS_VERSION + "/handlebars" + minified( ".js" ) ) )
						.withKey( HANDLEBARS )
						.toBucket( JAVASCRIPT_PAGE_END ),
				add( WebResource.javascript( "@webjars:org.webjars.npm/corejs-typeahead/" + TYPEAHEAD_VERSION + "/dist/typeahead.bundle" + minified( ".js" ) ) )
						.withKey( TYPEAHEAD )
						.toBucket( JAVASCRIPT_PAGE_END ),

				// BootstrapUiModule specific
				add( WebResource.javascript( "@static:/" + BootstrapUiModule.NAME + "/js/bootstrapui-formelements.js" ) )
						.withKey( NAME )
						.toBucket( JAVASCRIPT_PAGE_END ),
				add( WebResource.css( "@static:/" + BootstrapUiModule.NAME + "/css/bootstrapui.css" ) )
						.withKey( NAME )
						.toBucket( CSS )
		);
	}

	private String minified( String extension ) {
		if ( minified ) {
			return ".min" + extension;
		}

		return extension;
	}
}
