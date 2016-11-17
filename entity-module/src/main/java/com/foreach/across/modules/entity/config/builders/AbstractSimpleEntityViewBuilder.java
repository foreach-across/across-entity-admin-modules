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

import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityViewRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyComparators;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistryProvider;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyRegistry;
import com.foreach.across.modules.entity.views.ConfigurablePropertiesEntityViewFactorySupport;
import com.foreach.across.modules.entity.views.EntityViewProcessor;
import com.foreach.across.modules.entity.views.EntityViewViewFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Basic view builder supporting both {@link com.foreach.across.modules.entity.views.SimpleEntityViewFactorySupport}
 * and {@link com.foreach.across.modules.entity.views.ConfigurablePropertiesEntityViewFactorySupport}.
 *
 * @author Arne Vandamme
 */
@Deprecated
public abstract class AbstractSimpleEntityViewBuilder<T extends ConfigurablePropertiesEntityViewFactorySupport, SELF extends AbstractSimpleEntityViewBuilder>
		extends AbstractEntityViewBuilder<T, SELF>
{
	private final SELF viewBuilder;
	protected String[] viewPropertySelectorRule;
	protected EntityPropertyComparators.Ordered viewPropertyOrder;
	private String template;
	private EntityViewPropertyRegistryBuilder propertyRegistryBuilder;
	private Collection<EntityViewProcessor> processors = new ArrayList<>();

	@SuppressWarnings("unchecked")
	public AbstractSimpleEntityViewBuilder() {
		this.viewBuilder = (SELF) this;
	}

	@SuppressWarnings("unchecked")
	public SELF template( String template ) {
		this.template = template;
		return (SELF) this;
	}

	@SuppressWarnings("unchecked")
	public EntityViewPropertyRegistryBuilder properties() {
		if ( propertyRegistryBuilder == null ) {
			propertyRegistryBuilder = createPropertiesBuilder();
		}

		return propertyRegistryBuilder;
	}

	public EntityViewPropertyRegistryBuilder properties( String... propertyNames ) {
		return properties().filter( propertyNames );
	}

	protected abstract EntityViewPropertyRegistryBuilder createPropertiesBuilder();

	/**
	 * Add a processor object that should be applied to the view factory.
	 *
	 * @param processor instance - should not be null
	 * @return current builder
	 * @see com.foreach.across.modules.entity.views.processors.WebViewProcessorAdapter
	 */
	@SuppressWarnings("unchecked")
	public SELF addProcessor( EntityViewProcessor processor ) {
		Assert.notNull( processor );
		processors.add( processor );

		return (SELF) this;
	}

	/**
	 * @return parent builder
	 */
	public abstract Object and();

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
				registry = registryFactory.createForParentRegistry(
						( (EntityConfiguration) viewRegistry ).getPropertyRegistry()
				);
			}
			else if ( viewRegistry instanceof EntityAssociation ) {
				registry = registryFactory.createForParentRegistry(
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

	public abstract class EntityViewPropertyRegistryBuilder<MYSELF extends EntityViewPropertyRegistryBuilder>
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
}
