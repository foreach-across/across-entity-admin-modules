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
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

import static com.foreach.across.modules.entity.views.processors.GlobalPageFeedbackViewProcessor.FEEDBACK_ATTRIBUTE_KEY;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestEntityViewPageHelper
{
	@Mock
	private EntityViewRequest entityViewRequest;

	@InjectMocks
	private EntityViewPageHelper pageHelper;

	private Map<String, Object> redirectAttributes;
	private Map<String, Object> flashAttributes;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		redirectAttributes = new HashMap<>();
		flashAttributes = new HashMap<>();

		RedirectAttributes ra = mock( RedirectAttributes.class );
		when( ra.asMap() ).thenReturn( redirectAttributes );
		when( (Map<String, Object>) ra.getFlashAttributes() ).thenReturn( flashAttributes );

		when( entityViewRequest.getRedirectAttributes() ).thenReturn( ra );
	}

	@Test
	public void addGlobalFeedbackUsesFlashByDefault() {
		assertNull( flashAttributes.get( FEEDBACK_ATTRIBUTE_KEY ) );
		pageHelper.addGlobalFeedbackAfterRedirect( entityViewRequest, Style.DANGER, "my.message" );
		assertEquals( "danger:my.message", flashAttributes.get( FEEDBACK_ATTRIBUTE_KEY ) );
		pageHelper.addGlobalFeedbackAfterRedirect( entityViewRequest, Style.INFO, "other.message" );
		assertEquals( "danger:my.message,info:other.message", flashAttributes.get( FEEDBACK_ATTRIBUTE_KEY ) );

		assertTrue( redirectAttributes.isEmpty() );
	}

	@Test
	public void addGlobalFeedbackAsRedirectAttribute() {
		pageHelper.setUseFlashAttributesForRedirect( false );

		assertNull( redirectAttributes.get( FEEDBACK_ATTRIBUTE_KEY ) );
		pageHelper.addGlobalFeedbackAfterRedirect( entityViewRequest, Style.DANGER, "my.message" );
		assertEquals( "danger:my.message", redirectAttributes.get( FEEDBACK_ATTRIBUTE_KEY ) );
		pageHelper.addGlobalFeedbackAfterRedirect( entityViewRequest, Style.INFO, "other.message" );
		assertEquals( "danger:my.message,info:other.message", redirectAttributes.get( FEEDBACK_ATTRIBUTE_KEY ) );

		assertTrue( flashAttributes.isEmpty() );
	}
}
