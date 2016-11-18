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

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Reverse parsing of a {@link String} into an {@link EntityQuery}.
 * Uses a {@link DefaultEntityQueryMetadataProvider} if none is set.  For improved functionality it is highly advised
 * to have a custom {@link EntityQueryMetadataProvider} implementation.
 * <p/>
 * Supports both a *raw* entity query and an *translated* entity query.
 *
 * @author Arne Vandamme
 * @see DefaultEntityQueryMetadataProvider
 * @since 2.0.0
 */
public class EntityQueryParser
{
	private static final EntityQueryTokenizer tokenizer = new EntityQueryTokenizer();
	private static final EntityQueryTokenConverter converter = new EntityQueryTokenConverter();

	private ConversionService conversionService;

	private EntityQueryMetadataProvider metadataProvider;

	private EntityConfiguration entityConfiguration;

	@Autowired
	public void setConversionService( ConversionService conversionService ) {
		this.conversionService = conversionService;
	}

	private enum Expecting
	{
		Property,
		Operator,
		Value
	}

	public void setEntityConfiguration( EntityConfiguration entityConfiguration ) {
		this.entityConfiguration = entityConfiguration;
		setMetadataProvider( new DefaultEntityQueryMetadataProvider( entityConfiguration.getPropertyRegistry() ) );
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
	 * Convert a query string into a typed and validated {@link EntityQuery}.  The query returned will be raw,
	 * meaning that actual values might not be converted. The {@link #setMetadataProvider(EntityQueryMetadataProvider)}
	 * value will be used for validating the query and converting the string values to correctly typed ones.
	 * An exception will be thrown if the query cannot be converted.
	 *
	 * @param queryString string representation of the query
	 * @return query instance
	 * @throws IllegalArgumentException if parsing fails
	 */
	public EntityQuery parse( String queryString ) {
		List<EntityQueryTokenizer.TokenMetadata> tokens = tokenizer.tokenize( queryString );
		EntityQuery rawQuery = converter.convertTokens( tokens );
		validatePropertiesAndOperators( rawQuery );

		return translate( rawQuery );
	}

	private EntityQuery translate( EntityQuery rawQuery ) {
		EntityQuery translated = new EntityQuery();
		translated.setOperand( rawQuery.getOperand() );

		for ( EntityQueryExpression expression : rawQuery.getExpressions() ) {
			if ( expression instanceof EntityQueryCondition ) {
				EntityQueryCondition condition = (EntityQueryCondition) expression;
				EntityPropertyDescriptor descriptor = entityConfiguration.getPropertyRegistry().getProperty(
						condition.getProperty() );

				if ( descriptor == null ) {
					throw new IllegalArgumentException( "Unknown property: " + condition.getProperty() );
				}

				EntityQueryConditionTranslator translator = new EntityQueryConditionTranslator( descriptor,
				                                                                                conversionService );

				translated.add( translator.translate( condition ) );
			}
			else if ( expression instanceof EntityQuery ) {
				translated.add( translate( (EntityQuery) expression ) );
			}
		}

		return translated;
	}

	private void validatePropertiesAndOperators( EntityQuery query ) {
		for ( EntityQueryExpression expression : query.getExpressions() ) {
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
				if ( !metadataProvider.isValidValueForPropertyAndOperator( condition.getFirstArgument(),
				                                                           condition.getProperty(),
				                                                           condition.getOperand() ) ) {
					throw new IllegalArgumentException( "Illegal value for operator " + condition
							.getOperand() + " and property: " + condition.getProperty() );
				}
			}
			else {
				validatePropertiesAndOperators( (EntityQuery) expression );
			}
		}
	}
}
