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
import com.foreach.across.modules.bootstrapui.elements.builder.ButtonViewElementBuilder;
import com.foreach.across.modules.bootstrapui.elements.builder.TableViewElementBuilder;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.modules.entity.web.links.EntityViewLinkBuilder;
import com.foreach.across.modules.entity.web.links.SingleEntityViewLinkBuilder;
import com.foreach.across.modules.spring.security.actions.AllowableAction;
import com.foreach.across.modules.spring.security.actions.AllowableActions;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import lombok.Setter;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements.bootstrap;
import static com.foreach.across.modules.entity.config.icons.EntityModuleIcons.entityModuleIcons;

/**
 * Adds common actions (update, delete) for an {@link EntityConfiguration} entity to every result item.
 */
public class EntityListActionsProcessor implements ViewElementPostProcessor<TableViewElement.Row>
{
	public static final String CELL_NAME = "row-actions";

	protected final EntityConfiguration<Object> entityConfiguration;
	protected final EntityViewLinkBuilder linkBuilder;
	protected final EntityMessages messages;

	/**
	 * -- SETTER --
	 * Sets whether every result item should link to the detail view by default.
	 */
	@Setter
	private boolean linkToDetailView = false;

	@SuppressWarnings("unchecked")
	public EntityListActionsProcessor( EntityConfiguration entityConfiguration,
	                                   EntityViewLinkBuilder linkBuilder,
	                                   EntityMessages messages ) {
		this.entityConfiguration = entityConfiguration;
		this.linkBuilder = linkBuilder;
		this.messages = messages;
	}

	@Override
	public void postProcess( ViewElementBuilderContext builderContext, TableViewElement.Row row ) {
		TableViewElementBuilder.Cell cell = new TableViewElementBuilder.Cell()
				.name( CELL_NAME )
				.with( css.text.nowrap )
				.css( "row-actions" );

		Object entity = EntityViewElementUtils.currentEntity( builderContext );

		if ( entity != null ) {
			addEntityActions( cell, entity );
		}
		else {
			cell.heading( true );
		}

		row.addChild( cell.build( builderContext ) );
	}

	protected void addEntityActions( TableViewElementBuilder.Cell cell, Object entity ) {
		AllowableActions allowableActions = entityConfiguration.getAllowableActions( entity );

		SingleEntityViewLinkBuilder url = linkBuilder.forInstance( entity );

		ButtonViewElementBuilder detailViewBtn = bootstrap.builders.button()
		                                                           .link( url.toUriString() )
		                                                           .iconOnly( entityModuleIcons.listView.linkToDetailView() )
		                                                           .text( messages.viewAction() );
		if ( linkToDetailView ) {
			if ( allowableActions.contains( AllowableAction.READ ) ) {
				cell.add( detailViewBtn );
			}
		}
		else {
			if ( allowableActions.contains( AllowableAction.UPDATE ) ) {
				cell.add(
						bootstrap.builders.button()
						                  .link( url.updateView().toUriString() )
						                  .iconOnly( entityModuleIcons.listView.linkToEditView() )
						                  .text( messages.updateAction() )
				);
			}
			else if ( allowableActions.contains( AllowableAction.READ ) ) {
				cell.add( detailViewBtn );
			}
		}

		if ( allowableActions.contains( AllowableAction.DELETE ) ) {
			cell.add(
					bootstrap.builders.button()
					                  .link( url.deleteView().toUriString() )
					                  .iconOnly( entityModuleIcons.listView.linkToDeleteView() )
					                  .text( messages.deleteAction() )
			);
		}
	}

}
