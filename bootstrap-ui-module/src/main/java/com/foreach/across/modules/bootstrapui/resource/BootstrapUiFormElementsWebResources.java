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
 * <li><a href="https://tempusdominus.github.io/bootstrap-4/">Tempus Dominus Bootstrap 4 datepicker</a></li>
 * <li><a href="http://autonumeric.org/">autoNumeric</a></li>
 * <li><a href="http://www.jacklmoore.com/autosize/">Autosize</a></li>
 * <li><a href="https://github.com/corejavascript/typeahead.js">Twitter Typeahead</a></li>
 * <li><a href="https://developer.snapappointments.com/bootstrap-select/">Bootstrap select</a></li>
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
	public static final String TEMPUS_DOMINUS_DATETIME = "tempus-dominus-datetime";
	public static final String AUTO_NUMERIC = "autoNumeric";
	public static final String AUTOSIZE = "autosize";
	public static final String BOOTSTRAP_SELECT = "bootstrap-select";
	public static final String TYPEAHEAD = "typeahead";
	public static final String HANDLEBARS = "handlebars";

	private static final String MOMENT_VERSION = "2.24.0";
	private static final String TEMPUS_DOMINUS_VERSION = "5.32.1";
	private static final String AUTO_NUMERIC_VERSION = "4.5.4";
	private static final String AUTOSIZE_VERSION = "4.0.2";
	private static final String BOOTSTRAP_SELECT_VERSION = "1.13.11";
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

				// Tempus Dominus - datetimepicker
				add( WebResource.javascript(
						"@webjars:org.webjars.npm/tempusdominus-bootstrap/" + TEMPUS_DOMINUS_VERSION + "/build/js/tempusdominus-bootstrap" +
								minified( ".js" ) ) )
						.withKey( TEMPUS_DOMINUS_DATETIME )
						.toBucket( JAVASCRIPT_PAGE_END ),
				add( WebResource
						     .css( "@webjars:org.webjars.npm/tempusdominus-bootstrap/" + TEMPUS_DOMINUS_VERSION + "/build/css/tempusdominus-bootstrap" +
								           minified( ".css" ) ) )
						.withKey( TEMPUS_DOMINUS_DATETIME )
						.toBucket( CSS ),

				// autoNumeric
				add( WebResource.javascript( "@webjars:org.webjars.npm/autonumeric/" + AUTO_NUMERIC_VERSION + "/dist/autoNumeric" + minified( ".js" ) ) )
						.withKey( AUTO_NUMERIC )
						.toBucket( JAVASCRIPT_PAGE_END ),

				// autosize
				add( WebResource.javascript( "@webjars:org.webjars.bower/autosize/" + AUTOSIZE_VERSION + "/dist/autosize" + minified( ".js" ) ) )
						.withKey( AUTOSIZE )
						.toBucket( JAVASCRIPT_PAGE_END ),

				// bootstrap select
				add( WebResource.javascript(
						"@webjars:org.webjars.npm/bootstrap-select/" + BOOTSTRAP_SELECT_VERSION + "/dist/js/bootstrap-select" + minified( ".js" ) ) )
						.withKey( BOOTSTRAP_SELECT )
						.toBucket( JAVASCRIPT_PAGE_END ),
				add( WebResource.css( "@webjars:org.webjars.npm/bootstrap-select/" + BOOTSTRAP_SELECT_VERSION + "/dist/css/bootstrap-select" + minified(
						".css" ) ) )
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
						.toBucket( JAVASCRIPT_PAGE_END )
		);
	}

	private String minified( String extension ) {
		if ( minified ) {
			return ".min" + extension;
		}

		return extension;
	}
}
