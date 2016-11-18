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
import com.foreach.across.modules.entity.config.builders.EntityConfigurationBuilder;
import com.foreach.across.modules.entity.controllers.EntityViewCommand;
import com.foreach.across.modules.entity.controllers.EntityViewRequest;
import com.foreach.across.modules.entity.query.EntityQueryExecutor;
import com.foreach.across.modules.entity.views.EntityListView;
import com.foreach.across.modules.entity.views.EntityListViewPageFetcher;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.processors.WebViewProcessorAdapter;
import com.foreach.across.modules.entity.web.WebViewCreationContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.TemplateViewElement;
import com.foreach.across.samples.entity.application.business.Group;
import com.foreach.across.samples.entity.application.repositories.GroupRepository;
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
 * <li>replaced by a custom filter on Group list</li>
 * <li>add acustom filter on Users list tab for a group</li>
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
		addFilteringForGroupEntity( configuration.withType( Group.class ) );

		configuration.matching( c -> c.hasAttribute( EntityQueryExecutor.class ) )
		             .listView( lvb -> lvb.entityQueryFilter( true ) );
	}

	private void addFilteringForGroupEntity( EntityConfigurationBuilder<Object> configuration ) {
		configuration.listView(
				lvb -> lvb.defaultSort( new Sort( "name" ) )
				          .entityQueryFilter( false )
				          .filter( groupFilteringProcessor() )
		);

		configuration.association( ab -> ab.name( "user.group" ).listView( lvb -> lvb.entityQueryFilter( true ) ) );
		/*
		configuration.association(
				ab -> ab.name( "user.group" )
				        .listView(
						        lvb -> lvb.defaultSort( new Sort( "name" ) )
						                  .filter( userInGroupFilteringProcessor() )
				        )
		);
		*/
	}

	@Bean
	protected GroupFilteringProcessor groupFilteringProcessor() {
		return new GroupFilteringProcessor();
	}

	@Bean
	protected UserInGroupFilteringProcessor userInGroupFilteringProcessor() {
		return new UserInGroupFilteringProcessor();
	}

	/**
	 * Custom filter for a main entity (Group) list view.
	 */
	private static class GroupFilteringProcessor extends WebViewProcessorAdapter<EntityListView> implements EntityListViewPageFetcher<WebViewCreationContext>
	{
		@Autowired
		private GroupRepository groupRepository;

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

						h.addChild( new TemplateViewElement( "th/entityModuleTest/group :: filterForm" ) );
					}
			);
		}

		@Override
		public Page fetchPage( WebViewCreationContext viewCreationContext, Pageable pageable, EntityView model ) {
			EntityViewRequest request = model.getAttribute( "viewRequest" );

			String filter = (String) request.getExtensions().get( "filter" );

			if ( !StringUtils.isBlank( filter ) ) {
				return groupRepository.findByNameContaining( filter, pageable );
			}

			return groupRepository.findAll( pageable );
		}
	}

	/**
	 * Custom filter for an associated entity (User in Group) list view.
	 */
	private static class UserInGroupFilteringProcessor extends WebViewProcessorAdapter<EntityListView> implements EntityListViewPageFetcher<WebViewCreationContext>
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

						h.addChild( new TemplateViewElement( "th/entityModuleTest/group :: filterForm" ) );
					}
			);
		}

		@Override
		public Page fetchPage( WebViewCreationContext viewCreationContext, Pageable pageable, EntityView model ) {
			EntityViewRequest request = model.getAttribute( "viewRequest" );
			Group group = (Group) model.getParentEntity();
			String filter = (String) request.getExtensions().get( "filter" );

			if ( !StringUtils.isBlank( filter ) ) {
				return userRepository.findByGroupAndNameContaining( group, filter, pageable );
			}

			return userRepository.findByGroup( group, pageable );
		}
	}
}
