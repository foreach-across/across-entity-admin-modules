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
import com.foreach.across.modules.entity.bind.EntityPropertiesBinder;
import com.foreach.across.modules.entity.bind.EntityPropertyBinder;
import com.foreach.across.modules.entity.bind.EntityPropertyControlName;
import com.foreach.across.modules.entity.bind.SingleEntityPropertyBinder;
import com.foreach.across.modules.entity.registry.properties.*;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.web.EntityViewModel;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.IteratorItemStatsImpl;
import com.foreach.across.modules.web.ui.IteratorViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import static com.foreach.across.modules.entity.views.util.EntityViewElementUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 */
@RunWith(MockitoJUnitRunner.class)
public class TestEntityViewElementUtils
{
	private final static Object SOME_ENTITY = "someEntity";

	private ViewElementBuilderContext builderContext = new DefaultViewElementBuilderContext();

	@Mock
	private EntityPropertyDescriptor descriptor;

	@Mock
	private EntityPropertyController controller;

	@Before
	public void before() {
		when( descriptor.getController() ).thenReturn( controller );
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
		setCurrentEntity( builderContext, "my entity" );
		assertNull( currentPropertyValue( builderContext ) );
	}

	@Test
	public void currentPropertyValueUsesFixedValueIfSet() {
		setCurrentPropertyValue( builderContext, "123" );
		assertEquals( "123", currentPropertyValue( builderContext ) );
	}

	@Test
	public void currentPropertyValueIsReturnValueOfControllerWithCurrentEntity() {
		when( descriptor.getName() ).thenReturn( "myprop" );
		builderContext.setAttribute( EntityPropertyDescriptor.class, descriptor );
		setCurrentEntity( builderContext, "my entity" );

		when( controller.fetchValue( EntityPropertyBindingContext.forReading( "my entity" ) ) ).thenReturn( 123L );

		assertEquals( 123L, currentPropertyValue( builderContext ) );
	}

	@Test
	public void currentPropertyBinderIsNullIfNoEntityPropertiesBinder() {
		builderContext.setAttribute( EntityPropertyDescriptor.class, descriptor );
		assertNull( currentPropertyBinder( builderContext ) );
	}

	@Test
	public void currentPropertyBinderIsResolvedFromEntityPropertiesBinder() {
		EntityPropertiesBinder propertiesBinder = mock( EntityPropertiesBinder.class );
		builderContext.setAttribute( EntityPropertiesBinder.class, propertiesBinder );

		EntityPropertyBinder target = mock( EntityPropertyBinder.class );
		when( propertiesBinder.get( descriptor ) ).thenReturn( target );

		builderContext.setAttribute( EntityPropertyDescriptor.class, descriptor );

		assertSame( target, currentPropertyBinder( builderContext ) );
	}

	@Test
	public void currentPropertyBinderIsResolvedFromParentPropertyBinderIfOneIsSet() {
		EntityPropertiesBinder propertiesBinder = mock( EntityPropertiesBinder.class );
		builderContext.setAttribute( EntityPropertiesBinder.class, propertiesBinder );

		SingleEntityPropertyBinder parent = mock( SingleEntityPropertyBinder.class );
		EntityPropertyBinder target = mock( EntityPropertyBinder.class );
		when( parent.resolvePropertyBinder( descriptor ) ).thenReturn( target );

		builderContext.setAttribute( EntityPropertyDescriptor.class, descriptor );
		builderContext.setAttribute( EntityPropertyBinder.class, parent );

		assertSame( target, currentPropertyBinder( builderContext ) );
	}

	@Test
	public void currentPropertyValueIsNullIfNotOfCorrectType() {
		builderContext.setAttribute( EntityPropertyDescriptor.class, descriptor );
		when( descriptor.getName() ).thenReturn( "myprop" );
		setCurrentEntity( builderContext, "my entity" );

		when( controller.fetchValue( EntityPropertyBindingContext.forReading( "my entity" ) ) ).thenReturn( 123L );

		assertEquals( Long.valueOf( 123L ), currentPropertyValue( builderContext, Long.class ) );
		assertNull( currentPropertyValue( builderContext, String.class ) );
	}

	@Test
	public void generateControlNameWithoutParentInspectsHandlingType() {
		SimpleEntityPropertyDescriptor userDescriptor = new SimpleEntityPropertyDescriptor( "user" );
		SimpleEntityPropertyDescriptor streetDescriptor = new SimpleEntityPropertyDescriptor( "user.street" );
		streetDescriptor.setParentDescriptor( userDescriptor );

		userDescriptor.setAttribute( EntityPropertyHandlingType.class, EntityPropertyHandlingType.DIRECT );

		{
			// direct
			streetDescriptor.setAttribute( EntityPropertyHandlingType.class, EntityPropertyHandlingType.DIRECT );
			assertEquals(
					"user.street",
					EntityViewElementUtils.controlName( streetDescriptor, builderContext ).toString()
			);
		}

		{
			// extension uses the binder
			streetDescriptor.setAttribute( EntityPropertyHandlingType.class, EntityPropertyHandlingType.BINDER );
			assertEquals(
					"properties[user].properties[street].value",
					EntityViewElementUtils.controlName( streetDescriptor, builderContext ).toString()
			);
		}

		{
			// manual uses the control name
			streetDescriptor.setAttribute( EntityPropertyHandlingType.class, EntityPropertyHandlingType.MANUAL );
			streetDescriptor.setAttribute( EntityAttributes.CONTROL_NAME, "customStreetControl" );

			assertEquals(
					"customStreetControl",
					EntityViewElementUtils.controlName( streetDescriptor, builderContext ).toString()
			);
		}
	}

	@Test
	public void generateControlNameWithParentOnTheBuilderContext() {
		builderContext.setAttribute( EntityPropertyControlName.class, EntityPropertyControlName.forProperty( "data" ) );

		SimpleEntityPropertyDescriptor userDescriptor = new SimpleEntityPropertyDescriptor( "user" );
		SimpleEntityPropertyDescriptor streetDescriptor = new SimpleEntityPropertyDescriptor( "user.street" );
		streetDescriptor.setParentDescriptor( userDescriptor );

		userDescriptor.setAttribute( EntityPropertyHandlingType.class, EntityPropertyHandlingType.DIRECT );

		{
			// direct
			streetDescriptor.setAttribute( EntityPropertyHandlingType.class, EntityPropertyHandlingType.DIRECT );
			assertEquals(
					"data.user.street",
					EntityViewElementUtils.controlName( streetDescriptor, builderContext ).toString()
			);
		}

		{
			// extension uses the binder
			streetDescriptor.setAttribute( EntityPropertyHandlingType.class, EntityPropertyHandlingType.BINDER );
			assertEquals(
					"properties[data].properties[user.street].value",
					EntityViewElementUtils.controlName( streetDescriptor, builderContext ).toString()
			);
		}

		{
			// manual uses the control name
			streetDescriptor.setAttribute( EntityPropertyHandlingType.class, EntityPropertyHandlingType.MANUAL );
			streetDescriptor.setAttribute( EntityAttributes.CONTROL_NAME, "customStreetControl" );

			assertEquals(
					"customStreetControl",
					EntityViewElementUtils.controlName( streetDescriptor, builderContext ).toString()
			);
		}
	}
}
