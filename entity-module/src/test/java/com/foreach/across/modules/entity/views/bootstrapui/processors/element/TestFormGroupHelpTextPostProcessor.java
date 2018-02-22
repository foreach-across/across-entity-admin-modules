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

package com.foreach.across.modules.entity.views.bootstrapui.processors.element;

import com.foreach.across.modules.bootstrapui.elements.FormGroupElement;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static com.foreach.across.modules.bootstrapui.elements.builder.FormGroupElementBuilder.CSS_FORM_TEXT_HELP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestFormGroupHelpTextPostProcessor
{
	@Mock
	private EntityPropertyDescriptor descriptor;

	@Spy
	private DefaultViewElementBuilderContext builderContext = new DefaultViewElementBuilderContext();

	@Spy
	private FormGroupElement group = new FormGroupElement();

	private FormGroupHelpTextPostProcessor<ViewElement> postProcessor = new FormGroupHelpTextPostProcessor<>();

	@Before
	public void before() {
		when( descriptor.getName() ).thenReturn( "myprop" );
		builderContext.setAttribute( EntityPropertyDescriptor.class, descriptor );
	}

	@Test
	public void textNotSetIfAlreadyPresent() {
		TextViewElement present = TextViewElement.text( "hello" );
		group.setHelpBlock( present );
		postProcessor.postProcess( builderContext, group );
		assertThat( group.getHelpBlock() ).isEqualTo( present );
		verify( builderContext, never() ).getMessage( any(), any( String.class ) );
	}

	@Test
	public void textNotSetIfEmpty() {
		when( builderContext.getMessage( "properties.myprop[help]", "" ) ).thenReturn( "" );
		postProcessor.postProcess( builderContext, group );
		assertThat( group.getHelpBlock() ).isNull();
	}

	@Test
	public void resolvedMessageSupportsHtmlByDefault() {
		when( builderContext.getMessage( "properties.myprop[help]", "" ) ).thenReturn( "description" );
		postProcessor.postProcess( builderContext, group );
		assertThat( group.getHelpBlock( NodeViewElement.class ) )
				.satisfies( node -> {
					assertThat( node.hasCssClass( CSS_FORM_TEXT_HELP ) ).isTrue();
					TextViewElement text = node.findAll( TextViewElement.class )
					                           .findFirst()
					                           .orElseThrow( AssertionError::new );
					assertThat( text.getText() ).isEqualTo( "description" );
					assertThat( text.isEscapeXml() ).isFalse();
				} );
	}

	@Test
	public void resolvedMessageIsEscapedIfConfigured() {
		postProcessor = new FormGroupHelpTextPostProcessor<>( true );
		when( builderContext.getMessage( "properties.myprop[help]", "" ) ).thenReturn( "description" );
		postProcessor.postProcess( builderContext, group );
		assertThat( group.getHelpBlock( NodeViewElement.class ) )
				.satisfies( node -> {
					TextViewElement text = node.findAll( TextViewElement.class )
					                           .findFirst()
					                           .orElseThrow( AssertionError::new );
					assertThat( text.getText() ).isEqualTo( "description" );
					assertThat( text.isEscapeXml() ).isTrue();
				} );
	}
}
