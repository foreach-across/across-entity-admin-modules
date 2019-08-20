/*
 * Copyright 2019 the original author or authors
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

package com.foreach.across.modules.bootstrapui.elements.icons;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * @author Stijn Vanhoof
 */
public class TestIconSets
{
	private IconSet initialIconSet = mock( IconSet.class );

	@Before
	public void setup() {
		IconSets.add( "test", initialIconSet );
	}

	@Test
	public void addNewIconSet() {
		IconSet testIconSet = mock( IconSet.class );

		IconSets.add( "bootstrapUiModule", testIconSet );
		Assert.assertEquals( IconSets.iconSet( "bootstrapUiModule" ), testIconSet );
		Assert.assertEquals( IconSets.iconSet( "test" ), initialIconSet );
	}

	@Test(expected = IllegalArgumentException.class)
	public void invalidIconSetThrowIllegalArguemntException() {
		IconSets.iconSet( "unknown" );
	}
}
