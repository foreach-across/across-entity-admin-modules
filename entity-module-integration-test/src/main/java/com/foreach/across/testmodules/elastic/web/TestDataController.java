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

package com.foreach.across.testmodules.elastic.web;

import com.foreach.across.testmodules.elastic.domain.jpa.contact.Contact;
import com.foreach.across.testmodules.elastic.domain.jpa.contact.ContactRepository;
import com.foreach.across.testmodules.elastic.domain.jpa.country.Country;
import com.foreach.across.testmodules.elastic.domain.jpa.country.CountryRepository;
import com.foreach.across.testmodules.elastic.domain.jpa.customer.Customer;
import com.foreach.across.testmodules.elastic.domain.jpa.customer.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(path = "/elastic/reset-test-data")
@RequiredArgsConstructor
public class TestDataController
{
	private final CustomerRepository customerRepository;
	private final CountryRepository countryRepository;
	private final ContactRepository contactRepository;

	@GetMapping
	public ResponseEntity resetTestData() {
		customerRepository.deleteAll();
		countryRepository.deleteAll();
		contactRepository.deleteAll();

		Country belgium = createCountry( "Belgium" );
		Country nl = createCountry( "Netherlands" );

		Contact alice = createContact( "Alice", "White" );
		Contact john = createContact( "John", "Smith" );

		for ( int i = 0; i < 100; i++ ) {
			Customer customer = new Customer();
			customer.setFirstName( "first-" + RandomStringUtils.randomAlphanumeric( 15 ) );
			customer.setLastName( "last-" + RandomStringUtils.randomAlphanumeric( 15 ) );
			int mod = i % 10;

			if ( i % 2 == 0 ) {
				customer.setCreatedDate( DateUtils.addDays( new Date(), -mod ) );
				customer.setCountry( belgium );
				if ( i % 4 == 0 ) {
					customer.setPrimaryContacts( Arrays.asList( alice, john ) );
				}
			}
			else {
				customer.setCreatedDate( DateUtils.addDays( new Date(), -mod ) );
				customer.setCountry( nl );
			}
			customer.setUpdatedDate( LocalDateTime.now() );
			customerRepository.save( customer );
		}
		return ResponseEntity.ok().build();
	}

	private Country createCountry( String name ) {
		List<Country> items = countryRepository.findByName( name );
		Country country;
		if ( items.size() == 0 ) {
			country = new Country();
			country.setName( name );
			countryRepository.save( country );
		}
		else {
			country = items.get( 0 );
		}
		return country;
	}

	private Contact createContact( String first, String last ) {
		Contact contact = contactRepository.findByFirstAndLast( first, last )
		                                   .orElseGet( Contact::new );
		contact.setFirst( first );
		contact.setLast( last );
		return contactRepository.save( contact );
	}
}
