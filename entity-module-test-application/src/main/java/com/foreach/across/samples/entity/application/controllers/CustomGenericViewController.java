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
import com.foreach.across.modules.adminweb.ui.PageContentStructure;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.web.EntityViewModel;
import com.foreach.across.samples.entity.application.business.User;
import com.foreach.across.samples.entity.modules.config.EntityViewContextParams;
import com.foreach.across.samples.entity.modules.config.EntityViewController;
import com.foreach.across.samples.entity.modules.config.EntityViewEntityViewControllerSupport;
import com.foreach.across.samples.entity.modules.utils.RequestUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@AdminWebController
@EntityViewController()
@RequestMapping("/users")
@RequiredArgsConstructor
public class CustomGenericViewController implements EntityViewEntityViewControllerSupport
{
	private final ConversionService conversionService;

	@GetMapping
//	@ViewProxy(viewFactory = "createView")
	public Object get(
			EntityViewRequest entityViewRequest,
			@NonNull @ModelAttribute(EntityViewModel.VIEW_COMMAND) EntityViewCommand command,
			BindingResult bindingResult
	) {
		entityViewRequest.setBindingResult( bindingResult );
		EntityView entityView = entityViewRequest.getViewFactory().createView( entityViewRequest );
		return StringUtils.defaultString( entityView.getTemplate(), PageContentStructure.TEMPLATE );
	}

	@Override
	public EntityViewContextParams resolveEntityViewContextParams( HttpServletRequest httpServletRequest ) {
		Optional<User> resolvedUser = RequestUtils.getPathVariable( "userId" )
		                                          .map( userId -> conversionService.convert( userId, User.class ) );
		return EntityViewContextParams.builder()
		                              .configurationName( "user" )
		                              .instance( resolvedUser.orElse( null ) )
		                              .build();
	}

	@Override
	public String resolveViewName( HttpServletRequest httpServletRequest, EntityViewContext entityViewContext ) {
		if ( entityViewContext.holdsEntity() ) {
			return "updateView";
		}
		return "listView";
	}
}
