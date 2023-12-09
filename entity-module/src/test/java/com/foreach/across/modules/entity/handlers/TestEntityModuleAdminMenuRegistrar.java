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

import com.foreach.across.modules.bootstrapui.elements.icons.IconSetRegistry;
import com.foreach.across.modules.bootstrapui.elements.icons.SimpleIconSet;
import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.actions.FixedEntityAllowableActionsBuilder;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.menu.EntityAdminMenu;
import com.foreach.across.modules.entity.views.menu.EntityAdminMenuEvent;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.web.links.EntityViewLinkBuilder;
import com.foreach.across.modules.entity.web.links.EntityViewLinks;
import com.foreach.across.modules.spring.security.actions.AllowableAction;
import com.foreach.across.modules.spring.security.actions.AllowableActionSet;
import com.foreach.across.modules.web.menu.Menu;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.html;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Steven Gentens
 * @since 3.2.0
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("unchecked")
public class TestEntityModuleAdminMenuRegistrar
{
	private EntityModuleAdminMenuRegistrar adminMenuRegistrar;
	private EntityRegistry entityRegistry;
	private EntityViewRequest viewRequest;
	private EntityViewContext viewContext;
	private EntityViewContext rootViewContext;
	private EntityConfiguration entityConfiguration;
	private EntityAdminMenuEvent entityAdminMenuEvent;
	private EntityAdminMenu entityAdminMenu;
	private PathBasedMenuBuilder menuBuilder;

	@BeforeEach
	public void setUp() {
		menuBuilder = new PathBasedMenuBuilder();
		entityAdminMenuEvent = mock( EntityAdminMenuEvent.class );
		entityConfiguration = mock( EntityConfiguration.class );
		entityRegistry = mock( EntityRegistry.class );
		viewRequest = mock( EntityViewRequest.class );
		viewContext = mock( EntityViewContext.class );
		rootViewContext = mock( EntityViewContext.class );

		when( viewRequest.isForView( EntityView.DETAIL_VIEW_NAME ) ).thenReturn( false );
		when( viewRequest.isForView( EntityView.UPDATE_VIEW_NAME ) ).thenReturn( false );
		when( viewRequest.getEntityViewContext() ).thenReturn( viewContext );
		when( viewContext.getEntityConfiguration() ).thenReturn( entityConfiguration );
		when( viewContext.isForAssociation() ).thenReturn( false );
		Item item = new Item();
		when( viewContext.getEntity() ).thenReturn( item );
		when( entityRegistry.getEntityConfiguration( Item.class ) ).thenReturn( entityConfiguration );

		EntityMessageCodeResolver messageCodeResolver = mock( EntityMessageCodeResolver.class );
		when( messageCodeResolver.getMessageWithFallback( eq( "adminMenu.general" ), anyString() ) ).thenReturn( "adminMenu.general" );
		when( messageCodeResolver.getMessageWithFallback( eq( "menu.advanced" ), anyString() ) ).thenReturn( "menu.advanced" );
		when( entityConfiguration.getEntityMessageCodeResolver() ).thenReturn( messageCodeResolver );
		when( entityConfiguration.getViewNames() ).thenReturn( new String[0] );
		when( entityConfiguration.getEntityType() ).thenReturn( Item.class );
		when( entityConfiguration.getName() ).thenReturn( "item" );
		when( entityConfiguration.getId( item ) ).thenReturn( 1L );

		EntityViewLinks entityViewLinks = new EntityViewLinks( "/admin", mock( EntityRegistry.class ) );
		EntityViewLinkBuilder.ForEntityConfiguration currentLink = entityViewLinks.linkTo( entityConfiguration );
		when( entityAdminMenuEvent.builder() ).thenReturn( menuBuilder );
		when( entityAdminMenuEvent.getEntity() ).thenReturn( item );
		when( entityAdminMenuEvent.getViewContext() ).thenReturn( viewContext );
		when( entityAdminMenuEvent.getLinkBuilder() ).thenReturn( currentLink );
		when( entityAdminMenuEvent.getEntityType() ).thenReturn( Item.class );

		entityAdminMenu = EntityAdminMenu.create( viewContext );
		adminMenuRegistrar = new EntityModuleAdminMenuRegistrar( entityRegistry, viewRequest );

		SimpleIconSet mutableIconSet = new SimpleIconSet();
		mutableIconSet.setDefaultIconResolver( ( iconName ) -> html.i() );
		IconSetRegistry.addIconSet( EntityModule.NAME, mutableIconSet );
	}

