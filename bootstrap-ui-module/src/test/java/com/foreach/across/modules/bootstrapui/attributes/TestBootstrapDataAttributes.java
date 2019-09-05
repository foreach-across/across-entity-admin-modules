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

package com.foreach.across.modules.bootstrapui.attributes;

import org.junit.jupiter.api.Test;

import static com.foreach.across.modules.bootstrapui.attributes.BootstrapAttributes.attribute;
import static com.foreach.across.modules.bootstrapui.attributes.TestBootstrapAttributes.assertAttribute;

/**
 * @author Arne Vandamme
 * @since 2.3.0
 */
class TestBootstrapDataAttributes
{
	@Test
	void customAttribute() {
		assertAttribute( attribute.data.of( "someAttr" ) ).is( "data-someAttr" );
		assertAttribute( attribute.data.of( "someAttr" ).withValue( 123 ) ).is( "data-someAttr", 123 );
	}

	@Test
	void toggle() {
		assertAttribute( attribute.data.toggle ).is( "data-toggle" );
		assertAttribute( attribute.data.toggle( "something" ) ).is( "data-toggle", "something" );
		assertAttribute( attribute.data.toggle.modal ).is( "data-toggle", "modal" );
		assertAttribute( attribute.data.toggle.dropdown ).is( "data-toggle", "dropdown" );
		assertAttribute( attribute.data.toggle.collapse ).is( "data-toggle", "collapse" );
		assertAttribute( attribute.data.toggle.tab ).is( "data-toggle", "tab" );
		assertAttribute( attribute.data.toggle.tooltip ).is( "data-toggle", "tooltip" );
	}
}
