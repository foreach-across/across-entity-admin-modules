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

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityConfigurationProvider;
import com.foreach.across.modules.entity.registry.MutableEntityConfiguration;
import com.foreach.across.modules.entity.registry.MutableEntityRegistry;
import lombok.NonNull;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Central builder for customizing configuration entries in the
 * {@link com.foreach.across.modules.entity.registry.EntityRegistry}.
 *
 * @author Arne Vandamme
 */
public class EntitiesConfigurationBuilder
{
	private final List<EntityConfigurationBuilder<?>> newConfigurationBuilders = new ArrayList<>();
	private final Map<String, EntityConfigurationBuilder<Object>> nameBuilders = new LinkedHashMap<>();
	private final Map<Class, EntityConfigurationBuilder<Object>> typeBuilders = new LinkedHashMap<>();
	private final Map<Predicate<MutableEntityConfiguration>, EntityConfigurationBuilder<Object>>
			predicateBuilders = new LinkedHashMap<>();

	private EntityConfigurationBuilder<Object> allBuilder;

	private final BeanFactory beanFactory;

	EntitiesConfigurationBuilder( @NonNull AutowireCapableBeanFactory beanFactory ) {
		this.beanFactory = beanFactory;
	}

	/**
	 * Configure a builder for a new {@link EntityConfiguration} for the class specified.
	 * This will automatically introspect the class and create the initial property registry.
	 * Use {@link #create()} if you want a blank entity configuration builder.
	 * <p/>
	 * Note: functionally this is the same as calling {@link #withType(Class)}. This method is
	 * provided for readability to indicate explicit registration of a new type.
	 *
	 * @return configuration builder
	 * @see #create()
	 * @see #withType(Class)
	 */
	public <U> EntityConfigurationBuilder<U> register( @NonNull Class<U> entityType ) {
		return create().entityType( entityType, true );
	}

	/**
	 * Configure a builder for a new {@link EntityConfiguration}.  The {@link EntityConfigurationBuilder} will
	 * only be passed to this consumer and the consumer is expected to manually populate a valid entity configuration.
	 * If you want the default {@link EntityConfigurationProvider} to be called, you should use
	 * {@link #withType(Class)} instead.
	 *
	 * @return configuration builder
	 */
	public EntityConfigurationBuilder<Object> create() {
		EntityConfigurationBuilder<Object> newBuilder = createConfigurationBuilder();
		newConfigurationBuilders.add( newBuilder );
		return newBuilder;
	}

	/**
	 * Configure a builder for an {@link EntityConfiguration} with the specific name.
	 * If non-existing this method will create a new configuration.  Unlike {@link #withType(Class)}
	 * however, the new configuration will be entirely blank and will require all settings to be
	 * done manually.
	 *
	 * @param configurationName for which to configure the builder
	 * @return configuration builder
	 */
	public EntityConfigurationBuilder<Object> withName( String configurationName ) {
		return nameBuilders.computeIfAbsent( configurationName, c -> createConfigurationBuilder() );
	}

	/**
	 * Configure a builder for a specific entity type.
	 * If non-existing, this method will create a default {@link EntityConfiguration} with
	 * settings that could be detected based on the specific exact type of the entity.
	 *
	 * @param entityType for which to configure the builder
	 * @return configuration builder
	 */
	public <U> EntityConfigurationBuilder<U> withType( Class<U> entityType ) {
		return typeBuilders.computeIfAbsent( entityType, c -> createConfigurationBuilder() ).as( entityType );
	}

	/**
	 * Configure a builder to apply to all configurations in the registry.
	 *
	 * @return configuration builder for all entity configurations
	 */
	public EntityConfigurationBuilder<Object> all() {
		if ( allBuilder == null ) {
			allBuilder = createConfigurationBuilder();
		}
		return allBuilder;
	}

	/**
	 * Configure a builder to apply to all entities that can be assigned to the specific parent type or interface.
	 * Same as a call to {@link #matching(Predicate)} with a predicate on entity type.
	 *
	 * @param entityType that entities can be assigned to
	 * @return entity builder instance
	 */
	public <U> EntityConfigurationBuilder<U> assignableTo( @NonNull Class<U> entityType ) {
		return matching( c -> entityType.isAssignableFrom( c.getEntityType() ) ).as( entityType );
	}

	/**
	 * Configure a builder to apply to all configurations matching the predicate.
	 *
	 * @param configurationPredicate predicate the configurations should match
	 * @return current builder
	 */
	public EntityConfigurationBuilder<Object> matching( @NonNull Predicate<MutableEntityConfiguration> configurationPredicate ) {
		return predicateBuilders.computeIfAbsent( configurationPredicate, c -> createConfigurationBuilder() );
	}

