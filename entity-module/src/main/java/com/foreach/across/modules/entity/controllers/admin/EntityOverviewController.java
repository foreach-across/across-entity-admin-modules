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

package com.foreach.across.modules.entity.controllers.admin;

import com.foreach.across.core.context.info.AcrossModuleInfo;
import com.foreach.across.modules.adminweb.ui.PageContentStructure;
import com.foreach.across.modules.bootstrapui.elements.Grid;
import com.foreach.across.modules.entity.conditionals.ConditionalOnBootstrapUI;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.web.links.EntityViewLinkBuilder;
import com.foreach.across.modules.spring.security.actions.AllowableAction;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements.bootstrap;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.html;

/**
 * Lists all entity types registered in the context for which the user has read access.
 * Deprecated as of 4.0.0 as the general value was limited. Developers are encouraged to
 * create their own overview pages.
 *
 * @author Arne Vandamme
 * @deprecated since 4.0.0
 */
@Deprecated
@ConditionalOnBootstrapUI
@RequiredArgsConstructor
public class EntityOverviewController
{
	private final EntityRegistry entityRegistry;
	private final PageContentStructure pageContentStructure;

	@RequestMapping(GenericEntityViewController.ROOT_PATH)
	public String listAllEntityTypes() {
		Map<String, List<EntityConfiguration>> entitiesByGroup = entityRegistry
				.getEntities()
				.stream()
				.sorted( Comparator.comparing( EntityConfiguration::getDisplayName ) )
				.filter( c -> !c.isHidden() && c.getAllowableActions().contains( AllowableAction.READ ) )
				.collect( Collectors.groupingBy( this::determineGroupName ) );

		NodeViewElementBuilder row = bootstrap.builders.row();

		entitiesByGroup.forEach( ( groupName, entities ) -> {
			NodeViewElementBuilder body = html.builders.div().with( css.listGroup, css.listGroup.flush );

			entities.forEach( entityConfiguration -> {
				EntityViewLinkBuilder linkBuilder = entityConfiguration.getAttribute( EntityViewLinkBuilder.class );
				EntityMessageCodeResolver codeResolver = entityConfiguration.getEntityMessageCodeResolver();

				body.add(
						bootstrap.builders.link()
						                  .text( codeResolver.getNameSingular() )
						                  .url( linkBuilder.toUriString() )
						                  .with( css.listGroup.item, css.listGroup.item.action )
				);
			} );

			row.add(
					bootstrap.builders.column( Grid.Device.MD.width( 3 ) )
					                  .add( html.builders.div()
					                                     .with( css.card )
					                                     .add( html.builders
							                                           .div()
							                                           .with( css.card.header )
							                                           .add( TextViewElement.text( groupName ) )
					                                     )
					                                     .add( body )
					                  )
			);
		} );

		pageContentStructure.addChild( row.build() );

		return PageContentStructure.TEMPLATE;
	}

	private String determineGroupName( EntityConfiguration entityConfiguration ) {
		return entityConfiguration.hasAttribute( AcrossModuleInfo.class )
				? entityConfiguration.getAttribute( AcrossModuleInfo.class ).getName()
				: "Other";
	}
}
