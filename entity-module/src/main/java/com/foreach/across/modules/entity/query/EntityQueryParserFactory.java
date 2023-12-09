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

import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Central component for creating {@link EntityQueryParser} instances and related components.
 *
 * @author Arne Vandamme
 * @since 3.1.0
 */
@Service
@RequiredArgsConstructor
public class EntityQueryParserFactory
{
	private final EQTypeConverter defaultEntityQueryTypeConverter;

	/**
	 * Create a new {@link EntityQueryParser} prepared for the {@link EntityPropertyRegistry} specified.
	 * This returns a pre-configured and usable instance that can convert EQL statements for entities
	 * represented by the property registry into executable {@link EntityQuery} instances.
	 *
	 * @param propertyRegistry to configure the parser for
	 * @return parser instance
	 */
	public EntityQueryParser createParser( @NonNull EntityPropertyRegistry propertyRegistry ) {
		EntityQueryParser parser = createParser();
		parser.setMetadataProvider( createMetadataProvider( propertyRegistry ) );
		parser.setQueryTranslator( createTranslator( propertyRegistry ) );
		return parser;
	}

	/**
	 * Create a new {@link EntityQueryParser} instance without any default configuration.
	 * The returned instance will not be usable until both a {@link EntityQueryTranslator}
	 * and a {@link EntityQueryMetadataProvider} have been assigned.
	 *
	 * @return parser instance
	 */
	public EntityQueryParser createParser() {
		return new EntityQueryParser();
	}

	/**
	 * Create a new {@link EntityQueryMetadataProvider} that uses the specified {@link EntityPropertyRegistry}
	 * to determine the valid property, operand and argument combinations.
	 *
	 * @param propertyRegistry to build the metadata provider on
	 * @return metadata provider instance
	 */
	public DefaultEntityQueryMetadataProvider createMetadataProvider( @NonNull EntityPropertyRegistry propertyRegistry ) {
		return new DefaultEntityQueryMetadataProvider( propertyRegistry );
	}

	/**
	 * Create a new {@link EntityQueryTranslator} that translates the properties of a raw {@link EntityQuery}
	 * based on the information in the {@link EntityPropertyRegistry} and turns it into an executable
	 * query that can be passed to the corresponding {@link EntityQueryExecutor}.
	 *
	 * @param propertyRegistry to build the translator on
	 * @return translator instance
	 */
	public DefaultEntityQueryTranslator createTranslator( @NonNull EntityPropertyRegistry propertyRegistry ) {
		DefaultEntityQueryTranslator translator = new DefaultEntityQueryTranslator();
		translator.setPropertyRegistry( propertyRegistry );
		translator.setTypeConverter( defaultEntityQueryTypeConverter );
		return translator;
	}
}
