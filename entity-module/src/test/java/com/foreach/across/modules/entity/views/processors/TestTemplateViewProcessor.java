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

package com.foreach.across.modules.entity.views.processors;

import com.foreach.across.modules.entity.views.EntityView;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class TestTemplateViewProcessor
{
	@Test(expected = NullPointerException.class)
	public void templateIsRequired() {
		new TemplateViewProcessor( null );
	}

	@Test
	public void equalsOnTemplate() {
		assertEquals(
				new TemplateViewProcessor( "one" ),
				new TemplateViewProcessor( "one" )
		);
		assertNotEquals(
				new TemplateViewProcessor( "one" ),
				new TemplateViewProcessor( "two" )
		);
	}

	@Test
	public void templateIsSetOnView() {
		EntityView entityView = mock( EntityView.class );
		new TemplateViewProcessor( "one" ).render( null, entityView );

		verify( entityView ).setTemplate( "one" );
	}
}
