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

import com.foreach.across.core.annotations.ConditionalOnAcrossModule;
import com.foreach.across.core.context.info.AcrossModuleInfo;
import com.foreach.across.modules.adminweb.menu.AdminMenuEvent;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.web.links.EntityViewLinkBuilder;
import com.foreach.across.modules.entity.web.links.EntityViewLinks;
import com.foreach.across.modules.spring.security.actions.AllowableAction;
import com.foreach.across.modules.spring.security.actions.AllowableActions;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.util.function.Consumer;

import static com.foreach.across.samples.entity.application.config.EntityElasticsearchConfiguration.ATTR_ELASTIC_PROXY_REFERENCE;

// todo Elasticsearch menu configuration, should be put somewhere together with {@link EntityElasticsearchConfiguration}
@Configuration
@RequiredArgsConstructor
@ConditionalOnAcrossModule("AcrossHibernateJpaModule")
public class ElasticsearchMenuConfiguration
{
	private final EntityRegistry entityRegistry;
	private final EntityViewLinks entityViewLinks;

	@EventListener
	public void configureElasticsearchProxyEntitiesMenuSelection( AdminMenuEvent adminMenuEvent ) {
		PathBasedMenuBuilder menu = adminMenuEvent.builder();
		entityRegistry.getEntities()
		              .stream()
		              .filter( this::isVisibleConfiguration )
		              .filter( this::isElasticsearchProxy )
		              .forEach( configureItemUrls( menu ) );

	}

	private boolean isVisibleConfiguration( EntityConfiguration<?> entityConfiguration ) {
		AllowableActions allowableActions = entityConfiguration.getAllowableActions();
		return !entityConfiguration.isHidden() && allowableActions.contains( AllowableAction.READ );
	}

	private boolean isElasticsearchProxy( EntityConfiguration<?> entityConfiguration ) {
		return entityConfiguration.hasAttribute( ATTR_ELASTIC_PROXY_REFERENCE );
	}

	private Consumer<EntityConfiguration> configureItemUrls( PathBasedMenuBuilder builder ) {
		return entityConfiguration -> {
			Class<?> targetType = entityConfiguration.getAttribute( ATTR_ELASTIC_PROXY_REFERENCE, Class.class );
			EntityConfiguration<?> targetConfiguration = entityRegistry.getEntityConfiguration( targetType );

			EntityViewLinkBuilder.ForEntityConfiguration configurationLinkBuilder = entityViewLinks.linkTo( entityConfiguration.getEntityType() );
			EntityViewLinkBuilder.ForEntityConfiguration targetConfigurationLinkBuilder = entityViewLinks.linkTo( targetConfiguration.getEntityType() );
			if ( isVisibleConfiguration( targetConfiguration ) ) {
				configureMenuRequestMatchers( builder, targetConfiguration, configurationLinkBuilder, targetConfigurationLinkBuilder );

				String elasticBaseGroupPath = getBaseGroupPath( entityConfiguration );
				builder.item( elasticBaseGroupPath ).enable( false );
			}
			else if ( isVisibleConfiguration( entityConfiguration ) ) {
				configureMenuRequestMatchers( builder, entityConfiguration, configurationLinkBuilder, targetConfigurationLinkBuilder );

			}

		};
	}

	private void configureMenuRequestMatchers( PathBasedMenuBuilder builder,
	                                           EntityConfiguration<?> targetConfiguration,
	                                           EntityViewLinkBuilder.ForEntityConfiguration configurationLinkBuilder,
	                                           EntityViewLinkBuilder.ForEntityConfiguration targetConfigurationLinkBuilder ) {
		String baseGroupPath = getBaseGroupPath( targetConfiguration );
		builder.item( baseGroupPath )
		       .matchRequests(
				       "~" + configurationLinkBuilder.listView().toUriString(), configurationLinkBuilder.listView().toUriString(),
				       "~" + targetConfigurationLinkBuilder.listView().toUriString(), targetConfigurationLinkBuilder.listView().toUriString()
		       );

		AllowableActions allowableActions = targetConfiguration.getAllowableActions();

		if ( allowableActions.contains( AllowableAction.CREATE ) ) {
			builder.item( baseGroupPath + "/create" )
			       .matchRequests( "~" + configurationLinkBuilder.createView().toUriString(), configurationLinkBuilder.createView().toUriString(),
			                       "~" + targetConfigurationLinkBuilder.createView().toUriString(),
			                       targetConfigurationLinkBuilder.createView().toUriString() );
		}
	}

	private String getBaseGroupPath( EntityConfiguration<?> entityConfiguration ) {
		AcrossModuleInfo moduleInfo = entityConfiguration.getAttribute( AcrossModuleInfo.class );
		String group = "/entities";
		if ( moduleInfo != null ) {
			group += "/" + moduleInfo.getName();
		}
		group += "/" + entityConfiguration.getName();
		return group;
	}
}
