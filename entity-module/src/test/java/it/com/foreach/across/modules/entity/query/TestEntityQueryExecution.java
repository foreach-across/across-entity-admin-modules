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

package it.com.foreach.across.modules.entity.query;

import com.foreach.across.modules.entity.query.EntityQueryExecutor;
import com.foreach.across.modules.entity.query.EntityQueryParser;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import testmodules.springdata.business.Company;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class TestEntityQueryExecution extends AbstractQueryTest
{
	@Autowired
	private EntityRegistry entityRegistry;

	@Test
	public void findAll() {
		find( "", one, two, three );
	}

	@Test
	public void companyByGroup() {
		find( "group.name = 'groupOne'", one, two );
	}

	@Test
	public void eq() {
		find( "id = two", two );
		find( "id = 'two'", two );
	}

	@Test
	public void neq() {
		find( "id != two", one, three );
	}

	@Test
	public void numericOperands() {
		find( "number > 1", two, three );
		find( "number >= 1", one, two, three );
		find( "number < 3", one, two );
		find( "number <= 3", one, two, three );
	}

	@Test
	public void dateOperands() {
		find( "created = '2015-01-17 13:30'", one );
		find( "created != '2015-01-17 13:30'", two, three );
		find( "created > '2015-01-17 13:30'", two, three );
		find( "created >= '2015-01-17 13:30'", one, two, three );
		find( "created < '2035-04-04 14:00'", one, two );
		find( "created <= '2035-04-04 14:00'", one, two, three );
		find( "created > today()", three );
		find( "created > now()", three );
	}

	@Test
	public void in() {
		find( "id in (one, two)", one, two );
	}

	@Test
	public void notIn() {
		find( "id not in ('one', 'two')", three );
	}

	@Test
	public void like() {
		find( "id like 'on%'", one );
		find( "id like '%wo'", two );
		find( "id like '%o%'", one, two );
	}

	@Test
	public void notLike() {
		find( "id not like 'on%'", two, three );
		find( "id not like '%wo'", one, three );
		find( "id not like '%o%'", three );
	}

	@Test
	public void contains() {
		find( "representatives contains " + john.getId(), one, two );
	}

	@Test
	public void notContains() {
		find( "representatives not contains " + john.getId(), three );
	}

	@Test
	public void combined() {
		find( "id != 'two' and representatives contains " + john.getId(), one );
	}

	@Test
	public void currentUser() {
		try {
			Authentication authentication = mock( Authentication.class );
			SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
			securityContext.setAuthentication( authentication );
			SecurityContextHolder.setContext( securityContext );

			when( authentication.getName() ).thenReturn( "one" );

			find( "id = currentUser()", one );
			find( "id in (currentUser(), 'three')", one, three );
		}
		finally {
			SecurityContextHolder.clearContext();
		}
	}

	@Test
	public void enumValues() {
		find( "status = BROKE", two );
		find( "status = IN_BUSINESS", one );
		find( "status not in (BROKE)", one );
	}

	@Test
	public void isNull() {
		find( "status is empty", three );
		find( "status is EMPTY", three );
		find( "status is NULL", three );
		find( "status is null", three );
		find( "status is not empty", one, two );
		find( "status is not EMPTY", one, two );
		find( "status is not NULL", one, two );
		find( "status is not null", one, two );
	}

	@Test
	public void isEmpty() {
		find( "representatives is empty", three );
		find( "representatives is EMPTY", three );
		find( "representatives is not empty", one, two );
		find( "representatives is not EMPTY", one, two );
	}

	private void find( String query, Company... expected ) {
		EntityConfiguration entityConfiguration = entityRegistry.getEntityConfiguration( Company.class );
		EntityQueryExecutor<Company> queryExecutor = entityConfiguration.getAttribute( EntityQueryExecutor.class );
		EntityQueryParser queryParser = entityConfiguration.getAttribute( EntityQueryParser.class );

		List<Company> found = queryExecutor.findAll( queryParser.parse( query ) );
		assertEquals( expected.length, found.size() );
		assertTrue( found.containsAll( Arrays.asList( expected ) ) );
	}
}
