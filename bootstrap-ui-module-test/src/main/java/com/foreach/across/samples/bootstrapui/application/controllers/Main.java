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
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;

@Controller
class Main
{
	@EventListener(condition = "#navMenu.menuName=='navMenu'")
	public void registerMenuItems( BuildMenuEvent navMenu ) {
		navMenu.builder()
		       .group( "/components", "Components" ).and()
		       .group( "/form-controls", "Form controls" ).and()
		       .group( "/utilities", "Utilities" );
	}

	@GetMapping("/")
	public String homepage( Model model ) {
		model.addAttribute( "generatedElements", Collections.emptyList() );
		return "th/bootstrapUiTest/elementsRendering";
	}
}
