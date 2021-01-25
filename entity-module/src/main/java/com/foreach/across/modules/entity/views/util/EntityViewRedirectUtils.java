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
import com.foreach.across.modules.entity.config.builders.EntityViewFactoryBuilder;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.processors.EntityViewProcessorAdapter;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.web.links.EntityViewLinkBuilderSupport;
import com.foreach.across.modules.entity.web.links.EntityViewLinks;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindingResult;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Supports configuring the url that should be redirected to after successfully saving an entity {@link #afterSave}
 * or reconfiguring the url on a {@link ButtonViewElement} on the page {@link #button}.
 * When configuring a redirect, one of the default views can be used or link can be built for the view specifially.
 * <p>
 * If one of the default links is used, the {@code 'from'} request parameter is taken into account. If present,
 * it will take precedence over the fixed configured url, following the flow of the default entity views.
 * <p>
 * The predefined link options do <b>not</b> include the defaults, being:
 * - After saving, an update view redirects to itself
 * - Cancel button (update/create/delete views) redirects to the list view
 * - Back button (detail view) redirects to the list view
 *
 * @author Marc Vanbrabant, Steven Gentens
 * @since 4.1.0
 */
@UtilityClass
public class EntityViewRedirectUtils
{
	public static final AfterSaveLinkHandler afterSave = new AfterSaveLinkHandler();
	public static final ButtonLinkHandler button = new ButtonLinkHandler();

	private static Function<LinkBasedEntityViewRedirectContext, String> fromBasedUrl( BiFunction<EntityViewRequest, EntityViewContext, EntityViewLinkBuilderSupport> provider ) {
		return ( ctx -> {
			EntityViewRequest entityViewRequest = ctx.getEntityViewRequest();
			EntityViewContext entityViewContext = entityViewRequest.getEntityViewContext();
			return Optional.ofNullable( entityViewRequest.getWebRequest().getParameter( "from" ) )
			               .orElseGet( () -> provider.apply( entityViewRequest, entityViewContext ).toUriString() );
		} );
	}

	private static EntityViewLinkBuilderSupport redirectToCustomView( EntityViewLinkBuilderSupport linkBuilder,
	                                                                  String customView ) {
		return StringUtils.isNotBlank( customView ) ? linkBuilder.withViewName( customView ) : linkBuilder;
	}

	private static Function<LinkBasedEntityViewRedirectContext, String> redirectListView( String customView ) {
		return fromBasedUrl( ( evr, evc ) -> redirectToCustomView( evc.getLinkBuilder().listView(), customView ) );
	}

	private static Function<LinkBasedEntityViewRedirectContext, String> redirectCreateView( String customView ) {
		return fromBasedUrl( ( evr, evc ) -> redirectToCustomView( evc.getLinkBuilder().createView(), customView ) );
	}

	private static Function<LinkBasedEntityViewRedirectContext, String> redirectDetailView( String customView ) {
		return fromBasedUrl( ( evr, evc ) -> redirectToCustomView( evc.getLinkBuilder().forInstance( evr.getCommand().getEntity() ), customView ) );
	}

	private static Function<LinkBasedEntityViewRedirectContext, String> redirectUpdateView( String customView ) {
		return fromBasedUrl(
				( evr, evc ) -> redirectToCustomView( evc.getLinkBuilder().forInstance( evr.getCommand().getEntity() ).updateView(), customView ) );

	}

	public static class AfterSaveLinkHandler
	{
		public Consumer<EntityViewFactoryBuilder> toCreateView() {
			return toCreateView( null );
		}

		public Consumer<EntityViewFactoryBuilder> toDetailView() {
			return toDetailView( null );
		}

		public Consumer<EntityViewFactoryBuilder> toListView() {
			return toListView( null );
		}

		public Consumer<EntityViewFactoryBuilder> toCreateView( String customView ) {
			return to( redirectCreateView( customView ) );
		}

		public Consumer<EntityViewFactoryBuilder> toDetailView( String customView ) {
			return to( redirectDetailView( customView ) );
		}

		public Consumer<EntityViewFactoryBuilder> toUpdateView( String customView ) {
			return to( redirectUpdateView( customView ) );
		}

		public Consumer<EntityViewFactoryBuilder> toListView( String customView ) {
			return to( redirectListView( customView ) );
		}

		public Consumer<EntityViewFactoryBuilder> to( Function<LinkBasedEntityViewRedirectContext, String> entityViewLinksConsumer ) {
			return entityViewFactoryBuilder -> entityViewFactoryBuilder
					.viewProcessor( vp -> vp.createBean( AfterSaveEntityViewRedirectProcessor.class )
					                        .updateIfPresent()
					                        .configure( linkVp -> linkVp.redirectUrlProvider( entityViewLinksConsumer ) ) );
		}
	}

	public static class ButtonLinkHandler
	{
		public final FixedButtonLinkHandler back = new FixedButtonLinkHandler( FixedButtonLinkHandler.BACK_BUTTON );
		public final FixedButtonLinkHandler cancel = new FixedButtonLinkHandler( FixedButtonLinkHandler.CANCEL_BUTTON );

		public Consumer<EntityViewFactoryBuilder> toCreateView( String button ) {
			return toCreateView( button, null );
		}

		public Consumer<EntityViewFactoryBuilder> toDetailView( String button ) {
			return toDetailView( button, null );
		}

		public Consumer<EntityViewFactoryBuilder> toUpdateView( String button ) {
			return toUpdateView( button, null );
		}

		public Consumer<EntityViewFactoryBuilder> toListView( String button ) {
			return toListView( button, null );
		}

		public Consumer<EntityViewFactoryBuilder> toCreateView( String button, String customView ) {
			return to( button, redirectCreateView( customView ) );
		}

		public Consumer<EntityViewFactoryBuilder> toDetailView( String button, String customView ) {
			return to( button, redirectDetailView( customView ) );
		}

		public Consumer<EntityViewFactoryBuilder> toUpdateView( String button, String customView ) {
			return to( button, redirectUpdateView( customView ) );
		}

		public Consumer<EntityViewFactoryBuilder> toListView( String button, String customView ) {
			return to( button, redirectListView( customView ) );
		}

		public Consumer<EntityViewFactoryBuilder> to( String button, Function<LinkBasedEntityViewRedirectContext, String> entityViewLinksConsumer ) {
			return entityViewFactoryBuilder -> entityViewFactoryBuilder.viewProcessor(
					vp -> vp.createBean( ButtonRedirectEntityViewRedirectProcessor.class )
					        .withName( ButtonRedirectEntityViewRedirectProcessor.class.getName() + "_" + button )
					        .updateIfPresent()
					        .configure(
							        rvp -> rvp.buttonElementName( button )
							                  .redirectUrlProvider( entityViewLinksConsumer )
					        )
			);
		}
	}

	@RequiredArgsConstructor
	public static class FixedButtonLinkHandler
	{
		public final static String BACK_BUTTON = "btn-back";
		public final static String CANCEL_BUTTON = "btn-cancel";

		public final String buttonName;

		public Consumer<EntityViewFactoryBuilder> toCreateView() {
			return toCreateView( null );
		}

		public Consumer<EntityViewFactoryBuilder> toDetailView() {
			return toDetailView( null );
		}

		public Consumer<EntityViewFactoryBuilder> toUpdateView() {
			return toUpdateView( null );
		}

		public Consumer<EntityViewFactoryBuilder> toCreateView( String customView ) {
			return to( redirectCreateView( customView ) );
		}

		public Consumer<EntityViewFactoryBuilder> toDetailView( String customView ) {
			return to( redirectDetailView( customView ) );
		}

		public Consumer<EntityViewFactoryBuilder> toUpdateView( String customView ) {
			return to( redirectUpdateView( customView ) );
		}

		public Consumer<EntityViewFactoryBuilder> toListView( String customView ) {
			return to( redirectListView( customView ) );
		}

		public Consumer<EntityViewFactoryBuilder> to( Function<LinkBasedEntityViewRedirectContext, String> entityViewLinksConsumer ) {
			return entityViewFactoryBuilder -> entityViewFactoryBuilder.viewProcessor(
					vp -> vp.createBean( ButtonRedirectEntityViewRedirectProcessor.class )
					        .withName( ButtonRedirectEntityViewRedirectProcessor.class.getName() + "_" + buttonName )
					        .updateIfPresent()
					        .configure(
							        rvp -> rvp.buttonElementName( buttonName )
							                  .redirectUrlProvider( entityViewLinksConsumer )
					        )
			);
		}
	}

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
