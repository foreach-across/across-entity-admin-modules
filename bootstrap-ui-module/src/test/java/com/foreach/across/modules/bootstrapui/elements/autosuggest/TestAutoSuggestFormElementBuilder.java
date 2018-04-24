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
import com.foreach.across.modules.bootstrapui.elements.builder.AlertViewElementBuilder;
import com.foreach.across.modules.bootstrapui.utils.BootstrapElementUtils;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.TemplateViewElement;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Ignore;
import org.junit.Test;

import static com.foreach.across.modules.bootstrapui.elements.autosuggest.AutoSuggestFormElementBuilder.*;
import static org.assertj.core.api.Assertions.assertThat;

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
	}

	@Test
	public void customTextboxAsInput() {

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

	@Ignore
	@Test
	public void customEndPointMarkup() {
		String endPoint = "/my-custom-endppoint";
		builder.endpoint( endPoint );
		AutoSuggestFormElement actual = builder.createElement( context );

		renderAndExpect( actual,
		                 String.format( "" +
				                                "<div data-autosuggest=\"{&quot;endpoint&quot;:&quot;%1$s&quot;}\" class=\"js-typeahead\">\n" +
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
				                                "</div>", endPoint ) );

	}

	@Ignore
	@Test
	public void customNotFoundTemplateWithViewElement() {
		builder.notFoundTemplate( new AlertViewElement() );
		AutoSuggestFormElement actual = builder.createElement( context );

		renderAndExpect( actual,
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
				                 "      <div class=\"js-typeahead-empty-template \"><div class='alert' role='alert'></div></div>\n" +
				                 "   </div>\n" +
				                 "   <table class=\"js-typeahead-prefill table\"></table>\n" +
				                 "</div>" );

	}

	@Ignore
	@Test
	public void customNotFoundTemplateWithBuilder() {
		builder.notFoundTemplate( new AlertViewElementBuilder() );
		AutoSuggestFormElement actual = builder.createElement( context );

		renderAndExpect( actual,
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
				                 "      <div class=\"js-typeahead-empty-template \"><div class='alert' role='alert'></div></div>\n" +
				                 "   </div>\n" +
				                 "   <table class=\"js-typeahead-prefill table\"></table>\n" +
				                 "</div>" );

	}

	@Ignore
	@Test
	public void customSuggestionTemplateWithViewElement() {
		BootstrapUiFactoryImpl bootstrapUiFactory = new BootstrapUiFactoryImpl();
		NodeViewElementBuilder suggestionTemplate = bootstrapUiFactory.span().attribute(
				AutoSuggestFormElementBuilder.ATTRIBUTE_DATA_PROPERTY, "link" )
		                                                              .add( new GlyphIcon( GlyphIcon.ASTERISK ) );
		builder.suggestionTemplate( suggestionTemplate );
		builder.properties( "link" );
		AutoSuggestFormElement actual = builder.createElement( context );

		renderAndExpect( actual,
		                 "" +
				                 "<div data-autosuggest=\"{&quot;endpoint&quot;:&quot;/autosuggest&quot;}\" class=\"js-typeahead\">\n" +
				                 "   <input type=\"text\" class=\"js-typeahead-input form-control\"/>\n" +
				                 "   <div class=\"hidden\">\n" +
				                 "      <div class=\"js-typeahead-suggestion-template\">\n" +
				                 "         <span data-as-property=\"link\"><span aria-hidden=\"true\" class=\"glyphicon glyphicon-asterisk\"></span></span>\n" +
				                 "      </div>\n" +
				                 "      <table class=\"table\">\n" +
				                 "            <tr class=\"js-typeahead-template\">\n" +
				                 "               <td data-as-property=\"link\"></td>\n" +
				                 "               <td class=\"row-actions\"><a href=\"#\" title=\"REMOVE\"><span aria-hidden=\"true\" class=\"glyphicon glyphicon-remove\"></span></a><input name=\"id\" type=\"hidden\"/></td>\n" +
				                 "            </tr>\n" +
				                 "      </table>\n" +
				                 "      <div class=\"js-typeahead-empty-template\">Not Found</div>\n" +
				                 "   </div>\n" +
				                 "   <table class=\"js-typeahead-prefill table\"></table>\n" +
				                 "</div>" );
	}

	@Ignore
	@Test
	public void customItemTemplateWithViewElement() {
		NodeViewElement container = new NodeViewElementBuilder( "ul" )
				.css( CSS_PREFILL_TABLE )
				.build( new DefaultViewElementBuilderContext() );
		NodeViewElement item = new NodeViewElementBuilder( "ul" )
				.add( new TemplateViewElement( "th/test/customItemList :: item" ) )
				.build( new DefaultViewElementBuilderContext() );
		builder.itemTemplate( container, item );
		builder.properties( "name" );
		AutoSuggestFormElement actual = builder.createElement( context );

		renderAndExpect( actual,
		                 "" +
				                 "<div data-autosuggest=\"{&quot;endpoint&quot;:&quot;/autosuggest&quot;}\" class=\"js-typeahead\">\n" +
				                 "   <input type=\"text\" class=\"js-typeahead-input form-control\"/>\n" +
				                 "   <div class=\"hidden\">\n" +
				                 "      <div class=\"js-typeahead-suggestion-template\">\n" +
				                 "         <div data-as-property=\"name\"></div>\n" +
				                 "      </div>\n" +
				                 "      <ul>\n" +
				                 "            <li class=\"js-typeahead-template\">\n" +
				                 "               <div data-as-property=\"name\"></div>\n" +
				                 "               <a>delete</a>\n" +
				                 "            </li>\n" +
				                 "      </ul>\n" +
				                 "      <div class=\"js-typeahead-empty-template\">Not Found</div>\n" +
				                 "   </div>\n" +
				                 "      <ul class=\"js-typeahead-prefill\"></ul>\n" +
				                 "</div>" );
	}
}