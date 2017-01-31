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
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
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
	public static final String ATTRIBUTE_DATA_PROPERTY = "data-as-property";
	public static final String DEFAULT_PROPERTY = "label";

	private final BootstrapUiFactory bootstrapUiFactory;
	private AutosuggestFormElementConfiguration configuration = new AutosuggestFormElementConfiguration();

	private String idProperty = "id";
	private String endPoint;

	private List<String> properties = Collections.singletonList( DEFAULT_PROPERTY );
	private List<Map<String, Object>> prefill = Collections.emptyList();
	private Optional<ElementOrBuilder> notFoundTemplate = Optional.empty();
	private Optional<ElementOrBuilder> suggestionTemplate = Optional.empty();

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

	public AutoSuggestFormElementBuilder endPoint( String endPoint ) {
		this.endPoint = endPoint;
		return this;
	}

	public AutoSuggestFormElementBuilder notFoundTemplate( ViewElement notFoundTemplate ) {
		this.notFoundTemplate = Optional.of( ElementOrBuilder.wrap( notFoundTemplate ) );
		return this;
	}

	public AutoSuggestFormElementBuilder notFoundTemplate( ViewElementBuilder notFoundTemplate ) {
		this.notFoundTemplate = Optional.of( ElementOrBuilder.wrap( notFoundTemplate ) );
		return this;
	}

	/**
	 * Use a custom {@code ViewElementBuilder} that will be used as a template for the rendering of suggestions
	 * in the suggestion dropdown.  If you want to reuse properties from this {@code AutoSuggestFormElementBuilder}
	 * instance, make sure to add an attribute {@code ATTRIBUTE_DATA_PROPERTY}to the node element which inner HTML should
	 * be replaced
	 */
	public AutoSuggestFormElementBuilder suggestionTemplate( ViewElementBuilder template ) {
		this.suggestionTemplate = Optional.of( ElementOrBuilder.wrap( template ) );
		return this;
	}

	/**
	 * {@see com.foreach.across.modules.bootstrapui.elements.builder.AutoSuggestFormElementBuilder#suggestionTemplate}
	 */
	public AutoSuggestFormElementBuilder suggestionTemplate( ViewElement template ) {
		this.suggestionTemplate = Optional.of( ElementOrBuilder.wrap( template ) );
		return this;
	}

	@Override
	protected NodeViewElement createElement( ViewElementBuilderContext viewElementBuilderContext ) {
		if ( StringUtils.isNotBlank( endPoint ) ) {
			this.configuration.setEndPoint( buildLink( endPoint, viewElementBuilderContext ) );
		}
		if ( configuration != null ) {
			this.configuration = configuration.localize( LocaleContextHolder.getLocale() );
		}

		return bootstrapUiFactory.div()
		                         .css( CSS_TYPEAHEAD_CLASS )
		                         .attribute( ATTRIBUTE_DATA_AUTOSUGGEST, configuration )
		                         .add( renderInputElement() )
		                         .add( renderTemplates( viewElementBuilderContext ) )
		                         .add( renderPrefillValues() )
		                         .build( viewElementBuilderContext );
	}

	@Override
	protected void registerWebResources( WebResourceRegistry webResourceRegistry ) {
		webResourceRegistry.addPackage( BootstrapUiFormElementsWebResources.NAME );
	}

	private TextboxFormElementBuilder renderInputElement() {
		return bootstrapUiFactory.textbox().css( CSS_TYPEAHEAD_INPUT );
	}

	private TableViewElementBuilder renderPrefillValues() {
		TableViewElementBuilder prefillTableElement = bootstrapUiFactory.table();
		return prefillTableElement.css( CSS_PREFILL_TABLE )
		                          .addAll( prefill.stream().map( it -> renderPrefill(
				                          prefillTableElement, CSS_TYPEAHEAD_ITEM_CLASS, it ) )
		                                          .collect( Collectors.toList() ) );
	}

	private NodeViewElementBuilder renderTemplates( ViewElementBuilderContext viewElementBuilderContext ) {
		return bootstrapUiFactory.div()
		                         .css( "hidden" )
		                         .add( getSuggestionTemplate( viewElementBuilderContext ) )
		                         .add( getItemTemplate( CSS_ITEM_TEMPLATE, properties ) )
		                         .add( getNotFoundTemplate( viewElementBuilderContext ) );
	}

	private NodeViewElementBuilder getNotFoundTemplate( ViewElementBuilderContext viewElementBuilderContext ) {
		NodeViewElementBuilder notFoundContainer = bootstrapUiFactory.div()
		                                                             .css( CSS_EMPTY_TEMPLATE, "empty-message" );
		ElementOrBuilder defaultNotFoundElementOrBuilder = ElementOrBuilder.wrap(
				bootstrapUiFactory.text( "Not Found" ) );//TODO make label
		return notFoundContainer.add( notFoundTemplate.orElse( defaultNotFoundElementOrBuilder )
		                                              .get( viewElementBuilderContext ) );
	}

	private NodeViewElementBuilder getSuggestionTemplate( ViewElementBuilderContext viewElementBuilderContext ) {
		NodeViewElementBuilder suggestionContainer = bootstrapUiFactory.div()
		                                                               .css( CSS_SUGGESTION_TEMPLATE );
		ElementOrBuilder defaultSuggestionElementOrBuilder =
				ElementOrBuilder.wrap( bootstrapUiFactory.container()
				                                         .addAll( properties.stream()
				                                                            .map( prop -> bootstrapUiFactory.div()
				                                                                                            .attribute(
						                                                                                            ATTRIBUTE_DATA_PROPERTY,
						                                                                                            prop ) )
				                                                            .collect( Collectors.toList() ) ) );
		return suggestionContainer
				.add( suggestionTemplate.orElse( defaultSuggestionElementOrBuilder ).get( viewElementBuilderContext ) );
	}

	private TableViewElementBuilder getItemTemplate( String classForRow,
	                                                 List<String> properties ) {
		TableViewElementBuilder table = bootstrapUiFactory.table();
		return table.add( table.row()
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
				                                        .controlName( idProperty ) ) ) );
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
