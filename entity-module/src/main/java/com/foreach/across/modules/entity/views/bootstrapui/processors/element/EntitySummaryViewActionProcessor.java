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
import com.foreach.across.modules.bootstrapui.styles.BootstrapStyles;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.modules.entity.web.links.EntityViewLinkBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils;

import static com.foreach.across.modules.entity.config.icons.EntityModuleIcons.entityModuleIcons;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElement.Functions.children;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElement.Functions.css;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.html;

/**
 * Registers an expandable detail view for an entity.
 *
 * @author Arne Vandamme
 */
public class EntitySummaryViewActionProcessor implements ViewElementPostProcessor<TableViewElement.Row>
{
	private final String viewName;
	private final EntityViewLinkBuilder linkBuilder;

	public EntitySummaryViewActionProcessor( EntityViewLinkBuilder linkBuilder, String viewName ) {
		this.linkBuilder = linkBuilder;
		this.viewName = viewName;
	}

	@Override
	public void postProcess( ViewElementBuilderContext builderContext, TableViewElement.Row element ) {
		Object entity = EntityViewElementUtils.currentEntity( builderContext );
		element.setAttribute( "data-summary-url", linkBuilder.forInstance( entity ).withViewName( viewName ).withPartial( "content" ).toUriString() );
		ContainerViewElementUtils.findAll( element, TableViewElement.Cell.class )
		                         .findFirst()
		                         .ifPresent( cell -> cell.addFirstChild(
				                         html.span( css( "js-summary-toggle" ),
				                                    children( entityModuleIcons.listView.summaryView.expand(),
				                                              entityModuleIcons.listView.summaryView.collapse()
				                                                                                    .set( BootstrapStyles.css.display.none ) ) ) ) );

	}
}
