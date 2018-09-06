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
import com.foreach.across.modules.bootstrapui.elements.FieldsetFormElement;
import com.foreach.across.modules.bootstrapui.elements.FormGroupElement;
import com.foreach.across.modules.bootstrapui.elements.HiddenFormElement;
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

/**
 * @author Arne Vandamme
 * @since 3.2.0
 */
// todo: better naming
@Accessors(chain = true, fluent = true)
public class EmbeddedCollectionViewElementBuilder extends NodeViewElementBuilder
{
	@Setter
	private ViewElementBuilder itemTemplate;

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
		NodeViewElement node = super.createElement( builderContext );
		node.addCssClass( "js-embedded-collection-form-group" );
		//node.setAttribute( "data-source-property", EntityAttributes.controlName( descriptor ) );
		//node.setAttribute( "data-target-property", EntityAttributes.controlName( memberDescriptor ) );

		String controlPrefix = StringUtils.removeEnd( EntityAttributes.controlName( EntityViewElementUtils.currentPropertyDescriptor( builderContext ) ),
		                                              ".value" );

		node.setAttribute( "data-item-format", controlPrefix + ".items[{{key}}]" );
		/*
		   class="form-group js-embedded-collection-form-group"
     data-source-property="entity.phones"
     data-target-property="extensions[EmbeddedCollections][entity.phones]">
		 */
		node.addChild( TextViewElement.html( "<a style=\"float: right\" data-action=\"add-item\"><span class=\"glyphicon glyphicon-plus-sign\"></span></a>" ) );

		FieldsetFormElement fieldset = new FieldsetFormElement();
		fieldset.setAttribute( "data-role", "items" );

		Map<String, EntityPropertyBinder<Object>> items = binder.getItems();

		int position = 0;
		int total = items.size();

		for ( Map.Entry<String, EntityPropertyBinder<Object>> entry : items.entrySet() ) {
			IteratorItemStats<Object> itemStats = new IteratorItemStatsImpl<>( entry.getValue().getValue(), position, position < total );
			IteratorViewElementBuilderContext ctx = new IteratorViewElementBuilderContext<>( itemStats );
			ctx.setParentContext( builderContext );

			NodeViewElement itemWrapper = new NodeViewElement( "div" );
			itemWrapper.setAttribute( "data-role", "item" );
			itemWrapper.setAttribute( "data-item-id", entry.getKey() );
			itemWrapper.addChild( TextViewElement
					                      .html( "<a data-action=\"remove-item\" role=\"button\" class=\"btn btn-link\" title=\"Remove\" href=\"#\"><span aria-hidden=\"true\" class=\"glyphicon glyphicon-remove\"></span></a>" ) );

			ViewElement singleItemControl = itemTemplate.build( ctx );
			new ControlNamePrefixAdjuster<>()
					.prefixToReplace( controlPrefix + ".items[]" )
					.prefixToAdd( controlPrefix + ".items[" + entry.getKey() + "]" )
					.accept( singleItemControl );

			HiddenFormElement sortIndex = new HiddenFormElement();
			sortIndex.setControlName( controlPrefix + ".items[" + entry.getKey() + "].sortIndex" );
			sortIndex.setValue( entry.getValue().getSortIndex() );
			itemWrapper.addChild( singleItemControl );
			itemWrapper.addChild( sortIndex );

			fieldset.addChild( itemWrapper );

		}

		node.addChild( fieldset );

		ViewElementBuilderContext bc = new DefaultViewElementBuilderContext( builderContext );
		EntityViewElementUtils.setCurrentEntity( bc, null );
		bc.setAttribute( EntityPropertyControlNamePostProcessor.PREFIX_CONTROL_NAMES, false );

		String baseControlName = controlPrefix;//EntityAttributes.controlName( memberDescriptor );

		String templateControlName = StringUtils.removeEnd( baseControlName, "items[].value" ) + "template";

		node.addChild(
				BootstrapUiBuilders.hidden()
				                   .controlName( controlPrefix + ".bound" )
				                   .value( "1" )
				                   .build( bc )
		);
		node.addChild(
				BootstrapUiBuilders.node( "script" )
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

		return node;
	}
}
