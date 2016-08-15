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

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Responsible for retrieving the tokens of a stringified {@link EntityQuery}.
 * Supports string literals with escaped characters as well as operator and grouping characters.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
class EntityQueryTokenizer
{
	private static final char[] RESERVED_CHARS = new char[] { '!', '=', '>', '<' };
	private static final char[] GROUPING_CHARS = new char[] { '(', ')' };
	public static final char[] STRING_LITERAL_CHARS = new char[] { '\'', '"' };

	/**
	 * Contains both token and the position information of the token in the query.
	 */
	static final class TokenMetadata
	{
		private final String token;
		private int position;

		public TokenMetadata( String token, int position ) {
			this.token = token;
			this.position = position;
		}

		public String getToken() {
			return token;
		}

		public int getPosition() {
			return position;
		}

		public int getNextTokenPosition() {
			return position + token.length();
		}

		@Override
		public boolean equals( Object o ) {
			if ( this == o ) {
				return true;
			}
			if ( o == null || getClass() != o.getClass() ) {
				return false;
			}
			TokenMetadata that = (TokenMetadata) o;
			return position == that.position &&
					Objects.equals( token, that.token );
		}

		@Override
		public int hashCode() {
			return Objects.hash( token, position );
		}

		@Override
		public String toString() {
			return token + ":" + position;
		}
	}

	/**
	 * Convert a query in its string form into the list of tokens.
	 * Example: <em>a >= 1</em>, will be converted to ['a','>=','1'].
	 *
	 * @param query string
	 * @return list of tokens
	 */
	List<TokenMetadata> tokenize( String query ) {
		List<TokenMetadata> literals = new ArrayList<>();

		boolean lastCharWasReserved = false;
		boolean charIsEscaped = false;
		char requiredClosingStringLiteral = 0;
		StringBuilder currentLiteral = new StringBuilder();

		for ( int i = 0; i < query.length(); i++ ) {
			char ch = query.charAt( i );

			if ( requiredClosingStringLiteral != 0 && ch == '\\' ) {
				if ( charIsEscaped ) {
					currentLiteral.append( ch );
					charIsEscaped = false;
				}
				else {
					charIsEscaped = true;
				}
			}
			else if ( requiredClosingStringLiteral != 0 && ( ch != requiredClosingStringLiteral || charIsEscaped ) ) {
				currentLiteral.append( ch );
				charIsEscaped = false;
			}
			else if ( requiredClosingStringLiteral != 0 ) {
				currentLiteral.append( ch );
				requiredClosingStringLiteral = 0;
				charIsEscaped = false;
			}
			else if ( isStringLiteralCharacter( ch ) ) {
				requiredClosingStringLiteral = ch;

				if ( currentLiteral.length() > 0 ) {
					int position = i - currentLiteral.length();
					literals.add( new TokenMetadata( currentLiteral.toString(), position ) );
					currentLiteral = new StringBuilder();
				}

				currentLiteral.append( ch );
			}
			else if ( Character.isWhitespace( ch ) ) {
				if ( currentLiteral.length() > 0 ) {
					int position = i - currentLiteral.length();
					literals.add( new TokenMetadata( currentLiteral.toString(), position ) );
					currentLiteral = new StringBuilder();
					lastCharWasReserved = false;
				}
			}
			else if ( isGroupingCharacter( ch ) ) {
				if ( currentLiteral.length() > 0 ) {
					int position = i - currentLiteral.length();
					literals.add( new TokenMetadata( currentLiteral.toString(), position ) );
				}

				literals.add( new TokenMetadata( String.valueOf( ch ), i ) );
				currentLiteral = new StringBuilder();
			}
			else if ( isReservedCharacter( ch ) ) {
				if ( !lastCharWasReserved && currentLiteral.length() > 0 ) {
					literals.add( new TokenMetadata( currentLiteral.toString(), 0 ) );
					currentLiteral = new StringBuilder().append( ch );
				}
				else {
					currentLiteral.append( ch );
				}
				lastCharWasReserved = true;
			}
			else {
				if ( lastCharWasReserved ) {
					int position = i - currentLiteral.length();
					literals.add( new TokenMetadata( currentLiteral.toString(), position ) );
					currentLiteral = new StringBuilder();
				}
				currentLiteral.append( ch );
				lastCharWasReserved = false;
			}
		}

		if ( currentLiteral.length() > 0 ) {
			int position = query.length() - currentLiteral.length();
			literals.add( new TokenMetadata( currentLiteral.toString(), position ) );
		}

		return literals;
	}

	private boolean isStringLiteralCharacter( char ch ) {
		return ArrayUtils.contains( STRING_LITERAL_CHARS, ch );
	}

	private boolean isGroupingCharacter( char ch ) {
		return ArrayUtils.contains( GROUPING_CHARS, ch );
	}

	private boolean isReservedCharacter( char ch ) {
		return ArrayUtils.contains( RESERVED_CHARS, ch );
	}
}
