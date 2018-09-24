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

import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertySelector;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderFactory;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderService;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.FieldsetElementBuilderFactory;
import com.foreach.across.modules.entity.views.bootstrapui.elements.ViewElementFieldset;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import com.foreach.across.modules.web.ui.elements.builder.TextViewElementBuilder;
import lombok.val;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;
import java.util.function.Function;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
public class TestFieldsetElementBuilderFactory extends ViewElementBuilderFactoryTestSupport<ViewElementFieldset>
{
	@Mock
	private EntityViewElementBuilderService viewElementBuilderService;

	@Override
	protected EntityViewElementBuilderFactory createBuilderFactory() {
		return new FieldsetElementBuilderFactory( viewElementBuilderService );
	}

	@Override
	protected Class getTestClass() {
		return Instance.class;
	}

	@Test
	public void legendTextShouldBeSetToTheLabel() {
		ViewElementBuilder labelBuilder = new TextViewElementBuilder().text( "label text" );
		when( viewElementBuilderService.getElementBuilder( properties.get( "embedded" ), ViewElementMode.LABEL ) ).thenReturn( labelBuilder );

		EntityPropertyDescriptor embedded = properties.get( "embedded" );

		ViewElementFieldset fieldset = assemble( embedded, ViewElementMode.FORM_READ );
		assertNotNull( fieldset );
		assertEquals( "embedded", fieldset.getName() );
		assertEquals( "label text", ( (TextViewElement) fieldset.getTitle().getChildren().get( 0 ) ).getText() );
	}

	@Test
	public void customTemplateAttributeIsUsed() {
		val template = ViewElementFieldset.structureTemplate( "", "x/y" );

		EntityPropertyDescriptor embedded = properties.get( "embedded" );
		when( embedded.getAttribute( ViewElementFieldset.TEMPLATE, Function.class ) ).thenReturn( template );

		ViewElementFieldset fieldset = assemble( embedded, ViewElementMode.FORM_READ );
		assertNotNull( fieldset );
		assertSame( template, fieldset.getTemplate() );
	}

	@Test
	public void childPropertiesAreSelectedIfNoSelector() {
		EntityPropertyDescriptor member = mock( EntityPropertyDescriptor.class );

		EntityPropertySelector selector = new EntityPropertySelector( "embedded.*" );
		when( registry.select( selector ) ).thenReturn( Collections.singletonList( member ) );

		EntityPropertyDescriptor embedded = properties.get( "embedded" );

		ViewElementFieldset fieldset = assemble( embedded, ViewElementMode.FORM_READ );
		assertNotNull( fieldset );
		verify( registry ).select( selector );
		verify( viewElementBuilderService ).getElementBuilder( member, ViewElementMode.FORM_READ );
	}

	@Test
	public void specificPropertySelector() {
		EntityPropertyDescriptor member = mock( EntityPropertyDescriptor.class );

		EntityPropertySelector selector = new EntityPropertySelector( "embedded.one" );
		when( registry.select( selector ) ).thenReturn( Collections.singletonList( member ) );

		EntityPropertyDescriptor embedded = properties.get( "embedded" );
		when( embedded.getAttribute( EntityAttributes.FIELDSET_PROPERTY_SELECTOR, EntityPropertySelector.class ) )
				.thenReturn( selector );

		ViewElementFieldset fieldset = assemble( embedded, ViewElementMode.FORM_READ );
		assertNotNull( fieldset );
		verify( registry ).select( selector );
		verify( viewElementBuilderService ).getElementBuilder( member, ViewElementMode.FORM_READ );
	}

	@Test
	public void inWriteModeTextSettingsAreAdded() {
		EntityPropertyDescriptor member = mock( EntityPropertyDescriptor.class );

		EntityPropertySelector selector = new EntityPropertySelector( "embedded.one" );
		when( registry.select( selector ) ).thenReturn( Collections.singletonList( member ) );

		EntityPropertyDescriptor embedded = properties.get( "embedded" );
		when( embedded.getAttribute( EntityAttributes.FIELDSET_PROPERTY_SELECTOR, EntityPropertySelector.class ) )
				.thenReturn( selector );

		doReturn( "description" ).when( builderContext ).getMessage( "properties.embedded[description]", "" );
		doReturn( "" ).when( builderContext ).getMessage( "properties.embedded[help]", "" );
		doReturn( "tooltip" ).when( builderContext ).getMessage( "properties.embedded[tooltip]", "" );

		ViewElementBuilder propertyBuilder = mock( ViewElementBuilder.class );
		when( viewElementBuilderService.getElementBuilder( member, ViewElementMode.FORM_WRITE ) ).thenReturn(
				propertyBuilder );
		when( propertyBuilder.build( builderContext ) ).thenReturn( mock( TextViewElement.class ) );

		ViewElementFieldset fieldset = assemble( embedded, ViewElementMode.FORM_WRITE );
		assertNotNull( fieldset );

		NodeViewElement descriptionBlock = (NodeViewElement) fieldset.getHeader().getChildren().get( 0 );
		assertNotNull( descriptionBlock );
		assertEquals( "description", ( (TextViewElement) descriptionBlock.getChildren().get( 0 ) ).getText() );

		verify( builderContext ).getMessage( eq( "properties.embedded[description]" ), any( String.class ) );
		verify( builderContext ).getMessage( eq( "properties.embedded[help]" ), any( String.class ) );
		verify( builderContext ).getMessage( eq( "properties.embedded[tooltip]" ), any( String.class ) );
	}

	@Test
	public void inReadModeTheTextSettingsAreNotAdded() {
		EntityPropertyDescriptor embedded = properties.get( "embedded" );

		ViewElementFieldset fieldset = assemble( embedded, ViewElementMode.FORM_READ );
		assertNotNull( fieldset );
		assertTrue( fieldset.getBody().getChildren().isEmpty() );

		verify( builderContext, never() ).getMessage( eq( "properties.embedded[description]" ), any( String.class ) );
		verify( builderContext, never() ).getMessage( eq( "properties.embedded[help]" ), any( String.class ) );
		verify( builderContext, never() ).getMessage( eq( "properties.embedded[tooltip]" ), any( String.class ) );
	}

	private static class Instance
	{
		private Object embedded;
	}
}
