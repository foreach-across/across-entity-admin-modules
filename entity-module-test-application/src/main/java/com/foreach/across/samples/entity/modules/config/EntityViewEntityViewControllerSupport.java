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
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.EntityViewFactory;
import com.foreach.across.modules.entity.views.context.ConfigurableEntityViewContext;
import com.foreach.across.modules.entity.views.context.DefaultEntityViewContext;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.context.EntityViewContextLoader;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.samples.entity.modules.utils.RequestUtils;
import com.foreach.across.samples.entity.modules.web.EntityViewMessageCodesResolverProxy;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ModelMethodProcessor;
import org.springframework.web.method.support.*;
import org.springframework.web.servlet.mvc.method.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiFunction;

public interface EntityViewEntityViewControllerSupport extends EntityViewControllerSupport
{
	ReflectionUtils.MethodFilter ENTITY_INSTANCE_RESOLVER_METHODS = method ->
			AnnotatedElementUtils.hasAnnotation( method, EntityInstanceResolver.class );

	ReflectionUtils.MethodFilter ASSOCIATION_INSTANCE_RESOLVER_METHODS = method ->
			AnnotatedElementUtils.hasAnnotation( method, EntityAssociationInstanceResolver.class );

	List<HandlerMethodArgumentResolver> DEFAULT_ARGUMENT_RESOLVERS = Arrays.asList( new SessionAttributeMethodArgumentResolver(),
	                                                                                new RequestAttributeMethodArgumentResolver(),
	                                                                                new ServletRequestMethodArgumentResolver(),
	                                                                                new ServletResponseMethodArgumentResolver(),
	                                                                                new RedirectAttributesMethodArgumentResolver(),
	                                                                                new ModelMethodProcessor(),
	                                                                                new PrincipalMethodArgumentResolver()
	);

	BiFunction<ConversionService, Class<?>, HandlerMethodReturnValueHandler> DEFAULT_RETURN_VALUE_HANDLER =
			( conversionService, clazz ) -> new HandlerMethodReturnValueHandler()
			{
				@Override
				public boolean supportsReturnType( MethodParameter returnType ) {
					return true;
				}

				@Override
				public void handleReturnValue( Object returnValue,
				                               MethodParameter returnType,
				                               ModelAndViewContainer mavContainer, NativeWebRequest webRequest ) {
					mavContainer.setView( conversionService.convert( returnValue, clazz ) );
				}
			};

