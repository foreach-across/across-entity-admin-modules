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
package com.foreach.across.modules.entity.newviews.bootstrapui.elements.builder;

import com.foreach.across.modules.entity.newviews.util.EntityViewElementUtils;
import com.foreach.across.modules.hibernate.business.Auditable;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;

/**
 * <p>Custom {@link ViewElementBuilder} for created and last modified properties of any
 * {@link com.foreach.across.modules.hibernate.business.Auditable} entity.  Will combine both
 * the timestamp and the principal (if available) into one field.</p>
 * <p>Usually set for {@link com.foreach.across.modules.entity.newviews.ViewElementMode#LIST_VALUE}.</p>
 *
 * @author Arne Vandamme
 * @see com.foreach.across.modules.entity.config.AuditableEntityUiConfiguration
 */
public class AuditablePropertyViewElementBuilder implements ViewElementBuilder
{
	@Override
	public ViewElement build( ViewElementBuilderContext builderContext ) {
		Auditable auditable = EntityViewElementUtils.currentEntity( builderContext, Auditable.class );

		if ( auditable != null ) {

		}

		return null;
	}
}

