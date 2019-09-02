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

package com.foreach.across.modules.adminweb.ui;

import com.foreach.across.modules.web.ui.elements.TextViewElement;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class TestPageContentStructure extends AbstractBootstrapViewElementTest
{
	private PageContentStructure page;

	@Before
	public void setUp() throws Exception {
		page = new PageContentStructure();
	}

	@Test
	public void nothingAddedRendersNoOutput() {
		renderAndExpect( page, "<div class='pcs'></div>" );

		page.setRenderAsTabs( true );
		renderAndExpect( page, "<div class='pcs'></div>" );
	}

	@Test
	public void simpleContentSection() {
		page.addChild( new TextViewElement( "some content" ) );

		assertFalse( page.isRenderAsTabs() );
		renderAndExpect(
				page,
				"<div class='pcs'><section class='pcs-body-section'>some content</section></div>"
		);

		page.setRenderAsTabs( true );
		renderAndExpect(
				page, "" +
						"<div class='pcs'>" +
						"<div class='mb-3 tabbable filled'>" +
						"<section class='pcs-body-section'>" +
						"<div class='tab-content px-3'>" +
						"<div class='tab-pane active'>" +
						"some content" +
						"</div></div></section></div>" +
						"</div>"
		);
	}

	@Test
	public void navSectionAndContent() {
		page.addChild( new TextViewElement( "some content" ) );
		page.withNav( nav -> nav.addChild( new TextViewElement( "some nav" ) ) );

		renderAndExpect(
				page,
				"<div class='pcs'><nav class='pcs-nav'>some nav</nav>" +
						"<section class='pcs-body-section'>some content</section></div>"
		);

		page.setRenderAsTabs( true );
		renderAndExpect(
				page,
				"<div class='pcs'>" +
						"<div class='mb-3 tabbable filled'>" +
						"<nav class='pcs-nav mb-3'>some nav</nav>" +
						"<section class='pcs-body-section'>" +
						"<div class='tab-content px-3'>" +
						"<div class='tab-pane active'>" +
						"some content" +
						"</div></div></section></div>" +
						"</div>"
		);
	}

	@Test
	public void simplePageTitle() {
		assertNull( page.getPageTitle() );
		page.setPageTitle( "Simple page title" );
		assertEquals( "Simple page title", page.getPageTitle() );

		renderAndExpect(
				page,
				"<div class='pcs'><header class='pcs-header'>" +
						"<h3 class='page-header mb-4 pb-2 border-bottom'>Simple page title <small class=\"text-muted\"></small></h3>" +
						"</header></div>"
		);
	}

	@Test
	public void onlyPageTitleSubText() {
		page.withPageTitleSubText( n -> n.addChild( new TextViewElement( "some action" ) ) );

		renderAndExpect(
				page,
				"<div class='pcs'><header class='pcs-header'>" +
						"<h3 class='page-header mb-4 pb-2 border-bottom'><small class=\"text-muted\">some action</small></h3>" +
						"</header></div>"
		);
	}

	@Test
	public void pageTitleWithSubText() {
		page.withPageTitleSubText( n -> n.addChild( new TextViewElement( "some action" ) ) );
		page.setPageTitle( "Simple page title" );

		renderAndExpect(
				page,
				"<div class='pcs'><header class='pcs-header'>" +
						"<h3 class='page-header mb-4 pb-2 border-bottom'>Simple page title <small class=\"text-muted\">some action</small></h3>" +
						"</header></div>"
		);
	}
}
