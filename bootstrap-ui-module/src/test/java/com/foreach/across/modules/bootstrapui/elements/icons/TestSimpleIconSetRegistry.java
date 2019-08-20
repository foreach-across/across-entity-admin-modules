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

import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.sun.deploy.panel.ExceptionListDialog;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.mock;

/**
 * @author Stijn Vanhoof
 */
public class TestSimpleIconSetRegistry
{
	private SimpleIconSet initialSimpleIconSet = mock( SimpleIconSet.class );

	@BeforeEach
	public void setup() {
		IconSetRegistry.addIconSet( "test", initialSimpleIconSet );
	}

	@Test
	public void addNewIconSet() {
		SimpleIconSet testSimpleIconSet = mock( SimpleIconSet.class );

		IconSetRegistry.addIconSet( "bootstrapUiModule", testSimpleIconSet );
		assertThat( IconSetRegistry.getIconSet( "bootstrapUiModule" ) ).isEqualTo( testSimpleIconSet );
		assertThat( IconSetRegistry.getIconSet( "test" ) ).isEqualTo( initialSimpleIconSet );
	}

	@Test
	public void getAllIconSets(){
		IconSetRegistry.removeAllIconSets(  );
		IconSetRegistry.addIconSet( "test1",  mock( SimpleIconSet.class ) );
		IconSetRegistry.addIconSet( "test2",  mock( SimpleIconSet.class ) );

		assertThat( IconSetRegistry.getAllIconSets() ).hasSize( 2 );
	}

	@Test
	public void invalidIconSetThrowIllegalArgumentException() {
		Assertions.assertThrows( IllegalArgumentException.class, () -> {
			IconSetRegistry.getIconSet( "unknown" );
		} );
	}

	@Test
	public void overrideIconSet() {
		SimpleIconSet simpleIconSetThatWillOverride = mock( SimpleIconSet.class );
		IconSetRegistry.addIconSet( "test", simpleIconSetThatWillOverride );

		assertThat( IconSetRegistry.getIconSet( "test" ) ).isEqualTo( simpleIconSetThatWillOverride );
		MutableIconSet previouslySetIconSet = IconSetRegistry.getIconSet( "test" );
		previouslySetIconSet.add( "sample-icon", (iconName) -> new NodeViewElement( "i" ) );
		IconSetRegistry.addIconSet( "test",  previouslySetIconSet);

		assertThat( IconSetRegistry.getIconSet( "test" ) ).isEqualTo( previouslySetIconSet );
	}

	@Test
	public void removeIconSet() {
		SimpleIconSet testSimpleIconSet = mock( SimpleIconSet.class );

		IconSetRegistry.addIconSet( "bootstrapUiModule", testSimpleIconSet );
		assertThat( IconSetRegistry.getIconSet( "bootstrapUiModule" ) ).isEqualTo( testSimpleIconSet );

		IconSetRegistry.removeIconSet( "bootstrapUiModule" );

		Assertions.assertThrows( IllegalArgumentException.class, () -> {
			IconSetRegistry.getIconSet( "bootstrapUiModule" );
		} );
	}
}
