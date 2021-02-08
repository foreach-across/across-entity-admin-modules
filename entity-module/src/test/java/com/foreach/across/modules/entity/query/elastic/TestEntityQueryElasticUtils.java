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
import com.foreach.across.modules.entity.query.*;
import com.foreach.across.modules.entity.query.elastic.repositories.CountryRepository;
import com.foreach.across.modules.entity.query.elastic.repositories.CustomerRepository;
import com.foreach.across.modules.entity.query.support.EntityQueryDateFunctions;
import com.foreach.across.modules.entity.registry.DefaultEntityConfigurationProvider;
import com.foreach.across.modules.entity.registry.EntityConfigurationImpl;
import com.foreach.across.modules.entity.registry.properties.DefaultEntityPropertyRegistryProvider;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptorFactory;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptorFactoryImpl;
import com.foreach.across.modules.entity.registry.properties.registrars.DefaultPropertiesRegistrar;
import lombok.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.validator.constraints.Length;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Persistable;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestEntityQueryElasticUtils.Config.class, initializers = TestEntityQueryElasticUtils.PropertyInitializer.class)
@Testcontainers
public class TestEntityQueryElasticUtils
{
	@Container
	public static ElasticsearchContainer container = new ElasticsearchContainer( "docker.elastic.co/elasticsearch/elasticsearch:7.9.3" );

	@Autowired
	ElasticsearchOperations elasticsearchOperations;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private CountryRepository countryRepository;

	final static Date TODAY = DateUtils.truncate( new Date(), Calendar.DATE );

	private EntityQueryParser parser;
	private Page<Customer> allCustomersCache;

	@BeforeEach
	public void setup() {
		EntityPropertyDescriptorFactory entityPropertyDescriptorFactory = new EntityPropertyDescriptorFactoryImpl();
		DefaultEntityPropertyRegistryProvider entityPropertyRegistryProvider = new DefaultEntityPropertyRegistryProvider( entityPropertyDescriptorFactory );
		entityPropertyRegistryProvider.setPropertiesRegistrars(
				Collections.singletonList( new DefaultPropertiesRegistrar( entityPropertyDescriptorFactory ) ) );
		DefaultEntityConfigurationProvider defaultEntityConfigurationProvider = new DefaultEntityConfigurationProvider( null, entityPropertyRegistryProvider );
		EntityConfigurationImpl<Customer> entityConfiguration =
				(EntityConfigurationImpl<Customer>) defaultEntityConfigurationProvider.create( "customer", Customer.class, true );

		EQTypeConverter eqTypeConverter = new EQTypeConverter();
		DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService();
		conversionService.addConverter( new StringToDateTimeConverter( conversionService ) );
		eqTypeConverter.setConversionService( conversionService );
		eqTypeConverter.setFunctionHandlers( Arrays.asList( new EntityQueryDateFunctions(), new ElasticBetweenFunctionHandler() ) );
		EntityQueryParserFactory entityQueryParserFactory = new EntityQueryParserFactory( eqTypeConverter );
		parser = entityQueryParserFactory.createParser( entityConfiguration.getPropertyRegistry() );
	}

	@Test
	public void testOrQuery() {
		Page<Customer> all = (Page<Customer>) SearchHitSupport.unwrapSearchHits( customerRepository.findAll() );
		List<Customer> items = all.getContent();
		Customer firstItem = items.get( 0 );
		Customer secondItem = items.get( 1 );
		EntityQuery rawQuery = EntityQuery.of( "(firstName = '" + firstItem.firstName + "' or lastName = '" + secondItem.lastName + "')" );
		assertSame( "(firstName = '" + firstItem.firstName + "' or lastName = '" + secondItem.lastName + "')",
		            m -> Objects.equals( firstItem.firstName, m.firstName ) || Objects.equals( secondItem.lastName, m.lastName ), 2 );
	}

	@Test
	public void testElasticBetweenFunctionQuery() {
		assertSame( "sequenceNumber = elastic.between(95, 200)", m -> m.getSequenceNumber() >= 95, 5 );
	}

	@Test
	public void testLikeQuery() {
		String searchString = "IT" + RandomStringUtils.randomNumeric( 1 );
		Page<Customer> all = getAllCustomers();
		List<Customer> filteredItems = all.stream().filter( m -> m.getFirstName().contains( searchString ) ).collect( Collectors.toList() );
		EntityQuery rawQuery = EntityQuery.of( "firstName like '%" + searchString + "%'" );

		CriteriaQuery criteriaQuery = EntityQueryElasticUtils.toCriteriaQuery( parser.prepare( rawQuery ) );

		assertThat( criteriaQuery ).isNotNull();
		SearchHits<Customer> search = elasticsearchOperations.search( criteriaQuery, Customer.class );
		assertThat( search.getSearchHits() ).size().isEqualTo( filteredItems.size() );
		assertThat( search.getSearchHits() ).map( SearchHit::getContent ).isEqualTo( filteredItems );

		assertSame( "country.name like '%lgi%'", m -> m.getCountry().getName().contains( "lgi" ), 50 );
	}

