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

package com.foreach.across.modules.entity.views.processors;

import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.Objects;

/**
 * Supports creating a {@link org.springframework.data.domain.Pageable} from request parameters and assigning it to a specific extension attribute.
 * Allows setting a default pageable that will be used if not all request parameters are present.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public final class PageableExtensionViewProcessor extends SimpleEntityViewProcessorAdapter
{
	/**
	 * Default extension name.
	 */
	public static final String DEFAULT_EXTENSION_NAME = "pageable";

	private final SortHandlerMethodArgumentResolver sortResolver = new SortHandlerMethodArgumentResolver();
	private final PageableHandlerMethodArgumentResolver pageableResolver = new PageableHandlerMethodArgumentResolver( sortResolver );

	// Dummy method parameter required for the PageableHandlerMethodArgumentResolver
	private static final MethodParameter METHOD_PARAMETER
			= MethodParameter.forMethodOrConstructor( ReflectionUtils.findMethod( MethodParameterContainer.class, "dummyMethod", Object.class ), 0 );

	@SuppressWarnings("all")
	private static class MethodParameterContainer
	{
		public void dummyMethod( Object parameter ) {
		}
	}

	/**
	 * Name under which the {@link org.springframework.data.domain.Pageable} should be registered in {@link EntityViewCommand#getExtensions()}.
	 */
	@Setter
	private String extensionName = DEFAULT_EXTENSION_NAME;

	@Getter
	private Pageable defaultPageable = new PageRequest( 0, 20 );

	private String prefix;

	/**
	 * Set the default {@link Pageable} that should be used if no parameters specified on the request.
	 */
	public void setDefaultPageable( Pageable defaultPageable ) {
		this.defaultPageable = defaultPageable;
		pageableResolver.setFallbackPageable( defaultPageable );
	}

	/**
	 * Set the prefix for all page related request parameter names.
	 */
	public void setRequestParameterPrefix( String requestParameterPrefix ) {
		prefix = StringUtils.defaultString( requestParameterPrefix );

		pageableResolver.setPrefix( prefix );
		sortResolver.setSortParameter( prefix + "sort" );
	}

	@Override
	public void initializeCommandObject( EntityViewRequest entityViewRequest, EntityViewCommand command, WebDataBinder dataBinder ) {
		NativeWebRequest webRequest = entityViewRequest.getWebRequest();

		Pageable pageable = pageableResolver.resolveArgument( METHOD_PARAMETER, null, webRequest, null );
		command.addExtension( extensionName, pageable );
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		PageableExtensionViewProcessor that = (PageableExtensionViewProcessor) o;
		return Objects.equals( extensionName, that.extensionName ) &&
				Objects.equals( defaultPageable, that.defaultPageable ) &&
				Objects.equals( prefix, that.prefix );
	}

	@Override
	public int hashCode() {
		return Objects.hash( extensionName, defaultPageable, prefix );
	}
}
