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
import com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements;
import com.foreach.across.modules.entity.config.builders.EntityListViewFactoryBuilder;
import com.foreach.across.modules.entity.config.builders.EntityViewFactoryBuilder;
import com.foreach.across.modules.entity.registry.*;
import com.foreach.across.modules.entity.views.DefaultEntityViewFactory;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.EntityViewProcessor;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.processors.EntityViewProcessorAdapter;
import com.foreach.across.modules.entity.views.processors.support.TransactionalEntityViewProcessorRegistry;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.web.EntityViewModel;
import com.foreach.across.modules.entity.web.links.EntityViewLinkBuilder;
import com.foreach.across.modules.entity.web.links.EntityViewLinks;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementBuilderContextHolder;
import com.foreach.across.modules.web.ui.elements.HtmlViewElements;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.Serializable;
import java.util.Collection;
import java.util.function.Consumer;

import static com.foreach.across.modules.entity.views.DefaultEntityViewFactory.ATTRIBUTE_CONTAINER_ELEMENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(SpringExtension.class)
class TestEntityViewRedirectUtils
{
	@Autowired
	private ConfigurableListableBeanFactory beanFactory;
	@Mock
	private EntityRegistry entityRegistry;

	private EntityViewLinks entityViewLinks;
	private EntityView entityView;
	private DefaultViewElementBuilderContext global;

	@BeforeEach
	public void setUp() throws Exception {
		entityViewLinks = new EntityViewLinks( "/root/path/", entityRegistry );
		if ( !beanFactory.containsBean( "entityViewLinks" ) ) {
			beanFactory.registerSingleton( "entityViewLinks", entityViewLinks );
		}
		global = new DefaultViewElementBuilderContext();
		ViewElementBuilderContextHolder.setViewElementBuilderContext( global );
		entityView = new EntityView( new ModelMap(), new RedirectAttributesModelMap() );
	}

	@Test
	public void afterSave() {
		verifyRedirectUrl( "/root/path/entity", EntityViewRedirectUtils.afterSave.toListView() );
		verifyRedirectUrl( "/root/path/entity?view=customView", EntityViewRedirectUtils.afterSave.toListView( "customView" ) );
		verifyRedirectUrl( "/root/path/entity/create", EntityViewRedirectUtils.afterSave.toCreateView() );
		verifyRedirectUrl( "/root/path/entity/create?view=customView", EntityViewRedirectUtils.afterSave.toCreateView( "customView" ) );
		verifyRedirectUrl( "/root/path/entity/2", EntityViewRedirectUtils.afterSave.toDetailView() );
		verifyRedirectUrl( "/root/path/entity/2?view=customView", EntityViewRedirectUtils.afterSave.toDetailView( "customView" ) );
		verifyRedirectUrl( "/root/path/entity/2/update?view=customView", EntityViewRedirectUtils.afterSave.toUpdateView( "customView" ) );
		verifyRedirectUrl( "/a-random-url/2/chart?state=some-state", EntityViewRedirectUtils.afterSave.to(
				redirectContext -> {
					EntityViewRequest entityViewRequest = redirectContext.getEntityViewRequest();
					return UriComponentsBuilder.fromPath( "/a-random-url" )
					                           .pathSegment( entityViewRequest.getCommand().getEntity().toString() )
					                           .pathSegment( "chart" )
					                           .queryParam( "state", entityViewRequest.getWebRequest().getParameter( "state" ) )
					                           .encode()
					                           .build()
					                           .toUriString();
				}
		) );
		verifyRedirectUrl( "/root/path/entity/1?view=customView",
		                   EntityViewRedirectUtils.afterSave.to( linkBuilder -> linkBuilder.getEntityViewLinks().linkTo( Entity.class )
		                                                                                   .forInstance( one )
		                                                                                   .withViewName( "customView" ).toUriString() ) );
	}

