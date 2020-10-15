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

package com.foreach.across.modules.entity.registry.properties;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arne Vandamme
 * @since 3.2.0
 */
public class TestEntityPropertyValue
{
	@Test
	public void isModified() {
		assertThat( new EntityPropertyValue<>( 1, 1, false ).isModified() ).isFalse();
		assertThat( new EntityPropertyValue<>( 1, 2, false ).isModified() ).isTrue();
		assertThat( new EntityPropertyValue<>( 1, 1, true ).isModified() ).isTrue();

		assertThat( new EntityPropertyValue<>( null, null, false ).isModified() ).isFalse();
		assertThat( new EntityPropertyValue<>( null, null, true ).isModified() ).isTrue();
		assertThat( new EntityPropertyValue<>( 1, null, false ).isModified() ).isTrue();
		assertThat( new EntityPropertyValue<>( null, 1, false ).isModified() ).isTrue();
	}
}
