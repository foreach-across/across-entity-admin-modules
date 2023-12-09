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

import com.foreach.across.modules.entity.views.EntityViewProcessor;
import com.foreach.across.modules.entity.views.processors.support.EntityViewProcessorRegistry;
import com.foreach.across.modules.entity.views.processors.support.EntityViewProcessorRegistry.EntityViewProcessorRegistration;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents a single {@link com.foreach.across.modules.entity.views.EntityViewProcessor} registration record.
 *
 * @author Arne Vandamme
 * @since 4.0.0
 */
public class EntityViewProcessorConfigurer<T extends EntityViewProcessor>
{
	private final List<Consumer<? extends T>> configurers = new ArrayList<>();
	private AutowireCapableBeanFactory beanFactory;

	private Class<? extends EntityViewProcessor> processorType = EntityViewProcessor.class;
	private String processorName;
	private Function<AutowireCapableBeanFactory, ?> provider;
	private Integer order;
	private boolean remove;
	private boolean skipIfPresent;
	private boolean updateIfPresent;
	private boolean replaceIfPresent;
	private boolean skipIfDifferentType;
	private boolean skipIfMissing;

	EntityViewProcessorConfigurer( AutowireCapableBeanFactory beanFactory ) {
		beanFactory( beanFactory );
	}

	/**
	 * Should this configurer be applied as a postprocessor, after all
	 * the initial registration of processor has been done.
	 * <p/>
	 * Default is {@code false}, but set to {@code true} if you want to
	 * modify/remove view processors that have been previously added.
	 */
	@Getter(value = AccessLevel.PACKAGE)
	private boolean deferred;

	/**
	 * Should this configurer be applied as a postprocessor, after all
	 * the initial registration of processor has been done.
	 * <p/>
	 * Default is {@code false}, but set to {@code true} if you want to
	 * modify/remove view processors that have been previously added.
	 *
	 * @return current configurer
	 */
	public EntityViewProcessorConfigurer<T> deferred() {
		this.deferred = true;
		return this;
	}

	/**
	 * Specify the bean factory that should be used for all calls of {@code getBean}, {@code createBean}
	 * and {@code provider}. Usually an instance is pre-configured with an appropriate bean factory but
	 * developers can override the one to use.
	 *
	 * @param beanFactory to use
	 * @return current configurer
	 */
	public EntityViewProcessorConfigurer<T> beanFactory( @NonNull AutowireCapableBeanFactory beanFactory ) {
		this.beanFactory = beanFactory;
		return this;
	}

	/**
	 * Simply skip this configurer when attempting to register a new processor which is already present.
	 * By default an exception would be thrown (unless {@link #updateIfPresent()} is set).
	 * <p/>
	 * Mutually exclusive with {@link #replaceIfPresent()} and {@link #updateIfPresent()}.
	 *
	 * @return current configurer
	 */
	public EntityViewProcessorConfigurer<T> skipIfPresent() {
		this.skipIfPresent = true;
		this.updateIfPresent = false;
		this.replaceIfPresent = false;
		return this;
	}

	/**
	 * Update the existing processor when attempting to register a new processor which is already present.
	 * This will only apply {@code order} and {@code configure} rules, but will throw an exception if
	 * the existing processor is of a different type an {@link #skipIfDifferentType()} is not set.
	 * <p/>
	 * Mutually exclusive with {@link #replaceIfPresent()} and {@link #skipIfPresent()}.
	 *
	 * @return current configurer
	 */
	public EntityViewProcessorConfigurer<T> updateIfPresent() {
		this.skipIfPresent = false;
		this.updateIfPresent = true;
		this.replaceIfPresent = false;
		return this;
	}

