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

package com.foreach.across.modules.entity.views.util;

import com.foreach.across.modules.bootstrapui.elements.ButtonViewElement;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.processors.EntityViewProcessorAdapter;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.web.links.EntityViewLinks;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindingResult;

import java.util.Optional;
import java.util.function.Function;

/**
 * The collection of supported processors used by {@link EntityViewLinksUtilsHandlers}
 *
 * @author Marc Vanbrabant
 * @author Steven Gentens
 * @since 4.2.0
 */
public class EntityViewLinksUtilsProcessors
{
	@Getter
	@RequiredArgsConstructor
	public abstract static class EntityViewRedirectProcessor<SELF extends EntityViewRedirectProcessor<SELF>> extends EntityViewProcessorAdapter
	{
		private final EntityViewLinks entityViewLinks;
		private Function<LinkBasedEntityViewRedirectContext, String> redirectUrlProvider;

		/**
		 * Configures the url to which the page should redirect after a successful save, based on the current request context.
		 */
		@SuppressWarnings("unchecked")
		public SELF redirectUrlProvider( Function<LinkBasedEntityViewRedirectContext, String> entityViewLinksConsumer ) {
			this.redirectUrlProvider = entityViewLinksConsumer;
			return (SELF) this;
		}
	}

	public static class ButtonRedirectEntityViewRedirectProcessor extends EntityViewRedirectProcessor<ButtonRedirectEntityViewRedirectProcessor>
	{
		private String buttonElementName;

		public ButtonRedirectEntityViewRedirectProcessor( EntityViewLinks entityViewLinks ) {
			super( entityViewLinks );
		}

		public ButtonRedirectEntityViewRedirectProcessor buttonElementName( String buttonElementName ) {
			this.buttonElementName = buttonElementName;
			return this;
		}

		@Override
		protected void postRender( EntityViewRequest entityViewRequest,
		                           EntityView entityView,
		                           ContainerViewElement container,
		                           ViewElementBuilderContext builderContext ) {
			ContainerViewElementUtils.find( container, buttonElementName, ButtonViewElement.class )
			                         .ifPresent(
					                         backButton -> {
						                         LinkBasedEntityViewRedirectContext context =
								                         new LinkBasedEntityViewRedirectContext( entityViewRequest, entityView, getEntityViewLinks() );
						                         backButton.setUrl( getRedirectUrlProvider().apply( context ) );
					                         }
			                         );
		}
	}

	public static class AfterSaveEntityViewRedirectProcessor extends EntityViewRedirectProcessor<AfterSaveEntityViewRedirectProcessor>
	{
		public AfterSaveEntityViewRedirectProcessor( EntityViewLinks entityViewLinks ) {
			super( entityViewLinks );
		}

		@Override
		protected void doPost( EntityViewRequest entityViewRequest, EntityView entityView, EntityViewCommand command, BindingResult bindingResult ) {
			if ( !bindingResult.hasErrors() ) {
				String from = Optional.ofNullable( entityViewRequest.getWebRequest().getParameter( "from" ) )
				                      .orElseGet( () -> {
					                      LinkBasedEntityViewRedirectContext context =
							                      new LinkBasedEntityViewRedirectContext( entityViewRequest, entityView, getEntityViewLinks() );
					                      return getRedirectUrlProvider().apply( context );
				                      } );
				if ( StringUtils.isNotBlank( from ) ) {
					entityView.setRedirectUrl( from );
				}
			}
		}
	}

	@Getter
	@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
	public static class LinkBasedEntityViewRedirectContext
	{
		private final EntityViewRequest entityViewRequest;
		private final EntityView entityView;
		private final EntityViewLinks entityViewLinks;
	}
}
