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

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders;
import com.foreach.across.modules.bootstrapui.elements.ButtonViewElement;
import com.foreach.across.modules.bootstrapui.elements.builder.ColumnViewElementBuilder;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.menu.EntityAdminMenuEvent;
import com.foreach.across.modules.entity.views.processors.ExtensionViewProcessorAdapter;
import com.foreach.across.modules.entity.views.processors.SaveEntityViewProcessor;
import com.foreach.across.modules.entity.views.processors.SingleEntityFormViewProcessor;
import com.foreach.across.modules.entity.views.processors.support.ViewElementBuilderMap;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.samples.entity.application.business.Group;
import com.foreach.across.samples.entity.application.business.Partner;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import static com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders.formGroup;
import static com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders.textbox;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
@Configuration
public class ExtensionConfiguration implements EntityConfigurer
{
	@EventListener
	public void registerPartnerMenuItem( EntityAdminMenuEvent<Partner> partnerMenu ) {
		// get link builder
		if ( partnerMenu.isForUpdate() ) {
			partnerMenu.builder()
			           .item( "/extension", "my extension" )
			           .url( partnerMenu.getLinkBuilder().view( partnerMenu.getEntity() ) + "?view=extension" );
		}
	}

	@EventListener
	public void registerGroupMenuItem( EntityAdminMenuEvent<Group> groupMenu ) {
		// get link builder
		if ( groupMenu.isForUpdate() ) {
			groupMenu.builder()
			         .item( "/extension", "my extension" )
			         .url( groupMenu.getLinkBuilder().view( groupMenu.getEntity() ) + "?view=extension" );
		}
	}

	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		entities.matching( cfg -> Partner.class.equals( cfg.getEntityType() ) || Group.class.equals( cfg.getEntityType() ) )
		        .formView(
				        "extension",
				        fvb -> fvb.viewProcessor( new PartnerExtensionViewProcessor() )
				                  .showProperties()
				                  .removeViewProcessor( SaveEntityViewProcessor.class.getName() )
		        );
	}

	static class PartnerExtensionViewProcessor extends ExtensionViewProcessorAdapter<PartnerExtension>
	{
		@Override
		protected String extensionName() {
			return "details";
		}

		@Override
		protected PartnerExtension createExtension( EntityViewRequest entityViewRequest,
		                                            EntityViewCommand command,
		                                            WebDataBinder dataBinder ) {
			return new PartnerExtension();
		}

		@Override
		protected void doPost( PartnerExtension extension,
		                       BindingResult bindingResult,
		                       EntityView entityView,
		                       EntityViewRequest entityViewRequest ) {
			if ( !bindingResult.hasErrors() ) {
				entityViewRequest.getPageContentStructure()
				                 .addToFeedback(
						                 BootstrapUiBuilders.alert().success().dismissible().text( "Updated url with: " + extension.getUrl() ).build()
				                 );
			}
		}

		@Override
		protected void createViewElementBuilders( PartnerExtension extension,
		                                          EntityViewRequest entityViewRequest,
		                                          EntityView entityView,
		                                          ViewElementBuilderMap builderMap ) {
			builderMap.get( SingleEntityFormViewProcessor.LEFT_COLUMN, ColumnViewElementBuilder.class )
			          .add(
					          formGroup()
							          .label( "URL" )
							          .control( textbox().controlName( controlPrefix() + ".url" ).text( extension.url ) )
			          )
			          .add(
					          formGroup()
							          .label( "Creation year" )
							          .control( textbox().controlName( controlPrefix() + ".creationYear" ).text( "" + extension.creationYear ) )
			          );
		}

		@Override
		protected void postRender( PartnerExtension extension,
		                           EntityViewRequest entityViewRequest,
		                           EntityView entityView,
		                           ContainerViewElement container,
		                           ViewElementBuilderContext builderContext ) {
			EntityViewContext entityViewContext = entityViewRequest.getEntityViewContext();

			container.find( "btn-cancel", ButtonViewElement.class )
			         .ifPresent( button -> button.setUrl( entityViewContext.getLinkBuilder().update( entityViewContext.getEntity() ) ) );
		}
	}

	@Data
	static class PartnerExtension
	{
		@NotBlank
		private String url;

		@Min(1980)
		@Max(2000)
		private int creationYear;
	}
}
