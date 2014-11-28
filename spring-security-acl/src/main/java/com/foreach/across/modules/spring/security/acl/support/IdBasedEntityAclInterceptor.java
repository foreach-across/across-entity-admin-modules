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
package com.foreach.across.modules.spring.security.acl.support;

import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.modules.hibernate.aop.IdBasedEntityInterceptor;
import com.foreach.across.modules.hibernate.business.IdBasedEntity;
import com.foreach.across.modules.spring.security.acl.services.AclSecurityService;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ClassUtils;

/**
 * Base class for an interceptor hooked to {@link com.foreach.across.modules.hibernate.repositories.BasicRepository}
 * persistence methods.  Useful for creating ACLs when saving or deleting instances.
 * <p/>
 * Implementations will be picked up automatically by the
 * {@link com.foreach.across.modules.hibernate.aop.BasicRepositoryInterceptor} if it is active.
 *
 * @author Arne Vandamme
 */
@AcrossDepends(required = "AcrossHibernateModule")
public abstract class IdBasedEntityAclInterceptor<T extends IdBasedEntity> extends IdBasedEntityInterceptor<T>
{
	protected final Logger LOG = LoggerFactory.getLogger( ClassUtils.getUserClass( getClass() ) );

	@Autowired
	private AclSecurityService aclSecurityService;

	protected AclSecurityService aclSecurityService() {
		return aclSecurityService;
	}

	/**
	 * @return The current SecurityPrincipal or null in case there is no instance of SecurityPrincipal attached.
	 */
	protected SecurityPrincipal currentSecurityPrincipal() {
		if ( isAuthenticated() ) {
			Object principal = currentAuthentication().getPrincipal();

			if ( principal instanceof SecurityPrincipal ) {
				return (SecurityPrincipal) principal;
			}
		}

		return null;
	}

	protected boolean isAuthenticated() {
		return currentAuthentication().isAuthenticated();
	}

	protected Authentication currentAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

}
