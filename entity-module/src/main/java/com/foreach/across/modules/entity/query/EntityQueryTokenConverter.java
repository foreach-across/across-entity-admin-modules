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

import com.foreach.across.modules.entity.query.EntityQueryParsingException.*;
import com.foreach.across.modules.entity.query.EntityQueryTokenizer.TokenMetadata;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;

import java.util.*;

import static com.foreach.across.modules.entity.query.EntityQueryOps.*;

/**
 * Converts a list of tokens into an {@link EntityQuery} without applying any content validation.
 * This will create groups, determine operators, see if the form of "field - operator - value" is respected,
 * and convert the value into one of EQString, EQValue, EQGroup or EQFunction.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
class EntityQueryTokenConverter
{
	/**
	 * Holds state of the token processing.
	 */
	private static class TokenQueue
	{
		private final List<TokenMetadata> originalTokens;
		private final Deque<TokenMetadata> unprocessedTokens;

		private TokenMetadata lastPopped = null;

		TokenQueue( List<TokenMetadata> originalTokens ) {
			this.originalTokens = originalTokens;
			this.unprocessedTokens = new ArrayDeque<>( originalTokens );
		}

		TokenMetadata getLastPopped() {
			return lastPopped;
		}

		TokenMetadata peek() {
			return unprocessedTokens.peekFirst();
		}

		void pushBack( TokenMetadata tokenMetadata ) {
			unprocessedTokens.addFirst( tokenMetadata );
		}

		TokenMetadata pop() {
			lastPopped = unprocessedTokens.removeFirst();
			return lastPopped;
		}

		boolean hasTokens() {
			return !unprocessedTokens.isEmpty();
		}

		List<String> retrieveProcessedTokensSince( TokenMetadata first ) {
			int start = originalTokens.indexOf( first );
			int end = originalTokens.indexOf( lastPopped );

			if ( start < 0 || end < 0 ) {
				return Collections.emptyList();
			}

			List<String> tokenValues = new ArrayList<>();
			for ( int i = start; i <= end; i++ ) {
				tokenValues.add( originalTokens.get( i ).getToken() );
			}
			return tokenValues;
		}
	}

	EntityQuery convertTokens( List<TokenMetadata> tokenMetadata ) {
		return buildQuery( new TokenQueue( tokenMetadata ), false );
	}

	@SuppressWarnings( "squid:S2583" )
	private EntityQuery buildQuery( TokenQueue queue, boolean inGroup ) {
		EntityQuery query = new EntityQuery();
		EntityQueryOps queryOp = null;

		boolean expectingAndOr = false;
		boolean expectingNextValue = inGroup;
		boolean inOrderByClause = false;

		List<Sort.Order> orders = new LinkedList<>();

		while ( queue.hasTokens() ) {
			TokenMetadata nextToken = queue.peek();

			if ( !inOrderByClause ) {
				if ( !expectingAndOr && "(".equals( nextToken.getToken() ) ) {
					queue.pop();
					query.add( buildQuery( queue, true ) );
					expectingAndOr = true;
					expectingNextValue = false;
				}
				else if ( ")".equals( nextToken.getToken() ) ) {
					if ( inGroup ) {
						queue.pop();
						return query;
					}
					else {
						throw new IllegalToken( nextToken.getToken(), nextToken.getPosition() );
					}
				}
				else if ( expectingAndOr && "and".equalsIgnoreCase( nextToken.getToken() ) ) {
					if ( queryOp != null && queryOp != EntityQueryOps.AND ) {
						throw new IllegalKeyword( nextToken.getToken(), nextToken.getPosition() );
					}
					queue.pop();
					query.setOperand( EntityQueryOps.AND );
					expectingAndOr = false;
					queryOp = EntityQueryOps.AND;
					expectingNextValue = true;
				}
				else if ( expectingAndOr && "or".equalsIgnoreCase( nextToken.getToken() ) ) {
					if ( queryOp != null && queryOp != EntityQueryOps.OR ) {
						throw new IllegalKeyword( nextToken.getToken(), nextToken.getPosition() );
					}
					queue.pop();
					query.setOperand( EntityQueryOps.OR );
					expectingAndOr = false;
					queryOp = EntityQueryOps.OR;
					expectingNextValue = true;
				}
				else {
					if ( "order".equalsIgnoreCase( nextToken.getToken() ) ) {
						TokenMetadata orderKeyword = queue.pop();

						if ( queue.hasTokens() && "by".equalsIgnoreCase( queue.peek().getToken() ) ) {
							inOrderByClause = true;
							expectingNextValue = true;
							queue.pop();
						}
						else {
							queue.pushBack( orderKeyword );
						}
					}

					if ( !inOrderByClause ) {
						if ( expectingAndOr ) {
							throw new MissingKeyword( "and/or", nextToken.getToken(), nextToken.getPosition() );
						}

						query.add( buildCondition( queue ) );
						expectingAndOr = true;
						expectingNextValue = false;
					}
				}
			}
			else {
				if ( expectingNextValue ) {
					orders.add( buildOrderSpecifier( queue ) );
					expectingNextValue = false;
				}
				else if ( ",".equalsIgnoreCase( nextToken.getToken() ) ) {
					queue.pop();
					expectingNextValue = true;
				}
				else {
					throw new IllegalToken( nextToken.getToken(), nextToken.getPosition() );
				}
			}
		}

		if ( expectingNextValue ) {
			MissingField missingField = new MissingField( queue.getLastPopped().getNextTokenPosition() );

			TokenMetadata previous = queue.getLastPopped();
			if ( previous != null ) {
				missingField.setContextExpressionStart( previous.getPosition() );
				missingField.setContextExpression( previous.getToken() );
			}

			throw missingField;
		}

		if ( !orders.isEmpty() ) {
			query.setSort( Sort.by( orders ) );
		}

		return query;
	}

	private Sort.Order buildOrderSpecifier( TokenQueue queue ) {
		if ( !queue.hasTokens() ) {
			throw new MissingField( queue.getLastPopped().getNextTokenPosition() );
		}

		String fieldName = queue.pop().getToken();

		if ( !queue.hasTokens() ) {
			throw new MissingOrderDirection( fieldName, queue.getLastPopped().getNextTokenPosition() );
		}

		TokenMetadata directionToken = queue.pop();

		try {
			Sort.Direction direction = Sort.Direction.fromString( directionToken.getToken() );
			return new Sort.Order( direction, fieldName );
		}
		catch ( IllegalArgumentException iae ) {
			throw new IllegalOrderDirection( fieldName, directionToken.getToken(), directionToken.getPosition() );
		}
	}

	private EntityQueryCondition buildCondition( TokenQueue queue ) {
		TokenMetadata startOfCondition = queue.getLastPopped();

		try {
			EntityQueryCondition condition = new EntityQueryCondition();

			TokenMetadata field = retrieveField( queue );

			if ( field == null ) {
				throw new EntityQueryParsingException.MissingField( queue.getLastPopped().getNextTokenPosition() );
			}

			startOfCondition = field;

			condition.setProperty( field.getToken() );

			EntityQueryOps operator = retrieveOperator( queue );

			if ( operator == null ) {
				throw new MissingOperator( field.getToken(), field.getNextTokenPosition() );
			}

			condition.setOperand( operator );

			int expectedPosition = queue.getLastPopped().getNextTokenPosition();
			EQType value = retrieveValue( queue, true );

			if ( value == null ) {
				throw new MissingValue( operator.toString( field.getToken(), EQValue.MISSING ), expectedPosition );
			}

			if ( isNullOrEmptyOperator( operator ) ) {
				if ( !validateNullOrEmptyConditionValue( condition, value ) ) {
					throw new IllegalIsValue( field.getToken(), expectedPosition );
				}
			}
			else {
				condition.setArguments( new Object[] { value } );
			}

			return condition;
		}
		catch ( EntityQueryParsingException pe ) {
			if ( startOfCondition != null ) {
				pe.setContextExpressionStart( startOfCondition.getPosition() );
				pe.setContextExpression(
						StringUtils.join( queue.retrieveProcessedTokensSince( startOfCondition ), " " )
				);
			}
			else {
				pe.setContextExpressionStart( 0 );
				pe.setContextExpression( pe.getErrorExpression() );
			}

			throw pe;
		}
	}

	private boolean isNullOrEmptyOperator( EntityQueryOps operator ) {
		return IS_NULL.equals( operator ) || IS_EMPTY.equals( operator )
				|| IS_NOT_NULL.equals( operator ) || IS_NOT_EMPTY.equals( operator );
	}

	private boolean validateNullOrEmptyConditionValue( EntityQueryCondition condition, Object value ) {
		if ( value instanceof EQValue ) {
			String rawValue = StringUtils.lowerCase( ( (EQValue) value ).getValue() );
			if ( "empty".equals( rawValue ) ) {
				if ( EntityQueryOps.IS_NULL.equals( condition.getOperand() ) ) {
					condition.setOperand( EntityQueryOps.IS_EMPTY );
				}
				if ( EntityQueryOps.IS_NOT_NULL.equals( condition.getOperand() ) ) {
					condition.setOperand( EntityQueryOps.IS_NOT_EMPTY );
				}

				return true;
			}
			else {
				return "null".equals( rawValue );
			}
		}

		return false;
	}

	private TokenMetadata retrieveField( TokenQueue queue ) {
		if ( queue.hasTokens() ) {
			TokenMetadata token = queue.pop();

			if ( isReserved( token.getToken(), false ) ) {
				throw new IllegalField( token.getToken(), token.getPosition() );
			}

			return token;
		}

		return null;
	}

	private EntityQueryOps retrieveOperator( TokenQueue queue ) {
		if ( queue.hasTokens() ) {
			TokenMetadata token = queue.pop();
			String operatorToken = token.getToken();

			while ( queue.hasTokens() && isKeyword( queue.peek().getToken() ) ) {
				operatorToken += " " + queue.pop().getToken();
			}

			EntityQueryOps operator = EntityQueryOps.forToken( operatorToken );

			if ( operator == null ) {
				throw new IllegalOperator( operatorToken, token.getPosition() );
			}

			return operator;
		}

		return null;
	}

	private EQType retrieveValue( TokenQueue queue, boolean allowGroupValue ) {
		if ( queue.hasTokens() ) {
			TokenMetadata token = queue.pop();

			if ( isReserved( token.getToken(), allowGroupValue ) ) {
				throw new IllegalToken( token.getToken(), token.getPosition() );
			}

			if ( isGroup( token.getToken() ) ) {
				return removeCurrentGroup( queue );
			}
			else if ( isLiteral( token ) ) {
				return convertToEQString( token.getToken() );
			}
			else if ( queue.hasTokens() && isGroup( queue.peek().getToken() ) ) {
				return buildFunction( token, queue );
			}
			else {
				return "null".equalsIgnoreCase( token.getToken() ) ? EQValue.NULL : new EQValue( token.getToken() );
			}
		}

		return null;
	}

	private EQFunction buildFunction( TokenMetadata token, TokenQueue queue ) {
		queue.pop();

		if ( queue.hasTokens() && ")".equals( queue.peek().getToken() ) ) {
			// no parameters
			queue.pop();
			return new EQFunction( token.getToken() );
		}

		EQGroup group = removeCurrentGroup( queue );
		return new EQFunction( token.getToken(), group.getValues() );
	}

	private EQGroup removeCurrentGroup( TokenQueue queue ) {
		List<EQType> values = new ArrayList<>();

		boolean inGroup = true;
		boolean expectingNext = true;

		while ( inGroup && queue.hasTokens() ) {
			TokenMetadata token = queue.peek();

			if ( !expectingNext && ")".equals( token.getToken() ) ) {
				queue.pop();
				inGroup = false;
			}
			else if ( !expectingNext && ",".equals( token.getToken() ) ) {
				queue.pop();
				expectingNext = true;
			}
			else {
				if ( !expectingNext ) {
					throw new IllegalToken( token.getToken(), token.getPosition() );
				}

				// don't allow nested groups
				values.add( retrieveValue( queue, false ) );
				expectingNext = false;
			}
		}

		if ( expectingNext ) {
			// expected value did not show up
			TokenMetadata lastProcessedToken = queue.getLastPopped();
			throw new MissingValue( lastProcessedToken.getToken(), lastProcessedToken.getNextTokenPosition() );
		}
		else if ( inGroup ) {
			// group not finished, missing an ) after the last token
			TokenMetadata lastProcessedToken = queue.getLastPopped();
			throw new MissingToken( ")", lastProcessedToken.getToken(), lastProcessedToken.getNextTokenPosition() );
		}

		return new EQGroup( values );
	}

	private boolean isGroup( String token ) {
		return "(".equals( token );
	}

	private boolean isLiteral( TokenMetadata metadata ) {
		String token = metadata.getToken();
		if ( token.length() > 1 ) {
			char firstChar = token.charAt( 0 );

			if ( ArrayUtils.contains( EntityQueryTokenizer.STRING_LITERAL_CHARS, firstChar ) ) {
				if ( firstChar == token.charAt( token.length() - 1 ) ) {
					return true;
				}
				else {
					throw new MissingToken( firstChar == '\'' ? "''" : "\"", token, metadata.getNextTokenPosition() );
				}
			}
		}

		return false;
	}

	private EQString convertToEQString( String token ) {
		return new EQString( StringUtils.substring( token, 1, token.length() - 1 ) );
	}

	private boolean isReserved( String token, boolean allowOpenGroup ) {
		switch ( token ) {
			case "(":
				return !allowOpenGroup;
			case ")":
			case ",":
				return true;
			default:
				return isKeyword( token );
		}
	}

	private boolean isKeyword( String token ) {
		switch ( token.toLowerCase() ) {
			case "!=":
			case "=":
			case ">":
			case ">=":
			case "<":
			case "<=":
			case "contains":
			case "and":
			case "or":
			case "not":
			case "in":
			case "like":
			case "ilike":
			case "is":
				return true;
		}

		return false;
	}
}
