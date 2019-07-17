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

package com.foreach.across.modules.entity.support.properties;

import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.config.builders.EntityPropertyDescriptorBuilder;
import com.foreach.across.modules.entity.config.builders.EntityPropertyRegistryBuilder;
import com.foreach.across.modules.entity.query.EntityQueryCondition;
import com.foreach.across.modules.entity.query.EntityQueryConditionTranslator;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityModel;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Helper for registering an entity id proxy property: a new property of a specific
 * entity type, which references another property which represents the entity id.
 * <p/>
 * Useful for mapping entities which are only referenced by id to their actual target instances (and back).
 * The new property will inherit some configuration settings (like visibility) from the original property,
 * and will have a default configuration that should work for both reading and updating the target property.
 * This will also enable {@link com.foreach.across.modules.entity.query.EntityQuery} support for the new property.
 * <p/>
 * Can be used on {@link EntityPropertyRegistryBuilder#property(Function)}, for example:
 * <pre>{@code
 * props -> props.property( propertyRegistrationHelper.entityIdProxy("author").entityType(Author.class).targetPropertyName("authorId") )
 * }</pre>
 * The above example will create a property {@code author} of entity type {@code Author}, which will loads its
 * value by id using the value of the {@code authorId} property. When setting a value on {@code author} it will
 * automatically be translated to setting a value on {@code authorId} instead.
 *
 * @author Arne Vandamme
 * @since 3.2.0
 */
@RequiredArgsConstructor
@Accessors(chain = true, fluent = true)
public class EntityIdProxyPropertyRegistrar implements Function<EntityPropertyRegistryBuilder, EntityPropertyRegistryBuilder.PropertyDescriptorBuilder>
{
	private final EntityRegistry entityRegistry;
	private final ConversionService mvcConversionService;

	/**
	 * Name of the new proxy property.
	 */
	@Setter
	private String propertyName;

	/**
	 * The target property that the new property proxies.
	 * The target property will be marked as hidden.
	 */
	@Setter
	private String targetPropertyName;

	private Supplier<EntityConfiguration<?>> configurationSupplier;

	/**
	 * Configure the entity that the new property represents.
	 * The {@link EntityConfiguration} must have a valid {@link com.foreach.across.modules.entity.registry.EntityModel}.
	 *
	 * @param entityName name of the entity configuration
	 * @return property registration function
	 * @see #entityType(Class)
	 */
	public EntityIdProxyPropertyRegistrar entityName( @NonNull String entityName ) {
		configurationSupplier = () -> entityRegistry.getEntityConfiguration( entityName );
		return this;
	}

	/**
	 * Configure the entity that the new property represents.
	 * The {@link EntityConfiguration} must have a valid {@link com.foreach.across.modules.entity.registry.EntityModel}.
	 *
	 * @param entityType type of the entity
	 * @return property registration function
	 * @see #entityName(String)
	 */
	public EntityIdProxyPropertyRegistrar entityType( @NonNull Class<?> entityType ) {
		configurationSupplier = () -> entityRegistry.getEntityConfiguration( entityType );
		return this;
	}

	@Override
	public EntityPropertyRegistryBuilder.PropertyDescriptorBuilder apply( EntityPropertyRegistryBuilder props ) {
		props.processRegistry( registry -> {
			MutableEntityPropertyDescriptor targetProperty = registry.getProperty( targetPropertyName );
			Assert.notNull( targetProperty, "Target property for proxy not found: " + targetPropertyName );

			MutableEntityPropertyDescriptor newProperty = new EntityPropertyDescriptorBuilder( propertyName )
					.readable( targetProperty.isReadable() )
					.writable( targetProperty.isWritable() )
					.hidden( targetProperty.isHidden() )
					.build();

			registry.register( newProperty );

			if ( EntityAttributes.isRequired( targetProperty ) ) {
				newProperty.setAttribute( EntityAttributes.PROPERTY_REQUIRED, true );
			}

			TypeDescriptor targetTypeDescriptor = targetProperty.getPropertyTypeDescriptor();

			if ( targetTypeDescriptor.isArray() || targetTypeDescriptor.isCollection() ) {
				configureMultiValueTarget( targetTypeDescriptor, newProperty, registry );
			}
			else {
				configureSingleValueTarget( newProperty, registry );
			}

			configureEntityQueryConditionTranslator( newProperty, registry );

			targetProperty.setHidden( true );
		} );

		return props.property( propertyName );
	}

	@SuppressWarnings("unchecked")
	private void configureSingleValueTarget( MutableEntityPropertyDescriptor newProperty, EntityPropertyRegistry propertyRegistry ) {
		newProperty.setPropertyType( configurationSupplier.get().getEntityType() );

		ConfigurableEntityPropertyController<Object, Object> controller =
				(ConfigurableEntityPropertyController<Object, Object>) newProperty.getController();

		controller.withTarget( Object.class, Object.class )
		          .valueFetcher( owner -> {
			          EntityPropertyDescriptor targetProperty = propertyRegistry.getProperty( targetPropertyName );
			          EntityModel entityModel = configurationSupplier.get().getEntityModel();

			          Serializable targetId = (Serializable) targetProperty.getPropertyValue( owner );
			          return entityModel.findOne( convertIfNecessary( targetId, entityModel.getIdType() ) );
		          } )
		          .contextualValidator( ( object, property, errors, hints ) -> {
			          // if required, add a simple not null check to cover common cases
			          if ( EntityAttributes.isRequired( newProperty ) && property == null ) {
				          errors.rejectValue( "", "NotNull" );
			          }
		          } )
		          .withBindingContext( Object.class )
		          .applyValueConsumer( ( owner, propertyValue ) -> {
			          EntityPropertyDescriptor targetProperty = propertyRegistry.getProperty( targetPropertyName );
			          EntityModel entityModel = configurationSupplier.get().getEntityModel();

			          Object newValue = convertSingleValue( propertyValue.getNewValue(), targetProperty, entityModel );
			          Object oldValue = convertSingleValue( propertyValue.getOldValue(), targetProperty, entityModel );

			          targetProperty.getController().applyValue( owner, new EntityPropertyValue<>( oldValue, newValue, propertyValue.isDeleted() ) );
		          } );
	}

	@SuppressWarnings("unchecked")
	private void configureMultiValueTarget( TypeDescriptor targetTypeDescriptor,
	                                        MutableEntityPropertyDescriptor newProperty,
	                                        EntityPropertyRegistry propertyRegistry ) {
		Class<?> entityType = configurationSupplier.get().getEntityType();

		if ( targetTypeDescriptor.isArray() ) {
			newProperty.setPropertyTypeDescriptor( TypeDescriptor.array( TypeDescriptor.valueOf( entityType ) ) );
		}
		else {
			newProperty.setPropertyTypeDescriptor( TypeDescriptor.collection( targetTypeDescriptor.getType(), TypeDescriptor.valueOf( entityType ) ) );
		}

		ConfigurableEntityPropertyController<Object, Object> controller =
				(ConfigurableEntityPropertyController<Object, Object>) newProperty.getController();

		controller.withTarget( Object.class, Object.class )
		          .valueFetcher( owner -> {
			          EntityPropertyDescriptor targetProperty = propertyRegistry.getProperty( targetPropertyName );
			          EntityModel entityModel = configurationSupplier.get().getEntityModel();

			          Object targetValue = targetProperty.getPropertyValue( owner );

			          if ( targetValue != null ) {
				          List valuesAsList = mvcConversionService.convert( targetValue, ArrayList.class );
				          List converted = new ArrayList( valuesAsList.size() );

				          TypeDescriptor entityTypeDescriptor = newProperty.getPropertyTypeDescriptor().getElementTypeDescriptor();

				          for ( Object o : valuesAsList ) {
					          converted.add( entityModel.findOne( convertIfNecessary( (Serializable) o, entityModel.getIdType() ) ) );
				          }

				          return mvcConversionService.convert( converted,
				                                               TypeDescriptor.collection( List.class, entityTypeDescriptor ),
				                                               newProperty.getPropertyTypeDescriptor() );
			          }

			          return null;
		          } )
		          .contextualValidator( ( object, property, errors, hints ) -> {
			          // if required, add a simple not empty check to cover common cases
			          if ( EntityAttributes.isRequired( newProperty ) && (
					          property == null
							          || ( property instanceof Collection && ( (Collection) property ).isEmpty() )
							          || ( property.getClass().isArray() && Array.getLength( property ) == 0 )
			          ) ) {
				          errors.rejectValue( "", "NotEmpty" );
			          }
		          } )
		          .withBindingContext( Object.class )
		          .applyValueConsumer( ( owner, propertyValue ) -> {
			          EntityPropertyDescriptor targetProperty = propertyRegistry.getProperty( targetPropertyName );
			          EntityModel entityModel = configurationSupplier.get().getEntityModel();

			          Object newValue = convertMultiValue( propertyValue.getNewValue(), targetProperty, entityModel );
			          Object originalValue = convertMultiValue( propertyValue.getOldValue(), targetProperty, entityModel );

			          targetProperty.getController().applyValue( owner, new EntityPropertyValue<>( originalValue, newValue, propertyValue.isDeleted() ) );
		          } );
	}

	private Object convertSingleValue( Object value, EntityPropertyDescriptor targetProperty, EntityModel<Object, Serializable> entityModel ) {
		if ( value != null ) {
			return convertIfNecessary( entityModel.getId( value ), targetProperty.getPropertyTypeDescriptor() );
		}
		return null;
	}

	private Object convertMultiValue( Object value, EntityPropertyDescriptor targetProperty, EntityModel<Object, Serializable> entityModel ) {
		Object newValue = value;

		if ( newValue != null ) {
			List valuesAsList = mvcConversionService.convert( newValue, ArrayList.class );
			List<Object> converted = new ArrayList<>( valuesAsList.size() );

			TypeDescriptor entityTypeDescriptor = targetProperty.getPropertyTypeDescriptor().getElementTypeDescriptor();

			for ( Object o : valuesAsList ) {
				converted.add( convertIfNecessary( entityModel.getId( o ), entityTypeDescriptor ) );
			}

			newValue = mvcConversionService.convert( converted,
			                                         TypeDescriptor.collection( List.class, entityTypeDescriptor ),
			                                         targetProperty.getPropertyTypeDescriptor() );
		}

		return newValue;
	}

	@SuppressWarnings("unchecked")
	private void configureEntityQueryConditionTranslator( MutableEntityPropertyDescriptor newProperty, EntityPropertyRegistry propertyRegistry ) {
		newProperty.setAttribute(
				EntityQueryConditionTranslator.class,
				condition -> {
					Object[] args = condition.getArguments();
					EntityModel entityModel = configurationSupplier.get().getEntityModel();
					EntityPropertyDescriptor targetProperty = propertyRegistry.getProperty( targetPropertyName );

					Object[] idArgs = new Object[args.length];

					TypeDescriptor propertyTypeDescriptor = targetProperty.getPropertyTypeDescriptor();
					TypeDescriptor targetType = propertyTypeDescriptor.isCollection() || propertyTypeDescriptor.isArray()
							? propertyTypeDescriptor.getElementTypeDescriptor() : propertyTypeDescriptor;

					for ( int i = 0; i < args.length; i++ ) {
						idArgs[i] = convertIfNecessary( entityModel.getId( args[i] ), targetType );
					}

					return new EntityQueryCondition( targetPropertyName, condition.getOperand(), idArgs );
				}
		);
	}

	private Serializable convertIfNecessary( Serializable value, TypeDescriptor targetType ) {
		return value != null && targetType != null
				? (Serializable) mvcConversionService.convert( value, TypeDescriptor.forObject( value ), targetType )
				: value;
	}

	private Object convertIfNecessary( Object value, TypeDescriptor targetType ) {
		return value != null && targetType != null
				? (Object) mvcConversionService.convert( value, TypeDescriptor.forObject( value ), targetType )
				: value;
	}

	private Serializable convertIfNecessary( Serializable value, Class<?> targetType ) {
		return value != null && targetType != null ? (Serializable) mvcConversionService.convert( value, targetType ) : value;
	}
}
