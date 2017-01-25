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
package com.foreach.across.modules.entity.views;

import com.foreach.across.modules.adminweb.menu.EntityAdminMenu;
import com.foreach.across.modules.adminweb.ui.PageContentStructure;
import com.foreach.across.modules.bootstrapui.components.BootstrapUiComponentFactory;
import com.foreach.across.modules.bootstrapui.elements.FormViewElement;
import com.foreach.across.modules.bootstrapui.elements.Grid;
import com.foreach.across.modules.bootstrapui.elements.HiddenFormElement;
import com.foreach.across.modules.bootstrapui.elements.Style;
import com.foreach.across.modules.entity.controllers.EntityControllerAttributes;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.entity.web.EntityLinkBuilder;
import com.foreach.across.modules.entity.web.WebViewCreationContext;
import com.foreach.across.modules.web.menu.Menu;
import com.foreach.across.modules.web.menu.MenuFactory;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import java.util.Optional;

/**
 * @author Arne Vandamme
 */
public class EntityFormViewFactory<V extends ViewCreationContext>
		extends SingleEntityViewFactory<V, EntityFormView>
{
	public static final String FORM_NAME = "entityForm";
	public static final String FORM_LEFT = "entityForm-left";
	public static final String FORM_RIGHT = "entityForm-right";

	private MenuFactory menuFactory;
	private BootstrapUiComponentFactory bootstrapUiComponentFactory;

	@Autowired
	public void setMenuFactory( MenuFactory menuFactory ) {
		this.menuFactory = menuFactory;
	}

	@Autowired
	public void setBootstrapUiComponentFactory( BootstrapUiComponentFactory bootstrapUiComponentFactory ) {
		this.bootstrapUiComponentFactory = bootstrapUiComponentFactory;
	}

	public EntityFormViewFactory() {
		setViewElementMode( ViewElementMode.FORM_WRITE );
	}

	@Override
	protected EntityFormView createEntityView( ModelMap model ) {
		return new EntityFormView( model );
	}

	@Override
	protected void preparePageContentStructure( PageContentStructure page, V creationContext, EntityFormView view ) {
		super.preparePageContentStructure( page, creationContext, view );

		EntityMessages entityMessages = view.getEntityMessages();
		EntityConfiguration<Object> entityConfiguration = view.getEntityConfiguration();

		Object entity = view.getOriginalEntity();

		if ( creationContext.isForAssociation() ) {
			entity = view.getParentEntity();
			entityConfiguration = creationContext.getEntityAssociation().getSourceEntityConfiguration();
			entityMessages = new EntityMessages( entityConfiguration.getEntityMessageCodeResolver() );
		}

		Menu menu;

		if ( entity == null ) {
			menu = menuFactory.buildMenu( new EntityAdminMenu<>( entityConfiguration.getEntityType() ) );
			page.setPageTitle( entityMessages.createPageTitle() );
		}
		else {
			String entityLabel = entityConfiguration.getLabel( entity );
			Class<?> entityType = entityConfiguration.getEntityType();
			menu = menuFactory.buildMenu( new EntityAdminMenu( entityConfiguration.getEntityType(),
			                                                   entityType.cast( entity ) ) );
			page.setPageTitle( entityMessages.updatePageTitle( entityLabel ) );
		}

		if ( menu != null ) {
			page.addToNav(
					bootstrapUiComponentFactory.nav( menu )
					                           .tabs()
					                           .replaceGroupBySelectedItem()
					                           .build()
			);
		}
	}

	@Override
	protected ContainerViewElement buildViewElements( V viewCreationContext,
	                                                  EntityViewElementBuilderContext<EntityFormView> viewElementBuilderContext,
	                                                  EntityMessageCodeResolver messageCodeResolver ) {
		Optional<String> fromUrl = Optional.ofNullable( retrieveFromUrl( viewCreationContext ) );

		ContainerViewElement elements
				= super.buildViewElements( viewCreationContext, viewElementBuilderContext, messageCodeResolver );

		EntityFormView entityView = viewElementBuilderContext.getEntityView();
		EntityLinkBuilder linkBuilder = entityView.getEntityLinkBuilder();
		EntityMessages messages = entityView.getEntityMessages();

		String cancelUrl = fromUrl.orElseGet( linkBuilder::overview );

		return bootstrapUi
				.form()
				.name( FORM_NAME )
				.commandAttribute(
						EntityControllerAttributes.VIEW_REQUEST )
				.post()
				.noValidate()
				.action(
						buildActionUrl( viewElementBuilderContext ) )
				.add(
						bootstrapUi.row()
						           .add(
								           bootstrapUi.column(
										           Grid.Device.MD
												           .width( Grid.Width.HALF ) )
								                      .name( FORM_LEFT )
								                      .add( elements )
						           )
						           .add(
								           bootstrapUi.column(
										           Grid.Device.MD
												           .width( Grid.Width.HALF ) )
								                      .name( FORM_RIGHT )
						           )
				)
				.add(
						bootstrapUi.container()
						           .name( "buttons" )
						           .add(
								           bootstrapUi.button()
								                      .name( "btn-save" )
								                      .style( Style.PRIMARY )
								                      .submit()
								                      .text( messages.messageWithFallback(
										                      "actions.save" ) )
						           )
						           .add(
								           bootstrapUi.button()
								                      .name( "btn-cancel" )
								                      .link( cancelUrl )
								                      .text(
										                      messages.messageWithFallback(
												                      "actions.cancel" )
								                      )
						           )
				)
				.postProcessor( ( ctx, form ) -> {
					fromUrl.ifPresent(
							url -> {
								HiddenFormElement hiddenFrom =
										new HiddenFormElement();
								hiddenFrom.setControlName( "from" );
								hiddenFrom.setValue( url );
								form.addChild( hiddenFrom );
							}
					);
				} )
				.postProcessor( ( ctx, form ) -> {
					addGlobalFormErrors( messageCodeResolver, entityView, messages, ctx, form );
				} )
				.build( viewElementBuilderContext );
	}

	private void addGlobalFormErrors( EntityMessageCodeResolver messageCodeResolver,
	                                  EntityFormView entityView,
	                                  EntityMessages messages,
	                                  ViewElementBuilderContext ctx,
	                                  FormViewElement form ) {
		BindingResult errors = entityView.getBindingResult();

		if ( errors != null && errors.hasErrors() ) {
			val entityConfiguration = entityView.getEntityConfiguration();
			val originalEntity = entityView.getOriginalEntity();
			val entityLabel = originalEntity != null
					? entityConfiguration.getLabel( entityView.getOriginalEntity() ) : "";

			val alert = bootstrapUi
					.alert()
					.danger()
					.text( messages.withNameSingular( "feedback.validationErrors", entityLabel ) );

			if ( errors.hasGlobalErrors() ) {
				val globalErrorList = bootstrapUi.node( "ul" ).css( "global-errors" );

				errors.getGlobalErrors().forEach(
						e -> globalErrorList.add( bootstrapUi.html(
								"<strong>" + messageCodeResolver.getMessage( e ) + "</strong>"
						) )
				);

				alert.add( globalErrorList );
			}

			form.addFirstChild( alert.build( ctx ) );
		}
	}

	private String retrieveFromUrl( ViewCreationContext viewCreationContext ) {
		return ( (WebViewCreationContext) viewCreationContext ).getRequest().getParameter( "from" );
	}

	private String buildActionUrl( EntityViewElementBuilderContext<EntityFormView> viewElementBuilderContext ) {
		EntityFormView formView = viewElementBuilderContext.getEntityView();

		if ( formView.isUpdate() ) {
			return formView.getEntityLinkBuilder().update( formView.getOriginalEntity() );
		}

		return formView.getEntityLinkBuilder().create();
	}
}
