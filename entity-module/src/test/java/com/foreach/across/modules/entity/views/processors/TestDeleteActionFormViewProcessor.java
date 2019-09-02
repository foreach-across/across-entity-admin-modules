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

package com.foreach.across.modules.entity.views.processors;

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders;
import com.foreach.across.modules.bootstrapui.elements.ButtonViewElement;
import com.foreach.across.modules.bootstrapui.elements.icons.IconSetRegistry;
import com.foreach.across.modules.bootstrapui.elements.icons.SimpleIconSet;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.processors.support.ViewElementBuilderMap;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.entity.web.links.EntityViewLinkBuilder;
import com.foreach.across.modules.entity.web.links.EntityViewLinks;
import com.foreach.across.modules.spring.security.actions.AllowableAction;
import com.foreach.across.modules.spring.security.actions.AllowableActionSet;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.builder.ContainerViewElementBuilderSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.html;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Steven Gentens
 * @since 3.2.0
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("unchecked")
public class TestDeleteActionFormViewProcessor
{
	@Mock
	private EntityViewContext entityViewContext;

	@Mock
	private EntityViewRequest entityViewRequest;

	@Mock
	private EntityView entityView;

	@Mock
	private ContainerViewElementBuilderSupport<?, ?> containerBuilder;

	@Mock
	private ViewElementBuilderMap builderMap;

	private DeleteActionFormViewProcessor deleteActionFormViewProcessor;
	private ViewElementBuilderContext builderContext = new DefaultViewElementBuilderContext();
	private Object entity = new Object();

	@Before
	public void setUp() {
		deleteActionFormViewProcessor = new DeleteActionFormViewProcessor();

		ContainerViewElementBuilderSupport buttonsContainer = BootstrapUiBuilders.container()
		                                                                         .name( "buttons" );
		when( builderMap.get( SingleEntityFormViewProcessor.FORM_BUTTONS, ContainerViewElementBuilderSupport.class ) )
				.thenReturn( buttonsContainer );

		when( entityViewContext.getEntity() ).thenReturn( entity );
		when( entityViewRequest.getEntityViewContext() ).thenReturn( entityViewContext );

		EntityConfiguration entityConfiguration = mock( EntityConfiguration.class );
		when( entityConfiguration.getName() ).thenReturn( "item" );
		when( entityConfiguration.getId( entity ) ).thenReturn( 1L );

		EntityViewLinks entityViewLinks = new EntityViewLinks( "/admin", mock( EntityRegistry.class ) );
		EntityViewLinkBuilder.ForEntityConfiguration currentLink = entityViewLinks.linkTo( entityConfiguration );
		when( entityViewContext.getLinkBuilder() ).thenReturn( currentLink );

		EntityMessages entityMessages = mock( EntityMessages.class );
		when( entityViewContext.getEntityMessages() ).thenReturn( entityMessages );

		SimpleIconSet mutableIconSet = new SimpleIconSet();
		mutableIconSet.setDefaultIconResolver( ( iconName ) -> html.i() );
		IconSetRegistry.addIconSet( EntityModule.NAME, mutableIconSet );
	}

	@After
	public void cleanUp(){
		IconSetRegistry.removeIconSet( EntityModule.NAME );
	}

	@Test
	public void deleteButtonIsPresentForDeleteAction() {
		when( entityViewContext.getAllowableActions() ).thenReturn( new AllowableActionSet( AllowableAction.DELETE.getId() ) );
		ContainerViewElementBuilderSupport buttonsContainer = builderMap.get( SingleEntityFormViewProcessor.FORM_BUTTONS,
		                                                                      ContainerViewElementBuilderSupport.class );
		ContainerViewElement build = (ContainerViewElement) buttonsContainer.build( builderContext );
		assertThat( build.getChildren() ).isEmpty();

		deleteActionFormViewProcessor.render( entityViewRequest, entityView, containerBuilder, builderMap, builderContext );
		build = (ContainerViewElement) buttonsContainer.build( builderContext );
		assertThat( build.getChildren() ).isNotEmpty()
		                                 .hasSize( 1 );
		ViewElement viewElement = build.getChildren().get( 0 );
		assertThat( viewElement ).isInstanceOf( ButtonViewElement.class );
		assertThat( viewElement.getName() ).isEqualTo( "btn-delete" );
	}

