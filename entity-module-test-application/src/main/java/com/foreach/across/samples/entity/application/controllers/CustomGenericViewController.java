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

import com.foreach.across.modules.adminweb.annotations.AdminWebController;
import com.foreach.across.modules.adminweb.menu.AdminMenuEvent;
import com.foreach.across.modules.adminweb.ui.PageContentStructure;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.web.EntityViewModel;
import com.foreach.across.modules.web.context.WebAppPathResolver;
import com.foreach.across.samples.entity.modules.config.EntityAssociationInstanceResolver;
import com.foreach.across.samples.entity.modules.config.EntityViewController;
import com.foreach.across.samples.entity.modules.config.EntityViewEntityViewControllerSupport;
import com.foreach.across.samples.entity.modules.config.ViewFactory;
import com.foreach.across.samples.entity.modules.utils.RequestUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@AdminWebController
@EntityViewController(target = "user", entityIdMappedBy = "userId")
@RequestMapping("/users")
@RequiredArgsConstructor
public class CustomGenericViewController implements EntityViewEntityViewControllerSupport
{
	private final WebAppPathResolver webAppPathResolver;

	@EventListener
	public void registerAdminMenu( AdminMenuEvent adminMenu ) {
		//TODO: not having a menu will NPE, might fix this in AdminMenu.breadcrumbLeaf()
		adminMenu.builder()
		         .group( "/customControllers", "Custom Controllers" ).and()
		         .item( "/customControllers/users", "Custom Generic User", "/users" );
	}

	@GetMapping("")
	@ViewFactory(view = EntityView.LIST_VIEW_NAME)
	public Object list(
			EntityViewRequest entityViewRequest,
			@NonNull @ModelAttribute(EntityViewModel.VIEW_COMMAND) EntityViewCommand command,
			BindingResult bindingResult
	) {
		entityViewRequest.setBindingResult( bindingResult );
		EntityView entityView = entityViewRequest.getViewFactory().createView( entityViewRequest );
		return StringUtils.defaultString( entityView.getTemplate(), PageContentStructure.TEMPLATE );
	}

	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	@ViewFactory(view = EntityView.UPDATE_VIEW_NAME)
	public Object updateWithRequestParam(
			EntityViewRequest entityViewRequest,
			@NonNull @ModelAttribute(EntityViewModel.VIEW_COMMAND) EntityViewCommand command,
			BindingResult bindingResult
	) {
		entityViewRequest.setBindingResult( bindingResult );
		EntityView entityView = entityViewRequest.getViewFactory().createView( entityViewRequest );
		return StringUtils.defaultString( entityView.getTemplate(), PageContentStructure.TEMPLATE );
	}

	@RequestMapping(value = "/{userId}", method = { RequestMethod.GET, RequestMethod.POST })
	@ViewFactory(view = EntityView.UPDATE_VIEW_NAME)
	public Object update(
			EntityViewRequest entityViewRequest,
			@NonNull @ModelAttribute(EntityViewModel.VIEW_COMMAND) EntityViewCommand command,
			BindingResult bindingResult
	) {
		entityViewRequest.setBindingResult( bindingResult );
		EntityView entityView = entityViewRequest.getViewFactory().createView( entityViewRequest );

		//TODO: should go in a more generic place
		if ( entityView.isRedirect() ) {
			return webAppPathResolver.redirect( entityView.getRedirectUrl() );
		}
		else if ( entityView.isCustomView() ) {
			return entityView.getCustomView();
		}

		return StringUtils.defaultString( entityView.getTemplate(), PageContentStructure.TEMPLATE );
	}

	@RequestMapping(value = "/{userId}/friends", method = { RequestMethod.GET, RequestMethod.POST })
	@ViewFactory(view = EntityView.LIST_VIEW_NAME, association = "friend.users")
	public Object listFriends(
			EntityViewRequest entityViewRequest,
			@NonNull @ModelAttribute(EntityViewModel.VIEW_COMMAND) EntityViewCommand command,
			BindingResult bindingResult
	) {
		entityViewRequest.setBindingResult( bindingResult );
		EntityView entityView = entityViewRequest.getViewFactory().createView( entityViewRequest );

		//TODO: should go in a more generic place
		if ( entityView.isRedirect() ) {
			return webAppPathResolver.redirect( entityView.getRedirectUrl() );
		}
		else if ( entityView.isCustomView() ) {
			return entityView.getCustomView();
		}

		return StringUtils.defaultString( entityView.getTemplate(), PageContentStructure.TEMPLATE );
	}

	@RequestMapping(value = "/{userId}/friends/{friendId}", method = { RequestMethod.GET, RequestMethod.POST })
	@ViewFactory(view = EntityView.UPDATE_VIEW_NAME, association = "friend.users")
	public Object updateFriend(
			EntityViewRequest entityViewRequest,
			@NonNull @ModelAttribute(EntityViewModel.VIEW_COMMAND) EntityViewCommand command,
			BindingResult bindingResult
	) {
		entityViewRequest.setBindingResult( bindingResult );
		EntityView entityView = entityViewRequest.getViewFactory().createView( entityViewRequest );

		//TODO: should go in a more generic place
		if ( entityView.isRedirect() ) {
			return webAppPathResolver.redirect( entityView.getRedirectUrl() );
		}
		else if ( entityView.isCustomView() ) {
			return entityView.getCustomView();
		}

		return StringUtils.defaultString( entityView.getTemplate(), PageContentStructure.TEMPLATE );
	}

	@EntityAssociationInstanceResolver
	public Object resolveFriendId( HttpServletRequest httpServletRequest ) {
		return RequestUtils.getPathVariable( "friendId" ).get();

	}
}
