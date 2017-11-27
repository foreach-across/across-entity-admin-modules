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

package com.foreach.across.modules.entity.views;

import org.junit.Test;

import static com.foreach.across.modules.entity.views.ViewElementMode.*;
import static org.junit.Assert.*;

/**
 * @author Arne Vandamme
 * @since 2.2.0
 */
public class TestViewElementMode
{
	private static final String NAME_SINGLE = "MY_NAME";
	private static final String NAME_MULTIPLE = "MY_NAME_MULTIPLE";

	@Test
	public void multipleSuffixIsAddedIfMissing() {
		ViewElementMode mode = new ViewElementMode( NAME_SINGLE );
		assertFalse( mode.isForMultiple() );

		ViewElementMode multiple = mode.forMultiple();
		assertEquals( new ViewElementMode( NAME_MULTIPLE ), multiple );

		assertTrue( multiple.isForMultiple() );
		assertSame( multiple, multiple.forMultiple() );
	}

	@Test
	public void multipleSuffixIsRemovedForSingle() {
		ViewElementMode multiple = new ViewElementMode( NAME_MULTIPLE );
		assertTrue( multiple.isForMultiple() );

		ViewElementMode single = multiple.forSingle();
		assertEquals( new ViewElementMode( NAME_SINGLE ), single );

		assertFalse( single.isForMultiple() );
		assertSame( single, single.forSingle() );
	}

	@Test
	public void isListMode() {
		assertTrue( isList( ViewElementMode.LIST_VALUE ) );
		assertTrue( isList( ViewElementMode.LIST_LABEL ) );
		assertTrue( isList( ViewElementMode.LIST_CONTROL ) );
		assertTrue( isList( ViewElementMode.LIST_VALUE.forMultiple() ) );
		assertTrue( isList( ViewElementMode.LIST_LABEL.forMultiple() ) );
		assertTrue( isList( ViewElementMode.LIST_CONTROL.forMultiple() ) );
		assertFalse( isList( ViewElementMode.CONTROL ) );
	}

	@Test
	public void isLabelMode() {
		assertTrue( isLabel( ViewElementMode.LABEL ) );
		assertTrue( isLabel( ViewElementMode.LIST_LABEL ) );
		assertTrue( isLabel( ViewElementMode.LABEL.forMultiple() ) );
		assertTrue( isLabel( ViewElementMode.LIST_LABEL.forMultiple() ) );
		assertFalse( isLabel( ViewElementMode.CONTROL ) );
	}

	@Test
	public void isValueMode() {
		assertTrue( isValue( ViewElementMode.VALUE ) );
		assertTrue( isValue( ViewElementMode.LIST_VALUE ) );
		assertTrue( isValue( ViewElementMode.VALUE.forMultiple() ) );
		assertTrue( isValue( ViewElementMode.LIST_VALUE.forMultiple() ) );
		assertFalse( isValue( ViewElementMode.CONTROL ) );
	}

	@Test
	public void isControlMode() {
		assertTrue( isControl( ViewElementMode.CONTROL ) );
		assertTrue( isControl( ViewElementMode.LIST_CONTROL ) );
		assertTrue( isControl( ViewElementMode.FILTER_CONTROL ) );
		assertTrue( isControl( ViewElementMode.CONTROL.forMultiple() ) );
		assertTrue( isControl( ViewElementMode.LIST_CONTROL.forMultiple() ) );
		assertTrue( isControl( ViewElementMode.FILTER_CONTROL.forMultiple() ) );
		assertFalse( isControl( ViewElementMode.LABEL ) );
	}
}
