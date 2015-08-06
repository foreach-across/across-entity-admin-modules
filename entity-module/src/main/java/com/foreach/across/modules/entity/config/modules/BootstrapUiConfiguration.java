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
package com.foreach.across.modules.entity.config.modules;

import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.modules.entity.newviews.EntityViewElementBuilderHelpers;
import com.foreach.across.modules.entity.newviews.EntityViewElementBuilderService;
import com.foreach.across.modules.entity.newviews.EntityViewElementBuilderServiceImpl;
import com.foreach.across.modules.entity.newviews.bootstrapui.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Arne Vandamme
 */
@AcrossDepends(required = "BootstrapUiModule")
@Configuration
public class BootstrapUiConfiguration
{
	// todo: this is not bootstrap dependant
	@Bean
	public EntityViewElementBuilderService entityViewElementBuilderService() {
		return new EntityViewElementBuilderServiceImpl();
	}

	@Bean
	public EntityViewElementBuilderHelpers entityViewElementHelpers() {
		return new EntityViewElementBuilderHelpers();
	}

	@Bean
	public BootstrapUiElementTypeLookupStrategy bootstrapUiElementTypeLookupStrategy() {
		return new BootstrapUiElementTypeLookupStrategy();
	}

	@Bean
	public FormGroupElementBuilderFactory formGroupElementBuilderFactory() {
		return new FormGroupElementBuilderFactory();
	}

	@Bean
	public LabelFormElementBuilderFactory labelFormElementBuilderFactory() {
		return new LabelFormElementBuilderFactory();
	}

	@Bean
	public TextboxFormElementBuilderFactory textboxFormElementBuilderFactory() {
		return new TextboxFormElementBuilderFactory();
	}

	@Bean
	public OptionsFormElementBuilderFactory optionsFormElementBuilderFactory() {
		return new OptionsFormElementBuilderFactory();
	}

	@Bean
	public TextViewElementBuilderFactory textViewElementBuilderFactory() {
		return new TextViewElementBuilderFactory();
	}

	@Bean
	public CheckboxFormElementBuilderFactory checkboxFormElementBuilderFactory() {
		return new CheckboxFormElementBuilderFactory();
	}

	@Bean
	public FieldsetFormElementBuilderFactory fieldsetFormElementBuilderFactory() {
		return new FieldsetFormElementBuilderFactory();
	}

	@Bean
	public DateTimeFormElementBuilderFactory dateTimeFormElementBuilderFactory() {
		return new DateTimeFormElementBuilderFactory();
	}

	@Bean
	public HiddenFormElementBuilderFactory hiddenFormElementBuilderFactory(){
		return new HiddenFormElementBuilderFactory();
	}
}
