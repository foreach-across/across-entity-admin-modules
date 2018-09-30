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

import com.foreach.across.modules.bootstrapui.elements.*;
import com.foreach.across.modules.entity.bind.EntityPropertyBinder;
import com.foreach.across.modules.entity.bind.EntityPropertyControlName;
import com.foreach.across.modules.entity.bind.ListEntityPropertyBinder;
import com.foreach.across.modules.entity.bind.MapEntityPropertyBinder;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyHandlingType;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.modules.web.ui.*;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementBuilder;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;

import java.util.Map;

import static com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders.*;
import static com.foreach.across.modules.entity.bind.EntityPropertyControlName.forProperty;
import static com.foreach.across.modules.entity.views.util.EntityViewElementUtils.currentPropertyDescriptor;

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

	/**
	 * Maximum number of items that can be managed through the control.
	 * The add option should be disabled if the maximum is reached.
	 */
	@Setter
	private int maximumNrOfItems = -1;

	/**
	 * Minimum number of items that must be managed through the control.
	 * The delete option will be disabled if the minimum is reached.
	 */
	@Setter
	private int minimumNrOfItems = -1;

	/**
	 * Set to {@code true} (default) if an add item button should be inserted.
	 */
	@Setter
	private boolean enableAddingItem = true;

	/**
	 * Set to {@code true} (default) if a remove button should be added to each item.
	 */
	@Setter
	private boolean enableRemovingItem = true;

	/**
	 * Set to {@code true} if a sorting handle should be inserted.
	 * Defaults to {@code false}.
	 */
	@Setter
	private boolean sortable;

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
		EntityPropertyDescriptor propertyDescriptor = currentPropertyDescriptor( builderContext );
		val propertyName = propertyDescriptor.getName();

		EntityPropertyControlName.ForProperty controlName = forProperty( propertyDescriptor, builderContext );
		EntityPropertyControlName.ForProperty.BinderProperty templateControlName = controlName.asCollectionItem().withBinderItemKey( "{{key}}" ).asBinderItem();

		String removeItemMessage = builderContext.getMessage( "properties." + propertyName + "[removeItem]", "" );
		String addItemMessage = builderContext.getMessage( "properties." + propertyName + "[addItem]", "" );

		NodeViewElement list = super.createElement( builderContext );
		list.addCssClass( "js-embedded-collection-form-group", "embedded-collection-control", "embedded-collection-control-list" );
		list.setAttribute( "data-item-format", templateControlName.toItemPath() );
		list.addChild( collectionErrorHolder( controlName.forHandlingType( EntityPropertyHandlingType.forProperty( propertyDescriptor ) ) ) );

		list.addChild( itemRows( builderContext, controlName, binder.getItems(), removeItemMessage ) );

		if ( enableAddingItem ) {
			list.addChild( addItemAction( builderContext, addItemMessage ) );
		}

		list.addChild( boundIndicator( controlName ) );
		list.addChild( itemTemplate( builderContext, templateControlName, removeItemMessage ) );

		return list;
	}

	private ViewElement collectionErrorHolder( EntityPropertyControlName controlName ) {
		HiddenFormElement hidden = new HiddenFormElement();
		hidden.setControlName( controlName.toString() );
		hidden.setDisabled( true );
		return hidden.toFormControl();
	}

	private ViewElement boundIndicator( EntityPropertyControlName.ForProperty controlName ) {
		HiddenFormElement hidden = new HiddenFormElement();
		hidden.setControlName( controlName.asBinderItem().toBound() );
		hidden.setValue( "1" );
		return hidden;
	}

	private ViewElement itemTemplate( ViewElementBuilderContext parentBuilderContext,
	                                  EntityPropertyControlName.ForProperty.BinderProperty controlName,
	                                  String removeItemMessage ) {
		ViewElementBuilderContext builderContext = new DefaultViewElementBuilderContext( parentBuilderContext );
		EntityViewElementUtils.setCurrentEntity( builderContext, null );
		builderContext.setAttribute( EntityPropertyControlName.class, controlName.withInitializedValue() );

		return script( MediaType.TEXT_HTML )
				.data( ROLE, "edit-item-template" )
				.data( "template-prefix", controlName.toItemPath() )
				.add( createItemRowBuilder( controlName, null, Integer.MAX_VALUE, removeItemMessage ) )
				.postProcessor(
						( bc, element ) -> element.findAll( FormGroupElement.class )
						                          .forEach( group -> group.setDetectFieldErrors( false ) )
				)
				.build( builderContext );

	}

	private NodeViewElement itemRows( ViewElementBuilderContext builderContext,
	                                  EntityPropertyControlName.ForProperty controlName,
	                                  Map<String, EntityPropertyBinder<Object>> items,
	                                  String removeItemMessage ) {
		NodeViewElement itemRows = new NodeViewElement( "div" );
		itemRows.setAttribute( "data-role", "items" );
		itemRows.addCssClass( "embedded-collection-items" );

		int position = 0;
		int total = items.size();

		for ( Map.Entry<String, EntityPropertyBinder<Object>> entry : items.entrySet() ) {
			EntityPropertyControlName.ForProperty.BinderProperty itemControlName = controlName.asCollectionItem()
			                                                                                  .withIndex( position )
			                                                                                  .withBinderItemKey( entry.getKey() )
			                                                                                  .asBinderItem();

			IteratorItemStats<Object> itemStats = new IteratorItemStatsImpl<>( entry.getValue().getValue(), position, position < total );
			IteratorViewElementBuilderContext itemContext = new IteratorViewElementBuilderContext<>( itemStats );
			itemContext.setAttribute( EntityPropertyControlName.class, itemControlName.withInitializedValue() );
			itemContext.setAttribute( EntityPropertyBinder.class, entry.getValue() );
			itemContext.setParentContext( builderContext );

			itemRows.addChild(
					createItemRowBuilder( itemControlName, entry.getKey(), entry.getValue().getSortIndex(), removeItemMessage ).build( itemContext )
			);
		}

		return itemRows;
	}

	private NodeViewElementBuilder createItemRowBuilder( EntityPropertyControlName.ForProperty.BinderProperty propertyControlName,
	                                                     String itemKey,
	                                                     int sortIndex,
	                                                     String removeItemMessage ) {
		return div()
				.data( ROLE, "item" )
				.data( "item-key", itemKey )
				.css( "embedded-collection-item" )
				.add(
						sortable ?
								div()
										.name( "itemHandle" )
										.data( ROLE, "item-handle" )
										.css( "embedded-collection-item-handle" )
										.add( BootstrapUiBuilders.glyphIcon( GlyphIcon.MENU_HAMBURGER ) )
								: null
				)
				.add(
						div()
								.name( "itemData" )
								.data( ROLE, "item-data" )
								.css( "embedded-collection-item-data" )
								.add( itemTemplate )
								.add(
										hidden()
												.controlName( propertyControlName.toSortIndex() )
												.value( sortIndex )
								)
				)
				.add(
						enableRemovingItem ?
								div()
										.name( "itemActions" )
										.data( ROLE, "item-actions" )
										.css( "embedded-collection-item-actions" )
										.add(
												link()
														.data( ACTION, "remove-item" )
														.title( removeItemMessage )
														.add( glyphIcon( GlyphIcon.REMOVE ) )
										)
								: null
				);
	}

	private NodeViewElement addItemAction( ViewElementBuilderContext builderContext, String addItemMessage ) {
		return div()
				.data( ROLE, "actions" )
				.css( "embedded-collection-actions" )
				.add(
						button()
								.data( ACTION, "add-item" )
								.style( Style.DEFAULT )
								.iconLeft()
								.icon( glyphIcon( GlyphIcon.PLUS ) )
								.title( addItemMessage )
								.text( StringUtils.isEmpty( addItemMessage ) ? "" : " " + addItemMessage )

				)
				.build( builderContext );
	}
}
