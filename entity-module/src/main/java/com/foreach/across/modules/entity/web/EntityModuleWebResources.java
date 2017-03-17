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

import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.web.resource.SimpleWebResourcePackage;
import com.foreach.across.modules.web.resource.WebResource;

/**
 * Contains the web resources for the entity module administration UI.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public final class EntityModuleWebResources extends SimpleWebResourcePackage
{
	public static final String NAME = "entity-module-admin";

	public EntityModuleWebResources() {
		setWebResources(
				new WebResource( WebResource.CSS, EntityModule.NAME, "/css/entity/entity-module.css", WebResource.VIEWS ),
				new WebResource( WebResource.JAVASCRIPT_PAGE_END, EntityModule.NAME,
				                 "/js/entity/entity-module.js", WebResource.VIEWS ),
				new WebResource( WebResource.JAVASCRIPT_PAGE_END, EntityModule.NAME + "-dependson",
				                 "/js/entity/dependson.js", WebResource.VIEWS ),
				new WebResource( WebResource.JAVASCRIPT_PAGE_END, EntityModule.NAME + "-form-elements",
				                 "/js/entity/form-elements.js", WebResource.VIEWS )
		);
	}
}
