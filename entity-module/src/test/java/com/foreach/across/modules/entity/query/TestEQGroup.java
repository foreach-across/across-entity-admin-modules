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

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class TestEQGroup
{
	@Test(expected = IllegalArgumentException.class)
	public void nullValueNotAllowed() {
		new EQGroup( (EQType[]) null );
	}

	@Test
	public void values() {
		EQType[] values = new EQType[] { new EQString( "one" ), new EQValue( "123" ) };

		EQGroup g = new EQGroup( values );
		assertArrayEquals( values, g.getValues() );
	}

	@Test
	public void equalIfSameValue() {
		assertEquals( new EQGroup( Arrays.asList( new EQString( "one" ), new EQValue( "123" ) ) ),
		              new EQGroup( Arrays.asList( new EQString( "one" ), new EQValue( "123" ) ) ) );
	}

	@Test
	public void notEqualIfDifferentValue() {
		assertNotEquals( new EQGroup( Arrays.asList( new EQString( "one" ), new EQValue( "123" ) ) ),
		                 new EQGroup( new EQString( "one" ), new EQValue( "456" ) ) );
	}

	@Test
	public void changesToOriginalCollectionHaveNoImpact() {
		List<EQType> values = new ArrayList<>();

		EQGroup g = new EQGroup( values );
		values.add( new EQString( "test" ) );
		assertFalse( ArrayUtils.contains( g.getValues(), new EQString( "test" ) ) );
	}

	@Test
	public void toStringWritesWrappedGroup() {
		assertEquals( "()", new EQGroup( Collections.emptyList() ).toString() );
		assertEquals(
				"(1,'test',2)",
				new EQGroup( Arrays.asList( new EQValue( "1" ), new EQString( "test" ), new EQValue( "2" ) ) )
						.toString()
		);
	}
}
