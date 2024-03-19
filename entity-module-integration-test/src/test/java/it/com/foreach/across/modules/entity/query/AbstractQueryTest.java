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

import com.foreach.across.test.AcrossWebAppConfiguration;
import com.foreach.across.testmodules.springdata.business.*;
import com.foreach.across.testmodules.springdata.repositories.CarRepository;
import com.foreach.across.testmodules.springdata.repositories.CompanyRepository;
import com.foreach.across.testmodules.springdata.repositories.GroupRepository;
import com.foreach.across.testmodules.springdata.repositories2.RepresentativeRepository;
import it.com.foreach.across.modules.entity.repository.TestRepositoryEntityRegistrar;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
@EnableWebSecurity
@ExtendWith(SpringExtension.class)
@DirtiesContext
@AcrossWebAppConfiguration
@ContextConfiguration(classes = TestRepositoryEntityRegistrar.Config.class)
public abstract class AbstractQueryTest
{
	private static boolean inserted = false;

	protected static Company one, two, three;
	protected static Representative john, joe, peter, weirdo, absolute;
	protected static Group groupOne, groupTwo, groupThree;
	protected static Car carOne, carTwo;

	@Autowired
	protected RepresentativeRepository representativeRepository;

	@Autowired
	protected CompanyRepository companyRepository;

	@Autowired
	protected GroupRepository groupRepository;

	@Autowired
	protected CarRepository carRepository;

	@BeforeEach
	public void insertTestData() {
		if ( !inserted ) {
			inserted = true;

			groupOne = new Group( "groupOne" );
			groupTwo = new Group( "groupTwo" );
			groupThree = new Group( "groupThree" );
			groupThree.setNumber( -400L );
			groupRepository.saveAll( Arrays.asList( groupOne, groupTwo, groupThree ) );

			john = new Representative( "john", "John % Surname" );
			joe = new Representative( "joe", "Joe ' Surname" );
			peter = new Representative( "peter", "Peter \\ Surname" );
			weirdo = new Representative( "weirdo", "!\"#%-_&/()=;?´`|/\\'" );
			absolute = new Representative( "absolute", "-100" );
			absolute.setNumber( -100L );

			representativeRepository.saveAll( Arrays.asList( john, joe, peter, weirdo, absolute ) );

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

			companyRepository.saveAll( Arrays.asList( one, two, three ) );
			;

			carOne = new Car( "one", one, true );
			carTwo = new Car( "two", two, true );
			carRepository.saveAll( Arrays.asList( carOne, carTwo ) );
		}
	}

	@AfterAll
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
