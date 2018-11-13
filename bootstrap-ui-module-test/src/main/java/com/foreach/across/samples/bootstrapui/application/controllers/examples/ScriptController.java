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

package com.foreach.across.samples.bootstrapui.application.controllers.examples;

import com.foreach.across.modules.bootstrapui.elements.ScriptViewElement;
import com.foreach.across.modules.bootstrapui.elements.Style;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders.*;

/**
 * @author Arne Vandamme
 * @since 2.1.1
 */
@Controller
@RequestMapping("/examples/scripts")
public class ScriptController
{
	@GetMapping
	public String todoForm( Model model ) {
		Map<String, ViewElement> generatedElements = new LinkedHashMap<>();
		generatedElements.put( "Simple script", simpleJavascript() );
		generatedElements.put( "Simple HTML template", simpleHtmlTemplate() );
		generatedElements.put( "Nested HTML template", nestedHtmlTemplate() );
		generatedElements.put( "Double nested HTML template", doubleNestedTemplate() );
		model.addAttribute( "generatedElements", generatedElements );

		return "th/bootstrapUiTest/elementsRendering";
	}

	private ViewElement simpleJavascript() {
		ScriptViewElement script = new ScriptViewElement();
		script.setAttribute( "type", "text/javascript" );
		script.addChild( TextViewElement.html( "document.write('hello from an injected script');" ) );

		return script;
	}

	private ViewElement doubleNestedTemplate() {
		return div()
				.add(
						script( MediaType.TEXT_HTML )
								.data( "id", "double-nested" )
								.add( nestedHtmlTemplate() )
								.add( nestedHtmlTemplate() )
				)
				.add( button().attribute( "onclick",
				                          "$(this.parentNode).append( $(BootstrapUiModule.refTarget( $('[data-id=double-nested]', this.parentNode)).html() ) );" )
				              .style( Style.DANGER )
				              .text( "Add nested template" ) )
				.add( paragraph().add( text( "The nested template will be added below." ) ) )
				.build();
	}

	private ViewElement nestedHtmlTemplate() {
		ScriptViewElement script = new ScriptViewElement();
		script.setAttribute( "type", "text/html" );
		script.setAttribute( "data-id", "nested" );
		script.addChild( simpleHtmlTemplate() );

		return div()
				.add( script )
				.add( button().attribute( "onclick",
				                          "$(this.parentNode).append( $(BootstrapUiModule.refTarget( $('[data-id=nested]', this.parentNode)).html() ) );" )
				              .style( Style.WARNING )
				              .text( "Add simple template" ) )
				.add( paragraph().add( text( "The simple template will be added below." ) ) )
				.build();
	}

	private ViewElement simpleHtmlTemplate() {
		ScriptViewElement script = new ScriptViewElement();
		script.setAttribute( "type", "text/html" );
		script.setAttribute( "data-id", "simple" );
		script.addChild( paragraph().add( text( "hello from html template" ) ).build() );

		return div()
				.add( script )
				.add( button().attribute( "onclick",
				                          "$(this.parentNode).append( $( BootstrapUiModule.refTarget($('[data-id=simple]', this.parentNode)).html() ) );" )
				              .style( Style.PRIMARY )
				              .text( "Show template body" ) )
				.build();
	}
}
