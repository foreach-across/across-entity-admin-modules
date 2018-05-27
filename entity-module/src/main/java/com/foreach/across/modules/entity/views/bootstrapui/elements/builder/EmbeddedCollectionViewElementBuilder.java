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
import com.foreach.across.modules.bootstrapui.utils.BootstrapElementUtils;
import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.EntityPropertyControlNamePostProcessor;
import com.foreach.across.modules.entity.views.processors.support.EmbeddedCollectionsBinder;
import com.foreach.across.modules.entity.views.processors.support.EntityPropertiesBinder;
import com.foreach.across.modules.entity.views.processors.support.EntityPropertyValueHolder;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementBuilder;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Arne Vandamme
 * @since 3.1.0
 */
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
		//Object collection = EntityViewElementUtils.currentPropertyValue( builderContext );

		EntityPropertyValueHolder holder = EntityViewElementUtils.currentPropertyValueHolder( builderContext );

		EntityPropertiesBinder.MultiValue multiValue = (EntityPropertiesBinder.MultiValue) holder;
		val collection = multiValue.getItemList();

		val descriptor = EntityViewElementUtils.currentPropertyDescriptor( builderContext );
		val collectionsBinder = builderContext.getAttribute( EmbeddedCollectionsBinder.class );

		val memberDescriptor = descriptor.getPropertyRegistry().getProperty( descriptor.getName() + EntityPropertyRegistry.INDEXER );

		val binderPrefix = buildBinderPrefix( collectionsBinder, descriptor );

		NodeViewElement node = super.createElement( builderContext );
		node.addCssClass( "js-embedded-collection-form-group" );
		node.setAttribute( "data-source-property", EntityAttributes.controlName( descriptor ) );
		node.setAttribute( "data-target-property", EntityAttributes.controlName( memberDescriptor ) );

		String controlPrefix = StringUtils.removeEnd( EntityAttributes.controlName( descriptor ), ".value" );

		/*
		   class="form-group js-embedded-collection-form-group"
     data-source-property="entity.phones"
     data-target-property="extensions[EmbeddedCollections][entity.phones]">
		 */
		node.addChild( TextViewElement.html( "<a style=\"float: right\" data-action=\"add-item\"><span class=\"glyphicon glyphicon-plus-sign\"></span></a>" ) );
		node.addChild(
				BootstrapUiBuilders.fieldset()
				                   .attribute( "data-role", "items" )
				                   .add(
						                   BootstrapUiBuilders.generator( EntityPropertiesBinder.MultiValue.Item.class, ContainerViewElement.class )
						                                      .creationCallback( ( item, container ) -> {

							                                      NodeViewElement itemWrapper = new NodeViewElement( "div" );
							                                      itemWrapper.setAttribute( "data-role", "item" );
							                                      itemWrapper.setAttribute( "data-item-id", item.getItem().getKey() );
							                                      itemWrapper.addChild( TextViewElement
									                                                            .html( "<a data-action=\"remove-item\" role=\"button\" class=\"btn btn-link\" title=\"Remove\" href=\"#\"><span aria-hidden=\"true\" class=\"glyphicon glyphicon-remove\"></span></a>" ) );

							                                      container.findAll( FormGroupElement.class )
							                                               .forEach( groupElement -> {
								                                               updateControlName( descriptor, item.getItem(), groupElement, controlPrefix );
							                                               } );

							                                      HiddenFormElement sortIndex = new HiddenFormElement();
							                                      sortIndex.setControlName(
									                                      controlPrefix + ".items[" + item.getItem().getKey() + "].sortIndex" );
							                                      sortIndex.setValue( item.getItem().getSortIndex() );
							                                      container.addChild( sortIndex );

							                                      itemWrapper.addChild( container );
							                                      return itemWrapper;
						                                      } )
						                                      .items( collection )
						                                      .itemBuilder( itemTemplate )
				                   )
				                   .build( builderContext )
		);

		ViewElementBuilderContext bc = new DefaultViewElementBuilderContext( builderContext );
		EntityViewElementUtils.setCurrentEntity( bc, null );
		bc.setAttribute( EntityPropertyControlNamePostProcessor.PREFIX_CONTROL_NAMES, false );

		String baseControlName = EntityAttributes.controlName( memberDescriptor );
		String templateControlName = StringUtils.removeEnd( baseControlName, "items[].value" ) + "template";

		node.addChild(
				BootstrapUiBuilders.hidden()
				                   .controlName( StringUtils.removeEnd( baseControlName, "items[].value" ) + "bound" )
				                   .value( "1" )
				                   .build( bc )
		);
		node.addChild(
				BootstrapUiBuilders.node( "script" )
				                   .attribute( "type", "text/html" )
				                   .attribute( "data-role", "edit-item-template" )
				                   .add( itemTemplate )
				                   .postProcessor( BootstrapElementUtils.replaceControlNamesPrefix( baseControlName, templateControlName ) )
				                   .build( bc )

		);

		return node;
	}

	private String buildBinderPrefix( EmbeddedCollectionsBinder binder, EntityPropertyDescriptor propertyDescriptor ) {
		return ( binder != null ? binder.getBinderPrefix() : "" ) + ".prop[" + propertyDescriptor.getName() + "]";
	}

	// todo use correct prefixing approach
	private void updateControlName( EntityPropertyDescriptor descriptor,
	                                EntityPropertiesBinder.MultiValue.Item item,
	                                FormGroupElement groupElement,
	                                String binderPrefix ) {
		val ctl = groupElement.getControl( FormControlElement.class );
		if ( ctl != null ) {
			ctl.setControlName( StringUtils.replaceOnce( ctl.getControlName(), binderPrefix + ".items[].value",
			                                             binderPrefix + ".items[" + item.getKey() + "].value" ) );
		}
		else if ( groupElement.getControl() instanceof ContainerViewElement ) {
			( (ContainerViewElement) groupElement.getControl() )
					.findAll( CheckboxFormElement.class )
					.forEach( cb -> cb.setControlName( "test" + "." + cb.getControlName() ) );

		}
	}
}
