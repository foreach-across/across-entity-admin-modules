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

package com.foreach.across.modules.entity.views.processors.query;

import com.foreach.across.modules.entity.query.EQString;
import com.foreach.across.modules.entity.query.EQValue;
import com.foreach.across.modules.entity.query.EntityQueryOps;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Fetches the value for a {@link EntityPropertyDescriptor} from a {@link EntityQueryRequest} instance.
 *
 * @author Arne Vandamme
 * @since 2.2.0
 */
@RequiredArgsConstructor
public class EntityQueryRequestValueFetcher implements ValueFetcher<EntityQueryRequest>
{
	public static final Object NOT_FILTERED = EQValue.MISSING;//new Object();

	@NonNull
	private final EntityPropertyDescriptor propertyDescriptor;

	private final EntityQueryOps operand;

	private final boolean multiple;

	@Override
	public final Object getValue( EntityQueryRequest entityQueryRequest ) {
		EntityQueryRequestProperty selectedProperty = entityQueryRequest.getSelectedProperty( propertyDescriptor.getName() );

		if ( selectedProperty != null ) {
			if ( selectedProperty.isSingleConditionWithOperand( operand ) ) {
				if ( multiple && selectedProperty.hasTranslatedValues() ) {
					List<Object> result = new ArrayList<>( selectedProperty.getTranslatedValues() );
					if ( selectedProperty.hasNullValue() ) {
						result.add( 0, null );
					}
					return result;
				}
				else if ( selectedProperty.hasSingleTranslatedValue() ) {
					return retrieveSingleValue( selectedProperty );
				}
				else if ( !multiple && selectedProperty.hasSingleRawValue() ) {
					return retrieveSingleRawValue( selectedProperty );
				}
				else if ( selectedProperty.hasNullValue() && !selectedProperty.hasTranslatedValues() ) {
					return multiple ? Collections.singletonList( null ) : null;
				}
			}
			if ( selectedProperty.getRawConditionCount() > 0 ) {
				entityQueryRequest.setConvertibleToBasicMode( false );
			}
		}

		return EntityQueryRequestValueFetcher.NOT_FILTERED;
	}

	private Object retrieveSingleValue( EntityQueryRequestProperty selectedProperty ) {
		Object singleValue = selectedProperty.getSingleTranslatedValue();

		if ( singleValue instanceof String && selectedProperty.hasSingleRawValue() ) {
			Object originalValue = selectedProperty.getSingleRawValue();
			if ( originalValue instanceof EQString ) {
				return ( (EQString) originalValue ).getValue();
			}
		}

		return singleValue;
	}

	private Object retrieveSingleRawValue( EntityQueryRequestProperty selectedProperty ) {
		Object singleValue = selectedProperty.getSingleRawValue();

		if ( singleValue instanceof EQString ) {
			return ( (EQString) singleValue ).getValue();
		}
		else if ( singleValue instanceof EQValue ) {
			return ( (EQValue) singleValue ).getValue();
		}

		return singleValue;
	}
}
