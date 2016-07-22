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

import com.foreach.across.modules.bootstrapui.elements.LinkViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderFactory;
import com.foreach.across.test.support.AbstractViewElementBuilderTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Arne Vandamme
 * @since 1.0.0
 */
public class TestLinkViewElementBuilder extends AbstractViewElementBuilderTest<LinkViewElementBuilder, LinkViewElement>
{
	@Override
	protected LinkViewElementBuilder createBuilder( ViewElementBuilderFactory builderFactory ) {
		return new LinkViewElementBuilder();
	}

	@Test
	public void defaultAttributes() {
		build();

		assertEquals( "#", element.getUrl() );
		assertNull( element.getText() );
		assertNull( element.getTitle() );
	}

	@Test
	public void customValues() {
		builder.text( "1" ).url( "2" ).title( "3" );
		build();

		assertEquals( "1", element.getText() );
		assertEquals( "2", element.getUrl() );
		assertEquals( "3", element.getTitle() );
	}

}
