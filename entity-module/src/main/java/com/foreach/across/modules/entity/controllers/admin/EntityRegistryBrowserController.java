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

package com.foreach.across.modules.entity.controllers.admin;

import com.foreach.across.core.annotations.ConditionalOnDevelopmentMode;
import com.foreach.across.core.context.info.AcrossModuleInfo;
import com.foreach.across.modules.adminweb.annotations.AdminWebController;
import com.foreach.across.modules.adminweb.menu.AdminMenu;
import com.foreach.across.modules.adminweb.menu.AdminMenuEvent;
import com.foreach.across.modules.adminweb.ui.PageContentStructure;
import com.foreach.across.modules.bootstrapui.components.BootstrapUiComponentFactory;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.DispatchingEntityViewFactory;
import com.foreach.across.modules.entity.views.EntityViewFactory;
import com.foreach.across.modules.entity.views.processors.EntityPropertyRegistryViewProcessor;
import com.foreach.across.modules.entity.views.processors.MessagePrefixingViewProcessor;
import com.foreach.across.modules.entity.views.processors.support.EntityViewProcessorRegistry;
import com.foreach.across.modules.web.menu.Menu;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import com.foreach.across.modules.web.menu.RequestMenuSelector;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.TemplateViewElement;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.ui.Model;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Development mode controller that allows browsing the entire registry.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@AdminWebController
@RequestMapping("/ax/developer/entityModule/entities")
@ConditionalOnDevelopmentMode
@RequiredArgsConstructor
class EntityRegistryBrowserController
{
	private static final String DOC_BASE_URL = "http://across.foreach.be/docs/across-standard-modules/EntityModule";
	private static final String DOC_VIEW_PROCESSORS = "%s/%s/reference/#appendix-view-processors";
	private static final String DOC_ATTRIBUTES_CONFIGURATION = "%s/%s/reference/#appendix-entity-configuration-attributes";
	private static final String DOC_ATTRIBUTES_PROPERTY = "%s/%s/reference/#appendix-entity-property-descriptor-attributes";
	private static final String DOC_MESSAGE_CODES = "%s/%s/reference/#appendix-message-codes";
	private static final String DOC_ENTITY_VIEWS = "%s/%s/reference/#entity-views";

	private final EntityRegistry entityRegistry;
	private final PageContentStructure page;
	private final BootstrapUiComponentFactory bootstrapUiComponentFactory;
	private final AcrossModuleInfo entityModuleInfo;

	@EventListener
	public void registerAdminMenu( AdminMenuEvent menuEvent ) {
		menuEvent.builder().item( "/ax/developer/entityModule/entities", "Entities" );
	}

	@GetMapping
	public String listAllEntities( Model model ) {
		Comparator<EntityConfiguration> comparator
				= Comparator.comparing( cfg -> cfg.hasAttribute( AcrossModuleInfo.class ) ? cfg.getAttribute( AcrossModuleInfo.class ).getName() : "" );

		ArrayList<EntityConfiguration> configurations = new ArrayList<>( entityRegistry.getEntities() );
		configurations.sort( comparator );

		model.addAttribute( "entities", configurations );

		page.setPageTitle( "Registered entities" );
		page.addChild( new TemplateViewElement( "th/entity/dev/registry-browser :: listAllEntities(${entities})" ) );

		return PageContentStructure.TEMPLATE;
	}