	@Test
	void endsWithWildcard() {
		assertSame( "country.name like '%gium'", m -> m.getCountry().getName().endsWith( "gium" ), 50 );
		assertSame( "country.name like '%giuM'", m -> m.getCountry().getName().endsWith( "giuM" ), 0 );
		assertSame( "country.name ilike '%GiUm'", m -> m.getCountry().getName().toLowerCase().endsWith( "gium" ), 50 );
	}

	@Test
	void startsWithWildcard() {
		assertSame( "country.name like 'Bel%'", m -> m.getCountry().getName().startsWith( "Bel" ), 50 );
		assertSame( "country.name like 'bel%'", m -> m.getCountry().getName().startsWith( "bel" ), 0 );
	}

	@Test
	@Disabled("TBD: setting an analyzer on _one_ keyword field, enables it on other keyword fields as well?")
	void caseSensitivityDependsOnAnalyzerOnKeywordFields() {
		String firstNameSearch = "IT" + RandomStringUtils.randomNumeric( 1 );
		assertSame( "firstName ilike '%" + firstNameSearch + "%'", m -> m.getCountry().getName().toLowerCase().contains( firstNameSearch ), 50 );

		String lastNameSearchEnd = RandomStringUtils.randomNumeric( 1 );
		String lastNameSearch = "last-%" + lastNameSearchEnd;
		assertSame( "lastName ilike '" + lastNameSearch + "'", m -> {
			String countryName = m.getCountry().getName().toLowerCase();
			return countryName.startsWith( "b" ) && countryName.endsWith( "m" );
		}, 50 );
	}

	@Test
	void likeWithInnerWildcards() {
		assertSame( "country.name like 'B%l%'", m -> {
			            String countryName = m.getCountry().getName();
			            return countryName.startsWith( "B" ) && countryName.contains( "l" );
		            },
		            50 );

		assertSame( "country.name like '%l%m'", m -> {
			            String countryName = m.getCountry().getName();
			            return countryName.contains( "l" ) && countryName.endsWith( "m" );
		            },
		            50 );

		assertSame( "country.name like 'B%m'",
		            m -> {
			            String countryName = m.getCountry().getName();
			            return countryName.startsWith( "B" ) && countryName.endsWith( "m" );
		            },
		            50 );
		assertSame( "country.name like 'b%m'", m -> {
			String countryName = m.getCountry().getName();
			return countryName.startsWith( "b" ) && countryName.endsWith( "m" );
		}, 0 );
	}

	@Test
	@Disabled(value = "Currently not supported")
	void likeWithMultipleInnerWildcards() {
		assertSame( "country.name like '%e%l%'", m -> m.getCountry().getName().matches( "^.*e.*l.*$" ), 100 );
		assertSame( "country.name like '%e%L%'", m -> m.getCountry().getName().matches( "^.*e.*L.*$" ), 0 );
		assertSame( "country.name ilike '%e%L%'", m -> m.getCountry().getName().toLowerCase().matches( "^.*e.*l.*$" ), 100 );
	}

	@Test
	public void testTodayEqQuery() {
		assertSame( "createdDate = today()", m -> Objects.equals( m.getCreatedDate(), TODAY ), 1 );
		assertSame( "updatedDate = today()", m -> Objects.equals( m.getUpdatedDate(), LocalDateTime.now() ), 0 );
	}

	@Test
	void localDateTimeQueries() {
		DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
		LocalDateTime startDate = LocalDate.now().atStartOfDay();
		LocalDateTime endDate = LocalDate.now().plusDays( 1 ).atStartOfDay();
		assertSame( String.format( "updatedDate >  '%s' and updatedDate < '%s'", startDate.format( formatter ), endDate.format( formatter ) ),
		            m -> m.getUpdatedDate().isAfter( startDate ) && m.getUpdatedDate().isBefore( endDate ),
		            51 );
	}

	@Test
	void localDateQueries() {
		DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
		LocalDate startDate = LocalDate.now().minusYears( 30 ).withDayOfYear( 1 ).minusDays( 1 );
		LocalDate endDate = LocalDate.now().minusYears( 24 ).withDayOfYear( 1 );
		assertSame( String.format( "dateOfBirth > '%s' and dateOfBirth < '%s'", startDate.format( formatter ), endDate.format( formatter ) ),
		            m -> m.getDateOfBirth().isAfter( startDate ) && m.getDateOfBirth().isBefore( endDate ),
		            50 );
	}

