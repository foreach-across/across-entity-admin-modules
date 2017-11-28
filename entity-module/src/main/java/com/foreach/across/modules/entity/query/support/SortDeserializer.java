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

package com.foreach.across.modules.entity.query.support;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple Jackson JSON deserializer for a {@link Sort} that was serialized without any special configuration.
 *
 * @author Arne Vandamme
 * @since 2.2.0
 */
public final class SortDeserializer extends JsonDeserializer<Sort>
{
	@Override
	public Sort deserialize( JsonParser jp, DeserializationContext ctxt ) throws IOException {
		JsonNode node = jp.getCodec().readTree( jp );

		List<Sort.Order> orderList = new ArrayList<>( node.size() );

		for ( JsonNode child : node ) {
			Sort.Direction direction = Sort.Direction.fromString( child.get( "direction" ).asText() );
			String property = child.get( "property" ).asText();
			Sort.NullHandling nullHandling = Sort.NullHandling.valueOf( child.get( "nullHandling" ).asText() );

			Sort.Order order = new Sort.Order( direction, property, nullHandling );

			if ( child.get( "ignoreCase" ).asBoolean() ) {
				order = order.ignoreCase();
			}

			orderList.add( order );
		}

		return new Sort( orderList );
	}
}
