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

package com.foreach.across.modules.entity.views.bootstrapui.processors.element;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
public class TestEnumValueTextPostProcessor
{
	@Mock
	private EntityPropertyDescriptor descriptor;

	@Mock
	private ViewElementBuilderContext builderContext;

	@Mock
	private EntityMessageCodeResolver codeResolver;

	private EnumValueTextPostProcessor processor;

	@BeforeEach
	public void setUp() throws Exception {
		processor = new EnumValueTextPostProcessor( descriptor, Counter.class );
	}

	@Test
	public void nullValueNotSet() {
		assertThat( processor.print( null, Locale.CANADA_FRENCH, builderContext ) ).isNull();
	}

	@Test
	public void valueNameIfNoCodeResolver() {
		assertThat( processor.print( Counter.ONE, Locale.CANADA_FRENCH, builderContext ) ).isEqualTo( "ONE" );
	}

	@Test
	public void codeResolvedIfResolver() {
		when( builderContext.getAttribute( EntityMessageCodeResolver.class ) ).thenReturn( codeResolver );
		when( codeResolver.getMessageWithFallback( "enums.Counter.ONE", "One", Locale.CANADA_FRENCH ) ).thenReturn( "één" );
		assertThat( processor.print( Counter.ONE, Locale.CANADA_FRENCH, builderContext ) ).isEqualTo( "één" );
	}

	enum Counter
	{
		ONE
	}
}
