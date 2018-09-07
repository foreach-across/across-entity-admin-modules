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

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.hibernate.business.IdBasedEntity;
import com.foreach.across.modules.spring.security.actions.AllowableAction;
import com.foreach.across.modules.spring.security.actions.AllowableActionSet;
import com.foreach.across.modules.spring.security.actions.AllowableActions;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestAbstractEntityFetchingViewProcessor
{
	Entry one = new Entry( 1L ), two = new Entry( 2L ),
			three = new Entry( 3L ), four = new Entry( 4L );
	List<Entry> entries = Arrays.asList( one, two, three, four );

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

	@Test
	@SuppressWarnings("unchecked")
	public void filterByRequestedAction() {
		EntityConfiguration entityConfiguration = mock( EntityConfiguration.class );
		when( entityConfiguration.getAllowableActions( any( IdBasedEntity.class ) ) )
				.thenAnswer( getAllowableActionsAnswer() );
		processor.setAccessItemAction( AllowableAction.READ );

		Iterable iterable = processor.filterAccessibleItems( entries, entityConfiguration, null );
		assertThat( iterable ).isNotNull()
		                      .isInstanceOf( List.class )
		                      .containsExactly( two, four );
		processor.setAccessItemAction( null );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void filterByRequestedActionForPage() {
		EntityConfiguration entityConfiguration = mock( EntityConfiguration.class );
		when( entityConfiguration.getAllowableActions( any( IdBasedEntity.class ) ) )
				.thenAnswer( getAllowableActionsAnswer() );
		processor.setAccessItemAction( AllowableAction.READ );

		Iterable iterable = processor.filterAccessibleItems( entries, entityConfiguration, new PageRequest( 0, 1, Sort.Direction.DESC, "id" ) );
		assertThat( iterable ).isNotNull()
		                      .isInstanceOf( Page.class )
		                      .containsExactly( two );
		Page page = (Page) iterable;
		assertThat( page.getTotalElements() ).isEqualTo( 2 );
		assertThat( page.getTotalPages() ).isEqualTo( 2 );
		processor.setAccessItemAction( null );
	}

	private Answer<AllowableActions> getAllowableActionsAnswer() {
		return (Answer<AllowableActions>) invocation -> {
			IdBasedEntity argument = invocation.getArgument( 0 );
			return Math.floorMod( argument.getId(), 2L ) == 0 ? new AllowableActionSet( AllowableAction.READ.getId() ) : new AllowableActionSet();
		};
	}

	@Data
	@RequiredArgsConstructor
	public static class Entry implements IdBasedEntity
	{
		private final Long id;
	}
}
