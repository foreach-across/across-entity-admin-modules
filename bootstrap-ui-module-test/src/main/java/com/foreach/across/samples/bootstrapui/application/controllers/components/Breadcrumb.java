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

import com.foreach.across.modules.bootstrapui.components.builder.NavComponentBuilder;
import com.foreach.across.modules.bootstrapui.styles.BootstrapStyles;
import com.foreach.across.modules.web.menu.Menu;
import com.foreach.across.modules.web.menu.MenuSelector;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.samples.bootstrapui.application.controllers.ExampleController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements.bootstrap;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.html;

@Controller
@RequestMapping("/components/breadcrumb")
class Breadcrumb extends ExampleController
{
	@Override
	protected void menuItems( PathBasedMenuBuilder menu ) {
		menu.item( "/components/breadcrumb", "Breadcrumb" );
	}

	@RequestMapping(method = RequestMethod.GET)
	String render() {
		return render(
				panel( "Simple breadcrumb nav", simpleBreadCrumbNav() ),
				panel( "Breadcrumb nav with icons", breadcrumbWithIcons() ) );
	}

	private NodeViewElement simpleBreadCrumbNav() {
		Menu menu = new PathBasedMenuBuilder()
				.item( "components", "Components" ).and()
				.item( "components/breadcrumb", "Breadcrumb example" ).and().build();

		menu.select( MenuSelector.byTitle( "Breadcrumb example" ) );
		menu.setTitle( "Bootstrap Ui Module" );

		return bootstrap.builders.breadcrumb(  ).menu( menu ).build();
	}

	private NodeViewElement breadcrumbWithIcons() {
		Menu menu = new PathBasedMenuBuilder()
				.item( "components", "Components" )
				.attribute( NavComponentBuilder.ATTR_ICON, html.i( BootstrapStyles.css.fa.solid( "home" ) ) )
				.attribute( NavComponentBuilder.ATTR_ICON_ONLY, true ).and()
				.item( "components/breadcrumb", "Breadcrumb example" )
				.attribute( NavComponentBuilder.ATTR_ICON, html.i( BootstrapStyles.css.fa.solid( "hourglass" ) ) )
				.and().build();

		menu.select( MenuSelector.byTitle( "Breadcrumb example" ) );
		menu.setTitle( "Bootstrap Ui Module" );

		return bootstrap.builders.breadcrumb(  ).menu( menu ).build();
	}

}
