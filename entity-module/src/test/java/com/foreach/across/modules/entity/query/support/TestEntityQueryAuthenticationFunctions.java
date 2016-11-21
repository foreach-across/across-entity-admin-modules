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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static com.foreach.across.modules.entity.query.support.EntityQueryAuthenticationFunctions.CURRENT_USER;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class TestEntityQueryAuthenticationFunctions
{
	private EntityQueryAuthenticationFunctions functions;

	@Before
	public void reset() {
		functions = new EntityQueryAuthenticationFunctions();
	}

	@After
	public void clearSecurity() {
		SecurityContextHolder.clearContext();
	}

	@Test
	public void accepts() {
		assertTrue( functions.accepts( CURRENT_USER, TypeDescriptor.valueOf( String.class ) ) );

		assertFalse( functions.accepts( "unknown", TypeDescriptor.valueOf( Object.class ) ) );
		assertFalse( functions.accepts( CURRENT_USER, TypeDescriptor.valueOf( Object.class ) ) );
	}

	@Test(expected = IllegalStateException.class)
	public void noSecurityContextThrowsIllegalState() {
		SecurityContextHolder.clearContext();
		functions.apply( "currentUser", new Object[0], TypeDescriptor.valueOf( String.class ), null );
	}

	@Test(expected = IllegalStateException.class)
	public void noAuthenticationThrowsIllegalState() {
		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		SecurityContextHolder.setContext( securityContext );
		functions.apply( "currentUser", new Object[0], TypeDescriptor.valueOf( String.class ), null );
	}

	@Test
	public void authenticationNameIsReturned() {
		Authentication authentication = mock( Authentication.class );
		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		securityContext.setAuthentication( authentication );
		SecurityContextHolder.setContext( securityContext );

		when( authentication.getName() ).thenReturn( "username" );

		assertEquals(
				"username",
				functions.apply( "currentUser", new Object[0], TypeDescriptor.valueOf( String.class ), null )
		);
	}
}
