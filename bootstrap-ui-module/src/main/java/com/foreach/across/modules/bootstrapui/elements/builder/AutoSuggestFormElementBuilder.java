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
import com.foreach.across.modules.bootstrapui.elements.GlyphIcon;
import com.foreach.across.modules.bootstrapui.resource.BootstrapUiFormElementsWebResources;
import com.foreach.across.modules.web.resource.WebResource;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementBuilder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AutoSuggestFormElementBuilder extends AbstractLinkSupportingNodeViewElementBuilder<NodeViewElement, AutoSuggestFormElementBuilder>
{
	public static final String CSS_TYPEAHEAD_CLASS = "js-typeahead";
	public static final String CSS_TYPEAHEAD_INPUT = "js-typeahead-input";
	public static final String CSS_TYPEAHEAD_ITEM_CLASS = "js-typeahead-item";
	public static final String CSS_PREFILL_TABLE = "js-typeahead-prefill";

	public static final String CSS_SUGGESTION_TEMPLATE = "js-typeahead-suggestion-template";
	public static final String CSS_ITEM_TEMPLATE = "js-typeahead-template";
	public static final String CSS_EMPTY_TEMPLATE = "js-typeahead-empty-template";

	public static final String ATTRIBUTE_DATA_AUTOSUGGEST = "data-autosuggest";
	public static final String DEFAULT_PROPERTY = "label";

	private final BootstrapUiFactory bootstrapUiFactory;
	private AutosuggestFormElementConfiguration configuration = new AutosuggestFormElementConfiguration();

	private String idProperty = "id";

	private List<String> properties = Collections.singletonList( DEFAULT_PROPERTY );
	private List<Map<String, Object>> prefill = Collections.emptyList();

	@Override
	protected void registerWebResources( WebResourceRegistry webResourceRegistry ) {
		webResourceRegistry.addPackage( BootstrapUiFormElementsWebResources.NAME );
		webResourceRegistry.addWithKey( WebResource.CSS, "autosuggest",
		                                "/static/BootstrapUiModule/css/autosuggest.css",
		                                WebResource.VIEWS );
	}

	public AutoSuggestFormElementBuilder configuration( AutosuggestFormElementConfiguration configuration ) {
		this.configuration = configuration;
		return this;
	}

	public AutoSuggestFormElementBuilder idProperty( String idProperty ) {
		this.idProperty = idProperty;
		return this;
	}

	public AutoSuggestFormElementBuilder prefill( List<Map<String, Object>> prefillValues ) {
		this.prefill = prefillValues;
		return this;
	}

	public AutoSuggestFormElementBuilder properties( String... properties ) {
		this.properties = Arrays.asList( properties );
		return this;
	}

	@Override
	protected NodeViewElement createElement( ViewElementBuilderContext viewElementBuilderContext ) {
		//TODO how to make this cleaner @Arne
		if ( StringUtils.isNotBlank( configuration.getEndPoint() ) ) {
			configuration.setEndPoint( buildLink( configuration.getEndPoint(), viewElementBuilderContext ) );
		}

		TableViewElementBuilder prefillTableElement = bootstrapUiFactory.table();
		return bootstrapUiFactory.div()
		                         .css( CSS_TYPEAHEAD_CLASS )
		                         .attribute( ATTRIBUTE_DATA_AUTOSUGGEST, configuration )
		                         .add( renderTemplates() )
		                         .add( bootstrapUiFactory.textbox().css( CSS_TYPEAHEAD_INPUT ),
		                               prefillTableElement.css( CSS_PREFILL_TABLE )
		                                                  .addAll( prefill.stream().map( it -> renderPrefill(
				                                                  prefillTableElement, CSS_TYPEAHEAD_ITEM_CLASS, it ) )
		                                                                  .collect( Collectors.toList() ) ) )
		                         .build( viewElementBuilderContext );
	}

	private NodeViewElementBuilder renderTemplates() {
		return bootstrapUiFactory.div()
		                         .css( "hidden" )
		                         .add( getSuggestionTemplate( properties ),
		                               getItemTemplate( CSS_ITEM_TEMPLATE, properties ),
		                               getNotFoundTemplate() );
	}

	private NodeViewElementBuilder getNotFoundTemplate() {
		return bootstrapUiFactory.div()
		                         .css( CSS_EMPTY_TEMPLATE, " empty-message" )
		                         .add( bootstrapUiFactory.text( "Not Found" ) );
	}

	private NodeViewElementBuilder getSuggestionTemplate( List<String> properties ) {
		return bootstrapUiFactory.div()
		                         .css( CSS_SUGGESTION_TEMPLATE )
		                         .addAll( properties.stream()
		                                            .map( prop -> bootstrapUiFactory.div()
		                                                                            .attribute( "data-as-property",
		                                                                                        prop ) )
		                                            .collect( Collectors.toList() ) );
	}

	private TableViewElementBuilder.Row getItemTemplate( String classForRow,
	                                                     List<String> properties ) {
		TableViewElementBuilder table = bootstrapUiFactory.table();
		return table.row()
		            .css( classForRow )
		            .addAll( properties.stream()
		                               .map( prop -> table.cell()
		                                                  .attribute( "data-as-property", prop ) )
		                               .collect( Collectors.toList() ) )
		            .add( table.cell()
		                       .css( "row-actions" )
		                       .add( bootstrapUiFactory
				                             .link()
				                             .title( "REMOVE" ) //TODO make configurable
				                             .add( new GlyphIcon( GlyphIcon.REMOVE ) ) )
		                       .add( bootstrapUiFactory
				                             .hidden()
				                             .controlName( idProperty ) ) );
	}

	private TableViewElementBuilder.Row renderPrefill( TableViewElementBuilder table,
	                                                   String classForRow,
	                                                   Map<String, Object> items ) {
		return table.row()
		            .css( classForRow )
		            .addAll( items.entrySet().stream()
		                          .map( prop -> table.cell()
		                                             .text( prop.getValue().toString() ) )
		                          .collect( Collectors.toList() ) )
		            .add( table.cell()
		                       .css( "row-actions" )
		                       .add( bootstrapUiFactory
				                             .link()
				                             .title( "REMOVE" ) //TODO make configurable
				                             .add( new GlyphIcon( GlyphIcon.REMOVE ) ) )
		                       .add( bootstrapUiFactory
				                             .hidden()
				                             .value( items.get( idProperty ) )
				                             .controlName( idProperty ) ) );
	}

}
