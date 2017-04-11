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

package com.foreach.across.modules.entity.registry;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class TestEntityConfiguration
{
	@Test
	public void hasEntityModel() {
		MutableEntityConfiguration<BigDecimal> config = new EntityConfigurationImpl<>( BigDecimal.class );
		assertFalse( config.hasEntityModel() );

		config.setEntityModel( mock( EntityModel.class ) );
		assertTrue( config.hasEntityModel() );
	}

	@Test
	public void defaultNameAndDisplayName() {
		MutableEntityConfiguration<BigDecimal> config = new EntityConfigurationImpl<>( BigDecimal.class );

		assertEquals( "bigDecimal", config.getName() );
		assertEquals( "Big decimal", config.getDisplayName() );
	}

	@Test
	public void configuredNameAndDefaultDisplayName() {
		MutableEntityConfiguration<BigDecimal> config = new EntityConfigurationImpl<>( "someOtherName", BigDecimal.class );

		assertEquals( "someOtherName", config.getName() );
		assertEquals( "Some other name", config.getDisplayName() );
	}

	@Test
	public void customDisplayName() {
		MutableEntityConfiguration<BigDecimal> config = new EntityConfigurationImpl<>( "someOtherName", BigDecimal.class );
		config.setDisplayName( "Display name" );

		assertEquals( "someOtherName", config.getName() );
		assertEquals( "Display name", config.getDisplayName() );
	}
}
