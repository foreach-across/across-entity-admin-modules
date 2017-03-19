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
import com.foreach.across.modules.entity.views.DefaultEntityViewFactory;
import com.foreach.across.modules.entity.views.EntityView;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

/**
 * Configures a blank {@link EntityViewFactoryBuilder} for a generic readonly view.
 * A generic view only has message prefixes and a custom {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry} processor.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Component
final class GenericViewInitializer extends AbstractViewInitializer<EntityViewFactoryBuilder>
{
	public GenericViewInitializer( AutowireCapableBeanFactory beanFactory,
	                               EntityPropertyRegistryProvider propertyRegistryProvider ) {
		super( beanFactory, propertyRegistryProvider );
	}

	@Override
	protected String templateName() {
		return EntityView.GENERIC_VIEW_NAME;
	}

	@Override
	protected BiConsumer<EntityConfiguration<?>, EntityViewFactoryBuilder> createConfigurationInitializer() {
		return ( entityConfiguration, builder ) -> {
			builder.factoryType( DefaultEntityViewFactory.class )
			       .messagePrefix( "entityViews" )
			       .propertyRegistry( propertyRegistryProvider.createForParentRegistry( entityConfiguration.getPropertyRegistry() ) );
		};
	}

	@Override
	protected BiConsumer<EntityAssociation, EntityViewFactoryBuilder> createAssociationInitializer() {
		return ( entityAssociation, builder ) -> {
			builder.messagePrefix(
					"entityViews.association." + entityAssociation.getName(),
					"entityViews"
			);

			EntityPropertyDescriptor targetProperty = entityAssociation.getTargetProperty();

			if ( targetProperty != null ) {
				builder.properties( props -> props.property( entityAssociation.getTargetProperty().getName() ).writable( false ).hidden( true ) );
			}
		};
	}
}
