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

package com.foreach.across.modules.entity.web.links;

import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

/**
 * @author Arne Vandamme
 * @since 4.0.0
 */
public interface EntityViewUriComponentsBuilder
{
	UriComponentsBuilder forEntityConfiguration( UriComponentsBuilder previousComponents,
	                                             EntityConfiguration entityConfiguration,
	                                             String viewName );

	UriComponentsBuilder forEntity( UriComponentsBuilder previousComponents,
	                                Map<String, Object> uriVariables,
	                                EntityConfiguration entityConfiguration,
	                                String entityId,
	                                String viewName );

	UriComponentsBuilder forEntityAssociation( UriComponentsBuilder previousComponents,
	                                           Map<String, Object> uriVariables,
	                                           EntityAssociation entityAssociation,
	                                           String entityId,
	                                           String associatedEntityId,
	                                           String viewName );

	UriComponentsBuilder forAssociatedEntity( UriComponentsBuilder previousComponents,
	                                          Map<String, Object> uriVariables,
	                                          EntityAssociation entityAssociation,
	                                          String entityId,
	                                          String associatedEntityId,
	                                          String viewName );
}