	/**
	 * Apply the builder configuration to the EntityRegistry. This will first dispatch to the registered builders
	 * for applying the configuration, and only afterwards iterated over the same builders for executing the
	 * post-processors.
	 *
	 * @param entityRegistry to modify
	 */
	@SuppressWarnings("unchecked")
	void apply( @NonNull MutableEntityRegistry entityRegistry ) {
		List<Pair<EntityConfigurationBuilder, MutableEntityConfiguration>> appliedBuilders = new ArrayList<>();

		// First create manual new entities
		createNewEntityConfigurations( entityRegistry, appliedBuilders );

		// Create new entities by type
		applyTypeSpecificBuilders( true, entityRegistry, appliedBuilders );

		// Create new entities by name
		applyNameSpecificBuilders( true, entityRegistry, appliedBuilders );

		// Apply to all existing entities
		if ( allBuilder != null ) {
			entityRegistry.getEntities().forEach(
					e -> {
						MutableEntityConfiguration cfg = entityRegistry.getEntityConfiguration( e.getName() );
						allBuilder.apply( cfg, false );
						appliedBuilders.add( new ImmutablePair<>( allBuilder, cfg ) );
					}
			);
		}

		// Apply to all existing entities that match the predicate
		applyPredicateBuilders( entityRegistry, appliedBuilders );

		// Apply to all entities with a given type - for new entities this means the same builder will be applied twice
		applyTypeSpecificBuilders( false, entityRegistry, appliedBuilders );

		// Apply to all entities with a given name - for new entities this means the same builder will be applied twice
		applyNameSpecificBuilders( false, entityRegistry, appliedBuilders );

		// Run postprocessors
		appliedBuilders.forEach( p -> p.getKey().postProcess( p.getValue() ) );
	}

	@SuppressWarnings("unchecked")
	private void applyPredicateBuilders(
			MutableEntityRegistry entityRegistry,
			List<Pair<EntityConfigurationBuilder, MutableEntityConfiguration>> appliedBuilders
	) {
		entityRegistry.getEntities().forEach( e -> {
			MutableEntityConfiguration config = entityRegistry.getEntityConfiguration( e.getName() );

			predicateBuilders.forEach( ( predicate, builder ) -> {
				if ( predicate.test( config ) ) {
					builder.apply( config, false );
					appliedBuilders.add( new ImmutablePair<>( builder, config ) );
				}
			} );
		} );
	}

	private void applyNameSpecificBuilders(
			boolean forCreation,
			MutableEntityRegistry entityRegistry,
			List<Pair<EntityConfigurationBuilder, MutableEntityConfiguration>> appliedBuilders
	) {
		nameBuilders.forEach(
				( name, builder ) ->
						applyEntityConfigurationBuilder(
								forCreation,
								entityRegistry,
								appliedBuilders,
								entityRegistry.getEntityConfiguration( name ),
								builder
						)
		);
	}

	private void applyTypeSpecificBuilders(
			boolean forCreation,
			MutableEntityRegistry entityRegistry,
			List<Pair<EntityConfigurationBuilder, MutableEntityConfiguration>> appliedBuilders ) {
		typeBuilders.forEach(
				( type, builder ) -> {
					MutableEntityConfiguration existing = entityRegistry.getEntityConfiguration( type );

					if ( forCreation && existing == null && !builder.hasEntityType() ) {
						// assign type automatically so creation does not fail
						builder.entityType( type, true );
					}

					applyEntityConfigurationBuilder(
							forCreation,
							entityRegistry,
							appliedBuilders,
							existing,
							builder
					);
				}
		);
	}

	@SuppressWarnings("unchecked")
	private void applyEntityConfigurationBuilder(
			boolean forCreation,
			MutableEntityRegistry entityRegistry,
			List<Pair<EntityConfigurationBuilder, MutableEntityConfiguration>> appliedBuilders,
			MutableEntityConfiguration existing,
			EntityConfigurationBuilder<?> configurationBuilder ) {
		MutableEntityConfiguration config = existing;

		if ( forCreation ) {
			if ( config == null ) {
				config = configurationBuilder.build( false );
				entityRegistry.register( config );
			}
		}
		else {
			// should never be null as the creation call should have registered it
			Assert.notNull( config, "existing MutableEntityConfiguration should not be null" );

			// register applied builders only once
			configurationBuilder.apply( config, false );
			appliedBuilders.add( new ImmutablePair<>( configurationBuilder, config ) );
		}
	}

	private void createNewEntityConfigurations(
			MutableEntityRegistry entityRegistry,
			List<Pair<EntityConfigurationBuilder, MutableEntityConfiguration>> appliedBuilders
	) {
		newConfigurationBuilders.forEach(
				c -> {
					MutableEntityConfiguration<?> config = c.build( false );
					entityRegistry.register( config );

					appliedBuilders.add( new ImmutablePair<>( c, config ) );
				}
		);
	}

	@SuppressWarnings("unchecked")
	private EntityConfigurationBuilder<Object> createConfigurationBuilder() {
		return beanFactory.getBean( EntityConfigurationBuilder.class );
	}
}
