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

import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TestDelegatingEntityFetchingViewProcessor
{
	@Mock
	private EntityViewRequest viewRequest;

	@Mock
	private EntityViewContext viewContext;

	@Mock
	private Pageable pageable;

	@Mock
	private Page<Object> items;

	private DelegatingEntityFetchingViewProcessor processor;

	@BeforeEach
	public void setUp() throws Exception {
		processor = new DelegatingEntityFetchingViewProcessor();
		when( viewRequest.getEntityViewContext() ).thenReturn( viewContext );
	}

	@Test
	public void exceptionThrownIfNoDelegateSet() {
		Assertions.assertThrows( IllegalStateException.class, () -> {
			processor.fetchItems( viewRequest, null, pageable );
		} );
	}

	@Test
	public void functionAsDelegate() {
		processor.setDelegate( p -> {
			assertSame( pageable, p );
			return items;
		} );

		assertSame( items, processor.fetchItems( viewRequest, null, pageable ) );
	}

	@Test
	public void biFunctionAsDelegate() {
		processor.setDelegate( ( ctx, p ) -> {
			assertSame( viewContext, ctx );
			assertSame( pageable, p );
			return items;
		} );

		assertSame( items, processor.fetchItems( viewRequest, null, pageable ) );
	}
}
