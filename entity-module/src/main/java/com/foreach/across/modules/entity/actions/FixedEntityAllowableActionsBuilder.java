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
package com.foreach.across.modules.entity.actions;

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.spring.security.actions.AllowableAction;
import com.foreach.across.modules.spring.security.actions.AllowableActions;

import java.util.Iterator;

/**
 * Implementation of {@link com.foreach.across.modules.entity.actions.EntityConfigurationAllowableActionsBuilder} that
 * will always return the same set of {@link com.foreach.across.modules.spring.security.actions.AllowableAction}s.
 *
 * @author Arne Vandamme
 */
public class FixedEntityAllowableActionsBuilder implements EntityConfigurationAllowableActionsBuilder
{
	/**
	 * Dummy {@link com.foreach.across.modules.spring.security.actions.AllowableActions} that will reply positively
	 * for every action but cannot be iterated.
	 */
	public static final AllowableActions DEFAULT_ALLOWABLE_ACTIONS = new AllowableActions()
	{
		@Override
		public boolean contains( AllowableAction action ) {
			return true;
		}

		@Override
		public Iterator<AllowableAction> iterator() {
			return new Iterator<AllowableAction>()
			{
				@Override
				public boolean hasNext() {
					return false;
				}

				@Override
				public AllowableAction next() {
					return null;
				}

				@Override
				public void remove() {

				}
			};
		}
	};

	private final AllowableActions allowableActions;

	public FixedEntityAllowableActionsBuilder( AllowableActions allowableActions ) {
		this.allowableActions = allowableActions;
	}

	@Override
	public AllowableActions getAllowableActions( EntityConfiguration<?> entityConfiguration ) {
		return allowableActions;
	}

	@Override
	public <V> AllowableActions getAllowableActions( EntityConfiguration<V> entityConfiguration, V entity ) {
		return allowableActions;
	}
}
