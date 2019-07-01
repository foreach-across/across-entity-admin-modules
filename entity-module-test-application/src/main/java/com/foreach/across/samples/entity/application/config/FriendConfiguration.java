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

package com.foreach.across.samples.entity.application.config;

import com.foreach.across.modules.entity.actions.EntityConfigurationAllowableActionsBuilder;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.spring.security.actions.AllowableActionSet;
import com.foreach.across.modules.spring.security.actions.AllowableActions;
import com.foreach.across.samples.entity.application.business.Friend;
import org.springframework.context.annotation.Configuration;

/**
 * @author Stijn Vanhoof
 * @since 3.3.0
 */
@Configuration
public class FriendConfiguration implements EntityConfigurer
{
	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		entities.withType( Friend.class )
		        .allowableActionsBuilder(
				        new EntityConfigurationAllowableActionsBuilder()
				        {
					        @Override
					        public AllowableActions getAllowableActions( EntityConfiguration<?> entityConfiguration ) {
						        return new AllowableActionSet();
					        }

					        @Override
					        public <V> AllowableActions getAllowableActions( EntityConfiguration<V> entityConfiguration, V entity ) {
						        return new AllowableActionSet();
					        }
				        }
		        );
	}
}
