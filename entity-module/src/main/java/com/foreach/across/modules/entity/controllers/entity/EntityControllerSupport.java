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
package com.foreach.across.modules.entity.controllers.entity;

import com.foreach.across.core.development.AcrossDevelopmentMode;
import com.foreach.across.modules.adminweb.ui.PageContentStructure;
import com.foreach.across.modules.bootstrapui.elements.builder.AlertViewElementBuilder;
import com.foreach.across.modules.entity.controllers.AbstractEntityModuleController;
import com.foreach.across.modules.entity.controllers.EntityViewRequest;
import com.foreach.across.modules.entity.controllers.ViewRequestValidator;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityModel;
import com.foreach.across.modules.entity.views.EntityFormView;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.EntityViewFactory;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.entity.web.WebViewCreationContextImpl;
import com.foreach.across.modules.spring.security.actions.AllowableAction;
import com.foreach.across.modules.spring.security.actions.AllowableActions;
import com.foreach.across.modules.web.template.WebTemplateInterceptor;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import java.io.Serializable;
import java.util.UUID;

/**
 * Support class for controllers generating entity views linked to a particular entity type and/or entity.
 *
 * @author Arne Vandamme
 */
public abstract class EntityControllerSupport extends AbstractEntityModuleController
{
	private static final String ATTRIBUTE_DATABINDER = EntityViewRequest.class.getName() + ".DataBinder";

	protected final Logger LOG = LoggerFactory.getLogger( getClass() );

	@Autowired
	private PageContentStructure page;

	@Autowired
	private ConversionService mvcConversionService;

	@Autowired
	private ViewRequestValidator viewRequestValidator;

	@Autowired
	private AcrossDevelopmentMode developmentMode;

	@SuppressWarnings("unchecked")
	protected Object buildViewRequest(
			EntityConfiguration entityConfiguration,
			boolean includeEntity,
			boolean includeDto,
			Serializable entityId,
			NativeWebRequest request,
			ModelMap model
	) {
		if ( entityConfiguration.isHidden() ) {
			LOG.warn( "EntityControllers for {} are disabled because the EntityConfiguration is hidden",
			          entityConfiguration.getName() );
			throw new AccessDeniedException( "Not allowed to manage this entity type." );
		}

		WebViewCreationContextImpl viewCreationContext = new WebViewCreationContextImpl();
		viewCreationContext.setRequest( request );

		String requestedViewName = request.getParameter( "view" );
		String partialFragment = request.getParameter( WebTemplateInterceptor.PARTIAL_PARAMETER );

		String viewName = StringUtils.defaultString( requestedViewName, getDefaultViewName() );

		EntityViewFactory viewFactory = entityConfiguration.getViewFactory( viewName );
		if ( viewFactory == null ) {
			throw new IllegalStateException( "No registered EntityViewFactory with name: " + viewName );
		}
		model.addAttribute( VIEW_FACTORY, viewFactory );

		viewCreationContext.setEntityConfiguration( entityConfiguration );
		model.addAttribute( CREATION_CONTEXT, viewCreationContext );

		EntityViewRequest viewRequest =
				new EntityViewRequest( viewName, viewFactory, viewCreationContext );
		viewRequest.setEntityName( entityConfiguration.getName() );
		model.addAttribute( VIEW_COMMAND, viewRequest );

		if ( StringUtils.isNotBlank( partialFragment ) ) {
			viewRequest.setPartialFragment( partialFragment );
		}

		if ( includeDto && !includeEntity ) {
			throw new RuntimeException( "Entity must be included if dto is requested." );
		}

		AllowableActions allowableActions;

		if ( includeEntity ) {
			if ( entityId != null ) {
				if ( includeDto ) {
					viewRequest.setEntity( buildUpdateDto( entityConfiguration, entityId, model ) );
				}
				else {
					viewRequest.setEntity( buildSourceEntityModel( entityConfiguration, entityId, model ) );
				}
			}
			else if ( includeDto ) {
				viewRequest.setEntity( buildNewEntityDto( entityConfiguration, model ) );
			}

			allowableActions = entityConfiguration.getAllowableActions( viewRequest.getEntity() );
		}
		else {
			allowableActions = entityConfiguration.getAllowableActions();
		}

		if ( !isAllowedAccess( entityConfiguration, allowableActions ) ) {
			LOG.warn( "Access denied because the security check on the AllowableActions returned false." );
			throw new AccessDeniedException( "Not allowed to manage this entity type." );
		}

		viewRequest.prepareModelAndCommand( model );

		request.setAttribute( EntityViewRequest.class.getName(), viewRequest, RequestAttributes.SCOPE_REQUEST );

		initViewFactoryBinder( request );

		preparePageContent( page );

		return viewRequest;
	}

