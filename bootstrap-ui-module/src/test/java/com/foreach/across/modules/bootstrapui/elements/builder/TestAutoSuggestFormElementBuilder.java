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

import com.foreach.across.modules.bootstrapui.elements.AbstractBootstrapViewElementTest;
import com.foreach.across.modules.bootstrapui.elements.AlertViewElement;
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiFactoryImpl;
import com.foreach.across.modules.bootstrapui.elements.GlyphIcon;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.TemplateViewElement;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementBuilder;
import org.junit.Before;
import org.junit.Test;

import static com.foreach.across.modules.bootstrapui.elements.builder.AutoSuggestFormElementBuilder.CSS_PREFILL_TABLE;

/**
 * @author: Sander Van Loock
 * @since 1.0.0
 */
public class TestAutoSuggestFormElementBuilder extends AbstractBootstrapViewElementTest
{
	private AutoSuggestFormElementBuilder builder;
	private DefaultViewElementBuilderContext context;

	@Before
	public void setUp() throws Exception {
		builder = new AutoSuggestFormElementBuilder( new BootstrapUiFactoryImpl() );
		context = new DefaultViewElementBuilderContext();
	}

	@Test
	public void defaultBuilderMarkup() throws Exception {
		NodeViewElement actual = builder.createElement( context );

		renderAndExpect( actual,
		                 "" +
				                 "<div data-autosuggest=\"{&quot;endPoint&quot;:&quot;/autosuggest&quot;}\" class=\"js-typeahead\">\n" +
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
				                 "</div>" );
	}

	@Test
	public void customEndPointMarkup() throws Exception {
		String endPoint = "/my-custom-endppoint";
		builder.endPoint( endPoint );
		NodeViewElement actual = builder.createElement( context );

		renderAndExpect( actual,
		                 String.format( "" +
				                                "<div data-autosuggest=\"{&quot;endPoint&quot;:&quot;%1$s&quot;}\" class=\"js-typeahead\">\n" +
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

	@Test
	public void customNotFoundTemplateWithViewElement() throws Exception {
		builder.notFoundTemplate( new AlertViewElement() );
		NodeViewElement actual = builder.createElement( context );

		renderAndExpect( actual,
		                 "" +
				                 "<div data-autosuggest=\"{&quot;endPoint&quot;:&quot;/autosuggest&quot;}\" class=\"js-typeahead\">\n" +
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

	@Test
	public void customNotFoundTemplateWithBuilder() throws Exception {
		builder.notFoundTemplate( new AlertViewElementBuilder() );
		NodeViewElement actual = builder.createElement( context );

		renderAndExpect( actual,
		                 "" +
				                 "<div data-autosuggest=\"{&quot;endPoint&quot;:&quot;/autosuggest&quot;}\" class=\"js-typeahead\">\n" +
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

	@Test
	public void customSuggestionTemplateWithViewElement() throws Exception {
		BootstrapUiFactoryImpl bootstrapUiFactory = new BootstrapUiFactoryImpl();
		NodeViewElementBuilder suggestionTemplate = bootstrapUiFactory.span().attribute(
				AutoSuggestFormElementBuilder.ATTRIBUTE_DATA_PROPERTY, "link" )
		                                                              .add( new GlyphIcon( GlyphIcon.ASTERISK ) );
		builder.suggestionTemplate( suggestionTemplate );
		builder.properties( "link" );
		NodeViewElement actual = builder.createElement( context );

		renderAndExpect( actual,
		                 "" +
				                 "<div data-autosuggest=\"{&quot;endPoint&quot;:&quot;/autosuggest&quot;}\" class=\"js-typeahead\">\n" +
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

	@Test
	public void customItemTemplateWithViewElement() throws Exception {
		NodeViewElement container = new NodeViewElementBuilder( "ul" )
				.css( CSS_PREFILL_TABLE )
				.build( new DefaultViewElementBuilderContext() );
		NodeViewElement item = new NodeViewElementBuilder( "ul" )
				.add( new TemplateViewElement( "th/test/customItemList :: item" ) )
				.build( new DefaultViewElementBuilderContext() );
		builder.itemTemplate( container, item );
		builder.properties( "name" );
		NodeViewElement actual = builder.createElement( context );

		renderAndExpect( actual,
		                 "" +
				                 "<div data-autosuggest=\"{&quot;endPoint&quot;:&quot;/autosuggest&quot;}\" class=\"js-typeahead\">\n" +
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