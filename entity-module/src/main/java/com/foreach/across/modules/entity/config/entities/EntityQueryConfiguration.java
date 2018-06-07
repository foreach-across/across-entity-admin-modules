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

package com.foreach.across.modules.entity.config.entities;

import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.query.*;
import com.foreach.across.modules.entity.query.support.EntityQueryAuthenticationFunctions;
import com.foreach.across.modules.entity.query.support.EntityQueryDateFunctions;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;

/**
 * Configures {@link EntityQuery} related components.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Configuration
class EntityQueryConfiguration
{
	@Bean
	public EQTypeConverter defaultEntityQueryTypeConverter() {
		return new EQTypeConverter();
	}

	@Bean
	public EntityQueryDateFunctions entityQueryDateFunctions() {
		return new EntityQueryDateFunctions();
	}

	@Bean
	@ConditionalOnClass(SecurityContext.class)
	public EntityQueryAuthenticationFunctions entityQueryAuthenticationFunctions() {
		return new EntityQueryAuthenticationFunctions();
	}

	/**
	 * Configures  default {@link com.foreach.across.modules.entity.query.EntityQueryParser} and {@link EntityQueryFacade}
	 * on all entities having a {@link com.foreach.across.modules.entity.query.EntityQueryExecutor}.
	 *
	 * @author Arne Vandamme
	 * @see EntityQueryParserFactory
	 * @since 2.0.0
	 */
	@Component
	@RequiredArgsConstructor
	static class EntityQueryFacadeRegistrar implements EntityConfigurer
	{
		private final EntityQueryParserFactory entityQueryParserFactory;

		@Override
		public void configure( EntitiesConfigurationBuilder all ) {
			all.matching( e -> e.hasAttribute( EntityQueryExecutor.class ) && !e.hasAttribute( EntityQueryParser.class ) && !e
					.hasAttribute( EntityQueryFacade.class ) )
			   .postProcessor(
					   entityConfiguration -> {
						   EntityPropertyRegistry propertyRegistry = entityConfiguration.getPropertyRegistry();

						   EntityQueryParser entityQueryParser = entityQueryParserFactory.createParser( propertyRegistry );
						   entityConfiguration.setAttribute( EntityQueryParser.class, entityQueryParser );

						   entityConfiguration.setAttribute(
								   EntityQueryFacade.class,
								   new SimpleEntityQueryFacade<>( entityQueryParser, entityConfiguration.getAttribute( EntityQueryExecutor.class ) )
						   );
					   }
			   );
		}
	}
}