	/**
	 * Replace any existing processor when attempting to register a new processor which is already present.
	 * This will do an in-place replacement, keeping the previously set {@code order} unless it is specified
	 * on this configurer. A combination of {@link #remove()} and adding a new processor would always add
	 * the processor with the default order.
	 * <p/>
	 * Mutually exclusive with {@link #updateIfPresent()} and {@link #skipIfPresent()}.
	 *
	 * @return current configurer
	 */
	public EntityViewProcessorConfigurer<T> replaceIfPresent() {
		this.skipIfPresent = false;
		this.updateIfPresent = false;
		this.replaceIfPresent = true;
		return this;
	}

	/**
	 * Skip this configurer when it would attempt to update a processor which is not of the expected type.
	 * If not set, an exception would be thrown instead.
	 *
	 * @return current configurer
	 */
	public EntityViewProcessorConfigurer<T> skipIfDifferentType() {
		this.skipIfDifferentType = true;
		return this;
	}

	/**
	 * Skip this configurer when it would attempt to update a processor which is not present.
	 * If not set, an exception would be thrown instead.
	 *
	 * @return current configurer
	 */
	public EntityViewProcessorConfigurer<T> skipIfMissing() {
		this.skipIfMissing = true;
		return this;
	}

	/**
	 * Configure the view processor with the specified type.
	 * This will determine the auto-generated name (matching the fully qualified class name).
	 * <p/>
	 * <p>
	 * If {@link #withName(String)} has been configured as well, the type will only be used
	 * for typing the configuration consumers.
	 *
	 * @param viewProcessorType type of the processor
	 * @param <Y>               class parameter
	 * @return current configurer - typed to the processor type
	 */
	@SuppressWarnings("unchecked")
	public <Y extends EntityViewProcessor> EntityViewProcessorConfigurer<Y> withType( @NonNull Class<Y> viewProcessorType ) {
		if ( processorName == null ) {
			processorName = ClassUtils.getUserClass( viewProcessorType ).getName();
		}
		processorType = viewProcessorType;
		return (EntityViewProcessorConfigurer<Y>) this;
	}

	/**
	 * Configure the name of this view processor. Name must be unique within the entire view processor registry.
	 * <p/>
	 * If not set the name will be resolved from one of the type related parameters.
	 *
	 * @param viewProcessorName name of the view processor
	 * @return current configurer
	 */
	public EntityViewProcessorConfigurer<T> withName( @NonNull String viewProcessorName ) {
		processorName = viewProcessorName;
		return this;
	}

	/**
	 * Register a new processor which is an instance of the bean with the given name.
	 * The {@link AutowireCapableBeanFactory} must have a bean definition under that name.
	 * If {@link #withName(String)} has not been called, the beanName will be used as the
	 * processor name.
	 *
	 * @param beanName name of the bean definition that represents the processor
	 * @return current configurer
	 */
	public EntityViewProcessorConfigurer<T> getBean( @NonNull String beanName ) {
		provider = beanFactory -> beanFactory.getBean( beanName );
		if ( processorName == null ) {
			processorName = beanName;
		}
		return this;
	}

	/**
	 * Register a new processor which is an instance of the bean with the given type.
	 * Uses {@link AutowireCapableBeanFactory#getBean(Class)} to ge the processor, a bean
	 * definition for that type must be known and depending on that bean definition a
	 * singleton or otherwise scoped instance might be returned.
	 * <p/>
	 * See {@link #createBean(Class)} if you always want to create a new instance of
	 * a type which is possibly not registered as a bean
	 *
	 * @param viewProcessorType type of the processor
	 * @param <Y>               class parameter
	 * @return current configurer - typed to the processor type
	 */
	public <Y extends EntityViewProcessor> EntityViewProcessorConfigurer<Y> getBean( @NonNull Class<Y> viewProcessorType ) {
		provider = beanFactory -> beanFactory.getBean( viewProcessorType );
		return withType( viewProcessorType );
	}

