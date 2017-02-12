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

import com.foreach.across.modules.spring.security.actions.AllowableAction;
import lombok.Setter;

/**
 * Responsible for checking if the view is actually allowed.  Requires the {@link com.foreach.across.modules.entity.registry.EntityConfiguration}
 * or {@link com.foreach.across.modules.entity.registry.EntityAssociation} to be visible, and the principal to have the {@link AllowableAction}.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class ActionAllowedAuthorizationViewProcessor
{
	/**
	 * Set the {@link AllowableAction} that the principal should have on the {@link com.foreach.across.modules.entity.registry.EntityConfiguration}
	 * being viewed.  This will take the entity instance into account if there is one.
	 */
	@Setter
	private AllowableAction requiredAllowableAction;
}
