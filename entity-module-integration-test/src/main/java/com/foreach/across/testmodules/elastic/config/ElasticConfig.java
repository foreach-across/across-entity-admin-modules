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
import com.foreach.across.testmodules.elastic.domain.DomainMarker;
import com.foreach.across.testmodules.elastic.domain.elastic.country.ElasticCountry;
import com.foreach.across.testmodules.elastic.domain.elastic.customer.ElasticCustomer;
import com.foreach.across.testmodules.elastic.domain.jpa.country.Country;
import com.foreach.across.testmodules.elastic.domain.jpa.customer.Customer;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * @author Arne Vandamme
 * @since 2.2.0
 */
@Configuration
@EnableElasticsearchRepositories(basePackageClasses = DomainMarker.class)
@Import({ ElasticsearchDataAutoConfiguration.class, ElasticsearchRepositoriesAutoConfiguration.class })
public class ElasticConfig implements EntityConfigurer
{
	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		entities.withType( ElasticCustomer.class )
		        .attribute( EntityElasticsearchConfiguration.ATTR_ELASTIC_PROXY, Customer.class )
		        .properties( p -> p
				                     .property( "readOnlyVersion" )
				                     .propertyType( Long.class )
				                     .controller( c -> c.withTarget( ElasticCustomer.class, Long.class ).valueFetcher( ElasticCustomer::getVersion ) )
				                     .viewElementPostProcessor( ViewElementMode.CONTROL,
				                                                (ViewElementPostProcessor<NumericFormElement>) ( viewElementBuilderContext, viewElement ) -> viewElement
						                                                .setReadonly( true ) )
				                     .and()
				                     .property( "createdDate" ).viewElementType( ViewElementMode.FILTER_CONTROL, BootstrapUiElements.DATETIME ).and()
				                     .property( "updatedDate" ).viewElementType( ViewElementMode.FILTER_CONTROL, BootstrapUiElements.DATETIME ).and()
		                     //.property( "updatedDate" ).viewElementType( ViewElementMode.FILTER_CONTROL, BootstrapUiElements.DATETIME )

		        )
		        .createOrUpdateFormView( cfv -> cfv.showProperties( "*" ) )
		        .listView( lvb -> lvb.entityQueryFilter( eqf -> eqf.showProperties( "*" )
		                                                           .advancedMode( true ) )
		                             .showProperties( "*" ) );

		entities.withType( ElasticCountry.class )
		        .attribute( EntityElasticsearchConfiguration.ATTR_ELASTIC_PROXY, Country.class );
	}
}
