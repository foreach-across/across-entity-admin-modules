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
package com.foreach.across.modules.entity.views;

import com.foreach.across.modules.entity.controllers.EntityViewCommand;
import com.foreach.across.modules.entity.views.context.ConfigurableEntityViewContext;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import org.springframework.ui.ModelMap;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.WebDataBinder;

/**
 * Default processor interface for entity view processing.  Whereas there is only a single
 * {@link EntityViewFactory}, if that factory implements {@link DispatchingEntityViewFactory}
 * there can be multiple processors that modify the resulting view.
 *
 * @author Arne Vandamme
 * @see DispatchingEntityViewFactory
 */
public interface EntityViewProcessor<V extends ViewCreationContext, T extends EntityView>
{
	@Deprecated
	void prepareModelAndCommand( String viewName,
	                             V creationContext,
	                             EntityViewCommand command,
	                             ModelMap model );

	@Deprecated
	void prepareDataBinder( String viewName, V creationContext, EntityViewCommand command, DataBinder dataBinder );

	@Deprecated
	void preProcess( V creationContext, T view );

	@Deprecated
	void postProcess( V creationContext, T view );

	/**
	 * Apply possible factory modifications to the {@link ConfigurableEntityViewContext}.
	 * Call this method before creating an {@link com.foreach.across.modules.entity.views.request.EntityViewRequest}
	 * that uses the context.
	 * <p/>
	 * This method is called first, before {@link #validateRequest(EntityViewRequest)} in the default view rendering.
	 *
	 * @param entityViewContext to modify
	 */
	void prepareEntityViewContext( ConfigurableEntityViewContext entityViewContext );

	/**
	 * Verify the {@link EntityViewRequest} is valid for this factory.
	 * This usually means things like performing security checks and checking all individual properties are valid.
	 * <p/>
	 * Any invalid requests are expected to throw the most relevant exceptions.
	 * <p/>
	 * Called after {@link #prepareEntityViewContext(ConfigurableEntityViewContext)} but before initializing the
	 * command object.
	 *
	 * @param entityViewRequest request to validate
	 */
	void validateRequest( EntityViewRequest entityViewRequest );

	/**
	 * Initialize the {@link com.foreach.across.modules.entity.views.request.EntityViewCommand} for the given request.
	 * Optionally configure the {@link WebDataBinder} that will be used to bind the command object.
	 *
	 * @param entityViewRequest request
	 * @param command           object
	 * @param dataBinder        to bind the web request to the command
	 */
	void initializeCommandObject( EntityViewRequest entityViewRequest,
	                              com.foreach.across.modules.entity.views.request.EntityViewCommand command,
	                              WebDataBinder dataBinder );

	/**
	 * Pre-process the created {@link EntityView} before any of the control or rendering methods are called.
	 *
	 * @param entityViewRequest request
	 * @param entityView        model and view
	 */
	void preProcess( EntityViewRequest entityViewRequest, EntityView entityView );

	/**
	 * Called after {@link #preProcess(EntityViewRequest, EntityView)} but before any rendering.
	 *
	 * @param entityViewRequest request
	 * @param entityView        model and view
	 * @param command           object for the request
	 */
	void doControl( EntityViewRequest entityViewRequest, EntityView entityView, com.foreach.across.modules.entity.views.request.EntityViewCommand command );

	/**
	 * Called after {@link #doControl(EntityViewRequest, EntityView, com.foreach.across.modules.entity.views.request.EntityViewCommand)} but only if rendering
	 * is supposed to happen.
	 *
	 * @param entityViewRequest request
	 * @param entityView        model and view
	 * @see EntityView#shouldRender()
	 */
	void preRender( EntityViewRequest entityViewRequest, EntityView entityView );

	/**
	 * Called after {@link #doControl(EntityViewRequest, EntityView, com.foreach.across.modules.entity.views.request.EntityViewCommand)}.
	 *
	 * @param entityViewRequest request
	 * @param entityView        model and view
	 * @see EntityView#shouldRender()
	 */
	void render( EntityViewRequest entityViewRequest, EntityView entityView );

	/**
	 * Called after {@link #render(EntityViewRequest, EntityView)} but only if rendering happened.
	 *
	 * @param entityViewRequest request
	 * @param entityView        model and view
	 * @see EntityView#shouldRender()
	 */
	void postRender( EntityViewRequest entityViewRequest, EntityView entityView );

	/**
	 * Post-process the created {@link EntityView} after control and render have completed.
	 * This method will always be called, even if render was skipped.
	 *
	 * @param entityViewRequest request
	 * @param entityView        model and view
	 */
	void postProcess( EntityViewRequest entityViewRequest, EntityView entityView );
}
