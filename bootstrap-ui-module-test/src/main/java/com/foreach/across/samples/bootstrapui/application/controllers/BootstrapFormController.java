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

package com.foreach.across.samples.bootstrapui.application.controllers;

import com.foreach.across.modules.bootstrapui.elements.builder.FormViewElementBuilder;
import com.foreach.across.modules.web.events.BuildMenuEvent;
import com.foreach.across.modules.web.ui.ViewElement;
import lombok.Data;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders.*;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Controller
@RequestMapping("/form")
public class BootstrapFormController
{
	@EventListener(condition = "#navMenu.menuName=='navMenu'")
	protected void registerMenuItems( BuildMenuEvent navMenu ) {
		navMenu.builder()
		       .item( "/test/form-elements/form", "Form", "/form" ).order( 1 );
	}

	@GetMapping
	public String renderForm( @ModelAttribute FormDto formDto, Model model ) {
		Map<String, ViewElement> generatedElements = new LinkedHashMap<>();
		generatedElements.put( "Simple form", buildForm( formDto ).build() );
		model.addAttribute( "generatedElements", generatedElements );

		return "th/bootstrapUiTest/elementsRendering";
	}

	@PostMapping
	public String updateForm( @ModelAttribute @Validated FormDto formDto, BindingResult bindingResult, Model model ) {
		return renderForm( formDto, model );
	}

	private FormViewElementBuilder buildForm( FormDto data ) {
		return form()
				.commandObject( data )
				.add(
						formGroup()
								.label( "My number" )
								.required()
								.detectFieldErrors( true )
								.control(
										textbox()
												.controlName( "number" )
												.text( "" + data.getNumber() )
								)
				)
				.add( button().submit().text( "Update" ) );
	}

	@Data
	static class FormDto
	{
		int number;
	}
}
