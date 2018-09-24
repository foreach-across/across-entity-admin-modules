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

import com.foreach.across.modules.entity.config.builders.EntityViewFactoryBuilder;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistryProvider;
import com.foreach.across.modules.entity.views.processors.AssociationHeaderViewProcessor;
import com.foreach.across.modules.entity.views.processors.SingleEntityPageStructureViewProcessor;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.util.function.BiConsumer;

/**
 * Component base class for registering both a configuration and association initializer.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
abstract class AbstractViewInitializer<T extends EntityViewFactoryBuilder>
{
	protected final AutowireCapableBeanFactory beanFactory;
	protected final EntityPropertyRegistryProvider propertyRegistryProvider;

	private BiConsumer<EntityConfiguration<?>, T> configurationInitializer;
	private BiConsumer<EntityAssociation, T> associationInitializer;

	public AbstractViewInitializer( AutowireCapableBeanFactory beanFactory,
	                                EntityPropertyRegistryProvider propertyRegistryProvider ) {
		this.beanFactory = beanFactory;
		this.propertyRegistryProvider = propertyRegistryProvider;

		configurationInitializer = createConfigurationInitializer();
		associationInitializer = createAssociationInitializer();
	}

	@Autowired
	public final void register( EntityViewFactoryBuilderInitializer initializer ) {
		initializer.registerConfigurationInitializer( templateName(), configurationInitializer );
		initializer.registerAssociationInitializer( templateName(), associationInitializer );
	}

	protected abstract String templateName();

	protected abstract BiConsumer<EntityConfiguration<?>, T> createConfigurationInitializer();

	protected BiConsumer<EntityAssociation, T> createAssociationInitializer() {
		return ( entityAssociation, builder ) -> {
			builder.messagePrefix( "views[" + templateName() + "]" );

			EntityPropertyDescriptor targetProperty = entityAssociation.getTargetProperty();

			if ( targetProperty != null ) {
				builder.properties( props -> props.property( entityAssociation.getTargetProperty().getName() ).writable( false ).hidden( true ) );
			}

			builder.postProcess( SingleEntityPageStructureViewProcessor.class, p -> p.setTitleMessageCode( EntityMessages.PAGE_TITLE_UPDATE ) );

			AssociationHeaderViewProcessor associationHeaderViewProcessor = beanFactory.createBean( AssociationHeaderViewProcessor.class );
			associationHeaderViewProcessor.setAddEntityMenu( true );
			builder.viewProcessor( associationHeaderViewProcessor );
		};
	}
}
