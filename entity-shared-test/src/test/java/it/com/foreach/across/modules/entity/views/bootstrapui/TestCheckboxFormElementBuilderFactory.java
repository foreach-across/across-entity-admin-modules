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

import com.foreach.across.modules.bootstrapui.elements.CheckboxFormElement;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderFactory;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.CheckboxFormElementBuilderFactory;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import com.foreach.across.modules.entity.web.EntityViewModel;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 */
public class TestCheckboxFormElementBuilderFactory extends ViewElementBuilderFactoryTestSupport<CheckboxFormElement>
{
	@Override
	protected EntityViewElementBuilderFactory createBuilderFactory() {
		return new CheckboxFormElementBuilderFactory();
	}

	@Override
	protected Class getTestClass() {
		return Booleans.class;
	}

	@Test
	public void controlNamePrefixing() {
		when( builderContext.hasAttribute( EntityViewCommand.class ) ).thenReturn( true );
		CheckboxFormElement checkbox = assemble( "primitive", ViewElementMode.CONTROL );
		assertEquals( "entity.primitive", checkbox.getControlName() );
	}

	@Test
	public void withoutValue() {
		CheckboxFormElement checkbox = assembleAndVerify( "primitive", false );
		assertFalse( checkbox.isChecked() );

		checkbox = assembleAndVerify( "object", false );
		assertFalse( checkbox.isChecked() );

		checkbox = assembleAndVerify( "atomic", false );
		assertFalse( checkbox.isChecked() );
	}

	@Test
	public void checkedFromEntity() {
		when( properties.get( "primitive" ).getValueFetcher() ).thenReturn( new ValueFetcher()
		{
			@Override
			public Object getValue( Object entity ) {
				return true;
			}
		} );
		when( properties.get( "object" ).getValueFetcher() ).thenReturn( new ValueFetcher()
		{
			@Override
			public Object getValue( Object entity ) {
				return Boolean.TRUE;
			}
		} );
		when( properties.get( "atomic" ).getValueFetcher() ).thenReturn( new ValueFetcher()
		{
			@Override
			public Object getValue( Object entity ) {
				return new AtomicBoolean( true );
			}
		} );
		when( builderContext.getAttribute( EntityViewModel.ENTITY ) ).thenReturn( "entity" );

		CheckboxFormElement checkbox = assembleAndVerify( "primitive", false );
		assertTrue( checkbox.isChecked() );

		checkbox = assembleAndVerify( "object", false );
		assertTrue( checkbox.isChecked() );

		checkbox = assembleAndVerify( "atomic", false );
		assertTrue( checkbox.isChecked() );
	}

	@Test
	public void uncheckedFromEntity() {
		when( properties.get( "primitive" ).getValueFetcher() ).thenReturn( new ValueFetcher()
		{
			@Override
			public Object getValue( Object entity ) {
				return false;
			}
		} );
		when( properties.get( "object" ).getValueFetcher() ).thenReturn( new ValueFetcher()
		{
			@Override
			public Object getValue( Object entity ) {
				return null;
			}
		} );
		when( properties.get( "atomic" ).getValueFetcher() ).thenReturn( new ValueFetcher()
		{
			@Override
			public Object getValue( Object entity ) {
				return new AtomicBoolean( false );
			}
		} );
		when( builderContext.getAttribute( EntityViewModel.ENTITY ) ).thenReturn( "entity" );

		CheckboxFormElement checkbox = assembleAndVerify( "primitive", false );
		assertFalse( checkbox.isChecked() );

		checkbox = assembleAndVerify( "object", false );
		assertFalse( checkbox.isChecked() );

		checkbox = assembleAndVerify( "atomic", false );
		assertFalse( checkbox.isChecked() );
	}

	@SuppressWarnings("unchecked")
	private <V> V assembleAndVerify( String propertyName, boolean required ) {
		CheckboxFormElement control = assemble( propertyName, ViewElementMode.CONTROL );
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
