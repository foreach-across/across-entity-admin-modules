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

import com.foreach.across.modules.entity.views.request.EntityViewCommandValidator;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.WebDataBinder;

import java.util.EnumSet;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestDefaultValidationViewProcessor
{
	@Mock
	private WebDataBinder dataBinder;

	@Mock
	private EntityViewRequest viewRequest;

	@Mock
	private EntityViewCommandValidator validator;

	@InjectMocks
	private DefaultValidationViewProcessor processor;

	@Before
	public void setUp() throws Exception {
		when( viewRequest.getDataBinder() ).thenReturn( dataBinder );
	}

	@Test
	public void defaultSettings() {
		verifyMethods( EnumSet.of( HttpMethod.PATCH, HttpMethod.PUT, HttpMethod.POST, HttpMethod.DELETE ), new Object[0] );
	}

	@Test
	public void customMethods() {
		processor.setHttpMethods( EnumSet.of( HttpMethod.GET, HttpMethod.HEAD, HttpMethod.POST ) );
		verifyMethods( EnumSet.of( HttpMethod.GET, HttpMethod.HEAD, HttpMethod.POST ), new Object[0] );
	}

	@Test
	public void customValidationHints() {
		processor.setValidationHints( new Object[] { 65L, "test" } );
		verifyMethods( EnumSet.of( HttpMethod.PATCH, HttpMethod.PUT, HttpMethod.POST, HttpMethod.DELETE ), new Object[] { 65L, "test" } );
	}

	private void verifyMethods( EnumSet<HttpMethod> methods, Object[] validationHints ) {
		Stream.of( HttpMethod.values() ).forEach(
				method -> {
					boolean isAllowed = methods.contains( method );

					when( viewRequest.getHttpMethod() ).thenReturn( method );

					processor.initializeCommandObject( viewRequest, null, dataBinder );

					if ( isAllowed ) {
						verify( dataBinder ).setValidator( validator );
					}
					else {
						verifyNoMoreInteractions( dataBinder );
					}

					processor.preProcess( viewRequest, null );

					if ( isAllowed ) {
						verify( dataBinder ).validate( validationHints );
					}
					else {
						verifyNoMoreInteractions( dataBinder );
					}

					reset( dataBinder );
				} );

	}
}