	@Test
	public void button() {
		verifyButtonUrl( "btn-delete", "/root/path/entity", EntityViewRedirectUtils.button.toListView( "btn-delete" ) );
		verifyButtonUrl( "btn-delete", "/root/path/entity?view=customView", EntityViewRedirectUtils.button.toListView( "btn-delete", "customView" ) );
		verifyButtonUrl( "btn-delete", "/root/path/entity/create", EntityViewRedirectUtils.button.toCreateView( "btn-delete" ) );
		verifyButtonUrl( "btn-delete", "/root/path/entity/create?view=customView", EntityViewRedirectUtils.button.toCreateView( "btn-delete", "customView" ) );
		verifyButtonUrl( "btn-delete", "/root/path/entity/2", EntityViewRedirectUtils.button.toDetailView( "btn-delete" ) );
		verifyButtonUrl( "btn-delete", "/root/path/entity/2?view=customView", EntityViewRedirectUtils.button.toDetailView( "btn-delete", "customView" ) );
		verifyButtonUrl( "btn-delete", "/root/path/entity/2/update", EntityViewRedirectUtils.button.toUpdateView( "btn-delete" ) );
		verifyButtonUrl( "btn-delete", "/root/path/entity/2/update?view=customView",
		                 EntityViewRedirectUtils.button.toUpdateView( "btn-delete", "customView" ) );
		Consumer<EntityViewFactoryBuilder> consumer =
				EntityViewRedirectUtils.button.to(
						"btn-delete",
						redirectContext -> {
							EntityViewRequest entityViewRequest = redirectContext.getEntityViewRequest();
							return UriComponentsBuilder.fromPath( "/a-random-url" )
							                           .pathSegment( entityViewRequest.getCommand().getEntity().toString() )
							                           .pathSegment( "chart" )
							                           .queryParam( "state", entityViewRequest.getWebRequest().getParameter( "state" ) )
							                           .encode()
							                           .build()
							                           .toUriString();
						}
				);
		verifyButtonUrl( "btn-delete", "/a-random-url/2/chart?state=some-state", consumer );
	}

	@Test
	public void backButton() {
		verifyButtonUrl( "btn-back", "/root/path/entity?view=customView", EntityViewRedirectUtils.button.back.toListView( "customView" ) );
		verifyButtonUrl( "btn-back", "/root/path/entity/create", EntityViewRedirectUtils.button.back.toCreateView() );
		verifyButtonUrl( "btn-back", "/root/path/entity/create?view=customView", EntityViewRedirectUtils.button.back.toCreateView( "customView" ) );
		verifyButtonUrl( "btn-back", "/root/path/entity/2", EntityViewRedirectUtils.button.back.toDetailView() );
		verifyButtonUrl( "btn-back", "/root/path/entity/2?view=customView", EntityViewRedirectUtils.button.back.toDetailView( "customView" ) );
		verifyButtonUrl( "btn-back", "/root/path/entity/2/update", EntityViewRedirectUtils.button.back.toUpdateView() );
		verifyButtonUrl( "btn-back", "/root/path/entity/2/update?view=customView", EntityViewRedirectUtils.button.back.toUpdateView( "customView" ) );
		Consumer<EntityViewFactoryBuilder> consumer =
				EntityViewRedirectUtils.button.back.to(
						redirectContext -> {
							EntityViewRequest entityViewRequest = redirectContext.getEntityViewRequest();
							return UriComponentsBuilder.fromPath( "/a-random-url" )
							                           .pathSegment( entityViewRequest.getCommand().getEntity().toString() )
							                           .pathSegment( "chart" )
							                           .queryParam( "state", entityViewRequest.getWebRequest().getParameter( "state" ) )
							                           .encode()
							                           .build()
							                           .toUriString();
						}
				);
		verifyButtonUrl( "btn-back", "/a-random-url/2/chart?state=some-state", consumer );
	}

	@Test
	public void cancelButton() {
		verifyButtonUrl( "btn-cancel", "/root/path/entity?view=customView", EntityViewRedirectUtils.button.cancel.toListView( "customView" ) );
		verifyButtonUrl( "btn-cancel", "/root/path/entity/create", EntityViewRedirectUtils.button.cancel.toCreateView() );
		verifyButtonUrl( "btn-cancel", "/root/path/entity/create?view=customView", EntityViewRedirectUtils.button.cancel.toCreateView( "customView" ) );
		verifyButtonUrl( "btn-cancel", "/root/path/entity/2", EntityViewRedirectUtils.button.cancel.toDetailView() );
		verifyButtonUrl( "btn-cancel", "/root/path/entity/2?view=customView", EntityViewRedirectUtils.button.cancel.toDetailView( "customView" ) );
		verifyButtonUrl( "btn-cancel", "/root/path/entity/2/update", EntityViewRedirectUtils.button.cancel.toUpdateView() );
		verifyButtonUrl( "btn-cancel", "/root/path/entity/2/update?view=customView", EntityViewRedirectUtils.button.cancel.toUpdateView( "customView" ) );
		Consumer<EntityViewFactoryBuilder> consumer =
				EntityViewRedirectUtils.button.cancel.to(
						redirectContext -> {
							EntityViewRequest entityViewRequest = redirectContext.getEntityViewRequest();
							return UriComponentsBuilder.fromPath( "/a-random-url" )
							                           .pathSegment( entityViewRequest.getCommand().getEntity().toString() )
							                           .pathSegment( "chart" )
							                           .queryParam( "state", entityViewRequest.getWebRequest().getParameter( "state" ) )
							                           .encode()
							                           .build()
							                           .toUriString();
						}
				);
		verifyButtonUrl( "btn-cancel", "/a-random-url/2/chart?state=some-state", consumer );
	}

