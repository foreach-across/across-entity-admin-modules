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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author Steven Gentens
 */
@Builder(toBuilder = true)
@AllArgsConstructor
@Getter
@Setter
public class BooleanDummy
{
	private Integer id;

	private boolean primitiveBooleanCheckbox;

	private boolean primitiveBooleanRadio;

	private boolean primitiveBooleanSelect;

	@NotNull
	private boolean primitiveBooleanSelectNonNull;

	private boolean primitiveBooleanDefaultControl;

	private Boolean booleanCheckbox;

	private Boolean booleanRadio;

	private Boolean booleanSelect;

	@NotNull
	private Boolean booleanSelectNonNull;

	private Boolean booleanDefaultControl;

}
