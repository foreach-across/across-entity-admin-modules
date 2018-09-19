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
package com.foreach.across.modules.entity.views.util;

import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyHandlingType;
import com.foreach.across.modules.entity.registry.properties.SimpleEntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.web.EntityViewModel;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.IteratorItemStatsImpl;
import com.foreach.across.modules.web.ui.IteratorViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import org.junit.Test;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import static com.foreach.across.modules.entity.views.util.EntityViewElementUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 */
public class TestEntityViewElementUtils
{
	private final static Object SOME_ENTITY = "someEntity";

	@Test
	public void currentEntityForNullContext() {
		assertNull( currentEntity( null ) );
	}

	@Test
	public void currentEntityForIterator() {
		IteratorViewElementBuilderContext ctx = new IteratorViewElementBuilderContext<>(
				new IteratorItemStatsImpl<>( SOME_ENTITY, 0, false )
		);

		assertSame( SOME_ENTITY, currentEntity( ctx ) );
		assertSame( SOME_ENTITY, currentEntity( ctx, String.class ) );
		assertNull( currentEntity( ctx, Integer.class ) );
	}

	@Test
	public void currentEntityForEntityView() {
		EntityView view = new EntityView( new ModelMap(), new RedirectAttributesModelMap() );

		ViewElementBuilderContext ctx = new DefaultViewElementBuilderContext( view );
		assertNull( currentEntity( ctx ) );

		view.addAttribute( EntityViewModel.ENTITY, SOME_ENTITY );
		assertSame( SOME_ENTITY, currentEntity( ctx ) );
		assertSame( SOME_ENTITY, currentEntity( ctx, String.class ) );
		assertNull( currentEntity( ctx, Integer.class ) );
	}

	@Test
	public void currentPropertyValueIsNullIfNoPropertyDescriptor() {
		ViewElementBuilderContext ctx = new DefaultViewElementBuilderContext();
		setCurrentEntity( ctx, "my entity" );

		assertNull( currentPropertyValue( ctx ) );
	}

	@Test
	public void currentPropertyValueIsReturnValueOfValueFetcher() {
		ViewElementBuilderContext ctx = new DefaultViewElementBuilderContext();
		EntityPropertyDescriptor descriptor = mock( EntityPropertyDescriptor.class );
		ctx.setAttribute( EntityPropertyDescriptor.class, descriptor );
		setCurrentEntity( ctx, "my entity" );

		when( descriptor.getPropertyValue( "my entity" ) ).thenReturn( 123L );

		assertEquals( 123L, currentPropertyValue( ctx ) );
	}

	@Test
	public void currentPropertyValueIsNullIfNotOfCorrectType() {
		ViewElementBuilderContext ctx = new DefaultViewElementBuilderContext();
		EntityPropertyDescriptor descriptor = mock( EntityPropertyDescriptor.class );
		ctx.setAttribute( EntityPropertyDescriptor.class, descriptor );
		setCurrentEntity( ctx, "my entity" );

		when( descriptor.getPropertyValue( "my entity" ) ).thenReturn( 123L );

		assertEquals( Long.valueOf( 123L ), currentPropertyValue( ctx, Long.class ) );
		assertNull( currentPropertyValue( ctx, String.class ) );
	}

	@Test
	public void generateControlNameWithoutParentInspectsHandlingType() {
		ViewElementBuilderContext ctx = new DefaultViewElementBuilderContext();
		SimpleEntityPropertyDescriptor userDescriptor = new SimpleEntityPropertyDescriptor( "user" );
		SimpleEntityPropertyDescriptor streetDescriptor = new SimpleEntityPropertyDescriptor( "user.street" );
		streetDescriptor.setParentDescriptor( userDescriptor );

		userDescriptor.setAttribute( EntityPropertyHandlingType.class, EntityPropertyHandlingType.DIRECT );

		{
			// direct
			streetDescriptor.setAttribute( EntityPropertyHandlingType.class, EntityPropertyHandlingType.DIRECT );
			assertEquals(
					"user.street",
					EntityViewElementUtils.controlName( streetDescriptor, ctx ).toString()
			);
		}

		{
			// extension uses the binder
			streetDescriptor.setAttribute( EntityPropertyHandlingType.class, EntityPropertyHandlingType.EXTENSION );
			assertEquals(
					"properties[user].properties[street].value",
					EntityViewElementUtils.controlName( streetDescriptor, ctx ).toString()
			);
		}

		{
			// manual uses the control name
			streetDescriptor.setAttribute( EntityPropertyHandlingType.class, EntityPropertyHandlingType.MANUAL );
			streetDescriptor.setAttribute( EntityAttributes.CONTROL_NAME, "customStreetControl" );

			assertEquals(
					"customStreetControl",
					EntityViewElementUtils.controlName( streetDescriptor, ctx ).toString()
			);
		}
	}
}
