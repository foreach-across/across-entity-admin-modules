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

import com.foreach.across.modules.bootstrapui.elements.ButtonViewElement;
import com.foreach.across.modules.entity.config.builders.EntityViewFactoryBuilder;
import com.foreach.across.modules.entity.views.processors.EntityViewProcessorAdapter;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.modules.entity.web.links.EntityViewLinkBuilderSupport;
import com.foreach.across.modules.entity.web.links.EntityViewLinks;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindingResult;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A utility class to quickly register view processors on the update/create view.
 * <p>
 * These processors can redirect back to list view or to a specific entity view using {@link EntityViewLinks}
 */
@UtilityClass
public class RedirectViewUtils
{
	public final AfterSaveLinkHandler afterSave = new AfterSaveLinkHandler();
	public final BackLinkHandler back = new BackLinkHandler();

	public class AfterSaveLinkHandler
	{
		public Consumer<EntityViewFactoryBuilder> toListView() {
			return entityViewFactoryBuilder -> entityViewFactoryBuilder.viewProcessor( vp -> vp.createBean( RedirectToListViewProcessor.class ) );
		}

		public Consumer<EntityViewFactoryBuilder> to( EntityViewLinks entityViewLinks,
		                                              Function<EntityViewLinks, EntityViewLinkBuilderSupport<?>> entityViewLinksConsumer ) {
			return entityViewFactoryBuilder -> entityViewFactoryBuilder.viewProcessor( vp -> vp.provideBean(
					new EntityViewLinksRedirectProcessor( entityViewLinksConsumer, entityViewLinks ) ) );
		}
	}

	public class BackLinkHandler
	{
		public Consumer<EntityViewFactoryBuilder> toListView() {
			return entityViewFactoryBuilder -> entityViewFactoryBuilder.viewProcessor( vp -> vp.createBean( RedirectBackButtonToListViewProcessor.class ) );
		}
	}

	@RequiredArgsConstructor
	public static class RedirectToListViewProcessor extends EntityViewProcessorAdapter
	{
		private final EntityViewLinks entityViewLinks;

		@Override
		protected void doPost( EntityViewRequest entityViewRequest, EntityView entityView, EntityViewCommand command, BindingResult bindingResult ) {
			if ( !bindingResult.hasErrors() ) {
				Optional<Object> entity = Optional.ofNullable( entityView.getModel().get( "entity" ) );
				entity.ifPresent( o -> {
					Class<?> clazz = o.getClass();
					String redirectUrl = entityViewLinks.linkTo( clazz ).toUriString();
					entityView.setRedirectUrl( redirectUrl );
				} );
			}
		}
	}

	@RequiredArgsConstructor
	public static class RedirectBackButtonToListViewProcessor extends EntityViewProcessorAdapter
	{
		private final EntityViewLinks entityViewLinks;

		@Override
		protected void postRender( EntityViewRequest entityViewRequest,
		                           EntityView entityView,
		                           ContainerViewElement container,
		                           ViewElementBuilderContext builderContext ) {
			Object entity = EntityViewElementUtils.currentEntity( builderContext );
			ContainerViewElementUtils.find( container, "btn-back", ButtonViewElement.class ).ifPresent(
					backButton -> backButton.setUrl( entityViewLinks.linkTo( entity.getClass() ).toUriString() )
			);
		}
	}

	@RequiredArgsConstructor
	public static class EntityViewLinksRedirectProcessor extends EntityViewProcessorAdapter
	{
		private final Function<EntityViewLinks, EntityViewLinkBuilderSupport<?>> entityViewLinksConsumer;
		private final EntityViewLinks entityViewLinks;

		@Override
		protected void doPost( EntityViewRequest entityViewRequest, EntityView entityView, EntityViewCommand command, BindingResult bindingResult ) {
			if ( !bindingResult.hasErrors() ) {
				String from = Optional.ofNullable( entityViewRequest.getWebRequest().getParameter( "from" ) )
				                      .orElse( entityViewLinksConsumer.apply( entityViewLinks ).toUriString() );
				if ( StringUtils.isNotBlank( from ) ) {
					entityView.setRedirectUrl( from );
				}
			}
		}
	}
}
