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

import com.foreach.across.modules.entity.conditionals.ConditionalOnAdminWeb;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.EntityViewProcessor;
import com.foreach.across.modules.entity.views.context.ConfigurableEntityViewContext;
import com.foreach.across.modules.entity.views.processors.support.ViewElementBuilderMap;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.resource.WebResourceUtils;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.builder.ContainerViewElementBuilderSupport;
import org.springframework.http.HttpMethod;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;

import static com.foreach.across.modules.entity.views.DefaultEntityViewFactory.ATTRIBUTE_CONTAINER_BUILDER;
import static com.foreach.across.modules.entity.views.DefaultEntityViewFactory.ATTRIBUTE_CONTAINER_ELEMENT;

/**
 * Adapter implementation for a default {@link EntityViewProcessor} following the common {@link ViewElement}
 * based rendering approach.  Adds several more fine-grained extension methods for that use case:
 * <ul>
 * <li>{@link #doGet(EntityViewRequest, EntityView, EntityViewCommand)}</li>
 * <li>{@link #doPost(EntityViewRequest, EntityView, EntityViewCommand, BindingResult)}</li>
 * <li>{@link #doControl(EntityViewRequest, EntityView, EntityViewCommand, BindingResult, HttpMethod)}</li>
 * <li>{@link #registerWebResources(EntityViewRequest, EntityView, WebResourceRegistry)}</li>
 * <li>{@link #prepareViewElementBuilderContext(EntityViewRequest, EntityView, ViewElementBuilderContext)}</li>
 * <li>{@link #createViewElementBuilders(EntityViewRequest, EntityView, ViewElementBuilderMap)}</li>
 * <li>{@link #render(EntityViewRequest, EntityView, ContainerViewElementBuilderSupport, ViewElementBuilderMap, ViewElementBuilderContext)}</li>
 * <li>{@link #postRender(EntityViewRequest, EntityView, ContainerViewElement, ViewElementBuilderContext)}</li>
 * </ul>
 * <p/>
 * This implementation introduces some overhead by managing default attributes and dispatching to additional methods.
 * If you do not need any of those, you should consider using the {@link SimpleEntityViewProcessorAdapter} instead.
 *
 * @author Arne Vandamme
 * @see SimpleEntityViewProcessorAdapter
 * @since 2.0.0
 */
@ConditionalOnAdminWeb
public abstract class EntityViewProcessorAdapter implements EntityViewProcessor
{
	private static final String ATTRIBUTE_BUILDER_MAP = "entityViewBuilderMap";

	@Override
	public void prepareEntityViewContext( ConfigurableEntityViewContext entityViewContext ) {
	}

	@Override
	public void authorizeRequest( EntityViewRequest entityViewRequest ) {
	}

	@Override
	public void initializeCommandObject( EntityViewRequest entityViewRequest,
	                                     EntityViewCommand command,
	                                     WebDataBinder dataBinder ) {
	}

	@Override
	public final void preProcess( EntityViewRequest entityViewRequest, EntityView entityView ) {
		validateCommandObject( entityViewRequest, entityViewRequest.getCommand(), entityViewRequest.getBindingResult(), entityViewRequest.getHttpMethod() );

		preProcess( entityViewRequest, entityView, entityViewRequest.getCommand() );
	}

	/**
	 * Perform (custom) validation on the command object before passing it on to any {@link #doControl(EntityViewRequest, EntityView, EntityViewCommand)} method.
	 *
	 * @param entityViewRequest view request
	 * @param command           object
	 * @param errors            for the command object
	 * @param httpMethod        http method requested
	 */
	protected void validateCommandObject( EntityViewRequest entityViewRequest, EntityViewCommand command, Errors errors, HttpMethod httpMethod ) {
	}

	/**
	 * Pre-process the view before the control and rendering methods get called
	 *
	 * @param entityViewRequest view request
	 * @param entityView        view generated
	 * @param command           object
	 */
	protected void preProcess( EntityViewRequest entityViewRequest, EntityView entityView, EntityViewCommand command ) {

	}

	@Override
	public final void doControl( EntityViewRequest entityViewRequest,
	                             EntityView entityView,
	                             EntityViewCommand command ) {
		HttpMethod httpMethod = entityViewRequest.getHttpMethod();
		doControl( entityViewRequest, entityView, command, entityViewRequest.getBindingResult(), httpMethod );

		if ( HttpMethod.GET == httpMethod ) {
			doGet( entityViewRequest, entityView, command );
		}
		else if ( HttpMethod.POST == httpMethod ) {
			doPost( entityViewRequest, entityView, command, entityViewRequest.getBindingResult() );
		}
	}

	/**
	 * Initial control method that is called for all HTTP methods.
	 *
	 * @param entityViewRequest view request
	 * @param entityView        view generated
	 * @param command           object
	 * @param bindingResult     for the command object
	 * @param httpMethod        http method requested
	 */
	protected void doControl( EntityViewRequest entityViewRequest,
	                          EntityView entityView,
	                          EntityViewCommand command,
	                          BindingResult bindingResult,
	                          HttpMethod httpMethod ) {
	}

