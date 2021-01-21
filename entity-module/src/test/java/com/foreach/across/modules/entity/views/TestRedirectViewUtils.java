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
import com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements;
import com.foreach.across.modules.entity.config.builders.EntityListViewFactoryBuilder;
import com.foreach.across.modules.entity.config.builders.EntityViewFactoryBuilder;
import com.foreach.across.modules.entity.registry.*;
import com.foreach.across.modules.entity.views.processors.EntityViewProcessorAdapter;
import com.foreach.across.modules.entity.views.processors.support.TransactionalEntityViewProcessorRegistry;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.web.EntityViewModel;
import com.foreach.across.modules.entity.web.links.EntityViewLinks;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementBuilderContextHolder;
import com.foreach.across.modules.web.ui.elements.HtmlViewElements;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
class TestRedirectViewUtils
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
	public void testRedirectUrlsAfterSave() {
		verifyRedirectUrl( "/root/path/entity", RedirectViewUtils.afterSave.toListView() );
		verifyRedirectUrl( "/root/path/entity/2/update",
		                   RedirectViewUtils.afterSave.to( entityViewLinks, linkBuilder -> linkBuilder.linkTo( Entity.class ).forInstance( 2L )
		                                                                                              .updateView() ) );
		verifyRedirectUrl( "/root/path/entity/1?view=customView",
		                   RedirectViewUtils.afterSave.to( entityViewLinks, linkBuilder -> linkBuilder.linkTo( Entity.class )
		                                                                                              .forInstance( 1L )
		                                                                                              .withViewName( "customView" ) ) );
	}

	@Test
	public void testRedirectUrlsForBackButton() {
		verifyBackButtonUrl( "/root/path/entity", RedirectViewUtils.back.toListView() );
	}

	private void verifyBackButtonUrl( String expectedUrl, Consumer<EntityViewFactoryBuilder> entityViewFactoryBuilderConsumer ) {
		setup( expectedUrl, entityViewFactoryBuilderConsumer, true );
	}

	private void verifyRedirectUrl( String expectedUrl, Consumer<EntityViewFactoryBuilder> entityViewFactoryBuilderConsumer ) {
		setup( expectedUrl, entityViewFactoryBuilderConsumer, false );
	}

	private void setup( String expectedUrl, Consumer<EntityViewFactoryBuilder> entityViewFactoryBuilderConsumer, boolean withBackButton ) {

		EntityViewFactoryBuilder builder = new EntityListViewFactoryBuilder( beanFactory ).factoryType( DefaultEntityViewFactory.class );
		entityViewFactoryBuilderConsumer.accept( builder );

		DefaultEntityViewFactory build = (DefaultEntityViewFactory) builder.build();

		TransactionalEntityViewProcessorRegistry processorRegistry = build.getProcessorRegistry();
		Collection<EntityViewProcessor> processorRegistrations = processorRegistry.getProcessors();
		assertThat( processorRegistrations ).hasSize( 1 );

		EntityViewProcessor processor = processorRegistrations.iterator().next();
		EntityViewProcessorAdapter processorAdapter = (EntityViewProcessorAdapter) processor;
		EntityViewRequest entityViewRequest = mock( EntityViewRequest.class );
		NativeWebRequest webRequest = mock( NativeWebRequest.class );
		when( entityViewRequest.getWebRequest() ).thenReturn( webRequest );
		when( entityViewRequest.getHttpMethod() ).thenReturn( HttpMethod.POST );
		BindingResult bindingResult = mock( BindingResult.class );
		when( bindingResult.hasErrors() ).thenReturn( false );
		when( entityViewRequest.getBindingResult() ).thenReturn( bindingResult );
		MutableEntityConfiguration<Entity> configuration = new EntityConfigurationImpl<>( "entity", Entity.class );
		EntityModel entityModel = new MockEntityModel();
		configuration.setEntityModel( entityModel );
		when( entityRegistry.getEntityConfiguration( Entity.class ) ).thenReturn( configuration );
		entityView.getModel().addAttribute( EntityViewModel.ENTITY, two );

		ButtonViewElement buttonViewElement = null;
		if ( withBackButton ) {
			NodeViewElement div = HtmlViewElements.html.div();
			buttonViewElement = BootstrapViewElements.bootstrap.button().setName( "btn-back" );
			div.addChild( buttonViewElement );

			entityView.getModel().addAttribute( ATTRIBUTE_CONTAINER_ELEMENT, div );
			global.setAttribute( EntityViewModel.ENTITY, two );
		}

		processorAdapter.postRender( entityViewRequest, entityView );
		processorAdapter.doControl( entityViewRequest, entityView, null );

		if ( withBackButton ) {
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

	static class MockEntityModel extends DefaultEntityModel<Long, Entity>
	{
		@Override
		public Entity getId( Long entity ) {
			if ( entity == 2L ) {
				return two;
			}
			else if ( entity == 1 ) {
				return one;
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