	/**
	 * Register a new processor which is an instance of the bean with the given type.
	 *
	 * @param viewProcessorType type of the processor
	 * @param <Y>               class parameter
	 * @return current configurer - typed to the processor type
	 */
	public <Y extends EntityViewProcessor> EntityViewProcessorConfigurer<Y> createBean( @NonNull Class<Y> viewProcessorType ) {
		provider = beanFactory -> beanFactory.createBean( viewProcessorType );
		return withType( viewProcessorType );
	}

	/**
	 * Register the given processor.
	 *
	 * @param viewProcessor processor to register
	 * @param <Y>           type
	 * @return current configurer - typed to the processor instance
	 */
	@SuppressWarnings("unchecked")
	public <Y extends EntityViewProcessor> EntityViewProcessorConfigurer<Y> provideBean( @NonNull Y viewProcessor ) {
		provider = beanFactory -> viewProcessor;
		return withType( (Class<Y>) viewProcessor.getClass() );
	}

	/**
	 * Register a new processor which will be provided by applying the function on the {@link AutowireCapableBeanFactory}.
	 *
	 * @param provider that supplies the processor
	 * @return current configurer
	 */
	public EntityViewProcessorConfigurer<T> provider( @NonNull Function<AutowireCapableBeanFactory, ? extends T> provider ) {
		this.provider = provider;
		return this;
	}

	/**
	 * Assign the following order to the processor.
	 *
	 * @param order to assign
	 * @return current configurer
	 */
	public EntityViewProcessorConfigurer<T> order( int order ) {
		this.order = order;
		return this;
	}

	/**
	 * Add a configuration consumer to apply to the actual processor entry.
	 *
	 * @param consumer to apply to the processor
	 * @return current configurer
	 */
	public EntityViewProcessorConfigurer<T> configure( @NonNull Consumer<? extends T> consumer ) {
		configurers.add( consumer );
		return this;
	}

	/**
	 * Remove the processor.
	 * If the defined processor is not present, nothing will happen.
	 * If a creation is also configured, any existing processor will be removed first, and then added with the new configuration.
	 *
	 * @return current configurer
	 */
	public EntityViewProcessorConfigurer<T> remove() {
		this.remove = true;
		return this;
	}

	@SuppressWarnings("unchecked")
	void applyTo( @NonNull EntityViewProcessorRegistry processorRegistry ) {
		Optional<EntityViewProcessorRegistration> registered = processorRegistry.getProcessorRegistration( processorName );

		if ( remove && registered.isPresent() ) {
			registered.get().remove();
			registered = Optional.empty();
		}

		boolean addNewProcessor = provider != null;

		if ( addNewProcessor && registered.isPresent() ) {
			if ( skipIfPresent ) {
				return;
			}

			if ( replaceIfPresent ) {
				registered.get().setProcessor( (T) provider.apply( beanFactory ) );
			}
			else if ( !updateIfPresent ) {
				throw new IllegalStateException( "Unable to register EntityViewProcessor '" + processorName + "': it is already present" );
			}
		}

		if ( provider != null && !registered.isPresent() ) {
			T processor = (T) provider.apply( beanFactory );
			processorRegistry.addProcessor( processorName, processor );

			registered = processorRegistry.getProcessorRegistration( processorName );
		}

		if ( order != null || !configurers.isEmpty() ) {
			if ( !registered.isPresent() ) {
				if ( skipIfMissing ) {
					return;
				}

				throw new IllegalStateException( "Unable to update EntityViewProcessor '" + processorName + "': expected it to be present but was not" );
			}

			EntityViewProcessor existing = registered.get().getProcessor();

			if ( processorType != null && !processorType.isInstance( existing ) ) {
				if ( skipIfDifferentType ) {
					return;
				}

				throw new IllegalStateException(
						"Unable to update EntityViewProcessor '" + processorName + "': expected type " + processorType + " but is " + existing.getClass() );
			}

			if ( order != null ) {
				registered.get().setOrder( order );
			}

			for ( Consumer configurer : configurers ) {
				configurer.accept( existing );
			}
		}
	}
}
