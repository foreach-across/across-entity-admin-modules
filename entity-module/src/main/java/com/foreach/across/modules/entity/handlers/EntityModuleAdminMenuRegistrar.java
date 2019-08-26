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
import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.conditionals.ConditionalOnAdminWeb;
import com.foreach.across.modules.entity.controllers.admin.GenericEntityViewController;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.EntityViewRegistry;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.EntityViewFactoryAttributes;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.menu.EntityAdminMenuEvent;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.entity.web.links.EntityViewLinkBuilder;
import com.foreach.across.modules.entity.web.links.SingleEntityViewLinkBuilder;
import com.foreach.across.modules.spring.security.actions.AllowableAction;
import com.foreach.across.modules.spring.security.actions.AllowableActions;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.function.Consumer;

import static com.foreach.across.modules.entity.config.icons.EntityModuleIcons.entityModuleIcons;

@ConditionalOnAdminWeb
@Component
@Slf4j
@RequiredArgsConstructor
class EntityModuleAdminMenuRegistrar
{
	private final EntityRegistry entityRegistry;
	private final EntityViewRequest entityViewRequest;

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
		boolean isAssociation = menu.getViewContext().isForAssociation();
		EntityConfiguration<Object> entityConfiguration = entityRegistry.getEntityConfiguration( menu.getEntityType() );
		EntityMessageCodeResolver messageCodeResolver = entityConfiguration.getEntityMessageCodeResolver();

		if ( menu.isForUpdate() ) {
			AllowableActions allowableActions = entityConfiguration.getAllowableActions( menu.getEntity() );
			val currentEntityLink = menu.getLinkBuilder().forInstance( menu.getEntity() );

			if ( allowableActions.contains( AllowableAction.UPDATE ) || allowableActions.contains( AllowableAction.READ ) ) {
				val linkToGeneralMenuItem = resolveLinkToGeneralMenuItem( currentEntityLink, entityViewRequest, menu.getViewContext(), allowableActions );
				builder.item( linkToGeneralMenuItem.toString(), messageCodeResolver.getMessageWithFallback( "adminMenu.general", "General" ) )
				       .order( Ordered.HIGHEST_PRECEDENCE );
			}

			if ( !isAssociation ) {
				// Get associations
				for ( EntityAssociation association : entityConfiguration.getAssociations() ) {
					if ( !association.isHidden() ) {
						EntityConfiguration associated = association.getTargetEntityConfiguration();

						if ( associated.getAllowableActions().contains( AllowableAction.READ ) ) {
							String itemTitle = messageCodeResolver.getMessageWithFallback(
									"adminMenu." + association.getName(),
									associated.getEntityMessageCodeResolver().getNamePlural()
							);

							builder.item( association.getName(), itemTitle, currentEntityLink.association( association.getName() ).listView().toString() );
						}
					}
				}
			}

			// Generate advanced options
			builder.group( "/advanced-options",
			               messageCodeResolver.getMessageWithFallback( "menu.advanced", "Advanced" ) )
			       .attribute( "html:class", "float-right" )
			       .attribute( NavComponentBuilder.ATTR_ICON, entityModuleIcons.formView.advancedSettings() )
			       .attribute( NavComponentBuilder.ATTR_KEEP_GROUP_ITEM, true )
			       .attribute( NavComponentBuilder.ATTR_ICON_ONLY, true );
		}
		else {
			builder.item( menu.getLinkBuilder().createView().toString(),
			              messageCodeResolver.getMessageWithFallback( "adminMenu.general", "General" ) )
			       .order( Ordered.HIGHEST_PRECEDENCE );
		}

		// Register the view menu items
		EntityViewRegistry viewRegistry = isAssociation ? menu.getViewContext().getEntityAssociation() : entityConfiguration;
		for ( String viewName : viewRegistry.getViewNames() ) {
			Consumer<EntityAdminMenuEvent> viewMenuBuilder = viewRegistry.getViewFactory( viewName )
			                                                             .getAttribute( EntityViewFactoryAttributes.ADMIN_MENU, Consumer.class );
			if ( viewMenuBuilder != null ) {
				viewMenuBuilder.accept( menu );
			}
		}
	}

	private SingleEntityViewLinkBuilder resolveLinkToGeneralMenuItem( SingleEntityViewLinkBuilder currentEntityLink,
	                                                                  EntityViewRequest entityViewRequest,
	                                                                  EntityViewContext menuViewContext,
	                                                                  AllowableActions allowableActions ) {
		EntityViewContext entityViewContext = entityViewRequest.getEntityViewContext();
		EntityConfiguration entityConfiguration = menuViewContext.getEntityConfiguration();
		boolean shouldLinkToDetailView = Boolean.TRUE.equals( entityConfiguration.getAttribute( EntityAttributes.LINK_TO_DETAIL_VIEW ) )
				&& !entityViewRequest.isForView( EntityView.UPDATE_VIEW_NAME );
		boolean isContextForDetailView = !allowableActions.contains( AllowableAction.UPDATE )
				|| ( entityViewContext.equals( menuViewContext ) && entityViewRequest.isForView( EntityView.DETAIL_VIEW_NAME ) );

		if ( shouldLinkToDetailView || isContextForDetailView ) {
			return currentEntityLink;
		}
		return currentEntityLink.updateView();
	}
}
