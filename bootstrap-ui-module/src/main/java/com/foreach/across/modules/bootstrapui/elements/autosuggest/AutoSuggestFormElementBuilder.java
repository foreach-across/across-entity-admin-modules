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

package com.foreach.across.modules.bootstrapui.elements.autosuggest;

import com.foreach.across.modules.bootstrapui.elements.*;
import com.foreach.across.modules.bootstrapui.elements.builder.FormControlElementBuilderSupport;
import com.foreach.across.modules.bootstrapui.elements.builder.TableViewElementBuilder;
import com.foreach.across.modules.bootstrapui.elements.builder.TextboxFormElementBuilder;
import com.foreach.across.modules.bootstrapui.resource.BootstrapUiFormElementsWebResources;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementBuilderSupport;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementBuilder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Will create an autosuggest component backed by a Typeahead JS implementation.
 * The textbox will serve as the typeahead input control, a hidden control will be
 * <p/>
 * The client-side javascript is responsible for swapping the actual textbox and hidden field control,
 * so the hidden field is used for the post-back value.
 */
@RequiredArgsConstructor
@Accessors(fluent = true)
public class AutoSuggestFormElementBuilder extends FormControlElementBuilderSupport<AutoSuggestFormElement, AutoSuggestFormElementBuilder>
{
	public static final String ATTRIBUTE_DATA_PROPERTY = "data-as-property";

	/**
	 * CSS class on the wrapper, representing the autosuggest (typeahead) component.
	 */
	public static final String CSS_TYPEAHEAD_MODULE = "axbum-typeahead";

	/**
	 * CSS class put on the textbox, that will actually be initialized as the typeahead control.
	 */
	public static final String CSS_TYPEAHEAD = "js-typeahead";
	public static final String CSS_TYPEAHEAD_VALUE = "js-typeahead-value";

	public static final String CSS_TYPEAHEAD_ITEM_CLASS = "js-typeahead-item";
	public static final String CSS_PREFILL_TABLE = "js-typeahead-prefill";
	public static final String CSS_SUGGESTION_TEMPLATE = "js-typeahead-suggestion-template";
	public static final String CSS_ITEM_TEMPLATE = "js-typeahead-template";
	public static final String CSS_EMPTY_TEMPLATE = "js-typeahead-empty-template";

	public static final String DEFAULT_PROPERTY = "label";

	private AutoSuggestFormElementConfiguration configuration = new AutoSuggestFormElementConfiguration();

	private String idProperty = "id";
	private String endpoint;

	private List<String> properties = Collections.singletonList( DEFAULT_PROPERTY );
	private List<Map<String, Object>> prefill = Collections.emptyList();
	private ViewElementBuilderSupport.ElementOrBuilder notFoundTemplate;
	private ViewElementBuilderSupport.ElementOrBuilder suggestionTemplate;
	private ViewElementBuilderSupport.ElementOrBuilder itemTemplate;

	/**
	 * -- SETTER --
	 * A textbox builder for creating a custom text input element.
	 * Note that the control name of the textbox will be cleared.
	 */
	@Setter
	private TextboxFormElementBuilder textboxBuilder;

	private ViewElement containerTemplate;

	private Function<String, String> linkBuilder;

