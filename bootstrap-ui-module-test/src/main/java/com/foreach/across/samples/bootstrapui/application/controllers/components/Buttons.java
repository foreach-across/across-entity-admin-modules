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

import com.foreach.across.modules.bootstrapui.elements.ButtonViewElement;
import com.foreach.across.modules.bootstrapui.elements.Size;
import com.foreach.across.modules.bootstrapui.styles.BootstrapStyles;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import com.foreach.across.samples.bootstrapui.application.controllers.ExampleController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements.bootstrap;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.html;

@Controller
@RequestMapping("/components/buttons")
class Buttons extends ExampleController
{
	@Override
	protected void menuItems( PathBasedMenuBuilder menu ) {
		menu.item( "/components/buttons", "Buttons" );
	}

	@RequestMapping(method = RequestMethod.GET)
	String render() {
		return render(
				panel( "Simple button", simpleButtonElement() ),
				panel( "Button as link with icon", buttonAsLink() ),
				panel( "Large submit button", largeSubmitButton() )
		);
	}

	private ButtonViewElement simpleButtonElement() {
		return bootstrap.builders
				.button()
				.text( "Click me" )
				.build();
	}

	private ButtonViewElement buttonAsLink() {
		return bootstrap.builders
				.button()
				.text( "To google  " )
				.link( "http://www.foreach.be" )
				.iconRight()
				.icon( html.i( BootstrapStyles.css.fa.solid( "arrow-right" ) ) )
				.build();
	}

	private ButtonViewElement largeSubmitButton() {
		return bootstrap.builders
				.button()
				.submit()
				.size( Size.LARGE )
				.text( "Submit" )
				.build();
	}
}
