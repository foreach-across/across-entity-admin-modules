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

import com.foreach.across.core.events.AcrossEventPublisher;
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiFactoryImpl;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.events.BuildEntityDeleteViewEvent;
import com.foreach.across.modules.entity.views.processors.support.EntityViewPageHelper;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementBuilderContextHolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.ResolvableType;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.concurrent.atomic.AtomicReference;

import static com.foreach.across.modules.entity.views.processors.DeleteEntityViewProcessor.DELETE_CONFIGURATION;
import static com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils.find;
import static com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils.findParent;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestDeleteEntityViewProcessor
{
	private final String entity = "my entity";

	@Mock
	private AcrossEventPublisher eventPublisher;

	@Mock
	private EntityViewRequest viewRequest;

	@Mock
	private EntityViewContext viewContext;

	@Mock
	private EntityMessages entityMessages;

	@Mock
	private EntityConfiguration entityConfiguration;

	private EntityView entityView;

	@InjectMocks
	private DeleteEntityViewProcessor processor;

	@Before
	public void setUp() throws Exception {
		processor.setBootstrapUiFactory( new BootstrapUiFactoryImpl() );
		when( viewRequest.getEntityViewContext() ).thenReturn( viewContext );
		when( viewContext.getEntityMessages() ).thenReturn( entityMessages );
		when( viewContext.getEntityConfiguration() ).thenReturn( entityConfiguration );
		when( viewContext.getEntity() ).thenReturn( entity );

		entityView = new EntityView( new ModelMap(), new RedirectAttributesModelMap() );

		ViewElementBuilderContextHolder.setViewElementBuilderContext( mock( ViewElementBuilderContext.class ) );
	}

	@After
	public void tearDown() throws Exception {
		ViewElementBuilderContextHolder.clearViewElementBuilderContext();
	}

	@Test
	public void viewConfigurationAllowsDeleteByDefault() {
		AtomicReference<BuildEntityDeleteViewEvent> eventPublished = new AtomicReference<>();

		doAnswer(
				invocation -> {
					BuildEntityDeleteViewEvent event = (BuildEntityDeleteViewEvent) invocation.getArguments()[0];
					eventPublished.set( event );
					assertArrayEquals(
							new ResolvableType[] { ResolvableType.forClass( String.class ) },
							event.getEventGenericTypes()
					);
					assertSame( entity, event.getEntity() );
					assertFalse( event.isDeleteDisabled() );
					assertNotNull( event.getBuilderContext() );
					assertNotNull( event.associations() );
					assertNotNull( event.messages() );
					assertTrue( find( event.messages(), "associations" ).isPresent() );
					assertEquals(
							find( event.messages(), "associations" ),
							findParent( event.messages(), event.associations() )
					);

					event.setDeleteDisabled( true );
					return null;
				}
		).when( eventPublisher ).publish( any( BuildEntityDeleteViewEvent.class ) );

		processor.doControl( viewRequest, entityView, null, null, null );

		BuildEntityDeleteViewEvent actual = entityView.getAttribute( DELETE_CONFIGURATION, BuildEntityDeleteViewEvent.class );
		assertSame( eventPublished.get(), actual );

		assertArrayEquals(
				new ResolvableType[] { ResolvableType.forClass( String.class ) },
				actual.getEventGenericTypes()
		);
		assertSame( entity, actual.getEntity() );
		assertTrue( actual.isDeleteDisabled() );
		assertNotNull( actual.getBuilderContext() );
		assertNotNull( actual.associations() );
		assertNotNull( actual.messages() );
		// no associations were added so block should have been removed
		assertFalse( find( actual.messages(), "associations" ).isPresent() );
	}
}
