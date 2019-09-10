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

import com.foreach.across.modules.bootstrapui.elements.FormControlElement;
import com.foreach.across.modules.bootstrapui.elements.FormGroupElement;
import com.foreach.across.modules.bootstrapui.elements.HiddenFormElement;
import com.foreach.across.modules.bootstrapui.elements.Style;
import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.bind.EntityPropertyBinder;
import com.foreach.across.modules.entity.bind.EntityPropertyControlName;
import com.foreach.across.modules.entity.bind.ListEntityPropertyBinder;
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

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements.bootstrap;
import static com.foreach.across.modules.entity.config.icons.EntityModuleIcons.entityModuleIcons;
import static com.foreach.across.modules.entity.views.util.EntityViewElementUtils.currentPropertyDescriptor;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.html;

/**
 * Experimental.
 * Builds a form control for an embedded {@link Collection} of objects.
 * Every item renders as a sub-form with add/remove option.
 *
 * @author Arne Vandamme
 * @since 3.2.0
 */
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

	/**
	 * Set to {@code true} if only the existing values should be rendered, without any possibility
	 * of adding/removing/sorting items. This will not render any script templates either.
	 */
	@Setter
	private boolean readonly;

	public EmbeddedCollectionViewElementBuilder() {
		super( "div" );
	}

	@Override
	protected NodeViewElement createElement( ViewElementBuilderContext builderContext ) {
		EntityPropertyBinder binder = EntityViewElementUtils.currentPropertyBinder( builderContext );

		if ( binder instanceof ListEntityPropertyBinder ) {
			return createListControl( (ListEntityPropertyBinder) binder, builderContext );
		}

		return null;
	}

	private NodeViewElement createListControl( ListEntityPropertyBinder binder, ViewElementBuilderContext builderContext ) {
		EntityPropertyDescriptor propertyDescriptor = currentPropertyDescriptor( builderContext );
		val propertyName = propertyDescriptor.getName();

		EntityPropertyControlName.ForProperty controlName = EntityViewElementUtils.controlName( propertyDescriptor, builderContext ).asProperty();
		EntityPropertyControlName.ForProperty.BinderProperty templateControlName = controlName.asCollectionItem().withBinderItemKey( "{{key}}" ).asBinderItem();

		String removeItemMessage = builderContext.getMessage( "properties." + propertyName + "[removeItem]", "" );
		String addItemMessage = builderContext.getMessage( "properties." + propertyName + "[addItem]", "" );

		List<EntityPropertyBinder> items = binder.getItemList();

		if ( readonly && items.isEmpty() ) {
			return html.builders.p().css( "form-control-static" ).build( builderContext );
		}

		NodeViewElement list = super.createElement( builderContext );
		list.addCssClass( "embedded-collection-control", "embedded-collection-control-list" );

		if ( readonly ) {
			list.addCssClass( "embedded-collection-readonly" );
		}
		else {
			list.addCssClass( "js-embedded-collection-form-group" );
			list.setAttribute( "data-item-format", templateControlName.toItemPath() );
			list.addChild(
					formGroupControl( controlName.forHandlingType( EntityPropertyHandlingType.forProperty( propertyDescriptor ) ),
					                  EntityAttributes.isRequired( propertyDescriptor ) )
			);
		}

		list.addChild( itemRows( builderContext, controlName, items, removeItemMessage ) );

		if ( !readonly && enableAddingItem ) {
			list.addChild( addItemAction( builderContext, addItemMessage ) );
		}

		if ( !readonly ) {
			list.addChild( boundIndicator( controlName ) );
			list.addChild( itemTemplate( builderContext, binder.getItemTemplate(), templateControlName, removeItemMessage ) );
		}

		return list;
	}

	/**
	 * Represents the control that should be detected by a {@link FormGroupElement} for flagging the group
	 * as required and detecting the binding errors.
	 */
	private ViewElement formGroupControl( EntityPropertyControlName controlName, boolean required ) {
		HiddenFormElement hidden = new HiddenFormElement();
		hidden.setControlName( controlName.toString() );
		hidden.setDisabled( true );

		FormControlElement control = hidden.toFormControl();
		control.setRequired( required );

		return control;
	}

	private ViewElement boundIndicator( EntityPropertyControlName.ForProperty controlName ) {
		HiddenFormElement hidden = new HiddenFormElement();
		hidden.setControlName( controlName.asBinderItem().toBound() );
		hidden.setValue( "1" );
		return hidden;
	}

	private ViewElement itemTemplate( ViewElementBuilderContext parentBuilderContext,
	                                  EntityPropertyBinder templateBinder,
	                                  EntityPropertyControlName.ForProperty.BinderProperty controlName,
	                                  String removeItemMessage ) {
		ViewElementBuilderContext builderContext = new DefaultViewElementBuilderContext( parentBuilderContext );
		EntityViewElementUtils.setCurrentEntity( builderContext, null );
		builderContext.setAttribute( EntityPropertyBinder.class, templateBinder );
		builderContext.setAttribute( EntityPropertyControlName.class, controlName.withInitializedValue() );

		return bootstrap.builders.script().attribute( "type", MediaType.TEXT_HTML )
		                         .data( ROLE, "edit-item-template" )
		                         .data( "next-item-index", System.currentTimeMillis() )
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
	                                  List<EntityPropertyBinder> items,
	                                  String removeItemMessage ) {
		NodeViewElement itemRows = new NodeViewElement( "div" );
		itemRows.setAttribute( "data-role", "items" );
		itemRows.addCssClass( "embedded-collection-items" );

		int lastIndex = items.size() - 1;
		for ( int i = 0; i < items.size(); i++ ) {
			EntityPropertyBinder item = items.get( i );
			EntityPropertyControlName.ForProperty.BinderProperty itemControlName = controlName.asCollectionItem()
			                                                                                  .withIndex( i )
			                                                                                  .withBinderItemKey( item.getItemKey() )
			                                                                                  .asBinderItem();

			IteratorItemStats<Object> itemStats = new IteratorItemStatsImpl<>( item.getValue(), i, i == lastIndex );
			IteratorViewElementBuilderContext itemContext = new IteratorViewElementBuilderContext<>( itemStats );
			itemContext.setAttribute( EntityPropertyControlName.class, itemControlName.withInitializedValue() );
			itemContext.setAttribute( EntityPropertyBinder.class, item );
			itemContext.setParentContext( builderContext );

			itemRows.addChild(
					createItemRowBuilder( itemControlName, item.getItemKey(), item.getSortIndex(), removeItemMessage ).build( itemContext )
			);
		}

		return itemRows;
	}

	private NodeViewElementBuilder createItemRowBuilder( EntityPropertyControlName.ForProperty.BinderProperty propertyControlName,
	                                                     String itemKey,
	                                                     long sortIndex,
	                                                     String removeItemMessage ) {
		return html.builders.div()
		                    .data( ROLE, "item" )
		                    .data( "item-key", itemKey )
		                    .with( css.display.flex, css.border.top, css.padding.top.s2 )
		                    .css( "embedded-collection-item" )
		                    .add(
				                    sortable && !readonly ?
						                    html.builders.div()
						                                 .name( "itemHandle" )
						                                 .data( ROLE, "item-handle" )
						                                 .css( "embedded-collection-item-handle" )
						                                 .add( entityModuleIcons.embeddedCollection.itemHandle() )
						                    : null
		                    )
		                    .add(
				                    html.builders.div()
				                                 .name( "itemData" )
				                                 .data( ROLE, "item-data" )
				                                 .css( "embedded-collection-item-data" )
				                                 .with( css.flex.grow.enabled, css.margin.right.s2 )
				                                 .add( itemTemplate )
				                                 .add(
						                                 !readonly ?
								                                 bootstrap.builders.hidden()
								                                                   .controlName( propertyControlName.toSortIndex() )
								                                                   .value( sortIndex )
								                                 : null
				                                 )
		                    )
		                    .add(
				                    enableRemovingItem && !readonly ?
						                    html.builders.div()
						                                 .name( "itemActions" )
						                                 .data( ROLE, "item-actions" )
						                                 .css( "embedded-collection-item-actions" )
						                                 .add(
								                                 bootstrap.builders.link()
								                                                   .data( ACTION, "remove-item" )
								                                                   .title( removeItemMessage )
								                                                   .add( entityModuleIcons.embeddedCollection.removeItem() )
						                                 )
						                    : null
		                    )
		                    .postProcessor( ( builderContext, element ) -> {
			                    List<FormGroupElement> formGroups = element.findAll( FormGroupElement.class ).collect( Collectors.toList() );

			                    if ( formGroups.size() == 1 ) {
				                    element.addCssClass( "embedded-collection-item-style-compact" );
				                    formGroups.get( 0 ).setLabel( null );
			                    }
		                    } );
	}

	private NodeViewElement addItemAction( ViewElementBuilderContext builderContext, String addItemMessage ) {
		return html.builders.div()
		                    .data( ROLE, "actions" )
		                    .css( "embedded-collection-actions" )
		                    .add(
				                    bootstrap.builders.button()
				                                      .data( ACTION, "add-item" )
				                                      .style( Style.DEFAULT )
				                                      .with( css.button.outline.secondary, css.size.width100 )
				                                      .iconLeft()
				                                      .icon( entityModuleIcons.embeddedCollection.addItem() )
				                                      .title( addItemMessage )
				                                      .text( StringUtils.isEmpty( addItemMessage ) ? "" : " " + addItemMessage )

		                    )
		                    .build( builderContext );
	}
}
