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

import com.foreach.across.modules.bootstrapui.elements.ColumnViewElement;
import com.foreach.across.modules.bootstrapui.elements.Grid;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.config.builders.EntityConfigurationBuilder;
import com.foreach.across.modules.entity.controllers.EntityViewCommand;
import com.foreach.across.modules.entity.controllers.EntityViewRequest;
import com.foreach.across.modules.entity.views.EntityListView;
import com.foreach.across.modules.entity.views.EntityListViewPageFetcher;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.processors.WebViewProcessorAdapter;
import com.foreach.across.modules.entity.web.WebViewCreationContext;
import com.foreach.across.modules.hibernate.jpa.repositories.config.EnableAcrossJpaRepositories;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.TemplateViewElement;
import com.foreach.across.samples.entity.EntityModuleTestApplication;
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
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Configuration
@EnableAcrossJpaRepositories(basePackageClasses = EntityModuleTestApplication.class)
public class EntitiesConfiguration implements EntityConfigurer
{
	@Override
	public void configure( EntitiesConfigurationBuilder configuration ) {
		addFilteringForGroupEntity( configuration.entity( Group.class ) );
	}

	private void addFilteringForGroupEntity( EntityConfigurationBuilder<Group> configuration ) {
		configuration.listView()
		             .defaultSort( new Sort( "name" ) )
		             .addProcessor( groupFilteringProcessor() )
		             .pageFetcher( groupFilteringProcessor() );

		configuration.association( "user.group" )
		             .listView()
		             .defaultSort( new Sort( "name" ) )
		             .addProcessor( userInGroupFilteringProcessor() )
		             .pageFetcher( userInGroupFilteringProcessor() );
	}

	@Bean
	protected GroupFilteringProcessor groupFilteringProcessor() {
		return new GroupFilteringProcessor();
	}

	@Bean
	protected UserInGroupFilteringProcessor userInGroupFilteringProcessor() {
		return new UserInGroupFilteringProcessor();
	}

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
			Optional<ContainerViewElement> header = find( elements, "entityForm-header", ContainerViewElement.class );

			header.ifPresent(
					h -> {
						ColumnViewElement row = new ColumnViewElement();
						row.addLayout( Grid.Device.MD.width( Grid.Width.FULL ) );
						row.addChild( new TemplateViewElement( "th/entityModuleTest/group :: filterForm" ) );

						Optional<ColumnViewElement> actions
								= find( h, "entityForm-header-actions", ColumnViewElement.class );

						actions.ifPresent( a -> {
							NodeViewElement newActions = new NodeViewElement( "div" );
							newActions.addCssClass( "pull-right" );
							newActions.getChildren().addAll( a.getChildren() );

							row.addFirstChild( newActions );
						} );

						h.getChildren().clear();
						h.addChild( row );
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
			Optional<ContainerViewElement> header = find( elements, "entityForm-header", ContainerViewElement.class );

			header.ifPresent(
					h -> {
						ColumnViewElement row = new ColumnViewElement();
						row.addLayout( Grid.Device.MD.width( Grid.Width.FULL ) );
						row.addChild( new TemplateViewElement( "th/entityModuleTest/group :: filterForm" ) );

						Optional<ColumnViewElement> actions
								= find( h, "entityForm-header-actions", ColumnViewElement.class );

						actions.ifPresent( a -> {
							NodeViewElement newActions = new NodeViewElement( "div" );
							newActions.addCssClass( "pull-right" );
							newActions.getChildren().addAll( a.getChildren() );

							row.addFirstChild( newActions );
						} );

						h.getChildren().clear();
						h.addChild( row );
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
