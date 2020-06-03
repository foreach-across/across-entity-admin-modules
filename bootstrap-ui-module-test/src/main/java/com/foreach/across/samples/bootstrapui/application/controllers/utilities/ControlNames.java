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

package com.foreach.across.samples.bootstrapui.application.controllers.utilities;

import com.foreach.across.modules.bootstrapui.elements.builder.FormViewElementBuilder;
import com.foreach.across.modules.bootstrapui.utils.BootstrapElementUtils;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import com.foreach.across.modules.web.ui.elements.builder.ContainerViewElementBuilder;
import com.foreach.across.samples.bootstrapui.application.controllers.ExampleController;
import lombok.Data;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements.bootstrap;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.html;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Controller
@RequestMapping("/utilities/control-names")
class ControlNames extends ExampleController
{
	@Override
	protected void menuItems( PathBasedMenuBuilder menu ) {
		menu.item( "/utilities/control-names", "Control names" );
	}

	@Data
	static class Todo
	{
		private String owner;
		private String description;
	}

	@GetMapping
	String render( Model model ) {
		FormViewElementBuilder form = bootstrap.builders.form();

		for ( int i = 0; i < 3; i++ ) {
			Todo todo = new Todo();
			form.add( createTodoForm( todo ).postProcessor( BootstrapElementUtils.prefixControlNames( "todoList[" + i + "]" ) ) );
		}

		return render(
				panel( "Non-prefixed control names", createTodoForm( new Todo() ) ),
				panel( "Prefixed control names form", form )
		);
	}

	private ContainerViewElementBuilder createTodoForm( Todo item ) {
		return html.builders.container()
		                    .add(
				                    bootstrap.builders
						                    .formGroup()
						                    .label( bootstrap.builders.label( "Owner" ) )
						                    .control( bootstrap.builders.textbox().controlName( "owner" ).text( item.getOwner() ) )
		                    )
		                    .add(
				                    bootstrap.builders
						                    .formGroup()
						                    .label( bootstrap.builders.label( "Description" ) )
						                    .control( bootstrap.builders.textbox().controlName( "description" ).text( item.getDescription() ) )
		                    );
	}
}
