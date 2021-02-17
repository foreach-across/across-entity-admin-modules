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

package com.foreach.across.samples.entity.application.controllers;

import com.foreach.across.modules.adminweb.ui.PageContentStructure;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.web.EntityViewModel;
import com.foreach.across.samples.entity.modules.config.EntityViewController;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@EntityViewController(target = "user"/*, targetType = Library.class, autoRegister = true*/)
@RequestMapping("helloworld")
public class CustomViewController
{

	@GetMapping
	public ResponseEntity<String> get( EntityViewRequest entityViewRequest,
	                                   @NonNull @ModelAttribute(EntityViewModel.VIEW_COMMAND) EntityViewCommand command,
	                                   BindingResult bindingResult ) {
		return ResponseEntity.ok( "hello world!" );
	}

	@GetMapping("/yada")
	public Object test( EntityViewRequest entityViewRequest,
	                    @NonNull @ModelAttribute(EntityViewModel.VIEW_COMMAND) EntityViewCommand command,
	                    BindingResult bindingResult ) {
		entityViewRequest.setBindingResult( bindingResult );

		EntityView entityView = entityViewRequest.getViewFactory().createView( entityViewRequest );

		return StringUtils.defaultString( entityView.getTemplate(), PageContentStructure.TEMPLATE );
	}

//	private Validator entityValidator = null;
//	private EntityViewElementBuilderHelper viewElementBuilderHelper;
//
//	public void get( @PathVariable(name = "id", required = false) Long id ) {
//		Entity myEntity = new Entity();
//
//		if ( Objects.nonNull( id ) ) {
//			myEntity = findByEntityId( id );
//		}
//
//		// inherit from base generic controller?
//		// base controller supports customizing ... after base setup
//		// reuse components created in base controller?
//		// multiple inheritance?
//
//		// forEntity / forEntityType
//		ViewElement entityView =
//				EntityViewComposer.forInstance(myEntity)
//				                  .component( MenuComponents.navigation() )
//				                  .component( FormViewComponents.formView() )
//				                  .compose();
//
//		EntityViewComposer.forInstance(myEntity)
//		                  .component(	                  );
//
//		EntityViewComposer.forType(Entity.class)
//		                  .provider( fetchByIdProvider(id) )
//		                  .provider( fetchByEqlProvider(eql) );
//
//
//		EntityViewComposer.forCollection(Entity.class);
//		EntityViewComposer.forCollectionOfType(Entity.class);
//
//
//		viewElementBuilderHelper.createSortableTableBuilder( Entity.class )
//		                        .propertyRegistry(  )
//		                        .setLabelViewElementMode(  ) // default modes
//		                        .setValueViewElementMode(  )
//		                        .properties(  ) // selection
//		.items(  )//list or page
//		.valueRowProcessor(  ) // support customization
//		                        .headerRowProcessor(  ) // support customization
//		.build(); // view element to use
//
//		EntityViewElementBatch<Entity> batch = viewElementBuilderHelper.createBatchForEntity( myEntity );
//		batch		                        .setPropertySelector(  ) 		;// select properties
//		batch.setViewElementMode(  );
//		batch.set
//	}
//
//	private Entity findByEntityId( Long id ) {
//		return new Entity();
//	}
//
////	@InitBinder
////	void initializeWebDataBinder( WebDataBinder webDataBinder ){
////		webDataBinder.addValidators( entityValidator );
////	}
//
//	public void post( @Valid Entity entity, BindingResult bindingResult ) {
//		// entityValidator.validate( entity, bindingResult );
//	}
//
//	class Entity
//	{
//
//	}
}