	@Test
	void localTimeQueries() {
		DateTimeFormatter formatter = DateTimeFormatter.ISO_TIME;
		LocalTime startDate = LocalTime.of( 14, 0 );
		LocalTime endDate = LocalTime.of( 18, 0 );
		LocalTime endDateToCheck = endDate.plusMinutes( 1 );
		assertSame( String.format( "workDayFinishesAt > '%s' and workDayFinishesAt <= '%s'", startDate.format( formatter ), endDate.format( formatter ) ),
		            m -> m.getWorkDayFinishesAt().isAfter( startDate ) && m.getWorkDayFinishesAt().isBefore( endDateToCheck ),
		            40 );
	}

	@Test
	public void testLtTodayQuery() {
		assertSame( "createdDate < today()", m -> m.getCreatedDate().before( TODAY ), 90 );
	}

	@Test
	public void testGtTodayQuery() {
		assertSame( "createdDate > today()", m -> m.getCreatedDate().after( TODAY ), 9 );
	}

	@Test
	public void testGtEqTodayQuery() {
		assertSame( "createdDate >= today()", m -> !m.getCreatedDate().before( TODAY ), 10 );
	}

	@Test
	public void testCollectionQuery() {
		assertSame( "primaryContacts[].first = 'Alice'",
		            m -> m.getPrimaryContacts() != null && m.getPrimaryContacts().stream().anyMatch( c -> Objects.equals( "Alice", c.getFirst() ) ), 25 );
	}

	@Test
	public void testNestedIdQuery() {
		Country belgium = countryRepository.findByName( "Belgium" );
		assertThat( belgium ).isNotNull();
		assertSame( "country.id = '" + belgium.getId() + "'", m -> Objects.equals( m.getCountry(), belgium ), 50 );
	}

	private void assertSame( String entityQuery, Predicate<Customer> p, int expectedSize ) {
		Page<Customer> all = getAllCustomers();
		List<Customer> filteredItems = all.stream().filter( p ).collect( Collectors.toList() );
		EntityQuery rawQuery = EntityQuery.of( entityQuery );

		CriteriaQuery criteriaQuery = EntityQueryElasticUtils.toCriteriaQuery( parser.prepare( rawQuery ) );

		assertThat( criteriaQuery ).isNotNull();
		SearchHits<Customer> search = elasticsearchOperations.search( criteriaQuery, Customer.class );
		assertThat( search.getSearchHits() ).size().isEqualTo( expectedSize );
		assertThat( search.getSearchHits() ).size().isEqualTo( filteredItems.size() );
		assertThat( search.getSearchHits() ).map( SearchHit::getContent ).isEqualTo( filteredItems );
	}

	private Page<Customer> getAllCustomers() {
		if ( allCustomersCache == null ) {
			allCustomersCache = customerRepository.findAll( Pageable.unpaged() );
		}
		return allCustomersCache;
	}

	@Configuration
	@Import(value = { ElasticsearchRestClientAutoConfiguration.class, ElasticsearchDataAutoConfiguration.class })
	@EnableElasticsearchRepositories(basePackageClasses = CustomerRepository.class)
	static class Config
	{
		@Bean
		public TestDataInitializer testDataInitializer() {
			return new TestDataInitializer();
		}
	}

	@Document(indexName = "itcustomeridx")
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

		@Field(type = FieldType.Long)
		private long sequenceNumber;

		@NotBlank
		@Length(max = 250)
		@Field(type = FieldType.Keyword, searchAnalyzer = "standard")
		private String firstName;

		@Length(max = 250)
		@Field(type = FieldType.Keyword/*, searchAnalyzer = "standard"*/)
		private String lastName;

		@Field(type = FieldType.Date, format = DateFormat.date_optional_time)
		private Date createdDate;

		@Field(type = FieldType.Date, format = DateFormat.date_optional_time)
		private LocalDateTime updatedDate;

		@Field(type = FieldType.Date, format = DateFormat.date)
		private LocalDate dateOfBirth;

		@Field(type = FieldType.Date, format = DateFormat.custom, pattern = "HH:mm:ss.SSS")
		private LocalTime workDayFinishesAt;

		@Version
		private Long version;

		// doesn't seem to have type nested in the actual index? curl -X GET "localhost:9200/customidx/_mapping?pretty"
		@Field(type = FieldType.Nested, includeInParent = true)
		private Country country;

