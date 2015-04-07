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
package com.foreach.across.modules.entity.newviews.bootstrapui;

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiElements;
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiFactory;
import com.foreach.across.modules.bootstrapui.elements.builder.FormGroupElementBuilder;
import com.foreach.across.modules.bootstrapui.elements.builder.LabelFormElementBuilder;
import com.foreach.across.modules.entity.newviews.EntityViewElementBuilderFactorySupport;
import com.foreach.across.modules.entity.newviews.EntityViewElementBuilderService;
import com.foreach.across.modules.entity.newviews.ViewElementMode;
import com.foreach.across.modules.entity.newviews.bootstrapui.processors.builder.FormGroupRequiredBuilderProcessor;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Arne Vandamme
 */
public class FormGroupElementBuilderFactory extends EntityViewElementBuilderFactorySupport<FormGroupElementBuilder>
{
	@Autowired
	private EntityViewElementBuilderService entityViewElementBuilderService;

	@Autowired
	private BootstrapUiFactory bootstrapUi;

	public FormGroupElementBuilderFactory() {
		addProcessor( new FormGroupRequiredBuilderProcessor() );
	}

	@Override
	public boolean supports( String viewElementType ) {
		return BootstrapUiElements.FORM_GROUP.equals( viewElementType );
	}

	@Override
	protected FormGroupElementBuilder createInitialBuilder( EntityPropertyDescriptor propertyDescriptor,
	                                                        EntityPropertyRegistry entityPropertyRegistry,
	                                                        EntityConfiguration entityConfiguration,
	                                                        ViewElementMode viewElementMode ) {
		ViewElementBuilder labelText = entityViewElementBuilderService.getElementBuilder(
				entityConfiguration, propertyDescriptor, ViewElementMode.LABEL
		);

		LabelFormElementBuilder labelBuilder = bootstrapUi.label().add( labelText );

		ViewElementMode controlMode = ViewElementMode.CONTROL;

		if ( !propertyDescriptor.isWritable() || ViewElementMode.FORM_READ.equals( viewElementMode ) ) {
			controlMode = ViewElementMode.VALUE;
		}

		ViewElementBuilder controlBuilder = entityViewElementBuilderService.getElementBuilder(
				entityConfiguration, propertyDescriptor, controlMode
		);

		return bootstrapUi.formGroup( labelBuilder, controlBuilder );
	}
}
