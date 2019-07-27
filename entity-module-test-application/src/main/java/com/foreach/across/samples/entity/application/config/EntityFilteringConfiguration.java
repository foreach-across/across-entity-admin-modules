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

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiElements;
import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.actions.EntityConfigurationAllowableActionsBuilder;
import com.foreach.across.modules.entity.actions.FixedEntityAllowableActionsBuilder;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.query.EntityQueryConditionTranslator;
import com.foreach.across.modules.entity.query.EntityQueryExecutor;
import com.foreach.across.modules.entity.query.EntityQueryOps;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.menu.EntityAdminMenuEvent;
import com.foreach.across.modules.entity.views.processors.AssociationHeaderViewProcessor;
import com.foreach.across.modules.entity.views.processors.EntityViewProcessorAdapter;
import com.foreach.across.modules.entity.views.processors.PageableExtensionViewProcessor;
import com.foreach.across.modules.entity.views.processors.query.EQLStringValueOptionEnhancer;
import com.foreach.across.modules.entity.views.processors.support.EntityPageStructureRenderedEvent;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.modules.entity.web.links.SingleEntityViewLinkBuilder;
import com.foreach.across.modules.hibernate.business.IdBasedEntity;
import com.foreach.across.modules.spring.security.actions.AllowableAction;
import com.foreach.across.modules.spring.security.actions.AllowableActionSet;
import com.foreach.across.modules.spring.security.actions.AllowableActions;
import com.foreach.across.modules.web.resource.WebResource;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.resource.WebResourceRule;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.TemplateViewElement;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import com.foreach.across.samples.entity.application.business.Group;
import com.foreach.across.samples.entity.application.business.Note;
import com.foreach.across.samples.entity.application.business.Partner;
import com.foreach.across.samples.entity.application.business.User;
import com.foreach.across.samples.entity.application.repositories.PartnerRepository;
import com.foreach.across.samples.entity.application.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpMethod;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import java.util.Collections;
import java.util.Optional;

import static com.foreach.across.modules.entity.views.EntityViewCustomizers.basicSettings;
import static com.foreach.across.modules.entity.views.EntityViewCustomizers.formSettings;
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
@RequiredArgsConstructor
public class EntityFilteringConfiguration implements EntityConfigurer
{
	@EventListener
	public void modifyGroupPageTitle( EntityPageStructureRenderedEvent<Group> page ) {
		if ( page.isListView() ) {
			page.getPageContentStructure().setPageTitle( "List view" );
		}
		else if ( page.holdsEntity() ) {
			page.getPageContentStructure().addToPageTitleSubText( TextViewElement.text( "[update]" ) );
		}
		else {
			page.getPageContentStructure().addToPageTitleSubText( TextViewElement.text( "[create]" ) );
		}
	}

	@EventListener
	public void modifyAssociationMenu( EntityAdminMenuEvent<Note> menuEvent ) {
		if ( menuEvent.getViewContext().isForAssociation() ) {
			if ( menuEvent.isForUpdate() ) {
				SingleEntityViewLinkBuilder linkBuilder = menuEvent.getLinkBuilder().forInstance( menuEvent.getEntity() );
				menuEvent.builder()
				         .item( linkBuilder.withViewName( "customListView" ).toString(), "Custom list view" );
			}
		}
	}

