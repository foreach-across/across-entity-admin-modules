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

package com.foreach.across.modules.entity.views.menu;

import com.foreach.across.core.support.AttributeSupport;
import com.foreach.across.core.support.WritableAttributes;
import com.foreach.across.modules.entity.config.AttributeRegistrar;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.views.EntityViewFactory;
import com.foreach.across.modules.entity.views.EntityViewFactoryAttributes;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.web.EntityLinkBuilder;
import com.foreach.across.modules.spring.security.actions.AllowableActions;
import com.foreach.across.modules.web.menu.Menu;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.function.BiPredicate;
import java.util.function.Consumer;

import static com.foreach.across.modules.entity.EntityAttributeRegistrars.adminMenu;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestEntityAdminMenuAttributeRegistrar
{
	@Mock
	private EntityViewFactory viewFactory;

	@Mock
	private EntityViewContext entityViewContext;

	@Mock
	private AllowableActions allowableActions;

	private WritableAttributes attributes = spy( AttributeSupport.class );

	@Test
	public void manualConsumer() {
		Menu menu = createMenu( adminMenu( entityAdminMenuEvent -> entityAdminMenuEvent.builder().root( "hello" ) ) );

		assertThat( menu.isEmpty() ).isTrue();
		assertThat( menu.getPath() ).isEqualTo( "hello" );
	}

	@Test
	public void itemNotAddedIfNotForUpdate() {
		when( entityViewContext.getEntity() ).thenReturn( null );

		Menu menu = createMenu( adminMenu( "/my-path" ) );
		assertThat( menu.isEmpty() ).isTrue();
	}

	@Test
	public void itemNotAddedIfAccessValidatorDoesNotMatch() {
		BiPredicate<EntityViewFactory, EntityViewContext> accessValidator = ( f, c ) -> false;

		when( viewFactory.getAttribute( EntityViewFactoryAttributes.ACCESS_VALIDATOR, BiPredicate.class ) ).thenReturn( accessValidator );
		when( entityViewContext.getEntity() ).thenReturn( "my-entity" );

		Menu menu = createMenu( adminMenu( "/my-path" ) );
		assertThat( menu.isEmpty() ).isTrue();
	}

	@Test
	public void itemAddedIfNoAccessValidator() {
		when( entityViewContext.getEntity() ).thenReturn( "my-entity" );

		Menu menu = createMenu( adminMenu( "/my-path" ) );
		assertThat( menu.size() ).isEqualTo( 1 );
		Menu item = menu.getFirstItem();
		assertThat( item.getPath() ).isEqualTo( "/my-path" );
		assertThat( item.getTitle() ).isEqualTo( "#{adminMenu.views[myView]=myView}" );
		assertThat( item.getUrl() ).isEqualTo( "/my-entity?view=myView" );
	}

	@Test
	public void itemAddedIfAccessValidatorMatches() {
		BiPredicate<EntityViewFactory, EntityViewContext> accessValidator = ( f, c ) -> true;

		when( viewFactory.getAttribute( EntityViewFactoryAttributes.ACCESS_VALIDATOR, BiPredicate.class ) ).thenReturn( accessValidator );
		when( entityViewContext.getEntity() ).thenReturn( "my-entity" );

		Menu menu = createMenu( adminMenu( "/my-path" ) );
		assertThat( menu.size() ).isEqualTo( 1 );
		Menu item = menu.getFirstItem();
		assertThat( item.getPath() ).isEqualTo( "/my-path" );
		assertThat( item.getTitle() ).isEqualTo( "#{adminMenu.views[myView]=myView}" );
		assertThat( item.getUrl() ).isEqualTo( "/my-entity?view=myView" );
	}

	@Test
	public void itemCanBeCustomized() {
		when( entityViewContext.getEntity() ).thenReturn( "my-entity" );

		Menu menu = createMenu( adminMenu( "/my-path", item -> item.title( "My View" ).order( 10 ).and().root( "hello" ) ) );
		assertThat( menu.size() ).isEqualTo( 1 );
		assertThat( menu.getPath() ).isEqualTo( "hello" );
		Menu item = menu.getFirstItem();
		assertThat( item.getPath() ).isEqualTo( "/my-path" );
		assertThat( item.getTitle() ).isEqualTo( "My View" );
		assertThat( item.getUrl() ).isEqualTo( "/my-entity?view=myView" );
		assertThat( item.getOrder() ).isEqualTo( 10 );
	}

	private Menu createMenu( AttributeRegistrar<EntityViewFactory> attributeRegistrar ) {
		attributeRegistrar.accept( viewFactory, attributes );
		Consumer<EntityAdminMenuEvent> consumer = attributes.getAttribute( EntityViewFactoryAttributes.ADMIN_MENU, Consumer.class );
		assertThat( consumer ).isNotNull();

		when( viewFactory.getAttribute( EntityViewFactoryAttributes.VIEW_NAME, String.class ) ).thenReturn( "myView" );

		EntityConfiguration entityConfiguration = mock( EntityConfiguration.class );
		when( entityConfiguration.getEntityType() ).thenReturn( String.class );
		when( entityConfiguration.getName() ).thenReturn( "entityName" );
		when( entityViewContext.getEntityConfiguration() ).thenReturn( entityConfiguration );

		EntityLinkBuilder linkBuilder = mock( EntityLinkBuilder.class );
		when( linkBuilder.update( any() ) ).thenReturn( "/my-entity" );
		when( entityViewContext.getLinkBuilder() ).thenReturn( linkBuilder );

		when( entityViewContext.getAllowableActions() ).thenReturn( allowableActions );

		PathBasedMenuBuilder menuBuilder = new PathBasedMenuBuilder();
		EntityAdminMenuEvent event = new EntityAdminMenuEvent<>( EntityAdminMenu.create( entityViewContext ), menuBuilder );
		consumer.accept( event );

		return menuBuilder.build();
	}
}
