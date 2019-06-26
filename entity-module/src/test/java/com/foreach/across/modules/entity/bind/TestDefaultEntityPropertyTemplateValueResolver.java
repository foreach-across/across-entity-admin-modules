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

package com.foreach.across.modules.entity.bind;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 * @since 3.3.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestDefaultEntityPropertyTemplateValueResolver
{
	private EntityPropertyTemplateValueResolver resolver = new DefaultEntityPropertyTemplateValueResolver();

	@Mock
	private EntityPropertyDescriptor descriptor;

	@Test
	public void nullValueIfNoResolverAttribute() {
		assertThat( resolver.resolveTemplateValue( null, descriptor ) ).isNull();
	}

	@Test
	public void resolverAttributeIsUsedIfPresent() {
		EntityPropertyTemplateValueResolver other = mock( EntityPropertyTemplateValueResolver.class );
		when( other.resolveTemplateValue( any(), any() ) ).thenReturn( 123 );

		when( descriptor.getAttribute( EntityPropertyTemplateValueResolver.class ) ).thenReturn( other );

		assertThat( resolver.resolveTemplateValue( null, descriptor ) ).isEqualTo( 123 );
	}
}
