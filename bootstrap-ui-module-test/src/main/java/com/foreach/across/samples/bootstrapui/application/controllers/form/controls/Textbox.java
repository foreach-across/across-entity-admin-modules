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

package com.foreach.across.samples.bootstrapui.application.controllers.form.controls;

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuildersBroken;
import com.foreach.across.modules.bootstrapui.elements.TextboxFormElement;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import com.foreach.across.samples.bootstrapui.application.controllers.ExampleController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Arne Vandamme
 * @since 1.2.0
 */
@Controller
@RequestMapping("/form-controls/textbox")
class Textbox extends ExampleController
{
	@Override
	protected void menuItems( PathBasedMenuBuilder menu ) {
		menu.item( "/form-controls/textbox", "Textbox" );
	}

	@RequestMapping(method = RequestMethod.GET)
	String renderTextboxes() {
		return render(
				panel( "Textbox without auto-size", simpleTextbox() ),
				panel( "Textarea with auto-size", textarea() ),
				panel( "Textbox with auto-size", autoSizingTextbox() )
		);
	}

	private TextboxFormElement simpleTextbox() {
		return BootstrapUiBuildersBroken.textbox()
		                                .placeholder( "Placeholder text..." )
		                                .build();
	}

	private TextboxFormElement textarea() {
		return BootstrapUiBuildersBroken.textarea()
		                                .placeholder( "Placeholder text..." )
		                                .autoSize()
		                                .build();
	}

	private TextboxFormElement autoSizingTextbox() {
		return BootstrapUiBuildersBroken.textbox()
		                                .placeholder( "Placeholder text..." )
		                                .autoSize()
		                                .build();
	}
}
