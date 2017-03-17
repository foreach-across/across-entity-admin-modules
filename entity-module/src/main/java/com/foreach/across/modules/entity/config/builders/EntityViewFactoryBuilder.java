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

package com.foreach.across.modules.entity.config.builders;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyComparators;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertySelector;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyRegistry;
import com.foreach.across.modules.entity.views.ConfigurablePropertiesEntityViewFactorySupport;
import com.foreach.across.modules.entity.views.EntityViewFactory;
import com.foreach.across.modules.entity.views.EntityViewProcessor;
import com.foreach.across.modules.entity.views.SimpleEntityViewFactorySupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.function.Consumer;

/**
 * Builder for creating a single {@link com.foreach.across.modules.entity.views.EntityViewFactory}.
 * Supports {@link SimpleEntityViewFactorySupport} and
 * {@link com.foreach.across.modules.entity.views.ConfigurablePropertiesEntityViewFactorySupport}.  Which properties
 * will apply will depend on the actual {@link #factoryType(Class)} configured.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EntityViewFactoryBuilder
{
	private final AutowireCapableBeanFactory beanFactory;
	private final Collection<Consumer<EntityPropertyRegistryBuilder>> registryConsumers = new ArrayDeque<>();
	private final Collection<EntityViewProcessor> processors = new LinkedHashSet<>();
	private final Collection<EntityViewProcessor> processorsToRemove = new LinkedHashSet<>();

	private Class<? extends EntityViewFactory> factoryType;
	private EntityViewFactory factory;
	private String[] propertiesToShow;
	private String template;

	@Autowired
	public EntityViewFactoryBuilder( AutowireCapableBeanFactory beanFactory ) {
		this.beanFactory = beanFactory;
	}

	protected <U> U getBean( Class<U> beanType ) {
		return beanFactory.getBean( beanType );
	}

	protected <U> U createBean( Class<U> beanType ) {
		return beanFactory.createBean( beanType );
	}

	/**
	 * Configure the type of {@link EntityViewFactory} that should be created
	 * in case of a {@link #build()} call.
	 *
	 * @param factoryType to create
	 * @return current builder
	 */
	public EntityViewFactoryBuilder factoryType( Class<? extends EntityViewFactory> factoryType ) {
		this.factoryType = factoryType;
		return this;
	}

	/**
	 * Configure the specific of {@link EntityViewFactory} that should be used for this view.
	 * This will take precedence over a registered {@link #factoryType(Class)}.
	 *
	 * @param factory to use
	 * @return current builder
	 */
	public EntityViewFactoryBuilder factory( EntityViewFactory factory ) {
		Assert.notNull( factory );
		this.factory = factory;
		return this;
	}

	/**
	 * Configures the template this {@link EntityViewFactory} should use.
	 * This requires the factory type to be a at least of type {@link SimpleEntityViewFactorySupport}.
	 *
	 * @param template the view should render
	 * @return current builder
	 */
	public EntityViewFactoryBuilder template( String template ) {
		this.template = template;
		return this;
	}

	/**
	 * Customize the property registry attached to this view.
	 *
	 * @param registryConsumer to customize the property registry builder
	 * @return current builder
	 */
	public EntityViewFactoryBuilder properties( Consumer<EntityPropertyRegistryBuilder> registryConsumer ) {
		Assert.notNull( registryConsumer );
		registryConsumers.add( registryConsumer );
		return this;
	}

	/**
	 * List of properties to be shown (in order).
	 *
	 * @param propertyNames property names in order
	 * @return current builder
	 */
	public EntityViewFactoryBuilder showProperties( String... propertyNames ) {
		this.propertiesToShow = propertyNames;
		return this;
	}

	/**
	 * Add a processor object that should be applied to the view factory.
	 *
	 * @param processor instance - should not be null
	 * @return current builder
	 * @see com.foreach.across.modules.entity.views.processors.WebViewProcessorAdapter
	 */
	public EntityViewFactoryBuilder viewProcessor( EntityViewProcessor processor ) {
		Assert.notNull( processor );
		processors.add( processor );

		return this;
	}

	/**
	 * Removes a previously registered processor instance.
	 *
	 * @param processor instance - should not be null
	 * @return current builder
	 */
	public EntityViewFactoryBuilder removeViewProcessor( EntityViewProcessor processor ) {
		Assert.notNull( processor );
		processors.remove( processor );
		processorsToRemove.add( processor );
		return this;
	}

	/**
	 * Build a new factory instance of the type configured using {@link #factoryType(Class)}.
	 * If no {@link #factoryType(Class)} has been configured, an exception will be thrown.
	 *
	 * @return factory instance
	 */
	public EntityViewFactory build() {
		Assert.notNull( factoryType );

		EntityViewFactory viewFactory = factory != null ? factory : createNewViewFactory( factoryType );
		apply( viewFactory );

		return viewFactory;
	}

	protected EntityViewFactory createNewViewFactory( Class<? extends EntityViewFactory> viewFactoryType ) {
		return createBean( viewFactoryType );
	}

	/**
	 * Apply the builder to an existing {@link EntityViewFactory}.
	 *
	 * @param viewFactory to apply the builder settings to
	 */
	void apply( EntityViewFactory viewFactory ) {
		if ( viewFactory instanceof SimpleEntityViewFactorySupport ) {
			applySimpleAttributes( (SimpleEntityViewFactorySupport) viewFactory );
		}
		if ( viewFactory instanceof ConfigurablePropertiesEntityViewFactorySupport ) {
			applyPropertyConfiguration( (ConfigurablePropertiesEntityViewFactorySupport) viewFactory );
		}
	}

	private void applyPropertyConfiguration( ConfigurablePropertiesEntityViewFactorySupport viewFactory ) {
		if ( propertiesToShow != null ) {
			EntityPropertySelector selector = viewFactory.getPropertySelector();
			if ( selector != null ) {
				selector.configure( propertiesToShow );
			}
			viewFactory.setPropertyComparator( EntityPropertyComparators.ordered( propertiesToShow ) );
		}

		if ( !registryConsumers.isEmpty() ) {
			EntityPropertyRegistry propertyRegistry = viewFactory.getPropertyRegistry();

			if ( propertyRegistry instanceof MutableEntityPropertyRegistry ) {
				EntityPropertyRegistryBuilder registryBuilder = new EntityPropertyRegistryBuilder();
				registryConsumers.forEach( c -> c.accept( registryBuilder ) );
				registryBuilder.apply( (MutableEntityPropertyRegistry) viewFactory.getPropertyRegistry() );
			}
			else {
				throw new IllegalStateException(
						"Attempting to modify properties but not a MutableEntityPropertyRegistry" );
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void applySimpleAttributes( SimpleEntityViewFactorySupport viewFactory ) {
		if ( template != null ) {
			viewFactory.setTemplate( template );
		}

		if ( !processors.isEmpty() ) {
			viewFactory.setProcessors( processors );
		}

		if ( !processorsToRemove.isEmpty() ) {
			processorsToRemove.forEach( viewFactory.getProcessors()::remove );
		}
	}
}
