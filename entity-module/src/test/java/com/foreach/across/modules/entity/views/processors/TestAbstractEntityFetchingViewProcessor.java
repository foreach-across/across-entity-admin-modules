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

import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Pageable;

import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestAbstractEntityFetchingViewProcessor
{
	@Mock
	private EntityViewRequest viewRequest;

	@Mock
	private EntityView entityView;

	@Mock
	private EntityViewCommand command;

	@Mock
	private Pageable pageable;

	@Mock
	private Iterable<Object> items;

	@Spy
	private AbstractEntityFetchingViewProcessor processor;

	@Test
	public void notFetchedIfNoPageable() {
		processor.doControl( viewRequest, entityView, command );
		verify( entityView ).containsAttribute( AbstractEntityFetchingViewProcessor.DEFAULT_ATTRIBUTE_NAME );
		verifyNoMoreInteractions( entityView );
		verify( processor, never() ).fetchItems( any(), any(), any() );
	}

	@Test
	public void fetchedIfPageableAndNoExistingItem() {
		when( command.getExtension( PageableExtensionViewProcessor.DEFAULT_EXTENSION_NAME, Pageable.class ) ).thenReturn( pageable );
		when( processor.fetchItems( viewRequest, entityView, pageable ) ).thenReturn( items );
		processor.doControl( viewRequest, entityView, command );

		verify( entityView ).addAttribute( AbstractEntityFetchingViewProcessor.DEFAULT_ATTRIBUTE_NAME, items );
	}

	@Test
	public void fetchingSkippedIfExistingItem() {
		when( entityView.containsAttribute( AbstractEntityFetchingViewProcessor.DEFAULT_ATTRIBUTE_NAME ) ).thenReturn( true );

		processor.doControl( viewRequest, entityView, command );

		verify( entityView, never() ).addAttribute( any(), any() );
		verify( processor, never() ).fetchItems( any(), any(), any() );
	}

	@Test
	public void replacingExistingItem() {
		when( command.getExtension( PageableExtensionViewProcessor.DEFAULT_EXTENSION_NAME, Pageable.class ) ).thenReturn( pageable );
		when( entityView.containsAttribute( AbstractEntityFetchingViewProcessor.DEFAULT_ATTRIBUTE_NAME ) ).thenReturn( true );
		when( processor.fetchItems( viewRequest, entityView, pageable ) ).thenReturn( items );

		processor.setReplaceExistingAttribute( true );
		processor.doControl( viewRequest, entityView, command );

		verify( entityView ).addAttribute( AbstractEntityFetchingViewProcessor.DEFAULT_ATTRIBUTE_NAME, items );
	}

	@Test
	public void customAttributeAndExtensionName() {
		when( command.getExtension( "customExtension", Pageable.class ) ).thenReturn( pageable );
		when( processor.fetchItems( viewRequest, entityView, pageable ) ).thenReturn( items );

		processor.setAttributeName( "customAttribute" );
		processor.setPageableExtensionName( "customExtension" );
		processor.doControl( viewRequest, entityView, command );

		verify( entityView ).containsAttribute( "customAttribute" );
		verify( entityView ).addAttribute( "customAttribute", items );
	}
}