	protected void preparePageContent( PageContentStructure page ) {
		page.setRenderAsTabs( true );
	}

	protected boolean isAllowedAccess( EntityConfiguration entityConfiguration, AllowableActions allowableActions ) {
		return allowableActions.contains( AllowableAction.READ );
	}

	protected boolean isDefaultView( String viewName ) {
		return StringUtils.equals( viewName, getDefaultViewName() );
	}

	protected abstract String getDefaultViewName();

	/**
	 * Create a DTO for a given entity.  This will load the source entity, add it to the model and then
	 * create a dto for it.
	 */
	@SuppressWarnings("unchecked")
	protected Object buildUpdateDto( EntityConfiguration entityConfiguration, Serializable entityId, ModelMap model ) {
		Object entity = buildSourceEntityModel( entityConfiguration, entityId, model );

		EntityModel entityModel = entityConfiguration.getEntityModel();
		Object dto = entityModel.createDto( entity );

		model.addAttribute( EntityView.ATTRIBUTE_ENTITY, dto );

		return dto;
	}

	/**
	 * Retrieve the source entity from the path.
	 */
	protected Object buildSourceEntityModel( EntityConfiguration<?> entityConfiguration,
	                                         Serializable entityId,
	                                         ModelMap model
	) {
		Object entity = entityConfiguration.getEntityModel().findOne( entityId );

		model.addAttribute( EntityFormView.ATTRIBUTE_ORIGINAL_ENTITY, entity );
		model.addAttribute( EntityView.ATTRIBUTE_ENTITY, entity );

		return entity;
	}

	protected Object buildNewEntityDto( EntityConfiguration<?> entityConfiguration, ModelMap model ) {
		EntityModel entityModel = entityConfiguration.getEntityModel();
		Object entity = entityModel.createNew();

		model.addAttribute( EntityView.ATTRIBUTE_ENTITY, entity );

		return entity;
	}

	@InitBinder(VIEW_REQUEST)
	protected void initBinder( @PathVariable(VAR_ENTITY) EntityConfiguration<?> entityConfiguration,
	                           WebRequest request,
	                           WebDataBinder binder ) {
		request.setAttribute( ATTRIBUTE_DATABINDER, binder, RequestAttributes.SCOPE_REQUEST );

		binder.setMessageCodesResolver( entityConfiguration.getEntityMessageCodeResolver() );
		binder.setValidator( viewRequestValidator );

		initViewFactoryBinder( request );
	}

	/**
	 * Allows the ViewFactory to customize DataBinder.  This should be done only once, however the calling order
	 * of @InitBinder and @ModelAttribute cannot be relied on, as such this method is called twice.  Dispatching
	 * to the ViewFactory should only be done once (before actual binding occurs).
	 */
	protected void initViewFactoryBinder( WebRequest request ) {
		EntityViewRequest viewRequest
				= (EntityViewRequest) request.getAttribute( EntityViewRequest.class.getName(),
				                                            RequestAttributes.SCOPE_REQUEST );
		WebDataBinder dataBinder
				= (WebDataBinder) request.getAttribute( ATTRIBUTE_DATABINDER, RequestAttributes.SCOPE_REQUEST );

		if ( viewRequest != null && dataBinder != null ) {
			viewRequest.initDataBinder( dataBinder );

			request.removeAttribute( ATTRIBUTE_DATABINDER, RequestAttributes.SCOPE_REQUEST );
		}
	}

	/**
	 * Logs the exception and returns the feedback if development mode is not active.
	 * In development mode exception will be thrown upwards instead.
	 */
	protected void buildExceptionLoggingModel( EntityConfiguration entityConfiguration,
	                                           RuntimeException thrown,
	                                           ModelMap model,
	                                           String message ) {
		if ( developmentMode.isActive() ) {
			throw thrown;
		}

		UUID exceptionId = UUID.randomUUID();
		LOG.error( "Exception [{}] in associated entity controller {}: {} ",
		           getClass().getSimpleName(),
		           entityConfiguration.getName(),
		           exceptionId,
		           thrown );

		EntityMessages messages = new EntityMessages( entityConfiguration.getEntityMessageCodeResolver() );
		Object entity = model.get( EntityView.ATTRIBUTE_ENTITY );
		String entityLabel = entity != null ? entityConfiguration.getLabel( entity ) : "";

		page.addToFeedback(
				new AlertViewElementBuilder()
						.danger()
						.dismissible()
						.add( TextViewElement.html( messages.withNameSingular( message, entityLabel, thrown.toString(),
						                                                       exceptionId ) ) )
						.build( new DefaultViewElementBuilderContext() )
		);
	}
}
