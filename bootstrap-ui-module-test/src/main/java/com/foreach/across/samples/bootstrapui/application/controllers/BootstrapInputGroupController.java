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

import com.foreach.across.modules.bootstrapui.elements.*;
import com.foreach.across.modules.bootstrapui.elements.builder.InputGroupFormElementBuilderSupport;
import com.foreach.across.modules.bootstrapui.resource.BootstrapUiFormElementsWebResources;
import com.foreach.across.modules.web.events.BuildMenuEvent;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.HtmlViewElements;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.foreach.across.modules.bootstrapui.attributes.BootstrapAttributes.attribute;
import static com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders.*;
import static com.foreach.across.modules.bootstrapui.elements.InputGroupFormElement.*;
import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.i;

@Controller
@RequestMapping("/input-group")
public class BootstrapInputGroupController
{
	@EventListener(condition = "#navMenu.menuName=='navMenu'")
	public void registerMenuItems( BuildMenuEvent navMenu ) {
		navMenu.builder()
		       .item( "/test/form-elements/input-group", "Input group", "/input-group" ).order( 24 );
	}

	@RequestMapping(method = RequestMethod.GET)
	public String renderIcon( Model model, ViewElementBuilderContext builderContext, WebResourceRegistry webResourceRegistry ) {
		webResourceRegistry.addPackage( BootstrapUiFormElementsWebResources.NAME );

		Map<String, ViewElement> generatedElements = new LinkedHashMap<>();
		generatedElements.put( "Simple inputGroup", simpleInputGroup() );
		generatedElements.put( "Checkboxes and radios", checkboxesAndRadios() );
		generatedElements.put( "Multiple inputs", multipleInputs() );
		generatedElements.put( "Multiple addons", multipleAddons() );
		generatedElements.put( "Button addons", buttonAddons() );

		model.addAttribute( "generatedElements", generatedElements );

		return "th/bootstrapUiTest/elementsRendering";
	}

	private ViewElement checkboxesAndRadios() {
		return container()
				.add( inputGroup().prepend( div( css.inputGroup.text ).add( checkbox().unwrapped() ) ) )
				.add( inputGroup().append( div( css.inputGroup.text ).add( radio().unwrapped() ) ) )
				.build();
	}

	private ViewElement multipleInputs() {
		return container()
				.add( inputGroup().prepend( text( "First name and last name" ) ).add( textbox().with( attribute.aria.label( "Last name" ) ) ) )
				.build();
	}

	private ViewElement multipleAddons() {
		return container()
				.add(
						inputGroup()
								.prepend( container().add( span( css.inputGroup.text ).add( text( "$" ) ) )
								                     .add( span( css.inputGroup.text ).add( text( "0.00" ) ) ) )
				)
				.add(
						inputGroup()
								.append( container().add( span( css.inputGroup.text ).add( checkbox().unwrapped() ) )
								                    .add( span( css.inputGroup.text ).add( text( "enable filter" ) ) ) )
				)
				.build();
	}

	private ViewElement buttonAddons() {
		return container()
				.add( inputGroup().prepend( button().style( Style.PRIMARY ).text( "Click me" ) ) )
				.add( inputGroup().append(
						container().add( button().style( Style.DANGER ).iconOnly( i( css.fa.solid( "trash" ) ) ).text( "Delete" ) )
						           .add( button().style( Style.PRIMARY ).iconOnly( i( css.fa.solid( "save" ) ) ).text( "Save" ) )
				      )
				)
				.add(
						inputGroup().prepend(
								container().add( button().style( Style.INFO ).with( css.dropdown.toggle, attribute.data.toggle.dropdown ).text( "Open" ) )
								           .add( div( css.dropdown.menu ).add( link().with( css.dropdown.item ).text( "A dropdown item" ) ) )

						)
				)
				.add(
						inputGroup().append(
								container().add( button().style( Style.DANGER ).icon( i( css.fa.solid( "save" ) ) ).text( " Save" ) )
								           .add( button().style( Style.WARNING ).with( css.dropdown.toggle, attribute.data.toggle.dropdown ) )
								           .add( div( css.dropdown.menu ).add( link().with( css.dropdown.item ).text( "A dropdown item" ) ) )
						)
				)
				.build();
	}

	private ViewElement simpleInputGroup() {
		return HtmlViewElements.container(
				inputGroup()
						.control( textbox().placeholder( "Username" ).type( TextboxFormElement.Type.EMAIL ) )
						.prepend( text( "@" ) ).build(),
				BootstrapUiElements.inputGroup(
						css.margin.bottom.s3,
						control( textbox().placeholder( "Email prefix" ).type( TextboxFormElement.Type.TEXT ).build() ),
						append( HtmlViewElements.text( "@example.com" ) )
				),
				BootstrapUiElements.inputGroup( css.margin.bottom.s3, prepend( HtmlViewElements.text( "$" ) ), append( HtmlViewElements.text( ".00" ) ) ),
				BootstrapUiElements
						.inputGroup( css.inputGroup.small, css.margin.bottom.s3,
						             prepend( HtmlViewElements.text( "With textarea" ) ), control( new TextareaFormElement() ) )
		);
	}

	private InputGroupFormElementBuilderSupport inputGroup() {
		return BootstrapUiBuilders.inputGroup().with( css.margin.bottom.s3 );
	}

}
