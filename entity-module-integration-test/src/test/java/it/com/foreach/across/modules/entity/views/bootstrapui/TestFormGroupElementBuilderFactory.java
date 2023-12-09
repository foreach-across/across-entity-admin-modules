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

import com.foreach.across.modules.bootstrapui.elements.FormGroupElement;
import com.foreach.across.modules.bootstrapui.elements.builder.LabelFormElementBuilder;
import com.foreach.across.modules.bootstrapui.elements.builder.TextboxFormElementBuilder;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderFactory;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderService;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.FormGroupElementBuilderFactory;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
public class TestFormGroupElementBuilderFactory extends ViewElementBuilderFactoryTestSupport<FormGroupElement>
{
	@Mock
	private EntityViewElementBuilderService viewElementBuilderService;

	@Override
	protected EntityViewElementBuilderFactory createBuilderFactory() {
		return new FormGroupElementBuilderFactory( viewElementBuilderService );
	}

	@Override
	protected Class getTestClass() {
		return Instance.class;
	}

	@Test
	public void textElementsAreBeingLookedUp() {
		when( properties.get( "text" ).isWritable() ).thenReturn( true );

		ViewElementBuilder controlBuilder = new TextboxFormElementBuilder().required();
		when( viewElementBuilderService.getElementBuilder( properties.get( "text" ), ViewElementMode.CONTROL ) ).thenReturn( controlBuilder );

		ViewElementBuilder labelBuilder = new LabelFormElementBuilder();
		when( viewElementBuilderService.getElementBuilder( properties.get( "text" ), ViewElementMode.LABEL ) ).thenReturn( labelBuilder );

		doReturn( "description" ).when( builderContext ).getMessage( "properties.text[description]", "" );
		doReturn( "" ).when( builderContext ).getMessage( "properties.text[help]", "" );
		doReturn( "tooltip" ).when( builderContext ).getMessage( "properties.text[tooltip]", "" );

		FormGroupElement group = assembleAndVerify( "text", true, ViewElementMode.FORM_WRITE );
		assertNotNull( group.getDescriptionBlock() );
		assertNull( group.getHelpBlock() );
		assertNotNull( group.getTooltip() );

		verify( builderContext ).getMessage( "properties.text[description]", "" );
		verify( builderContext ).getMessage( "properties.text[tooltip]", "" );
		verify( builderContext ).getMessage( "properties.text[help]", "" );
	}

	@Test
	public void inReadModeTheTextAndRequiredSettingsAreNotAdded() {
		when( properties.get( "text" ).isWritable() ).thenReturn( true );

		ViewElementBuilder controlBuilder = new TextboxFormElementBuilder().required();
		when( viewElementBuilderService.getElementBuilder( properties.get( "text" ), ViewElementMode.VALUE ) ).thenReturn( controlBuilder );

		ViewElementBuilder labelBuilder = new LabelFormElementBuilder();
		when( viewElementBuilderService.getElementBuilder( properties.get( "text" ), ViewElementMode.LABEL ) ).thenReturn( labelBuilder );

		FormGroupElement group = assembleAndVerify( "text", false, ViewElementMode.FORM_READ );
		assertNull( group.getHelpBlock() );
		assertNull( group.getTooltip() );
		assertNull( group.getDescriptionBlock() );
		verify( builderContext, never() ).getMessage( eq( "properties.text[description]" ), any( String.class ) );
		verify( builderContext, never() ).getMessage( eq( "properties.text[help]" ), any( String.class ) );
		verify( builderContext, never() ).getMessage( eq( "properties.text[tooltip]" ), any( String.class ) );
	}

	@SuppressWarnings("unchecked")
	private <V> V assembleAndVerify( String propertyName, boolean required, ViewElementMode mode ) {
		FormGroupElement control = assemble( propertyName, mode );
		assertEquals( "formGroup-" + propertyName, control.getName() );
		assertEquals( required, control.isRequired() );

		return (V) control;
	}

	@SuppressWarnings("unused")
	private static class Instance
	{
		private String text;
	}
}