	@GetMapping({ "/{entityName:.+}", "/{entityName:.+}/{detailView}", "/{entityName:.+}/{detailView}/{detailName}" })
	public String entityDetails( AdminMenu adminMenu,
	                             @PathVariable String entityName,
	                             @PathVariable(required = false) String detailView,
	                             @PathVariable(required = false) String detailName,
	                             @RequestParam(required = false, name = "property") String propertyName,
	                             Model model,
	                             ViewElementBuilderContext builderContext,
	                             HttpServletRequest request ) {
		EntityConfiguration<?> entity = entityRegistry.getEntityConfiguration( entityName );
		adminMenu.breadcrumbLeaf( entity.getName() );

		model.addAttribute( "entity", entity );
		registerDocumentationLinks( model );

		page.setPageTitle( "Registered entities: " + entity.getName() );
		page.setRenderAsTabs( true );

		String baseUrl = builderContext.buildLink( "@adminWeb:/ax/developer/entityModule/entities/" + entity.getName() );

		Menu menu = new PathBasedMenuBuilder()
				.item( "/details", "Details", baseUrl ).order( 1 ).and()
				.item( "/associations", "Associations", baseUrl + "/associations" ).order( 2 ).and()
				.item( "/views", "Views", baseUrl + "/views" ).order( 3 ).and()
				.build();
		menu.sort();
		menu.select( new RequestMenuSelector( request ) );

		page.addToNav( bootstrapUiComponentFactory.nav( menu ).tabs().build() );

		if ( "associations".equals( detailView ) ) {
			if ( detailName != null ) {
				page.addChild( new TemplateViewElement( "th/entity/dev/registry-browser :: entityAssociation(${entity},${detailName})" ) );
			}
			else {
				List<EntityAssociation> incomingAssociations = new ArrayList<>();

				entityRegistry.getEntities()
				              .stream()
				              .filter( c -> !c.equals( entity ) )
				              .flatMap( c -> c.getAssociations().stream() )
				              .filter( a -> ( (EntityAssociation) a ).getTargetEntityConfiguration().equals( entity ) )
				              .forEach( a -> incomingAssociations.add( (EntityAssociation) a ) );

				model.addAttribute( "incomingAssociations", incomingAssociations );

				page.addChild( new TemplateViewElement( "th/entity/dev/registry-browser :: entityAssociations(${entity})" ) );
			}
		}
		else if ( "views".equals( detailView ) ) {
			if ( detailName != null ) {
				model.addAttribute( "viewName", detailName );

				final EntityMessageCodeResolver codeResolver = getEntityOrAssociationCodeResolver( detailName, entity );

				EntityViewFactory viewFactory = entity.getViewFactory( detailName );
				if ( viewFactory instanceof DispatchingEntityViewFactory ) {
					EntityViewProcessorRegistry processorRegistry = ( (DispatchingEntityViewFactory) viewFactory ).getProcessorRegistry();

					processorRegistry
							.getProcessorRegistration( EntityPropertyRegistryViewProcessor.class.getName() )
							.ifPresent(
									registration -> {
										EntityPropertyRegistry propertyRegistry = registration.getProcessor( EntityPropertyRegistryViewProcessor.class )
										                                                      .getPropertyRegistry();

										if ( propertyName != null ) {
											EntityPropertyDescriptor property = propertyRegistry.getProperty( propertyName );
											model.addAttribute( "property", property );

											page.addChild( new TemplateViewElement(
													"th/entity/dev/registry-browser :: propertyDetails(entity=${entity},property=${property},viewName=${viewName})" )
											);
										}
										else {
											ArrayList<EntityPropertyDescriptor> propertyDescriptors = new ArrayList<>(
													propertyRegistry.getRegisteredDescriptors() );
											propertyDescriptors.sort( Comparator.comparing( EntityPropertyDescriptor::getName ) );
											model.addAttribute( "properties", propertyDescriptors );
										}
									}
							);

					processorRegistry
							.getProcessorRegistration( MessagePrefixingViewProcessor.class.getName() )
							.ifPresent(
									registration -> {
										String[] prefixes = registration.getProcessor( MessagePrefixingViewProcessor.class )
										                                .getMessagePrefixes();
										if ( codeResolver != null ) {
											model.addAttribute(
													"messageCodePrefixes",
													codeResolver.prefixedResolver( prefixes ).buildMessageCodes(
															propertyName != null ? "properties." + propertyName : "*", true
													)
											);
										}
									}
							);

					if ( propertyName == null ) {
						model.addAttribute( "processors",
						                    processorRegistry
								                    .getProcessorNames()
								                    .stream()
								                    .map( processorRegistry::getProcessorRegistration )
								                    .map( Optional::get )
								                    .collect( Collectors.toList() )
						);
					}
				}

				if ( propertyName == null ) {
					page.addChild( new TemplateViewElement( "th/entity/dev/registry-browser :: entityView(${entity},${viewName})" ) );
				}

			}
			else {
				page.addChild( new TemplateViewElement( "th/entity/dev/registry-browser :: entityViews(${entity})" ) );
			}
		}
		else {
			if ( propertyName != null ) {
				EntityPropertyDescriptor property = entity.getPropertyRegistry().getProperty( propertyName );
				model.addAttribute( "property", property );

				EntityMessageCodeResolver codeResolver = entity.getEntityMessageCodeResolver();
				if ( codeResolver != null ) {
					model.addAttribute( "messageCodePrefixes", codeResolver.buildMessageCodes( "properties." + property.getName(), true ) );
				}

				page.addChild( new TemplateViewElement(
						"th/entity/dev/registry-browser :: propertyDetails(entity=${entity},property=${property},viewName=${null})" ) );
			}
			else {
				EntityMessageCodeResolver codeResolver = entity.getEntityMessageCodeResolver();
				if ( codeResolver != null ) {
					model.addAttribute( "messageCodePrefixes", codeResolver.buildMessageCodes( "*", true ) );
				}

				ArrayList<EntityPropertyDescriptor> propertyDescriptors = new ArrayList<>( entity.getPropertyRegistry().getRegisteredDescriptors() );
				propertyDescriptors.sort( Comparator.comparing( EntityPropertyDescriptor::getName ) );
				model.addAttribute( "allProperties", propertyDescriptors );

				page.addChild( new TemplateViewElement( "th/entity/dev/registry-browser :: entityDetails(${entity})" ) );
			}
		}

		return PageContentStructure.TEMPLATE;
	}

