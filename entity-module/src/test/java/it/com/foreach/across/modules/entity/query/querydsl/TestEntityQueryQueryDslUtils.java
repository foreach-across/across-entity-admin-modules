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

package it.com.foreach.across.modules.entity.query.querydsl;

import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.query.querydsl.EntityQueryQueryDslUtils;
import it.com.foreach.across.modules.entity.query.jpa.TestEntityQueryJpaUtils;
import testmodules.springdata.business.Company;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Arne Vandamme
 */
public class TestEntityQueryQueryDslUtils extends TestEntityQueryJpaUtils
{
	@Override
	protected void assertQueryResults( EntityQuery query, Company... companies ) {
		List<Company> found = (List<Company>) companyRepository.findAll(
				EntityQueryQueryDslUtils.toPredicate( query, Company.class, "company" ) );
		assertEquals( companies.length, found.size() );
		assertTrue( found.containsAll( Arrays.asList( companies ) ) );
	}
}
