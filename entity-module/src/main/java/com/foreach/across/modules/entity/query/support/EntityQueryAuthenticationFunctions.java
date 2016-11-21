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

package com.foreach.across.modules.entity.query.support;

import com.foreach.across.modules.entity.query.EQTypeConverter;
import com.foreach.across.modules.entity.query.EntityQueryFunctionHandler;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Contains Spring security related entity query functions:
 * <ul>
 * <li>currentUser(): returns the principal name of the current authenticated principal</li>
 * </ul>
 * Supported property type is {@link String}.
 * <p/>
 * Note that if there is no principal authenticated this function will throw an exception.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class EntityQueryAuthenticationFunctions implements EntityQueryFunctionHandler
{
	public static final String CURRENT_USER = "currentUser";

	@Override
	public boolean accepts( String functionName, TypeDescriptor desiredType ) {
		return CURRENT_USER.equals( functionName ) && String.class.equals( desiredType.getObjectType() );
	}

	@Override
	public Object apply( String functionName,
	                     Object[] arguments,
	                     TypeDescriptor desiredType,
	                     EQTypeConverter argumentConverter ) {
		SecurityContext securityContext = SecurityContextHolder.getContext();

		if ( securityContext == null ) {
			throw new IllegalStateException( "No SecurityContext available for currentUser() function" );
		}

		Authentication authentication = securityContext.getAuthentication();

		if( authentication == null ) {
			throw new IllegalStateException( "No Authentication available for currentUser() function" );
		}

		return authentication.getName();
	}
}
