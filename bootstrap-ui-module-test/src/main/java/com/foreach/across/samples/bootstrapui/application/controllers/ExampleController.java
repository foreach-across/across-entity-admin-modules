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

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuildersBroken;
import com.foreach.across.modules.bootstrapui.resource.BootstrapUiFormElementsWebResources;
import com.foreach.across.modules.bootstrapui.styles.BootstrapStyleRule;
import com.foreach.across.modules.web.events.BuildMenuEvent;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.elements.HtmlViewElements;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementBuilder;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.html;

@Controller
public abstract class ExampleController
{
	@EventListener(condition = "#navMenu.menuName=='navMenu'")
	public void registerMenuItems( BuildMenuEvent navMenu ) {
		menuItems( navMenu.builder() );
		navMenu.builder().item( "/form-controls/textbox", "Textbox" );
	}

	@ModelAttribute
	void registerWebResources( WebResourceRegistry webResourceRegistry ) {
		webResourceRegistry.addPackage( BootstrapUiFormElementsWebResources.NAME );
	}

	protected abstract void menuItems( PathBasedMenuBuilder menu );

	protected NodeViewElementBuilder panel( String title, Object... elementOrBuilders ) {
		return html.builders.div( css.margin.bottom.s3 ).add( html.h4( html.text( title ) ) ).addAll( toViewElements( elementOrBuilders ) );
	}

	protected String render( Object... elementOrBuilders ) {
		RequestContextHolder.currentRequestAttributes().setAttribute(
				"container", html.builders.container().addAll( toViewElements( elementOrBuilders ) ).build(), RequestAttributes.SCOPE_REQUEST
		);

		return "th/bootstrapUiTest/container";
	}

	private List<ViewElement> toViewElements( Object[] elementOrBuilders ) {
		return Stream.of( elementOrBuilders )
		             .map( e -> e instanceof ViewElementBuilder ? ( (ViewElementBuilder) e ).build() : (ViewElement) e )
		             .collect( Collectors.toList() );
	}
}
