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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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
	private DeleteActionFormViewProcessor deleteActionFormViewProcessor;

	@Mock
	private EntityViewRequest entityViewRequest;
	@Mock
	private EntityView entityView;
	@Mock
	private ContainerViewElementBuilderSupport<?, ?> containerBuilder;
	@Mock
	private ViewElementBuilderMap builderMap;
	private ViewElementBuilderContext builderContext = new DefaultViewElementBuilderContext();
	private EntityConfiguration entityConfiguration = mock( EntityConfiguration.class );
	private Object entity = new Object();

	@Before
	public void setUp() {
		deleteActionFormViewProcessor = new DeleteActionFormViewProcessor();

		ContainerViewElementBuilderSupport buttonsContainer = BootstrapUiBuilders.container()
		                                                                         .name( "buttons" );
		when( builderMap.containsKey( SingleEntityFormViewProcessor.FORM_BUTTONS ) ).thenReturn( true );
		when( builderMap.get( SingleEntityFormViewProcessor.FORM_BUTTONS, ContainerViewElementBuilderSupport.class ) )
				.thenReturn( buttonsContainer );

		EntityViewContext entityViewContext = mock( EntityViewContext.class );
		when( entityViewContext.getEntity() ).thenReturn( entity );
		when( entityViewRequest.getEntityViewContext() ).thenReturn( entityViewContext );

		entityConfiguration = mock( EntityConfiguration.class );
		when( entityConfiguration.getName() ).thenReturn( "item" );
		when( entityConfiguration.getId( entity ) ).thenReturn( 1L );
		when( entityViewContext.getEntityConfiguration() ).thenReturn( entityConfiguration );

		EntityViewLinks entityViewLinks = new EntityViewLinks( "/admin", mock( EntityRegistry.class ) );
		EntityViewLinkBuilder.ForEntityConfiguration currentLink = entityViewLinks.linkTo( entityConfiguration );
		when( entityViewContext.getLinkBuilder() ).thenReturn( currentLink );

		EntityMessages entityMessages = mock( EntityMessages.class );
		when( entityViewContext.getEntityMessages() ).thenReturn( entityMessages );
	}

	@Test
	public void deleteButtonIsPresentForDeleteAction() {
		when( entityConfiguration.getAllowableActions( entity ) ).thenReturn( new AllowableActionSet( AllowableAction.DELETE.getId() ) );
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
	public void deleteButtonIsNotPresentWithoutDeleteAction() {
		when( entityConfiguration.getAllowableActions( entity ) ).thenReturn( new AllowableActionSet() );
		ContainerViewElementBuilderSupport buttonsContainer = builderMap.get( SingleEntityFormViewProcessor.FORM_BUTTONS,
		                                                                      ContainerViewElementBuilderSupport.class );
		ContainerViewElement build = (ContainerViewElement) buttonsContainer.build( builderContext );
		assertThat( build.getChildren() ).isEmpty();

		deleteActionFormViewProcessor.render( entityViewRequest, entityView, containerBuilder, builderMap, builderContext );
		build = (ContainerViewElement) buttonsContainer.build( builderContext );
		assertThat( build.getChildren() ).isEmpty();
	}
}
