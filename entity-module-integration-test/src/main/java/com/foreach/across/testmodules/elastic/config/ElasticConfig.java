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

package com.foreach.across.testmodules.elastic.config;

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiElements;
import com.foreach.across.modules.bootstrapui.elements.NumericFormElement;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import com.foreach.across.testmodules.elastic.ElasticTestModule;
import com.foreach.across.testmodules.elastic.domain.ElasticCountry;
import com.foreach.across.testmodules.elastic.domain.ElasticCountryRepository;
import com.foreach.across.testmodules.elastic.domain.ElasticCustomer;
import com.foreach.across.testmodules.elastic.domain.ElasticCustomerRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.util.Date;
import java.util.List;

/**
 * @author Arne Vandamme
 * @since 2.2.0
 */
@Configuration
@EnableElasticsearchRepositories(basePackageClasses = ElasticTestModule.class)
@Import({ ElasticsearchDataAutoConfiguration.class, ElasticsearchRepositoriesAutoConfiguration.class })
public class ElasticConfig implements EntityConfigurer
{
	@Autowired
	public void setup( ElasticCustomerRepository elasticCustomerRepository, ElasticCountryRepository countryRepository ) {
		ElasticCountry belgium = createCountry( countryRepository, "Belgium" );

		ElasticCountry nl = createCountry( countryRepository, "Netherlands" );
		long count = elasticCustomerRepository.count();
		if ( count >= 100 ) {
			return;
		}
		for ( int i = 0; i < 100; i++ ) {
			ElasticCustomer elasticCustomer = new ElasticCustomer();
			elasticCustomer.setFirstName( "first-" + RandomStringUtils.randomAlphanumeric( 15 ) );
			elasticCustomer.setLastName( "last-" + RandomStringUtils.randomAlphanumeric( 15 ) );
			int mod = i % 10;

			if ( i % 2 == 0 ) {
				//elasticCustomer.setCreatedDate( LocalDateTime.now().minusDays( mod ) );
				elasticCustomer.setCreatedDate( DateUtils.addDays( new Date(), -mod ) );
				elasticCustomer.setCountry( belgium );
				//JoinField<String> customer = new JoinField<>( "customer", belgium.getId() );
				//elasticCustomer.setMyJoinField( customer );
			}
			else {
				//elasticCustomer.setCreatedDate( LocalDateTime.now().plusDays( mod ) );
				elasticCustomer.setCreatedDate( DateUtils.addDays( new Date(), -mod ) );
				elasticCustomer.setCountry( nl );
				//JoinField<String> customer = new JoinField<>( "customer", nl.getId() );
				//elasticCustomer.setMyJoinField( customer );
			}
			//elasticCustomer.setUpdatedDate( LocalDateTime.now() );
			elasticCustomerRepository.save( elasticCustomer );
		}
	}

	private ElasticCountry createCountry( ElasticCountryRepository countryRepository, String name ) {
		List<ElasticCountry> items = countryRepository.findByName( name );
		ElasticCountry country;
		if ( items.size() == 0 ) {
			country = new ElasticCountry();
			country.setName( name );
			countryRepository.save( country );
		}
		else {
			country = items.get( 0 );
		}
		return country;
	}

	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		entities.withType( ElasticCustomer.class )
		        .properties( p -> p
				                     .property( "readOnlyVersion" )
				                     .propertyType( Long.class )
				                     .controller( c -> c.withTarget( ElasticCustomer.class, Long.class ).valueFetcher( ElasticCustomer::getVersion ) )
				                     .viewElementPostProcessor( ViewElementMode.CONTROL,
				                                                (ViewElementPostProcessor<NumericFormElement>) ( viewElementBuilderContext, viewElement ) -> viewElement
						                                                .setReadonly( true ) )
				                     .and()
				                     .property( "createdDate" ).viewElementType( ViewElementMode.FILTER_CONTROL, BootstrapUiElements.DATETIME ).and()
		                     //.property( "updatedDate" ).viewElementType( ViewElementMode.FILTER_CONTROL, BootstrapUiElements.DATETIME )

		        )
		        .createOrUpdateFormView( cfv -> cfv.showProperties( "*" ) )
		        .listView( lvb -> lvb.entityQueryFilter( eqf -> eqf.showProperties( "*" )
		                                                           .advancedMode( true ) )
		                             .showProperties( "*" ) );
	}
}
