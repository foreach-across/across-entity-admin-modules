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

package com.foreach.across.modules.entity.bind;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyBindingContext;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;

/**
 * Responsible for resolving the value that should be used for a template of a particular property.
 * A template is usually represented by either {@link ListEntityPropertyBinder#getItemTemplate()} or
 * {@link MapEntityPropertyBinder#getTemplate()}.
 * <p/>
 * The value is mostly used for pre-filling controls.
 *
 * @author Arne Vandamme
 * @see DefaultEntityPropertyTemplateValueResolver
 * @since 3.3.0
 */
@FunctionalInterface
public interface EntityPropertyTemplateValueResolver
{
	/**
	 * @param bindingContext for which a template is being rendered
	 * @param descriptor     for the template value
	 * @return value
	 */
	Object resolveTemplateValue( EntityPropertyBindingContext bindingContext, EntityPropertyDescriptor descriptor );
}
