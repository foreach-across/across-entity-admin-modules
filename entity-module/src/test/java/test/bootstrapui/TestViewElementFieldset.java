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

package test.bootstrapui;

import com.foreach.across.modules.bootstrapui.elements.Style;
import com.foreach.across.modules.entity.views.bootstrapui.elements.ViewElementFieldset;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.test.support.AbstractViewElementTemplateTest;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.foreach.across.modules.web.ui.elements.TextViewElement.html;
import static com.foreach.across.modules.web.ui.elements.TextViewElement.text;

/**
 * @author Arne Vandamme
 * @since 3.2.0
 */
public class TestViewElementFieldset extends AbstractViewElementTemplateTest
{
	private ViewElementFieldset fieldset = new ViewElementFieldset();

	@Before
	public void addContent() {
		fieldset.getTitle().addChild( text( "title" ) );
		fieldset.getHeader().addChild( text( "header" ) );
		fieldset.getBody().addChild( text( "body" ) );
		fieldset.getFooter().addChild( text( "footer" ) );
	}

	@Test
	public void byDefaultFormFieldsetIsRendered() {
		renderAndExpect( fieldset, "<fieldset class='element-fieldset element-fieldset-fieldset'>" +
				"<legend class='element-fieldset-title'>title</legend>" +
				"headerbodyfooter" +
				"</fieldset>" );
	}

	@Test
	public void customTemplate() {
		val function = ViewElementFieldset.template( fields -> {
			NodeViewElement div = new NodeViewElement( "div" );
			NodeViewElement h1 = new NodeViewElement( "h1" );
			h1.addChild( fields.getTitle() );
			div.addChild( h1 );
			div.addChild( fields.getBody() );
			div.addChild( html( "<footer>fixed footer</footer>" ) );
			return div;
		} );

		fieldset.setTemplate( function );

		renderAndExpect( fieldset, "<div><h1>title</h1>body<footer>fixed footer</footer></div>" );
	}

	@Test
	public void bodyOnlyTemplate() {
		fieldset.setTemplate( ViewElementFieldset.TEMPLATE_BODY_ONLY );
		renderAndExpect( fieldset, "body" );
	}

	@Test
	public void sectionHeadingTemplates() {
		Map<Function<ViewElementFieldset, ? extends ViewElement>, String> testCases = new HashMap<>();
		testCases.put( ViewElementFieldset.TEMPLATE_SECTION_H1, "h1" );
		testCases.put( ViewElementFieldset.TEMPLATE_SECTION_H2, "h2" );
		testCases.put( ViewElementFieldset.TEMPLATE_SECTION_H3, "h3" );

		testCases.forEach(
				( template, heading ) -> {
					String expected = StringUtils.replace(
							"<section class='element-fieldset element-fieldset-section-h1'>" +
									"<h1 class='element-fieldset-title'>title</h1>" +
									"<div class='element-fieldset-content'>headerbodyfooter</div>" +
									"</section>", "h1", heading );
					fieldset.setTemplate( template );
					renderAndExpect( fieldset, expected );
				}
		);
	}

	@Test
	public void panelTemplates() {
		Map<Function<ViewElementFieldset, ? extends ViewElement>, String> testCases = new HashMap<>();
		testCases.put( ViewElementFieldset.TEMPLATE_PANEL_DEFAULT, "panel-default" );
		testCases.put( ViewElementFieldset.TEMPLATE_PANEL_DANGER, "panel-danger" );
		testCases.put( ViewElementFieldset.TEMPLATE_PANEL_PRIMARY, "panel-primary" );
		testCases.put( ViewElementFieldset.TEMPLATE_PANEL_INFO, "panel-info" );
		testCases.put( ViewElementFieldset.TEMPLATE_PANEL_SUCCESS, "panel-success" );
		testCases.put( ViewElementFieldset.TEMPLATE_PANEL_WARNING, "panel-warning" );

		testCases.forEach(
				( template, panelStyle ) -> {
					String expected = StringUtils.replace(
							"<div class='element-fieldset element-fieldset-panel-default panel panel-default'>" +
									"<div class='element-fieldset-title panel-heading'>title</div>" +
									"<div class='element-fieldset-content panel-body'>" +
									"<div class='element-fieldset-header'>header</div>" +
									"<div class='element-fieldset-body'>body</div>" +
									"</div>" +
									"<div class='element-fieldset-footer panel-footer'>footer</div>" +
									"</div>", "panel-default", panelStyle );
					fieldset.setTemplate( template );
					renderAndExpect( fieldset, expected );
				}
		);
	}

