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

package com.foreach.across.modules.entity.config.builders;

import java.lang.annotation.*;

/**
 * An annotation which can be used to shadow an existing entityType and create a seperate view.
 * This view will create a new entityConfiguration, but keep all existing attributes from the original entityType.
 *
 * @author Marc Vanbrabant
 * @since 4.2.0
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EntityConfigurationView
{
	/***
	 * Determines for which entityType a view will be created.
	 * If not set, it will default to the super class of this entityType.
	 *
	 * See also {@link com.foreach.across.modules.entity.registry.processors.EntityConfigurationViewProcessor}
	 *
	 */
	Class<?> entityType() default void.class;
}
