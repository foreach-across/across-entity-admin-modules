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

import java.text.MessageFormat;

/**
 * Base class for exceptions thrown by the {@link EntityQueryTokenConverter}.
 * Returns information on where the exception has occurred in the original query.
 * <p/>
 * The {@link #getErrorExpression()} and {@link #getErrorExpressionPosition()} properties give additional information on the
 * last part of the errorExpression that was handled.  Depending on the type of exception the actual error occurred
 * when handling the last token or not finding the expected token right after (see sub-class {@link ExpressionUnbalanced}).
 * <p/>
 * The {@link #getContextExpression()} and {@link #getContextExpressionStart()} return the sub-errorExpression that was being handled,
 * usually the entire predicate clause in a query.  The position returns the start of the sub-errorExpression clause.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public abstract class EntityQueryParsingException extends RuntimeException
{
	private int contextExpressionStart, errorExpressionPosition;
	private String contextExpression, errorExpression;

	public EntityQueryParsingException( String message ) {
		super( message );
	}

	/**
	 * @param contextExpressionStart start of the sub-errorExpression in the original query
	 */
	public void setContextExpressionStart( int contextExpressionStart ) {
		this.contextExpressionStart = contextExpressionStart;
	}

	/**
	 * @param errorExpressionPosition position most relevant to where the exception occurred
	 */
	public void setErrorExpressionPosition( int errorExpressionPosition ) {
		this.errorExpressionPosition = errorExpressionPosition;
	}

	/**
	 * @param contextExpression sub-errorExpression string that was being handled
	 */
	public void setContextExpression( String contextExpression ) {
		this.contextExpression = contextExpression;
	}

	/**
	 * @param errorExpression most relevant to the exception
	 */
	public void setErrorExpression( String errorExpression ) {
		this.errorExpression = errorExpression;
	}

	/**
	 * @return {@code true} if contextual errorExpression information is set
	 */
	public boolean hasContext() {
		return contextExpression != null;
	}

	public int getContextExpressionStart() {
		return contextExpressionStart;
	}

	public int getErrorExpressionPosition() {
		return errorExpressionPosition;
	}

	public String getContextExpression() {
		return contextExpression;
	}

	public String getErrorExpression() {
		return errorExpression;
	}

	@Override
	public String getMessage() {
		return MessageFormat.format( super.getMessage(), contextExpression, contextExpressionStart, errorExpression,
		                             errorExpressionPosition );
	}

	public static class IllegalToken extends EntityQueryParsingException
	{
		public IllegalToken( String token, int position ) {
			super( "Illegal token: {2}" );
			setErrorExpression( token );
			setErrorExpressionPosition( position );
		}
	}

	public static class IllegalKeyword extends EntityQueryParsingException
	{
		public IllegalKeyword( String keyword, int position ) {
			super( "Illegal keyword {2} - cannot combine and/or on the same level without explicit grouping" );
			setErrorExpression( keyword );
			setErrorExpressionPosition( position );
		}
	}

	public static class IllegalField extends EntityQueryParsingException
	{
		public IllegalField( String expression, int position ) {
			super( "Illegal field: {2}" );
			setErrorExpression( expression );
			setErrorExpressionPosition( position );
		}
	}

	public static class IllegalOperator extends EntityQueryParsingException
	{
		public IllegalOperator( String expression, int position ) {
			super( "Illegal operator: {2}" );
			setErrorExpression( expression );
			setErrorExpressionPosition( position );
		}
	}

	/**
	 * Base class for unbalanced exception, meaning a token or keyword was expected but not found.
	 * The {@link #getErrorExpressionPosition()} will contain the position of where the token was expected.
	 */
	public static abstract class ExpressionUnbalanced extends EntityQueryParsingException
	{
		public ExpressionUnbalanced( String message ) {
			super( message );
		}
	}

	public static class MissingOperator extends ExpressionUnbalanced
	{
		public MissingOperator( String field, int expectedPosition ) {
			super( "Missing operator for: {2}" );
			setErrorExpression( field );
			setErrorExpressionPosition( expectedPosition );
		}
	}

	public static class MissingValue extends ExpressionUnbalanced
	{
		public MissingValue( String expression, int expectedPosition ) {
			super( "Missing value after: {2}" );
			setErrorExpression( expression );
			setErrorExpressionPosition( expectedPosition );
		}
	}

	public static class MissingField extends ExpressionUnbalanced
	{
		public MissingField( int expectedPosition ) {
			super( "Missing expected field at position {3}" );
			setErrorExpressionPosition( expectedPosition );
		}
	}

	public static class MissingKeyword extends ExpressionUnbalanced
	{
		public MissingKeyword( String keyword, String expression, int expectedPosition ) {
			super( "Missing keyword " + keyword + " before: {2}" );
			setErrorExpression( expression );
			setErrorExpressionPosition( expectedPosition );
		}
	}

	public static class MissingToken extends ExpressionUnbalanced
	{
		public MissingToken( String token, String expression, int expectedPosition ) {
			super( "Missing token " + token + " after: {2}" );
			setErrorExpression( expression );
			setErrorExpressionPosition( expectedPosition );
		}
	}
}
