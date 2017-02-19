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

import com.foreach.across.modules.bootstrapui.elements.Style;
import org.junit.Test;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.foreach.across.modules.entity.views.processors.GlobalPageFeedbackViewProcessor.addFeedbackMessage;
import static com.foreach.across.modules.entity.views.processors.GlobalPageFeedbackViewProcessor.decodeFeedbackMessages;
import static org.junit.Assert.assertEquals;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class TestGlobalPageFeedbackViewProcessor
{
	@Test
	public void addFeedbackMessages() {
		assertEquals( "danger:my.message", addFeedbackMessage( null, Style.DANGER, "my.message" ) );
		assertEquals( "info:some.other.message", addFeedbackMessage( "", Style.INFO, "some.other.message" ) );
		assertEquals(
				"danger:my.message,info:some.other.message",
				addFeedbackMessage( "danger:my.message", Style.INFO, "some.other.message" )
		);
	}

	@Test
	public void decodingFeedback() {
		assertEquals( Collections.emptyMap(), decodeFeedbackMessages( null ) );
		assertEquals( Collections.emptyMap(), decodeFeedbackMessages( "" ) );
		assertEquals(
				Collections.singletonMap( "my.message", new Style( "danger" ) ),
				decodeFeedbackMessages( "danger:my.message" )
		);

		Map<String, Style> feedback = new LinkedHashMap<>();
		feedback.put( "my.message", new Style( "danger" ) );
		feedback.put( "some.other.message", new Style( "info" ) );
		assertEquals( feedback, decodeFeedbackMessages( "danger:my.message,info:some.other.message" ) );
	}
}
