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

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders;
import com.foreach.across.modules.bootstrapui.elements.builder.FormViewElementBuilder;
import com.foreach.across.modules.bootstrapui.utils.BootstrapElementUtils;
import com.foreach.across.modules.web.events.BuildMenuEvent;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.builder.ContainerViewElementBuilder;
import lombok.Data;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders.*;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Controller
@RequestMapping("/examples/todo")
public class ExamplesController
{
	@EventListener(condition = "#navMenu.menuName=='navMenu'")
	protected void registerMenuItems( BuildMenuEvent navMenu ) {
		navMenu.builder()
		       .group( "/examples", "Examples" ).and()
		       .item( "/examples/todo", "Control names", "/examples/todo" ).order( 1 ).and()
		       .item( "/examples/scripts", "Scripts", "/examples/scripts" ).order( 2 );
	}

	@Data
	static class Todo
	{
		private String owner;
		private String description;
	}

	@GetMapping
	public String todoForm( Model model ) {
		FormViewElementBuilder form = BootstrapUiBuilders.form();

		for ( int i = 0; i < 3; i++ ) {
			Todo todo = new Todo();
			form.add( createTodoForm( todo ).postProcessor( BootstrapElementUtils.prefixControlNames( "todoList[" + i + "]" ) ) );
		}

		Map<String, ViewElement> generatedElements = new LinkedHashMap<>();
		generatedElements.put( "Non-prefixed control names", createTodoForm( new Todo() ).build() );
		generatedElements.put( "Prefixed control names form", form.build() );
		model.addAttribute( "generatedElements", generatedElements );

		return "th/bootstrapUiTest/elementsRendering";
	}

	private ContainerViewElementBuilder createTodoForm( Todo item ) {
		return container()
				.add(
						formGroup(
								label( "Owner" ),
								textbox().controlName( "owner" ).text( item.getOwner() )
						)
				)
				.add(
						formGroup(
								label( "Description" ),
								textbox().controlName( "description" ).text( item.getDescription() )
						)
				);
	}
}
