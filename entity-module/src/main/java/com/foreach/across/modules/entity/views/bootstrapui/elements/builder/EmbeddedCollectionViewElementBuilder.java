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

package com.foreach.across.modules.entity.views.bootstrapui.elements.builder;

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders;
import com.foreach.across.modules.bootstrapui.elements.FormGroupElement;
import com.foreach.across.modules.bootstrapui.elements.GlyphIcon;
import com.foreach.across.modules.bootstrapui.utils.ControlNamePrefixAdjuster;
import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.bind.EntityPropertyBinder;
import com.foreach.across.modules.entity.bind.ListEntityPropertyBinder;
import com.foreach.across.modules.entity.bind.MapEntityPropertyBinder;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.EntityPropertyControlNamePostProcessor;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.modules.web.ui.*;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementBuilder;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

import static com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders.*;

/**
 * @author Arne Vandamme
 * @since 3.2.0
 */
// todo: better naming
@Accessors(chain = true, fluent = true)
public class EmbeddedCollectionViewElementBuilder extends NodeViewElementBuilder
{
	private final static String ROLE = "role";
	private final static String ACTION = "action";

	@Setter
	private ViewElementBuilder<ViewElement> itemTemplate;

	public EmbeddedCollectionViewElementBuilder() {
		super( "div" );
	}

	@Override
	protected NodeViewElement createElement( ViewElementBuilderContext builderContext ) {
		EntityPropertyBinder binder = EntityViewElementUtils.currentPropertyValueHolder( builderContext );

		if ( binder instanceof ListEntityPropertyBinder ) {
			return createListControl( (ListEntityPropertyBinder) binder, builderContext );
		}
		else if ( binder instanceof MapEntityPropertyBinder ) {
			throw new IllegalStateException( "Map types are not yet supported as embedded collections" );
		}

		return null;
	}

	private NodeViewElement createListControl( ListEntityPropertyBinder binder, ViewElementBuilderContext builderContext ) {
		NodeViewElement list = super.createElement( builderContext );
		list.setTagName( "div" );
		list.addCssClass( "js-embedded-collection-form-group", "embedded-collection-control", "embedded-collection-control-list" );

		String controlPrefix = StringUtils.removeEnd( EntityAttributes.controlName( EntityViewElementUtils.currentPropertyDescriptor( builderContext ) ),
		                                              ".value" );

		list.setAttribute( "data-item-format", controlPrefix + ".items[{{key}}]" );

		NodeViewElement itemRows = new NodeViewElement( "div" );
		itemRows.setAttribute( "data-role", "items" );
		itemRows.addCssClass( "embedded-collection-items" );

		Map<String, EntityPropertyBinder<Object>> items = binder.getItems();

		int position = 0;
		int total = items.size();

		for ( Map.Entry<String, EntityPropertyBinder<Object>> entry : items.entrySet() ) {
			IteratorItemStats<Object> itemStats = new IteratorItemStatsImpl<>( entry.getValue().getValue(), position, position < total );
			IteratorViewElementBuilderContext ctx = new IteratorViewElementBuilderContext<>( itemStats );
			ctx.setParentContext( builderContext );

			itemRows.addChild( createItemRow( ctx, controlPrefix, entry.getKey(), entry.getValue() ) );
		}

		list.addChild( itemRows );

		ViewElementBuilderContext bc = new DefaultViewElementBuilderContext( builderContext );
		EntityViewElementUtils.setCurrentEntity( bc, null );
		bc.setAttribute( EntityPropertyControlNamePostProcessor.PREFIX_CONTROL_NAMES, false );

		String baseControlName = controlPrefix;//EntityAttributes.controlName( memberDescriptor );

		String templateControlName = StringUtils.removeEnd( baseControlName, "items[].value" ) + "template";

		list.addChild( createAddItemAction() );
		list.addChild(
				hidden()
						.controlName( controlPrefix + ".bound" )
						.value( "1" )
						.build( bc )
		);
		list.addChild(
				node( "script" )
						.attribute( "type", "text/html" )
						.attribute( "data-role", "edit-item-template" )
						.attribute( "data-template-prefix", controlPrefix + ".itemTemplate" )
						.add( itemTemplate )
						.postProcessor(
								new ControlNamePrefixAdjuster<>()
										.prefixToReplace( controlPrefix + ".items[]" )
										.prefixToAdd( controlPrefix + ".itemTemplate" )
										::postProcess

						)
						.postProcessor(
								( builderContext1, element ) -> element.findAll( FormGroupElement.class )
								                                       .forEach( group -> group.setDetectFieldErrors( false ) )
						)
						.build( bc )

		);

		return list;
	}

	private NodeViewElement createItemRow( ViewElementBuilderContext builderContext, String controlPrefix, String itemKey, EntityPropertyBinder<Object> item ) {
		return div()
				.data( ROLE, "item" )
				.data( "item-id", itemKey )
				.css( "embedded-collection-item" )
				.add(
						div()
								.name( "itemHandle" )
								.data( ROLE, "item-handle" )
								.css( "embedded-collection-item-handle" )
								.add( BootstrapUiBuilders.glyphIcon( GlyphIcon.MENU_HAMBURGER ) )
				)
				.add(
						div()
								.name( "itemData" )
								.data( ROLE, "item-data" )
								.css( "embedded-collection-item-data" )
								.add(
										itemTemplate.andThen(
												new ControlNamePrefixAdjuster<>()
														.prefixToReplace( controlPrefix + ".items[]" )
														.prefixToAdd( controlPrefix + ".items[" + itemKey + "]" )
										)
								)
								.add(
										hidden()
												.controlName( controlPrefix + ".items[" + itemKey + "].sortIndex" )
												.value( item.getSortIndex() )
								)
				)
				.add(
						div()
								.name( "itemActions" )
								.data( ROLE, "item-actions" )
								.css( "embedded-collection-item-actions" )
								.add(
										link()
												.data( ACTION, "remove-item" )
												.title( "Remove" )
												.add( glyphIcon( GlyphIcon.REMOVE ) )
								)
				)
				.build( builderContext );
	}

	private NodeViewElement createItemTemplate( ViewElementBuilderContext builderContext ) {
		return new NodeViewElement( "div" );
	}

	private NodeViewElement createAddItemAction() {
		NodeViewElement actions = new NodeViewElement( "div" );
		actions.addChild(
				TextViewElement
						.html( "Toevoegen <a style=\"float: right\" data-action=\"add-item\"><span class=\"glyphicon glyphicon-plus-sign\"></span></a>" ) );
		return actions;
	}
}
