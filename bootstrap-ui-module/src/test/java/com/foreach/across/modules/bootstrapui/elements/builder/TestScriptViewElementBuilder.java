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
import com.foreach.across.modules.web.ui.elements.HtmlViewElements;
import org.junit.Test;
import org.springframework.http.MediaType;

import static com.foreach.across.modules.bootstrapui.elements.entry.BootstrapViewElements.bootstrap;

/**
 * @author Arne Vandamme
 * @since 2.1.1
 */
public class TestScriptViewElementBuilder extends AbstractBootstrapViewElementTest
{
	@Test
	public void emptyScript() {
		renderAndExpect( bootstrap.builders.script().build(), "<script></script>" );
	}

	@Test
	public void configuredScript() {
		renderAndExpect(
				bootstrap.builders.script()
				                  .type( MediaType.TEXT_HTML )
				                  .htmlId( "myscript" )
				                  .async( true )
				                  .source( "mysrc" )
				                  .defer( true )
				                  .charset( "utf-8" )
				                  .data( "value", "test" )
				                  .add( HtmlViewElements.html.unescapedText( "alert('hello');" ) )
				                  .build(),
				"<script id='myscript' data-value='test' type='text/html' async='async' defer='defer' src='mysrc' charset='utf-8'>" +
						"alert('hello');" +
						"</script>"
		);
	}
}
