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

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.util.EntityUtils;
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
public class PageableExtensionViewProcessor extends SimpleEntityViewProcessorAdapter
{
	public static final int DEFAULT_MAX_PAGE_SIZE = 2000;

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
	private Pageable defaultPageable = PageRequest.of( 0, 20 );

	/**
	 * Should the incoming pageable be translated using the current {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry}
	 * by calling {@link com.foreach.across.modules.entity.util.EntityUtils#translateSort(Pageable, EntityPropertyRegistry)}?
	 * <p/>
	 * This allows sorting to be specified using simple property and direction, but the actual property to sort on as well as null handling
	 * or case ordering to be determined by the <em>Sort.Order.class</em> attribute on the corresponding {@link EntityPropertyDescriptor}.
	 * <p/>
	 * Defaults to {@code true}.
	 */
	@Setter
	private boolean translatePageable = true;

	private String prefix;
	private int maxPageSize = DEFAULT_MAX_PAGE_SIZE;

	public PageableExtensionViewProcessor() {
		pageableResolver.setMaxPageSize( maxPageSize );
	}

	/**
	 * Set the default {@link Pageable} that should be used if no parameters specified on the request.
	 * If the assigned page size is larger than the {@link #maxPageSize}, the max size will automatically be updated.
	 */
	public void setDefaultPageable( Pageable defaultPageable ) {
		this.defaultPageable = defaultPageable;
		pageableResolver.setFallbackPageable( defaultPageable );

		if ( defaultPageable.getPageSize() > maxPageSize ) {
			setMaxPageSize( defaultPageable.getPageSize() );
		}
	}

	/**
	 * Configures the maximum page size to be accepted. This allows to put an upper boundary of the page size to prevent
	 * potential attacks trying to issue an {@link OutOfMemoryError}. Defaults to {@link #DEFAULT_MAX_PAGE_SIZE}.
	 *
	 * @param maxPageSize the maxPageSize to set
	 */
	public void setMaxPageSize( int maxPageSize ) {
		this.maxPageSize = maxPageSize;
		pageableResolver.setMaxPageSize( maxPageSize );
	}

	/**
	 * Set the request parameter prefix for all page related request parameter names.
	 * Defaults to empty.
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

		if ( translatePageable ) {
			pageable = translatePageable( pageable, entityViewRequest.getEntityViewContext().getPropertyRegistry() );
		}

		command.addExtension( extensionName, pageable );
	}

	protected Pageable translatePageable( Pageable pageable, EntityPropertyRegistry propertyRegistry ) {
		return EntityUtils.translateSort( pageable, propertyRegistry );
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
