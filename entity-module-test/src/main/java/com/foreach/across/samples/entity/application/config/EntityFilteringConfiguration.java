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

import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.query.EntityQueryExecutor;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.processors.EntityViewProcessorAdapter;
import com.foreach.across.modules.entity.views.processors.PageableExtensionViewProcessor;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpMethod;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

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
				                       .viewProcessor( partnerFilterProcessor() )
		             );

		// Custom filters on users under Group
		configuration.withType( Group.class )
		             .association(
				             ab -> ab.name( "user.group" )
				                     .associationType( EntityAssociation.Type.EMBEDDED )
				                     .listView(
						                     lvb -> lvb.defaultSort( new Sort( "name" ) )
						                               .viewProcessor( userInGroupFilterProcessor() )
				                     )
		             )
		             .attribute( EntityAttributes.OPTIONS_ENTITY_QUERY, "name like 'animals%'" );
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
	private static class PartnerFilterProcessor extends EntityViewProcessorAdapter
	{
		private PartnerRepository partnerRepository;

		@Override
		public void initializeCommandObject( EntityViewRequest entityViewRequest, EntityViewCommand command, WebDataBinder dataBinder ) {
			command.addExtension( "filter", "" );
		}

		@Override
		protected void doControl( EntityViewRequest entityViewRequest,
		                          EntityView entityView,
		                          EntityViewCommand command,
		                          BindingResult bindingResult,
		                          HttpMethod httpMethod ) {
			String filter = command.getExtension( "filter", String.class );
			Pageable pageable = command.getExtension( PageableExtensionViewProcessor.DEFAULT_EXTENSION_NAME, Pageable.class );

			if ( !StringUtils.isBlank( filter ) ) {
				entityView.addAttribute( "items", partnerRepository.findByNameContaining( filter, pageable ) );
			}
			else {
				entityView.addAttribute( "items", partnerRepository.findAll( pageable ) );
			}
		}

		@Override
		protected void postRender( EntityViewRequest entityViewRequest,
		                           EntityView entityView,
		                           ContainerViewElement container,
		                           ViewElementBuilderContext builderContext ) {
			Optional<ContainerViewElement> header = find( container, "entityListForm-header", ContainerViewElement.class );
			header.ifPresent(
					h -> {
						Optional<NodeViewElement> actions
								= find( h, "entityListForm-header-actions", NodeViewElement.class );
						actions.ifPresent( a -> a.addCssClass( "pull-right" ) );

						h.addChild( new TemplateViewElement( "th/entityModuleTest/filters :: filterForm" ) );
					}
			);
		}

		@Autowired
		void setPartnerRepository( PartnerRepository partnerRepository ) {
			this.partnerRepository = partnerRepository;
		}
	}

	/**
	 * Custom filter for an associated entity (User in Group) list view.
	 */
	private static class UserInGroupFilterProcessor extends EntityViewProcessorAdapter
	{
		private UserRepository userRepository;

		@Override
		public void initializeCommandObject( EntityViewRequest entityViewRequest,
		                                     EntityViewCommand command,
		                                     WebDataBinder dataBinder ) {
			command.addExtension( "filter", "" );
		}

		@Override
		protected void doControl( EntityViewRequest entityViewRequest,
		                          EntityView entityView,
		                          EntityViewCommand command,
		                          BindingResult bindingResult,
		                          HttpMethod httpMethod ) {
			String filter = command.getExtension( "filter", String.class );
			Pageable pageable = command.getExtension( PageableExtensionViewProcessor.DEFAULT_EXTENSION_NAME, Pageable.class );
			Group group = entityViewRequest.getEntityViewContext().getParentContext().getEntity( Group.class );

			if ( !StringUtils.isBlank( filter ) ) {
				entityView.addAttribute( "items", userRepository.findByGroupAndNameContaining( group, filter, pageable ) );
			}
			else {
				entityView.addAttribute( "items", userRepository.findByGroup( group, pageable ) );
			}
		}

		@Override
		protected void postRender( EntityViewRequest entityViewRequest,
		                           EntityView entityView,
		                           ContainerViewElement container,
		                           ViewElementBuilderContext builderContext ) {
			Optional<ContainerViewElement> header = find( container, "entityListForm-header", ContainerViewElement.class );

			header.ifPresent(
					h -> {
						Optional<NodeViewElement> actions
								= find( h, "entityListForm-header-actions", NodeViewElement.class );
						actions.ifPresent( a -> a.addCssClass( "pull-right" ) );

						h.addChild( new TemplateViewElement( "th/entityModuleTest/filters :: filterForm" ) );
					}
			);
		}

		@Autowired
		void setUserRepository( UserRepository userRepository ) {
			this.userRepository = userRepository;
		}
	}
}
