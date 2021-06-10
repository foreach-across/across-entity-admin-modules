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

import com.foreach.across.modules.bootstrapui.elements.AbstractBootstrapViewElementTest;
import com.foreach.across.modules.bootstrapui.elements.FormInputElement;
import com.foreach.across.modules.bootstrapui.utils.BootstrapElementUtils;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import static com.foreach.across.modules.bootstrapui.elements.autosuggest.AutoSuggestFormElementBuilder.*;
import static com.foreach.across.modules.bootstrapui.elements.autosuggest.AutoSuggestFormElementConfiguration.withDataSet;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.html;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Sander Van Loock, Arne Vandamme
 * @since 2.0.0
 */
public class TestAutoSuggestFormElementBuilder extends AbstractBootstrapViewElementTest
{
	private AutoSuggestFormElementBuilder builder = new AutoSuggestFormElementBuilder();
	private DefaultViewElementBuilderContext context = new DefaultViewElementBuilderContext();

	@Test
	public void defaultTextbox() {
		Element markup = parse( builder.controlName( "my-property" ) );

		assertThat( markup.hasAttr( "data-bootstrapui-autosuggest" ) ).isTrue();
		assertThat( markup.hasClass( CSS_TYPEAHEAD_MODULE ) );

		Element textbox = single( markup.getElementsByClass( CSS_TYPEAHEAD ) );
		assertThat( textbox.hasAttr( "name" ) ).isFalse();
		assertThat( textbox.attr( "autocomplete" ) ).isEqualTo( "off" );
		assertThat( textbox.attr( "type" ) ).isEqualTo( "search" );
		assertThat( textbox.hasAttr( "value" ) ).isFalse();

		Element value = single( markup.getElementsByClass( CSS_TYPEAHEAD_VALUE ) );
		assertThat( value.attr( "name" ) ).isEqualTo( "my-property" );
		assertThat( value.hasAttr( "value" ) ).isFalse();


		/*renderAndExpect( actual,
		                 "" +
				                 "<div data-autosuggest=\"{&quot;endpoint&quot;:&quot;/autosuggest&quot;}\" class=\"js-typeahead\">\n" +
				                 "   <input type=\"text\" class=\"js-typeahead-input form-control\"/>\n" +
				                 "   <div class=\"hidden\">\n" +
				                 "      <div class=\"js-typeahead-suggestion-template\">\n" +
				                 "         <div data-as-property=\"label\"></div>\n" +
				                 "      </div>\n" +
				                 "      <table class=\"table\">\n" +
				                 "            <tr class=\"js-typeahead-template\">\n" +
				                 "               <td data-as-property=\"label\"></td>\n" +
				                 "               <td class=\"row-actions\"><a href=\"#\" title=\"REMOVE\"><span aria-hidden=\"true\" class=\"glyphicon glyphicon-remove\"></span></a><input name=\"id\" type=\"hidden\"/></td>\n" +
				                 "            </tr>\n" +
				                 "      </table>\n" +
				                 "      <div class=\"js-typeahead-empty-template \">Not Found</div>\n" +
				                 "   </div>\n" +
				                 "   <table class=\"js-typeahead-prefill table\"></table>\n" +
				                 "</div>" );*/

	}

	@Test
	public void updateControlName() {
		AutoSuggestFormElement control = builder.controlName( "one" ).build( context );
		render( control );
		control.setControlName( "two" );

		Element markup = parse( render( control ) );
		Element value = single( markup.getElementsByClass( CSS_TYPEAHEAD_VALUE ) );
		assertThat( value.attr( "name" ) ).isEqualTo( "two" );

		assertEquals( "two", control.getControlName() );
	}

