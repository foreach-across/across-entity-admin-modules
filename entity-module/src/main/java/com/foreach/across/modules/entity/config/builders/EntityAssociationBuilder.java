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

import com.foreach.across.modules.entity.registry.*;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.function.Consumer;

/**
 * Builder for managing as specific {@link EntityAssociation} on a {@link EntityConfiguration}.
 *
 * @author Arne Vandamme
 * @since 1.1.1
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EntityAssociationBuilder extends AbstractWritableAttributesAndViewsBuilder
{
	private String name;
	private boolean hiddenSpecified;
	private Boolean hidden;
	private EntityAssociation.ParentDeleteMode parentDeleteMode;

	private Class<?> targetEntityType;
	private String targetEntityName, sourceProperty, targetProperty;

	private boolean sourcePropertyRemoved, targetPropertyRemoved;

	private final AutowireCapableBeanFactory beanFactory;

	@Autowired
	public EntityAssociationBuilder( AutowireCapableBeanFactory beanFactory ) {
		super( beanFactory );
		this.beanFactory = beanFactory;
	}

	/**
	 * Specify the name of the association that this builder is responsible for.
	 *
	 * @param name of the association
	 * @return current builder
	 */
	public EntityAssociationBuilder name( String name ) {
		Assert.notNull( name );
		this.name = name;
		return this;
	}

	/**
	 * Set the target entity type for this association.  This type must be registered in the
	 * {@link com.foreach.across.modules.entity.registry.EntityRegistry}.
	 *
	 * @param targetEntityType type of the target entity
	 * @return current builder
	 */
	public EntityAssociationBuilder targetEntityType( Class<?> targetEntityType ) {
		Assert.notNull( targetEntityType );
		this.targetEntityType = targetEntityType;
		return this;
	}

	/**
	 * Set the name of the target entity for this association.  The entity must be registered in the
	 * {@link EntityRegistry} present in the {@link org.springframework.beans.factory.BeanFactory} provided.
	 * This property takes precedence over {@link #targetEntityType(Class)}.
	 *
	 * @param entityName name of the target entity
	 * @return current builder
	 */
	public EntityAssociationBuilder targetEntity( String entityName ) {
		Assert.notNull( entityName );
		this.targetEntityName = entityName;
		return this;
	}

	/**
	 * Set the name of the property on the source end.  The property should be found in the corresponding
	 * {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry} of the source
	 * {@link com.foreach.across.modules.entity.registry.EntityConfiguration}.
	 * <p/>
	 * Should not be null.  If you want to remove the source property explicitly, use {@link #removeSourceProperty()}.
	 *
	 * @param sourceProperty name, not null
	 * @return current builder
	 */
	public EntityAssociationBuilder sourceProperty( String sourceProperty ) {
		Assert.notNull( sourceProperty );
		this.sourceProperty = sourceProperty;
		sourcePropertyRemoved = false;
		return this;
	}

	/**
	 * Remove the property on the source end.  This implies that the association is entirely determined by
	 * the target entity linking back to the source entity.
	 *
	 * @return current builder
	 */
	public EntityAssociationBuilder removeSourceProperty() {
		sourcePropertyRemoved = true;
		return this;
	}

	/**
	 * Set the name of the property on the target end.  The property should be found in the corresponding
	 * {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry} of the target
	 * {@link com.foreach.across.modules.entity.registry.EntityConfiguration}.
	 * <p/>
	 * Should not be null.  If you want to remove the target property explicitly, use {@link #removeTargetProperty()}.
	 *
	 * @param targetProperty name, not null
	 * @return current builder
	 */
	public EntityAssociationBuilder targetProperty( String targetProperty ) {
		Assert.notNull( targetProperty );
		this.targetProperty = targetProperty;
		targetPropertyRemoved = false;
		return this;
	}

	/**
	 * Remove the property on the target end.  This implies that the association is entirely determined by
	 * the source entity linking to the target entity, without a back link.
	 *
	 * @return current builder
	 */
	public EntityAssociationBuilder removeTargetProperty() {
		targetPropertyRemoved = true;
		return this;
	}

	/**
	 * Ensures the association <strong>will not</strong> be labeled as hidden for UI implementations.
	 *
	 * @return current builder
	 */
	public EntityAssociationBuilder show() {
		return hidden( false );
	}

	/**
	 * Ensures the association <strong>will</strong> be labeled as hidden for UI implementations.
	 *
	 * @return current builder
	 */
	public EntityAssociationBuilder hide() {
		return hidden( true );
	}

	/**
	 * Ensures the configuration will be labeled as hidden for UI implementations if any of its
	 * participating configurations is hidden.  This is the default behavior if the property is not
	 * specified explicitly on the association.
	 *
	 * @return current builder
	 */
	public EntityAssociationBuilder autoHide() {
		return hidden( null );
	}

	/**
	 * Should the {@link com.foreach.across.modules.entity.registry.EntityAssociation} be hidden from UI
	 * implementations. This property can be considered a hint for automatically generated user interfaces.
	 *
	 * @param hidden True if the association should be hidden from UI.
	 * @return current builder
	 */
	public EntityAssociationBuilder hidden( Boolean hidden ) {
		hiddenSpecified = true;
		this.hidden = hidden;
		return this;
	}

	/**
	 * Set the UI behaviour in case the parent entity is being deleted but there are associations.
	 *
	 * @param parentDeleteMode to use
	 * @return current builder
	 * @see com.foreach.across.modules.entity.registry.EntityAssociation.ParentDeleteMode
	 */
	public EntityAssociationBuilder parentDeleteMode( EntityAssociation.ParentDeleteMode parentDeleteMode ) {
		this.parentDeleteMode = parentDeleteMode;
		return this;
	}

	@Override
	public EntityAssociationBuilder listView( Consumer<EntityListViewFactoryBuilder> consumer ) {
		return (EntityAssociationBuilder) super.listView( consumer );
	}

	@Override
	public EntityAssociationBuilder listView( String viewName,
	                                          Consumer<EntityListViewFactoryBuilder> consumer ) {
		return (EntityAssociationBuilder) super.listView( viewName, consumer );
	}

	@Override
	public EntityAssociationBuilder createOrUpdateFormView( Consumer<EntityViewFactoryBuilder> consumer ) {
		return (EntityAssociationBuilder) super.createOrUpdateFormView( consumer );
	}

	@Override
	public EntityAssociationBuilder createFormView( Consumer<EntityViewFactoryBuilder> consumer ) {
		return (EntityAssociationBuilder) super.createFormView( consumer );
	}

	@Override
	public EntityAssociationBuilder updateFormView( Consumer<EntityViewFactoryBuilder> consumer ) {
		return (EntityAssociationBuilder) super.updateFormView( consumer );
	}

	@Override
	public EntityAssociationBuilder deleteFormView( Consumer<EntityViewFactoryBuilder> consumer ) {
		return (EntityAssociationBuilder) super.deleteFormView( consumer );
	}

	@Override
	public EntityAssociationBuilder formView( String viewName,
	                                          Consumer<EntityViewFactoryBuilder> consumer ) {
		return (EntityAssociationBuilder) super.formView( viewName, consumer );
	}

	@Override
	public EntityAssociationBuilder view( String viewName,
	                                      Consumer<EntityViewFactoryBuilder> consumer ) {
		return (EntityAssociationBuilder) super.view( viewName, consumer );
	}

	@Override
	public EntityAssociationBuilder attribute( String name, Object value ) {
		return (EntityAssociationBuilder) super.attribute( name, value );
	}

	@Override
	public <S> EntityAssociationBuilder attribute( Class<S> type, S value ) {
		return (EntityAssociationBuilder) super.attribute( type, value );
	}

	/**
	 * Apply the association builder to the configuration specified.  This will add or modify the association
	 * represented by this builder.
	 *
	 * @param configuration to register the association in
	 */
	void apply( MutableEntityConfiguration configuration ) {
		MutableEntityAssociation association = configuration.association( name );

		if ( association == null ) {
			EntityConfiguration targetConfiguration = retrieveTargetConfiguration();

			association = configuration.createAssociation( name );
			association.setTargetEntityConfiguration( targetConfiguration );
		}
		else if ( targetEntityType != null ) {
			association.setTargetEntityConfiguration( retrieveTargetConfiguration() );
		}

		if ( sourcePropertyRemoved ) {
			association.setSourceProperty( null );
		}
		else if ( sourceProperty != null ) {
			EntityPropertyDescriptor sourcePropertyDescriptor =
					association.getSourceEntityConfiguration().getPropertyRegistry().getProperty( sourceProperty );

			if ( sourcePropertyDescriptor == null ) {
				throw new IllegalArgumentException(
						"Property " + sourceProperty + " was not found as source property for association " + name );
			}

			association.setSourceProperty( sourcePropertyDescriptor );
		}

		if ( targetPropertyRemoved ) {
			association.setTargetProperty( null );
		}
		else if ( targetProperty != null ) {
			EntityPropertyDescriptor targetPropertyDescriptor =
					association.getTargetEntityConfiguration().getPropertyRegistry().getProperty( targetProperty );

			if ( targetPropertyDescriptor == null ) {
				throw new IllegalArgumentException(
						"Property " + targetProperty + " was not found as target property for association " + name );
			}

			association.setTargetProperty( targetPropertyDescriptor );
		}

		if ( hiddenSpecified ) {
			association.setHidden( hidden );
		}

		if ( parentDeleteMode != null ) {
			association.setParentDeleteMode( parentDeleteMode );
		}

		applyAttributes( association );
		applyViews( association );
	}

	private EntityConfiguration retrieveTargetConfiguration() {
		EntityRegistry entityRegistry = beanFactory.getBean( EntityRegistry.class );
		EntityConfiguration targetConfiguration = targetEntityName != null
				? entityRegistry.getEntityConfiguration( targetEntityName )
				: entityRegistry.getEntityConfiguration( targetEntityType );

		if ( targetConfiguration == null ) {
			throw new IllegalArgumentException( "Unable to retrieve target entity configured: " + targetEntityType );
		}

		return targetConfiguration;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected <U extends EntityViewFactoryBuilder> U createViewFactoryBuilder( Class<U> builderType ) {
		if ( EntityListViewFactoryBuilder.class.isAssignableFrom( builderType ) ) {
			return (U) new EntityListViewFactoryBuilder( beanFactory );
		}

		return (U) new EntityViewFactoryBuilder( beanFactory );
	}
}
