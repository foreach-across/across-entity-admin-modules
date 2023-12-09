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

import com.foreach.across.modules.entity.registry.properties.SimpleEntityPropertyDescriptor;
import com.foreach.across.testmodules.springdata.business.Company;
import com.foreach.across.testmodules.springdata.business.Representative;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.LinkedHashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class TestAssociatedEntityQueryExecutor
{
	@Test
	public void fixedPropertyAlwaysReturnsFullList() {
		AssociatedEntityQueryExecutor executor = AssociatedEntityQueryExecutor.forBeanProperty( new SimpleEntityPropertyDescriptor( "representatives" ) );

		Company company = new Company();
		Representative one = new Representative();
		one.setId( "2" );
		Representative two = new Representative();
		company.setRepresentatives( new LinkedHashSet<>( Arrays.asList( one, two ) ) );

		assertEquals(
				Arrays.asList( one, two ),
				executor.findAll( company, EntityQuery.all() )
		);

		assertEquals(
				new PageImpl<>( Arrays.asList( one, two ) ),
				executor.findAll( company, EntityQuery.all(), PageRequest.of( 0, 2 ) )
		);
	}
}