	@Test
	public void updateControlNameThroughContainer() {
		ContainerViewElement container = new ContainerViewElement();
		FormInputElement control = builder.controlName( "one" ).build( context );
		control.setControlName( "one" );
		render( control );
		container.addChild( control );

		BootstrapElementUtils.prefixControlNames( "prefix.", container );

		Element markup = parse( render( control ) );
		Element value = single( markup.getElementsByClass( CSS_TYPEAHEAD_VALUE ) );
		assertThat( value.attr( "name" ) ).isEqualTo( "prefix.one" );

		assertEquals( "prefix.one", control.getControlName() );
	}

	@Test
	public void customLinkBuilderToTranslateUrls() {
		builder.linkBuilder( ( link ) -> "translatedUrl" )
		       .configuration( withDataSet( dataSet -> dataSet.remoteUrl( "someUrl" ) ) );

		AutoSuggestFormElement actual = builder.createElement( context );

		renderAndExpect( actual,
		                 "<div data-bootstrapui-adapter-type=\"autosuggest\" class=\"axbum-typeahead\"\n" +
				                 "        data-bootstrapui-autosuggest=\"{&quot;highlight&quot;:true,&quot;hint&quot;:true,&quot;minLength&quot;:1,&quot;_datasets&quot;:[{&quot;name&quot;:&quot;default&quot;,&quot;bloodhound&quot;:{&quot;remote&quot;:{&quot;url&quot;:&quot;translatedUrl&quot;}}}]}\">\n" +
				                 "        <input autocomplete=\"off\" type=\"search\" class=\"js-typeahead form-control\"></input><input type=\"hidden\"\n" +
				                 "                class=\"js-typeahead-value\"></input>\n" +
				                 "</div>" );
	}

	@Test
	public void customLinkBuilderToSetMaximumResults() {
		builder.linkBuilder( ( link ) -> "translatedUrl" )
		       .configuration( withDataSet( ds -> ds.maximumResults( 20 ) ) );

		AutoSuggestFormElement actual = builder.createElement( context );

		renderAndExpect( actual,
		                 "<div data-bootstrapui-adapter-type=\"autosuggest\" class=\"axbum-typeahead\"\n" +
				                 "        data-bootstrapui-autosuggest=\"{&quot;highlight&quot;:true,&quot;hint&quot;:true,&quot;minLength&quot;:1,&quot;_datasets&quot;:[{&quot;name&quot;:&quot;default&quot;,&quot;bloodhound&quot;:{},&quot;limit&quot;:20}]}\">\n" +
				                 "        <input autocomplete=\"off\" type=\"search\" class=\"js-typeahead form-control\"></input><input type=\"hidden\"\n" +
				                 "                class=\"js-typeahead-value\"></input>\n" +
				                 "</div>" );
	}

	private Element single( Elements elements ) {
		assertThat( elements ).hasSize( 1 );
		return elements.first();
	}

	private Element parse( AutoSuggestFormElementBuilder builder ) {
		return parse( render( builder.build( context ) ) );
	}

	private Element parse( String html ) {
		return Jsoup.parseBodyFragment( html )
		            .getElementsByAttribute( AutoSuggestFormElement.ATTRIBUTE_DATA_AUTOSUGGEST )
		            .get( 0 );
	}

	@Test
	public void customNotFoundTemplateWithViewElementBuilder() {
		builder.notFoundTemplate( html.builders.div().add( html.builders.text( "No results available for {{query}}" ) ) );
		AutoSuggestFormElement actual = builder.createElement( context );

		renderAndExpect( actual,
		                 "<div data-bootstrapui-adapter-type=\"autosuggest\" class=\"axbum-typeahead\"\n" +
				                 "        data-bootstrapui-autosuggest=\"{&quot;highlight&quot;:true,&quot;hint&quot;:true,&quot;minLength&quot;:1,&quot;_datasets&quot;:[]}\">\n" +
				                 "        <input autocomplete=\"off\" type=\"search\" class=\"js-typeahead form-control\"></input><input type=\"hidden\"\n" +
				                 "                class=\"js-typeahead-value\"></input>" +
				                 "   <script data-template='notFound-default' type='text/html'><div>No results available for {{query}}</div></script>" +
				                 "</div>" );

	}

