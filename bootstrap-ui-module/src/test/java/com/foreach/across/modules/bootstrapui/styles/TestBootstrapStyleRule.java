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

package com.foreach.across.modules.bootstrapui.styles;

import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import org.junit.jupiter.api.Test;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.html;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
class TestBootstrapStyleRule
{
	@Test
	void cssClasses() {
		assertThat( css.button.primary.toCssClasses() ).containsExactly( "btn", "btn-primary" );
	}

	@Test
	void asSetter() {
		NodeViewElement node = html.a( css.button.primary );
		assertThat( node.hasCssClass( "btn" ) ).isTrue();
		assertThat( node.hasCssClass( "btn-primary" ) ).isTrue();
	}

	@Test
	void asRemover() {
		NodeViewElement node = html.a( BootstrapStyleRule.of( "btn", "btn-primary" ) );
		assertThat( node.hasCssClass( "btn" ) ).isTrue();
		assertThat( node.hasCssClass( "btn-primary" ) ).isTrue();

		node.remove( BootstrapStyleRule.of( "btn", "btn-primary" ) );
		assertThat( node.hasCssClass( "btn" ) ).isFalse();
		assertThat( node.hasCssClass( "btn-primary" ) ).isFalse();
	}

	@Test
	void asPredicate() {
		NodeViewElement node = html.a( css.button.primary );
		assertThat( node.matches( css.button.primary ) ).isTrue();
		assertThat( node.matches( css.button ) ).isTrue();
		assertThat( node.matches( css.button.secondary ) ).isFalse();
	}

	@Test
	void appending() {
		NodeViewElement node = html.a( css.button.primary );
		assertThat( node.matches( css.button.primary ) ).isTrue();
		assertThat( node.matches( css.button ) ).isTrue();

		node.remove( css.button.primary );
		assertThat( node.matches( css.button.primary ) ).isFalse();
		assertThat( node.matches( css.button ) ).isTrue();

		node.remove( css.button );
		assertThat( node.matches( css.button ) ).isFalse();
	}
}
