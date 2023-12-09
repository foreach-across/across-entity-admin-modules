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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TestMessagePrefixingViewProcessor
{
	@Mock
	private ConfigurableEntityViewContext viewContext;

	@Mock
	private EntityMessageCodeResolver codeResolver;

	@BeforeEach
	public void setUp() throws Exception {
		when( viewContext.getMessageCodeResolver() ).thenReturn( codeResolver );
	}

	@Test
	public void defaultPrefix() {
		EntityMessageCodeResolver newResolver = mock( EntityMessageCodeResolver.class );
		when( codeResolver.prefixedResolver( "views" ) ).thenReturn( newResolver );

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

	@Test
	public void equalsIfSamePrefixes() {
		String[] prefixes = new String[] { "one", "two" };
		assertEquals(
				new MessagePrefixingViewProcessor( prefixes ),
				new MessagePrefixingViewProcessor( prefixes )
		);
		assertNotEquals(
				new MessagePrefixingViewProcessor( prefixes ),
				new MessagePrefixingViewProcessor( "one" )
		);
	}
}