	@Override
	default void configureViewContext( EntityRegistry entityRegistry,
	                                   ConversionService conversionService,
	                                   ConfigurableEntityViewContext entityViewContext,
	                                   HttpServletRequest httpServletRequest,
	                                   EntityViewContextLoader loader ) {
		EntityViewContextParams params = resolveEntityViewContextParams( entityRegistry, conversionService, httpServletRequest );
		if ( StringUtils.isNotBlank( params.getAssociationName() ) ) {
			EntityViewContext parentContext = configureConfigurationContext( new DefaultEntityViewContext(), params.getConfigurationName(),
			                                                                 params.getInstance(), loader
			);
			configureAssociationContext( entityViewContext, parentContext, params.getAssociationName(), params.getAssociationInstance(), loader
			);
		}
		else {
			configureConfigurationContext( entityViewContext, params.getConfigurationName(), params.getInstance(), loader
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
		String viewName = resolveViewName();
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

	@SneakyThrows
	default EntityViewContextParams resolveEntityViewContextParams( EntityRegistry entityRegistry,
	                                                                ConversionService conversionService,
	                                                                HttpServletRequest httpServletRequest ) {
		String associationName = resolveAssociationName();
		HandlerMethod handlerMethod = EntityViewControllerHandlerResolver.currentHandlerMethod();
		EntityViewController annotation = AnnotationUtils.getAnnotation( handlerMethod.getMethod().getDeclaringClass(), EntityViewController.class );
		if ( annotation == null ) {
			throw new RuntimeException( "Annotate your controller with @EntityViewController and specify 'target' or 'targetType'" );
		}

		EntityConfiguration<?> entityConfiguration;
		if ( StringUtils.isNotBlank( annotation.target() ) ) {
			entityConfiguration = entityRegistry.getEntityConfiguration( annotation.target() );
		}
		else if ( annotation.targetType() != void.class ) {
			entityConfiguration = entityRegistry.getEntityConfiguration( annotation.targetType() );
		}
		else {
			throw new RuntimeException( "Cannot determine type of @EntityViewController" );
		}

		EntityViewContextParams.EntityViewContextParamsBuilder builder = EntityViewContextParams.builder();
		builder.configurationName( annotation.target() );

		String entityIdMappedBy = annotation.entityIdMappedBy();
		Optional<?> entityId = RequestUtils.getPathVariable( entityIdMappedBy );
		if ( !entityId.isPresent() ) {
			entityId = Optional.ofNullable( RequestUtils.getCurrentRequest().getParameter( entityIdMappedBy ) );
		}
		if ( !entityId.isPresent() ) {
			applyInstanceResolvers( ENTITY_INSTANCE_RESOLVER_METHODS, conversionService, handlerMethod, entityConfiguration, builder );
		}
		else {
			Object resolvedEntity = entityId.map( o -> conversionService.convert( o, entityConfiguration.getEntityType() ) ).orElse( null );
			builder.instance( resolvedEntity );
		}
		if ( StringUtils.isNotBlank( associationName ) ) {
			builder.associationName( associationName );
			applyInstanceResolvers( ASSOCIATION_INSTANCE_RESOLVER_METHODS, conversionService, handlerMethod, entityConfiguration, builder );
		}
		return builder.build();
	}

	default void applyInstanceResolvers( ReflectionUtils.MethodFilter methodFilter, ConversionService conversionService,
	                                     HandlerMethod handlerMethod,
	                                     EntityConfiguration<?> entityConfiguration,
	                                     EntityViewContextParams.EntityViewContextParamsBuilder builder ) throws Exception {
		Set<Method> methods = MethodIntrospector.selectMethods( handlerMethod.getBeanType(), methodFilter );
		for ( Method method : methods ) {
			if ( method != null ) {
				ServletInvocableHandlerMethod servletInvocableHandlerMethod = new ServletInvocableHandlerMethod( handlerMethod.getBean(), method );

				HandlerMethodArgumentResolverComposite argumentHandlerComposite = new HandlerMethodArgumentResolverComposite();
				HandlerMethodReturnValueHandlerComposite returnValueHandlerComposite = new HandlerMethodReturnValueHandlerComposite();

				argumentHandlerComposite.addResolvers( retrieveArgumentResolvers() );
				returnValueHandlerComposite.addHandlers( retrieveReturnValueHandlers( conversionService, entityConfiguration.getEntityType() ) );

				servletInvocableHandlerMethod.setHandlerMethodReturnValueHandlers( returnValueHandlerComposite );
				servletInvocableHandlerMethod.setHandlerMethodArgumentResolvers( argumentHandlerComposite );

				ServletWebRequest webRequest = new ServletWebRequest( RequestUtils.getCurrentRequest(), RequestUtils.getCurrentResponse() );
				ModelAndViewContainer mavContainer = new ModelAndViewContainer();
				servletInvocableHandlerMethod.invokeAndHandle( webRequest, mavContainer );

				if ( mavContainer.getView() != null ) {
					builder.instance( mavContainer.getView() );
					break;
				}
			}
		}
	}

	default List<HandlerMethodArgumentResolver> retrieveArgumentResolvers() {
		return DEFAULT_ARGUMENT_RESOLVERS;
	}

	default List<HandlerMethodReturnValueHandler> retrieveReturnValueHandlers( ConversionService conversionService, Class<?> clazz ) {
		return Collections.singletonList( DEFAULT_RETURN_VALUE_HANDLER.apply( conversionService, clazz ) );
	}

	default String resolveViewName() {
		HandlerMethod handlerMethod = EntityViewControllerHandlerResolver.currentHandlerMethod();
		ViewFactory viewFactory = AnnotationUtils.getAnnotation( handlerMethod.getMethod(), ViewFactory.class );
		if ( viewFactory == null || StringUtils.isBlank( viewFactory.view() ) ) {
			throw new RuntimeException( "Annotate your method with @ViewFactory or implement resolveViewName()" );
		}
		return viewFactory.view();
	}

	default String resolveAssociationName() {
		HandlerMethod handlerMethod = EntityViewControllerHandlerResolver.currentHandlerMethod();
		ViewFactory viewFactory = AnnotationUtils.getAnnotation( handlerMethod.getMethod(), ViewFactory.class );
		if ( viewFactory == null || StringUtils.isBlank( viewFactory.association() ) ) {
			throw new RuntimeException( "Annotate your method with @ViewFactory or implement resolveViewName()" );
		}
		return viewFactory.association();
	}
}
