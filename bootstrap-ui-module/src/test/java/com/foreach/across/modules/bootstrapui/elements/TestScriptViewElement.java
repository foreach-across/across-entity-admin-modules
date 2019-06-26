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

import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.XmlExpectationsHelper;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arne Vandamme
 * @since 2.1.1
 */
public class TestScriptViewElement extends AbstractBootstrapViewElementTest
{
	@Test
	public void simpleElement() {
		ScriptViewElement script = new ScriptViewElement();
		assertThat( render( script ) ).isEqualTo( "<script></script>" );

		script.setType( "application/javascript" );
		script.setSource( "my-source" );
		renderAndExpect( script, "<script type='application/javascript' src='my-source'></script>" );

		assertThat( script.getType().toString() ).isEqualTo( "application/javascript" );

		script.setAsync( true );
		script.setDefer( true );
		script.setCharset( "utf-8" );
		renderAndExpect(
				script,
				"<script type='application/javascript' src='my-source' charset='utf-8' async='async' defer='defer'></script>"
		);

		script.addChild( TextViewElement.html( "alert('hello');" ) );
		renderAndExpect(
				script,
				"<script type='application/javascript' src='my-source' charset='utf-8' async='async' defer='defer'>" +
						"alert('hello');" +
						"</script>"
		);
	}

	@Test
	public void nestedScript() {
		ScriptViewElement script = new ScriptViewElement();
		script.setType( MediaType.TEXT_HTML );

		ScriptViewElement nested = new ScriptViewElement();
		nested.setType( "application/javascript" );
		nested.addChild( TextViewElement.html( "alert('hello');" ) );

		NodeViewElement div = new NodeViewElement( "div" );
		div.addChild( nested );
		script.addChild( div );

		String output = render( script );
		Elements scriptTags = Jsoup.parseBodyFragment( output ).getElementsByTag( "script" );
		assertThat( scriptTags ).hasSize( 2 );
		String refId = scriptTags.get( 1 ).attr( "id" );
		assertThat( refId ).isNotEmpty();

		expectContent(
				output,
				"<script type='text/html'>" +
						"<div><i data-bum-ref-id='" + refId + "' style='display: none; visibility: hidden;'></i></div>" +
						"</script>" +
						"<script type='application/javascript' id='" + refId + "'>" +
						"alert('hello');" +
						"</script>"
		);

		// change the referring tag element
		nested.setRefTagName( "span" );
		output = render( script );
		refId = Jsoup.parseBodyFragment( output ).getElementsByTag( "script" ).get( 1 ).attr( "id" );

		expectContent(
				output,
				"<script type='text/html'>" +
						"<div><span data-bum-ref-id='" + refId + "' style='display: none; visibility: hidden;'></span></div>" +
						"</script>" +
						"<script type='application/javascript' id='" + refId + "'>" +
						"alert('hello');" +
						"</script>"
		);

		// don't write a referring tag at all
		nested.setRefTagName( ScriptViewElement.NO_REF_TAG );
		output = render( script );
		refId = Jsoup.parseBodyFragment( output ).getElementsByTag( "script" ).get( 1 ).attr( "id" );

		expectContent(
				output,
				"<script type='text/html'>" +
						"<div></div>" +
						"</script>" +
						"<script type='application/javascript' id='" + refId + "'>" +
						"alert('hello');" +
						"</script>"
		);
	}

	private void expectContent( String actual, String expected ) {
		String pattern = "<?xml version=\"1.0\"?><root xmlns:across='https://across.dev'>%s</root>";

		try {
			( new XmlExpectationsHelper() ).assertXmlEqual( String.format( pattern, expected ), String.format( pattern, actual ) );
		}
		catch ( Exception | AssertionError e ) {
			throw new AssertionError( "Unexpected content:\n" + actual, e );
		}
	}
}
