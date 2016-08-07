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

import org.springframework.util.Assert;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * Reverse parsing of a {@link String} into an {@link EntityQuery}.
 * Uses a {@link DefaultEntityMetadataProvider} if none is set.  For improved functionality it is highly advised
 * to have a custom {@link EntityQueryMetadataProvider} implementation.
 *
 * @author Arne Vandamme
 * @see DefaultEntityMetadataProvider
 * @since 2.0.0
 */
public class EntityQueryParser
{
	private static final EntityQueryTokenizer tokenizer = new EntityQueryTokenizer();

	private EntityQueryMetadataProvider metadataProvider = new DefaultEntityMetadataProvider();

	private enum Expecting
	{
		Property,
		Operator,
		Value
	}

	/**
	 * Set the actual {@link EntityQueryMetadataProvider} that should be used for validating and typing query strings.
	 *
	 * @param metadataProvider instance
	 */
	public void setMetadataProvider( EntityQueryMetadataProvider metadataProvider ) {
		Assert.notNull( metadataProvider );
		this.metadataProvider = metadataProvider;
	}

	/**
	 * Convert a query string into a typed and validated {@link EntityQuery}.
	 * The {@link #setMetadataProvider(EntityQueryMetadataProvider)} value will be used for validating the query
	 * and converting the string values to correctly typed ones.  An exception will be thrown if the query cannot
	 * be converted.
	 *
	 * @param queryString string representation of the query
	 * @return query instance
	 * @throws IllegalArgumentException if parsing fails
	 */
	public EntityQuery parse( String queryString ) {
		List<String> tokens = tokenizer.tokenize( queryString );

		EntityQuery query = convertTokensToRawQuery( tokens );
		validatePropertiesAndOperators( query );
		convertPropertyValues( query );

		return query;
	}

	private EntityQuery convertTokensToRawQuery( List<String> tokenList ) {
		Deque<String> tokens = new ArrayDeque<>( tokenList );

		EntityQuery query = new EntityQuery( EntityQueryOps.AND );
		EntityQueryCondition condition = new EntityQueryCondition();
		Expecting expecting = Expecting.Property;

		// single condition is field/operator/value
		while ( !tokens.isEmpty() ) {
			String token = tokens.removeFirst();

			switch ( expecting ) {
				case Property:
					condition.setProperty( token );
					expecting = Expecting.Operator;
					break;
				case Operator:
					condition.setOperand( retrieveOperatorFromToken( token ) );
					expecting = Expecting.Value;
					break;
				case Value:
					condition.setArguments( new Object[] { token } );
					query.add( condition );
					break;
			}
		}

		return query;
	}

	private void validatePropertiesAndOperators( EntityQuery query ) {
		query.getExpressions().forEach(
				expression -> {
					if ( expression instanceof EntityQueryCondition ) {
						EntityQueryCondition condition = (EntityQueryCondition) expression;
						if ( !metadataProvider.isValidProperty( condition.getProperty() ) ) {
							throw new IllegalArgumentException( "Unknown property: " + condition.getProperty() );
						}
						if ( !metadataProvider.isValidOperatorForProperty( condition.getOperand(),
						                                                   condition.getProperty() ) ) {
							throw new IllegalArgumentException(
									"Illegal operator " + condition.getOperand() + " for property: " + condition
											.getProperty() );
						}
					}
					else {
						validatePropertiesAndOperators( (EntityQuery) expression );
					}
				}
		);
	}

	private void convertPropertyValues( EntityQuery query ) {
		query.getExpressions().forEach(
				expression -> {
					if ( expression instanceof EntityQueryCondition ) {
						EntityQueryCondition condition = (EntityQueryCondition) expression;
						String rawValue = (String) condition.getFirstArgument();

						Object[] typedValues = metadataProvider.convertStringToTypedValue(
								condition.getProperty(), condition.getOperand(), rawValue
						);

						condition.setArguments( typedValues );
					}
					else {
						validatePropertiesAndOperators( (EntityQuery) expression );
					}
				}
		);
	}

	private EntityQueryOps retrieveOperatorFromToken( String token ) {
		return EntityQueryOps.forToken( token );
	}
}
