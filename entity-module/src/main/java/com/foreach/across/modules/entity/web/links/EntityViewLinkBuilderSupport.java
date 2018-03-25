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

package com.foreach.across.modules.entity.web.links;

import com.foreach.across.modules.web.template.WebTemplateInterceptor;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Base class for entity link builder implementations.
 * Simply call {@link #toString()} to get the URI {@code String} representation.
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
public class EntityViewLinkBuilderSupport<T extends EntityViewLinkBuilderSupport<T>>
{
	private final UriComponentsBuilder uriComponents;
	protected final EntityViewLinks links;

	public EntityViewLinkBuilderSupport( UriComponentsBuilder uriComponents, EntityViewLinks links ) {
		this.uriComponents = uriComponents;
		this.links = links;
	}

	/**
	 * Add a path segment to the URI.
	 *
	 * @param path segment to add
	 * @return new link builder instance
	 */
	public T slash( String path ) {
		return cloneLinkBuilder( toUriComponentsBuilder().pathSegment( path ) );
	}

	/**
	 * Add a {@code _partial} query string parameter to the URI.
	 *
	 * @param fragment partial fragment identifier
	 * @return new link builder instance
	 */
	public T withPartial( String fragment ) {
		return withQueryParam( WebTemplateInterceptor.PARTIAL_PARAMETER, fragment );
	}

	/**
	 * Add a {@code from} query string parameter to the URI.
	 *
	 * @param url original from url to set
	 * @return new link builder instance
	 */
	public T withFromUrl( String url ) {
		return withQueryParam( "from", url );
	}

	/**
	 * Add a {@code view} query string parameter to the URI.
	 * Note: This will not perform any checking if that view actually exists.
	 *
	 * @param viewName name of the view
	 * @return new link builder instance
	 */
	public T withViewName( String viewName ) {
		return withQueryParam( "view", viewName );
	}

	/**
	 * Add a query parameter to the URI.
	 * This will replace a previously set value.
	 * Passing a single {@code null} value is the same as passing no values and will
	 * remove the query string parameter.
	 *
	 * @param name   of the query string parameter
	 * @param values for the parameter name
	 * @return new link builder instance
	 */
	public T withQueryParam( String name, Object... values ) {
		if ( values.length == 1 && values[0] == null ) {
			return cloneLinkBuilder( toUriComponentsBuilder().replaceQueryParam( name ) );
		}
		return cloneLinkBuilder( toUriComponentsBuilder().replaceQueryParam( name, values ) );
	}

	/**
	 * Return the root {@link EntityViewLinks} to link to a different entity type.
	 *
	 * @return links component
	 */
	public EntityViewLinks root() {
		return links;
	}

	/**
	 * @return a new URI components builder for the currently configured URI
	 */
	public UriComponentsBuilder toUriComponentsBuilder() {
		return uriComponents.cloneBuilder();
	}

	/**
	 * @return a URI components builder for the currently configured URI
	 */
	public UriComponents toUriComponents() {
		return uriComponents.build();
	}

	/**
	 * @return URI version
	 */
	public URI toUri() {
		return uriComponents.build().encode().toUri().normalize();
	}

	/**
	 * Create a URI string of the configured link, this will resolve
	 * any path prefixes and will optionally encode the URL.
	 *
	 * @return string version of the URI, resolved version of {@link #toString()}
	 */
	public String toUriString() {
		return links.buildUriLink( uriComponents.build().toString() );
	}

	@Override
	public final String toString() {
		return uriComponents.build().toString();
	}

	@SuppressWarnings("unchecked")
	protected T cloneLinkBuilder( UriComponentsBuilder uriComponents ) {
		return (T) new EntityViewLinkBuilderSupport( uriComponents, links );
	}
}
