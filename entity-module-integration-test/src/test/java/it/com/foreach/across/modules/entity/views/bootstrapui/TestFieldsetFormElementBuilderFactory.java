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

import com.foreach.across.modules.bootstrapui.elements.FieldsetFormElement;
import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertySelector;
import com.foreach.across.modules.entity.registry.properties.meta.PropertyPersistenceMetadata;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderFactory;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderService;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.FieldsetFormElementBuilderFactory;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import com.foreach.across.modules.web.ui.elements.builder.TextViewElementBuilder;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
public class TestFieldsetFormElementBuilderFactory extends ViewElementBuilderFactoryTestSupport<FieldsetFormElement>
{
	@Mock
	private EntityViewElementBuilderService viewElementBuilderService;

	@Override
	protected EntityViewElementBuilderFactory createBuilderFactory() {
		return new FieldsetFormElementBuilderFactory( viewElementBuilderService );
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

		FieldsetFormElement fieldset = assemble( embedded, ViewElementMode.FORM_READ );
		assertNotNull( fieldset );
		assertEquals( "label text", ( (TextViewElement) fieldset.getLegend().getChildren().get( 0 ) ).getText() );
	}

	@Test
	public void embeddedClassSelectsByDefault() {
		EntityPropertyDescriptor member = mock( EntityPropertyDescriptor.class );

		EntityPropertySelector selector = new EntityPropertySelector( "embedded.*" );
		when( registry.select( selector ) ).thenReturn( Collections.singletonList( member ) );

		PropertyPersistenceMetadata metadata = new PropertyPersistenceMetadata();
		metadata.setEmbedded( true );

		EntityPropertyDescriptor embedded = properties.get( "embedded" );
		when( embedded.getAttribute( PropertyPersistenceMetadata.class ) ).thenReturn( metadata );

		FieldsetFormElement fieldset = assemble( embedded, ViewElementMode.FORM_READ );
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

		FieldsetFormElement fieldset = assemble( embedded, ViewElementMode.FORM_READ );
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

		FieldsetFormElement fieldset = assemble( embedded, ViewElementMode.FORM_WRITE );
		assertNotNull( fieldset );
		assertTrue( fieldset.getChildren().get( 0 ) instanceof FieldsetFormElement.Legend );

		NodeViewElement descriptionBlock = (NodeViewElement) fieldset.getChildren().get( 1 );
		assertNotNull( descriptionBlock );
		assertEquals( "description", ( (TextViewElement) descriptionBlock.getChildren().get( 0 ) ).getText() );

		verify( builderContext ).getMessage( eq( "properties.embedded[description]" ), any( String.class ) );
		verify( builderContext ).getMessage( eq( "properties.embedded[help]" ), any( String.class ) );
		verify( builderContext ).getMessage( eq( "properties.embedded[tooltip]" ), any( String.class ) );
	}

	@Test
	public void inReadModeTheTextSettingsAreNotAdded() {
		EntityPropertyDescriptor embedded = properties.get( "embedded" );

		FieldsetFormElement fieldset = assemble( embedded, ViewElementMode.FORM_READ );
		assertNotNull( fieldset );
		assertTrue( fieldset.getChildren().isEmpty() );

		verify( builderContext, never() ).getMessage( eq( "properties.embedded[description]" ), any( String.class ) );
		verify( builderContext, never() ).getMessage( eq( "properties.embedded[help]" ), any( String.class ) );
		verify( builderContext, never() ).getMessage( eq( "properties.embedded[tooltip]" ), any( String.class ) );
	}

	private static class Instance
	{
		private Object embedded;
	}
}
