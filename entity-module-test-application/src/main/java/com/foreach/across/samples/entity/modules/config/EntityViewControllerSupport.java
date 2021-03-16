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

package com.foreach.across.samples.entity.modules.config;

import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.views.context.ConfigurableEntityViewContext;
import com.foreach.across.modules.entity.views.context.EntityViewContextLoader;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.bind.WebDataBinder;

import javax.servlet.http.HttpServletRequest;

public interface EntityViewControllerSupport
{
	void configureViewContext( EntityRegistry entityRegistry,
	                           ConversionService conversionService,
	                           ConfigurableEntityViewContext entityViewContext,
	                           HttpServletRequest httpServletRequest,
	                           EntityViewContextLoader entityViewContextLoader );

	void configureEntityViewRequest( EntityViewRequest entityViewRequest,
	                                 ConfigurableEntityViewContext entityViewContext,
	                                 HttpServletRequest httpServletRequest );

	void configureEntityViewCommandBinder( WebDataBinder dataBinder, EntityViewRequest request );

	void registerWebResources( WebResourceRegistry webResourceRegistry );
}
