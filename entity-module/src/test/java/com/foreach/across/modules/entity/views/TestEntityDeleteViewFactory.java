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

import com.foreach.across.config.EnableAcrossContext;
import com.foreach.across.core.events.AcrossEventPublisher;
import com.foreach.across.modules.bootstrapui.BootstrapUiModule;
import com.foreach.across.modules.entity.controllers.EntityViewCommand;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.events.BuildEntityDeleteViewEvent;
import com.foreach.across.modules.entity.web.EntityLinkBuilder;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElements;
import com.foreach.across.test.AcrossWebAppConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ResolvableType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ui.ModelMap;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@AcrossWebAppConfiguration
public class TestEntityDeleteViewFactory
{
	private final String entity = "my entity";

	@Autowired
	private EntityDeleteViewFactory<ViewCreationContext> deleteViewFactory;

	@Autowired
	private AcrossEventPublisher eventPublisher;

	private ModelMap model;

	@Before
	public void prepareTest() {
		reset( eventPublisher );

		model = new ModelMap();
		model.addAttribute( EntityView.ATTRIBUTE_ENTITY, entity );
	}

	@Test
	public void deleteIsPossibleByDefault() {
		AtomicBoolean eventPublished = new AtomicBoolean( false );

		doAnswer(
				invocation -> {
					BuildEntityDeleteViewEvent event = (BuildEntityDeleteViewEvent) invocation.getArguments()[0];
					eventPublished.set( true );
					assertArrayEquals(
							new ResolvableType[] { ResolvableType.forClass( String.class ) },
							event.getEventGenericTypes()
					);
					assertSame( entity, event.getEntity() );
					assertFalse( event.isDeleteDisabled() );
					return null;
				}
		).when( eventPublisher ).publish( any( BuildEntityDeleteViewEvent.class ) );

		EntityView view = buildView();

		assertTrue( eventPublished.get() );
		ViewElements viewElements = view.getViewElements();

		ViewElement element = viewElements.get( "btn-delete" );
		assertNotNull( element );
	}

	@Test
	public void deleteSuppressedThroughEvent() {
		doAnswer(
				invocation -> {
					BuildEntityDeleteViewEvent event = (BuildEntityDeleteViewEvent) invocation.getArguments()[0];
					event.setDeleteDisabled( true );
					return null;
				}
		).when( eventPublisher ).publish( any( BuildEntityDeleteViewEvent.class ) );

		EntityView view = buildView();
		assertNull( view.getViewElements().get( "btn-delete" ) );
	}

	private EntityView buildView() {
		EntityConfiguration entityConfiguration = mock( EntityConfiguration.class );

		ViewCreationContext creationContext = mock( ViewCreationContext.class );
		when( creationContext.getEntityConfiguration() ).thenReturn( entityConfiguration );

		EntityViewCommand cmd = mock( EntityViewCommand.class );

		deleteViewFactory.prepareModelAndCommand( "deleteView", creationContext, cmd, model );
		EntityView view = deleteViewFactory.create( "deleteView", creationContext, model );
		assertNotNull( view );
		assertNotNull( view.getViewElements() );

		return view;
	}

	@Configuration
	@EnableAcrossContext(modules = BootstrapUiModule.NAME)
	protected static class Config
	{
		@Bean
		public AcrossEventPublisher eventPublisher() {
			return mock( AcrossEventPublisher.class );
		}

		@Bean
		public EntityDeleteViewFactory<ViewCreationContext> entityDeleteViewFactory() {
			EntityMessageCodeResolver messageCodeResolver = mock( EntityMessageCodeResolver.class );
			when( messageCodeResolver.prefixedResolver( anyVararg() ) ).thenReturn( messageCodeResolver );

			EntityDeleteViewFactory<ViewCreationContext> entityDeleteViewFactory = new EntityDeleteViewFactory<>();
			entityDeleteViewFactory.setMessageCodeResolver( messageCodeResolver );

			EntityLinkBuilder linkBuilder = mock( EntityLinkBuilder.class );
			entityDeleteViewFactory.setEntityLinkBuilder( linkBuilder );

			return entityDeleteViewFactory;
		}
	}
}
