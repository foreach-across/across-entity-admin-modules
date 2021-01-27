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

package com.foreach.across.modules.entity.query.elastic;

import com.foreach.across.core.convert.StringToDateTimeConverter;
import com.foreach.across.modules.entity.config.builders.EntityPropertyDescriptorBuilder;
import com.foreach.across.modules.entity.query.EQTypeConverter;
import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.query.EntityQueryParser;
import com.foreach.across.modules.entity.query.EntityQueryParserFactory;
import com.foreach.across.modules.entity.query.elastic.repositories.CustomerRepository;
import com.foreach.across.modules.entity.query.support.EntityQueryDateFunctions;
import com.foreach.across.modules.entity.registry.properties.DefaultEntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.SimpleEntityPropertyDescriptor;
import lombok.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.validator.constraints.Length;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Persistable;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestEntityQueryElasticUtils.Config.class)
public class TestEntityQueryElasticUtils
{
	@Autowired
	ElasticsearchOperations elasticsearchOperations;

	@Autowired
	private CustomerRepository repository;

	EntityQueryParser parser;

	@BeforeEach
	public void setup() {
		// TODO: simply this by parsing Customer class directly
		DefaultEntityPropertyRegistry registry = new DefaultEntityPropertyRegistry();
		registry.register( new SimpleEntityPropertyDescriptor( "firstName", new EntityPropertyDescriptorBuilder( "firstName" ).propertyType( String.class )
		                                                                                                                      .build() ) );
		registry.register( new SimpleEntityPropertyDescriptor( "lastName", new EntityPropertyDescriptorBuilder( "lastName" ).propertyType( String.class )
		                                                                                                                    .build() ) );
		registry.register( new SimpleEntityPropertyDescriptor( "createdDate", new EntityPropertyDescriptorBuilder( "createdDate" ).propertyType( Date.class )
		                                                                                                                          .build() ) );
		registry.register( new SimpleEntityPropertyDescriptor( "updatedDate", new EntityPropertyDescriptorBuilder( "updatedDate" )
				.propertyType( LocalDateTime.class ).build() ) );
		registry.register( new SimpleEntityPropertyDescriptor( "age", new EntityPropertyDescriptorBuilder( "age" ).propertyType( Long.class ).build() ) );
		EQTypeConverter eqTypeConverter = new EQTypeConverter();
		DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService();
		conversionService.addConverter( new StringToDateTimeConverter( conversionService ) );
		eqTypeConverter.setConversionService( conversionService );
		eqTypeConverter.setFunctionHandlers( Collections.singletonList( new EntityQueryDateFunctions() ) );
		EntityQueryParserFactory entityQueryParserFactory = new EntityQueryParserFactory( eqTypeConverter );
		parser = entityQueryParserFactory.createParser( registry );

		long count = repository.count();
		if ( count >= 100 ) {
			return;
		}
		for ( int i = 0; i < 100; i++ ) {
			Customer elasticCustomer = new Customer();
			elasticCustomer.setFirstName( "first-" + RandomStringUtils.randomAlphanumeric( 15 ) );
			elasticCustomer.setLastName( "last-" + RandomStringUtils.randomAlphanumeric( 15 ) );
			int mod = i % 10;

			if ( i % 2 == 0 ) {
				elasticCustomer.setCreatedDate( DateUtils.addDays( new Date(), -mod ) );
			}
			else {
				elasticCustomer.setCreatedDate( DateUtils.addDays( new Date(), -mod ) );
			}
			elasticCustomer.setUpdatedDate( LocalDateTime.now() );
			repository.save( elasticCustomer );
		}
	}

	@Test
	public void testOrQuery() {
		Page<Customer> all = (Page<Customer>) SearchHitSupport.unwrapSearchHits( repository.findAll() );
		List<Customer> items = all.getContent();
		Customer firstItem = items.get( 0 );
		Customer secondItem = items.get( 1 );
		EntityQuery rawQuery = EntityQuery.of( "(firstName = '" + firstItem.firstName + "' or lastName = '" + secondItem.lastName + "')" );

		CriteriaQuery criteriaQuery = EntityQueryElasticUtils.toCriteriaQuery( parser.prepare( rawQuery ) );

		assertThat( criteriaQuery ).isNotNull();
		SearchHits<Customer> search = elasticsearchOperations.search( criteriaQuery, Customer.class );
		assertThat( search.getSearchHits() ).size().isEqualTo( 2 );
	}

	@Test
	public void testTodayQuery() {
		Page<Customer> all = (Page<Customer>) SearchHitSupport.unwrapSearchHits( repository.findAll() );
		List<Customer> items = all.getContent();

		EntityQuery rawQuery = EntityQuery.of( "createdDate < today()" );

		CriteriaQuery criteriaQuery = EntityQueryElasticUtils.toCriteriaQuery( parser.prepare( rawQuery ) );

		assertThat( criteriaQuery ).isNotNull();
		SearchHits<Customer> search = elasticsearchOperations.search( criteriaQuery, Customer.class );
		assertThat( search.getSearchHits() ).size().isEqualTo( 90 );
	}

	@Configuration
	@Import(value = { ElasticsearchRestClientAutoConfiguration.class, ElasticsearchDataAutoConfiguration.class })
	@EnableElasticsearchRepositories(basePackageClasses = CustomerRepository.class)
	static class Config
	{
	}

	@Document(indexName = "customeridx")
	@AllArgsConstructor
	@NoArgsConstructor
	@EqualsAndHashCode(of = "id")
	@Getter
	@Setter
	public static class Customer implements Persistable<String>
	{
		@Id
		@Length(max = 20)
		private String id;

		@NotBlank
		@Length(max = 250)
		@Field(type = FieldType.Keyword)
		private String firstName;

		@Length(max = 250)
		@Field(type = FieldType.Keyword)
		private String lastName;

		@Field(type = FieldType.Date)
		private Date createdDate;

		@Field(type = FieldType.Date, format = DateFormat.date_time)
		private LocalDateTime updatedDate;
		@Version
		private Long version;

		@Override
		public String toString() {
			return String.format(
					"Customer[id=%s, firstName='%s', lastName='%s']",
					id, firstName, lastName );
		}

		@Override
		public boolean isNew() {
			return getId() == null;
		}
	}
}