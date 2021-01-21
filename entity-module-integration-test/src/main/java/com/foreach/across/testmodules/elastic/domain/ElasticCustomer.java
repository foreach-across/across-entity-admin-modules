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

package com.foreach.across.testmodules.elastic.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

/**
 * @author Arne Vandamme
 * @since 2.2.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "customeridx")
public class ElasticCustomer
{
	@Id
	public String id;

	@Getter
	@Setter
	@NotBlank
	@Length(max = 250)
	public String firstName;

	@Getter
	@Setter
	@Length(max = 250)
	public String lastName;

	@Override
	public String toString() {
		return String.format(
				"Customer[id=%s, firstName='%s', lastName='%s']",
				id, firstName, lastName );
	}
}


