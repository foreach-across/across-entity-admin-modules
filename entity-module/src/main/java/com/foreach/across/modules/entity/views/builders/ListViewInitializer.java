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

import com.foreach.across.modules.entity.conditionals.ConditionalOnAdminWeb;
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
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

/**
 * Configures a blank {@link EntityViewFactoryBuilder} for the {@link EntityView#LIST_VIEW_NAME}.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Component
@ConditionalOnAdminWeb
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
			       .messagePrefix( "views[" + templateName() + "]" )
			       .requiredAllowableAction( AllowableAction.READ )
			       .propertyRegistry( propertyRegistryProvider.createForParentRegistry( entityConfiguration.getPropertyRegistry() ) )
			       .viewElementMode( ViewElementMode.LIST_VALUE )
			       .pageSize( 50 )
			       .showProperties( EntityPropertySelector.READABLE )
			       .viewProcessor( beanFactory.getBean( ListPageStructureViewProcessor.class ) )
			       .viewProcessor( beanFactory.getBean( DefaultValidationViewProcessor.class ), 0 )
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

		if ( isSortable( propertyRegistry.getProperty( "name" ) ) ) {
			return "name";
		}
		if ( isSortable( propertyRegistry.getProperty( "title" ) ) ) {
			return "title";
		}
		if ( isSortable( propertyRegistry.getProperty( "label" ) ) ) {
			return "label";
		}

		return null;
	}

	// only consider persistent properties by default
	private boolean isSortable( EntityPropertyDescriptor descriptor ) {
		return descriptor != null && descriptor.hasAttribute( PersistentProperty.class );
	}

	@Override
	protected BiConsumer<EntityAssociation, EntityListViewFactoryBuilder> createAssociationInitializer() {
		return ( entityAssociation, builder ) -> {
			builder.messagePrefix( "views[" + templateName() + "]" );

			// associations are rendered as single entity
			builder.removeViewProcessor( ListPageStructureViewProcessor.class.getName() );

			SingleEntityPageStructureViewProcessor pageStructureViewProcessor = beanFactory.createBean( SingleEntityPageStructureViewProcessor.class );
			pageStructureViewProcessor.setAddEntityMenu( true );
			pageStructureViewProcessor.setTitleMessageCode( EntityMessages.PAGE_TITLE_UPDATE );
			builder.viewProcessor( pageStructureViewProcessor );

			EntityPropertyDescriptor targetProperty = entityAssociation.getTargetProperty();

			if ( targetProperty != null ) {
				builder.properties( props -> props.property( entityAssociation.getTargetProperty().getName() ).writable( false ).hidden( true ) );
			}

			if ( EntityAssociation.Type.EMBEDDED.equals( entityAssociation.getAssociationType() ) ) {
				AssociationHeaderViewProcessor associationHeaderViewProcessor = beanFactory.createBean( AssociationHeaderViewProcessor.class );
				associationHeaderViewProcessor.setAddEntityMenu( true );
				builder.viewProcessor( associationHeaderViewProcessor );
			}
		};
	}

	private void configureDefaultFallbackFetcher( EntityListViewFactoryBuilder builder ) {
		builder.postProcess( ( factory, registry ) ->
				                     registry.addProcessor(
						                     DefaultEntityFetchingViewProcessor.class.getName(),
						                     beanFactory.createBean( DefaultEntityFetchingViewProcessor.class ),
						                     DefaultEntityFetchingViewProcessor.DEFAULT_ORDER
				                     )
		);
	}
}
