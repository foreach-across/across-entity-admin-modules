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

import com.foreach.across.modules.bootstrapui.elements.HiddenFormElement;
import com.foreach.across.modules.bootstrapui.elements.TextboxFormElement;
import com.foreach.across.modules.bootstrapui.elements.builder.FormControlElementBuilderSupport;
import com.foreach.across.modules.bootstrapui.elements.builder.TextboxFormElementBuilder;
import com.foreach.across.modules.bootstrapui.elements.entry.BootstrapViewElements;
import com.foreach.across.modules.bootstrapui.resource.BootstrapUiFormElementsWebResources;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

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
	/**
	 * CSS class on the wrapper, representing the autosuggest (typeahead) component.
	 */
	public static final String CSS_TYPEAHEAD_MODULE = "axbum-typeahead";

	/**
	 * CSS class put on the textbox, that will actually be initialized as the typeahead control.
	 */
	public static final String CSS_TYPEAHEAD = "js-typeahead";
	public static final String CSS_TYPEAHEAD_VALUE = "js-typeahead-value";

	private AutoSuggestFormElementConfiguration configuration = new AutoSuggestFormElementConfiguration();
	private Map<String, ViewElementBuilder> templatesByKey = new HashMap<>();

	/**
	 * -- SETTER --
	 * A textbox builder for creating a custom text input element.
	 * Note that the control name of the textbox will be cleared.
	 */
	@Setter
	private TextboxFormElementBuilder textboxBuilder;

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

	/**
	 * <p>Use a custom {@code ViewElementBuilder} that will be used as template when there are no suggestions found
	 * for the default dataset.
	 * </p>
	 *
	 * @param template that should be used
	 * @return current builder
	 * @see #notFoundTemplate(String, ViewElementBuilder)
	 */
	public AutoSuggestFormElementBuilder notFoundTemplate( ViewElementBuilder template ) {
		return notFoundTemplate( AutoSuggestFormElementConfiguration.DEFAULT_DATASET, template );
	}

	/**
	 * <p>Use a custom {@code ViewElementBuilder} that will be used as template when there are no suggestions found
	 * for the specified dataset.
	 * </p>
	 *
	 * @param datasetId id of the dataset
	 * @param template  that should be used
	 * @return current builder
	 */
	public AutoSuggestFormElementBuilder notFoundTemplate( String datasetId, ViewElementBuilder template ) {
		templatesByKey.put( String.format( "notFound-%s", datasetId ), template );
		return this;
	}

	/**
	 * <p>Use a custom {@code ViewElementBuilder} that will be used as template for the rendering of suggestions
	 * of the default dataset in the suggestion dropdown. The template will be applied to each suggestion and the
	 * associated suggestion object will be available within the context.
	 * The default template for suggestions is {@code <div>{{value}}</div>}.
	 * </p>
	 *
	 * @param template that should be used
	 * @return current builder
	 * @see #suggestionTemplate(String, ViewElementBuilder)
	 */
	public AutoSuggestFormElementBuilder suggestionTemplate( ViewElementBuilder template ) {
		return suggestionTemplate( AutoSuggestFormElementConfiguration.DEFAULT_DATASET, template );
	}

	/**
	 * <p>Use a custom {@code ViewElementBuilder} that will be used as template for the rendering of suggestions
	 * in the suggestion dropdown for a specific dataset. The template will be applied to each suggestion and the
	 * associated suggestion object will be available within the context.
	 * The default template for suggestions is {@code <div>{{value}}</div>}.
	 * </p>
	 *
	 * @param datasetId id of the dataset
	 * @param template  that should be used
	 * @return current builder
	 */
	public AutoSuggestFormElementBuilder suggestionTemplate( String datasetId, ViewElementBuilder template ) {
		templatesByKey.put( String.format( "suggestion-%s", datasetId ), template );
		return this;
	}

	/**
	 * <p>Use a custom {@code ViewElementBuilder} that will be used as template for the default dataset when synchronous results
	 * are not available, but asynchronous results are expected. The current query can be used in the template,
	 * for example {@code <div>Loading results for {{query}}...</div>}
	 * </p>
	 *
	 * @param template that should be used
	 * @return current builder
	 * @see #pendingTemplate(String, ViewElementBuilder)
	 */
	public AutoSuggestFormElementBuilder pendingTemplate( ViewElementBuilder template ) {
		return pendingTemplate( AutoSuggestFormElementConfiguration.DEFAULT_DATASET, template );
	}

	/**
	 * <p>Use a custom {@code ViewElementBuilder} that will be used as template for the default dataset when synchronous results
	 * are not available, but asynchronous results are expected. The current query can be used in the template,
	 * for example {@code <div>Loading results for {{query}}...</div>}
	 * </p>
	 *
	 * @param template that should be used
	 * @return current builder
	 */
	public AutoSuggestFormElementBuilder pendingTemplate( String name, ViewElementBuilder template ) {
		templatesByKey.put( String.format( "pending-%s", name ), template );
		return this;
	}

	/**
	 * <p>Use a custom {@code ViewElementBuilder} that will be used as template as the header of the suggestions fetched for the default dataset
	 * </p>
	 *
	 * @param template that should be used
	 * @return current builder
	 * @see #headerTemplate(String, ViewElementBuilder)
	 */
	public AutoSuggestFormElementBuilder headerTemplate( ViewElementBuilder template ) {
		return headerTemplate( AutoSuggestFormElementConfiguration.DEFAULT_DATASET, template );
	}

	/**
	 * <p>Use a custom {@code ViewElementBuilder} that will be used as template as the header of the suggestions fetched for the default dataset
	 * </p>
	 *
	 * @param template that should be used
	 * @return current builder
	 */
	public AutoSuggestFormElementBuilder headerTemplate( String name, ViewElementBuilder template ) {
		templatesByKey.put( String.format( "header-%s", name ), template );
		return this;
	}

	/**
	 * <p>Use a custom {@code ViewElementBuilder} that will be used as template as footer of the suggestions fetched for the default dataset
	 * </p>
	 *
	 * @param template that should be used
	 * @return current builder
	 * @see #headerTemplate(String, ViewElementBuilder)
	 */
	public AutoSuggestFormElementBuilder footerTemplate( ViewElementBuilder template ) {
		return footerTemplate( AutoSuggestFormElementConfiguration.DEFAULT_DATASET, template );
	}

	/**
	 * <p>Use a custom {@code ViewElementBuilder} that will be used as template as footer of the suggestions fetched for the default dataset
	 * </p>
	 *
	 * @param template that should be used
	 * @return current builder
	 */
	public AutoSuggestFormElementBuilder footerTemplate( String name, ViewElementBuilder template ) {
		templatesByKey.put( String.format( "footer-%s", name ), template );
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
		TextboxFormElement textbox = createTextbox( builderContext );

		HiddenFormElement value = new HiddenFormElement();
		value.addCssClass( CSS_TYPEAHEAD_VALUE );

		AutoSuggestFormElement container = new AutoSuggestFormElement( textbox, value );
		container.addCssClass( CSS_TYPEAHEAD_MODULE );
		container.setConfiguration( configuration.translate( url -> buildLink( url, builderContext ) ) );
		container.addChild( textbox );
		container.addChild( value );
		templatesByKey
				.entrySet()
				.stream()
				.map( entry -> createTemplateElement( entry.getKey(), entry.getValue() ).build( builderContext ) )
				.forEach( container::addChild );

		return apply( container, builderContext );
	}

	private ViewElementBuilder createTemplateElement( String key, ViewElementBuilder template ) {
		return BootstrapViewElements.bootstrap.builders.script()
				.data( "template", key )
				.type( MediaType.TEXT_HTML )
				.add( template );
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
				: BootstrapViewElements.bootstrap.builders.textbox().type( TextboxFormElement.Type.SEARCH ).attribute( "autocomplete", "off" ).build( context );
		textbox.addCssClass( CSS_TYPEAHEAD );

		return textbox;
	}
}
