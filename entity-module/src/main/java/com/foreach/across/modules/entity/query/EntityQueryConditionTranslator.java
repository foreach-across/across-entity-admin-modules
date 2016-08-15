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
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

import java.util.ArrayList;
import java.util.List;

/**
 * Default translator that uses an {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor}
 * and {@link org.springframework.core.convert.ConversionService} for converting a condition with possibly raw
 * values to an executable expression.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class EntityQueryConditionTranslator
{
	private final EntityPropertyDescriptor descriptor;
	private final ConversionService conversionService;

	public EntityQueryConditionTranslator( EntityPropertyDescriptor descriptor,
	                                       ConversionService conversionService ) {
		this.descriptor = descriptor;
		this.conversionService = conversionService;
	}

	EntityQueryExpression translate( EntityQueryCondition condition ) {
		EntityQueryCondition translated = new EntityQueryCondition();
		translated.setProperty( descriptor.getName() );
		translated.setOperand( condition.getOperand() );

		TypeDescriptor expectedType = descriptor.getPropertyTypeDescriptor();
		translated.setArguments( resolveArgumentValues( expectedType, condition.getArguments() ) );

		return translated;
	}

	private Object[] resolveArgumentValues( TypeDescriptor expectedType, Object[] arguments ) {
		List<Object> resolved = new ArrayList<>();

		for ( Object argument : arguments ) {
			if ( argument instanceof EQValue ) {
				resolved.add(
						conversionService
								.convert( ( (EQValue) argument ).getValue(), TypeDescriptor.valueOf( String.class ),
								          expectedType )
				);
			}
			else if ( argument instanceof EQString ) {
				resolved.add( ( (EQString) argument ).getValue() );
			}
			else {
				resolved.add( argument );
			}
		}

		return resolved.toArray();
	}
}
