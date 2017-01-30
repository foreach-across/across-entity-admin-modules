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
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiFactoryImpl;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import org.junit.Before;
import org.junit.Test;

/**
 * @author: sander
 * @date: 30/01/2017
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
				                 "      <div class=\"js-typeahead-empty-template empty-message\">Not Found</div>\n" +
				                 "   </div>\n" +
				                 "   <input type=\"text\" class=\"js-typeahead-input form-control\"/>\n" +
				                 "   <table class=\"js-typeahead-prefill table\"></table>\n" +
				                 "</div>" );
	}
}