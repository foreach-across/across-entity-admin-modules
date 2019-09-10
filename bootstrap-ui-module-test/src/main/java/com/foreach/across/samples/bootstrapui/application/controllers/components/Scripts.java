/*
 * Copyright 2019 the original author or authors
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

package com.foreach.across.samples.bootstrapui.application.controllers.components;

import com.foreach.across.modules.bootstrapui.elements.ScriptViewElement;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import com.foreach.across.samples.bootstrapui.application.controllers.ExampleController;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements.bootstrap;
import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.html;
import static com.foreach.across.modules.web.ui.elements.TextViewElement.text;

/**
 * @author Arne Vandamme
 * @since 2.1.1
 */
@Controller
@RequestMapping("/components/scripts")
class Scripts extends ExampleController
{
	@Override
	protected void menuItems( PathBasedMenuBuilder menu ) {
		menu.item( "/components/scripts", "Scripts" );
	}

	@GetMapping
	String render() {
		return render(
				panel( "Simple script", simpleJavascript() ),
				panel( "Simple HTML template", simpleHtmlTemplate() ),
				panel( "Nested HTML template", nestedHtmlTemplate() ),
				panel( "Double nested HTML template", doubleNestedTemplate() )
		);
	}

	private ViewElement simpleJavascript() {
		ScriptViewElement script = new ScriptViewElement();
		script.setAttribute( "type", "text/javascript" );
		script.addChild( TextViewElement.html( "document.write('hello from an injected script');" ) );

		return script;
	}

	private ViewElement doubleNestedTemplate() {
		return html.builders.div()
		                    .add(
				                    bootstrap.builders.script()
				                                      .type( MediaType.TEXT_HTML )
				                                      .data( "id", "double-nested" )
				                                      .add( nestedHtmlTemplate() )
				                                      .add( nestedHtmlTemplate() )
		                    )
		                    .add( bootstrap.builders.button( css.button.danger ).attribute( "onclick",
		                                                                                    "$(this.parentNode).append( $(BootstrapUiModule.refTarget( $('[data-id=double-nested]', this.parentNode)).html() ) );" )
		                                            .text( "Add nested template" ) )
		                    .add( html.builders.p().add( text( "The nested template will be added below." ) ) )
		                    .build();
	}

	private ViewElement nestedHtmlTemplate() {
		ScriptViewElement script = new ScriptViewElement();
		script.setAttribute( "type", "text/html" );
		script.setAttribute( "data-id", "nested" );
		script.addChild( simpleHtmlTemplate() );

		return html.builders.div()
				.add( script )
				.add( bootstrap.builders.button(css.button.warning).attribute( "onclick",
				                          "$(this.parentNode).append( $(BootstrapUiModule.refTarget( $('[data-id=nested]', this.parentNode)).html() ) );" )
				              .text( "Add simple template" ) )
				.add( html.builders.p().add( text( "The simple template will be added below." ) ) )
				.build();
	}

	private ViewElement simpleHtmlTemplate() {
		ScriptViewElement script = new ScriptViewElement();
		script.setAttribute( "type", "text/html" );
		script.setAttribute( "data-id", "simple" );
		script.addChild( html.builders.p().add( text( "hello from html template" ) ).build() );

		return html.builders.div()
				.add( script )
				.add( bootstrap.builders.button(css.button.primary).attribute( "onclick",
				                          "$(this.parentNode).append( $( BootstrapUiModule.refTarget($('[data-id=simple]', this.parentNode)).html() ) );" )
				              .text( "Show template body" ) )
				.build();
	}
}
