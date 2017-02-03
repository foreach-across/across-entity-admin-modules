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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collection;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestEntityView
{
	@Mock
	private ModelMap model;

	@Mock
	private RedirectAttributes redirectAttributes;

	@InjectMocks
	private EntityView view;

	@Test
	public void redirectAttributes() {
		assertSame( redirectAttributes, view.getRedirectAttributes() );
	}

	@Test
	@SuppressWarnings( "unchecked" )
	public void modelBehaviour() {
		view.addAttribute( "test", "test" );
		verify( model ).addAttribute( "test", "test" );

		Collection attributeValuesCollection = mock( Collection.class );
		view.addAllAttributes( attributeValuesCollection );
		verify( model ).addAllAttributes( attributeValuesCollection );

		Map<String, Object> attributeValuesMap = mock( Map.class );
		view.addAllAttributes( attributeValuesMap );
		verify( model ).addAllAttributes( attributeValuesMap );

		view.addAttribute( "attributeValue" );
		verify( model ).addAttribute( "attributeValue" );

		view.containsAttribute( "someAttribute" );
		verify( model ).containsAttribute( "someAttribute" );

		view.getAttribute( "attribute" );
		verify( model ).get( "attribute" );

		when( model.get( "longValue" ) ).thenReturn( 123L );
		assertEquals( Long.valueOf( 123L ), view.getAttribute( "longValue", Long.class ) );

		when( model.remove( "attributeToRemove" ) ).thenReturn( "removed" );
		assertEquals( "removed", view.removeAttribute( "attributeToRemove" ) );

		when( model.remove( "longToRemove" ) ).thenReturn( 123L );
		assertEquals( Long.valueOf( 123L ), view.removeAttribute( "longToRemove", Long.class ) );

		assertSame( model, view.asMap() );
	}

	@Test
	public void defaultSettings() {
		assertTrue( view.shouldRender() );
		assertFalse( view.isRedirect() );
		assertFalse( view.isCustomView() );
		assertFalse( view.isResponseEntity() );
	}

	@Test
	public void redirectDisablesRender() {
		view.setRedirectUrl( "url" );
		assertTrue( view.isRedirect() );
		assertFalse( view.shouldRender() );
		view.setRedirectUrl( null );
		assertTrue( view.shouldRender() );
	}

	@Test
	public void responseEntity() {
		view.setResponseEntity( mock( ResponseEntity.class ) );
		assertTrue( view.isResponseEntity() );
		assertTrue( view.isCustomView() );
	}

	@Test
	public void customView() {
		view.setCustomView( "sklmsjdfds" );
		assertFalse( view.isResponseEntity() );
		assertTrue( view.isCustomView() );
	}
}
