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

package it.com.foreach.across.modules.entity.utils;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import lombok.RequiredArgsConstructor;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
@RequiredArgsConstructor
public class EntityPropertyDescriptorVerifier
{
	private final EntityPropertyDescriptor descriptor;

	public EntityPropertyDescriptorVerifier hasAttribute( String attributeName, Object attributeValue ) {
		assertThat( descriptor.getAttribute( attributeName ) ).isEqualTo( attributeValue );
		return this;
	}

	public EntityPropertyDescriptorVerifier doesNotHaveAttribute( String attributeName ) {
		assertThat( descriptor.hasAttribute( attributeName ) ).isFalse();
		return this;
	}
}
