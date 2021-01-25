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

import com.foreach.across.modules.entity.config.builders.EntityViewFactoryBuilder;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.web.links.EntityViewLinkBuilderSupport;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The collection of supported handlers used by {@link EntityViewLinksUtils}
 *
 * @author Marc Vanbrabant
 * @author Steven Gentens
 * @since 4.2.0
 */
@UtilityClass
public class EntityViewLinksUtilsHandlers
{
	private static Function<EntityViewLinksUtilsProcessors.LinkBasedEntityViewRedirectContext, String> fromBasedUrl( BiFunction<EntityViewRequest, EntityViewContext, EntityViewLinkBuilderSupport> provider ) {
		return ( ctx -> {
			EntityViewRequest entityViewRequest = ctx.getEntityViewRequest();
			EntityViewContext entityViewContext = entityViewRequest.getEntityViewContext();
			return Optional.ofNullable( entityViewRequest.getWebRequest().getParameter( "from" ) )
			               .orElseGet( () -> provider.apply( entityViewRequest, entityViewContext ).toUriString() );
		} );
	}

	public static class ButtonLinkHandler
	{
		public final FixedButtonLinkHandler
				back = new FixedButtonLinkHandler( FixedButtonLinkHandler.BACK_BUTTON );
		public final FixedButtonLinkHandler
				cancel = new FixedButtonLinkHandler( FixedButtonLinkHandler.CANCEL_BUTTON );

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

		public Consumer<EntityViewFactoryBuilder> to( String button,
		                                              Function<EntityViewLinksUtilsProcessors.LinkBasedEntityViewRedirectContext, String> entityViewLinksConsumer ) {
			return entityViewFactoryBuilder -> entityViewFactoryBuilder.viewProcessor(
					vp -> vp.createBean( EntityViewLinksUtilsProcessors.ButtonRedirectEntityViewRedirectProcessor.class )
					        .withName( EntityViewLinksUtilsProcessors.ButtonRedirectEntityViewRedirectProcessor.class.getName() + "_" + button )
					        .updateIfPresent()
					        .configure(
							        rvp -> rvp.buttonElementName( button )
							                  .redirectUrlProvider( entityViewLinksConsumer )
					        )
			);
		}
	}

	private static EntityViewLinkBuilderSupport<?> redirectToCustomView( EntityViewLinkBuilderSupport<?> linkBuilder,
	                                                                     String customView ) {
		return StringUtils.isNotBlank( customView ) ? linkBuilder.withViewName( customView ) : linkBuilder;
	}

	private static Function<EntityViewLinksUtilsProcessors.LinkBasedEntityViewRedirectContext, String> redirectListView( String customView ) {
		return fromBasedUrl( ( evr, evc ) -> redirectToCustomView( evc.getLinkBuilder().listView(), customView ) );
	}

	private static Function<EntityViewLinksUtilsProcessors.LinkBasedEntityViewRedirectContext, String> redirectCreateView( String customView ) {
		return fromBasedUrl( ( evr, evc ) -> redirectToCustomView( evc.getLinkBuilder().createView(), customView ) );
	}

	private static Function<EntityViewLinksUtilsProcessors.LinkBasedEntityViewRedirectContext, String> redirectDetailView( String customView ) {
		return fromBasedUrl( ( evr, evc ) -> redirectToCustomView( evc.getLinkBuilder().forInstance( evr.getCommand().getEntity() ), customView ) );
	}

	private static Function<EntityViewLinksUtilsProcessors.LinkBasedEntityViewRedirectContext, String> redirectUpdateView( String customView ) {
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

		public Consumer<EntityViewFactoryBuilder> to( Function<EntityViewLinksUtilsProcessors.LinkBasedEntityViewRedirectContext, String> entityViewLinksConsumer ) {
			return entityViewFactoryBuilder -> entityViewFactoryBuilder
					.viewProcessor( vp -> vp.createBean( EntityViewLinksUtilsProcessors.AfterSaveEntityViewRedirectProcessor.class )
					                        .updateIfPresent()
					                        .configure( linkVp -> linkVp.redirectUrlProvider( entityViewLinksConsumer ) ) );
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

		public Consumer<EntityViewFactoryBuilder> to( Function<EntityViewLinksUtilsProcessors.LinkBasedEntityViewRedirectContext, String> entityViewLinksConsumer ) {
			return entityViewFactoryBuilder -> entityViewFactoryBuilder.viewProcessor(
					vp -> vp.createBean( EntityViewLinksUtilsProcessors.ButtonRedirectEntityViewRedirectProcessor.class )
					        .withName( EntityViewLinksUtilsProcessors.ButtonRedirectEntityViewRedirectProcessor.class.getName() + "_" + buttonName )
					        .updateIfPresent()
					        .configure(
							        rvp -> rvp.buttonElementName( buttonName )
							                  .redirectUrlProvider( entityViewLinksConsumer )
					        )
			);
		}
	}
}
