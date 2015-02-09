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

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.processors.ViewPostProcessor;
import com.foreach.across.modules.entity.views.processors.ViewPreProcessor;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.entity.web.EntityLinkBuilder;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;

/**
 * Base support class for entity view factories that support message resolving along with template specification.
 * It also allows a custom EntityLinkBuilder to be specified for the view.
 *
 * @author Arne Vandamme
 */
public abstract class SimpleEntityViewFactorySupport<T extends EntityView> implements EntityViewFactory
{
	private Collection<ViewPreProcessor<ViewCreationContext, T>> preProcessors = Collections.emptyList();
	private Collection<ViewPostProcessor<ViewCreationContext, T>> postProcessors = Collections.emptyList();
	private String template;
	private MessageSource messageSource;
	private EntityMessageCodeResolver messageCodeResolver;
	private EntityLinkBuilder entityLinkBuilder;
	private String[] messagePrefixes = new String[] { "entityViews" };

	/**
	 * @param template Template that should be used to render this view.
	 */
	public void setTemplate( String template ) {
		this.template = template;
	}

	public String getTemplate() {
		return template;
	}

	/**
	 * Set the message source that should be used for this view.  If none is specified, the default
	 * messagesource will be used.
	 *
	 * @param messageSource MessagesSource instance or null to use the default.
	 */
	public void setMessageSource( MessageSource messageSource ) {
		this.messageSource = messageSource;
	}

	/**
	 * Set the {@link com.foreach.across.modules.entity.support.EntityMessageCodeResolver} that should be
	 * used for looking up all the messages.  This resolver will first be prefixed with the configured
	 * prefixes and will use the message source configured on the view if there is one.
	 * <p/>
	 * If no resolver is specified, the default resolver from the set EntityConfiguration will be used.
	 *
	 * @param messageCodeResolver EntityMessageCodeResolver instance or null to use the entity default.
	 */
	public void setMessageCodeResolver( EntityMessageCodeResolver messageCodeResolver ) {
		this.messageCodeResolver = messageCodeResolver;
	}

	/**
	 * Set the prefixes that should be applied to the message keys.  These prefixes will be applied to
	 * the configured {@link com.foreach.across.modules.entity.support.EntityMessageCodeResolver}.
	 *
	 * @param messagePrefixes One or more message key prefixes.
	 */
	public void setMessagePrefixes( String... messagePrefixes ) {
		this.messagePrefixes = messagePrefixes;
	}

	/**
	 * Set the EntityLinkBuilder that should be used for this view.
	 *
	 * @param entityLinkBuilder EntityLinkBuilder to use or null to use the entity default.
	 */
	public void setEntityLinkBuilder( EntityLinkBuilder entityLinkBuilder ) {
		this.entityLinkBuilder = entityLinkBuilder;
	}

	/**
	 * Set the ViewPreProcessors that should be handled before building the EntityView
	 *
	 * @param preProcessors A Collection of ViewPreProcessors
	 */
	public void setPreProcessors( Collection<ViewPreProcessor<ViewCreationContext, T>> preProcessors ) {
		Assert.notNull( preProcessors );
		this.preProcessors = preProcessors;
	}

	public Collection<ViewPreProcessor<ViewCreationContext, T>> getPreProcessors() {
		return preProcessors;
	}

	/**
	 * Set the ViewPostProcessors that should be handled before building the EntityView
	 *
	 * @param postProcessors A Collection of ViewPostProcessors
	 */
	public void setPostProcessors( Collection<ViewPostProcessor<ViewCreationContext, T>> postProcessors ) {
		Assert.notNull( postProcessors );
		this.postProcessors = postProcessors;
	}

	public Collection<ViewPostProcessor<ViewCreationContext, T>> getPostProcessors() {
		return postProcessors;
	}

	@Override
	public EntityView create( String viewName, ViewCreationContext creationContext, Model model ) {
		EntityConfiguration entityConfiguration = creationContext.getEntityConfiguration();
		Assert.notNull( entityConfiguration );

		T view = createEntityView();
		view.setViewName( template );
		view.setEntityConfiguration( entityConfiguration );
		view.setEntityLinkBuilder(
				entityLinkBuilder != null
						? entityLinkBuilder : entityConfiguration.getAttribute( EntityLinkBuilder.class )
		);
		view.addModel( model );

		EntityMessageCodeResolver codeResolver = createMessageCodeResolver( entityConfiguration );
		view.setEntityMessages( createEntityMessages( codeResolver ) );

		handlePreProcessors( creationContext, view );

		buildViewModel( entityConfiguration, codeResolver, view );

		handlePostProcessors( creationContext, view );

		return view;
	}

	private void handlePreProcessors( ViewCreationContext creationContext, T view ) {
		for ( ViewPreProcessor<ViewCreationContext, T> preProcessor : preProcessors ) {
			preProcessor.preProcess( creationContext, view );
		}
	}

	private void handlePostProcessors( ViewCreationContext creationContext, T view ) {
		for ( ViewPostProcessor<ViewCreationContext, T> postProcessor : postProcessors ) {
			postProcessor.postProcess( creationContext, view );
		}
	}

	protected EntityMessages createEntityMessages( EntityMessageCodeResolver codeResolver ) {
		return new EntityMessages( codeResolver );
	}

	protected EntityMessageCodeResolver createMessageCodeResolver( EntityConfiguration entityConfiguration ) {
		EntityMessageCodeResolver codeResolver = messageCodeResolver;

		if ( codeResolver == null ) {
			codeResolver = entityConfiguration.getEntityMessageCodeResolver();
		}

		codeResolver = codeResolver.prefixedResolver( messagePrefixes );

		if ( messageSource != null ) {
			codeResolver.setMessageSource( messageSource );
		}

		return codeResolver;
	}

	protected abstract T createEntityView();

	protected abstract void buildViewModel( EntityConfiguration entityConfiguration,
	                                        EntityMessageCodeResolver codeResolver, T view );
}
