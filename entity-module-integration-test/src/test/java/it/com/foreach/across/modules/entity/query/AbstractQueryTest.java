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

import com.foreach.across.testmodules.springdata.business.Company;
import com.foreach.across.testmodules.springdata.business.CompanyStatus;
import com.foreach.across.testmodules.springdata.business.Group;
import com.foreach.across.testmodules.springdata.business.Representative;
import com.foreach.across.testmodules.springdata.repositories.CompanyRepository;
import com.foreach.across.testmodules.springdata.repositories.GroupRepository;
import com.foreach.across.testmodules.springdata.repositories2.RepresentativeRepository;
import it.com.foreach.across.modules.entity.query.jpa.ITEntityQueryJpaUtils;
import it.com.foreach.across.modules.entity.repository.TestRepositoryEntityRegistrar;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.function.Supplier;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@WebAppConfiguration
@ContextConfiguration(classes = TestRepositoryEntityRegistrar.Config.class)
public abstract class AbstractQueryTest
{
	private static boolean inserted = false;

	protected static Company one, two, three;
	protected static Representative john, joe, peter, weirdo;
	protected static Group groupOne, groupTwo;

	@Autowired
	protected RepresentativeRepository representativeRepository;

	@Autowired
	protected CompanyRepository companyRepository;

	@Autowired
	protected GroupRepository groupRepository;

	@Before
	public void insertTestData() {
		if ( !inserted ) {
			inserted = true;

			groupOne = new Group( "groupOne" );
			groupTwo = new Group( "groupTwo" );
			groupRepository.save( Arrays.asList( groupOne, groupTwo ) );

			john = new Representative( "john", "John % Surname" );
			joe = new Representative( "joe", "Joe ' Surname" );
			peter = new Representative( "peter", "Peter \\ Surname" );
			weirdo = new Representative( "weirdo", "!\"#%-_&/()=;?´`|/\\'" );

			representativeRepository.save( Arrays.asList( john, joe, peter, weirdo ) );

			one = new Company( "one", 1, asDate( "2015-01-17 13:30" ) );
			one.setStatus( CompanyStatus.IN_BUSINESS );

			two = new Company( "two", 2, asDate( "2016-03-04 14:00" ) );
			two.setStatus( CompanyStatus.BROKE );

			three = new Company( "three", 3, asDate( "2035-04-04 14:00" ) );

			one.setGroup( groupOne );
			two.setGroup( groupOne );
			three.setGroup( groupTwo );

			one.setRepresentatives( Collections.singleton( john ) );
			two.setRepresentatives( new HashSet<>( Arrays.asList( john, joe, peter ) ) );

			companyRepository.save( Arrays.asList( one, two, three ) );
		}
	}

	@AfterClass
	public static void resetTestData() {
		inserted = false;
	}

	protected static Date asDate( String str ) {
		try {
			return DateUtils.parseDateStrictly( str, "yyyy-MM-dd HH:mm" );
		}
		catch ( ParseException pe ) {
			throw new RuntimeException( pe );
		}
	}

	protected void fallback( Supplier original, Supplier fallback ) {
		try {
			original.get();
		}
		catch ( AssertionError ae ) {
			LOG.error( "Initial test failed - probably case insensitive database - using fallback" );
			fallback.get();
		}
	}
}
