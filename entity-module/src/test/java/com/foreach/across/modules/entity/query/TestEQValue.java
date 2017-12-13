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

package com.foreach.across.modules.entity.query;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class TestEQValue
{
	@Test(expected = NullPointerException.class)
	public void nullValueNotAllowed() {
		new EQValue( null );
	}

	@Test
	public void value() {
		EQValue v = new EQValue( "my string" );
		assertEquals( "my string", v.getValue() );
	}

	@Test
	public void equalIfSameValue() {
		assertEquals( new EQValue( "some value" ), new EQValue( "some value" ) );
	}

	@Test
	public void notEqualIfDifferentValue() {
		assertNotEquals( new EQValue( "some value" ), new EQValue( "other value" ) );
	}

	@Test
	public void toStringSimplyWritesVale() {
		assertEquals( "my value", new EQValue( "my value" ).toString() );
	}
}