	@Test
	public void deleteFromDetailView() {
		when( entityViewRequest.getViewName() ).thenReturn( EntityView.DETAIL_VIEW_NAME );
		when( entityViewContext.getAllowableActions() ).thenReturn( new AllowableActionSet( AllowableAction.DELETE.getId() ) );
		ContainerViewElementBuilderSupport buttonsContainer = builderMap.get( SingleEntityFormViewProcessor.FORM_BUTTONS,
		                                                                      ContainerViewElementBuilderSupport.class );
		ContainerViewElement build = (ContainerViewElement) buttonsContainer.build( builderContext );
		assertThat( build.getChildren() ).isEmpty();

		deleteActionFormViewProcessor.render( entityViewRequest, entityView, containerBuilder, builderMap, builderContext );
		build = (ContainerViewElement) buttonsContainer.build( builderContext );
		assertThat( build.getChildren() ).isNotEmpty()
		                                 .hasSize( 1 );
		ViewElement viewElement = build.getChildren().get( 0 );
		assertThat( viewElement ).isInstanceOf( ButtonViewElement.class );
		assertThat( viewElement.getName() ).isEqualTo( "btn-delete" );
		ButtonViewElement buttonViewElement = (ButtonViewElement) viewElement;
		assertThat( buttonViewElement.getUrl() ).contains( "from=" );
	}

	@Test
	public void deleteFromUpdateView() {
		when( entityViewRequest.getViewName() ).thenReturn( EntityView.UPDATE_VIEW_NAME );
		when( entityViewContext.getAllowableActions() ).thenReturn( new AllowableActionSet( AllowableAction.DELETE.getId() ) );
		ContainerViewElementBuilderSupport buttonsContainer = builderMap.get( SingleEntityFormViewProcessor.FORM_BUTTONS,
		                                                                      ContainerViewElementBuilderSupport.class );
		ContainerViewElement build = (ContainerViewElement) buttonsContainer.build( builderContext );
		assertThat( build.getChildren() ).isEmpty();

		deleteActionFormViewProcessor.render( entityViewRequest, entityView, containerBuilder, builderMap, builderContext );
		build = (ContainerViewElement) buttonsContainer.build( builderContext );
		assertThat( build.getChildren() ).isNotEmpty()
		                                 .hasSize( 1 );
		ViewElement viewElement = build.getChildren().get( 0 );
		assertThat( viewElement ).isInstanceOf( ButtonViewElement.class );
		assertThat( viewElement.getName() ).isEqualTo( "btn-delete" );
		ButtonViewElement buttonViewElement = (ButtonViewElement) viewElement;
		assertThat( buttonViewElement.getUrl() ).contains( "from=" ).endsWith( "update" );
	}

	@Test
	public void deleteButtonIsNotPresentWithoutDeleteAction() {
		when( entityViewContext.getAllowableActions() ).thenReturn( new AllowableActionSet() );
		ContainerViewElementBuilderSupport buttonsContainer = builderMap.get( SingleEntityFormViewProcessor.FORM_BUTTONS,
		                                                                      ContainerViewElementBuilderSupport.class );
		ContainerViewElement build = (ContainerViewElement) buttonsContainer.build( builderContext );
		assertThat( build.getChildren() ).isEmpty();

		deleteActionFormViewProcessor.render( entityViewRequest, entityView, containerBuilder, builderMap, builderContext );
		build = (ContainerViewElement) buttonsContainer.build( builderContext );
		assertThat( build.getChildren() ).isEmpty();
	}
}
