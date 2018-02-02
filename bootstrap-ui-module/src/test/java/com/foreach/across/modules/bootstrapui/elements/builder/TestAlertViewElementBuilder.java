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

package com.foreach.across.modules.bootstrapui.elements.builder;

import com.foreach.across.modules.bootstrapui.elements.AlertViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderFactory;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import com.foreach.across.test.support.AbstractViewElementBuilderTest;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arne Vandamme
 * @since 1.0.0
 */
public class TestAlertViewElementBuilder extends AbstractViewElementBuilderTest<AlertViewElementBuilder, AlertViewElement>
{
	@Override
	protected AlertViewElementBuilder createBuilder( ViewElementBuilderFactory builderFactory ) {
		return new AlertViewElementBuilder();
	}

	@Test
	public void resolvedNonHtml() {
		builder.escapeHtml( true ).text( "#{resolved}" );

		build();

		assertThat( element.getText() ).isEqualTo( "resolved" );
	}

	@Test
	public void resolvedHtml() {
		builder.text( "#{resolved}" );

		build();

		assertThat( element.getText() ).isNull();
		assertThat( element.getChildren().get( 0 ) )
				.isInstanceOf( TextViewElement.class )
				.matches( text -> !( (TextViewElement) text ).isEscapeXml() && ( (TextViewElement) text ).getText().equals( "resolved" ) );
	}
}
