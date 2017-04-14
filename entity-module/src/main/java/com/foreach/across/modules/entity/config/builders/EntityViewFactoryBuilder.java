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

import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertySelector;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyRegistry;
import com.foreach.across.modules.entity.views.DispatchingEntityViewFactory;
import com.foreach.across.modules.entity.views.EntityViewFactory;
import com.foreach.across.modules.entity.views.EntityViewProcessor;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.processors.*;
import com.foreach.across.modules.entity.views.processors.support.EntityViewProcessorRegistry;
import com.foreach.across.modules.spring.security.actions.AllowableAction;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Builder for creating a single {@link com.foreach.across.modules.entity.views.EntityViewFactory}.
 * Supports configuring default processors on a {@link DispatchingEntityViewFactory}.  If a custom factory
 * type is being used, most properties will have no effect.
 *
 * @author Arne Vandamme
 * @see DispatchingEntityViewFactory
 * @see com.foreach.across.modules.entity.views.DefaultEntityViewFactory
 * @since 2.0.0
 */
@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EntityViewFactoryBuilder
{
	private final AutowireCapableBeanFactory beanFactory;
	private final Collection<Consumer<EntityPropertyRegistryBuilder>> registryConsumers = new ArrayDeque<>();
	private final Collection<ProcessorEntry> processors = new ArrayDeque<>();
	private final Collection<BiConsumer<EntityViewFactory, EntityViewProcessorRegistry>> postProcessors = new ArrayDeque<>();

	private Class<? extends EntityViewFactory> factoryType;
	private EntityViewFactory factory;
	private EntityPropertySelector propertiesToShow;
	private ViewElementMode viewElementMode;
	private String[] messagePrefixes;
	private String template;
	private AllowableAction requiredAllowableAction;
	private EntityPropertyRegistry propertyRegistry;

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
	 * Configure the specific {@link EntityViewFactory} that should be used for this view.
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
	 *
	 * @param template the view should render
	 * @return current builder
	 * @see TemplateViewProcessor
	 */
	public EntityViewFactoryBuilder template( String template ) {
		this.template = template;
		return this;
	}

	/**
	 * Set the property registry that should be attached to this view.  Will add a {@link EntityPropertyRegistryViewProcessor}.
	 * This is required before a property registry can be customized using {@link #properties(Consumer)}.
	 *
	 * @param propertyRegistry to attach
	 * @return current builder
	 */
	public EntityViewFactoryBuilder propertyRegistry( EntityPropertyRegistry propertyRegistry ) {
		this.propertyRegistry = propertyRegistry;
		return this;
	}

	/**
	 * Customize the property registry attached to this view.  Requires a {@link EntityPropertyRegistryViewProcessor} to be set.
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
		EntityPropertySelector selector = EntityPropertySelector.of( propertyNames );
		propertiesToShow = propertiesToShow != null ? propertiesToShow.combine( selector ) : selector;
		return this;
	}

	/**
	 * Configure the rendering mode for the properties.
	 *
	 * @param viewElementMode mode
	 * @return current builder
	 */
	public EntityViewFactoryBuilder viewElementMode( ViewElementMode viewElementMode ) {
		this.viewElementMode = viewElementMode;
		return this;
	}

	/**
	 * Add a processor object that should be applied to the view factory.
	 *
	 * @param processor instance - should not be null
	 * @return current builder
	 * @see com.foreach.across.modules.entity.views.processors.EntityViewProcessorAdapter
	 * @see com.foreach.across.modules.entity.views.processors.SimpleEntityViewProcessorAdapter
	 */
	public EntityViewFactoryBuilder viewProcessor( EntityViewProcessor processor ) {
		Assert.notNull( processor );
		return viewProcessor( viewProcessorName( processor.getClass() ), processor );
	}

	/**
	 * Add a processor object that should be applied to the view factory.
	 * Specify an explicit order for the processor.
	 *
	 * @param processor instance - should not be null
	 * @return current builder
	 * @see com.foreach.across.modules.entity.views.processors.EntityViewProcessorAdapter
	 * @see com.foreach.across.modules.entity.views.processors.SimpleEntityViewProcessorAdapter
	 */
	public EntityViewFactoryBuilder viewProcessor( EntityViewProcessor processor, int order ) {
		Assert.notNull( processor );
		return viewProcessor( viewProcessorName( processor.getClass() ), processor, order );
	}

	private String viewProcessorName( Class<?> processorType ) {
		return ClassUtils.getUserClass( processorType ).getName();
	}

	/**
	 * Add a processor object that should be applied to the view factory.
	 * Register the processor under the given name.
	 *
	 * @param processorName unique processor name
	 * @param processor     instance - should not be null
	 * @return current builder
	 * @see #postProcess(BiConsumer)
	 * @see com.foreach.across.modules.entity.views.processors.EntityViewProcessorAdapter
	 * @see com.foreach.across.modules.entity.views.processors.SimpleEntityViewProcessorAdapter
	 */
	public EntityViewFactoryBuilder viewProcessor( String processorName, EntityViewProcessor processor ) {
		Assert.notNull( processorName );
		Assert.notNull( processor );
		processors.add( new ProcessorEntry( processorName, processor ) );
		return this;
	}

	/**
	 * Add a processor object that should be applied to the view factory.
	 * Register the processor under the given name and give it a specific order.
	 *
	 * @param processorName unique processor name
	 * @param processor     instance - should not be null
	 * @param order         of the processor
	 * @return current builder
	 * @see #postProcess(BiConsumer)
	 * @see com.foreach.across.modules.entity.views.processors.EntityViewProcessorAdapter
	 * @see com.foreach.across.modules.entity.views.processors.SimpleEntityViewProcessorAdapter
	 */
	public EntityViewFactoryBuilder viewProcessor( String processorName, EntityViewProcessor processor, int order ) {
		Assert.notNull( processorName );
		Assert.notNull( processor );
		ProcessorEntry entry = new ProcessorEntry( processorName, processor );
		entry.setOrder( order );
		processors.add( entry );
		return this;
	}

	/**
	 * Configure one or more message prefixes to use when rendering this view.
	 *
	 * @param messagePrefixes prefixes
	 * @return current builder
	 * @see com.foreach.across.modules.entity.views.processors.MessagePrefixingViewProcessor
	 */
	public EntityViewFactoryBuilder messagePrefix( String... messagePrefixes ) {
		this.messagePrefixes = messagePrefixes;
		return this;
	}

	/**
	 * Configure the {@link AllowableAction} that the authenticated principal should have to access this view.
	 *
	 * @param action that is required
	 * @return current builder
	 * @see com.foreach.across.modules.entity.views.processors.ActionAllowedAuthorizationViewProcessor
	 */
	public EntityViewFactoryBuilder requiredAllowableAction( AllowableAction action ) {
		this.requiredAllowableAction = action;
		return this;
	}

	/**
	 * Removes a processor with the given name.
	 *
	 * @param processorName unique name of the processor
	 * @return current builder
	 */
	public EntityViewFactoryBuilder removeViewProcessor( String processorName ) {
		Assert.notNull( processorName );
		processors.add( new ProcessorEntry( processorName, null ) );
		return this;
	}

	/**
	 * Add a post processor for a single {@link EntityViewProcessor} with the default name determined by its implementing class.
	 * Shortcut for easy post-processing of the default {@link EntityViewProcessor}s.
	 *
	 * @param viewProcessorType type of the view processor (will determine its name)
	 * @param postProcessor     post processor
	 * @param <U>               type of the view processor
	 * @return current builder
	 */
	public <U extends EntityViewProcessor> EntityViewFactoryBuilder postProcess( Class<U> viewProcessorType, Consumer<U> postProcessor ) {
		Assert.notNull( viewProcessorType );
		Assert.notNull( postProcessor );
		return postProcess( viewProcessorName( viewProcessorType ), viewProcessorType, postProcessor );
	}

	/**
	 * Add a post processor for a single {@link EntityViewProcessor}.
	 * Shortcut for easy post-processing of the default {@link EntityViewProcessor}s.
	 *
	 * @param viewProcessorName name of the view processor
	 * @param viewProcessorType expected type of the view processor
	 * @param postProcessor     post processor
	 * @param <U>               type of the view processor
	 * @return current builder
	 */
	public <U extends EntityViewProcessor> EntityViewFactoryBuilder postProcess( String viewProcessorName,
	                                                                             Class<U> viewProcessorType,
	                                                                             Consumer<U> postProcessor ) {
		Assert.notNull( viewProcessorName );
		Assert.notNull( viewProcessorType );
		Assert.notNull( postProcessor );
		postProcessors.add( ( factory, registry ) -> {
			if ( registry != null ) {
				registry.getProcessor( viewProcessorName, viewProcessorType )
				        .ifPresent( postProcessor );
			}
		} );
		return this;
	}

	/**
	 * Add a post processor for this entire view factory.  If the view factory is not of type {@link DispatchingEntityViewFactory},
	 * the second parameter ({@link EntityViewProcessorRegistry} will be {@code null}.
	 * <p/>
	 * Use this method if you want to make global modifications like changing the {@link EntityViewProcessor} ordering.
	 * <p/>
	 * The post processors will be called after general building.
	 *
	 * @param postProcessor post processor
	 * @return current builder
	 */
	public EntityViewFactoryBuilder postProcess( BiConsumer<EntityViewFactory, EntityViewProcessorRegistry> postProcessor ) {
		Assert.notNull( postProcessor );
		postProcessors.add( postProcessor );
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
		if ( viewFactory instanceof DispatchingEntityViewFactory ) {
			buildViewProcessors( ( (DispatchingEntityViewFactory) viewFactory ).getProcessorRegistry() );
		}
		else {
			LOG.debug( "Custom view factory is not a DispatchingEntityViewFactory - the properties of this builder will be skipped" );
		}

		postProcess( viewFactory );
	}

	/**
	 * Post process the {@link EntityViewFactory}.
	 *
	 * @param viewFactory to post process
	 */
	private void postProcess( EntityViewFactory viewFactory ) {
		EntityViewProcessorRegistry processorRegistry =
				viewFactory instanceof DispatchingEntityViewFactory ? ( (DispatchingEntityViewFactory) viewFactory ).getProcessorRegistry() : null;
		postProcessors.forEach( pp -> pp.accept( viewFactory, processorRegistry ) );
	}

	private void buildViewProcessors( EntityViewProcessorRegistry processorRegistry ) {
		processors.forEach( entry -> {
			if ( entry.isRemove() ) {
				processorRegistry.remove( entry.name );
			}
			else {
				processorRegistry.addProcessor( entry.name, entry.processor, entry.order );
			}
		} );

		configureTemplateProcessor( processorRegistry );
		configureMessagePrefixingProcessor( processorRegistry );
		configureAuthorizationProcessor( processorRegistry );
		configurePropertyRegistryProcessor( processorRegistry );
		configureRenderingProcessors( processorRegistry, propertiesToShow, viewElementMode );
	}

	protected void configureRenderingProcessors( EntityViewProcessorRegistry processorRegistry,
	                                             EntityPropertySelector propertiesToShow,
	                                             ViewElementMode viewElementMode ) {
		if ( propertiesToShow != null || viewElementMode != null ) {
			PropertyRenderingViewProcessor renderingViewProcessor
					= processorRegistry.getProcessor( PropertyRenderingViewProcessor.class.getName(), PropertyRenderingViewProcessor.class )
					                   .orElseGet( () -> {
						                   PropertyRenderingViewProcessor propertyRenderingViewProcessor = beanFactory.createBean(
								                   PropertyRenderingViewProcessor.class );
						                   processorRegistry.addProcessor( propertyRenderingViewProcessor );
						                   return propertyRenderingViewProcessor;
					                   } );

			if ( propertiesToShow != null ) {
				renderingViewProcessor.setSelector( propertiesToShow );
			}

			if ( viewElementMode != null ) {
				renderingViewProcessor.setViewElementMode( viewElementMode );
			}
		}
	}

	private void configurePropertyRegistryProcessor( EntityViewProcessorRegistry processorRegistry ) {
		if ( propertyRegistry != null ) {
			processorRegistry.remove( EntityPropertyRegistryViewProcessor.class.getName() );
			processorRegistry.addProcessor( new EntityPropertyRegistryViewProcessor( propertyRegistry ) );
		}

		if ( !registryConsumers.isEmpty() ) {
			EntityPropertyRegistry registryToConfigure
					= processorRegistry.getProcessor( EntityPropertyRegistryViewProcessor.class.getName(), EntityPropertyRegistryViewProcessor.class )
					                   .orElseThrow( () -> new IllegalStateException(
							                   "Attempting to modify properties but no default EntityPropertyRegistryViewProcessor named: " + EntityPropertyRegistryViewProcessor.class
									                   .getName() )
					                   )
					                   .getPropertyRegistry();

			if ( registryToConfigure instanceof MutableEntityPropertyRegistry ) {
				EntityPropertyRegistryBuilder registryBuilder = new EntityPropertyRegistryBuilder();
				registryConsumers.forEach( c -> c.accept( registryBuilder ) );
				registryBuilder.apply( (MutableEntityPropertyRegistry) registryToConfigure );
			}
			else {
				throw new IllegalStateException(
						"Attempting to modify properties but not a MutableEntityPropertyRegistry" );
			}
		}
	}

	private void configureAuthorizationProcessor( EntityViewProcessorRegistry processorRegistry ) {
		if ( requiredAllowableAction != null ) {
			processorRegistry.getProcessor( ActionAllowedAuthorizationViewProcessor.class.getName(), ActionAllowedAuthorizationViewProcessor.class )
			                 .orElseGet( () -> {
				                 ActionAllowedAuthorizationViewProcessor authorizationViewProcessor = new ActionAllowedAuthorizationViewProcessor();
				                 processorRegistry.addProcessor( authorizationViewProcessor );
				                 return authorizationViewProcessor;
			                 } )
			                 .setRequiredAllowableAction( requiredAllowableAction );
		}
	}

	private void configureMessagePrefixingProcessor( EntityViewProcessorRegistry processorRegistry ) {
		if ( messagePrefixes != null ) {
			processorRegistry.remove( MessagePrefixingViewProcessor.class.getName() );
			processorRegistry.addProcessor( new MessagePrefixingViewProcessor( messagePrefixes ) );
		}
	}

	private void configureTemplateProcessor( EntityViewProcessorRegistry processorRegistry ) {
		if ( template != null ) {
			processorRegistry.remove( TemplateViewProcessor.class.getName() );
			processorRegistry.addProcessor( new TemplateViewProcessor( template ) );
		}
	}

	@RequiredArgsConstructor
	private static class ProcessorEntry
	{
		private final String name;
		private final EntityViewProcessor processor;

		@Setter
		private int order = EntityViewProcessorRegistry.DEFAULT_ORDER;

		public boolean isRemove() {
			return processor == null;
		}
	}
}
