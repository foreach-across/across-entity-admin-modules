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
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.foreach.across.modules.entity.views.processors.GlobalPageFeedbackViewProcessor.addFeedbackMessage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class TestGlobalPageFeedbackViewProcessor
{
	@Test
	public void addFeedbackMessages() {
		Map<String, Style> feedback = addFeedbackMessage( null, Style.DANGER, "my message" );
		assertMapContainsPair( feedback, "my message", Style.DANGER );
		feedback = addFeedbackMessage( null, Style.DANGER, "my message" );
		assertMapContainsPair( feedback, "my message", Style.DANGER );
		feedback = addFeedbackMessage( new HashMap<>(), Style.INFO, "some other message" );
		assertMapContainsPair( feedback, "some other message", Style.INFO );
		Style somestyle = new Style( "somestyle" );
		feedback = addFeedbackMessage( Collections.singletonMap( "my message", Style.DANGER ), somestyle,
		                               "some other message" );
		assertMapContainsPair( feedback, "my message", Style.DANGER );
		assertMapContainsPair( feedback, "some other message", somestyle );
	}

	private void assertMapContainsPair( Map<String, Style> map, String key, Style value ) {
		assertTrue( map.containsKey( key ) );
		assertEquals( value, map.get( key ) );
	}

}
