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

import com.foreach.across.modules.bootstrapui.elements.AlertViewElement;
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuildersBroken;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.samples.bootstrapui.application.controllers.ExampleController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/components/alerts")
public class Alerts extends ExampleController
{
	@Override
	protected void menuItems( PathBasedMenuBuilder menu ) {
		menu.item( "/components/alerts", "Alerts" );
	}

	@RequestMapping(method = RequestMethod.GET)
	String renderIcon( WebResourceRegistry webResourceRegistry ) {
		return render(
				panel( "Simple alert", simpleAlertElement() ),
				panel( "Full option alert", fullOptionAlertElement() )
		);
	}

	private AlertViewElement simpleAlertElement() {
		return BootstrapUiBuildersBroken
				.alert()
				.danger()
				.text( "This is a danger alert—check it out!\n" )
				.build();
	}

	private AlertViewElement fullOptionAlertElement() {
		return BootstrapUiBuildersBroken
				.alert()
				.closeLabel( "Close alert" )
				.dismissible( true )
				.success()
				.text( "This is a success alert—check it out!\n" )
				.build();
	}
}
