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
class TestHtmlAriaAttributes
{
	@Test
	void customAttribute() {
		assertAttribute( attribute.aria.of( "someAttr" ) ).is( "aria-someAttr" );
		assertAttribute( attribute.aria.of( "someAttr" ).withValue( 123 ) ).is( "aria-someAttr", 123 );
	}

	@Test
	void hasPopup() {
		assertAttribute( attribute.aria.hasPopup ).is( "aria-haspopup" ).is( "aria-haspopup", true );
		assertAttribute( attribute.aria.hasPopup( true ) ).is( "aria-haspopup", true );
		assertAttribute( attribute.aria.hasPopup( false ) ).is( "aria-haspopup", false );
	}

	@Test
	void expanded() {
		assertAttribute( attribute.aria.expanded ).is( "aria-expanded" );
		assertAttribute( attribute.aria.expanded( true ) ).is( "aria-expanded", true );
		assertAttribute( attribute.aria.expanded( false ) ).is( "aria-expanded", false );
	}

	@Test
	void hidden() {
		assertAttribute( attribute.aria.hidden ).is( "aria-hidden" ).is( "aria-hidden", true );
		assertAttribute( attribute.aria.hidden( true ) ).is( "aria-hidden", true );
		assertAttribute( attribute.aria.hidden( false ) ).is( "aria-hidden", false );
	}

	@Test
	void label() {
		assertAttribute( attribute.aria.label ).is( "aria-label" );
		assertAttribute( attribute.aria.label( "Some text" ) ).is( "aria-label", "Some text" );
	}
}