	/**
	 * Control method called only if the HTTP method is GET.
	 *
	 * @param entityViewRequest view request
	 * @param entityView        view generated
	 * @param command           object
	 */
	protected void doGet( EntityViewRequest entityViewRequest,
	                      EntityView entityView,
	                      EntityViewCommand command ) {
	}

	/**
	 * Control method called only if the HTTP method is POST.
	 *
	 * @param entityViewRequest view request
	 * @param entityView        view generated
	 * @param command           object
	 * @param bindingResult     for the command object
	 */
	protected void doPost( EntityViewRequest entityViewRequest,
	                       EntityView entityView,
	                       EntityViewCommand command,
	                       BindingResult bindingResult ) {
	}

	@Override
	public final void preRender( EntityViewRequest entityViewRequest,
	                             EntityView entityView ) {
		ViewElementBuilderMap builderMap = retrieveEntityViewElementBuilderMap( entityView );

		WebResourceUtils.getRegistry( entityViewRequest.getWebRequest() )
		                .ifPresent( registry -> registerWebResources( entityViewRequest, entityView, registry ) );

		ViewElementBuilderContext builderContext = retrieveBuilderContext();

		prepareViewElementBuilderContext( entityViewRequest, entityView, builderContext );

		createViewElementBuilders( entityViewRequest, entityView, builderMap );
	}

	/**
	 * Customize the {@link ViewElementBuilderContext} that should be used.
	 *
	 * @param entityViewRequest view request
	 * @param entityView        view generated
	 * @param builderContext    that will be used
	 */
	protected void prepareViewElementBuilderContext( EntityViewRequest entityViewRequest, EntityView entityView, ViewElementBuilderContext builderContext ) {

	}

	/**
	 * Register one or more web resources in the registry.  This method will only be executed if there is a
	 * {@link WebResourceRegistry} for the current request.
	 *
	 * @param entityViewRequest   view request
	 * @param entityView          view generated
	 * @param webResourceRegistry for the current request
	 */
	protected void registerWebResources( EntityViewRequest entityViewRequest,
	                                     EntityView entityView,
	                                     WebResourceRegistry webResourceRegistry ) {
	}

	/**
	 * Create the initial {@link com.foreach.across.modules.web.ui.ViewElementBuilder} instances you would like to allow next processors to customize.
	 *
	 * @param entityViewRequest view request
	 * @param entityView        view generated
	 * @param builderMap        named collection of builders
	 */
	protected void createViewElementBuilders( EntityViewRequest entityViewRequest,
	                                          EntityView entityView,
	                                          ViewElementBuilderMap builderMap ) {
	}

	@Override
	public final void render( EntityViewRequest entityViewRequest,
	                          EntityView entityView ) {
		// get container builder, get builder map
		ViewElementBuilderMap builderMap = retrieveEntityViewElementBuilderMap( entityView );
		ContainerViewElementBuilderSupport<?, ?> containerViewElementBuilder
				= entityView.getAttribute( ATTRIBUTE_CONTAINER_BUILDER, ContainerViewElementBuilderSupport.class );

		render( entityViewRequest, entityView, containerViewElementBuilder, builderMap, retrieveBuilderContext() );
	}

	/**
	 * Build the general structure to render by adding the builders from {@code builderMap} to the {@code containerBuilder} in the right hierarchy.
	 * The {@code containerBuilder} represents the main body content.
	 *
	 * @param entityViewRequest view request
	 * @param entityView        view generated
	 * @param containerBuilder  builder for the main content
	 * @param builderMap        named collection of builders
	 * @param builderContext    that should be used
	 */
	protected void render( EntityViewRequest entityViewRequest,
	                       EntityView entityView,
	                       ContainerViewElementBuilderSupport<?, ?> containerBuilder,
	                       ViewElementBuilderMap builderMap,
	                       ViewElementBuilderContext builderContext ) {
	}

	@Override
	public final void postRender( EntityViewRequest entityViewRequest,
	                              EntityView entityView ) {
		entityView.removeAttribute( ATTRIBUTE_BUILDER_MAP );

		ContainerViewElement container = entityView.getAttribute( ATTRIBUTE_CONTAINER_ELEMENT, ContainerViewElement.class );

		postRender( entityViewRequest, entityView, container, retrieveBuilderContext() );
	}

	/**
	 * Modify the built container of {@link ViewElement}s.
	 *
	 * @param entityViewRequest view request
	 * @param entityView        view generated
	 * @param container         holding the main content elements
	 * @param builderContext    that was used
	 */
	protected void postRender( EntityViewRequest entityViewRequest,
	                           EntityView entityView,
	                           ContainerViewElement container,
	                           ViewElementBuilderContext builderContext ) {
	}

	@Override
	public void postProcess( EntityViewRequest entityViewRequest, EntityView entityView ) {
	}

	static ViewElementBuilderMap retrieveEntityViewElementBuilderMap( EntityView entityView ) {
		return (ViewElementBuilderMap) entityView
				.asMap()
				.computeIfAbsent( ATTRIBUTE_BUILDER_MAP, key -> new ViewElementBuilderMap() );
	}

	static ViewElementBuilderContext retrieveBuilderContext() {
		return ViewElementBuilderContext
				.retrieveGlobalBuilderContext()
				.orElseThrow( () -> new IllegalStateException( "A global or request-bound ViewElementBuilderContext is required" ) );
	}
}