		@Field(type = FieldType.Nested, includeInParent = true)
		private List<ElasticContact> primaryContacts;

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

	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ElasticContact
	{
		@Field(type = FieldType.Keyword)
		@Length(max = 250)
		private String first;

		@Field(type = FieldType.Keyword)
		@Length(max = 250)
		private String last;
	}

	@Document(indexName = "itcountryidx")
	@AllArgsConstructor
	@NoArgsConstructor
	@EqualsAndHashCode(of = "id")
	@Getter
	@Setter
	public static class Country implements Persistable<String>
	{
		@Id
		@Length(max = 20)
		private String id;

		@NotBlank
		@Length(max = 250)
		@Field(type = FieldType.Keyword)
		private String name;

		@Override
		public String toString() {
			return String.format(
					"Country[id=%s, name='%s']",
					id, name );
		}

		@Override
		public boolean isNew() {
			return getId() == null;
		}
	}

	static class PropertyInitializer
			implements ApplicationContextInitializer<ConfigurableApplicationContext>
	{

		@Override
		public void initialize( ConfigurableApplicationContext context ) {
			TestPropertyValues.of(
					"spring.elasticsearch.rest.uris=" + container.getHttpHostAddress()
			).applyTo( context );
		}
	}

	static class ElasticBetweenFunctionHandler implements EntityQueryFunctionHandler
	{

		@Override
		public boolean accepts( String functionName, TypeDescriptor desiredType ) {
			return Objects.equals( "elastic.between", functionName );
		}

		@Override
		public Object apply( String functionName, EQType[] arguments, TypeDescriptor desiredType, EQTypeConverter argumentConverter ) {
			return (EntityQueryConditionElasticFunctionHandler) entityQueryCondition -> {
				if ( !( entityQueryCondition.getOperand() == EntityQueryOps.EQ || entityQueryCondition.getOperand() == EntityQueryOps.NEQ ) ) {
					throw new RuntimeException( "Unsupported operand: " + entityQueryCondition.getOperand().name() + " for elastic.between() function." );
				}

				if ( arguments.length != 2 ) {
					throw new RuntimeException( "Expected two arguments" );
				}

				Object[] convertedArgs = argumentConverter.convertAll( desiredType, false, arguments );
				if ( entityQueryCondition.getOperand().isNegation() ) {
					return Criteria.where( entityQueryCondition.getProperty() ).not().between( convertedArgs[0], convertedArgs[1] );
				}
				else {
					return Criteria.where( entityQueryCondition.getProperty() ).between( convertedArgs[0], convertedArgs[1] );
				}
			};
		}
	}

	static class TestDataInitializer
	{

		@Autowired
		void insertTestData( CustomerRepository customerRepository, CountryRepository countryRepository ) {
			customerRepository.deleteAll();
			countryRepository.deleteAll();

			Country belgium = createCountry( "Belgium", countryRepository );
			Country netherlands = createCountry( "Netherlands", countryRepository );
			ElasticContact alice = new ElasticContact( "Alice", "White" );
			ElasticContact john = new ElasticContact( "John", "Smith" );

			for ( int i = 0; i < 100; i++ ) {
				Customer customer = new Customer();
				customer.setSequenceNumber( i );
				customer.setFirstName( "first-abIT" + i + RandomStringUtils.randomAlphanumeric( 15 ) );
				customer.setLastName( "last-" + RandomStringUtils.randomAlphanumeric( 15 ) );
				int mod = i % 10;

				if ( i == 0 ) {
					customer.setCreatedDate( TestEntityQueryElasticUtils.TODAY );
				}
				else {
					if ( i % 2 == 0 ) {
						customer.setCreatedDate( DateUtils.addDays( new Date(), -mod ) );
					}
					else {
						customer.setCreatedDate( DateUtils.addDays( new Date(), -mod ) );
					}
				}

				if ( i % 2 == 0 ) {
					if ( i % 4 == 0 ) {
						// 25 of them
						customer.setPrimaryContacts( Arrays.asList( alice, john ) );
					}

					customer.setCountry( belgium );
				}
				else {
					customer.setCountry( netherlands );
				}

				if ( i > 50 ) {
					customer.setUpdatedDate( LocalDateTime.now().plusDays( i - 50 ) );
				}
				else {
					customer.setUpdatedDate( LocalDateTime.now() );
				}

				customer.setDateOfBirth( LocalDate.now().minusYears( 20 + mod ) );

				customer.setWorkDayFinishesAt( LocalTime.of( 14, 0 ).plusHours( mod ) );
				customerRepository.save( customer );
			}
		}

		private Country createCountry( String name, CountryRepository countryRepository ) {
			Country country = new Country();
			country.setName( name );
			return countryRepository.save( country );
		}
	}
}