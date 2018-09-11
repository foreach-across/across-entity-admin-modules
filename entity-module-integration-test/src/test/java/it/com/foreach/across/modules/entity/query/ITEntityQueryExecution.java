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

import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.query.EntityQueryConditionTranslator;
import com.foreach.across.modules.entity.query.EntityQueryExecutor;
import com.foreach.across.modules.entity.query.EntityQueryParser;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyRegistry;
import com.foreach.across.testmodules.springdata.business.Company;
import com.foreach.across.testmodules.springdata.business.Representative;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

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
public class ITEntityQueryExecution extends AbstractQueryTest
{
	@Autowired
	private EntityRegistry entityRegistry;

	@Test
	public void findAll() {
		findCompanies( "", one, two, three );
	}

	@Test
	public void findAllCompaniesOrdered() {
		List<Company> ordered = findCompanies( "order by id desc", one, two, three );
		assertEquals( two, ordered.get( 0 ) );
		assertEquals( three, ordered.get( 1 ) );
		assertEquals( one, ordered.get( 2 ) );
	}

	@Test
	public void findCompaniesOrdered() {
		List<Company> ordered = findCompanies( "number > 1 order by created asc", two, three );
		assertEquals( two, ordered.get( 0 ) );
		assertEquals( three, ordered.get( 1 ) );
	}

	@Test
	public void findAllRepresentativesOrdered() {
		List<Representative> ordered = findRepresentatives( "order by name desc", john, joe, peter, weirdo );
		assertEquals( peter, ordered.get( 0 ) );
		assertEquals( john, ordered.get( 1 ) );
		assertEquals( joe, ordered.get( 2 ) );
		assertEquals( weirdo, ordered.get( 3 ) );
	}

	@Test
	public void findRepresentativesOrdered() {
		List<Representative> ordered = findRepresentatives( "id like 'j%' order by name asc", john, joe );
		assertEquals( joe, ordered.get( 0 ) );
		assertEquals( john, ordered.get( 1 ) );
	}

	@Test
	public void companyByGroup() {
		findCompanies( "group.name = 'groupOne'", one, two );
	}

	@Test
	public void eq() {
		findCompanies( "id = two", two );
		findCompanies( "id = 'two'", two );
	}

	@Test
	public void neq() {
		findCompanies( "id != two", one, three );
	}

	@Test
	public void numericOperands() {
		findCompanies( "number > 1", two, three );
		findCompanies( "number >= 1", one, two, three );
		findCompanies( "number < 3", one, two );
		findCompanies( "number <= 3", one, two, three );
	}

	@Test
	public void dateOperands() {
		findCompanies( "created = '2015-01-17 13:30'", one );
		findCompanies( "created != '2015-01-17 13:30'", two, three );
		findCompanies( "created > '2015-01-17 13:30'", two, three );
		findCompanies( "created >= '2015-01-17 13:30'", one, two, three );
		findCompanies( "created < '2035-04-04 14:00'", one, two );
		findCompanies( "created <= '2035-04-04 14:00'", one, two, three );
		findCompanies( "created > today()", three );
		findCompanies( "created > now()", three );
	}

	@Test
	public void in() {
		findCompanies( "id in (one, two)", one, two );
	}

	@Test
	public void notIn() {
		findCompanies( "id not in ('one', 'two')", three );
	}

	@Test
	public void like() {
		findCompanies( "id like 'on%'", one );
		findCompanies( "id like '%wo'", two );
		findCompanies( "id like '%o%'", one, two );
	}

	@Test
	public void containsOnText() {
		findCompanies( "id contains 'o'", one, two );
		findCompanies( "id not contains 'o'", three );
	}

	@Test
	public void notLike() {
		findCompanies( "id not like 'on%'", two, three );
		findCompanies( "id not like '%wo'", one, three );
		findCompanies( "id not like '%o%'", three );
	}

	@Test
	public void likeIgnoreCase() {
		findCompanies( "id ilike 'oN%'", one );
		findCompanies( "id ilike '%Wo'", two );
		findCompanies( "id ilike '%O%'", one, two );
	}

	@Test
	public void notLikeIgnoreCase() {
		findCompanies( "id not ilike 'oN%'", two, three );
		findCompanies( "id not ilike '%Wo'", one, three );
		findCompanies( "id not ilike '%O%'", three );
	}

	@Test
	public void contains() {
		findCompanies( "representatives contains " + john.getId(), one, two );
		findCompanies( "representatives contains (" + john.getId() + "," + joe.getId() + ")", one, two );
		findCompanies( "representatives contains (" + joe.getId() + "," + peter.getId() + ")", two );
	}

	@Test
	public void notContains() {
		findCompanies( "representatives not contains " + john.getId(), three );
		findCompanies( "representatives not contains (" + john.getId() + "," + joe.getId() + ")", one, three );
		findCompanies( "representatives not contains (" + joe.getId() + "," + peter.getId() + ")", one, three );
	}

	@Test
	public void combined() {
		findCompanies( "id != 'two' and representatives contains " + john.getId(), one );
	}

	@Test
	public void currentUser() {
		try {
			Authentication authentication = mock( Authentication.class );
			SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
			securityContext.setAuthentication( authentication );
			SecurityContextHolder.setContext( securityContext );

			when( authentication.getName() ).thenReturn( "one" );

			findCompanies( "id = currentUser()", one );
			findCompanies( "id in (currentUser(), 'three')", one, three );
		}
		finally {
			SecurityContextHolder.clearContext();
		}
	}

	@Test
	public void enumValues() {
		findCompanies( "status = BROKE", two );
		findCompanies( "status = IN_BUSINESS", one );
		findCompanies( "status not in (BROKE)", one );
	}

