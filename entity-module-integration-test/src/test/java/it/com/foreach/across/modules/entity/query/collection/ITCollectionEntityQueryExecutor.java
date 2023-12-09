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

package it.com.foreach.across.modules.entity.query.collection;

import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.query.EntityQueryExecutor;
import com.foreach.across.modules.entity.query.EntityQueryParser;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.testmodules.springdata.business.Car;
import it.com.foreach.across.modules.entity.query.AbstractQueryTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Arne Vandamme
 * @since 3.3.0
 */
public class ITCollectionEntityQueryExecutor extends AbstractQueryTest
{
	@Autowired
	private EntityRegistry entityRegistry;

	@Test
	public void findAll() {
		assertCarsFound( "", carOne, carTwo );
	}

	@Test
	public void findAllOrdered() {
		assertCarsFound( "order by id desc", carTwo, carOne );
		assertCarsFound( "order by id asc", carOne, carTwo );
	}

	@Test
	public void findByName() {
		assertCarsFound( "id = 'one'", carOne );
		assertCarsFound( "id like 't%'", carTwo );
		assertCarsFound( "id not in ('one', 'two')" );
	}

	@Test
	public void findByCompanyName() {
		assertCarsFound( "company.id = 'one'", carOne );
		assertCarsFound( "company.id = 'one' and id != 'two'", carOne );
		assertCarsFound( "company.id = 'two' and id != 'two'" );
	}

	private void assertCarsFound( String query, Car... expected ) {
		EntityConfiguration entityConfiguration = entityRegistry.getEntityConfiguration( Car.class );
		EntityQueryExecutor<Car> queryExecutor = entityConfiguration.getAttribute( EntityQueryExecutor.class );
		EntityQueryParser queryParser = entityConfiguration.getAttribute( EntityQueryParser.class );

		List<Car> found = queryExecutor.findAll( queryParser.parse( query ) );
		assertEquals( expected.length, found.size() );
		assertTrue( found.containsAll( Arrays.asList( expected ) ) );

		EntityQuery rawQuery = EntityQuery.parse( query );
		EntityQuery executableQuery = queryParser.prepare( rawQuery );
		assertEquals( executableQuery, queryParser.prepare( executableQuery ) );
		assertEquals( found, queryExecutor.findAll( executableQuery ) );
	}
}
