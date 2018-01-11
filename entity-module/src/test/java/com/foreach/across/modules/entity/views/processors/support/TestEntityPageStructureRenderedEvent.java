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

package com.foreach.across.modules.entity.views.processors.support;

import com.foreach.across.modules.adminweb.ui.PageContentStructure;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.ResolvableType;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestEntityPageStructureRenderedEvent
{
	@Mock
	private EntityViewRequest viewRequest;

	@Mock
	private EntityView view;

	@Mock
	private EntityViewContext viewContext;

	@Mock
	private ViewElementBuilderContext builderContext;

	@Mock
	private EntityConfiguration entityConfiguration;

	private EntityPageStructureRenderedEvent<Object> event;

	@Before
	public void setUp() throws Exception {
		when( viewContext.getEntityConfiguration() ).thenReturn( entityConfiguration );

		event = new EntityPageStructureRenderedEvent<>( false, viewRequest, view, viewContext, builderContext );
	}

	@Test
	public void eventNameIsTheEntityConfigurationName() {
		when( entityConfiguration.getName() ).thenReturn( "configName" );
		assertEquals( "configName", event.getEventName() );
	}

	@Test
	public void eventTypeIsEntityConfigurationType() {
		when( entityConfiguration.getEntityType() ).thenReturn( String.class );
		assertEquals(
				ResolvableType.forClassWithGenerics( EntityPageStructureRenderedEvent.class, String.class ),
				event.getResolvableType()
		);
	}

	@Test
	public void pageContentStructureFromRequestIsReturned() {
		PageContentStructure page = mock( PageContentStructure.class );
		when( viewRequest.getPageContentStructure() ).thenReturn( page );
		assertSame( page, event.getPageContentStructure() );
	}

	@Test
	public void holdsEntityIsDelegatedToViewContext() {
		assertFalse( event.holdsEntity() );
		when( viewContext.holdsEntity() ).thenReturn( true );
		assertTrue( event.holdsEntity() );
	}

	@Test
	public void typedEntityValue() {
		when( viewContext.getEntity( Object.class ) ).thenReturn( "stringValue" );
		EntityPageStructureRenderedEvent<String> newEvent = new EntityPageStructureRenderedEvent<>( false, viewRequest, view, viewContext, builderContext );
		String actual = newEvent.getEntity();
		assertEquals( "stringValue", actual );
	}
}
