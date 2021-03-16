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

import com.foreach.across.modules.adminweb.ui.PageContentStructure;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityModel;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.EntityViewFactory;
import com.foreach.across.modules.entity.views.context.ConfigurableEntityViewContext;
import com.foreach.across.modules.entity.views.context.DefaultEntityViewContext;
import com.foreach.across.modules.entity.views.context.EntityViewContextLoader;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.entity.web.EntityModuleWebResources;
import com.foreach.across.modules.entity.web.EntityViewModel;
import com.foreach.across.modules.entity.web.links.EntityViewLinks;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.template.WebTemplateInterceptor;
import com.foreach.across.samples.entity.modules.web.EntityViewMessageCodesResolverProxy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpMethod;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Optional;

@ControllerAdvice(annotations = { EntityViewController.class })
@Slf4j
@SuppressWarnings("Duplicates")
public class EntityViewControllerContextAdvice
{
	private ConfigurableEntityViewContext entityViewContext;
	private EntityViewRequest entityViewRequest;
	private PageContentStructure pageContentStructure;
	private EntityViewContextLoader entityViewContextLoader;
	private EntityViewLinks entityViewLinks;
	private ConversionService mvcConversionService;
	private EntityRegistry entityRegistry;

	/**
	 * Responsible for building the initial {@link com.foreach.across.modules.entity.views.context.EntityViewContext}
	 * and {@link EntityViewRequest}.
	 */
	@ModelAttribute
	public void createEntityViewRequest(
//			@PathVariable(VAR_ENTITY) String entityName,
//			@PathVariable(value = VAR_ENTITY_ID, required = false) Serializable entityId,
//			@PathVariable(value = VAR_ASSOCIATION, required = false) String associationName,
//			@PathVariable(value = VAR_ASSOCIATED_ENTITY_ID, required = false) Serializable associatedEntityId,
//			@PathVariable(value = "action", required = false) String action,
			HttpMethod httpMethod,
			NativeWebRequest webRequest,
			ModelMap model,
			RedirectAttributes redirectAttributes
	) {
		if ( EntityViewControllerHandlerResolver.isEntityViewControllerSupportHandler() ) {
			HandlerMethod handlerMethod = EntityViewControllerHandlerResolver.currentHandlerMethod();
			EntityViewControllerSupport handler = (EntityViewControllerSupport) handlerMethod.getBean();

			HttpServletRequest httpServletRequest = webRequest.getNativeRequest( HttpServletRequest.class );
			handler.configureViewContext( entityRegistry, mvcConversionService, entityViewContext, httpServletRequest, entityViewContextLoader );

			entityViewRequest.setEntityViewContext( entityViewContext );
			entityViewRequest.setModel( model );
			entityViewRequest.setRedirectAttributes( redirectAttributes );
			entityViewRequest.setPageContentStructure( pageContentStructure );
			entityViewRequest.setWebRequest( webRequest );
			entityViewRequest.setHttpMethod( httpMethod );
			entityViewRequest.setCommand( new EntityViewCommand() );
			entityViewRequest.setPartialFragment(
					StringUtils.defaultIfBlank( webRequest.getParameter( WebTemplateInterceptor.PARTIAL_PARAMETER ), null )
			);

			handler.configureEntityViewRequest( entityViewRequest, entityViewContext, httpServletRequest );
		}
		else {
			Class<?> declaringClass = EntityViewControllerHandlerResolver.currentHandlerClass();
			EntityViewController annotation = declaringClass.getAnnotation( EntityViewController.class );
			String entityName = resolveTargetEntityType( annotation );
			String entityId = null;//"create";
			String associationName = null;
			String associatedEntityId = null;
			String action = null;

			buildEntityViewContext( entityName, entityId, associationName, associatedEntityId );

			// add the basic properties
			String viewName = resolveViewName( webRequest, entityViewContext.isForAssociation() ? associatedEntityId : entityId, action );

			entityViewRequest.setModel( model );
			entityViewRequest.setRedirectAttributes( redirectAttributes );
			entityViewRequest.setPageContentStructure( pageContentStructure );
			entityViewRequest.setEntityViewContext( entityViewContext );
			entityViewRequest.setWebRequest( webRequest );
			entityViewRequest.setHttpMethod( httpMethod );
			entityViewRequest.setViewName( viewName );
			entityViewRequest.setCommand( new EntityViewCommand() );
			entityViewRequest.setPartialFragment(
					StringUtils.defaultIfBlank( webRequest.getParameter( WebTemplateInterceptor.PARTIAL_PARAMETER ), null )
			);

			// retrieve and set the view factory
			EntityViewFactory viewFactory = entityViewContext.isForAssociation()
					? entityViewContext.getEntityAssociation().getViewFactory( viewName )
					: entityViewContext.getEntityConfiguration().getViewFactory( viewName );

			if ( viewFactory == null ) {
				throw new IllegalStateException( "No registered EntityViewFactory with name: " + viewName );
			}

			entityViewRequest.setViewFactory( viewFactory );
			entityViewRequest.setConfigurationAttributes( new HashMap<>( viewFactory.attributeMap() ) );

			viewFactory.prepareEntityViewContext( entityViewContext );

			viewFactory.authorizeRequest( entityViewRequest );
		}

		model.addAttribute( EntityViewModel.VIEW_REQUEST, entityViewRequest );
		model.addAttribute( EntityViewModel.VIEW_COMMAND, entityViewRequest.getCommand() );
		model.addAttribute( EntityViewModel.VIEW_CONTEXT, entityViewContext );

	}

