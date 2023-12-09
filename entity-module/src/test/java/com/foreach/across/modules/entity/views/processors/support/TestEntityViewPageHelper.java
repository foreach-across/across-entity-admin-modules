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

package com.foreach.across.modules.entity.views.processors.support;

import com.foreach.across.modules.bootstrapui.elements.Style;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

import static com.foreach.across.modules.entity.views.processors.GlobalPageFeedbackViewProcessor.FEEDBACK_ATTRIBUTE_KEY;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@ExtendWith(MockitoExtension.class)
public class TestEntityViewPageHelper
{
	@Mock
	private EntityMessages entityMessages;

	@Mock
	private EntityViewRequest entityViewRequest;

	@InjectMocks
	private EntityViewPageHelper pageHelper;

	private Map<String, Object> redirectAttributes;
	private Map<String, Object> flashAttributes;

	@SuppressWarnings("unchecked")
	@BeforeEach
	public void setUp() throws Exception {
		redirectAttributes = new HashMap<>();
		flashAttributes = new HashMap<>();

		RedirectAttributes ra = mock( RedirectAttributes.class );
		when( (Map<String, Object>) ra.getFlashAttributes() ).thenReturn( flashAttributes );

		when( entityViewRequest.getRedirectAttributes() ).thenReturn( ra );

		EntityViewContext entityViewContext = mock( EntityViewContext.class );
		when( entityViewRequest.getEntityViewContext() ).thenReturn( entityViewContext );
		when( entityViewContext.getEntityMessages() ).thenReturn( entityMessages );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void addGlobalFeedbackResolvesMessageCode() {
		when( entityMessages.withNameSingular( eq( "my.message" ), any() ) ).thenReturn( "My message" );
		when( entityMessages.withNameSingular( eq( "other.message" ), any() ) ).thenReturn( "Other message" );
		assertNull( flashAttributes.get( FEEDBACK_ATTRIBUTE_KEY ) );

		pageHelper.addGlobalFeedbackAfterRedirect( entityViewRequest, Style.DANGER, "my.message" );
		Map<String, Style> attributes = (Map<String, Style>) flashAttributes.get( FEEDBACK_ATTRIBUTE_KEY );
		assertEquals( 1, attributes.size() );
		assertMapContainsPair( attributes, "My message", Style.DANGER );

		pageHelper.addGlobalFeedbackAfterRedirect( entityViewRequest, Style.INFO, "other.message" );
		attributes = (Map<String, Style>) flashAttributes.get( FEEDBACK_ATTRIBUTE_KEY );
		assertEquals( 2, attributes.size() );
		assertMapContainsPair( attributes, "My message", Style.DANGER );
		assertMapContainsPair( attributes, "Other message", Style.INFO );

		assertTrue( redirectAttributes.isEmpty() );
	}

	private void assertMapContainsPair( Map<String, Style> map, String key, Style value ) {
		assertTrue( map.containsKey( key ) );
		assertEquals( value, map.get( key ) );
	}
}
