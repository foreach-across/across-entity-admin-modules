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

package com.foreach.across.modules.entity.query;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class TestEntityQueryTokenizer
{
	private EntityQueryTokenizer tokenizer;

	@Before
	public void before() {
		tokenizer = new EntityQueryTokenizer();
	}

	@Test
	public void simpleTokensSeparatedByWhitespace() {
		assertEquals(
				Arrays.asList( "value", "=", "123" ),
				tokenizer.tokenize( "value = 123" )
		);
	}

	@Test
	public void allWhitespaceIsIgnored() {
		assertEquals(
				Arrays.asList( "value", "!=", "123" ),
				tokenizer.tokenize( "  value  != \t123 " )
		);
	}

	@Test
	public void noWhitespace() {
		assertEquals(
				Arrays.asList( "value", "!=", "123" ),
				tokenizer.tokenize( "value!=123" )
		);
	}

	@Test
	public void nonGroupedExampleWithSimpleValues() {
		assertEquals(
				Arrays.asList( "a", "=", "1", "and", "b", ">", "1", "or", "c", "<", "1", "and", "d", ">=", "4",
				               "or", "e", "<=", "5", "and", "f", "!=", "6", "or", "g", "<>", "7" ),
				tokenizer.tokenize( "a=1 and b>1 or c<1 and d>=4 or e<=5 and f!=6 or g<>7" )
		);
	}

	@Test
	public void groupedExamplesWithSimpleValues() {
		assertEquals(
				Arrays.asList( "a", "=", "1", "and", "b", ">", "1", "or", "(", "(", "c", "<", "1", "and", "d", ">=",
				               "4", ")", "or", "e", "<=", "5", "and", "f", "!=", "6", "or", "g", "<>", "7", ")" ),
				tokenizer.tokenize( "a=1 and b>1 or((c<1 and d>=4) or e<=5 and f!=6 or g<>7)" )
		);
	}

	@Test
	public void stringLiterals() {
		assertEquals(
				Arrays.asList( "value", "=", "'123 456'" ),
				tokenizer.tokenize( "value = '123 456'" )
		);

		assertEquals(
				Arrays.asList( "value", "=", "\"123 456\"" ),
				tokenizer.tokenize( "value = \"123 456\"" )
		);

		assertEquals(
				Arrays.asList( "value", "=", "'123\"\" >= 456'" ),
				tokenizer.tokenize( "value = '123\"\" >= 456'" )
		);

		assertEquals(
				Arrays.asList( "value", "=", "\"123'' '(456)\"" ),
				tokenizer.tokenize( "value = \"123'' '(456)\"" )
		);
	}

	@Test
	public void stringLiteralWithEscapedCharacters() {
		assertEquals(
				Arrays.asList( "value", "=", "'123 '456'" ),
				tokenizer.tokenize( "value = '123 \\'456'" )
		);

		assertEquals(
				Arrays.asList( "value", "=", "\"123 \"456\"" ),
				tokenizer.tokenize( "value = \"123 \\\"456\"" )
		);
	}

	@Test
	public void validQueryTokenization() {
		assertEquals(
				Arrays.asList( "(", "name", "=", "'someName'", "and", "city", "!=", "217", "and", "(", "email",
				               "contains", "'emailOne'", "or", "email", "=", "'emailTwo'", ")", ")" ),
				tokenizer.tokenize(
						"(name = 'someName' and city != 217 and (email contains 'emailOne' or email = 'emailTwo'))"
				)
		);
	}
}
