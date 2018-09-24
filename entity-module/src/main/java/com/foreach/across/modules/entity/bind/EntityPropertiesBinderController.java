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

package com.foreach.across.modules.entity.bind;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyBindingContext;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyController;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.Errors;

import java.util.*;

/**
 * Controller for an {@link EntityPropertiesBinder} which will perform {@link EntityPropertyController}
 * actions on the individual properties present. Callbacks can be registered for the related actions
 * that should be peformed on the actual entity, and those callbacks will execute after any
 * {@link EntityPropertyController#BEFORE_ENTITY} but before any {@link EntityPropertyController#AFTER_ENTITY} methods.
 * <p/>
 * Can be used to validate the properties of a binder, as well as save the modified values.
 *
 * @author Arne Vandamme
 * @since 3.2.0
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class EntityPropertiesBinderController
{
	private final EntityPropertiesBinder propertiesBinder;
	private final Collection<Runnable> entityValidationCallbacks = new ArrayList<>( 1 );
	private final Collection<Runnable> entitySaveCallbacks = new ArrayList<>( 1 );

	/**
	 * Add one or more callbacks that should be called whenever the entity validation should happen.
	 * These will get executed (in their registration order), after {@link EntityPropertyController#BEFORE_ENTITY},
	 * but before {@link EntityPropertyController#AFTER_ENTITY}.
	 *
	 * @param callback to add
	 * @return current controller
	 */
	public EntityPropertiesBinderController addEntityValidationCallback( Runnable... callback ) {
		entityValidationCallbacks.addAll( Arrays.asList( callback ) );
		return this;
	}

	/**
	 * Add one or more callbacks that should be called whenever the entity save should happen.
	 * These will get executed (in their registration order), after {@link EntityPropertyController#BEFORE_ENTITY},
	 * but before {@link EntityPropertyController#AFTER_ENTITY}.
	 *
	 * @param callback to add
	 * @return current controller
	 */
	public EntityPropertiesBinderController addEntitySaveCallback( Runnable... callback ) {
		entitySaveCallbacks.addAll( Arrays.asList( callback ) );
		return this;
	}

	/**
	 * Validate the properties that are present on the binder, executing the registered callbacks when the
	 * actual entity validation is expected to happen. Note that performing validation will also call
	 * {@link EntityPropertyController#applyValue(EntityPropertyBindingContext, EntityPropertyValue)} for any
	 * property with successful validation.
	 * <p/>
	 * Validation is considered successful (returns {@code true}) if no additional errors have been added and
	 * the count at the start of the method is the same as at the end.
	 *
	 * @param errors          to register validation errors
	 * @param validationHints optional validation hints (eg. validation groups)
	 * @return true if validation was successful, no validation errors have been added
	 */
	public boolean validateAndBind( @NonNull Errors errors, Object... validationHints ) {
		List<OrderedRunnable> actions = new ArrayList<>();
		actions.add( new OrderedRunnable( 0, () -> entitySaveCallbacks.forEach( Runnable::run ) ) );

		propertiesBinder.values()
		                .stream()
		                .map( property -> new OrderedRunnable( property.getControllerOrder(), () -> {
			                try {
				                errors.pushNestedPath( "propertyPrefix" );
				                if ( property.validate( errors, validationHints ) ) {
					                property.applyValue();
				                }
			                }
			                finally {
				                errors.popNestedPath();
			                }
		                } ) )
		                .forEach( actions::add );

		actions.sort( Comparator.comparingInt( OrderedRunnable::getOrder ) );
		actions.forEach( OrderedRunnable::run );

		return true;
	}

	/**
	 * Save the properties present on the binder, executing the registered callbacks when the actual entity
	 * is expected to be saved.
	 */
	public void save() {
		List<OrderedRunnable> actions = new ArrayList<>();
		actions.add( new OrderedRunnable( 0, () -> entitySaveCallbacks.forEach( Runnable::run ) ) );

		propertiesBinder.values()
		                .stream()
		                .map( property -> new OrderedRunnable( property.getControllerOrder(), property::save ) )
		                .forEach( actions::add );

		actions.sort( Comparator.comparingInt( OrderedRunnable::getOrder ) );
		actions.forEach( OrderedRunnable::run );
	}

	@RequiredArgsConstructor
	private static class OrderedRunnable implements Runnable
	{
		@Getter
		private final int order;
		private final Runnable target;

		@Override
		public void run() {
			target.run();
		}
	}
}
