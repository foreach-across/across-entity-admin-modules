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

package com.foreach.across.modules.bootstrapui.elements.tooltip;

import com.foreach.across.modules.bootstrapui.elements.AbstractBootstrapViewElementTest;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class TestTooltipViewElement extends AbstractBootstrapViewElementTest
{
	private TooltipViewElement tooltip = new TooltipViewElement();

	@Test
	public void defaultTooltip() {
		assertThat( tooltip.isEscapeHtml() ).isFalse();
		assertThat( tooltip.getIcon() ).isNotNull();
		assertThat( tooltip.getText() ).isNull();

		renderAndExpect(
				tooltip,
				"<a data-toggle='tooltip' class='tooltip-link text-muted' data-html='true'>" +
						"<i aria-hidden='true' class='fas fa-question-circle'></i></a>"
		);
	}

	@Test
	public void simpleTooltipText() {
		tooltip.setText( "my text" );
		assertThat( tooltip.getText() ).isEqualTo( "my text" );

		renderAndExpect(
				tooltip,
				"<a data-toggle='tooltip' title='my text' class='tooltip-link text-muted' data-html='true'>" +
						"<i aria-hidden='true' class='fas fa-question-circle'></i></a>"
		);
	}

	@Test
	public void customizedTooltip() {
		tooltip.setTagName( "span" );
		tooltip.setIcon( null );
		tooltip.setText( "my text" );
		tooltip.setEscapeHtml( true );
		tooltip.addChild( TextViewElement.text( "child" ) );

		assertThat( tooltip.isEscapeHtml() ).isTrue();
		assertThat( tooltip.getIcon() ).isNull();
		assertThat( tooltip.getText() ).isEqualTo( "my text" );

		renderAndExpect(
				tooltip,
				"<span data-toggle='tooltip' title='my text' class='tooltip-link text-muted' data-html='false'>" +
						"child</span>"
		);
	}

}
