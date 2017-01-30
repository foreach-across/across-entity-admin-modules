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

package com.foreach.across.modules.bootstrapui.elements.builder;

import com.foreach.across.modules.bootstrapui.elements.AutosuggestFormElementConfiguration;
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiFactory;
import com.foreach.across.modules.bootstrapui.resource.BootstrapUiFormElementsWebResources;
import com.foreach.across.modules.bootstrapui.resource.JQueryWebResources;
import com.foreach.across.modules.web.resource.WebResource;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AutoSuggestFormElementBuilder extends AbstractLinkSupportingNodeViewElementBuilder<NodeViewElement, AutoSuggestFormElementBuilder>
{
	public static final String TYPEAHEAD_CLASS = "js-typeahead";
	public static final String TYPEAHEAD_INPUT_CLASS = "js-typeahead-input";
	public static final String ATTRIBUTE_DATA_AUTOSUGGEST = "data-autosuggest";

	private final BootstrapUiFactory bootstrapUiFactory;
	private AutosuggestFormElementConfiguration configuration;

	@Override
	protected void registerWebResources( WebResourceRegistry webResourceRegistry ) {
		webResourceRegistry.addPackage(
				JQueryWebResources.NAME ); //both bloodhound.js and typeahead.jquery.js have a dependency on jQuery 1.9+
		webResourceRegistry.addPackage( BootstrapUiFormElementsWebResources.NAME );
		webResourceRegistry.addWithKey( WebResource.CSS, "autosuggest",
		                                "/static/BootstrapUiModule/css/autosuggest.css",
		                                WebResource.VIEWS );
	}

	public AutoSuggestFormElementBuilder configuration( AutosuggestFormElementConfiguration configuration ) {
		this.configuration = configuration;
		return this;
	}

	@Override
	protected NodeViewElement createElement( ViewElementBuilderContext viewElementBuilderContext ) {
		//TODO how to make this cleaner @Arne
		configuration.setEndPoint( buildLink( configuration.getEndPoint(), viewElementBuilderContext ) );
		return bootstrapUiFactory.div()
		                         .css( TYPEAHEAD_CLASS )
		                         .attribute( ATTRIBUTE_DATA_AUTOSUGGEST, configuration )
		                         .add( bootstrapUiFactory.textbox().css( TYPEAHEAD_INPUT_CLASS ) )
		                         .build( viewElementBuilderContext );
	}
}
