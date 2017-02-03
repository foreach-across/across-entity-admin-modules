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

import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.context.ConfigurableEntityViewContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestMessagePrefixingViewProcessor
{
	@Mock
	private ConfigurableEntityViewContext viewContext;

	@Mock
	private EntityMessageCodeResolver codeResolver;

	@Before
	public void setUp() throws Exception {
		when( viewContext.getMessageCodeResolver() ).thenReturn( codeResolver );
	}

	@Test
	public void defaultPrefix() {
		EntityMessageCodeResolver newResolver = mock( EntityMessageCodeResolver.class );
		when( codeResolver.prefixedResolver( "entityViews" ) ).thenReturn( newResolver );

		new MessagePrefixingViewProcessor().prepareEntityViewContext( viewContext );
		verify( viewContext ).setMessageCodeResolver( newResolver );
		verify( viewContext ).setEntityMessages( any() );
	}

	@Test
	public void customPrefixes() {
		EntityMessageCodeResolver newResolver = mock( EntityMessageCodeResolver.class );
		when( codeResolver.prefixedResolver( "other", "secondOther" ) ).thenReturn( newResolver );

		new MessagePrefixingViewProcessor( "other", "secondOther" ).prepareEntityViewContext( viewContext );
		verify( viewContext ).setMessageCodeResolver( newResolver );
		verify( viewContext ).setEntityMessages( any() );
	}
}