	/**
	 * Set the configuration for the autosuggest control.
	 *
	 * @param configuration to use
	 * @return current builder
	 */
	public AutoSuggestFormElementBuilder configuration( @NonNull AutoSuggestFormElementConfiguration configuration ) {
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

	/**
	 * Configure the endpoint that this control should use to retrieve suggestions.
	 *
	 * @param endpoint url
	 * @return current builder
	 */
	public AutoSuggestFormElementBuilder endpoint( String endpoint ) {
		this.endpoint = endpoint;
		return this;
	}

	public AutoSuggestFormElementBuilder notFoundTemplate( ViewElement notFoundTemplate ) {
		this.notFoundTemplate = ElementOrBuilder.wrap( notFoundTemplate );
		return this;
	}

	public AutoSuggestFormElementBuilder notFoundTemplate( ViewElementBuilder notFoundTemplate ) {
		this.notFoundTemplate = ElementOrBuilder.wrap( notFoundTemplate );
		return this;
	}

	/**
	 * <p>Use a custom {@code ViewElementBuilder} that will be used as a template for the rendering of suggestions
	 * in the suggestion dropdown.
	 * </p>
	 * <p>
	 * If you want to reuse properties from this {@code AutoSuggestFormElementBuilder}
	 * instance, make sure to add an attribute {@code ATTRIBUTE_DATA_PROPERTY}to the node element which inner HTML should
	 * be replaced.
	 * </p>
	 */
	public AutoSuggestFormElementBuilder suggestionTemplate( ViewElementBuilder template ) {
		this.suggestionTemplate = ElementOrBuilder.wrap( template );
		return this;
	}

	/**
	 * {@see com.foreach.across.modules.bootstrapui.elements.builder.AutoSuggestFormElementBuilder#suggestionTemplate}
	 */
	public AutoSuggestFormElementBuilder suggestionTemplate( ViewElement template ) {
		this.suggestionTemplate = ElementOrBuilder.wrap( template );
		return this;
	}

	/**
	 * <p>
	 * Use a custom {@code ViewElement} that will be used as a template for the rendering of selected suggestions.
	 * </p>
	 * <p>
	 * The given {@code ContainerViewElementBuilder} will contain all selected suggestions.  The container may not have child nodes
	 * The given itemTemplate must contain a node with class {@code CSS_ITEM_TEMPLATE}. This childnode will be repeated
	 * for all selected suggestions.
	 * If you want to reuse properties from this {@code AutoSuggestFormElementBuilder} instance, make sure to add an
	 * attribute {@code ATTRIBUTE_DATA_PROPERTY}to the node element which inner HTML should be replaced
	 * </p>
	 */
	public AutoSuggestFormElementBuilder itemTemplate( ViewElement containerTemplate, ViewElement itemTemplate ) {
		this.containerTemplate = containerTemplate;
		this.itemTemplate = ElementOrBuilder.wrap( itemTemplate );
		return this;
	}

	/**
	 * Set a conversion function that should be applied to all url type properties
	 * when setting them as the attribute for the generated links.
	 * <p/>
	 * If not set, this builder will dispatch to {@link ViewElementBuilderContext#buildLink(String)}.
	 * You can suppress the default behaviour by setting this property to {@link Function#identity()}.
	 *
	 * @param linkBuilder to use for translating the urls
	 * @return current builder
	 */
	@SuppressWarnings("unchecked")
	public AutoSuggestFormElementBuilder linkBuilder( Function<String, String> linkBuilder ) {
		this.linkBuilder = linkBuilder;
		return this;
	}

	@Override
	protected AutoSuggestFormElement createElement( ViewElementBuilderContext builderContext ) {
	/*
		if ( StringUtils.isNotBlank( endpoint ) ) {
			this.configuration.setEndpoint( buildLink( endpoint, viewElementBuilderContext ) );
		}
		if ( configuration != null ) {
			this.configuration = configuration.localize( LocaleContextHolder.getLocale() );
		}*/

/*		return BootstrapUiBuilders.div()
		                          .css( CSS_TYPEAHEAD )
		                          .attribute( ATTRIBUTE_DATA_AUTOSUGGEST, configuration )
		                          .add( renderInputElement() )
		                          .add( renderTemplates( viewElementBuilderContext ) )
		                          //.add( renderPrefillValues( viewElementBuilderContext ) )
		                          .build( viewElementBuilderContext );
		                          */
		TextboxFormElement textbox = createTextbox( builderContext );

		HiddenFormElement value = new HiddenFormElement();
		value.addCssClass( CSS_TYPEAHEAD_VALUE );

		AutoSuggestFormElement container = new AutoSuggestFormElement( textbox, value );
		container.addCssClass( CSS_TYPEAHEAD_MODULE );
		container.setConfiguration( configuration.translate( url -> buildLink( url, builderContext ) ) );
		container.addChild( textbox );
		container.addChild( value );

		return apply( container, builderContext );
	}

	protected String buildLink( String link, ViewElementBuilderContext builderContext ) {
		if ( linkBuilder != null ) {
			return linkBuilder.apply( link );
		}

		return builderContext.buildLink( link );
	}

	@Override
	protected void registerWebResources( WebResourceRegistry webResourceRegistry ) {
		webResourceRegistry.addPackage( BootstrapUiFormElementsWebResources.NAME );
	}

	private TextboxFormElement createTextbox( ViewElementBuilderContext context ) {
		TextboxFormElement textbox = textboxBuilder != null
				? textboxBuilder.build( context )
				: BootstrapUiBuilders.textbox().type( TextboxFormElement.Type.SEARCH ).attribute( "autocomplete", "off" ).build( context );
		textbox.addCssClass( CSS_TYPEAHEAD );

		return textbox;
	}

	private ViewElement renderPrefillValues( ViewElementBuilderContext viewElementBuilderContext ) {
		TableViewElementBuilder prefillTableElement = BootstrapUiBuilders.table();
		TableViewElement defaultPrefillValues = prefillTableElement.css( CSS_PREFILL_TABLE )
		                                                           .addAll( prefill.stream()
		                                                                           .map( it -> renderDefaultPrefill(
				                                                                           prefillTableElement,
				                                                                           it ) )
		                                                                           .collect( Collectors
				                                                                                     .toList() ) )
		                                                           .build( viewElementBuilderContext );
		return containerTemplate != null ? containerTemplate : defaultPrefillValues;
	}

	private NodeViewElementBuilder renderTemplates( ViewElementBuilderContext viewElementBuilderContext ) {
		return BootstrapUiBuilders.div()
		                          .css( "hidden" )
		                          .add( getSuggestionTemplate( viewElementBuilderContext ) )
		                          .add( getItemTemplate( viewElementBuilderContext ) )
		                          .add( getNotFoundTemplate( viewElementBuilderContext ) );
	}

	private NodeViewElementBuilder getNotFoundTemplate( ViewElementBuilderContext viewElementBuilderContext ) {
		NodeViewElementBuilder notFoundContainer = BootstrapUiBuilders.div()
		                                                              .css( CSS_EMPTY_TEMPLATE );
		ViewElement defaultNotFoundViewElement = BootstrapUiBuilders.text( "Not Found" )
		                                                            .build( viewElementBuilderContext );
		return notFoundContainer.add( notFoundTemplate != null ?
				                              notFoundTemplate
						                              .get( viewElementBuilderContext ) : defaultNotFoundViewElement );
	}

	private NodeViewElementBuilder getSuggestionTemplate( ViewElementBuilderContext viewElementBuilderContext ) {
		NodeViewElementBuilder suggestionContainer = BootstrapUiBuilders.div()
		                                                                .css( CSS_SUGGESTION_TEMPLATE );
		ViewElement defaultSuggestionElementOrBuilder =
				BootstrapUiBuilders.container()
				                   .addAll( properties.stream()
				                                      .map( prop -> BootstrapUiBuilders.div()
				                                                                       .attribute(
						                                                                       ATTRIBUTE_DATA_PROPERTY,
						                                                                       prop ) )
				                                      .collect( Collectors.toList() ) )
				                   .build( viewElementBuilderContext );
		return suggestionContainer.add( suggestionTemplate != null ?
				                                suggestionTemplate
						                                .get( viewElementBuilderContext ) : defaultSuggestionElementOrBuilder );
	}

	private ViewElement getItemTemplate( ViewElementBuilderContext viewElementBuilderContext ) {
		if ( itemTemplate != null ) {
			return itemTemplate.get( viewElementBuilderContext );
		}
		TableViewElementBuilder defaultItemContainer = BootstrapUiBuilders.table();
		return defaultItemContainer.add( defaultItemContainer.row()
		                                                     .css( CSS_ITEM_TEMPLATE )
		                                                     .addAll( properties.stream()
		                                                                        .map( prop -> defaultItemContainer
				                                                                        .cell()
				                                                                        .attribute(
						                                                                        "data-as-property",
						                                                                        prop ) )
		                                                                        .collect(
				                                                                        Collectors
						                                                                        .toList() ) )
		                                                     .add( defaultItemContainer.cell()
		                                                                               .css( "row-actions" )
		                                                                               .add( BootstrapUiBuilders
				                                                                                     .link()
				                                                                                     .title( "REMOVE" ) //TODO make configurable
				                                                                                     .add( new GlyphIcon(
						                                                                                     GlyphIcon.REMOVE ) ) )
		                                                                               .add( BootstrapUiBuilders
				                                                                                     .hidden()
				                                                                                     .controlName(
						                                                                                     idProperty ) ) ) )
		                           .build( viewElementBuilderContext );
	}

	private TableViewElementBuilder.Row renderDefaultPrefill( TableViewElementBuilder table,
	                                                          Map<String, Object> items ) {
		return table.row()
		            .css( CSS_TYPEAHEAD_ITEM_CLASS )
		            .addAll( items.entrySet().stream()
		                          .map( prop -> table.cell()
		                                             .text( prop.getValue().toString() ) )
		                          .collect( Collectors.toList() ) )
		            .add( table.cell()
		                       .css( "row-actions" )
		                       .add( BootstrapUiBuilders
				                             .link()
				                             .title( "REMOVE" ) //TODO make configurable
				                             .add( new GlyphIcon( GlyphIcon.REMOVE ) ) )
		                       .add( BootstrapUiBuilders
				                             .hidden()
				                             .value( items.get( idProperty ) )
				                             .controlName( idProperty ) ) );
	}
}
