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

package com.foreach.across.testmodules.elastic.domain.elastic.country;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "countryidx")
@EqualsAndHashCode(of = "id")
public class ElasticCountry implements Persistable<Long>
{
	@Id
	@Getter
	@Setter
	@Field(type = FieldType.Long)
	private Long id;

	@Getter
	@Setter
	@NotBlank
	@Length(max = 250)
	@Field(type = FieldType.Keyword)
	public String name;

	@Override
	public boolean isNew() {
		return Objects.isNull( getId() ) || getId() == 0;
	}
}
