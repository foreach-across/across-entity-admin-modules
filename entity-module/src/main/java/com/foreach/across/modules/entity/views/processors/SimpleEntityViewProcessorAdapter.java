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

package com.foreach.across.modules.entity.views.processors;

import com.foreach.across.modules.entity.controllers.EntityViewCommand;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.EntityViewProcessor;
import com.foreach.across.modules.entity.views.ViewCreationContext;
import com.foreach.across.modules.entity.views.context.ConfigurableEntityViewContext;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import org.springframework.ui.ModelMap;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.WebDataBinder;

/**
 * Simple adapter for {@link EntityViewProcessor} that does not directly tie into the {@link com.foreach.across.modules.web.ui.ViewElement}
 * generation.  If you like more fine-grained methods for building a {@link com.foreach.across.modules.web.ui.ViewElement} based interface,
 * consider the {@link EntityViewProcessorAdapter} adapter instead.
 *
 * @author Arne Vandamme
 * @see EntityViewProcessorAdapter
 * @since 2.0.0
 */
public class SimpleEntityViewProcessorAdapter implements EntityViewProcessor<ViewCreationContext, EntityView>
{
	@Override
	public final void prepareModelAndCommand( String viewName, ViewCreationContext creationContext, EntityViewCommand command, ModelMap model ) {

	}

	@Override
	public final void prepareDataBinder( String viewName, ViewCreationContext creationContext, EntityViewCommand command, DataBinder dataBinder ) {

	}

	@Override
	public final void preProcess( ViewCreationContext creationContext, EntityView view ) {

	}

	@Override
	public final void postProcess( ViewCreationContext creationContext, EntityView view ) {

	}

	@Override
	public void prepareEntityViewContext( ConfigurableEntityViewContext entityViewContext ) {

	}

	@Override
	public void authorizeRequest( EntityViewRequest entityViewRequest ) {

	}

	@Override
	public void initializeCommandObject( EntityViewRequest entityViewRequest,
	                                     com.foreach.across.modules.entity.views.request.EntityViewCommand command,
	                                     WebDataBinder dataBinder ) {

	}

	@Override
	public void preProcess( EntityViewRequest entityViewRequest, EntityView entityView ) {

	}

	@Override
	public void doControl( EntityViewRequest entityViewRequest,
	                       EntityView entityView,
	                       com.foreach.across.modules.entity.views.request.EntityViewCommand command ) {

	}

	@Override
	public void preRender( EntityViewRequest entityViewRequest, EntityView entityView ) {

	}

	@Override
	public void render( EntityViewRequest entityViewRequest, EntityView entityView ) {

	}

	@Override
	public void postRender( EntityViewRequest entityViewRequest, EntityView entityView ) {

	}

	@Override
	public void postProcess( EntityViewRequest entityViewRequest, EntityView entityView ) {

	}
}
