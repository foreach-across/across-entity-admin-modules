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

package com.foreach.across.modules.entity.web;

import com.foreach.across.modules.bootstrapui.resource.BootstrapUiWebResources;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.web.resource.WebResource;
import com.foreach.across.modules.web.resource.WebResourcePackage;
import com.foreach.across.modules.web.resource.WebResourceRegistry;

import static com.foreach.across.modules.web.resource.WebResource.css;
import static com.foreach.across.modules.web.resource.WebResource.javascript;
import static com.foreach.across.modules.web.resource.WebResourceRule.add;
import static com.foreach.across.modules.web.resource.WebResourceRule.addPackage;

/**
 * Contains the web resources for the entity module administration UI.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public final class EntityModuleWebResources implements WebResourcePackage
{
	public static final String NAME = "entity-module-admin";

	@Override
	public void install( WebResourceRegistry registry ) {
		registry.apply(
				addPackage( BootstrapUiWebResources.NAME ),
				add( css( "@static:/entity/css/entity-module.css" ) ).withKey( EntityModule.NAME ).toBucket( WebResource.CSS ),
				add( javascript( "@static:/entity/js/dependson.js" ) ).withKey( EntityModule.NAME + "-dependson" ).toBucket( WebResource.JAVASCRIPT_PAGE_END ),
				add( javascript( "@static:/entity/js/entity-module.js" ) ).withKey( EntityModule.NAME ).toBucket( WebResource.JAVASCRIPT_PAGE_END )
		);
	}
}
