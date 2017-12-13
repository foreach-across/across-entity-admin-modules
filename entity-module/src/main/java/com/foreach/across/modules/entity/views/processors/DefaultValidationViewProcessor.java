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

package com.foreach.across.modules.entity.views.processors;

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.request.EntityViewCommandValidator;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.WebDataBinder;

import java.util.EnumSet;
import java.util.Set;

/**
 * Applies default validation in case of a PUT, POST, DELETE or PATCH request (state modifying).
 * Sets the {@link com.foreach.across.modules.entity.views.request.EntityViewCommandValidator} and applies it.
 * <p/>
 * This processor should by preference come as early as possible as it sets the default validator.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Component
@Exposed
@Scope("prototype")
public class DefaultValidationViewProcessor extends SimpleEntityViewProcessorAdapter
{
	private EntityViewCommandValidator entityViewCommandValidator;

	/**
	 * Set the {@link HttpMethod} for which default validation should be configured.
	 */
	@NonNull
	@Setter
	private Set<HttpMethod> httpMethods = EnumSet.of( HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.PATCH );

	/**
	 * Optionally set validation hints that should be used for the default validation.
	 */
	private Object[] validationHints = new Object[0];

	public void setValidationHints( @NonNull Object... validationHints ) {
		this.validationHints = validationHints;
	}

	@Override
	public void initializeCommandObject( EntityViewRequest entityViewRequest, EntityViewCommand command, WebDataBinder dataBinder ) {
		if ( httpMethods.contains( entityViewRequest.getHttpMethod() ) ) {
			dataBinder.setValidator( entityViewCommandValidator );
		}
	}

	@Override
	public void preProcess( EntityViewRequest entityViewRequest, EntityView entityView ) {
		if ( httpMethods.contains( entityViewRequest.getHttpMethod() ) ) {
			entityViewRequest.getDataBinder().validate( validationHints );
		}
	}

	@Autowired
	void setEntityViewCommandValidator( EntityViewCommandValidator entityViewCommandValidator ) {
		this.entityViewCommandValidator = entityViewCommandValidator;
	}
}
