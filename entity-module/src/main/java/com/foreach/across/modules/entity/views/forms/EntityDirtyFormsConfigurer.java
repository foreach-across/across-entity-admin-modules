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

package com.foreach.across.modules.entity.views.forms;

import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.registry.MutableEntityConfiguration;
import com.foreach.across.modules.entity.views.DispatchingEntityViewFactory;
import com.foreach.across.modules.entity.views.EntityViewFactory;
import com.foreach.across.modules.entity.views.processors.SaveEntityViewProcessor;
import com.foreach.across.modules.entity.views.processors.SingleEntityFormViewProcessor;
import com.foreach.across.modules.entity.views.processors.support.EntityViewProcessorRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.Arrays;
import java.util.function.Function;

@Configuration
@Order
public class EntityDirtyFormsConfigurer implements EntityConfigurer
{
	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		/*
		 todo configuration for activation:
		  - configure for all entities?
		  - disable for some entities?
		  - enable for some entities?
		 */
		entities.all()
		        .postProcessor(
				        mec -> {
					        Arrays.stream( mec.getViewNames() )
					              .map( mec::getViewFactory )
					              .filter( DispatchingEntityViewFactory.class::isInstance )
					              .map( DispatchingEntityViewFactory.class::cast )
					              .filter( devf -> devf.getProcessorRegistry().contains( SaveEntityViewProcessor.class.getName() ) )
					              .forEach( EntityDirtyFormsConfigurer::configureDirtyFormActivation );
				        }
		        );
	}

	public static void configureDirtyForms( MutableEntityConfiguration<Object> mec ) {
		configureViews( mec.getViewNames(), mec::getViewFactory );

		mec.getAssociations()
		   .forEach( ass -> configureViews( ass.getViewNames(), ass::getViewFactory ) );
	}

	private static void configureViews( String[] viewNames, Function<String, EntityViewFactory> viewFactoryMapping ) {
		Arrays.stream( viewNames )
		      .map( viewFactoryMapping )
		      .filter( DispatchingEntityViewFactory.class::isInstance )
		      .map( DispatchingEntityViewFactory.class::cast )
		      .filter( devf -> {
			      EntityViewProcessorRegistry processorRegistry = devf.getProcessorRegistry();
			      boolean containsSaveViewProcessor = processorRegistry.contains( SaveEntityViewProcessor.class.getName() );
			      boolean containsEntityForm = processorRegistry.contains( SingleEntityFormViewProcessor.class.getName() );
			      return containsSaveViewProcessor && containsEntityForm;
		      } )
		      .forEach( EntityDirtyFormsConfigurer::configureDirtyFormActivation );
	}

	private static void configureDirtyFormActivation( DispatchingEntityViewFactory dispatchingEntityViewFactory ) {
		dispatchingEntityViewFactory.getProcessorRegistry()
		                            .addProcessor( new EntityDirtyFormActivationViewProcessor() );
	}
}