	@Test
	public void customSuggestionTemplateWithViewElementBuilder() {
		builder.suggestionTemplate( html.builders.div().add( html.builders.text( "{{value}} (alt: {{other}})" ) ) );
		AutoSuggestFormElement actual = builder.createElement( context );

		renderAndExpect( actual,
		                 "<div data-bootstrapui-adapter-type=\"autosuggest\" class=\"axbum-typeahead\"\n" +
				                 "        data-bootstrapui-autosuggest=\"{&quot;highlight&quot;:true,&quot;hint&quot;:true,&quot;minLength&quot;:1,&quot;_datasets&quot;:[]}\">\n" +
				                 "        <input autocomplete=\"off\" type=\"search\" class=\"js-typeahead form-control\"></input><input type=\"hidden\"\n" +
				                 "                class=\"js-typeahead-value\"></input>" +
				                 "   <script data-template='suggestion-default' type='text/html'><div>{{value}} (alt: {{other}})</div></script>" +
				                 "</div>" );
	}

	@Test
	public void customFooterTemplateWithViewElementBuilder() {
		builder.footerTemplate( "willyWonka", html.builders.text( "End of results for willy wonka" ) );
		AutoSuggestFormElement actual = builder.createElement( context );

		renderAndExpect( actual,
		                 "<div data-bootstrapui-adapter-type=\"autosuggest\" class=\"axbum-typeahead\"\n" +
				                 "        data-bootstrapui-autosuggest=\"{&quot;highlight&quot;:true,&quot;hint&quot;:true,&quot;minLength&quot;:1,&quot;_datasets&quot;:[]}\">\n" +
				                 "        <input autocomplete=\"off\" type=\"search\" class=\"js-typeahead form-control\"></input><input type=\"hidden\"\n" +
				                 "                class=\"js-typeahead-value\"></input>" +
				                 "   <script data-template='footer-willyWonka' type='text/html'>End of results for willy wonka</script>" +
				                 "</div>" );
	}

	@Test
	public void customHeaderTemplateWithViewElementBuilder() {
		builder.headerTemplate( html.builders.div().attribute( "style", "text-decoration: underline" ).add( html.builders.text( "Suggestions" ) ) );
		AutoSuggestFormElement actual = builder.createElement( context );

		renderAndExpect( actual,
		                 "<div data-bootstrapui-adapter-type=\"autosuggest\" class=\"axbum-typeahead\"\n" +
				                 "        data-bootstrapui-autosuggest=\"{&quot;highlight&quot;:true,&quot;hint&quot;:true,&quot;minLength&quot;:1,&quot;_datasets&quot;:[]}\">\n" +
				                 "        <input autocomplete=\"off\" type=\"search\" class=\"js-typeahead form-control\"></input><input type=\"hidden\"\n" +
				                 "                class=\"js-typeahead-value\"></input>" +
				                 "   <script data-template='header-default' type='text/html'><div style='text-decoration: underline'>Suggestions</div></script>" +
				                 "</div>" );
	}

	@Test
	public void customPendingTemplateWithViewElementBuilder() {
		builder.pendingTemplate( "cars", html.builders.text( "Fetching results for {{query}}..." ) );
		AutoSuggestFormElement actual = builder.createElement( context );

		renderAndExpect( actual,
		                 "<div data-bootstrapui-adapter-type=\"autosuggest\" class=\"axbum-typeahead\"\n" +
				                 "        data-bootstrapui-autosuggest=\"{&quot;highlight&quot;:true,&quot;hint&quot;:true,&quot;minLength&quot;:1,&quot;_datasets&quot;:[]}\">\n" +
				                 "        <input autocomplete=\"off\" type=\"search\" class=\"js-typeahead form-control\"></input><input type=\"hidden\"\n" +
				                 "                class=\"js-typeahead-value\"></input>" +
				                 "   <script data-template='pending-cars' type='text/html'>Fetching results for {{query}}...</script>" +
				                 "</div>" );
	}
}
