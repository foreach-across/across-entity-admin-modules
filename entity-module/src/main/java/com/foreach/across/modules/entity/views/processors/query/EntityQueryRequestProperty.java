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

import com.foreach.across.modules.entity.query.EQGroup;
import com.foreach.across.modules.entity.query.EQValue;
import com.foreach.across.modules.entity.query.EntityQueryCondition;
import com.foreach.across.modules.entity.query.EntityQueryOps;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.foreach.across.modules.entity.query.EntityQueryOps.IS_EMPTY;
import static com.foreach.across.modules.entity.query.EntityQueryOps.IS_NULL;

/**
 * Holds the raw and translated {@link EntityQueryCondition} for a given property inside an {@link com.foreach.across.modules.entity.query.EntityQuery} of an {@link EntityQueryRequest}.
 * *
 *
 * @author Steven Gentens
 * @since 2.2.0
 */
@Getter
@RequiredArgsConstructor
public class EntityQueryRequestProperty
{
	private final List<EntityQueryCondition> rawConditions = new ArrayList<>();
	private final List<EntityQueryCondition> translatedConditions = new ArrayList<>();
	private final List<Object> rawValues = new ArrayList<>();
	private final List<Object> translatedValues = new ArrayList<>();
	private final String propertyName;

	public void addRawCondition( EntityQueryCondition condition ) {
		rawConditions.add( condition );
	}

	public void addTranslatedCondition( EntityQueryCondition condition ) {
		translatedConditions.add( condition );
	}

	public int getRawConditionCount() {
		return rawConditions.size();
	}

	public int getTranslatedConditionCount() {
		return translatedConditions.size();
	}

	public void addRawValues( Object[] arguments ) {
		rawValues.addAll( Arrays.asList( arguments ) );
	}

	public void addTranslatedValues( Object[] arguments ) {
		translatedValues.addAll( Arrays.asList( arguments ) );
	}

	public boolean hasSingleRawValue() {
		return rawValues.size() == 1;
	}

	public Object getSingleRawValue() {
		return hasSingleRawValue() ? rawValues.get( 0 ) : null;
	}

	/**
	 * Checks whether there is a null value present.
	 * If there is a single raw value that is an {@link EQGroup}, the values within the group will be searched.
	 *
	 * @return true if it is an {@link EntityQueryOps#IS_NULL} or {@link EntityQueryOps#IS_EMPTY} or whether the raw values contain {@link EQValue#NULL}.
	 */
	public boolean hasNullValue() {
		if ( hasSingleRawValue() ) {
			Object singleValue = getSingleRawValue();
			return singleValue instanceof EQGroup && ArrayUtils.contains( ( (EQGroup) singleValue ).getValues(), EQValue.NULL );
		}

		return isSingleConditionWithOperand( IS_NULL ) ||
				isSingleConditionWithOperand( IS_EMPTY ) ||
				rawValues.contains( EQValue.NULL );

	}

	public boolean hasSingleTranslatedValue() {
		return translatedValues.size() == 1;
	}

	public Object getSingleTranslatedValue() {
		return hasSingleTranslatedValue() ? translatedValues.get( 0 ) : null;
	}

	public boolean isSingleConditionWithOperand( EntityQueryOps operand ) {
		if ( rawConditions.size() == 1 ) {
			EntityQueryOps conditionOperand = rawConditions.get( 0 ).getOperand();
			return operand.equals( conditionOperand ) || canConvertOperand( conditionOperand, operand );
		}
		return false;
	}

	public boolean hasTranslatedValues() {
		return !translatedValues.isEmpty();
	}

	private boolean canConvertOperand( EntityQueryOps from, EntityQueryOps to ) {
		EQTranslationRule rule = EQTranslationRule.getTranslationRuleFor( from, to );
		return rule != null && rule.canConvert( rawValues );
	}

}
