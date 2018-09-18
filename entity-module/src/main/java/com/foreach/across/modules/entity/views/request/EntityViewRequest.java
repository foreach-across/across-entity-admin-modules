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

package com.foreach.across.modules.entity.views.request;

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.modules.adminweb.ui.PageContentStructure;
import com.foreach.across.modules.entity.conditionals.ConditionalOnAdminWeb;
import com.foreach.across.modules.entity.views.EntityViewFactory;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a specifically requested {@link com.foreach.across.modules.entity.views.EntityViewFactory} with its
 * full set of information:
 * <ul>
 * <li>the {@link com.foreach.across.modules.entity.views.context.EntityViewContext}</li>
 * <li>the {@link EntityViewCommand} and its {@link BindingResult}</li>
 * <li>the {@link org.springframework.web.context.request.NativeWebRequest} that requested the view</li>
 * <li>the {@link com.foreach.across.modules.adminweb.ui.PageContentStructure} being built and the template information</li>
 * </ul>
 * <p>
 * Data bean that is also registered as a request-scoped proxy.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Component
@Exposed
@Scope(scopeName = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Data
@ConditionalOnAdminWeb
public class EntityViewRequest
{
	/**
	 * The contextual information of the entity type and entity being viewed.
	 */
	private EntityViewContext entityViewContext;

	/**
	 * Page content being built.
	 */
	private PageContentStructure pageContentStructure;

	/**
	 * Command object for the view.
	 */
	private EntityViewCommand command;

	/**
	 * DataBinder being used for the command object.
	 */
	private WebDataBinder dataBinder;

	/**
	 * Binding result for the command object.
	 */
	private BindingResult bindingResult;

	/**
	 * Name of the view being requested.
	 */
	private String viewName;

	/**
	 * The {@link EntityViewFactory} being used.
	 */
	private EntityViewFactory viewFactory;

	/**
	 * The map of configuration attributes that were used to initialized this {@link EntityViewFactory}.
	 * Should never be {@code null} and can optionally be modified by early view processors to modify
	 * behaviour of later processors.
	 */
	private Map<String, Object> configurationAttributes = new HashMap<>();

	/**
	 * Original web request/response context.
	 */
	private NativeWebRequest webRequest;

	/**
	 * HTTP method for the web request.
	 */
	private HttpMethod httpMethod;

	/**
	 * The partial fragment to render.
	 */
	private String partialFragment;

	/**
	 * The model associated with the current view.
	 */
	private ModelMap model;

	/**
	 * The redirect attributes for the current view request.
	 */
	private RedirectAttributes redirectAttributes;

	/**
	 * @return {@code true} if only a partial fragment should be rendered of the view
	 */
	public boolean hasPartialFragment() {
		return partialFragment != null;
	}

	/**
	 * Checks whether the given name matches the view being rendered.
	 *
	 * @param viewName to check
	 * @return {@code true} if the name matches the current view name.
	 */
	public boolean isForView( String viewName ) {
		return StringUtils.equals( viewName, getViewName() );
	}

	@Override
	public String toString() {
		return "EntityViewRequest{" +
				"entityViewContext=" + entityViewContext +
				", viewName='" + viewName + '\'' +
				", httpMethod=" + httpMethod +
				'}';
	}
}
