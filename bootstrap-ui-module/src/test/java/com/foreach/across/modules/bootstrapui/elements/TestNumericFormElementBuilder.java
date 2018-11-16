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
package com.foreach.across.modules.bootstrapui.elements;

import com.foreach.across.modules.bootstrapui.elements.builder.NumericFormElementBuilder;
import com.foreach.across.modules.bootstrapui.utils.BootstrapElementUtils;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

/**
 * @author Arne Vandamme
 */
public class TestNumericFormElementBuilder
{
	private NumericFormElementBuilder numericFormElementBuilder;
	private NumericFormElement numeric;

	@Before
	public void before() {
		numericFormElementBuilder = new NumericFormElementBuilder();
	}

	@Test
	public void percentageNumeric() {
		NumericFormElement numeric = numericFormElementBuilder.percent()
		                                                      .build();

		assertEquals( NumericFormElementConfiguration.Format.PERCENT , numeric.getConfiguration().getFormat() );
	}

	@Test
	public void currencyNumeric() {
		NumericFormElement numeric = numericFormElementBuilder.currency()
		                                                      .build();

		assertEquals( NumericFormElementConfiguration.Format.CURRENCY , numeric.getConfiguration().getFormat() );
	}
}
