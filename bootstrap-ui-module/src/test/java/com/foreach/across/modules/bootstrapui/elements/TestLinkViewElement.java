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

package com.foreach.across.modules.bootstrapui.elements;

import com.foreach.across.modules.web.ui.elements.TextViewElement;
import org.junit.Test;

/**
 * @author Arne Vandamme
 * @since 1.0.0
 */
public class TestLinkViewElement extends AbstractBootstrapViewElementTest
{
	@Test
	public void emptyLink() {
		renderAndExpect( new LinkViewElement(), "<a href='#'></a>" );
	}

	@Test
	public void simpleLink() {
		LinkViewElement link = new LinkViewElement();
		link.setText( "link text" );
		link.setUrl( "http://test" );
		link.setTitle( "link title" );

		renderAndExpect( link, "<a href='http://test' title='link title'>link text</a>" );
	}

	@Test
	public void additionalContent() {
		LinkViewElement link = new LinkViewElement();
		link.setAttribute( "target", "_blank" );
		link.setText( "that is " );
		link.addChild( new TextViewElement( "my text" ) );
		link.setText( "this is " );

		renderAndExpect( link, "<a href='#' target='_blank'>this is my text</a>" );
	}
}
