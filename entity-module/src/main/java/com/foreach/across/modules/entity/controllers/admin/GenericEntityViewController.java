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

package com.foreach.across.modules.entity.controllers.admin;

import com.foreach.across.modules.adminweb.annotations.AdminWebController;
import com.foreach.across.modules.adminweb.ui.PageContentStructure;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityModel;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.EntityViewFactory;
import com.foreach.across.modules.entity.views.context.ConfigurableEntityViewContext;
import com.foreach.across.modules.entity.views.context.DefaultEntityViewContext;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.context.EntityViewContextLoader;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.entity.web.EntityModuleWebResources;
import com.foreach.across.modules.entity.web.EntityViewModel;
import com.foreach.across.modules.entity.web.links.EntityViewLinks;
import com.foreach.across.modules.web.context.WebAppPathResolver;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.template.WebTemplateInterceptor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpMethod;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Optional;

import static com.foreach.across.modules.entity.controllers.admin.GenericEntityViewController.PATH_ENTITY_TYPE;

/**
 * Generic controller for building entity views of non-associated entities.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@AdminWebController
@RequestMapping(PATH_ENTITY_TYPE)
@SuppressWarnings("unused")
public class GenericEntityViewController
{
	public static final String ROOT_PATH = "/entities";

	static final String PATH_ENTITY_TYPE = ROOT_PATH + "/{entityConfig:.+}";

	private static final String VAR_ENTITY_ID = "entityId";
	private static final String VAR_ENTITY = "entityConfig";
	private static final String VAR_ASSOCIATION = "associatedConfig";
	private static final String VAR_ASSOCIATED_ENTITY_ID = "associatedEntityId";

	private static final String PATH_ENTITY = "/{entityId}";
	private static final String PATH_ASSOCIATION = PATH_ENTITY + "/associations/{associatedConfig:.+}";
	private static final String PATH_ASSOCIATED_ENTITY = PATH_ASSOCIATION + "/{associatedEntityId}";

	private ConfigurableEntityViewContext entityViewContext;
	private EntityViewRequest entityViewRequest;
	private PageContentStructure pageContentStructure;
	private EntityViewContextLoader entityViewContextLoader;
	private WebAppPathResolver webAppPathResolver;
	private EntityViewLinks entityViewLinks;
	private ConversionService mvcConversionService;

	/**
	 * Responsible for building the initial {@link com.foreach.across.modules.entity.views.context.EntityViewContext}
	 * and {@link EntityViewRequest}.
	 */
	@ModelAttribute
	public void createEntityViewRequest(
			@PathVariable(VAR_ENTITY) String entityName,
			@PathVariable(value = VAR_ENTITY_ID, required = false) Serializable entityId,
			@PathVariable(value = VAR_ASSOCIATION, required = false) String associationName,
			@PathVariable(value = VAR_ASSOCIATED_ENTITY_ID, required = false) Serializable associatedEntityId,
			@PathVariable(value = "action", required = false) String action,
			HttpMethod httpMethod,
			NativeWebRequest webRequest,
			ModelMap model,
			RedirectAttributes redirectAttributes
	) {
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

		model.addAttribute( EntityViewModel.VIEW_REQUEST, entityViewRequest );
		model.addAttribute( EntityViewModel.VIEW_COMMAND, entityViewRequest.getCommand() );
		model.addAttribute( EntityViewModel.VIEW_CONTEXT, entityViewContext );
	}

	@ModelAttribute
	public void registerWebResources( WebResourceRegistry webResourceRegistry ) {
		webResourceRegistry.addPackage( EntityModuleWebResources.NAME );
	}

	@InitBinder(EntityViewModel.VIEW_COMMAND)
	public void initViewCommandBinder( WebDataBinder dataBinder ) {
		entityViewRequest.setDataBinder( dataBinder );

		dataBinder.setMessageCodesResolver( new EntityViewMessageCodesResolverProxy( entityViewContext ) );

		EntityViewFactory viewFactory = entityViewRequest.getViewFactory();
		viewFactory.initializeCommandObject( entityViewRequest, entityViewRequest.getCommand(), dataBinder );
	}

	@RequestMapping(value = { "",
	                          PATH_ENTITY,
	                          PATH_ENTITY + "/{action:delete|update}",
	                          PATH_ASSOCIATION,
	                          PATH_ASSOCIATED_ENTITY,
	                          PATH_ASSOCIATED_ENTITY + "/{action:delete|update}"
	})
	public Object executeView( @NonNull @ModelAttribute(EntityViewModel.VIEW_COMMAND) EntityViewCommand command, BindingResult bindingResult ) {
		entityViewRequest.setBindingResult( bindingResult );

		EntityView entityView = entityViewRequest.getViewFactory().createView( entityViewRequest );

		if ( entityView.isRedirect() ) {
			return webAppPathResolver.redirect( entityView.getRedirectUrl() );
		}
		else if ( entityView.isCustomView() ) {
			return entityView.getCustomView();
		}

		return StringUtils.defaultString( entityView.getTemplate(), PageContentStructure.TEMPLATE );
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
	void setWebAppPathResolver( WebAppPathResolver webAppPathResolver ) {
		this.webAppPathResolver = webAppPathResolver;
	}

	@Autowired
	void setEntityViewLinks( EntityViewLinks entityViewLinks ) {
		this.entityViewLinks = entityViewLinks;
	}

	@Autowired
	@Qualifier("mvcConversionService")
	void setMvcConversionService( ConversionService conversionService ) {
		this.mvcConversionService = conversionService;
	}

	/**
	 * Proxy wrapping around the current message code resolver attached to the view context, which should never be {@code null}.
	 */
	@RequiredArgsConstructor
	private static class EntityViewMessageCodesResolverProxy implements MessageCodesResolver
	{
		private final EntityViewContext viewContext;

		@Override
		public String[] resolveMessageCodes( String errorCode, String objectName ) {
			return viewContext.getMessageCodeResolver().resolveMessageCodes( errorCode, objectName );
		}

		@Override
		public String[] resolveMessageCodes( String errorCode, String objectName, String field, Class<?> fieldType ) {
			return viewContext.getMessageCodeResolver().resolveMessageCodes( errorCode, objectName, field, fieldType );
		}
	}
}
