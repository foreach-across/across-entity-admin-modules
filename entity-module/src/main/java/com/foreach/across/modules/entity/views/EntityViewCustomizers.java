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

package com.foreach.across.modules.entity.views;

import com.foreach.across.modules.entity.views.settings.BasicEntityViewSettings;
import com.foreach.across.modules.entity.views.settings.FormEntityViewSettings;
import lombok.experimental.UtilityClass;

/**
 * Contains some additional {@link com.foreach.across.modules.entity.config.builders.EntityViewFactoryBuilder} consumers for common scenarios.
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
@UtilityClass
public class EntityViewCustomizers
{
	/**
	 * Customize basic settings.
	 *
	 * @return builder consumer
	 */
	public static BasicEntityViewSettings basicSettings() {
		return new BasicEntityViewSettings();
	}

	/**
	 * Customize form settings.
	 *
	 * @return builder consumer
	 */
	public static FormEntityViewSettings formSettings() {
		return new FormEntityViewSettings();
	}
}
