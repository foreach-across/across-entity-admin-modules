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

package com.foreach.across.modules.entity.views.request;

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Default validator for an {@link EntityViewCommand} that performs validation on both
 * the {@link EntityViewCommand#getEntity()} and individual {@link EntityViewCommand#getExtensions()} properties.
 * <p/>
 * This validator takes a single {@link com.foreach.across.modules.entity.views.context.EntityViewContext} and
 * a fallback {@link Validator}.  The entity attached to the {@link EntityViewCommand} is assumed
 * to be of {@link EntityViewContext#getEntityConfiguration()}.  If no custom validator is defined on the
 * {@link com.foreach.across.modules.entity.registry.EntityConfiguration}, the fallback validator will be used.
 * The same fallback validator will be used for validating the extensions.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Component
@Exposed
public class EntityViewCommandValidator implements Validator
{
	private final EntityViewContext entityViewContext;
	private final Validator fallbackValidator;

	@Autowired
	public EntityViewCommandValidator( EntityViewContext entityViewContext,
	                                   @Qualifier(EntityModule.VALIDATOR) Validator fallbackValidator ) {
		Assert.notNull( entityViewContext );
		Assert.notNull( fallbackValidator );

		this.entityViewContext = entityViewContext;
		this.fallbackValidator = fallbackValidator;
	}

	@Override
	public boolean supports( Class<?> clazz ) {
		return EntityViewCommand.class.isAssignableFrom( clazz );
	}

	@Override
	public void validate( Object target, Errors errors ) {
		EntityViewCommand command = (EntityViewCommand) target;
		Object entity = command.getEntity();

		if ( entity != null ) {
			errors.pushNestedPath( "entity" );
			retrieveEntityValidator().validate( entity, errors );
			errors.popNestedPath();
		}

		command.getExtensions()
		       .forEach( ( key, value ) -> {
			       errors.pushNestedPath( "extensions[" + key + "]" );
			       fallbackValidator.validate( value, errors );
			       errors.popNestedPath();
		       } );
	}

	private Validator retrieveEntityValidator() {
		Validator validator = null;

		EntityConfiguration entityConfiguration = entityViewContext.getEntityConfiguration();

		if ( entityConfiguration != null ) {
			validator = entityConfiguration.getAttribute( Validator.class );
		}

		return validator != null ? validator : fallbackValidator;
	}
}