	@AfterEach
	public void cleanUp() {
		IconSetRegistry.removeIconSet( EntityModule.NAME );
	}

	@Test
	public void readAndUpdatePermissionsDetailView() {
		when( entityConfiguration.getAllowableActions( entityAdminMenu.getEntity() ) ).thenReturn(
				FixedEntityAllowableActionsBuilder.DEFAULT_ALLOWABLE_ACTIONS );
		when( entityAdminMenuEvent.isForUpdate() ).thenReturn( true );
		when( viewRequest.isForView( EntityView.DETAIL_VIEW_NAME ) ).thenReturn( true );

		adminMenuRegistrar.entityMenu( entityAdminMenuEvent );
		Menu menu = menuBuilder.build();
		assertThat( menu.getItems() ).isNotEmpty();
		assertThat( menu.getItemWithPath( "/general" ) ).isNotNull().matches( m -> "/admin/item/1".equals( m.getUrl() ) );
		assertThat( menu.getItemWithPath( "/admin/item/1/update" ) ).isNull();

	}

	@Test
	public void readAndUpdatePermissionsNoDetailView() {
		when( entityConfiguration.getAllowableActions( entityAdminMenu.getEntity() ) )
				.thenReturn( FixedEntityAllowableActionsBuilder.DEFAULT_ALLOWABLE_ACTIONS );
		when( entityAdminMenuEvent.isForUpdate() ).thenReturn( true );

		adminMenuRegistrar.entityMenu( entityAdminMenuEvent );
		Menu menu = menuBuilder.build();
		assertThat( menu.getItems() ).isNotEmpty();
		assertThat( menu.getItemWithPath( "/admin/item/1" ) ).isNull();
		assertThat( menu.getItemWithPath( "/general" ) ).isNotNull().matches( m -> "/admin/item/1/update".equals( m.getUrl() ) );
	}

	@Test
	public void noPermissions() {
		when( entityConfiguration.getAllowableActions( entityAdminMenu.getEntity() ) ).thenReturn( new AllowableActionSet() );
		when( entityAdminMenuEvent.isForUpdate() ).thenReturn( true );

		adminMenuRegistrar.entityMenu( entityAdminMenuEvent );
		Menu menu = menuBuilder.build();
		assertThat( menu.getItems() ).isNotEmpty();
		assertThat( menu.getItems().size() ).isEqualTo( 1 );
		assertThat( menu.getItemWithPath( "/advanced-options" ) ).isNotNull();
		assertThat( menu.getItemWithPath( "/advanced-options" ).getItems() ).isEmpty();
		assertThat( menu.getItemWithPath( "/admin/item/1" ) ).isNull();
		assertThat( menu.getItemWithPath( "/admin/item/1/update" ) ).isNull();
	}

	@Test
	public void shouldLinkToDetailView() {
		when( entityConfiguration.getAttribute( EntityAttributes.LINK_TO_DETAIL_VIEW ) ).thenReturn( true );
		when( entityConfiguration.getAllowableActions( entityAdminMenu.getEntity() ) )
				.thenReturn( FixedEntityAllowableActionsBuilder.DEFAULT_ALLOWABLE_ACTIONS );
		when( entityAdminMenuEvent.isForUpdate() ).thenReturn( true );

		adminMenuRegistrar.entityMenu( entityAdminMenuEvent );
		Menu menu = menuBuilder.build();
		assertThat( menu.getItems() ).isNotEmpty();
		assertThat( menu.getItemWithPath( "/general" ) ).isNotNull().matches( m -> "/admin/item/1".equals( m.getUrl() ) );
		assertThat( menu.getItemWithPath( "/admin/item/1/update" ) ).isNull();
	}