	private void verifyButtonUrl( String buttonName, String expectedUrl, Consumer<EntityViewFactoryBuilder> entityViewFactoryBuilderConsumer ) {
		setup( expectedUrl, entityViewFactoryBuilderConsumer, buttonName );
	}

	private void verifyRedirectUrl( String expectedUrl, Consumer<EntityViewFactoryBuilder> entityViewFactoryBuilderConsumer ) {
		setup( expectedUrl, entityViewFactoryBuilderConsumer, null );
	}

	private void setup( String expectedUrl, Consumer<EntityViewFactoryBuilder> entityViewFactoryBuilderConsumer, String buttonName ) {

		EntityViewFactoryBuilder builder = new EntityListViewFactoryBuilder( beanFactory ).factoryType( DefaultEntityViewFactory.class );
		entityViewFactoryBuilderConsumer.accept( builder );

		DefaultEntityViewFactory build = (DefaultEntityViewFactory) builder.build();

		TransactionalEntityViewProcessorRegistry processorRegistry = build.getProcessorRegistry();
		Collection<EntityViewProcessor> processorRegistrations = processorRegistry.getProcessors();
		assertThat( processorRegistrations ).hasSize( 1 );

		EntityViewProcessor processor = processorRegistrations.iterator().next();
		EntityViewProcessorAdapter processorAdapter = (EntityViewProcessorAdapter) processor;
		EntityViewRequest entityViewRequest = mock( EntityViewRequest.class );

		EntityViewCommand entityViewCommand = mock( EntityViewCommand.class );
		when( entityViewRequest.getCommand() ).thenReturn( entityViewCommand );
		when( entityViewCommand.getEntity() ).thenReturn( two );

		NativeWebRequest webRequest = mock( NativeWebRequest.class );
		when( entityViewRequest.getWebRequest() ).thenReturn( webRequest );
		when( entityViewRequest.getHttpMethod() ).thenReturn( HttpMethod.POST );
		when( webRequest.getParameter( "state" ) ).thenReturn( "some-state" );

		BindingResult bindingResult = mock( BindingResult.class );
		when( bindingResult.hasErrors() ).thenReturn( false );
		when( entityViewRequest.getBindingResult() ).thenReturn( bindingResult );

		MutableEntityConfiguration<Entity> configuration = new EntityConfigurationImpl<>( "entity", Entity.class );
		EntityModel entityModel = new MockEntityModel();
		configuration.setEntityModel( entityModel );
		when( entityRegistry.getEntityConfiguration( Entity.class ) ).thenReturn( configuration );
		entityView.getModel().addAttribute( EntityViewModel.ENTITY, two );

		EntityViewContext entityViewContext = mock( EntityViewContext.class );
		when( entityViewRequest.getEntityViewContext() ).thenReturn( entityViewContext );
		EntityViewLinkBuilder.ForEntityConfiguration linkBuilder = entityViewLinks.linkTo( Entity.class );
		when( entityViewContext.getLinkBuilder() ).thenReturn( linkBuilder );

		ButtonViewElement buttonViewElement = null;
		if ( StringUtils.isNotBlank( buttonName ) ) {
			NodeViewElement div = HtmlViewElements.html.div();
			buttonViewElement = BootstrapViewElements.bootstrap.button().setName( buttonName );
			div.addChild( buttonViewElement );

			entityView.getModel().addAttribute( ATTRIBUTE_CONTAINER_ELEMENT, div );
			global.setAttribute( EntityViewModel.ENTITY, two );
		}

		processorAdapter.postRender( entityViewRequest, entityView );
		processorAdapter.doControl( entityViewRequest, entityView, null );

		if ( StringUtils.isNotBlank( buttonName ) ) {
			assertThat( buttonViewElement ).isNotNull();
			assertThat( buttonViewElement.getUrl() ).isEqualTo( expectedUrl );
		}
		else {
			assertEquals( expectedUrl, entityView.getRedirectUrl() );
		}
	}

	@RequiredArgsConstructor
	@Getter
	static class Entity implements Serializable
	{
		private final Long id;
		private final String name;

		@Override
		public String toString() {
			// Simulates EntityViewLinks.convertId
			return id.toString();
		}
	}

	static Entity one = new Entity( 1L, "dummy entity" );
	static Entity two = new Entity( 2L, "dummy entity" );

	static class MockEntityModel extends DefaultEntityModel<Entity, Long>
	{
		@Override
		public Long getId( Entity entity ) {
			if ( entity == two ) {
				return 2L;
			}
			else if ( entity == one ) {
				return 1L;
			}
			throw new RuntimeException( "should not come here" );
		}
	}

	/*
	@Configuration
	public static class Config
	{
		@Bean
		public EntityRegistry entityRegistry() {
			return mock( EntityRegistry.class );
		}

		@Bean
		public EntityViewLinks entityViewLinks( EntityRegistry entityRegistry ) {
			return new EntityViewLinks( "/root/path", entityRegistry );
		}
	}
	 */
}