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

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.bootstrapui.elements.ViewElementFieldset;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
@ExtendWith(MockitoExtension.class)
public class TestFieldsetHelpTextPostProcessor
{
	protected static final String CSS_FORM_TEXT_HELP = "form-text-help";

	@Mock
	private EntityPropertyDescriptor descriptor;

	@Spy
	private DefaultViewElementBuilderContext builderContext = new DefaultViewElementBuilderContext();

	private ViewElementFieldset fieldset = new ViewElementFieldset();
	private FieldsetHelpTextPostProcessor<ViewElement> postProcessor = new FieldsetHelpTextPostProcessor<>();

	@BeforeEach
	public void before() {
		when( descriptor.getName() ).thenReturn( "myprop" );
		builderContext.setAttribute( EntityPropertyDescriptor.class, descriptor );
	}

	@Test
	public void textNotSetIfEmpty() {
		when( builderContext.getMessage( "properties.myprop[help]", "" ) ).thenReturn( "" );
		postProcessor.postProcess( builderContext, fieldset );
		assertThat( fieldset.getFooter().getChildren() ).isEmpty();
	}

	@Test
	public void resolvedMessageSupportsHtmlByDefault() {
		fieldset.addChild( TextViewElement.text( "body" ) );
		when( builderContext.getMessage( "properties.myprop[help]", "" ) ).thenReturn( "description" );
		postProcessor.postProcess( builderContext, fieldset );
		assertThat( fieldset.getFooter().getChildren() ).hasSize( 1 );
		assertThat( (NodeViewElement) fieldset.getFooter().getChildren().get( 0 ) )
				.satisfies( node -> {
					Assertions.assertThat( node.getAttribute( "class", String.class ).split( " " ) )
					          .containsAll( Arrays.asList( css.form.text.toCssClasses()));
					TextViewElement text = node.findAll( TextViewElement.class )
					                           .findFirst()
					                           .orElseThrow( AssertionError::new );
					assertThat( text.getText() ).isEqualTo( "description" );
					assertThat( text.isEscapeXml() ).isFalse();
				} );
	}

	@Test
	public void resolvedMessageIsEscapedIfConfigured() {
		fieldset.addChild( TextViewElement.text( "body" ) );
		postProcessor = new FieldsetHelpTextPostProcessor<>( true );
		when( builderContext.getMessage( "properties.myprop[help]", "" ) ).thenReturn( "description" );
		postProcessor.postProcess( builderContext, fieldset );
		assertThat( fieldset.getFooter().getChildren() ).hasSize( 1 );
		assertThat( (NodeViewElement) fieldset.getFooter().getChildren().get( 0 ) )
				.satisfies( node -> {
					Assertions.assertThat( node.getAttribute( "class", String.class ).split( " " ) )
					          .containsAll( Arrays.asList( css.form.text.toCssClasses()));
					TextViewElement text = node.findAll( TextViewElement.class )
					                           .findFirst()
					                           .orElseThrow( AssertionError::new );
					assertThat( text.getText() ).isEqualTo( "description" );
					assertThat( text.isEscapeXml() ).isTrue();
				} );
	}
}
