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

import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.EntityViewFactory;
import com.foreach.across.modules.entity.views.context.ConfigurableEntityViewContext;
import com.foreach.across.modules.entity.views.context.DefaultEntityViewContext;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.context.EntityViewContextLoader;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.samples.entity.modules.web.EntityViewMessageCodesResolverProxy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.WebDataBinder;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

public interface EntityViewEntityViewControllerSupport extends EntityViewControllerSupport
{
	@Override
	default void configureViewContext( ConfigurableEntityViewContext beanCtx, HttpServletRequest httpServletRequest,
	                                   EntityViewContextLoader loader ) {
		EntityViewContextParams params = resolveEntityViewContextParams( httpServletRequest );
		if ( StringUtils.isNotBlank( params.getAssociationName() ) ) {
			EntityViewContext parentContext = configureConfigurationContext( new DefaultEntityViewContext(), params.getConfigurationName(),
			                                                                 params.getInstance(), loader
			);
			configureAssociationContext( beanCtx, parentContext, params.getAssociationName(), params.getAssociationInstance(), loader
			);
		}
		else {
			configureConfigurationContext( beanCtx, params.getConfigurationName(), params.getInstance(), loader
			);
		}
	}

	default EntityViewContext configureConfigurationContext( ConfigurableEntityViewContext context,
	                                                         String name,
	                                                         Object instance,
	                                                         EntityViewContextLoader contextLoader ) {
		if ( StringUtils.isNotBlank( name ) ) {
			contextLoader.loadForEntityConfiguration( context, name );
		}
		else {
			contextLoader.loadForEntity( context, instance );
		}
		context.setEntity( instance );
		return context;
	}

	default EntityViewContext configureAssociationContext(
			ConfigurableEntityViewContext context, EntityViewContext parentContext, String name, Object instance, EntityViewContextLoader contextLoader
	) {
		EntityAssociation association = parentContext.getEntityConfiguration().association( name );
		contextLoader.loadForEntityConfiguration( context, association.getTargetEntityConfiguration() );
		context.setEntityAssociation( association );
		context.setParentContext( parentContext );
		context.setLinkBuilder( parentContext.getLinkBuilder().forInstance( parentContext.getEntity() )
		                                     .association( association.getName() ) );
		EntityMessageCodeResolver codeResolver = association.getAttribute( EntityMessageCodeResolver.class );
		if ( codeResolver != null ) {
			context.setMessageCodeResolver( codeResolver );
			context.setEntityMessages( new EntityMessages( codeResolver ) );
		}
		context.setEntity( instance );
		return context;
	}

	@Override
	default void configureEntityViewRequest( EntityViewRequest entityViewRequest,
	                                         ConfigurableEntityViewContext entityViewContext,
	                                         HttpServletRequest httpServletRequest ) {
		String viewName = resolveViewName( httpServletRequest, entityViewContext );
		entityViewRequest.setViewName( viewName );
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

	@Override
	default void configureEntityViewCommandBinder( WebDataBinder dataBinder, EntityViewRequest request ) {
		request.setDataBinder( dataBinder );

		dataBinder.setMessageCodesResolver( new EntityViewMessageCodesResolverProxy( request.getEntityViewContext() ) );

		EntityViewFactory viewFactory = request.getViewFactory();
		viewFactory.initializeCommandObject( request, request.getCommand(), dataBinder );
	}

	@Override
	default void registerWebResources( WebResourceRegistry webResourceRegistry ) {

	}

	EntityViewContextParams resolveEntityViewContextParams( HttpServletRequest httpServletRequest );

	String resolveViewName( HttpServletRequest httpServletRequest, EntityViewContext entityViewContext );
}
