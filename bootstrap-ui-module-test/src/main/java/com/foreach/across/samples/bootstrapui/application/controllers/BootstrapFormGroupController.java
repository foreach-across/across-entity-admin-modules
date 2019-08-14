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

package com.foreach.across.samples.bootstrapui.application.controllers;

import com.foreach.across.modules.web.events.BuildMenuEvent;
import com.foreach.across.modules.web.ui.ViewElement;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders.*;
import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Controller
@RequestMapping("/formGroup")
public class BootstrapFormGroupController
{
	@EventListener(condition = "#navMenu.menuName=='navMenu'")
	public void registerMenuItems( BuildMenuEvent navMenu ) {
		navMenu.builder()
		       .item( "/test/form-elements/formGroup", "Form group", "/formGroup" ).order( 1 );
	}

	@GetMapping
	public String renderFormGroup( Model model ) {
		Map<String, ViewElement> generatedElements = new LinkedHashMap<>();
		generatedElements.put( "Simple form group", simpleFormGroup() );
		generatedElements.put( "Checkbox and radios", checkboxAndRadios() );
		generatedElements.put( "Validation", validation() );
		model.addAttribute( "generatedElements", generatedElements );

		return "th/bootstrapUiTest/elementsRendering";
	}

	private ViewElement simpleFormGroup() {
		return container()
				.add( formGroup().label( "Control label" ).control( textbox().controlName( "control" ) ) )
				.add(
						formGroup().label( "Control label" )
						           .required()
						           .tooltip( "Control tooltip" )
						           .descriptionBlock( "This is the control description." )
						           .control( textarea().controlName( "control" ) )
						           .helpBlock( "Control help text." )
				)
				.build();
	}

	private ViewElement checkboxAndRadios() {
		return container()
				.add( formGroup().control( checkbox().controlName( "control" ).label( "This is a checkbox" ) ) )
				.add(
						formGroup()
								.label( "Radio options" )
								.control(
										options()
												.radio()
												.controlName( "radio" )
												.add( option().text( "Option 1" ) )
												.add( option().text( "Option 2" ) )
								)
				)
				.build();
	}

	private ViewElement validation() {
		return container()
				.add( formGroup().with( css.invalid ).label( "Control label" ).control( textbox().controlName( "control" ) ) )
				.build();
	}

}
