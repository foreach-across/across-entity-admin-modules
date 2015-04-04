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

import com.foreach.across.modules.bootstrapui.elements.LabelFormElement;
import com.foreach.across.modules.bootstrapui.elements.builder.FormGroupElementBuilder;
import com.foreach.across.modules.bootstrapui.elements.builder.LabelFormElementBuilder;
import com.foreach.across.modules.entity.newviews.EntityViewElementBuilderFactorySupport;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;

/**
 * @author Arne Vandamme
 */
public abstract class FormGroupElementBuilderFactorySupport extends EntityViewElementBuilderFactorySupport<FormGroupElementBuilder>
{
	public FormGroupElementBuilderFactorySupport() {
		addProcessor( new FormGroupRequiredBuilderProcessor() );
	}

	@Override
	protected FormGroupElementBuilder createInitialBuilder( EntityPropertyDescriptor propertyDescriptor,
	                                                        EntityPropertyRegistry entityPropertyRegistry,
	                                                        EntityConfiguration entityConfiguration ) {
		FormGroupElementBuilder group = new FormGroupElementBuilder();

		LabelFormElementBuilder label = new LabelFormElementBuilder()
				.text( propertyDescriptor.getDisplayName() )
				.postProcessor( labelCodeResolver( propertyDescriptor, entityConfiguration ) );

		ViewElementBuilder controlBuilder = createControlBuilder( propertyDescriptor, entityPropertyRegistry,
		                                                          entityConfiguration );

		return group.label( label ).control( controlBuilder );
	}

	protected abstract ViewElementBuilder createControlBuilder( EntityPropertyDescriptor propertyDescriptor,
	                                                            EntityPropertyRegistry entityPropertyRegistry,
	                                                            EntityConfiguration entityConfiguration );

	protected ViewElementPostProcessor<LabelFormElement> labelCodeResolver( EntityPropertyDescriptor propertyDescriptor,
	                                                                        EntityConfiguration entityConfiguration ) {
		return new LabelCodeResolverPostProcessor( "properties." + propertyDescriptor.getName(),
		                                           entityConfiguration.getEntityMessageCodeResolver() );
	}
}
