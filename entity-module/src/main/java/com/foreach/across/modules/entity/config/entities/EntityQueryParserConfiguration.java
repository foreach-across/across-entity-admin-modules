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
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContext;

/**
 * Configures default {@link com.foreach.across.modules.entity.query.EntityQueryParser} on all entities
 * having a {@link com.foreach.across.modules.entity.query.EntityQueryExecutor}.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Configuration
public class EntityQueryParserConfiguration implements EntityConfigurer
{
	@Override
	public void configure( EntitiesConfigurationBuilder all ) {
		all.matching( e -> e.hasAttribute( EntityQueryExecutor.class ) && !e.hasAttribute( EntityQueryParser.class ) )
		   .postProcessor(
				   entityConfiguration -> {
					   EntityPropertyRegistry propertyRegistry = entityConfiguration.getPropertyRegistry();

					   EntityQueryParser entityQueryParser = entityQueryParser();
					   entityQueryParser.setMetadataProvider( entityQueryMetadataProvider( propertyRegistry ) );
					   entityQueryParser.setQueryTranslator( entityQueryTranslator( propertyRegistry ) );

					   entityConfiguration.setAttribute( EntityQueryParser.class, entityQueryParser );
				   }
		   );
	}

	@Bean
	@Scope("prototype")
	protected EntityQueryParser entityQueryParser() {
		return new EntityQueryParser();
	}

	@Bean
	@Scope("prototype")
	protected EntityQueryMetadataProvider entityQueryMetadataProvider( EntityPropertyRegistry propertyRegistry ) {
		return new DefaultEntityQueryMetadataProvider( propertyRegistry );
	}

	@Bean
	@Scope("prototype")
	protected EntityQueryTranslator entityQueryTranslator( EntityPropertyRegistry propertyRegistry ) {
		DefaultEntityQueryTranslator translator = new DefaultEntityQueryTranslator();
		translator.setPropertyRegistry( propertyRegistry );
		translator.setTypeConverter( defaultEntityQueryTypeConverter() );
		return translator;
	}

	@Bean
	protected EQTypeConverter defaultEntityQueryTypeConverter() {
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
}
