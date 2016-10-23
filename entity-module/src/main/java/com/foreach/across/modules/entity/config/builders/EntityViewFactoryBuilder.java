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
import com.foreach.across.modules.entity.views.EntityViewFactory;
import com.foreach.across.modules.entity.views.EntityViewProcessor;
import com.foreach.across.modules.entity.views.SimpleEntityViewFactorySupport;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Builder for creating a single {@link com.foreach.across.modules.entity.views.EntityViewFactory}.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class EntityViewFactoryBuilder<T extends EntityViewFactory>
{
	private final AutowireCapableBeanFactory beanFactory;

	private Class<? extends T> factoryType;

	protected String[] viewPropertySelectorRule;
	protected EntityPropertyComparators.Ordered viewPropertyOrder;
	private String template;
	private AbstractSimpleEntityViewBuilder.EntityViewPropertyRegistryBuilder propertyRegistryBuilder;
	private Collection<EntityViewProcessor> processors = new ArrayList<>();

	public EntityViewFactoryBuilder( AutowireCapableBeanFactory beanFactory ) {
		this.beanFactory = beanFactory;
	}

	/**
	 * Configure the type of {@link EntityViewFactory} that should be created
	 * in case of a {@link #build()} call.
	 *
	 * @param factoryType to create
	 * @return current builder
	 */
	public EntityViewFactoryBuilder<T> factory( Class<? extends T> factoryType ) {
		this.factoryType = factoryType;
		return this;
	}

	/**
	 * Configures the template this {@link EntityViewFactory} should use.
	 * This requires the factory type to be a {@link SimpleEntityViewFactorySupport}.
	 *
	 * @param template the view should render
	 * @return current builder
	 */
	public EntityViewFactoryBuilder<T> template( String template ) {
		this.template = template;
		return this;
	}

	/*

	 */
	public EntityViewFactoryBuilder<T> showProperties( String... propertyNames ) {
		return this;
	}

	/**
	 * Add a processor object that should be applied to the view factory.
	 *
	 * @param processor instance - should not be null
	 * @return current builder
	 * @see com.foreach.across.modules.entity.views.processors.WebViewProcessorAdapter
	 */
	public EntityViewFactoryBuilder<T> viewProcessor( EntityViewProcessor processor ) {
		Assert.notNull( processor );
		processors.add( processor );

		return this;
	}

	/**
	 * Build a new factory instance of the type configured using {@link #factory(Class)}.
	 * If no {@link #factory(Class)} has been configured, an exception will be thrown.
	 *
	 * @return factory instance
	 */
	public T build() {
		Assert.notNull( factoryType );

		T viewFactory = beanFactory.createBean( factoryType );
		apply( viewFactory );

		return viewFactory;
	}

	/**
	 * Apply the builder to an existing {@link EntityViewFactory}.
	 *
	 * @param viewFactory to apply the builder settings to
	 */
	public void apply( T viewFactory ) {
		if ( viewFactory instanceof SimpleEntityViewFactorySupport ) {
			applySimpleAttributes( (SimpleEntityViewFactorySupport) viewFactory );
		}
	}

	private void applySimpleAttributes( SimpleEntityViewFactorySupport viewFactory ) {
		if ( template != null ) {
			viewFactory.setTemplate( template );
		}
	}

	/*

	@SuppressWarnings("unchecked")
	@Override
	protected T createFactoryInstance( AutowireCapableBeanFactory beanFactory ) {
		return (T) beanFactory.getBean( EntityViewViewFactory.class );
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void applyToViewFactory( AutowireCapableBeanFactory beanFactory, EntityViewRegistry viewRegistry,
	                                   T factory ) {
		if ( template != null ) {
			factory.setTemplate( template );
		}

		if ( !processors.isEmpty() ) {
			factory.setProcessors( merge( factory.getProcessors(), processors ) );
		}

		EntityPropertyRegistry registry = factory.getPropertyRegistry();

		if ( registry == null ) {
			EntityPropertyRegistryProvider registryFactory = beanFactory.getBean( EntityPropertyRegistryProvider.class );
			if ( viewRegistry instanceof EntityConfiguration ) {
				registry = registryFactory.createWithParent(
						( (EntityConfiguration) viewRegistry ).getPropertyRegistry()
				);
			}
			else if ( viewRegistry instanceof EntityAssociation ) {
				registry = registryFactory.createWithParent(
						( (EntityAssociation) viewRegistry ).getTargetEntityConfiguration().getPropertyRegistry()
				);
			}

			factory.setPropertyRegistry( registry );
		}

		if ( propertyRegistryBuilder != null && registry instanceof MutableEntityPropertyRegistry ) {
			propertyRegistryBuilder.apply( (MutableEntityPropertyRegistry) registry );
		}

		if ( viewPropertySelectorRule != null ) {
			factory.getPropertySelector().configure( viewPropertySelectorRule );
		}

		if ( viewPropertyOrder != null ) {
			factory.setPropertyComparator( viewPropertyOrder );
		}

	}

	private <V> Collection<V> merge( Collection<V> original, Collection<V> additional ) {
		List<V> total = new ArrayList<>( original.size() + additional.size() );
		total.addAll( original );
		total.addAll( additional );

		return total;
	}
*/
	/*
	public abstract class EntityViewPropertyRegistryBuilder<MYSELF extends AbstractSimpleEntityViewBuilder.EntityViewPropertyRegistryBuilder>
			extends AbstractEntityPropertyRegistryBuilder<MYSELF>
	{
		@SuppressWarnings("unchecked")
		public MYSELF filter( String... propertyNames ) {
			and().viewPropertySelectorRule = propertyNames;
			and().viewPropertyOrder = EntityPropertyComparators.ordered( propertyNames );

			return (MYSELF) this;
		}

		@Override
		public SELF and() {
			return viewBuilder;
		}
	}
	*/
}
