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

package com.foreach.across.modules.entity.query;

import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;

/**
 * Resolver interface to determine the best suited {@link EntityQueryFacade} to use, based on a specific context.
 * Usually entity query infrastructure is created based on the specific entity configuration being
 * queried. All methods except one delegate to this implementation.
 * <p/>
 * Purposely designed as a functional interface for simple cases.
 *
 * @author Arne Vandamme
 * @see EntityQueryFacade
 * @since 3.1.0
 */
@FunctionalInterface
public interface EntityQueryFacadeResolver
{
	default EntityQueryFacade forEntityViewRequest( EntityViewRequest viewRequest ) {
		return forEntityViewContext( viewRequest.getEntityViewContext() );
	}

	default EntityQueryFacade forEntityViewContext( EntityViewContext viewContext ) {
		return viewContext.isForAssociation()
				? forEntityAssociation( viewContext.getEntityAssociation() ) : forEntityConfiguration( viewContext.getEntityConfiguration() );
	}

	default EntityQueryFacade forEntityAssociation( EntityAssociation association ) {
		return forEntityConfiguration( association.getTargetEntityConfiguration() );
	}

	EntityQueryFacade forEntityConfiguration( EntityConfiguration entityConfiguration );
}
