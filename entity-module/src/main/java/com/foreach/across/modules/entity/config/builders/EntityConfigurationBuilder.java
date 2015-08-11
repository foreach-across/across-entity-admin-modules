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
import com.foreach.across.modules.entity.config.PostProcessor;
import com.foreach.across.modules.entity.config.builders.configuration.FormViewBuilder;
import com.foreach.across.modules.entity.config.builders.configuration.ListViewBuilder;
import com.foreach.across.modules.entity.config.builders.configuration.ViewBuilder;
import com.foreach.across.modules.entity.registry.*;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistryFactory;
import com.foreach.across.modules.entity.views.EntityFormView;
import com.foreach.across.modules.entity.views.EntityListView;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.util.Assert;

import java.util.*;

/**
 * @author Arne Vandamme
 */
public class EntityConfigurationBuilder<T> extends AbstractAttributesAndViewsBuilder<EntityConfigurationBuilder, MutableEntityConfiguration<T>>
{
	private final EntityConfigurationBuilder configurationBuilder;
	private final Class<T> entityType;
	private final boolean assignableTo;
	private final EntitiesConfigurationBuilder parent;
	private final Map<String, EntityAssociationBuilder> associations = new HashMap<>();

	private PropertyRegistryBuilder propertyRegistryBuilder;
	private EntityConfigurationAllowableActionsBuilder allowableActionsBuilder;
	private Boolean hidden;

	EntityConfigurationBuilder( Class<T> entityType, boolean assignableTo, EntitiesConfigurationBuilder parent ) {
		this.entityType = entityType;
		this.assignableTo = assignableTo;
		this.parent = parent;
		this.configurationBuilder = this;
	}

	public PropertyRegistryBuilder properties() {
		if ( propertyRegistryBuilder == null ) {
			propertyRegistryBuilder = new PropertyRegistryBuilder();
		}

		return propertyRegistryBuilder;
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
	 * This is a shortcut for {@link #properties()#label(java.lang.String)#and()#and}.
	 *
	 * @param propertyName of the registered property to use as a base of the label
	 * @return current builder
	 */
	public EntityConfigurationBuilder<T> label( String propertyName ) {
		return properties().label( propertyName ).and().and();
	}

	/**
	 * @return the parent builder
	 */
	public EntitiesConfigurationBuilder and() {
		return parent;
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

	public synchronized EntityAssociationBuilder association( String name ) {
		EntityAssociationBuilder builder = associations.get( name );

		if ( builder == null ) {
			builder = new EntityAssociationBuilder( name );
			associations.put( name, builder );
		}

		return builder;
	}

	void apply( MutableEntityRegistry entityRegistry, AutowireCapableBeanFactory beanFactory ) {
		for ( Class<T> entityType : entityTypesToHandle( entityRegistry ) ) {
			MutableEntityConfiguration configuration
					= entityRegistry.getMutableEntityConfiguration( entityType );

			if ( configuration == null ) {
				configuration = new EntityConfigurationImpl<>( entityType );
				configuration.setPropertyRegistry(
						beanFactory.getBean( EntityPropertyRegistryFactory.class ).getOrCreate( entityType )
				);
				entityRegistry.register( configuration );
			}

			if ( propertyRegistryBuilder != null ) {
				propertyRegistryBuilder.apply( configuration.getPropertyRegistry() );
			}

			if ( hidden != null ) {
				configuration.setHidden( hidden );
			}

			if ( allowableActionsBuilder != null ) {
				configuration.setAllowableActionsBuilder( allowableActionsBuilder );
			}

			applyAttributes( configuration );
			applyViewBuilders( configuration, beanFactory );

			for ( EntityAssociationBuilder associationBuilder : associations.values() ) {
				associationBuilder.apply( configuration, entityRegistry, beanFactory );
			}
		}
	}

	void postProcess( MutableEntityRegistry entityRegistry ) {
		for ( Class<T> entityType : entityTypesToHandle( entityRegistry ) ) {
			MutableEntityConfiguration configuration
					= entityRegistry.getMutableEntityConfiguration( entityType );

			for ( PostProcessor<MutableEntityConfiguration<T>> postProcessor : postProcessors() ) {
				postProcessor.process( configuration );
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

	@SuppressWarnings("unchecked")
	public class PropertyRegistryBuilder
			extends AbstractEntityPropertyRegistryBuilder<PropertyRegistryBuilder>
	{
		private final PropertyRegistryBuilder propertyRegistryBuilder;

		public PropertyRegistryBuilder() {
			this.propertyRegistryBuilder = this;
		}

		@Override
		public PropertyDescriptorBuilder label( String property ) {
			return (PropertyDescriptorBuilder) super.label( property );
		}

		@Override
		public PropertyDescriptorBuilder label() {
			return (PropertyDescriptorBuilder) super.label();
		}

		@Override
		public synchronized PropertyDescriptorBuilder property( String name ) {
			return (PropertyDescriptorBuilder) super.property( name );
		}

		@Override
		protected PropertyDescriptorBuilder createDescriptorBuilder( String name ) {
			return new PropertyDescriptorBuilder();
		}

		@Override
		public EntityConfigurationBuilder and() {
			return configurationBuilder;
		}

		public class PropertyDescriptorBuilder extends AbstractEntityPropertyDescriptorBuilder<PropertyDescriptorBuilder>
		{
			@Override
			public PropertyDescriptorBuilder attribute( String name, Object value ) {
				return super.attribute( name, value );
			}

			@Override
			public <S> PropertyDescriptorBuilder attribute( Class<S> type, S value ) {
				return super.attribute( type, value );
			}

			@Override
			public PropertyDescriptorBuilder displayName( String displayName ) {
				return super.displayName( displayName );
			}

			@Override
			public PropertyDescriptorBuilder spelValueFetcher( String expression ) {
				return super.spelValueFetcher( expression );
			}

			@Override
			public PropertyDescriptorBuilder valueFetcher( ValueFetcher valueFetcher ) {
				return super.valueFetcher( valueFetcher );
			}

			@Override
			public PropertyDescriptorBuilder writable( boolean writable ) {
				return super.writable( writable );
			}

			@Override
			public PropertyDescriptorBuilder order( int order ) {
				return super.order( order );
			}

			@Override
			public PropertyDescriptorBuilder readable( boolean readable ) {
				return super.readable( readable );
			}

			@Override
			public PropertyDescriptorBuilder hidden( boolean hidden ) {
				return super.hidden( hidden );
			}

			@Override
			public PropertyDescriptorBuilder viewElementModeCaching( ViewElementMode mode,
			                                                         boolean cacheable ) {
				return super.viewElementModeCaching( mode, cacheable );
			}

			@Override
			public PropertyDescriptorBuilder viewElementType( ViewElementMode mode,
			                                                  String viewElementType ) {
				return super.viewElementType( mode, viewElementType );
			}

			@Override
			public PropertyDescriptorBuilder viewElementBuilder(
					ViewElementMode mode,
					ViewElementBuilder viewElementBuilder ) {
				return super.viewElementBuilder( mode, viewElementBuilder );
			}

			@Override
			public PropertyRegistryBuilder and() {
				return propertyRegistryBuilder;
			}
		}
	}
}
