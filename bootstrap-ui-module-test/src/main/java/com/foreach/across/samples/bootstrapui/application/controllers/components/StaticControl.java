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

import com.foreach.across.modules.bootstrapui.elements.StaticFormElement;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import com.foreach.across.samples.bootstrapui.application.controllers.ExampleController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/components/forms/static")
@Deprecated
public class StaticControl extends ExampleController
{
	@Override
	protected void menuItems( PathBasedMenuBuilder menu ) {
		menu.item( "/components/forms/static", "Static form element" );
	}

	@RequestMapping(method = RequestMethod.GET)
	String render() {
		return render( panel( "Static form element", simpleStaticFormElement() ) );
	}

	private StaticFormElement simpleStaticFormElement() {
		StaticFormElement staticFormElement = new StaticFormElement();
		staticFormElement.setText( "Static text" );

		return staticFormElement;
	}

}
