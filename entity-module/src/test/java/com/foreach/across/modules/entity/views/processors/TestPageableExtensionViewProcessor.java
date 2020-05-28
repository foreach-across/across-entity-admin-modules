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

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.context.request.NativeWebRequest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestPageableExtensionViewProcessor
{
	@Mock
	private NativeWebRequest webRequest;

	@Mock
	private EntityViewRequest viewRequest;

	@Mock
	private EntityPropertyRegistry propertyRegistry;

	private EntityViewCommand viewCommand;

	private PageableExtensionViewProcessor processor;

	@Before
	public void setUp() throws Exception {
		processor = new PageableExtensionViewProcessor();
		viewCommand = new EntityViewCommand();

		when( viewRequest.getWebRequest() ).thenReturn( webRequest );
		EntityViewContext ctx = mock( EntityViewContext.class );
		when( ctx.getPropertyRegistry() ).thenReturn( propertyRegistry );
		when( viewRequest.getEntityViewContext() ).thenReturn( ctx );
	}

	@Test
	public void defaultPageableForDefaultExtensionIsAlwaysRegistered() {
		processor.initializeCommandObject( viewRequest, viewCommand, null );

		Pageable pageable = viewCommand.getExtension( PageableExtensionViewProcessor.DEFAULT_EXTENSION_NAME, Pageable.class );
		assertNotNull( pageable );
	}

	@Test
	public void defaultCustomPageableAndExtension() {
		PageRequest expected = PageRequest.of( 10, 30,
		                                       Sort.by( new Sort.Order( Sort.Direction.ASC, "name" ), new Sort.Order( Sort.Direction.DESC, "date" ) ) );
		processor.setDefaultPageable( expected );
		processor.setExtensionName( "customPageable" );

		processor.initializeCommandObject( viewRequest, viewCommand, null );

		Pageable pageable = viewCommand.getExtension( "customPageable", Pageable.class );
		assertEquals( expected, pageable );
	}

	@Test
	public void defaultRequestParameters() {
		when( webRequest.getParameter( "size" ) ).thenReturn( "100" );
		when( webRequest.getParameter( "page" ) ).thenReturn( "5" );
		when( webRequest.getParameterValues( "sort" ) )
				.thenReturn( new String[] { "name,ASC", "date,DESC" } );

		PageRequest expected = PageRequest.of(
				5, 100, Sort.by( new Sort.Order( Sort.Direction.ASC, "name" ), new Sort.Order( Sort.Direction.DESC, "date" ) )
		);

		processor.initializeCommandObject( viewRequest, viewCommand, null );

		Pageable pageable = viewCommand.getExtension( PageableExtensionViewProcessor.DEFAULT_EXTENSION_NAME, Pageable.class );
		assertEquals( expected, pageable );
	}

	@Test
	public void customRequestParameters() {
		when( webRequest.getParameter( "extensions[custom].size" ) ).thenReturn( "100" );
		when( webRequest.getParameter( "extensions[custom].page" ) ).thenReturn( "5" );
		when( webRequest.getParameterValues( "extensions[custom].sort" ) )
				.thenReturn( new String[] { "name,ASC", "date,DESC" } );

		PageRequest expected = PageRequest.of(
				5, 100, Sort.by( new Sort.Order( Sort.Direction.ASC, "name" ), new Sort.Order( Sort.Direction.DESC, "date" ) )
		);

		processor.setRequestParameterPrefix( "extensions[custom]." );
		processor.setExtensionName( "customPageable" );

		processor.initializeCommandObject( viewRequest, viewCommand, null );

		Pageable pageable = viewCommand.getExtension( "customPageable", Pageable.class );
		assertEquals( expected, pageable );

		processor.setRequestParameterPrefix( null );
		processor.initializeCommandObject( viewRequest, viewCommand, null );
		assertNotEquals( expected, viewCommand.getExtension( "customPageable", Pageable.class ) );
	}

	@Test
	public void maxPageSize() {
		when( webRequest.getParameter( "size" ) ).thenReturn( "5000" );
		when( webRequest.getParameter( "page" ) ).thenReturn( "5" );
		when( webRequest.getParameterValues( "sort" ) ).thenReturn( new String[] { "name,ASC" } );

		PageRequest expected = PageRequest.of( 5, 2000, Sort.by( new Sort.Order( Sort.Direction.ASC, "name" ) ) );
		processor.initializeCommandObject( viewRequest, viewCommand, null );

		Pageable pageable = viewCommand.getExtension( PageableExtensionViewProcessor.DEFAULT_EXTENSION_NAME, Pageable.class );
		assertEquals( expected, pageable );

		processor.setMaxPageSize( 10000 );
		processor.initializeCommandObject( viewRequest, viewCommand, null );

		pageable = viewCommand.getExtension( PageableExtensionViewProcessor.DEFAULT_EXTENSION_NAME, Pageable.class );
		assertEquals( PageRequest.of( 5, 5000, Sort.by( new Sort.Order( Sort.Direction.ASC, "name" ) ) ), pageable );
	}

	@Test
	public void defaultTranslation() {
		when( webRequest.getParameter( "size" ) ).thenReturn( "5000" );
		when( webRequest.getParameter( "page" ) ).thenReturn( "5" );
		when( webRequest.getParameterValues( "sort" ) ).thenReturn( new String[] { "name,ASC" } );

		EntityPropertyDescriptor descriptor = mock( EntityPropertyDescriptor.class );
		when( descriptor.getAttribute( Sort.Order.class ) ).thenReturn( Sort.Order.by( "title" ).nullsLast() );
		when( propertyRegistry.getProperty( "name" ) ).thenReturn( descriptor );

		PageRequest expected = PageRequest.of( 5, 2000, Sort.by( new Sort.Order( Sort.Direction.ASC, "title" ).nullsLast() ) );
		processor.initializeCommandObject( viewRequest, viewCommand, null );

		Pageable pageable = viewCommand.getExtension( PageableExtensionViewProcessor.DEFAULT_EXTENSION_NAME, Pageable.class );
		assertEquals( expected, pageable );
	}

	@Test
	public void noTranslation() {
		when( webRequest.getParameter( "size" ) ).thenReturn( "1000" );
		when( webRequest.getParameter( "page" ) ).thenReturn( "5" );
		when( webRequest.getParameterValues( "sort" ) ).thenReturn( new String[] { "name,ASC" } );

		PageRequest expected = PageRequest.of( 5, 1000, Sort.by( new Sort.Order( Sort.Direction.ASC, "name" ) ) );
		processor.setTranslatePageable( false );
		processor.initializeCommandObject( viewRequest, viewCommand, null );

		Pageable pageable = viewCommand.getExtension( PageableExtensionViewProcessor.DEFAULT_EXTENSION_NAME, Pageable.class );
		assertEquals( expected, pageable );

		verifyNoMoreInteractions( propertyRegistry );
	}
}
