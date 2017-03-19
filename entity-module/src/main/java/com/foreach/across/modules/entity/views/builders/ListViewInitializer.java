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

import com.foreach.across.modules.entity.config.builders.EntityListViewFactoryBuilder;
import com.foreach.across.modules.entity.config.builders.EntityViewFactoryBuilder;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistryProvider;
import com.foreach.across.modules.entity.registry.properties.EntityPropertySelector;
import com.foreach.across.modules.entity.views.DefaultEntityViewFactory;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.processors.*;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.spring.security.actions.AllowableAction;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

/**
 * Configures a blank {@link EntityViewFactoryBuilder} for the {@link EntityView#LIST_VIEW_NAME}.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Component
final class ListViewInitializer extends AbstractViewInitializer<EntityListViewFactoryBuilder>
{
	public ListViewInitializer( AutowireCapableBeanFactory beanFactory,
	                            EntityPropertyRegistryProvider propertyRegistryProvider ) {
		super( beanFactory, propertyRegistryProvider );
	}

	@Override
	protected String templateName() {
		return EntityView.LIST_VIEW_NAME;
	}

	@Override
	protected BiConsumer<EntityConfiguration<?>, EntityListViewFactoryBuilder> createConfigurationInitializer() {
		return ( entityConfiguration, builder ) -> {
			builder.factoryType( DefaultEntityViewFactory.class )
			       .messagePrefix( "entityViews." + templateName(), "entityViews" )
			       .requiredAllowableAction( AllowableAction.READ )
			       .propertyRegistry( propertyRegistryProvider.createForParentRegistry( entityConfiguration.getPropertyRegistry() ) )
			       .viewElementMode( ViewElementMode.LIST_VALUE )
			       .pageSize( 50 )
			       .showProperties( EntityPropertySelector.ALL )
			       .viewProcessor( beanFactory.getBean( GlobalPageFeedbackViewProcessor.class ) );

			String defaultSort = determineDefaultSort( entityConfiguration );
			if ( defaultSort != null ) {
				builder.defaultSort( defaultSort );
			}

			ListFormViewProcessor listFormViewProcessor = beanFactory.createBean( ListFormViewProcessor.class );
			listFormViewProcessor.setAddDefaultButtons( true );
			builder.viewProcessor( listFormViewProcessor );

			SortableTableRenderingViewProcessor tableRenderingViewProcessor = beanFactory.createBean( SortableTableRenderingViewProcessor.class );
			tableRenderingViewProcessor.setIncludeDefaultActions( true );
			tableRenderingViewProcessor.setFormName( ListFormViewProcessor.DEFAULT_FORM_NAME );
			builder.viewProcessor( tableRenderingViewProcessor );

			configureDefaultFallbackFetcher( builder );
		};
	}

	private String determineDefaultSort( EntityConfiguration<?> entityConfiguration ) {
		EntityPropertyRegistry propertyRegistry = entityConfiguration.getPropertyRegistry();

		if ( propertyRegistry.contains( "name" ) ) {
			return "name";
		}
		if ( propertyRegistry.contains( "title" ) ) {
			return "title";
		}
		if ( propertyRegistry.contains( "label" ) ) {
			return "label";
		}

		return null;
	}

	@Override
	protected BiConsumer<EntityAssociation, EntityListViewFactoryBuilder> createAssociationInitializer() {
		return ( entityAssociation, builder ) -> {
			builder.messagePrefix(
					"entityViews.association." + entityAssociation.getName() + "." + templateName(),
					"entityViews." + templateName(),
					"entityViews"
			);

			SingleEntityPageStructureViewProcessor pageStructureViewProcessor = beanFactory.createBean( SingleEntityPageStructureViewProcessor.class );
			pageStructureViewProcessor.setAddEntityMenu( true );
			pageStructureViewProcessor.setTitleMessageCode( EntityMessages.PAGE_TITLE_UPDATE );
			builder.viewProcessor( pageStructureViewProcessor );

			EntityPropertyDescriptor targetProperty = entityAssociation.getTargetProperty();

			if ( targetProperty != null ) {
				builder.properties( props -> props.property( entityAssociation.getTargetProperty().getName() ).writable( false ).hidden( true ) );
			}
		};
	}

	private void configureDefaultFallbackFetcher( EntityListViewFactoryBuilder builder ) {
		builder.postProcess( ( factory, registry ) -> {
			registry.addProcessor(
					DefaultEntityFetchingViewProcessor.class.getName(),
					new DefaultEntityFetchingViewProcessor(),
					DefaultEntityFetchingViewProcessor.DEFAULT_ORDER
			);
		} );
	}
}