	private String resolveTargetEntityType( EntityViewController annotation ) {
		if ( StringUtils.isNotBlank( annotation.target() ) ) {
			return annotation.target();
		}
		if ( void.class != annotation.targetType() ) {
			return annotation.targetType().getName();
		}
		return null;
	}

	@ModelAttribute
	public void registerWebResources( WebResourceRegistry webResourceRegistry ) {
		webResourceRegistry.addPackage( EntityModuleWebResources.NAME );

		if ( EntityViewControllerHandlerResolver.isEntityViewControllerSupportHandler() ) {
			HandlerMethod handlerMethod = EntityViewControllerHandlerResolver.currentHandlerMethod();
			EntityViewControllerSupport handler = (EntityViewControllerSupport) handlerMethod.getBean();

			handler.registerWebResources( webResourceRegistry );
		}
	}

	@InitBinder(EntityViewModel.VIEW_COMMAND)
	public void initViewCommandBinder( WebDataBinder dataBinder ) {
		if ( EntityViewControllerHandlerResolver.isEntityViewControllerSupportHandler() ) {
			HandlerMethod handlerMethod = EntityViewControllerHandlerResolver.currentHandlerMethod();
			EntityViewControllerSupport handler = (EntityViewControllerSupport) handlerMethod.getBean();

			handler.configureEntityViewCommandBinder( dataBinder, entityViewRequest );
		}
		else {
			entityViewRequest.setDataBinder( dataBinder );

			dataBinder.setMessageCodesResolver( new EntityViewMessageCodesResolverProxy( entityViewContext ) );

			EntityViewFactory viewFactory = entityViewRequest.getViewFactory();
			viewFactory.initializeCommandObject( entityViewRequest, entityViewRequest.getCommand(), dataBinder );
		}
	}

	@SuppressWarnings("unchecked")
	private void buildEntityViewContext( String entityName,
	                                     Serializable entityId,
	                                     String associationName,
	                                     Serializable associatedEntityId ) {
		if ( associationName != null ) {
			DefaultEntityViewContext parentViewContext = new DefaultEntityViewContext();
			entityViewContextLoader.loadForEntityConfiguration( parentViewContext, entityName );
			EntityConfiguration parentEntityConfiguration = parentViewContext.getEntityConfiguration();
			parentViewContext.setEntity( findEntity( entityId, parentEntityConfiguration.getEntityModel() ) );

			EntityAssociation association = parentEntityConfiguration.association( associationName );
			entityViewContextLoader.loadForEntityConfiguration( entityViewContext, association.getTargetEntityConfiguration() );
			entityViewContext.setEntityAssociation( association );
			entityViewContext.setParentContext( parentViewContext );
			entityViewContext.setLinkBuilder( entityViewLinks.linkTo( parentEntityConfiguration ).withId( entityId ).association( associationName ) );

			EntityMessageCodeResolver codeResolver = association.getAttribute( EntityMessageCodeResolver.class );
			if ( codeResolver != null ) {
				entityViewContext.setMessageCodeResolver( codeResolver );
				entityViewContext.setEntityMessages( new EntityMessages( codeResolver ) );
			}

			if ( isPossibleEntityId( associatedEntityId ) ) {
				entityViewContext.setEntity( findEntity( associatedEntityId, entityViewContext.getEntityModel() ) );
			}
		}
		else {
			entityViewContextLoader.loadForEntityConfiguration( entityViewContext, entityName );

			if ( isPossibleEntityId( entityId ) ) {
				entityViewContext.setEntity( findEntity( entityId, entityViewContext.getEntityModel() ) );
			}
		}
	}

	private Object findEntity( Serializable entityId, EntityModel<Object, Serializable> entityModel ) {
		if ( mvcConversionService.canConvert( entityId.getClass(), entityModel.getIdType() ) ) {
			return entityModel.findOne( mvcConversionService.convert( entityId, entityModel.getIdType() ) );
		}
		return entityModel.findOne( entityId );
	}

	private boolean isPossibleEntityId( Serializable candidate ) {
		return candidate != null && !"create".equals( candidate );
	}

	private String resolveViewName( NativeWebRequest webRequest, Serializable entityId, String action ) {
		return Optional
				.ofNullable( webRequest.getParameter( "view" ) )
				.orElseGet( () -> {
					if ( entityViewContext.holdsEntity() ) {
						if ( "delete".equals( action ) ) {
							return EntityView.DELETE_VIEW_NAME;
						}
						else if ( "update".equals( action ) ) {
							return EntityView.UPDATE_VIEW_NAME;
						}

						return "view";
					}

					return "create".equals( entityId ) ? EntityView.CREATE_VIEW_NAME : EntityView.LIST_VIEW_NAME;
				} );
	}

	@Autowired
	void setEntityViewContextLoader( EntityViewContextLoader entityViewContextLoader ) {
		this.entityViewContextLoader = entityViewContextLoader;
	}

	@Autowired
	void setEntityViewContext( ConfigurableEntityViewContext entityViewContext ) {
		this.entityViewContext = entityViewContext;
	}

	@Autowired
	void setEntityViewRequest( EntityViewRequest entityViewRequest ) {
		this.entityViewRequest = entityViewRequest;
	}

	@Autowired
	void setPageContentStructure( PageContentStructure pageContentStructure ) {
		this.pageContentStructure = pageContentStructure;
	}

	@Autowired
	void setEntityViewLinks( EntityViewLinks entityViewLinks ) {
		this.entityViewLinks = entityViewLinks;
	}

	@Autowired
	public void setEntityRegistry( EntityRegistry entityRegistry ) {
		this.entityRegistry = entityRegistry;
	}

	@Autowired
	@Qualifier("mvcConversionService")
	void setMvcConversionService( ConversionService conversionService ) {
		this.mvcConversionService = conversionService;
	}

}