	private EntityMessageCodeResolver getEntityOrAssociationCodeResolver( @PathVariable(required = false) String detailName, EntityConfiguration<?> entity ) {
		EntityMessageCodeResolver codeResolver = entity.getEntityMessageCodeResolver();

		if ( StringUtils.contains( detailName, "_" ) ) {
			String associationName = StringUtils.split( detailName, "_" )[0];
			EntityAssociation association = entity.association( associationName );
			if ( association != null && association.hasAttribute( EntityMessageCodeResolver.class ) ) {
				codeResolver = association.getAttribute( EntityMessageCodeResolver.class );
			}
		}
		return codeResolver;
	}

	private void registerDocumentationLinks( Model model ) {
		model.addAttribute( "javadoc", new JavadocHelper() );
		model.addAttribute( "referenceDocumentationViewProcessors",
		                    String.format( DOC_VIEW_PROCESSORS, DOC_BASE_URL, entityModuleInfo.getVersionInfo().getVersion() ) );
		model.addAttribute( "referenceDocumentationConfigurationAttributes",
		                    String.format( DOC_ATTRIBUTES_CONFIGURATION, DOC_BASE_URL, entityModuleInfo.getVersionInfo().getVersion() ) );
		model.addAttribute( "referenceDocumentationPropertyAttributes",
		                    String.format( DOC_ATTRIBUTES_PROPERTY, DOC_BASE_URL, entityModuleInfo.getVersionInfo().getVersion() ) );
		model.addAttribute( "referenceDocumentationMessageCodes",
		                    String.format( DOC_MESSAGE_CODES, DOC_BASE_URL, entityModuleInfo.getVersionInfo().getVersion() ) );
		model.addAttribute( "referenceDocumentationEntityViews",
		                    String.format( DOC_ENTITY_VIEWS, DOC_BASE_URL, entityModuleInfo.getVersionInfo().getVersion() ) );
	}

	class JavadocHelper
	{
		public String type( Class<?> type ) {
			if ( type != null ) {
				Class<?> actual = ClassUtils.getUserClass( type );
				String path = actual.getName().replace( '.', '/' );

				if ( path.startsWith( "com/foreach/across/modules/entity" ) ) {
					return String.format( "<a href=\"%s/%s/javadoc/%s.html\" target=\"_blank\" title=\"Open javadoc\">%s</a>",
					                      DOC_BASE_URL,
					                      entityModuleInfo.getVersionInfo().getVersion(),
					                      path,
					                      actual.getSimpleName() );
				}

				return String.format( "<span title=\"%s\">%s</span>", actual.getName(), actual.getSimpleName() );
			}

			return "";
		}

		public String name( String typeName ) {
			if ( typeName != null ) {
				String path = typeName.replace( '.', '/' );

				if ( path.startsWith( "com/foreach/across/modules/entity" ) ) {
					return String.format( "<a href=\"%s/%s/javadoc/%s.html\" target=\"_blank\" title=\"Open javadoc\">%s</a>",
					                      DOC_BASE_URL,
					                      entityModuleInfo.getVersionInfo().getVersion(),
					                      path,
					                      typeName );
				}
			}

			return typeName;
		}
	}
}
