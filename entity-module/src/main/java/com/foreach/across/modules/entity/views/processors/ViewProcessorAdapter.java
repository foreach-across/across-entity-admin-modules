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
import com.foreach.across.modules.entity.views.context.ConfigurableEntityViewContext;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.web.WebViewCreationContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import org.springframework.ui.ModelMap;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.WebDataBinder;

/**
 * Default implementation of an {@link EntityViewProcessor}.
 *
 * @author Andy Somers, Arne Vandamme
 */
@Deprecated
public class ViewProcessorAdapter
		implements EntityViewProcessor
{
	@Override
	public void prepareModelAndCommand( String viewName,
	                                    WebViewCreationContext creationContext,
	                                    EntityViewCommand command,
	                                    ModelMap model ) {
	}

	@Override
	public void prepareDataBinder( String viewName,
	                               WebViewCreationContext creationContext,
	                               EntityViewCommand command,
	                               DataBinder dataBinder ) {
	}

	@Override
	public final void preProcess( WebViewCreationContext creationContext, EntityView view ) {
		applyCustomPreProcessing( creationContext, view );
	}

	protected void applyCustomPreProcessing( WebViewCreationContext creationContext, EntityView view ) {
	}

	@Override
	public final void postProcess( WebViewCreationContext creationContext, EntityView view ) {
		applyCustomPostProcessing( creationContext, view );

		modifyViewElements( view.getViewElements() );
	}

	protected void applyCustomPostProcessing( WebViewCreationContext creationContext, EntityView view ) {

	}

	protected void modifyViewElements( ContainerViewElement elements ) {

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
	public void render( EntityViewRequest entityViewRequest,
	                    EntityView entityView ) {

	}

	@Override
	public void preRender( EntityViewRequest entityViewRequest,
	                       EntityView entityView ) {

	}

	@Override
	public void postRender( EntityViewRequest entityViewRequest,
	                        EntityView entityView ) {

	}

	@Override
	public void postProcess( EntityViewRequest entityViewRequest, EntityView entityView ) {

	}
}
