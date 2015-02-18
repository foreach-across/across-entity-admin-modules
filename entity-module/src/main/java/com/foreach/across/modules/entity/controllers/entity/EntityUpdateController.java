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
package com.foreach.across.modules.entity.controllers.entity;

import com.foreach.across.modules.adminweb.AdminWeb;
import com.foreach.across.modules.adminweb.annotations.AdminWebController;
import com.foreach.across.modules.adminweb.menu.AdminMenu;
import com.foreach.across.modules.adminweb.menu.EntityAdminMenu;
import com.foreach.across.modules.entity.controllers.EntityViewRequest;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityModel;
import com.foreach.across.modules.entity.views.EntityFormView;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.web.EntityLinkBuilder;
import com.foreach.across.modules.web.menu.MenuFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.Serializable;

@AdminWebController
@RequestMapping(EntityUpdateController.PATH)
public class EntityUpdateController extends EntityControllerSupport
{
	public static final String PATH = EntityViewController.PATH + "/update";

	@Autowired
	private AdminWeb adminWeb;

	@Autowired
	private MenuFactory menuFactory;

	@Override
	protected String getDefaultViewName() {
		return EntityFormView.UPDATE_VIEW_NAME;
	}

	@ModelAttribute(VIEW_REQUEST)
	public Object buildViewRequest(
			@PathVariable(VAR_ENTITY) EntityConfiguration entityConfiguration,
			@PathVariable(VAR_ENTITY_ID) Serializable entityId,
			NativeWebRequest request,
			ModelMap model ) {
		return super.buildViewRequest( entityConfiguration, true, true, entityId, request, model );
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.POST)
	public String saveEntity(
			@PathVariable(VAR_ENTITY) EntityConfiguration entityConfiguration,
			@ModelAttribute(VIEW_REQUEST) @Valid EntityViewRequest viewRequest,
			BindingResult bindingResult,
			ModelMap model,
			AdminMenu adminMenu,
			RedirectAttributes redirectAttributes
	) {
		EntityModel entityModel = entityConfiguration.getEntityModel();

		if ( !bindingResult.hasErrors() ) {
			entityModel.save( viewRequest.getEntity() );

			redirectAttributes.addFlashAttribute( "successMessage", "feedback.entityUpdated" );

			return adminWeb.redirect( entityConfiguration.getAttribute( EntityLinkBuilder.class )
			                                             .update( viewRequest.getEntity() ) );
		}
		else {
			return showUpdateEntityForm( entityConfiguration, viewRequest, adminMenu, model );
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.GET)
	public String showUpdateEntityForm(
			@PathVariable(VAR_ENTITY) EntityConfiguration entityConfiguration,
			@ModelAttribute(VIEW_REQUEST) EntityViewRequest viewRequest,
			AdminMenu adminMenu,
			ModelMap model
	) {
		Object original = model.get( EntityFormView.ATTRIBUTE_ORIGINAL_ENTITY );
		adminMenu.breadcrumbLeaf( entityConfiguration.getLabel( original ) );

		EntityView view = viewRequest.createView( model );
		view.setEntityMenu(
				menuFactory.buildMenu( new EntityAdminMenu( entityConfiguration.getEntityType(),
				                                            original ) )
		);

		if ( view.getPageTitle() == null ) {
			view.setPageTitle(
					view.getEntityMessages().updatePageTitle( entityConfiguration.getLabel( original ) )
			);
		}

		return view.getTemplate();
	}

}
