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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.data.domain.Sort;

import java.io.IOException;

/**
 * Custom serialization of {@link Sort} with full details.
 * @author Arne Vandamme
 * @since 4.0.0
 */
// todo: use EntityQuery custom serialization instead (?)
public final class SortSerializer extends JsonSerializer<Sort>
{
	@Override
	public void serialize( Sort sort, JsonGenerator jsonGenerator, SerializerProvider serializerProvider ) throws IOException {
		jsonGenerator.writeStartArray();
		for ( Sort.Order order : sort ) {
			jsonGenerator.writeStartObject();
			jsonGenerator.writeStringField( "property", order.getProperty() );
			jsonGenerator.writeStringField( "direction", order.getDirection().toString() );
			jsonGenerator.writeStringField( "nullHandling", order.getNullHandling().toString() );
			jsonGenerator.writeBooleanField( "ignoreCase", order.isIgnoreCase() );
			jsonGenerator.writeEndObject();
		}
		jsonGenerator.writeEndArray();
	}
}
