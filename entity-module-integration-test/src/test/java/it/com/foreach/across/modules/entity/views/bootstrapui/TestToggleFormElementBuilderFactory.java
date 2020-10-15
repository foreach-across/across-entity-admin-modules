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

package it.com.foreach.across.modules.entity.views.bootstrapui;

import com.foreach.across.modules.bootstrapui.elements.ToggleFormElement;
import com.foreach.across.modules.entity.query.EQValue;
import com.foreach.across.modules.entity.query.EntityQueryOps;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyBindingContext;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderFactory;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.ToggleFormElementBuilderFactory;
import com.foreach.across.modules.entity.views.processors.EntityQueryFilterProcessor;
import com.foreach.across.modules.entity.views.processors.query.EntityQueryFilterControlUtils;
import com.foreach.across.modules.entity.web.EntityViewModel;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 */
public class TestToggleFormElementBuilderFactory extends ViewElementBuilderFactoryTestSupport<ToggleFormElement>
{
	@Override
	protected EntityViewElementBuilderFactory createBuilderFactory() {
		return new ToggleFormElementBuilderFactory();
	}

	@Override
	protected Class getTestClass() {
		return Booleans.class;
	}

	@Test
	public void controlNamePrefixing() {
		simulateEntityViewForm();
		ToggleFormElement toggle = assemble( "primitive", ViewElementMode.CONTROL );
		assertEquals( "entity.primitive", toggle.getControlName() );
	}

	@Test
	public void withoutValue() {
		ToggleFormElement toggle = assembleAndVerify( "primitive", false );
		assertFalse( toggle.isChecked() );

		toggle = assembleAndVerify( "object", false );
		assertFalse( toggle.isChecked() );

		toggle = assembleAndVerify( "atomic", false );
		assertFalse( toggle.isChecked() );
	}

	@Test
	public void checkedFromEntity() {
		EntityPropertyBindingContext bindingContext = EntityPropertyBindingContext.forReading( "entity" );
		when( properties.get( "primitive" ).getController().fetchValue( bindingContext ) ).thenReturn( true );
		when( properties.get( "object" ).getController().fetchValue( bindingContext ) ).thenReturn( Boolean.TRUE );
		when( properties.get( "atomic" ).getController().fetchValue( bindingContext ) ).thenReturn( new AtomicBoolean( true ) );

		builderContext.setAttribute( EntityViewModel.ENTITY, "entity" );

		ToggleFormElement toggle = assembleAndVerify( "primitive", false );
		assertTrue( toggle.isChecked() );

		toggle = assembleAndVerify( "object", false );
		assertTrue( toggle.isChecked() );

		toggle = assembleAndVerify( "atomic", false );
		assertTrue( toggle.isChecked() );
	}

	@Test
	public void uncheckedFromEntity() {
		EntityPropertyBindingContext bindingContext = EntityPropertyBindingContext.forReading( "entity" );
		when( properties.get( "primitive" ).getController().fetchValue( bindingContext ) ).thenReturn( false );
		when( properties.get( "object" ).getController().fetchValue( bindingContext ) ).thenReturn( null );
		when( properties.get( "atomic" ).getController().fetchValue( bindingContext ) ).thenReturn( new AtomicBoolean( false ) );
		builderContext.setAttribute( EntityViewModel.ENTITY, "entity" );

		ToggleFormElement toggle = assembleAndVerify( "primitive", false );
		assertFalse( toggle.isChecked() );

		toggle = assembleAndVerify( "object", false );
		assertFalse( toggle.isChecked() );

		toggle = assembleAndVerify( "atomic", false );
		assertFalse( toggle.isChecked() );
	}

	@Test
	public void filterControlProperties() {
		when( properties.get( "primitive" ).getAttribute( EntityQueryOps.class ) ).thenReturn( EntityQueryOps.EQ );

		ToggleFormElement toggle = assemble( "primitive", ViewElementMode.FILTER_CONTROL );
		assertEquals( true, toggle.getValue() );
		assertTrue( toggle.hasCssClass( EntityQueryFilterProcessor.ENTITY_QUERY_CONTROL_MARKER ) );
		assertEquals( "primitive", toggle.getAttribute( "data-" + EntityQueryFilterControlUtils.FilterControlAttributes.PROPERTY_NAME ) );
		assertEquals( EntityQueryOps.EQ.name(), toggle.getAttribute( "data-" + EntityQueryFilterControlUtils.FilterControlAttributes.OPERAND ) );
		assertEquals( EQValue.class.getSimpleName(), toggle.getAttribute( "data-" + EntityQueryFilterControlUtils.FilterControlAttributes.TYPE ) );
	}

	@SuppressWarnings("unchecked")
	private <V> V assembleAndVerify( String propertyName, boolean required ) {
		ToggleFormElement control = assemble( propertyName, ViewElementMode.CONTROL );
		assertEquals( propertyName, control.getName() );
		assertEquals( propertyName, control.getControlName() );
		assertEquals( "resolved: " + propertyName, control.getText() );
		assertFalse( control.isReadonly() );
		assertFalse( control.isDisabled() );
		assertEquals( required, control.isRequired() );
		assertEquals( "on", control.getValue() );

		return (V) control;
	}

	private static class Booleans
	{
		public boolean primitive;

		public Boolean object;

		public AtomicBoolean atomic;
	}
}
