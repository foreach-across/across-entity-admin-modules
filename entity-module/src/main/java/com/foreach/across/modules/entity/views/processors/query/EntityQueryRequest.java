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

import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.query.EntityQueryCondition;
import com.foreach.across.modules.entity.query.EntityQueryExpression;
import com.foreach.across.modules.entity.query.EntityQueryOps;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Simple data object wrapping the different {@link EntityQuery} parameters that get submitted for
 * an {@link com.foreach.across.modules.entity.views.processors.EntityQueryFilterProcessor}.
 *
 * @author Arne Vandamme
 * @since 2.2.0
 */
@Data
public class EntityQueryRequest
{
	private final Map<String, EntityQueryRequestProperty> selectedProperties = new HashMap<>();

	private boolean showBasicFilter;
	private boolean convertibleToBasicMode = true;
	private EntityQuery basePredicate;
	private EntityQuery rawQuery;
	private EntityQuery translatedRawQuery;
	private EntityQuery executableQuery;

	public EntityQueryRequestProperty getSelectedProperty( String propertyName ) {
		return selectedProperties.get( propertyName );
	}

	public void setRawQuery( EntityQuery rawQuery ) {
		this.rawQuery = rawQuery;
		parseSelectedProperties( rawQuery, EntityQueryRequestProperty::addRawCondition, EntityQueryRequestProperty::addRawValues );
		detectInconvertibleQuery( rawQuery );
	}

	private void detectInconvertibleQuery( EntityQueryExpression expression ) {
		if ( expression instanceof EntityQuery ) {
			if ( EntityQueryOps.OR.equals( expression.getOperand() ) ) {
				convertibleToBasicMode = false;
			}
			List<EntityQueryExpression> childExpressions = ( (EntityQuery) expression ).getExpressions();
			childExpressions.forEach( this::detectInconvertibleQuery );
		}
	}

	public void setTranslatedRawQuery( EntityQuery translatedRawQuery ) {
		this.translatedRawQuery = translatedRawQuery;
		parseSelectedProperties( translatedRawQuery, EntityQueryRequestProperty::addTranslatedCondition, EntityQueryRequestProperty::addTranslatedValues );
	}

	private void parseSelectedProperties( EntityQueryExpression expression,
	                                      BiConsumer<EntityQueryRequestProperty, EntityQueryCondition> addCondition,
	                                      BiConsumer<EntityQueryRequestProperty, Object[]> addValues ) {
		if ( expression instanceof EntityQueryCondition ) {
			parseSelectedProperty( (EntityQueryCondition) expression, addCondition, addValues );
		}
		else if ( expression instanceof EntityQuery ) {
			List<EntityQueryExpression> childExpressions = ( (EntityQuery) expression ).getExpressions();
			childExpressions.forEach( e -> parseSelectedProperties( e, addCondition, addValues ) );
		}
	}

	private void parseSelectedProperty( EntityQueryCondition condition,
	                                    BiConsumer<EntityQueryRequestProperty, EntityQueryCondition> addCondition,
	                                    BiConsumer<EntityQueryRequestProperty, Object[]> addValues ) {
		EntityQueryRequestProperty property = selectedProperties.computeIfAbsent( condition.getProperty(), EntityQueryRequestProperty::new );
		addCondition.accept( property, condition );
		addValues.accept( property, condition.getArguments() );
	}
}
