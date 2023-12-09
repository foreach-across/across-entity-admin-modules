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

import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.processors.support.ViewElementBuilderMap;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.builder.ContainerViewElementBuilderSupport;
import org.springframework.http.HttpMethod;
import org.springframework.util.ClassUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;

/**
 * Base class for managing an extension component on a view.
 * Specialization of {@link EntityViewProcessorAdapter} that gives you easier access to a typed extension class.
 * Generally useful if a single processor manages the extension rendering, validation, update actions etc.
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
public abstract class ExtensionViewProcessorAdapter<T> extends EntityViewProcessorAdapter
{
	/**
	 * @return the control prefix for binding properties to this extension object
	 */
	protected final String controlPrefix() {
		return "extensions[" + extensionName() + "]";
	}

	/**
	 * Optionally provide a unique key of the extension. If not overridden, the short class name will be used.
	 * It's advised for shared processors to define their own unique name.
	 *
	 * @return unique key of the extension
	 */
	protected String extensionName() {
		return ClassUtils.getShortName( getClass() );
	}

	@Override
	public final void initializeCommandObject( EntityViewRequest entityViewRequest, EntityViewCommand command, WebDataBinder dataBinder ) {
		command.addExtension( extensionName(), createExtension( entityViewRequest, command, dataBinder ) );
	}

	/**
	 * Create the extension instance an optionally customize databinder and view request.
	 *
	 * @param entityViewRequest of the view being requested
	 * @param command           full command object being used
	 * @param dataBinder        for binding properties
	 * @return extension to register
	 */
	protected abstract T createExtension( EntityViewRequest entityViewRequest, EntityViewCommand command, WebDataBinder dataBinder );

	@Override
	protected final void validateCommandObject( EntityViewRequest entityViewRequest, EntityViewCommand command, Errors errors, HttpMethod httpMethod ) {
		T extension = command.getExtension( extensionName() );
		errors.pushNestedPath( controlPrefix() );
		validateExtension( extension, errors, httpMethod, entityViewRequest );
		errors.popNestedPath();
	}

	/**
	 * Validate the extension object.
	 *
	 * @param extension         object
	 * @param errors            to register validation error on
	 * @param httpMethod        being used for the request
	 * @param entityViewRequest full request information
	 */
	protected void validateExtension( T extension, Errors errors, HttpMethod httpMethod, EntityViewRequest entityViewRequest ) {
	}

	@Override
	protected final void doControl( EntityViewRequest entityViewRequest,
	                                EntityView entityView,
	                                EntityViewCommand command,
	                                BindingResult bindingResult,
	                                HttpMethod httpMethod ) {
		doControl( command.getExtension( extensionName() ), bindingResult, httpMethod, entityView, entityViewRequest );
	}

	/**
	 * Initial control method that is called for all HTTP methods.
	 *
	 * @param extension         object
	 * @param bindingResult     for the command object
	 * @param httpMethod        http method requested
	 * @param entityView        view generated
	 * @param entityViewRequest view request
	 */
	protected void doControl( T extension, BindingResult bindingResult, HttpMethod httpMethod, EntityView entityView, EntityViewRequest entityViewRequest ) {
	}

	@Override
	protected final void doGet( EntityViewRequest entityViewRequest, EntityView entityView, EntityViewCommand command ) {
		doGet( command.getExtension( extensionName() ), entityView, entityViewRequest );
	}

	/**
	 * Control method called only if the HTTP method is GET.
	 *
	 * @param extension         object
	 * @param entityView        view generated
	 * @param entityViewRequest view request
	 */
	protected void doGet( T extension, EntityView entityView, EntityViewRequest entityViewRequest ) {
	}

	@Override
	protected final void doPost( EntityViewRequest entityViewRequest, EntityView entityView, EntityViewCommand command, BindingResult bindingResult ) {
		doPost( command.getExtension( extensionName() ), bindingResult, entityView, entityViewRequest );
	}

	/**
	 * Control method called only if the HTTP method is POST.
	 *
	 * @param extension         object
	 * @param bindingResult     for the command object
	 * @param entityView        view generated
	 * @param entityViewRequest view request
	 */
	protected void doPost( T extension, BindingResult bindingResult, EntityView entityView, EntityViewRequest entityViewRequest ) {
	}

	@Override
	protected final void createViewElementBuilders( EntityViewRequest entityViewRequest, EntityView entityView, ViewElementBuilderMap builderMap ) {
		createViewElementBuilders( entityViewRequest.getCommand().getExtension( extensionName() ), entityViewRequest, entityView, builderMap );
	}

	/**
	 * Create the initial {@link com.foreach.across.modules.web.ui.ViewElementBuilder} instances you would like to allow next processors to customize.
	 *
	 * @param extension         object
	 * @param entityViewRequest view request
	 * @param entityView        view generated
	 * @param builderMap        named collection of builders
	 */
	protected void createViewElementBuilders( T extension, EntityViewRequest entityViewRequest, EntityView entityView, ViewElementBuilderMap builderMap ) {
	}

	@Override
	protected final void render( EntityViewRequest entityViewRequest,
	                             EntityView entityView,
	                             ContainerViewElementBuilderSupport<?, ?> containerBuilder,
	                             ViewElementBuilderMap builderMap,
	                             ViewElementBuilderContext builderContext ) {
		render( entityViewRequest.getCommand().getExtension( extensionName() ), entityViewRequest, entityView, containerBuilder, builderMap, builderContext );
	}

	/**
	 * Build the general structure to render by adding the builders from {@param builderMap} to the {@param containerBuilder} in the right hierarchy.
	 * The {@param containerBuilder} represents the main body content.
	 *
	 * @param extension         object
	 * @param entityViewRequest view request
	 * @param entityView        view generated
	 * @param containerBuilder  builder for the main content
	 * @param builderMap        named collection of builders
	 * @param builderContext    that should be used
	 */
	protected void render( T extension,
	                       EntityViewRequest entityViewRequest,
	                       EntityView entityView,
	                       ContainerViewElementBuilderSupport<?, ?> containerBuilder,
	                       ViewElementBuilderMap builderMap,
	                       ViewElementBuilderContext builderContext ) {
	}

	@Override
	protected final void postRender( EntityViewRequest entityViewRequest,
	                                 EntityView entityView,
	                                 ContainerViewElement container,
	                                 ViewElementBuilderContext builderContext ) {
		postRender( entityViewRequest.getCommand().getExtension( extensionName() ), entityViewRequest, entityView, container, builderContext );
	}

	/**
	 * Modify the built container of {@link ViewElement}s.
	 *
	 * @param extension         object
	 * @param entityViewRequest view request
	 * @param entityView        view generated
	 * @param container         holding the main content elements
	 * @param builderContext    that was used
	 */
	protected void postRender( T extension,
	                           EntityViewRequest entityViewRequest,
	                           EntityView entityView,
	                           ContainerViewElement container,
	                           ViewElementBuilderContext builderContext ) {
	}
}