	@Override
	public void configure( EntitiesConfigurationBuilder configuration ) {
		configuration.withType( Note.class )
		             .attribute( EntityAttributes.LINK_TO_DETAIL_VIEW, true )
		             .allowableActionsBuilder( new EntityConfigurationAllowableActionsBuilder()
		             {
			             @Override
			             public AllowableActions getAllowableActions( EntityConfiguration<?> entityConfiguration ) {
				             return FixedEntityAllowableActionsBuilder.DEFAULT_ALLOWABLE_ACTIONS;
			             }

			             @Override
			             public <V> AllowableActions getAllowableActions( EntityConfiguration<V> entityConfiguration, V entity ) {
				             IdBasedEntity item = (IdBasedEntity) entity;
				             AllowableActionSet allowableActions = new AllowableActionSet();
				             if ( Math.floorMod( item.getId(), 2 ) == 0 ) {
					             allowableActions.add( AllowableAction.READ );
				             }
				             if ( Math.floorMod( item.getId(), 4 ) == 0 ) {
					             allowableActions.add( AllowableAction.UPDATE );
				             }
				             allowableActions.add( AllowableAction.DELETE );
				             return allowableActions;
			             }
		             } )
		             .properties( props -> props.property( "text" )
		                                        .valueFetcher( entity -> "" )
		                                        .propertyType( TypeDescriptor.valueOf( String.class ) )
		                                        .viewElementType( ViewElementMode.CONTROL, BootstrapUiElements.TEXTAREA )
		                                        .attribute( EntityQueryConditionTranslator.class,
		                                                    EntityQueryConditionTranslator.expandingOr( "name", "content" ) )
		                                        .hidden( true )

		             )
		             .association( ab -> ab.name( "note.parent" )
		                                   .targetEntityType( Note.class )
		                                   .associationType( EntityAssociation.Type.EMBEDDED )
		                                   .listView( "customListView",
		                                              lvb -> lvb.pageFetcher( pageable -> new PageImpl<>( Collections.emptyList(), pageable, 0L ) )
		                                                        .postProcess( AssociationHeaderViewProcessor.class,
		                                                                      p -> p.setTitleMessageCode( EntityMessages.PAGE_TITLE_UPDATE )
		                                                                            .setAddEntityMenu( true ) )
		                                   )
		                                   .formView( "customView", basicSettings().adminMenu( "customView" )
		                                                                           .andThen( formSettings().forExtension( false )
		                                                                                                   .addFormButtons( true ) ) )
		             )
		             .listView( lvb -> lvb.showProperties( "*", "lastModified", "parent.lastModified" )
		                                  .entityQueryFilter(
				                                  eqf -> eqf.showProperties( "text", "parent.content", "lastModifiedDate" )
				                                            .properties(
						                                            props -> props.property( "lastModifiedDate" )
						                                                          .writable( true )
						                                                          .viewElementType( ViewElementMode.FILTER_CONTROL,
						                                                                            BootstrapUiElements.DATETIME )
				                                            )
				                                            .basicMode( true )
				                                            .advancedMode( true )
		                                  )
		                                  .showOnlyItemsWithAction( AllowableAction.READ )
		             )
		;
		/*configuration.withType( Client.class )
		             .properties( props -> props.property( "phones" )
		                                        .attribute( EntityPropertyHandlingType.class, EntityPropertyHandlingType.BINDER )
		                                        .viewElementType( ViewElementMode.FORM_WRITE, EmbeddedCollectionOrMapElementBuilderFactory.ELEMENT_TYPE ) );
*/
		configuration.withType( User.class )
//		             .properties(
//				             props -> props.property( "address" )
//				                           .attribute( EntityPropertyHandlingType.class, EntityPropertyHandlingType.BINDER )
//		             )
                     .listView( lvb -> lvb.showProperties( "id", "group", "registrationDate", "active" )
                                          .properties(
		                                          props -> props.property( "id" )
		                                                        .attribute( Sort.Order.class, new Sort.Order( Sort.Direction.DESC, "name" ) )
				                                          .<User>valueFetcher( user -> user.getId() + " - " + user.getName() )
                                          )
                                          .showResultNumber( false )
                                          .viewProcessor( new EntityViewProcessorAdapter()
                                          {
	                                          @Override
	                                          protected void registerWebResources( EntityViewRequest entityViewRequest,
	                                                                               EntityView entityView,
	                                                                               WebResourceRegistry webResourceRegistry ) {
		                                          webResourceRegistry.apply(
				                                          WebResourceRule.add( WebResource.javascript( "@static:/entityModuleTest/js/test.js" ) )
				                                                         .toBucket( WebResource.JAVASCRIPT_PAGE_END )
		                                          );
	                                          }
                                          } )
                                          .entityQueryFilter( eqf -> eqf.showProperties( "name", "group", "active" )
                                                                        .basicMode( true )
                                                                        .advancedMode( true )
                                                                        .multiValue( "group" )
                                                                        .properties( props -> props
		                                                                        .property( "group" )
		                                                                        .attribute( EntityAttributes.OPTIONS_ENHANCER,
		                                                                                    EQLStringValueOptionEnhancer.create( Group::getName ) )
                                                                        )
                                          )
                     )
                     .view( EntityView.SUMMARY_VIEW_NAME, vb -> vb.showProperties( "name", "group", "address" ) );

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
		             .properties(
				             props -> props.property( "name" )
				                           .attribute( EntityQueryConditionTranslator.class, EntityQueryConditionTranslator.ignoreCase() ).and()
				                           .property( "userCount" )
				                           .displayName( "# Users" )
				                           .valueFetcher( Group::calculateUserCount )
		             )
		             .viewElementBuilder(
				             ViewElementMode.LIST_VALUE,
				             viewElementBuilderContext -> {
					             Group group = EntityViewElementUtils.currentPropertyValue( viewElementBuilderContext, Group.class );

					             return group != null
							             ? TextViewElement.text( group.getName() + " (" + group.getUsers().size() + " users)" )
							             : TextViewElement.text( "" );
				             }
		             )
		             .listView( lvb -> lvb.entityQueryPredicate( "name not like 'small people%'" )
		                                  .showProperties( "name", "userCount" )
		                                  .entityQueryFilter( eql -> eql.basicMode( true )
		                                                                .showProperties( "name", "users" )
		                                                                .multiValue( "users" )
		                                                                .advancedMode( true )
		                                                                .properties( props -> props.property( "name" )
		                                                                                           .attribute( EntityQueryOps.class,
		                                                                                                       EntityQueryOps.LIKE_IC ) ) ) )
		             .association(
				             ab -> ab.name( "user.group" )
				                     .associationType( EntityAssociation.Type.EMBEDDED )
				                     .listView(
						                     lvb -> lvb.defaultSort( new Sort( "name" ) )
						                               .viewProcessor( userInGroupFilterProcessor() )
				                     )
		             )
		             .attribute( EntityAttributes.OPTIONS_ENTITY_QUERY, "name like 'animals%' order by id desc" );
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
					h -> h.addFirstChild( new TemplateViewElement( "th/entityModuleTest/filters :: filterForm" ) )
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
					h -> h.addFirstChild( new TemplateViewElement( "th/entityModuleTest/filters :: filterForm" ) )
			);
		}

		@Autowired
		void setUserRepository( UserRepository userRepository ) {
			this.userRepository = userRepository;
		}
	}
}
