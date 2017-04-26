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

package com.foreach.across.modules.entity.views.request;

import lombok.val;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class TestEntityViewCommand
{
	private EntityViewCommand command;

	@Before
	public void setUp() throws Exception {
		command = new EntityViewCommand();
	}

	@Test
	public void defaultValues() {
		assertNull( command.getEntity() );
		assertFalse( command.holdsEntity() );
		assertNull( command.getExtension( "some-extension", String.class ) );
		assertFalse( command.hasExtension( "some-extension" ) );
	}

	@Test
	public void getTypedEntity() {
		command.setEntity( "myEntity" );
		assertTrue( command.holdsEntity() );
		String value = command.getEntity( String.class );
		assertEquals( "myEntity", value );
		command.setEntity( null );
		assertFalse( command.holdsEntity() );
	}

	@Test(expected = ClassCastException.class)
	public void exceptionWhenCoercingEntityToWrongType() {
		command.setEntity( 123L );
		command.getEntity( String.class );
	}

	@Test
	public void getExtension() {
		val map = new HashMap<>();
		command.addExtension( "my-extension", map );
		assertTrue( command.hasExtension( "my-extension" ) );
		assertSame( map, command.getExtension( "my-extension", Map.class ) );
		command.removeExtension( "my-extension" );
		assertFalse( command.hasExtension( "my-extension" ) );

		Map<String, Long> typedMap = new HashMap<>();
		command.addExtension( "myMap", typedMap );
		Map<String, Long> value = command.getExtension( "myMap" );
		assertSame( value, typedMap );
	}

	@Test(expected = ClassCastException.class)
	public void exceptionWhenCoercingExtensionToWrongType() {
		val map = new HashMap<>();
		command.addExtension( "my-extension", map );
		command.getExtension( "my-extension", HashSet.class );
	}
}
