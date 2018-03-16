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

package com.foreach.across.samples.entity.application.config;

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders;
import com.foreach.across.modules.bootstrapui.elements.SelectFormElement;
import com.foreach.across.modules.bootstrapui.elements.SelectFormElementConfiguration;
import com.foreach.across.modules.bootstrapui.elements.builder.OptionsFormElementBuilder;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.processors.EntityViewProcessorAdapter;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;

import java.util.Optional;

import static com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils.find;

/**
 * @author Steven Gentens
 */
public class BooleanDummyListViewConfiguration extends EntityViewProcessorAdapter
{
	@Override
	protected void postRender( EntityViewRequest entityViewRequest,
	                           EntityView entityView,
	                           ContainerViewElement container,
	                           ViewElementBuilderContext builderContext ) {
		Optional<ContainerViewElement> header = find( container, "entityListForm-header", ContainerViewElement.class );
		header.ifPresent(
				h -> {
					OptionsFormElementBuilder selectFormElement = BootstrapUiBuilders.options().select( SelectFormElementConfiguration.simple() );
					SelectFormElement.OptionGroup group = new SelectFormElement.OptionGroup();
//					group.setLabel( "group label" );
					group.addChild( BootstrapUiBuilders.option().label( "group - one" ).value( null ).rawValue( null ).build( builderContext ) );
					group.addChild( BootstrapUiBuilders.option().label( "group - two" ).value( null ).rawValue( null ).build( builderContext ) );
					selectFormElement.add( BootstrapUiBuilders.option().label( "one" ).value( null ).rawValue( null ) );
					selectFormElement.add( group );
					selectFormElement.add( BootstrapUiBuilders.option().label( "two" ).value( null ).rawValue( null ) );
					h.addFirstChild( selectFormElement.build( builderContext ) );
				}
		);
	}
}
