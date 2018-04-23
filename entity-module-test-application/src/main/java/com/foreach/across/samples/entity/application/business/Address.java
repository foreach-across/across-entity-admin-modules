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

package com.foreach.across.samples.entity.application.business;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
@Getter
@Setter
@Embeddable
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address
{
	@Column
	@NotNull
	private AddressType addressType;

	@Column
	@NotBlank
	@Length(max = 250)
	private String street;
/*
	@Column
	@NotBlank
	@Length(max = 10)
	private String number;
*/
	@Column
	@NotBlank
	@Length(max = 50)
	private String city;

/*	@Column
	@NotBlank
	@Length(max = 2)
	private String country;*/

	public enum AddressType
	{
		PRIMARY,
		WORK,
		OTHER
	}
}
