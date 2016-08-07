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

/**
 * Default lenient implementation of {@link EntityQueryMetadataProvider} that has no knowledge of property types
 * or operators.  It accepts all combinations of the two.  Non-string values are assumed to be a number.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class DefaultEntityMetadataProvider implements EntityQueryMetadataProvider
{
	@Override
	public boolean isValidProperty( String property ) {
		return false;
	}

	@Override
	public boolean isValidOperatorForProperty( EntityQueryOps operator, String property ) {
		return false;
	}

	@Override
	public Object[] convertStringToTypedValue( String property, EntityQueryOps operator, String rawValue ) {
		return new Object[0];
	}
}
