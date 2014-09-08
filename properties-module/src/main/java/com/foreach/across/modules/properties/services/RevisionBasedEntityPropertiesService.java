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
package com.foreach.across.modules.properties.services;

import com.foreach.across.core.revision.Revision;
import com.foreach.across.modules.properties.business.EntityProperties;

/**
 * @author Arne Vandamme
 */
public interface RevisionBasedEntityPropertiesService<T extends EntityProperties<U>, U, R extends Revision>
{
	T getProperties( U entityId, R revision );

	void saveProperties( T entityProperties, R revision );

	void deleteProperties( U entityId );

	void checkin( U entityId, R revision, int revisionId );
}
