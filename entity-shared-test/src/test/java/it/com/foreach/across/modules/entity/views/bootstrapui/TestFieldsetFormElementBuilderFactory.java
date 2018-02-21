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
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderFactory;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderService;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.FieldsetFormElementBuilderFactory;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
		FieldsetFormElementBuilderFactory builderFactory = new FieldsetFormElementBuilderFactory();
		builderFactory.setEntityViewElementBuilderService( viewElementBuilderService );
		return builderFactory;
	}

	@Override
	protected Class getTestClass() {
		return Instance.class;
	}

	@Test
	public void legendTextShouldBeSet() {
		EntityPropertyDescriptor embedded = properties.get( "embedded" );

		FieldsetFormElement fieldset = assemble( embedded, ViewElementMode.FORM_READ );
		assertNotNull( fieldset );
		assertEquals( "resolved: embedded", fieldset.getLegend().getText() );
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
	public void descriptionTextIsAddedOnTopOfFieldSet() {
		EntityPropertyDescriptor member = mock( EntityPropertyDescriptor.class );

		EntityPropertySelector selector = new EntityPropertySelector( "embedded.one" );
		when( registry.select( selector ) ).thenReturn( Collections.singletonList( member ) );

		EntityPropertyDescriptor embedded = properties.get( "embedded" );
		when( embedded.getAttribute( EntityAttributes.FIELDSET_PROPERTY_SELECTOR, EntityPropertySelector.class ) )
				.thenReturn( selector );

		EntityMessageCodeResolver codeResolver = mock( EntityMessageCodeResolver.class );
		when( codeResolver.getMessageWithFallback( "properties.embedded[description]", "" ) ).thenReturn( "help text" );
		when( builderContext.getAttribute( EntityMessageCodeResolver.class ) ).thenReturn( codeResolver );

		ViewElementBuilder propertyBuilder = mock( ViewElementBuilder.class );
		when( viewElementBuilderService.getElementBuilder( member, ViewElementMode.FORM_READ ) ).thenReturn(
				propertyBuilder );
		when( propertyBuilder.build( builderContext ) ).thenReturn( mock( TextViewElement.class ) );

		FieldsetFormElement fieldset = assemble( embedded, ViewElementMode.FORM_READ );
		assertNotNull( fieldset );
		verify( codeResolver ).getMessageWithFallback( "properties.embedded[description]", "" );

		NodeViewElement helpBlock = (NodeViewElement) fieldset.getChildren().get( 0 );
		assertNotNull( helpBlock );
		assertEquals( "help text", ( (TextViewElement) helpBlock.getChildren().get( 0 ) ).getText() );
	}

	private static class Instance
	{
		private Object embedded;
	}
}
