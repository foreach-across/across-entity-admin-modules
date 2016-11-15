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

import com.foreach.across.modules.entity.actions.EntityConfigurationAllowableActionsBuilder;
import com.foreach.across.modules.entity.config.builders.configuration.FormViewBuilder;
import com.foreach.across.modules.entity.config.builders.configuration.ListViewBuilder;
import com.foreach.across.modules.entity.config.builders.configuration.ViewBuilder;
import com.foreach.across.modules.entity.registry.*;
import com.foreach.across.modules.entity.views.EntityFormView;
import com.foreach.across.modules.entity.views.EntityListView;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Arne Vandamme
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EntityConfigurationBuilder<T> extends AbstractAttributesAndViewsBuilder<EntityConfigurationBuilder, MutableEntityConfiguration<T>>
{
	private static final Logger LOG = LoggerFactory.getLogger( EntityConfigurationBuilder.class );

	private final BeanFactory beanFactory;

	private final Map<String, EntityAssociationBuilder> associations = new HashMap<>();

	private String name;
	private String displayName;
	private Class<T> entityType;
	private boolean registerForClass = false;
	private EntityConfigurationAllowableActionsBuilder allowableActionsBuilder;
	private Boolean hidden;

	private EntityModel<T, Serializable> entityModel;

	private final Collection<Consumer<EntityPropertyRegistryBuilder>> registryConsumers = new ArrayDeque<>();
	private final Collection<Consumer<EntityModelBuilder<T>>> modelConsumers = new ArrayDeque<>();
	private final Collection<Consumer<MutableEntityConfiguration<T>>> postProcessors = new ArrayDeque<>();

	private String labelProperty;

	@Autowired
	public EntityConfigurationBuilder( BeanFactory beanFactory ) {
		this.beanFactory = beanFactory;
	}

	/**
	 * Set the internal name of the entity configuration. Must be unique within a registry.
	 * Can only be used along with {@link #build()}, cannot be modified on an existing configuration.
	 *
	 * @param name of the configuration
	 * @return current builder
	 */
	public EntityConfigurationBuilder<T> name( String name ) {
		this.name = name;
		return this;
	}

	/**
	 * Set the default display name of the entity configuration in UI implementations.
	 *
	 * @param displayName of the configuration
	 * @return current builder
	 */
	public EntityConfigurationBuilder<T> displayName( String displayName ) {
		this.displayName = displayName;
		return this;
	}

	/**
	 * Set the entity type of the entity configuration.  Can only be used along with
	 * {@link #build()}.  Cannot be modified on an existing configuration.
	 * <p/>
	 * The registerForClass parameter determines if the property registry of the class should
	 * be the main registry in the {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistryProvider}.
	 * If that is the case, nested properties on this type will be resolved against this configuration.
	 * If you're expecting to have only one entity configuration using this entity type, then you most
	 * likely want to register this configuration for that type it.
	 *
	 * @param entityType       for the configuration
	 * @param registerForClass true if the property registry should be the main registry for that class
	 * @return current builder
	 */
	public EntityConfigurationBuilder<T> entityType( Class<T> entityType, boolean registerForClass ) {
		this.entityType = entityType;
		this.registerForClass = registerForClass;
		return this;
	}

	/**
	 * Customize the property registry builder.
	 *
	 * @param registryConsumer to customize the registry builder
	 * @return current builder
	 */
	public EntityConfigurationBuilder<T> properties( Consumer<EntityPropertyRegistryBuilder> registryConsumer ) {
		Assert.notNull( registryConsumer );
		registryConsumers.add( registryConsumer );
		return this;
	}

	/**
	 * Ensures the configuration <strong>will not</strong> be labeled as hidden for UI implementations.
	 *
	 * @return current builder
	 */
	public EntityConfigurationBuilder<T> show() {
		return hidden( false );
	}

	/**
	 * Ensures the configuration <strong>will be</strong> labeled as hidden for UI implementations.
	 *
	 * @return current builder
	 */
	public EntityConfigurationBuilder<T> hide() {
		return hidden( true );
	}

	/**
	 * Should the {@link com.foreach.across.modules.entity.registry.EntityConfiguration} be hidden from UI
	 * implementations. This property can be considered a hint for automatically generated user interfaces.
	 *
	 * @param hidden True if the configuration should be hidden from UI.
	 * @return current builder
	 */
	public EntityConfigurationBuilder<T> hidden( boolean hidden ) {
		this.hidden = hidden;
		return this;
	}

	/**
	 * Configure the {@link com.foreach.across.modules.entity.actions.EntityConfigurationAllowableActionsBuilder} to be used.
	 *
	 * @param allowableActionsBuilder instance
	 * @return current builder
	 */
	public EntityConfigurationBuilder<T> allowableActionsBuilder( EntityConfigurationAllowableActionsBuilder allowableActionsBuilder ) {
		Assert.notNull( allowableActionsBuilder );
		this.allowableActionsBuilder = allowableActionsBuilder;
		return this;
	}

	/**
	 * Configure the label property based on another registered property.
	 * This is a shortcut for configuring a properties builder setting the label.
	 *
	 * @param propertyName of the registered property to use as a base of the label
	 * @return current builder
	 */
	public EntityConfigurationBuilder<T> label( String propertyName ) {
		labelProperty = propertyName;
		return this;
	}

	/**
	 * Sets a custom {@link EntityModel} for this configuration.
	 *
	 * @param entityModel implementation
	 * @return current builder
	 */
	public EntityConfigurationBuilder<T> entityModel( EntityModel<T, Serializable> entityModel ) {
		this.entityModel = entityModel;
		return this;
	}

	/**
	 * Add a consumer to customize the {@link EntityModel}.
	 *
	 * @return current builder
	 */
	public EntityConfigurationBuilder<T> entityModel( Consumer<EntityModelBuilder<T>> entityModelConsumer ) {
		modelConsumers.add( entityModelConsumer );
		return this;
	}

	/**
	 * Add a post processor instance.  The post processor will only be executed after the rest of the builder
	 * has been applied.  It can be used to modify the configuration directly.
	 *
	 * @param postProcessor instance
	 * @return current builder
	 */
	public EntityConfigurationBuilder<T> postProcessor( Consumer<MutableEntityConfiguration<T>> postProcessor ) {
		Assert.notNull( postProcessor );
		postProcessors.add( postProcessor );
		return this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public EntityConfigurationBuilder<T> attribute( String name, Object value ) {
		return (EntityConfigurationBuilder<T>) super.attribute( name, value );
	}

	@Override
	@SuppressWarnings("unchecked")
	public <S> EntityConfigurationBuilder<T> attribute( Class<S> type, S value ) {
		return (EntityConfigurationBuilder<T>) super.attribute( type, value );
	}

	@Override
	public ViewBuilder view( String name ) {
		return view( name, ViewBuilder.class );
	}

	@Override
	public ListViewBuilder listView() {
		return listView( EntityListView.VIEW_NAME );
	}

	@Override
	public ListViewBuilder listView( String name ) {
		return view( name, ListViewBuilder.class );
	}

	@Override
	public FormViewBuilder createFormView() {
		return formView( EntityFormView.CREATE_VIEW_NAME );
	}

	@Override
	public FormViewBuilder updateFormView() {
		return formView( EntityFormView.UPDATE_VIEW_NAME );
	}

	@Override
	public FormViewBuilder formView( String name ) {
		return view( name, FormViewBuilder.class );
	}

	public EntityAssociationBuilder association( String name ) {
		EntityAssociationBuilder builder = associations.get( name );

		if ( builder == null ) {
			builder = new EntityAssociationBuilder( name );
			associations.put( name, builder );
		}

		return builder;
	}

	/**
	 * Build a new {@link EntityConfigurationImpl} with the settings specified.
	 * Allows setting both name and entity type. Will apply the post processors after initial creation.
	 *
	 * @return built configuration
	 */
	@SuppressWarnings("unchecked")
	public MutableEntityConfiguration<T> build() {
		return build( true );
	}

	MutableEntityConfiguration<T> build( boolean applyPostProcessors ) {
		Assert.notNull( entityType );

		EntityConfigurationProvider configurationProvider = beanFactory.getBean( EntityConfigurationProvider.class );
		String defaultName = name != null ? name : StringUtils.uncapitalize( entityType.getSimpleName() );

		MutableEntityConfiguration<T> configuration
				= configurationProvider.create( defaultName, entityType, registerForClass );

		apply( configuration, applyPostProcessors );

		return configuration;
	}

	/**
	 * Apply the builder to the given entity configuration.
	 *
	 * @param configuration to apply the builder to
	 */
	public void apply( MutableEntityConfiguration<T> configuration ) {
		apply( configuration, true );
	}

	void apply( MutableEntityConfiguration<T> configuration, boolean applyPostProcessors ) {
		Assert.notNull( configuration );

		if ( displayName != null ) {
			configuration.setDisplayName( displayName );
		}

		// Configure the entity model
		buildEntityModel( configuration );

		// Build the properties
		EntityPropertyRegistryBuilder registryBuilder = new EntityPropertyRegistryBuilder();
		if ( labelProperty != null ) {
			registryBuilder.label( labelProperty );
		}
		registryConsumers.forEach( c -> c.accept( registryBuilder ) );
		registryBuilder.apply( configuration.getPropertyRegistry() );

		if ( hidden != null ) {
			configuration.setHidden( hidden );
		}

		if ( allowableActionsBuilder != null ) {
			configuration.setAllowableActionsBuilder( allowableActionsBuilder );
		}

		applyAttributes( configuration );

		if ( applyPostProcessors ) {
			postProcess( configuration );
		}
	}

	void postProcess( MutableEntityConfiguration<T> configuration ) {
		postProcessors.forEach( c -> c.accept( configuration ) );
	}


	/*
	void apply( MutableEntityRegistry entityRegistry, AutowireCapableBeanFactory beanFactory ) {
		for ( Class<T> entityType : entityTypesToHandle( entityRegistry ) ) {
			MutableEntityConfiguration<T> configuration
					= entityRegistry.getMutableEntityConfiguration( entityType );

			if ( configuration == null ) {
				configuration = new EntityConfigurationImpl<>( entityType );
				configuration.setPropertyRegistry(
						beanFactory.getBean( EntityPropertyRegistryProvider.class ).get( entityType )
				);
				entityRegistry.register( configuration );
			}


			applyViewBuilders( configuration, beanFactory );

			for ( EntityAssociationBuilder associationBuilder : associations.values() ) {
				associationBuilder.apply( configuration, entityRegistry, beanFactory );
			}
		}
	}
	*/

	private void buildEntityModel( MutableEntityConfiguration<T> configuration ) {
		if ( entityModel != null ) {
			configuration.setEntityModel( entityModel );
		}

		if ( !modelConsumers.isEmpty() ) {
			EntityModel<T, Serializable> actualModel = configuration.getEntityModel();

			EntityModelBuilder<T> modelBuilder = new EntityModelBuilder<>();
			modelConsumers.forEach( c -> c.accept( modelBuilder ) );

			if ( actualModel == null ) {
				configuration.setEntityModel( modelBuilder.build() );
			}
			else if ( actualModel instanceof DefaultEntityModel ) {
				modelBuilder.apply( (DefaultEntityModel<T, Serializable>) actualModel );
			}
			else if ( !modelConsumers.isEmpty() ) {
				LOG.error(
						"Unable to apply EntityModelBuilder - one or more consumers were registered, but a custom " +
								"EntityModel implementation not extending DefaultEntityModel was configured." +
								"The custom implementation takes precedence and the builder will be ignored!" );
			}
		}
	}

	/*
	void postProcess( MutableEntityRegistry entityRegistry ) {
		for ( Class<T> entityType : entityTypesToHandle( entityRegistry ) ) {
			MutableEntityConfiguration configuration
					= entityRegistry.getMutableEntityConfiguration( entityType );

			for ( Consumer<MutableEntityConfiguration<T>> postProcessor : postProcessors() ) {
				postProcessor.accept( configuration );
			}

			for ( EntityAssociationBuilder associationBuilder : associations.values() ) {
				associationBuilder.postProcess( configuration );
			}
		}
	}

	private Collection<Class<T>> entityTypesToHandle( EntityRegistry entityRegistry ) {
		if ( assignableTo ) {
			List<Class<T>> entityTypes = new ArrayList<>();
			for ( EntityConfiguration entityConfiguration : entityRegistry.getEntities() ) {
				if ( entityType.isAssignableFrom( entityConfiguration.getEntityType() ) ) {
					entityTypes.add( entityConfiguration.getEntityType() );
				}
			}
			return entityTypes;
		}

		return Collections.singleton( entityType );
	}

*/
}
