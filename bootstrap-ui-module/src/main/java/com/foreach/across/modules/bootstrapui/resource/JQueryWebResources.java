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

import com.foreach.across.modules.web.resource.WebResource;
import com.foreach.across.modules.web.resource.WebResourcePackage;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.resource.WebResourceRule;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;

/**
 * Registers the main JQuery javascript dependency in a {@link WebResourceRegistry}.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@RequiredArgsConstructor
public class JQueryWebResources implements WebResourcePackage
{
	public static final String NAME = "jquery";

	private static final String JQUERY_VERSION = "3.4.1";

	private final boolean minified;

	@Override
	public void install( WebResourceRegistry webResourceRegistry ) {
		webResourceRegistry.apply(
				WebResourceRule.add( WebResource.javascript( "@webjars:/jquery/" + JQUERY_VERSION + "/jquery" + ( minified ? ".min" : "" ) + ".js" ) )
				               .withKey( NAME )
				               .toBucket( WebResource.JAVASCRIPT_PAGE_END )
				               .order( Ordered.HIGHEST_PRECEDENCE )
		);
	}
}
