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
package com.foreach.across.modules.entity.config.entities;

import com.foreach.across.core.annotations.OrderInModule;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;

/**
 * Configures some basic settings for {@link com.foreach.across.modules.hibernate.repositories.Undeletable} entities.
 *
 * @author Arne Vandamme
 */
//@Configuration
@OrderInModule(3)
public class UndeletableEntityUiConfiguration implements EntityConfigurer
{
	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
//		entities.assignableTo( Undeletable.class )
//		        .updateFormView(
//				        fvb -> fvb.viewProcessor( new WebViewProcessorAdapter()
//				        {
//					        @Override
//					        protected void extendViewModel( EntityView view ) {
//						        Undeletable undeletable = view.getEntity();
//
//						        if ( undeletable.isDeleted() ) {
//							        NodeViewElement message = new NodeViewElement( "div" );
//							        message.addCssClass( "alert", "alert-danger" );
//							        message.addChild( new TextViewElement( "You are watching a deleted entity." ) );
//
//							        view.getViewElements().addFirstChild( message );
//						        }
//						        //view.getViewElements().addFirst(  );
//					        }
//				        } )
//		        );
	}
}
