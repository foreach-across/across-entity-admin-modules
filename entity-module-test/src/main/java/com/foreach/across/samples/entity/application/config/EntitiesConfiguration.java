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

import com.foreach.across.modules.bootstrapui.elements.TextboxFormElement;
import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.config.builders.EntityConfigurationBuilder;
import com.foreach.across.modules.entity.controllers.EntityViewCommand;
import com.foreach.across.modules.entity.controllers.EntityViewRequest;
import com.foreach.across.modules.entity.registry.EntityFactory;
import com.foreach.across.modules.entity.validators.EntityValidatorSupport;
import com.foreach.across.modules.entity.views.EntityListView;
import com.foreach.across.modules.entity.views.EntityListViewPageFetcher;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.ViewCreationContext;
import com.foreach.across.modules.entity.views.processors.EntityQueryFilterProcessor;
import com.foreach.across.modules.entity.views.processors.WebViewProcessorAdapter;
import com.foreach.across.modules.entity.web.WebViewCreationContext;
import com.foreach.across.modules.hibernate.jpa.repositories.config.EnableAcrossJpaRepositories;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.TemplateViewElement;
import com.foreach.across.samples.entity.EntityModuleTestApplication;
import com.foreach.across.samples.entity.application.business.Group;
import com.foreach.across.samples.entity.application.business.User;
import com.foreach.across.samples.entity.application.repositories.GroupRepository;
import com.foreach.across.samples.entity.application.repositories.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.*;
import java.util.function.UnaryOperator;

import static com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils.find;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Configuration
@EnableAcrossJpaRepositories(basePackageClasses = EntityModuleTestApplication.class)
public class EntitiesConfiguration implements EntityConfigurer
{
	@Configuration
	public static class NestedEntityConfig implements EntityConfigurer
	{
		@Autowired
		private EntityQueryFilterProcessor entityQueryFilterProcessor;

		@Override
		public void configure( EntitiesConfigurationBuilder configuration ) {
			addFilteringForGroupEntity( configuration.withType( Group.class ) );

			configuration.withType( User.class )
			             .listView(
					             lvb -> lvb.viewProcessor( entityQueryFilterProcessor )
					                       .pageFetcher( entityQueryFilterProcessor )
			             );
		}

		private void addFilteringForGroupEntity( EntityConfigurationBuilder<Object> configuration ) {
			configuration.listView(
					lvb -> lvb.defaultSort( new Sort( "name" ) )
					          .viewProcessor( groupFilteringProcessor() )
					          .pageFetcher( groupFilteringProcessor() )
			);

			configuration.association(
					ab -> ab.name( "user.group" )
					        .listView(
							        lvb -> lvb.defaultSort( new Sort( "name" ) )
							                  .viewProcessor( userInGroupFilteringProcessor() )
							                  .pageFetcher( userInGroupFilteringProcessor() )
					        )
			);
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

	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		// configuration of a dummy entity represented by a map, requires manual configuration of pretty much everything:
		// entity configuration, the properties, the backing model, the views - nothing will have been created up front
		List<Map<String, Object>> categories = new ArrayList<>();
		Map<String, Object> tv = new HashMap<>();
		tv.put( "id", "tv" );
		tv.put( "name", "Televisions" );
		categories.add( tv );

		entities.create()
		        .name( "category" )
		        .entityType( Map.class, false )
		        .displayName( "Category" )
		        .attribute( Validator.class, categoryValidator() )
		        .properties(
				        props -> props
						        .property( "id" )
						        .displayName( "Id" )
						        .propertyType( String.class )
						        .attribute( EntityAttributes.CONTROL_NAME, "[id]" )
						        .attribute( TextboxFormElement.Type.class, TextboxFormElement.Type.TEXT )
						        .writable( true )
						        .spelValueFetcher( "get('id')" )
						        .order( 1 )
						        .and()
						        .property( "name" )
						        .displayName( "Name" )
						        .propertyType( String.class )
						        .attribute( EntityAttributes.CONTROL_NAME, "[name]" )
						        .attribute( TextboxFormElement.Type.class, TextboxFormElement.Type.TEXT )
						        .writable( true )
						        .spelValueFetcher( "get('name')" )
						        .order( 2 )
		        )
		        .entityModel(
				        model -> {
					        UnaryOperator<Object> saveMethod = cat -> {
						        Map<String, Object> category = (Map<String, Object>) cat;
						        Optional<Map<String, Object>> existing = categories
								        .stream()
								        .filter( m -> m.get( "id" ).equals( category.get( "id" ) ) )
								        .findFirst();

						        if ( existing.isPresent() ) {
							        existing.ifPresent( e -> e.putAll( category ) );
						        }
						        else {
							        categories.add( category );
						        }

						        return category;
					        };

					        model
							        .entityFactory( new CategoryEntityFactory() )
							        .entityInformation( new CategoryEntityInformation() )
							        .labelPrinter( ( o, locale ) -> (String) ( (Map) o ).get( "name" ) )
							        .findOneMethod( id -> categories.stream()
							                                        .filter( m -> id.equals( m.get( "id" ) ) )
							                                        .findFirst().orElse( null ) )
							        .saveMethod( saveMethod )
							        .deleteMethod( categories::remove );
				        }
		        )
		        .listView( lvb -> lvb.pageFetcher( new EntityListViewPageFetcher()
		        {
			        @Override
			        public Page fetchPage( ViewCreationContext viewCreationContext,
			                               Pageable pageable,
			                               EntityView model ) {
				        return new PageImpl<>( categories );
			        }
		        } ) )
		        .createFormView( fvb -> fvb.showProperties( "id", "name" ) )
		        .updateFormView( fvb -> fvb.showProperties( "name" ) )
		        .deleteFormView( dvb -> dvb.showProperties( "." ) )
		        .show();

	}

	@Bean
	protected CategoryValidator categoryValidator() {
		return new CategoryValidator();
	}

	private static class CategoryValidator extends EntityValidatorSupport<Map<String, Object>>
	{
		@Override
		public boolean supports( Class<?> aClass ) {
			return Map.class.equals( aClass );
		}

		@Override
		protected void postValidation( Map<String, Object> entity, Errors errors ) {
			String prefix = StringUtils.removeEnd( errors.getNestedPath(), "." );
			errors.setNestedPath( "" );

			if ( StringUtils.defaultString( Objects.toString( entity.get( "id" ) ) ).length() == 0 ) {
				errors.rejectValue( prefix + "[id]", "NotBlank" );
			}
			if ( StringUtils.defaultString( Objects.toString( entity.get( "name" ) ) ).length() == 0 ) {
				errors.rejectValue( prefix + "[name]", "NotBlank" );
			}

			errors.pushNestedPath( "entity" );
		}
	}

	private static class CategoryEntityFactory implements EntityFactory<Object>
	{
		@Override
		public Object createNew( Object... args ) {
			return new HashMap<>();
		}

		@Override
		public Object createDto( Object entity ) {
			return new HashMap<>( (Map<?, ?>) entity );
		}
	}

	private static class CategoryEntityInformation implements EntityInformation<Map, String>
	{
		@Override
		public boolean isNew( Map map ) {
			return map.containsKey( "id" );
		}

		@Override
		public String getId( Map map ) {
			return (String) map.get( "id" );
		}

		@Override
		public Class<String> getIdType() {
			return String.class;
		}

		@Override
		public Class<Map> getJavaType() {
			return Map.class;
		}
	}
}
