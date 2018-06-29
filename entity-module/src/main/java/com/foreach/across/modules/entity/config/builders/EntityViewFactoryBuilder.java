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

import com.foreach.across.core.support.WritableAttributes;
import com.foreach.across.modules.entity.conditionals.ConditionalOnAdminWeb;
import com.foreach.across.modules.entity.config.AttributeRegistrar;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertySelector;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyRegistry;
import com.foreach.across.modules.entity.views.*;
import com.foreach.across.modules.entity.views.processors.*;
import com.foreach.across.modules.entity.views.processors.support.EntityViewProcessorRegistry;
import com.foreach.across.modules.entity.views.processors.support.TransactionalEntityViewProcessorRegistry;
import com.foreach.across.modules.spring.security.actions.AllowableAction;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.foreach.across.modules.entity.config.builders.EntityViewFactoryBuilder.BEAN_NAME;

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
@ConditionalOnAdminWeb
@Slf4j
@Component(BEAN_NAME)
@Primary
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EntityViewFactoryBuilder extends AbstractWritableAttributesBuilder<EntityViewFactory>
{
	static final String BEAN_NAME = "entityViewFactoryBuilder";

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
	 * <p/>
	 * NOTE that the factory will most likely be modified by this builder, so only use
	 * prototype instances.
	 *
	 * @param factory to use
	 * @return current builder
	 */
	public EntityViewFactoryBuilder factory( @NonNull EntityViewFactory factory ) {
		this.factory = factory;
		return this;
	}

	@Override
	public EntityViewFactoryBuilder attribute( String name, Object value ) {
		return (EntityViewFactoryBuilder) super.attribute( name, value );
	}

	@Override
	public <S> EntityViewFactoryBuilder attribute( Class<S> type, S value ) {
		return (EntityViewFactoryBuilder) super.attribute( type, value );
	}

	@Override
	public EntityViewFactoryBuilder attribute( AttributeRegistrar<EntityViewFactory> attributeRegistrar ) {
		return (EntityViewFactoryBuilder) super.attribute( attributeRegistrar );
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
	 * Set the name of the {@link PlatformTransactionManager} bean that should be used by the {@link DispatchingEntityViewFactory}.
	 * If {@code null}, will remove any previously registered transaction manager.
	 * <p/>
	 * This setting will simply be ignored if:
	 * <ul>
	 * <li>the {@link EntityViewFactory} is not a {@link DispatchingEntityViewFactory}</li>
	 * <li>there is no {@link com.foreach.across.modules.entity.views.processors.support.TransactionalEntityViewProcessorRegistry}</li>
	 * <li>there is no {@link PlatformTransactionManager} with that name in the bean factory</li>
	 * </ul>
	 *
	 * @param transactionManagerName bean name
	 * @return current builder
	 * @see com.foreach.across.modules.entity.views.processors.support.TransactionalEntityViewProcessorRegistry
	 */
	public EntityViewFactoryBuilder transactionManager( String transactionManagerName ) {
		postProcess( ( viewFactory, entityViewProcessorRegistry ) -> {
			if ( entityViewProcessorRegistry instanceof TransactionalEntityViewProcessorRegistry ) {
				if ( beanFactory.containsBean( transactionManagerName ) ) {
					PlatformTransactionManager transactionManager = beanFactory.getBean( transactionManagerName, PlatformTransactionManager.class );
					TransactionTemplate transactionTemplate = transactionManager != null ? new TransactionTemplate( transactionManager ) : null;
					( (TransactionalEntityViewProcessorRegistry) entityViewProcessorRegistry ).setTransactionTemplate( transactionTemplate );
				}
				else {
					LOG.warn( "Skipping transaction template registration as there is no bean named {} in the bean factory",
					          transactionManagerName );
				}
			}
			else {
				LOG.warn( "Skipping transaction template registration as {} does not have a TransactionalEntityViewProcessorRegistry", viewFactory );
			}
		} );
		return this;
	}

	/**
	 * Set the {@link PlatformTransactionManager} bean that should be used by the {@link DispatchingEntityViewFactory}.
	 * If {@code null}, will remove any previously registered transaction manager.
	 * If the {@link EntityViewFactory} is not a {@link DispatchingEntityViewFactory} or does not have a
	 * {@link com.foreach.across.modules.entity.views.processors.support.TransactionalEntityViewProcessorRegistry}, this setting will be ignored.
	 *
	 * @param transactionManager to use
	 * @return current builder
	 * @see com.foreach.across.modules.entity.views.processors.support.TransactionalEntityViewProcessorRegistry
	 */
	public EntityViewFactoryBuilder transactionManager( PlatformTransactionManager transactionManager ) {
		postProcess( ( viewFactory, entityViewProcessorRegistry ) -> {
			if ( entityViewProcessorRegistry instanceof TransactionalEntityViewProcessorRegistry ) {
				TransactionTemplate transactionTemplate = transactionManager != null ? new TransactionTemplate( transactionManager ) : null;
				( (TransactionalEntityViewProcessorRegistry) entityViewProcessorRegistry ).setTransactionTemplate( transactionTemplate );
			}
			else {
				LOG.warn( "Skipping transaction template registration as {} does not have a TransactionalEntityViewProcessorRegistry", viewFactory );
			}
		} );
		return this;
	}

	/**
	 * Set the {@link TransactionTemplate} bean that should be used by the {@link DispatchingEntityViewFactory}.
	 * If {@code null}, will remove any previously registered transaction template.
	 * If the {@link EntityViewFactory} is not a {@link DispatchingEntityViewFactory} or does not have a
	 * {@link com.foreach.across.modules.entity.views.processors.support.TransactionalEntityViewProcessorRegistry}, this setting will be ignored.
	 * <p/>
	 * Use this method if you want to deviate from the default transaction settings, else it should be sufficient to simply specify the
	 * {@link PlatformTransactionManager} to use.
	 *
	 * @param transactionTemplate to use
	 * @return current builder
	 * @see com.foreach.across.modules.entity.views.processors.support.TransactionalEntityViewProcessorRegistry
	 * @see #transactionManager(String)
	 * @see #transactionManager(PlatformTransactionManager)
	 */
	public EntityViewFactoryBuilder transactionTemplate( TransactionTemplate transactionTemplate ) {
		postProcess( ( viewFactory, entityViewProcessorRegistry ) -> {
			if ( entityViewProcessorRegistry instanceof TransactionalEntityViewProcessorRegistry ) {
				( (TransactionalEntityViewProcessorRegistry) entityViewProcessorRegistry ).setTransactionTemplate( transactionTemplate );
			}
			else {
				LOG.warn( "Skipping transaction template registration as {} does not have a TransactionalEntityViewProcessorRegistry", viewFactory );
			}
		} );
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
	public EntityViewFactoryBuilder properties( @NonNull Consumer<EntityPropertyRegistryBuilder> registryConsumer ) {
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
	public EntityViewFactoryBuilder viewProcessor( @NonNull EntityViewProcessor processor ) {
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
	public EntityViewFactoryBuilder viewProcessor( @NonNull EntityViewProcessor processor, int order ) {
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
	public EntityViewFactoryBuilder viewProcessor( @NonNull String processorName, @NonNull EntityViewProcessor processor ) {
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
	public EntityViewFactoryBuilder viewProcessor( @NonNull String processorName, @NonNull EntityViewProcessor processor, int order ) {
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
		attribute( AllowableAction.class, action );
		if ( action != null ) {
			attribute( ( viewFactory, attributes ) -> {
				if ( !attributes.hasAttribute( EntityViewFactoryAttributes.ACCESS_VALIDATOR ) ) {
					attributes.setAttribute( EntityViewFactoryAttributes.ACCESS_VALIDATOR, EntityViewFactoryAttributes.defaultAccessValidator() );
				}
			} );
		}
		return this;
	}

	/**
	 * Removes a processor with the given name.
	 *
	 * @param processorName unique name of the processor
	 * @return current builder
	 */
	public EntityViewFactoryBuilder removeViewProcessor( @NonNull String processorName ) {
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
	public <U extends EntityViewProcessor> EntityViewFactoryBuilder postProcess( @NonNull Class<U> viewProcessorType, @NonNull Consumer<U> postProcessor ) {
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
	public <U extends EntityViewProcessor> EntityViewFactoryBuilder postProcess( @NonNull String viewProcessorName,
	                                                                             @NonNull Class<U> viewProcessorType,
	                                                                             @NonNull Consumer<U> postProcessor ) {
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
	 * the second parameter ({@link EntityViewProcessorRegistry}) will be {@code null}.
	 * <p/>
	 * Use this method if you want to make global modifications like changing the {@link EntityViewProcessor} ordering.
	 * <p/>
	 * The post processors will be called after general building.
	 *
	 * @param postProcessor post processor
	 * @return current builder
	 */
	public EntityViewFactoryBuilder postProcess( @NonNull BiConsumer<EntityViewFactory, EntityViewProcessorRegistry> postProcessor ) {
		postProcessors.add( postProcessor );
		return this;
	}

	/**
	 * Apply an additional consumer to this builder.
	 *
	 * @param consumer to apply
	 * @return current builder
	 */
	public EntityViewFactoryBuilder and( @NonNull Consumer<EntityViewFactoryBuilder> consumer ) {
		consumer.accept( this );
		return this;
	}

	/**
	 * Build a new factory instance of the type configured using {@link #factoryType(Class)}.
	 * If no {@link #factoryType(Class)} has been configured, an exception will be thrown.
	 *
	 * @return factory instance
	 */
	public EntityViewFactory build() {
		Assert.notNull( factoryType, "factoryType cannot be null" );

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
		if ( viewFactory instanceof WritableAttributes ) {
			applyAttributes( viewFactory, (WritableAttributes) viewFactory );
		}

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
			processorRegistry.addProcessor( new EntityPropertyRegistryViewProcessor( propertyRegistry ), Ordered.HIGHEST_PRECEDENCE );
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
