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

package com.foreach.across.samples.entity.application.config;

import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.views.context.ConfigurableEntityViewContext;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.menu.EntityAdminMenuEvent;
import com.foreach.across.modules.entity.views.processors.EntityViewProcessorAdapter;
import com.foreach.across.modules.entity.web.links.EntityViewLinkBuilder;
import com.foreach.across.testmodules.elastic.config.EntityElasticsearchConfiguration;
import com.foreach.across.testmodules.elastic.domain.elastic.country.ElasticCountry;
import com.foreach.across.testmodules.elastic.domain.elastic.customer.ElasticCustomer;
import com.foreach.across.testmodules.elastic.domain.jpa.country.Country;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

/**
 * WIP configuring view navigation within associations.
 * See {@link EntityElasticsearchConfiguration} and {@link ElasticsearchMenuConfiguration} for configuration related to non-association views
 */
@Configuration
public class CountryConfiguration implements EntityConfigurer
{
	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		entities.withType( ElasticCountry.class )
		        .attribute( EntityElasticsearchConfiguration.ATTR_ELASTIC_PROXY_REFERENCE, Country.class );

		entities.withType( Country.class )
		        .association(
				        ab -> ab.name( "customer.country" ).associationType( EntityAssociation.Type.EMBEDDED )
				                .createOrUpdateFormView(
						                fvb -> fvb.viewProcessor(
								                vp -> vp.provideBean( new ElasticAssociationViewContextLinkBuilderProcessor( "elasticCustomer.country",
								                                                                                             "customer.country" ) ) )
				                )
				                .deleteFormView(
						                fvb -> fvb.viewProcessor(
								                vp -> vp.provideBean( new ElasticAssociationViewContextLinkBuilderProcessor( "elasticCustomer.country",
								                                                                                             "customer.country" ) ) )
				                )
		        )
		        .association(
				        ab -> ab.name( "elasticCustomer.country" )
				                .targetEntityType( ElasticCustomer.class )
				                .sourceProperty( "id" )
				                .targetProperty( "country.id" )
				                .associationType( EntityAssociation.Type.EMBEDDED )
				                .listView(
						                lvb -> lvb.viewProcessor(
								                vp -> vp.provideBean( new ElasticAssociationViewContextLinkBuilderProcessor( "elasticCustomer.country",
								                                                                                             "customer.country" ) ) )
				                )
		        );
	}

	@EventListener
	public void customizeEntityAdminMenu( EntityAdminMenuEvent<?> menuEvent ) {
		String targetAssociation = "customer.country";
		String associationName = "elasticCustomer.country";
		EntityViewContext viewContext = menuEvent.getViewContext();

		if ( Country.class.equals( viewContext.getEntityConfiguration().getEntityType() ) ) {
			EntityViewLinkBuilder instanceLinkBuilder = resolveAssociationLinkBuilder( viewContext, targetAssociation );
			menuEvent.builder()
			         .item( associationName )
			         .matchRequests( "~" + instanceLinkBuilder.listView().toUriString(), instanceLinkBuilder.listView().toUriString() )
			         .and().item( targetAssociation )
			         .enable( false );
		}
	}

	static EntityViewLinkBuilder resolveAssociationLinkBuilder( EntityViewContext associationParentContext, String targetAssociation ) {
		return associationParentContext.getLinkBuilder().forInstance( associationParentContext.getEntity() )
		                               .association( targetAssociation );
	}

	@RequiredArgsConstructor
	static class ElasticAssociationViewContextLinkBuilderProcessor extends EntityViewProcessorAdapter
	{
		private final String searchAssociation;
		private final String targetAssociation;

		@Override
		public void prepareEntityViewContext( ConfigurableEntityViewContext entityViewContext ) {
			if ( entityViewContext.isForAssociation() ) {
				EntityViewLinkBuilder instanceLinkBuilder = resolveAssociationLinkBuilder( entityViewContext.getParentContext(), targetAssociation );
				EntityViewLinkBuilder linkBuilder = resolveAssociationLinkBuilder( entityViewContext.getParentContext(), searchAssociation );

				EntityViewLinkBuilder proxyInstanceLinkBuilder = new EntityElasticsearchConfiguration.ProxyEntityViewLinkBuilder( linkBuilder,
				                                                                                                                  instanceLinkBuilder );
				EntityViewLinkBuilder proxyLinkBuilder = new EntityElasticsearchConfiguration.ProxyEntityViewLinkBuilder( linkBuilder,
				                                                                                                          proxyInstanceLinkBuilder );
				entityViewContext.setLinkBuilder( proxyLinkBuilder );
			}
		}

	}
}
