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
package com.foreach.across.modules.entity.views.bootstrapui;

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiElements;
import com.foreach.across.modules.bootstrapui.elements.builder.OptionFormElementBuilder;
import com.foreach.across.modules.entity.conditionals.ConditionalOnBootstrapUI;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.ViewElementMode;
import org.springframework.stereotype.Component;

/**
 * Builds a {@link OptionFormElementBuilder} in the form of a bootstrap toggle for boolean attributes.
 *
 * @author Arne Vandamme
 */
@ConditionalOnBootstrapUI
@Component
public class ToggleFormElementBuilderFactory extends CheckboxFormElementBuilderFactory
{
	@Override
	public boolean supports( String viewElementType ) {
		return BootstrapUiElements.TOGGLE.equals( viewElementType );
	}

	@Override
	public OptionFormElementBuilder createInitialBuilder( EntityPropertyDescriptor descriptor, ViewElementMode viewElementMode, String viewElementType ) {
		return super.createInitialBuilder( descriptor, viewElementMode, viewElementType )
		            .toggle();
	}
}
