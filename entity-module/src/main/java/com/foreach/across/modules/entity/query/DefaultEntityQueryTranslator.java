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

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.util.EntityUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.stream.Stream;

import static com.foreach.across.modules.entity.query.EntityQueryOps.*;

/**
 * Default implementation of {@link EntityQueryTranslator} that uses the metadata from a {@link EntityPropertyRegistry}
 * to retrieve the property type information and an {@link EQTypeConverter} to convert raw arguments into typed values.
 * <p/>
 * If a {@link EntityPropertyDescriptor} has an {@link EntityQueryConditionTranslator} attribute, the processed
 * {@link EntityQueryCondition} will be run through that translator as well.
 * <p/>
 * Note that as of {@code 2.2.0} translation happens recursively on sub queries returned: if a condition translation results in a sub query,
 * the individual conditions of that sub query will also be translated.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class DefaultEntityQueryTranslator implements EntityQueryTranslator
{
	private EQTypeConverter typeConverter;
	private EntityPropertyRegistry propertyRegistry;

	public void setPropertyRegistry( EntityPropertyRegistry propertyRegistry ) {
		this.propertyRegistry = propertyRegistry;
	}

	public void setTypeConverter( EQTypeConverter typeConverter ) {
		this.typeConverter = typeConverter;
	}

	@PostConstruct
	public void validateProperties() {
		Assert.notNull( propertyRegistry, "propertyRegistry should be available" );
		Assert.notNull( typeConverter, "typeConverter should be available" );
	}

	/**
	 * Translate the raw query into strongly typed version.
	 *
	 * @param rawQuery instance
	 * @return translated query
	 */
	@Override
	public EntityQuery translate( EntityQuery rawQuery ) {
		EntityQuery translated = new EntityQuery();
		translated.setOperand( rawQuery.getOperand() );

		for ( EntityQueryExpression expression : rawQuery.getExpressions() ) {
			if ( expression instanceof EntityQueryCondition ) {
				EntityQueryExpression translatedCondition = translateSingleCondition( (EntityQueryCondition) expression );
				if ( translatedCondition != null ) {
					if ( translatedCondition instanceof EntityQuery ) {
						translated.add( translate( (EntityQuery) translatedCondition ) );
					}
					else {
						translated.add( translatedCondition );
					}
				}
			}
			else if ( expression instanceof EntityQuery ) {
				translated.add( translate( (EntityQuery) expression ) );
			}
		}

		if ( rawQuery.hasSort() ) {
			translated.setSort( EntityUtils.translateSort( rawQuery.getSort(), propertyRegistry ) );
		}

		return translated;
	}

	private EntityQueryExpression translateSingleCondition( EntityQueryCondition condition ) {
		EntityPropertyDescriptor descriptor = propertyRegistry.getProperty( condition.getProperty() );

		if ( descriptor == null ) {
			throw new EntityQueryParsingException.IllegalField( condition.getProperty() );
		}

		if ( ( IN.equals( condition.getOperand() ) || EntityQueryOps.NOT_IN.equals( condition.getOperand() ) )
				&& collectionContainsNullValue( condition.getArguments() ) ) {
			return expandCollectionExpressionWithNullValue( condition );
		}

		EntityQueryCondition translated = new EntityQueryCondition();
		translated.setProperty( descriptor.getName() );

		TypeDescriptor expectedType = descriptor.getPropertyTypeDescriptor();

		translated.setOperand( findTypeSpecificOperand( condition.getOperand(), expectedType ) );

		if ( condition.hasArguments() ) {
			translated.setArguments( typeConverter.convertAll( expectedType, true, condition.getArguments() ) );
		}

		EntityQueryConditionTranslator conditionTranslator = descriptor.getAttribute( EntityQueryConditionTranslator.class );

		if ( conditionTranslator != null ) {
			EntityQueryExpression expression = conditionTranslator.translate( translated );

			if ( expression instanceof EntityQueryCondition ) {
				translated = (EntityQueryCondition) expression;
			}
			else {
				return expression;
			}
		}

		convertTextContainsToLike( translated, expectedType );

		return translated;
	}

	private boolean collectionContainsNullValue( Object[] arguments ) {
		if ( arguments.length == 1 && arguments[0] instanceof EQGroup ) {
			return collectionContainsNullValue( ( (EQGroup) arguments[0] ).getValues() );
		}
		return ArrayUtils.contains( arguments, EQValue.NULL ) || ArrayUtils.contains( arguments, null );
	}

	private EntityQueryExpression expandCollectionExpressionWithNullValue( EntityQueryCondition condition ) {
		Object[] nonNullArguments = filterCollectionArguments( condition.getArguments() );

		EntityQuery query = new EntityQuery( IN.equals( condition.getOperand() ) ? OR : AND );
		if ( nonNullArguments.length > 0 ) {
			query.add( new EntityQueryCondition( condition.getProperty(), condition.getOperand(), nonNullArguments ) );
		}
		query.add( new EntityQueryCondition( condition.getProperty(), IN.equals( condition.getOperand() ) ? IS_NULL : IS_NOT_NULL ) );

		return query;
	}

	private Object[] filterCollectionArguments( Object[] arguments ) {
		if ( arguments.length == 1 && arguments[0] instanceof EQGroup ) {
			return new Object[] { new EQGroup(
					Stream.of( filterCollectionArguments( ( (EQGroup) arguments[0] ).getValues() ) )
					      .map( EQType.class::cast )
					      .toArray( EQType[]::new )
			) };
		}
		return Stream.of( arguments ).filter( arg -> !EQValue.NULL.equals( arg ) && arg != null ).toArray();
	}

	private EntityQueryOps findTypeSpecificOperand( EntityQueryOps operand, TypeDescriptor expectedType ) {
		if ( IS_EMPTY.equals( operand ) ) {
			return !expectedType.isCollection() && !expectedType.isArray() ? IS_NULL : IS_EMPTY;
		}
		else if ( IS_NOT_EMPTY.equals( operand ) ) {
			return !expectedType.isCollection() && !expectedType.isArray() ? IS_NOT_NULL : IS_NOT_EMPTY;
		}

		return operand;
	}

	private void convertTextContainsToLike( EntityQueryCondition condition, TypeDescriptor expectedType ) {
		if ( String.class.equals( expectedType.getType() ) ) {
			if ( EntityQueryOps.CONTAINS.equals( condition.getOperand() ) ) {
				condition.setOperand( EntityQueryOps.LIKE );
				condition.setArguments( new Object[] { "%" + escape( (String) condition.getFirstArgument() ) + "%" } );
			}
			else if ( EntityQueryOps.NOT_CONTAINS.equals( condition.getOperand() ) ) {
				condition.setOperand( EntityQueryOps.NOT_LIKE );
				condition.setArguments( new Object[] { "%" + escape( (String) condition.getFirstArgument() ) + "%" } );
			}
		}
	}

	private String escape( String text ) {
		return StringUtils.replace( text, "%", "\\%" );
	}
}
