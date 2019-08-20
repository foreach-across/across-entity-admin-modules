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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.mock;

/**
 * @author Stijn Vanhoof
 */
public class TestIconSets
{
	private IconSet initialIconSet = mock( IconSet.class );

	@BeforeEach
	public void setup() {
		IconSets.add( "test", initialIconSet );
	}

	@Test
	public void addNewIconSet() {
		IconSet testIconSet = mock( IconSet.class );

		IconSets.add( "bootstrapUiModule", testIconSet );
		assertThat( IconSets.iconSet( "bootstrapUiModule" ) ).isEqualTo( testIconSet );
		assertThat( IconSets.iconSet( "test" ) ).isEqualTo( initialIconSet );
	}

	@Test
	public void invalidIconSetThrowIllegalArgumentException() {
		Assertions.assertThrows( IllegalArgumentException.class, () -> {
			IconSets.iconSet( "unknown" );
		} );
	}

	@Test
	public void overrideIconSet() {
		IconSet iconSetThatWillOverride = mock( IconSet.class );
		IconSets.add( "test", iconSetThatWillOverride );

		assertThat( IconSets.iconSet( "test" ) ).isEqualTo( iconSetThatWillOverride );
	}

	@Test
	public void removeIconSet() {
		IconSet testIconSet = mock( IconSet.class );

		IconSets.add( "bootstrapUiModule", testIconSet );
		assertThat( IconSets.iconSet( "bootstrapUiModule" ) ).isEqualTo( testIconSet );

		IconSets.remove( "bootstrapUiModule" );

		Assertions.assertThrows( IllegalArgumentException.class, () -> {
			IconSets.iconSet( "bootstrapUiModule" );
		} );
	}
}
