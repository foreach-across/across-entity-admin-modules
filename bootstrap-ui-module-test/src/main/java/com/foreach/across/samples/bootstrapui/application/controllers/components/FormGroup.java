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

import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.samples.bootstrapui.application.controllers.ExampleController;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;
import java.util.Map;

import static com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements.bootstrap;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.html;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Controller
@RequestMapping("/components/forms/formGroup")
class FormGroup extends ExampleController
{
	@Override
	protected void menuItems( PathBasedMenuBuilder menu ) {
		menu.item( "/components/forms/formGroup", "Form group" );
	}

	@GetMapping
	String renderFormGroup( Model model ) {
		return render(
				panel( "Simple form group", simpleFormGroup() ),
				panel( "Checkbox and radios", checkboxAndRadios() ),
				panel( "Validation", validation( model ) )
		);
	}

	private ViewElement simpleFormGroup() {
		return html.builders.container()
		                    .add( bootstrap.builders.formGroup().label( "Control label" ).control( bootstrap.builders.textbox().controlName( "control" ) ) )
		                    .add(
				                    bootstrap.builders.formGroup().label( "Control label" )
				                                      .required()
				                                      .tooltip( "Control tooltip" )
				                                      .descriptionBlock( "This is the control description." )
				                                      .control( bootstrap.builders.textbox().multiLine().controlName( "control" ) )
				                                      .helpBlock( "Control help text." )
		                    )
		                    .build();
	}

	private ViewElement checkboxAndRadios() {
		return html.builders.container()
		                    .add( bootstrap.builders.formGroup()
		                                            .control( bootstrap.builders.checkbox().controlName( "control" ).label( "This is a checkbox" ) ) )
		                    .add(
				                    bootstrap.builders.formGroup()
				                                      .label( "Radio options" )
				                                      .control(
						                                      bootstrap.builders.option.options()
						                                                               .radio()
						                                                               .controlName( "radio" )
						                                                               .add( bootstrap.builders.option.option().text( "Option 1" ) )
						                                                               .add( bootstrap.builders.option.option().text( "Option 2" ) )
				                                      )
		                    )
		                    .build();
	}

	private ViewElement validation( Model model ) {
		TestClass target = new TestClass( "test value" );
		BindingResult errors = new BeanPropertyBindingResult( target, "item" );
		errors.rejectValue( "control", "broken", "broken" );
		errors.rejectValue( "values[sub.item].name", "map-broken", "map-broken" );

		model.addAttribute( BindingResult.MODEL_KEY_PREFIX + "item", errors );
		model.addAttribute( "item", target );

		return bootstrap.builders.form().commandObject( target )
		                         .add( bootstrap.builders.formGroup().label( "Texbox" ).control( bootstrap.builders.textbox().controlName( "control" ) ) )
		                         .add( bootstrap.builders.formGroup().control( bootstrap.builders.checkbox().controlName( "control" ).label( "Checkbox" ) ) )
		                         //.postProcessor( (builderContext, formGroup) -> formGroup.setDetectFieldErrors( tr ) ))
		                         .build();
	}

	@Getter
	@Setter
	public static class TestClass
	{
		private final Map<String, Object> values = Collections.singletonMap( "sub.item", new NamedItem() );
		private String control;

		TestClass( String control ) {
			this.control = control;
		}

		@Getter
		@Setter
		static class NamedItem
		{
			String name;
		}
	}
}
