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
import com.foreach.across.modules.entity.controllers.EntityViewCommand;
import com.foreach.across.modules.entity.controllers.EntityViewRequest;
import com.foreach.across.modules.entity.query.EntityQueryExecutor;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.views.EntityListView;
import com.foreach.across.modules.entity.views.EntityListViewPageFetcher;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.processors.WebViewProcessorAdapter;
import com.foreach.across.modules.entity.web.WebViewCreationContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.TemplateViewElement;
import com.foreach.across.samples.entity.application.business.Group;
import com.foreach.across.samples.entity.application.business.Partner;
import com.foreach.across.samples.entity.application.business.User;
import com.foreach.across.samples.entity.application.repositories.PartnerRepository;
import com.foreach.across.samples.entity.application.repositories.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;

import static com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils.find;

/**
 * Showcase on configuring filtering on list views.
 * Registers 3 filters:
 * <ul>
 * <li>default entity query filter on all entities having an {@link EntityQueryExecutor}</li>
 * <li>replaced by a custom filter on Partner list</li>
 * <li>add a custom filter on Users list tab for a group</li>
 * </ul>
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Configuration
public class EntityFilteringConfiguration implements EntityConfigurer
{
	@Override
	public void configure( EntitiesConfigurationBuilder configuration ) {
		configuration.withType( User.class )
		             .listView( lvb -> lvb.showProperties( "id", "name", "group", "registrationDate" )
		                                  .showResultNumber( false ) );

		configuration.matching( c -> c.hasAttribute( EntityQueryExecutor.class ) )
		             .listView( lvb -> lvb.entityQueryFilter( true ) );

		// Custom filter on partners
		configuration.withType( Partner.class )
		             .listView(
				             lvb -> lvb.defaultSort( "name" )
				                       .entityQueryFilter( false )
				                       .filter( partnerFilterProcessor() )
		             );

		// Custom filters on users under Group
		configuration.withType( Group.class )
		             .association(
				             ab -> ab.name( "user.group" )
				                     .associationType( EntityAssociation.Type.EMBEDDED )
				                     .listView(
						                     lvb -> lvb.defaultSort( new Sort( "name" ) )
						                               .filter( userInGroupFilterProcessor() )
				                     )
		             );
	}

	@Bean
	protected PartnerFilterProcessor partnerFilterProcessor() {
		return new PartnerFilterProcessor();
	}

	@Bean
	protected UserInGroupFilterProcessor userInGroupFilterProcessor() {
		return new UserInGroupFilterProcessor();
	}

	/**
	 * Custom filter for a main entity (Group) list view.
	 */
	private static class PartnerFilterProcessor extends WebViewProcessorAdapter<EntityListView> implements EntityListViewPageFetcher<WebViewCreationContext>
	{
		@Autowired
		private PartnerRepository partnerRepository;

		@Override
		protected void registerCommandExtensions( EntityViewCommand command ) {
			command.addExtensions( "filter", "" );
		}

		@Override
		protected void modifyViewElements( ContainerViewElement elements ) {
			// move the original actions
			Optional<ContainerViewElement> header = find( elements, "entityForm-header",
			                                              ContainerViewElement.class );

			header.ifPresent(
					h -> {
						Optional<NodeViewElement> actions
								= find( h, "entityForm-header-actions", NodeViewElement.class );
						actions.ifPresent( a -> a.addCssClass( "pull-right" ) );

						h.addChild( new TemplateViewElement( "th/entityModuleTest/filters :: filterForm" ) );
					}
			);
		}

		@Override
		public Page fetchPage( WebViewCreationContext viewCreationContext, Pageable pageable, EntityView model ) {
			EntityViewRequest request = model.getAttribute( "viewRequest", EntityViewRequest.class );

			String filter = (String) request.getExtensions().get( "filter" );

			if ( !StringUtils.isBlank( filter ) ) {
				return partnerRepository.findByNameContaining( filter, pageable );
			}

			return partnerRepository.findAll( pageable );
		}
	}

	/**
	 * Custom filter for an associated entity (User in Group) list view.
	 */
	private static class UserInGroupFilterProcessor extends WebViewProcessorAdapter<EntityListView> implements EntityListViewPageFetcher<WebViewCreationContext>
	{
		@Autowired
		private UserRepository userRepository;

		@Override
		protected void registerCommandExtensions( EntityViewCommand command ) {
			command.addExtensions( "filter", "" );
		}

		@Override
		protected void modifyViewElements( ContainerViewElement elements ) {
			// move the original actions
			Optional<ContainerViewElement> header = find( elements, "entityForm-header",
			                                              ContainerViewElement.class );

			header.ifPresent(
					h -> {
						Optional<NodeViewElement> actions
								= find( h, "entityForm-header-actions", NodeViewElement.class );
						actions.ifPresent( a -> a.addCssClass( "pull-right" ) );

						h.addChild( new TemplateViewElement( "th/entityModuleTest/filters :: filterForm" ) );
					}
			);
		}

		@Override
		public Page fetchPage( WebViewCreationContext viewCreationContext, Pageable pageable, EntityView model ) {
			EntityViewRequest request = model.getAttribute( "viewRequest", EntityViewRequest.class );
			Group group = (Group) model.getParentEntity();
			String filter = (String) request.getExtensions().get( "filter" );

			if ( !StringUtils.isBlank( filter ) ) {
				return userRepository.findByGroupAndNameContaining( group, filter, pageable );
			}

			return userRepository.findByGroup( group, pageable );
		}
	}
}
