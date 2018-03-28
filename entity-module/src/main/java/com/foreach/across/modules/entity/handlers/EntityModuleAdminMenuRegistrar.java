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

package com.foreach.across.modules.entity.handlers;

import com.foreach.across.core.context.info.AcrossModuleInfo;
import com.foreach.across.modules.adminweb.menu.AdminMenu;
import com.foreach.across.modules.adminweb.menu.AdminMenuEvent;
import com.foreach.across.modules.bootstrapui.components.builder.NavComponentBuilder;
import com.foreach.across.modules.bootstrapui.elements.GlyphIcon;
import com.foreach.across.modules.entity.conditionals.ConditionalOnAdminWeb;
import com.foreach.across.modules.entity.controllers.admin.GenericEntityViewController;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.EntityViewFactoryAttributes;
import com.foreach.across.modules.entity.views.menu.EntityAdminMenuEvent;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.entity.web.links.EntityViewLinkBuilder;
import com.foreach.across.modules.spring.security.actions.AllowableAction;
import com.foreach.across.modules.spring.security.actions.AllowableActions;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import com.foreach.across.modules.web.menu.RequestMenuSelector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.function.Consumer;

@ConditionalOnAdminWeb
@Component
@Slf4j
@RequiredArgsConstructor
class EntityModuleAdminMenuRegistrar
{
	private final EntityRegistry entityRegistry;

	@EventListener
	public void adminMenu( AdminMenuEvent adminMenuEvent ) {
		PathBasedMenuBuilder builder = adminMenuEvent.builder();
		builder.item( "/entities", "#{EntityModule.adminMenu=Entity management}", "@adminWeb:" + GenericEntityViewController.ROOT_PATH ).group( true );

		for ( EntityConfiguration entityConfiguration : entityRegistry.getEntities() ) {
			AllowableActions allowableActions = entityConfiguration.getAllowableActions();

			if ( !entityConfiguration.isHidden() && allowableActions.contains( AllowableAction.READ ) ) {
				EntityMessageCodeResolver messageCodeResolver = entityConfiguration.getEntityMessageCodeResolver();

				Assert.notNull(
						messageCodeResolver,
						"A visible EntityConfiguration (" + entityConfiguration
								.getName() + ") requires an EntityMessageCodeResolver"
				);

				EntityMessages messages = new EntityMessages( messageCodeResolver );
				EntityViewLinkBuilder linkBuilder = entityConfiguration.getAttribute( EntityViewLinkBuilder.class );
				AcrossModuleInfo moduleInfo = entityConfiguration.getAttribute( AcrossModuleInfo.class );

				if ( linkBuilder != null ) {
					String group = "/entities";

					if ( moduleInfo != null ) {
						group = "/entities/" + moduleInfo.getName();
						builder.group( group, "#{" + moduleInfo.getName() + ".adminMenu=" + moduleInfo.getName() + "}" )
						       .attribute( AdminMenu.ATTR_BREADCRUMB, false );
					}

					builder.item( group + "/" + entityConfiguration.getName(),
					              messageCodeResolver.getMessageWithFallback( "adminMenu", messageCodeResolver.getNameSingular() ),
					              linkBuilder.listView().toString() );

					if ( allowableActions.contains( AllowableAction.CREATE ) ) {
						builder.item( group + "/" + entityConfiguration.getName() + "/create",
						              messages.createAction(),
						              linkBuilder.createView().toString()
						);
					}
				}
				else {
					LOG.trace( "Not showing entity {} - not hidden but no EntityLinkBuilder",
					           entityConfiguration.getName() );
				}
			}
		}
	}

	@EventListener
	@SuppressWarnings("unchecked")
	public void entityMenu( EntityAdminMenuEvent menu ) {
		PathBasedMenuBuilder builder = menu.builder();

		EntityConfiguration<Object> entityConfiguration = entityRegistry.getEntityConfiguration( menu.getEntityType() );
		EntityMessageCodeResolver messageCodeResolver = entityConfiguration.getEntityMessageCodeResolver();

		if ( menu.isForUpdate() ) {
			val currentEntityLink = menu.getLinkBuilder().forInstance( menu.getEntity() );

			builder.item( currentEntityLink.updateView().toString(),
			              messageCodeResolver.getMessageWithFallback( "adminMenu.general", "General" ) )
			       .order( Ordered.HIGHEST_PRECEDENCE );

			// Get associations
			for ( EntityAssociation association : entityConfiguration.getAssociations() ) {
				if ( !association.isHidden() ) {
					EntityConfiguration associated = association.getTargetEntityConfiguration();

					String itemTitle = messageCodeResolver.getMessageWithFallback(
							"adminMenu." + association.getName(),
							associated.getEntityMessageCodeResolver().getNamePlural()
					);

					builder.item( association.getName(), itemTitle, currentEntityLink.association( association.getName() ).listView().toString() );
				}
			}

			// Generate advanced options
			builder.group( "/advanced-options",
			               messageCodeResolver.getMessageWithFallback( "menu.advanced", "Advanced" ) )
			       .attribute( "html:class", "pull-right" )
			       .attribute( NavComponentBuilder.ATTR_ICON, new GlyphIcon( GlyphIcon.COG ) )
			       .attribute( NavComponentBuilder.ATTR_KEEP_GROUP_ITEM, true )
			       .attribute( NavComponentBuilder.ATTR_ICON_ONLY, true );

			AllowableActions allowableActions = entityConfiguration.getAllowableActions( menu.getEntity() );
			if ( allowableActions.contains( AllowableAction.DELETE ) ) {
				val deleteLink = currentEntityLink.deleteView();

				builder.item( "/advanced-options/delete",
				              messageCodeResolver.getMessageWithFallback( "menu.delete", "Delete" ),
				              deleteLink.withFromUrl( currentEntityLink.updateView().toUriString() ).toString() )
				       .attribute( RequestMenuSelector.ATTRIBUTE_MATCHERS, Collections.singleton( deleteLink.toUriString() ) )
				       .attribute( NavComponentBuilder.ATTR_ICON, new GlyphIcon( GlyphIcon.TRASH ) )
				       .attribute( NavComponentBuilder.ATTR_INSERT_SEPARATOR, NavComponentBuilder.Separator.BEFORE )
				       .order( Ordered.LOWEST_PRECEDENCE );
			}
		}
		else {
			builder.item( menu.getLinkBuilder().createView().toString(),
			              messageCodeResolver.getMessageWithFallback( "adminMenu.general", "General" ) )
			       .order( Ordered.HIGHEST_PRECEDENCE );
		}

		// Register the view menu items
		for ( String viewName : entityConfiguration.getViewNames() ) {
			Consumer<EntityAdminMenuEvent> viewMenuBuilder = entityConfiguration.getViewFactory( viewName )
			                                                                    .getAttribute( EntityViewFactoryAttributes.ADMIN_MENU, Consumer.class );
			if ( viewMenuBuilder != null ) {
				viewMenuBuilder.accept( menu );
			}
		}
	}
}