	@Test
	public void customPanelTemplateFormat() {
		fieldset.setTemplate( ViewElementFieldset.panelTemplate( "x", Style.DANGER ) );
		renderAndExpect( fieldset, "<div class='element-fieldset x card panel-danger'>" +
				"<div class='element-fieldset-title panel-heading'>title</div>" +
				"<div class='element-fieldset-content panel-body'>" +
				"<div class='element-fieldset-header'>header</div>" +
				"<div class='element-fieldset-body'>body</div>" +
				"</div>" +
				"<div class='element-fieldset-footer panel-footer'>footer</div>" +
				"</div>" );

		fieldset.setTemplate( ViewElementFieldset.panelTemplate( "y", Style.PRIMARY ) );
		fieldset.getFooter().clearChildren();
		renderAndExpect( fieldset, "<div class='element-fieldset y panel panel-primary'>" +
				"<div class='element-fieldset-title panel-heading'>title</div>" +
				"<div class='element-fieldset-content panel-body'>" +
				"<div class='element-fieldset-header'>header</div>" +
				"<div class='element-fieldset-body'>body</div>" +
				"</div>" +
				"</div>" );

		fieldset.getTitle().clearChildren();
		renderAndExpect( fieldset, "<div class='element-fieldset y panel panel-primary'>" +
				"<div class='element-fieldset-content panel-body'>" +
				"<div class='element-fieldset-header'>header</div>" +
				"<div class='element-fieldset-body'>body</div>" +
				"</div>" +
				"</div>" );
	}

	@Test
	public void customTemplateFormat() {
		fieldset.setTemplate( ViewElementFieldset.structureTemplate( "x", "section" ) );
		renderAndExpect( fieldset, "<section class='element-fieldset x'>titleheaderbodyfooter</section>" );

		fieldset.setTemplate( ViewElementFieldset.structureTemplate( "y", "section/h3" ) );
		renderAndExpect( fieldset, "<section class='element-fieldset y'><h3 class='element-fieldset-title'>title</h3>headerbodyfooter</section>" );

		fieldset.setTemplate( ViewElementFieldset.structureTemplate( "z", "section/h3/content" ) );
		renderAndExpect( fieldset, "<section class='element-fieldset z'>" +
				"<h3 class='element-fieldset-title'>title</h3>" +
				"<content class='element-fieldset-content'>headerbodyfooter</content>" +
				"</section>" );

		fieldset.setTemplate( ViewElementFieldset.structureTemplate( "u", "section/h3/head/main/foot" ) );
		renderAndExpect( fieldset, "<section class='element-fieldset u'>" +
				"<h3 class='element-fieldset-title'>title</h3>" +
				"<head class='element-fieldset-header'>header</head>" +
				"<main class='element-fieldset-body'>body</main>" +
				"<foot class='element-fieldset-footer'>footer</foot>" +
				"</section>" );

		fieldset.setTemplate( ViewElementFieldset.structureTemplate( "v", "section/h3/content/head/main/foot" ) );
		renderAndExpect( fieldset, "<section class='element-fieldset v'>" +
				"<h3 class='element-fieldset-title'>title</h3>" +
				"<content class='element-fieldset-content'>" +
				"<head class='element-fieldset-header'>header</head>" +
				"<main class='element-fieldset-body'>body</main>" +
				"<foot class='element-fieldset-footer'>footer</foot>" +
				"</content>" +
				"</section>" );
	}
}
