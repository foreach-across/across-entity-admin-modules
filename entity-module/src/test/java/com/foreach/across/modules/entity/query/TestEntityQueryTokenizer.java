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

import com.foreach.across.modules.entity.query.EntityQueryTokenizer.TokenMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class TestEntityQueryTokenizer
{
	private EntityQueryTokenizer tokenizer;

	@BeforeEach
	public void before() {
		tokenizer = new EntityQueryTokenizer();
	}

	@Test
	public void simpleTokensSeparatedByWhitespace() {
		assertEquals(
				Arrays.asList(
						meta( "value", 0 ),
						meta( "=", 6 ),
						meta( "123", 8 )
				),
				tokenizer.tokenize( "value = 123" )
		);
	}

	@Test
	public void allWhitespaceIsIgnored() {
		assertEquals(
				Arrays.asList(
						meta( "value", 2 ),
						meta( "!=", 9 ),
						meta( "123", 13 )
				),
				tokenizer.tokenize( "  value  != \t123 " )
		);
	}

	@Test
	public void simpleGroupPositions() {
		assertEquals(
				Arrays.asList(
						meta( "a", 0 ),
						meta( "=", 1 ),
						meta( "b", 2 ),
						meta( "or", 4 ),
						meta( "(", 7 ),
						meta( "b", 8 ),
						meta( "=", 10 ),
						meta( "c", 12 ),
						meta( "and", 14 ),
						meta( "d", 18 ),
						meta( "=", 20 ),
						meta( "'f'", 22 ),
						meta( ")", 25 )
				),
				tokenizer.tokenize( "a=b or (b = c and d = 'f')" )
		);
	}

	private TokenMetadata meta( String token, int position ) {
		return new TokenMetadata( token, position );
	}

	@Test
	public void noWhitespace() {
		assertEquals(
				Arrays.asList( "value", "!=", "123" ),
				tokens( "value!=123" )
		);
	}

	@Test
	public void nonGroupedExampleWithSimpleValues() {
		assertEquals(
				Arrays.asList( "a", "=", "1", "and", "b", ">", "1", "or", "c", "<", "1", "and", "d", ">=", "4",
				               "or", "e", "<=", "5", "and", "f", "!=", "6", "or", "g", "<>", "7" ),
				tokens( "a=1 and b>1 or c<1 and d>=4 or e<=5 and f!=6 or g<>7" )
		);
	}

	@Test
	public void groupedExamplesWithSimpleValues() {
		assertEquals(
				Arrays.asList( "a", "=", "1", "and", "b", ">", "1", "or", "(", "(", "c", "<", "1", "and", "d", ">=",
				               "4", ")", "or", "e", "<=", "5", "and", "f", "!=", "6", "or", "g", "<>", "7", ")" ),
				tokens( "a=1 and b>1 or((c<1 and d>=4) or e<=5 and f!=6 or g<>7)" )
		);
	}

	@Test
	public void commaSeparatedValues() {
		assertEquals(
				Arrays.asList( "a", "=", "(", "1", ",", "2", ")" ),
				tokens( "a = (1,2)" )
		);
	}

	@Test
	public void stringLiterals() {
		assertEquals(
				Arrays.asList( "value", "=", "'123 456'" ),
				tokens( "value = '123 456'" )
		);

		assertEquals(
				Arrays.asList( "value", "=", "\"123 456\"" ),
				tokens( "value = \"123 456\"" )
		);

		assertEquals(
				Arrays.asList( "value", "=", "'123\"\" >= 456'" ),
				tokens( "value = '123\"\" >= 456'" )
		);

		assertEquals(
				Arrays.asList( "value", "=", "\"123'' '(456)\"" ),
				tokens( "value = \"123'' '(456)\"" )
		);
	}

	@Test
	public void stringLiteralWithEscapedCharacters() {
		assertEquals(
				Arrays.asList( "value", "=", "'123 '456'" ),
				tokens( "value = '123 \\'456'" )
		);

		assertEquals(
				Arrays.asList( "value", "=", "\"123 \"456\"" ),
				tokens( "value = \"123 \\\"456\"" )
		);
	}

	@Test
	public void validQueryTokenization() {
		assertEquals(
				Arrays.asList( "(", "name", "=", "'someName'", "and", "city", "!=", "217", "and", "(", "email",
				               "contains", "'emailOne'", "and", "email", "not", "contains", "'emailTwo'", ")", ")" ),
				tokens(
						"(name = 'someName' and city != 217 and (email contains 'emailOne' and email not contains 'emailTwo'))"
				)
		);
	}

	@Test
	public void orderBy() {
		assertEquals(
				Arrays.asList( "order", "by", "name", "desc", ",", "city", "asc" ),
				tokens( "order by name desc, city asc" )
		);
	}

	@Test
	public void orderByAppend() {
		assertEquals(
				Arrays.asList( "(", "name", "=", "'someName'", "and", "city", "!=", "217", "and", "(", "email",
				               "contains", "'emailOne'", "and", "email", "not", "contains", "'emailTwo'", ")", ")",
				               "order", "by", "name", "desc", ",", "city", "asc" ),
				tokens( "(name = 'someName' and city != 217 and (email contains 'emailOne' and email not contains 'emailTwo')) order by name desc, city asc" )
		);
	}

	private List<String> tokens( String query ) {
		List<TokenMetadata> metadata = tokenizer.tokenize( query );
		return metadata.stream().map( TokenMetadata::getToken ).collect( Collectors.toList() );
	}
}
