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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Arne Vandamme
 */
public class TestNumericFormElementBuilder
{
	private NumericFormElementBuilder numericFormElementBuilder;
	private NumericFormElement numeric;

	@BeforeEach
	public void before() {
		numericFormElementBuilder = new NumericFormElementBuilder();
	}

	@Test
	public void percentageNumeric() {
		NumericFormElement numeric = numericFormElementBuilder.percent()
		                                                      .build();

		assertEquals( NumericFormElementConfiguration.Format.PERCENT, numeric.getConfiguration().getFormat() );
	}

	@Test
	public void currencyNumeric() {
		LocaleContextHolder.setLocale( new Locale( "en", "us" ) );
		NumericFormElement numeric = numericFormElementBuilder.currency()
		                                                      .build();

		assertEquals( NumericFormElementConfiguration.Format.CURRENCY , numeric.getConfiguration().getFormat() );
		LocaleContextHolder.resetLocaleContext();
	}
}
