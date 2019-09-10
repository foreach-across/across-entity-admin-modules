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

import com.foreach.across.modules.bootstrapui.elements.Grid;
import com.foreach.across.modules.bootstrapui.elements.builder.FormViewElementBuilder;
import com.foreach.across.modules.bootstrapui.elements.entry.BootstrapViewElements;
import com.foreach.across.modules.bootstrapui.styles.BootstrapStyles;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import com.foreach.across.samples.bootstrapui.application.controllers.ExampleController;
import lombok.Data;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.foreach.across.modules.bootstrapui.elements.entry.BootstrapViewElements.bootstrap;
import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Controller
@RequestMapping("/components/forms/form")
class Forms extends ExampleController
{
	@Override
	protected void menuItems( PathBasedMenuBuilder menu ) {
		menu.group( "/components/forms", "Forms" ).and()
		    .item( "/components/forms/form", "Form" );
	}

	@GetMapping
	String renderForm( @ModelAttribute FormDto formDto, Model model ) {
		return render( panel( "Simple form", buildForm( formDto ) ) );
	}

	@PostMapping
	public String updateForm( @ModelAttribute @Validated FormDto formDto, BindingResult bindingResult, Model model ) {
		bindingResult.rejectValue( "number", "NaN", "Not really a number" );
		return renderForm( formDto, model );
	}

	protected FormViewElementBuilder buildForm( FormDto data ) {
		return bootstrap.builders.form()
		                         .commandObject( data )
		                         .add(
				                         bootstrap.builders.formGroup()
				                                           .label( "My number" )
				                                           .tooltip( bootstrap.builders.tooltip().text( "another tooltip" ) )
				                                           .required()
				                                           .detectFieldErrors( true )
				                                           .control(
						                                           bootstrap.builders.textbox()
						                                                             .controlName( "number" )
						                                                             .text( "" + data.getNumber() )
				                                           )
		                         )
		                         .add(
				                         bootstrap.builders.formGroup().tooltip( bootstrap.builders.tooltip().text( "hello" ) )
				                                           .control( bootstrap.builders.checkbox().controlName( "checkme" ).label( "Check me out" ) )
		                         )
		                         .add(
				                         bootstrap.builders.formGroup()
				                                           .label( "My text" )
				                                           .descriptionBlock( "My text is a very important field containing... your text!" )
				                                           .helpBlock( "Please fill in all the data" )
				                                           .control( bootstrap.builders.textbox().controlName( "mytext" ) )
		                         )
		                         .add(
				                         bootstrap.builders.formGroup()
				                                           .control( bootstrap.builders.checkbox().controlName( "checkmetoo" ).label( "Try me babe" ) )
				                                           .helpBlock( "Try clicking *on* the checkbox in front of you." )
		                         )
		                         .add( bootstrap.builders.row().add( bootstrap.builders.column( Grid.Device.MD.width( 12 ) )
		                                                                               .add( bootstrap.builders.button( css.button.primary ).submit()
		                                                                                                       .text( "Update" ) ) ) );
	}

	@Data
	static class FormDto
	{
		int number;
	}
}
