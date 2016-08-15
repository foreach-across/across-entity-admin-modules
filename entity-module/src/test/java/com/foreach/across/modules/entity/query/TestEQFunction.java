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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class TestEQFunction
{
	@Test(expected = IllegalArgumentException.class)
	public void nullNameNotAllowed() {
		new EQFunction( null );
	}

	@Test(expected = IllegalArgumentException.class)
	public void nullArgumentsNotAllowed() {
		new EQFunction( "myFunction", null );
	}

	@Test
	public void properties() {
		EQFunction function = new EQFunction( "myFunction" );
		assertEquals( "myFunction", function.getName() );
		assertTrue( function.getArguments().isEmpty() );

		List<Object> values = Arrays.asList( "one", 123 );
		function = new EQFunction( "otherFunction", values );
		assertEquals( "otherFunction", function.getName() );
		assertEquals( values, function.getArguments() );
	}

	@Test
	public void equalIfSameNameAndArguments() {
		assertEquals( new EQFunction( "myFunc" ), new EQFunction( "myFunc" ) );
		assertEquals(
				new EQFunction( "otherFunc", Arrays.asList( "one", 123 ) ),
				new EQFunction( "otherFunc", Arrays.asList( "one", 123 ) )
		);
	}

	@Test
	public void notEqualIfDifferentNamdOrArguments() {
		assertNotEquals( new EQFunction( "myFunc" ), new EQFunction( "otherFunc" ) );
		assertNotEquals(
				new EQFunction( "otherFunc", Arrays.asList( "one", 123 ) ),
				new EQFunction( "otherFunc", Arrays.asList( "one", 456 ) )
		);
	}

	@Test
	public void changesToOriginalCollectionHaveNoImpact() {
		List<Object> values = new ArrayList<>();

		EQFunction f = new EQFunction( "myFunc", values );
		values.add( "test" );
		assertFalse( f.getArguments().contains( "test" ) );
	}

	@Test
	public void toStringWritesWrappedFunction() {
		assertEquals( "someFunction()", new EQFunction( "someFunction" ).toString() );
		assertEquals(
				"anotherFunc(1,'test',2)",
				new EQFunction( "anotherFunc",
				                Arrays.asList( 1, new EQString( "test" ), new EQValue( "2" ) ) ).toString()
		);
	}
}
