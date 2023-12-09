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

package com.foreach.across.modules.entity.views.bootstrapui.elements.builder;

import com.foreach.across.modules.bootstrapui.elements.TextboxFormElement;
import com.foreach.across.modules.bootstrapui.elements.builder.TextboxFormElementBuilder;
import com.foreach.across.modules.entity.web.EntityModuleWebResources;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;

/**
 * Helper builder that renders a textbox as a multi-value control.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public final class MultiValueControlViewElementBuilder extends TextboxFormElementBuilder
{
	public MultiValueControlViewElementBuilder() {
		customTemplate( "th/entity/elements :: multi-value-control" );
	}

	@Override
	protected TextboxFormElement createElement( ViewElementBuilderContext builderContext ) {
		return super.createElement( builderContext );
	}

	@Override
	protected void registerWebResources( WebResourceRegistry webResourceRegistry ) {
		super.registerWebResources( webResourceRegistry );

		webResourceRegistry.addPackage( EntityModuleWebResources.NAME );
	}
}