	@Test
	public void updateViewWithoutUpdateAction() {
		when( entityConfiguration.getAllowableActions( entityAdminMenu.getEntity() ) )
				.thenReturn( new AllowableActionSet( AllowableAction.READ.getId() ) );
		when( entityAdminMenuEvent.isForUpdate() ).thenReturn( true );

		adminMenuRegistrar.entityMenu( entityAdminMenuEvent );
		Menu menu = menuBuilder.build();
		assertThat( menu.getItems() ).isNotEmpty();
		assertThat( menu.getItemWithPath( "/general" ) ).isNotNull().matches( m -> "/admin/item/1".equals( m.getUrl() ) );
		assertThat( menu.getItemWithPath( "/admin/item/1/update" ) ).isNull();
	}

	@Test
	public void generalTabShouldLinkToUpdateViewIfUpdateViewIsActive() {
		when( entityConfiguration.getAttribute( EntityAttributes.LINK_TO_DETAIL_VIEW ) ).thenReturn( true );
		when( entityConfiguration.getAllowableActions( entityAdminMenu.getEntity() ) )
				.thenReturn( FixedEntityAllowableActionsBuilder.DEFAULT_ALLOWABLE_ACTIONS );
		when( entityAdminMenuEvent.isForUpdate() ).thenReturn( true );
		when( viewRequest.isForView( EntityView.UPDATE_VIEW_NAME ) ).thenReturn( true );

		adminMenuRegistrar.entityMenu( entityAdminMenuEvent );
		Menu menu = menuBuilder.build();
		assertThat( menu.getItems() ).isNotEmpty();
		assertThat( menu.getItemWithPath( "/admin/item/1" ) ).isNull();
		assertThat( menu.getItemWithPath( "/general" ) ).isNotNull().matches( m -> "/admin/item/1/update".equals( m.getUrl() ) );
	}

	@Test
	public void updateForRootContext() {
		when( entityConfiguration.getAttribute( EntityAttributes.LINK_TO_DETAIL_VIEW ) ).thenReturn( false );
		when( entityConfiguration.getAllowableActions( entityAdminMenu.getEntity() ) )
				.thenReturn( FixedEntityAllowableActionsBuilder.DEFAULT_ALLOWABLE_ACTIONS );
		when( entityAdminMenuEvent.isForUpdate() ).thenReturn( true );
		when( entityAdminMenuEvent.getViewContext() ).thenReturn( rootViewContext );
		when( rootViewContext.getEntityConfiguration() ).thenReturn( entityConfiguration );

		adminMenuRegistrar.entityMenu( entityAdminMenuEvent );
		Menu menu = menuBuilder.build();
		assertThat( menu.getItems() ).isNotEmpty();
		assertThat( menu.getItemWithPath( "/admin/item/1" ) ).isNull();
		assertThat( menu.getItemWithPath( "/general" ) ).isNotNull().matches( m -> "/admin/item/1/update".equals( m.getUrl() ) );
	}

	@Test
	public void detailForAssociationContext() {
		when( viewContext.isForAssociation() ).thenReturn( true );
		EntityAssociation entityAssociation = mock( EntityAssociation.class );
		when( viewContext.getEntityAssociation() ).thenReturn( entityAssociation );
		when( entityAssociation.getViewNames() ).thenReturn( new String[0] );
		when( entityConfiguration.getAllowableActions( entityAdminMenu.getEntity() ) )
				.thenReturn( FixedEntityAllowableActionsBuilder.DEFAULT_ALLOWABLE_ACTIONS );
		when( entityAdminMenuEvent.isForUpdate() ).thenReturn( true );
		when( viewRequest.isForView( EntityView.DETAIL_VIEW_NAME ) ).thenReturn( true );

		adminMenuRegistrar.entityMenu( entityAdminMenuEvent );
		Menu menu = menuBuilder.build();
		assertThat( menu.getItems() ).isNotEmpty();
		assertThat( menu.getItemWithPath( "/general" ) ).isNotNull().matches( m -> "/admin/item/1".equals( m.getUrl() ) );
		assertThat( menu.getItemWithPath( "/admin/item/1/update" ) ).isNull();
	}

	class Item
	{

	}
}
