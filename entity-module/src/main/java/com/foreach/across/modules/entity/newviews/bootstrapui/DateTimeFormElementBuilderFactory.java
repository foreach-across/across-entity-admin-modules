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
import com.foreach.across.modules.bootstrapui.elements.builder.DateTimeFormElementBuilder;
import com.foreach.across.modules.bootstrapui.elements.builder.InputGroupFormElementBuilder;
import com.foreach.across.modules.entity.newviews.EntityViewElementBuilderFactorySupport;
import com.foreach.across.modules.entity.newviews.ViewElementMode;
import com.foreach.across.modules.entity.newviews.bootstrapui.TextboxFormElementBuilderFactory.TextboxPlaceholderProcessor;
import com.foreach.across.modules.entity.newviews.bootstrapui.processors.element.EntityPropertyControlPostProcessor;
import com.foreach.across.modules.entity.newviews.util.EntityViewElementUtils;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Arne Vandamme
 */
public class DateTimeFormElementBuilderFactory extends EntityViewElementBuilderFactorySupport<InputGroupFormElementBuilder>
{
	@Autowired
	private ConversionService conversionService;

	@Autowired
	private BootstrapUiFactory bootstrapUi;

	public DateTimeFormElementBuilderFactory() {
		//addProcessor( new FormControlRequiredBuilderProcessor<DateTimeFormElementBuilder>() );
	}

	@Override
	public boolean supports( String viewElementType ) {
		return BootstrapUiElements.DATETIME.equals( viewElementType );
	}

	@Override
	protected InputGroupFormElementBuilder createInitialBuilder( EntityPropertyDescriptor propertyDescriptor,
	                                                             EntityPropertyRegistry entityPropertyRegistry,
	                                                             EntityConfiguration entityConfiguration,
	                                                             ViewElementMode viewElementMode ) {

		return new DateTimeFormElementBuilder()
				.name( propertyDescriptor.getName() )
				.control(
						bootstrapUi.textbox()
						           .controlName( propertyDescriptor.getName() )
						           .postProcessor(
								           ( builderContext, textbox ) ->
								           {
									           Object entity = EntityViewElementUtils.currentEntity( builderContext );
									           ValueFetcher valueFetcher = propertyDescriptor.getValueFetcher();

									           if ( entity != null && valueFetcher != null ) {
										           Date propertyValue = (Date) valueFetcher.getValue( entity );

										           if ( propertyValue == null ) {
											           propertyValue = new Date();
										           }

										           textbox.setText(
												           new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSZ" )
														           .format( propertyValue )
										           );
									           }
								           }
						           )
						           .postProcessor( new EntityPropertyControlPostProcessor<>() )
						           .postProcessor( new TextboxPlaceholderProcessor( propertyDescriptor ) )
				);
	}
}
