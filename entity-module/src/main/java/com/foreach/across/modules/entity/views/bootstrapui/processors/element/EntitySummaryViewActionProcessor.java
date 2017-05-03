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
package com.foreach.across.modules.entity.views.bootstrapui.processors.element;

import com.foreach.across.modules.bootstrapui.elements.TableViewElement;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.modules.entity.web.EntityLinkBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Registers an expandable detail view for an entity.
 *
 * @author Arne Vandamme
 */
public class EntitySummaryViewActionProcessor implements ViewElementPostProcessor<TableViewElement.Row>
{
	private final String viewName;
	private final EntityLinkBuilder linkBuilder;

	public EntitySummaryViewActionProcessor( EntityLinkBuilder linkBuilder, String viewName ) {
		this.linkBuilder = linkBuilder;
		this.viewName = viewName;
	}

	@Override
	public void postProcess( ViewElementBuilderContext builderContext, TableViewElement.Row element ) {
		Object entity = EntityViewElementUtils.currentEntity( builderContext );

		element.setAttribute( "data-summary-url",
		                      ServletUriComponentsBuilder
				                      .fromUriString( linkBuilder.view( entity ) )
				                      .queryParam( "view", viewName )
				                      .queryParam( "_partial", "content" )
				                      .toUriString()
		);
	}
}
