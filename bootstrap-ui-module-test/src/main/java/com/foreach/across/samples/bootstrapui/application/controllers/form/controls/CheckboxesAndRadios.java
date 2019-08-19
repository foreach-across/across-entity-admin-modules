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
import com.foreach.across.modules.bootstrapui.elements.builder.OptionsFormElementBuilder;
import com.foreach.across.modules.web.events.BuildMenuEvent;
import com.foreach.across.modules.web.ui.elements.HtmlViewElements;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.function.Supplier;

import static com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders.*;
import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.h4;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.hr;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Controller
@RequestMapping("/formControls/checkboxAndRadios")
@RequiredArgsConstructor
class CheckboxesAndRadios
{
	@EventListener(condition = "#navMenu.menuName=='navMenu'")
	public void registerMenuItems( BuildMenuEvent navMenu ) {
		navMenu.builder()
		       .item( "/test/form-controls/checkboxAndRadios", "Checkboxes and radios", "/formControls/checkboxAndRadios/checkbox" ).and()
		       .item( "/test/form-controls/checkboxAndRadios/checkbox", "Checkboxes", "/formControls/checkboxAndRadios/checkbox" ).and()
		       .item( "/test/form-controls/checkboxAndRadios/radio", "Radios", "/formControls/checkboxAndRadios/radio" ).and()
		       .item( "/test/form-controls/checkboxAndRadios/switch", "Switches", "/formControls/checkboxAndRadios/switch" );
	}

	@GetMapping("/checkbox")
	String checkboxControls( Model model ) {
		return render( CheckboxFormElement::new, () -> options().checkbox(), model );
	}

	@GetMapping("/radio")
	String radioControls( Model model ) {
		return render( RadioFormElement::new, () -> options().radio(), model );
	}

	private String render( Supplier<? extends CheckboxFormElement> control, Supplier<OptionsFormElementBuilder> options, Model model ) {
		model.addAttribute( "container", container()
				.add(
						div( css.margin.bottom.s3 )
								.add( h4( HtmlViewElements.text( "Default custom controls" ) ) )
								.add( control.get().setControlName( "control" ).setText( "Simple control text" ) )
								.add( hr() )
								.add( options.get()
								             .controlName( "optionsControl" )
								             .add( option().text( "option 1" ).value( 1 ) )
								             .add( option().text( "option 2" ).value( 2 ).selected() )
								             .add( option().text( "option 3" ).value( 3 ) ) )
				)
				.add(
						div( css.margin.bottom.s3 )
								.add( h4( HtmlViewElements.text( "Browser native controls" ) ) )
								.add( control.get().setRenderAsCustomControl( false ).setControlName( "control" ).setText( "Simple control text" ) )
				)
				.add(
						div( css.margin.bottom.s3 )
								.add( h4( HtmlViewElements.text( "Unwrapped" ) ) )
								.add( control.get().setWrapped( false ).setControlName( "control3" ) )
								.add( hr() )
								.add( control.get().setWrapped( false ).setControlName( "control2" ).setText( "Unwrapped with label" ) )
				)
				.build()
		);

		return "th/bootstrapUiTest/container";
	}
}
