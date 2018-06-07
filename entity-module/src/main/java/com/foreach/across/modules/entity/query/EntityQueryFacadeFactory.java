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
 * Factory interface to build the best suited {@link EntityQueryFacade} to use, based on a specific context.
 * Usually entity query infrastructure is created based on the specific entity configuration being queried.
 * <p/>
 * All methods have default implementations. Only {@link #createForEntityConfiguration(EntityConfiguration)}
 * must be overridden in order to have an implementation that is fully functional. All other methods delegate
 * to {@link EntityConfiguration} based creation by default.
 * <p/>
 * Instances of this interface are rarely used directly, but through the {@link EntityQueryFacadeResolver}.
 *
 * @author Arne Vandamme
 * @see EntityQueryFacade
 * @see EntityQueryFacadeResolver
 * @since 3.1.0
 */
public interface EntityQueryFacadeFactory
{
	default EntityQueryFacade createForEntityViewRequest( EntityViewRequest viewRequest ) {
		return createForEntityViewContext( viewRequest.getEntityViewContext() );
	}

	default EntityQueryFacade createForEntityViewContext( EntityViewContext viewContext ) {
		return viewContext.isForAssociation()
				? createForEntityAssociation( viewContext.getEntityAssociation() ) : createForEntityConfiguration( viewContext.getEntityConfiguration() );
	}

	default EntityQueryFacade createForEntityAssociation( EntityAssociation association ) {
		return createForEntityConfiguration( association.getTargetEntityConfiguration() );
	}

	default EntityQueryFacade createForEntityConfiguration( EntityConfiguration entityConfiguration ) {
		return null;
	}
}