	@Test
	public void isNull() {
		findCompanies( "status is empty", three );
		findCompanies( "status is EMPTY", three );
		findCompanies( "status is NULL", three );
		findCompanies( "status is null", three );
		findCompanies( "status is not empty", one, two );
		findCompanies( "status is not EMPTY", one, two );
		findCompanies( "status is not NULL", one, two );
		findCompanies( "status is not null", one, two );
	}

	@Test
	public void nullValuesInInOperand() {
		findCompanies( "status in (BROKE,IN_BUSINESS)", one, two );
		findCompanies( "status in (NULL)", three );
		findCompanies( "status in (null, BROKE, IN_BUSINESS)", one, two, three );
		findCompanies( "status not in (null)", one, two );
	}

	@Test
	public void isEmpty() {
		findCompanies( "representatives is empty", three );
		findCompanies( "representatives is EMPTY", three );
		findCompanies( "representatives is not empty", one, two );
		findCompanies( "representatives is not EMPTY", one, two );
	}

	@Test
	public void characterEscaping() {
		findRepresentatives( "name = 'John % Surname'", john );
		findRepresentatives( "name = 'Joe \\' Surname'", joe );
		findRepresentatives( "name = 'Peter \\\\ Surname'", peter );
		findRepresentatives( "name like 'John \\\\% Surname'", john );
		findRepresentatives( "name like 'Joe \\' Surname'", joe );
		findRepresentatives( "name like 'Peter \\\\\\\\ Surname'", peter );
		findRepresentatives( "name like '% Surname'", john, joe, peter );
		findRepresentatives( "name like '%\\\\% Surname'", john );
		findRepresentatives( "name like '!\"#\\%-_&/()=;?´`|/\\\\\\\\\\''", weirdo );
		findRepresentatives( "name like '%_%'", weirdo );
		findRepresentatives( "name like '%\\\\\\\\%'", weirdo, peter );
	}

	@Test
	public void containsWithCharacterEscaping() {
		findRepresentatives( "name contains 'John % Surname'", john );
		findRepresentatives( "name contains 'Joe \\' Surname'", joe );
		findRepresentatives( "name contains 'Peter \\\\\\\\ Surname'", peter );
		findRepresentatives( "name contains '%'", john, weirdo );
	}

	@Test
	public void ignoreCaseOnProperty() {
		EntityConfiguration entityConfiguration = entityRegistry.getEntityConfiguration( Representative.class );
		MutableEntityPropertyRegistry propertyRegistry = (MutableEntityPropertyRegistry) entityConfiguration.getPropertyRegistry();

		MutableEntityPropertyDescriptor descriptor = propertyRegistry.getProperty( "name" );
		try {
			descriptor.setAttribute( EntityQueryConditionTranslator.class, EntityQueryConditionTranslator.ignoreCase() );

			findRepresentatives( "name = 'john % surname'", john );
			findRepresentatives( "name = 'joe \\' surname'", joe );
			findRepresentatives( "name = 'peter \\\\ surname'", peter );
			findRepresentatives( "name like 'john \\\\% surname'", john );
			findRepresentatives( "name like 'joe \\' surname'", joe );
			findRepresentatives( "name like 'peter \\\\\\\\ surname'", peter );
			findRepresentatives( "name like '% surname'", john, joe, peter );
			findRepresentatives( "name like '%\\\\% surname'", john );
			findRepresentatives( "name contains 'SURNAME'", john, joe, peter );

			// AXEUM-128 - a combination of expanding to name property should still take into account the case insensitivity
			findRepresentatives( "searchText contains 'surname'", john, joe, peter );
			findRepresentatives( "searchText contains 'SURNAME'", john, joe, peter );
		}
		finally {
			descriptor.removeAttribute( EntityQueryConditionTranslator.class );
		}
	}

	private List<Company> findCompanies( String query, Company... expected ) {
		EntityConfiguration entityConfiguration = entityRegistry.getEntityConfiguration( Company.class );
		EntityQueryExecutor<Company> queryExecutor = entityConfiguration.getAttribute( EntityQueryExecutor.class );
		EntityQueryParser queryParser = entityConfiguration.getAttribute( EntityQueryParser.class );

		List<Company> found = queryExecutor.findAll( queryParser.parse( query ) );
		assertEquals( expected.length, found.size() );
		assertTrue( found.containsAll( Arrays.asList( expected ) ) );

		EntityQuery rawQuery = EntityQuery.parse( query );
		EntityQuery executableQuery = queryParser.prepare( rawQuery );
		assertEquals( executableQuery, queryParser.prepare( executableQuery ) );
		assertEquals( found, queryExecutor.findAll( executableQuery ) );

		return found;
	}

	private List<Representative> findRepresentatives( String query, Representative... expected ) {
		EntityConfiguration entityConfiguration = entityRegistry.getEntityConfiguration( Representative.class );
		EntityQueryExecutor<Representative> queryExecutor = entityConfiguration.getAttribute( EntityQueryExecutor.class );
		EntityQueryParser queryParser = entityConfiguration.getAttribute( EntityQueryParser.class );

		List<Representative> found = queryExecutor.findAll( queryParser.parse( query ) );
		assertEquals( expected.length, found.size() );
		assertTrue( found.containsAll( Arrays.asList( expected ) ) );

		EntityQuery rawQuery = EntityQuery.parse( query );
		EntityQuery executableQuery = queryParser.prepare( rawQuery );
		assertEquals( executableQuery, queryParser.prepare( executableQuery ) );
		assertEquals( found, queryExecutor.findAll( executableQuery ) );

		return found;
	}
}
