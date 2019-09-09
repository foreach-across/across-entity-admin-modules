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

import com.foreach.across.modules.bootstrapui.elements.CheckboxFormElement;
import com.foreach.across.modules.bootstrapui.elements.RadioFormElement;
import com.foreach.across.modules.bootstrapui.elements.ToggleFormElement;
import com.foreach.across.modules.bootstrapui.elements.builder.OptionsFormElementBuilder;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import com.foreach.across.samples.bootstrapui.application.controllers.ExampleController;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.function.Supplier;

import static com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuildersBroken.option;
import static com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuildersBroken.options;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.html;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Controller
@RequestMapping("/form-controls/checkboxAndRadios")
@RequiredArgsConstructor
class CheckboxesAndRadios extends ExampleController
{
	@Override
	protected void menuItems( PathBasedMenuBuilder menu ) {
		menu.item( "/form-controls/checkboxAndRadios", "Checkboxes and radios", "/form-controls/checkboxAndRadios/checkbox" ).and()
		    .item( "/form-controls/checkboxAndRadios/checkbox", "Checkboxes" ).and()
		    .item( "/form-controls/checkboxAndRadios/radio", "Radios" ).and()
		    .item( "/form-controls/checkboxAndRadios/switch", "Switches" );
	}

	@GetMapping("/checkbox")
	String checkboxControls( Model model ) {
		return renderControls( CheckboxFormElement::new, () -> options().checkbox() );
	}

	@GetMapping("/radio")
	String radioControls( Model model ) {
		return renderControls( RadioFormElement::new, () -> options().radio() );
	}

	@GetMapping("/switch")
	String switchControls( Model model ) {
		return renderControls( ToggleFormElement::new, () -> options().toggle() );
	}

	private String renderControls( Supplier<? extends CheckboxFormElement> control, Supplier<OptionsFormElementBuilder> options ) {
		return render(
				panel( "Default custom controls" )
						.add( control.get().setControlName( "control" ).setText( "Simple control text" ) )
						.add( html.hr() )
						.add( options.get()
						             .controlName( "optionsControl" )
						             .add( option().text( "" ).value( 0 ) )
								             .add( option().text( "option 1" ).value( 1 ) )
						             .add( option().text( "option 2" ).value( 2 ).selected() )
						             .add( option().text( "option 3" ).value( 3 ) ) ),
				panel( "Browser native controls" )
						.add( control.get().setRenderAsCustomControl( false ).setControlName( "control" ).setText( "Simple control text" ) ),
				panel( "Unwrapped" )
						.add( control.get().setWrapped( false ).setControlName( "control3" ) )
						.add( html.hr() )
						.add( control.get().setWrapped( false ).setControlName( "control2" ).setText( "Unwrapped with label" ) )

		);
	}
}
