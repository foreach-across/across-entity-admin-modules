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

package com.foreach.across.modules.entity.views.builders;

import com.foreach.across.modules.bootstrapui.elements.Style;
import com.foreach.across.modules.bootstrapui.elements.builder.FormViewElementBuilder;
import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.conditionals.ConditionalOnAdminWeb;
import com.foreach.across.modules.entity.config.builders.EntityViewFactoryBuilder;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistryProvider;
import com.foreach.across.modules.entity.registry.properties.EntityPropertySelector;
import com.foreach.across.modules.entity.views.DefaultEntityViewFactory;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.processors.EntityViewProcessorAdapter;
import com.foreach.across.modules.entity.views.processors.GlobalPageFeedbackViewProcessor;
import com.foreach.across.modules.entity.views.processors.SingleEntityFormViewProcessor;
import com.foreach.across.modules.entity.views.processors.SingleEntityPageStructureViewProcessor;
import com.foreach.across.modules.entity.views.processors.support.ViewElementBuilderMap;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.entity.web.links.EntityViewLinkBuilder;
import com.foreach.across.modules.entity.web.links.SingleEntityViewLinkBuilder;
import com.foreach.across.modules.spring.security.actions.AllowableAction;
import com.foreach.across.modules.web.ui.elements.builder.ContainerViewElementBuilder;
import com.foreach.across.modules.web.ui.elements.builder.ContainerViewElementBuilderSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

import static com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders.button;
import static com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders.container;

/**
 * //TODO document
 *
 * @author Marc Vanbrabant, Steven Gentens
 * @since 3.2.0
 */
@Component
@ConditionalOnAdminWeb
final class ReadonlyViewInitializer extends AbstractViewInitializer<EntityViewFactoryBuilder>
{
	public ReadonlyViewInitializer( AutowireCapableBeanFactory beanFactory,
	                                EntityPropertyRegistryProvider propertyRegistryProvider ) {
		super( beanFactory, propertyRegistryProvider );
	}

	@Override
	protected String templateName() {
		return EntityView.READONLY_UPDATE_VIEW_NAME;
	}

	@Override
	protected BiConsumer<EntityConfiguration<?>, EntityViewFactoryBuilder> createConfigurationInitializer() {
		return ( entityConfiguration, builder ) -> {
			builder.factoryType( DefaultEntityViewFactory.class )
			       .messagePrefix( "views[" + templateName() + "]" )
			       .requiredAllowableAction( AllowableAction.READ )
			       .propertyRegistry( propertyRegistryProvider.createForParentRegistry( entityConfiguration.getPropertyRegistry() ) )
			       .viewElementMode( ViewElementMode.FORM_READ )
			       .showProperties( EntityPropertySelector.READABLE )
			       .viewProcessor( beanFactory.getBean( GlobalPageFeedbackViewProcessor.class ) );

			if ( entityConfiguration.hasAttribute( EntityAttributes.TRANSACTION_MANAGER_NAME ) ) {
				builder.transactionManager( entityConfiguration.<String, String>getAttribute( EntityAttributes.TRANSACTION_MANAGER_NAME, String.class ) );
			}

			SingleEntityPageStructureViewProcessor pageStructureViewProcessor = beanFactory.createBean( SingleEntityPageStructureViewProcessor.class );
			pageStructureViewProcessor.setAddEntityMenu( true );
			pageStructureViewProcessor.setTitleMessageCode( EntityMessages.PAGE_TITLE_VIEW );
			builder.viewProcessor( pageStructureViewProcessor );

			SingleEntityFormViewProcessor formViewProcessor = beanFactory.createBean( SingleEntityFormViewProcessor.class );
			formViewProcessor.setAddDefaultButtons( false );
			formViewProcessor.setAddGlobalBindingErrors( true );
			builder.viewProcessor( formViewProcessor )
			       .viewProcessor( new ReadOnlyFormViewPostProcessor() );
		};
	}

	@RequiredArgsConstructor
	private static final class ReadOnlyFormViewPostProcessor extends EntityViewProcessorAdapter
	{
		@Override
		protected void createViewElementBuilders( EntityViewRequest entityViewRequest,
		                                          EntityView entityView,
		                                          ViewElementBuilderMap builderMap ) {
			FormViewElementBuilder form = (FormViewElementBuilder) builderMap.get( SingleEntityFormViewProcessor.FORM );
			EntityViewContext entityViewContext = entityViewRequest.getEntityViewContext();

			Object entity = getEntity( entityViewContext );
			EntityViewLinkBuilder linkBuilder = entityViewContext.getLinkBuilder();
			SingleEntityViewLinkBuilder linkToEntity = linkBuilder.forInstance( entity );

			String updateUrl = linkToEntity.updateView().withFromUrl( linkToEntity.toUriString() ).toUriString();

			ContainerViewElementBuilderSupport buttons = buildButtonsContainer( entityViewContext, updateUrl, linkBuilder.listView().toUriString() );
			form.add( buttons );
			builderMap.put( SingleEntityFormViewProcessor.FORM_BUTTONS, buttons );
		}

		@SuppressWarnings("unchecked")
		private ContainerViewElementBuilderSupport buildButtonsContainer( EntityViewContext entityViewContext, String updateUrl, String cancelUrl ) {
			EntityMessages messages = entityViewContext.getEntityMessages();
			ContainerViewElementBuilder container = container().name( "buttons" );
			EntityConfiguration entityConfiguration = entityViewContext.getEntityConfiguration();
			Object entity = getEntity( entityViewContext );

			if ( entityConfiguration.getAllowableActions( entity ).contains( AllowableAction.UPDATE ) ) {
				container.add(
						button().name( "btn-update" )
						        .link( updateUrl )
						        .style( Style.PRIMARY )
						        .text( messages.withNameSingular( "actions.modify", entity ) )
				);
			}

			container.add(
					button().name( "btn-cancel" )
					        .link( cancelUrl )
					        .text( messages.messageWithFallback( "actions.cancel" ) )
			);
			return container;
		}

		private Object getEntity( EntityViewContext entityViewContext ) {
			return entityViewContext.getEntity();
		}

	}

}
