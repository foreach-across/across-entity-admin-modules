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
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

import static com.foreach.across.modules.entity.query.EntityQueryOps.*;

/**
 * Default implementation of {@link EntityQueryTranslator} that uses the metadata from a {@link EntityPropertyRegistry}
 * to retrieve the property type information and an {@link EQTypeConverter} to convert raw arguments into typed values.
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
		Assert.notNull( propertyRegistry );
		Assert.notNull( typeConverter );
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
				translated.add( translateSingleCondition( (EntityQueryCondition) expression ) );
			}
			else if ( expression instanceof EntityQuery ) {
				translated.add( translate( (EntityQuery) expression ) );
			}
		}

		return translated;
	}

	private EntityQueryExpression translateSingleCondition( EntityQueryCondition condition ) {
		EntityPropertyDescriptor descriptor = propertyRegistry.getProperty( condition.getProperty() );

		if ( descriptor == null ) {
			throw new EntityQueryParsingException.IllegalField( condition.getProperty() );
		}

		EntityQueryCondition translated = new EntityQueryCondition();
		translated.setProperty( descriptor.getName() );

		TypeDescriptor expectedType = descriptor.getPropertyTypeDescriptor();
		translated.setOperand( findTypeSpecificOperand( condition.getOperand(), expectedType ) );

		if ( condition.hasArguments() ) {
			translated.setArguments( typeConverter.convertAll( expectedType, true, condition.getArguments() ) );
		}
		return translated;
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
}
