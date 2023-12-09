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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Base class for entity link builder implementations.
 * Simply call {@link #toString()} to get the URI {@code String} representation.
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
public class EntityViewLinkBuilderSupport<T extends EntityViewLinkBuilderSupport<T>>
{
	private final DelegatingUriComponentsBuilders uriComponents;
	protected final EntityViewLinks links;

	public EntityViewLinkBuilderSupport( UriComponentsBuilder uriComponents, EntityViewLinks links ) {
		this.uriComponents =
				uriComponents instanceof DelegatingUriComponentsBuilders ? (DelegatingUriComponentsBuilders) uriComponents : new DelegatingUriComponentsBuilders(
						uriComponents );
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
		UriComponentsBuilder uriComponentsBuilder = toUriComponentsBuilder();
		if ( values.length == 1 && values[0] == null ) {
			if ( uriComponentsBuilder instanceof DelegatingUriComponentsBuilders ) {
				return cloneLinkBuilder( ( (DelegatingUriComponentsBuilders) uriComponentsBuilder ).setQueryParam( name ) );
			}
			else {
				return cloneLinkBuilder( uriComponentsBuilder.replaceQueryParam( name ) );
			}

		}
		if ( uriComponentsBuilder instanceof DelegatingUriComponentsBuilders ) {
			return cloneLinkBuilder( ( (DelegatingUriComponentsBuilders) uriComponentsBuilder ).setQueryParam( name, values ) );
		}
		else {
			return cloneLinkBuilder( toUriComponentsBuilder().replaceQueryParam( name, values ) );
		}
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

	private static class DelegatingUriComponentsBuilders extends UriComponentsBuilder
	{
		private final UriComponentsBuilder original;
		private final MultiValueMap<String, Object> queryParams = new LinkedMultiValueMap<>();

		public DelegatingUriComponentsBuilders( UriComponentsBuilder original ) {
			this.original = original.cloneBuilder();
		}

		@Override
		public UriComponentsBuilder queryParam( String name, Object... values ) {
			//queryParams.add( name, values );
			original.queryParam( name, values );
			return this;
		}

		@Override
		public UriComponentsBuilder queryParams( MultiValueMap<String, String> params ) {
			original.queryParams( params );
			return this;
		}

		@Override
		public UriComponentsBuilder replaceQueryParam( String name, Object... values ) {
			original.replaceQueryParam( name, values );
			return this;
		}

		@Override
		public UriComponentsBuilder replaceQueryParams( MultiValueMap<String, String> params ) {
			original.replaceQueryParams( params );
			return this;
		}

		@Override
		public UriComponentsBuilder encode( Charset charset ) {
			original.encode( charset );
			return this;
		}

		@Override
		public UriComponents build() {
			Map<String, Object> vars = buildUriVariables();
			return original.encode().build().expand( vars );
		}

		@Override
		public UriComponents build( boolean encoded ) {
			Map<String, Object> vars = buildUriVariables();
			return original.encode().build( false ).expand( vars );
		}

		@Override
		public UriComponents buildAndExpand( Object... uriVariableValues ) {
			Map<String, Object> vars = buildUriVariables();
			ArrayList<Object> objects = new ArrayList<>();
			objects.addAll( Arrays.asList( uriVariableValues ) );
			objects.addAll( vars.values() );
			return original.buildAndExpand( objects.toArray() );
		}

		@Override
		public URI build( Object... uriVariables ) {
			Map<String, Object> vars = buildUriVariables();
			ArrayList<Object> objects = new ArrayList<>();
			objects.addAll( Arrays.asList( uriVariables ) );
			objects.addAll( vars.values() );
			return original.build( objects.toArray() );
		}

		@Override
		public URI build( Map<String, ?> uriVariables ) {
			Map<String, Object> items = new HashMap<>( uriVariables );
			items.putAll( buildUriVariables() );
			return original.build( items );
		}

		@Override
		public UriComponents buildAndExpand( Map<String, ?> uriVariables ) {
			Map<String, Object> items = new HashMap<>( uriVariables );
			items.putAll( buildUriVariables() );
			return original.buildAndExpand( items );
		}

		private HashMap<String, Object> buildUriVariables() {
			int i = 0;
			HashMap<String, Object> entries = new LinkedHashMap<>();
			for ( Map.Entry<String, List<Object>> entry : queryParams.entrySet() ) {
				for ( Object ignore : entry.getValue() ) {
					String templateKey = entry.getKey() + i;
					original.queryParam( entry.getKey(), "{" + templateKey + "}" );
					entries.put( templateKey, ignore );
					i++;
				}
			}
			return entries;
		}

		@Override
		public String toUriString() {
			return original.toUriString();
		}

		@Override
		public UriComponentsBuilder uri( URI uri ) {
			original.uri( uri );
			return this;
		}

		@Override
		public UriComponentsBuilder uriComponents( UriComponents uriComponents ) {
			original.uriComponents( uriComponents );
			return this;
		}

		@Override
		public UriComponentsBuilder scheme( String scheme ) {
			original.scheme( scheme );
			return this;
		}

		@Override
		public UriComponentsBuilder schemeSpecificPart( String ssp ) {
			original.schemeSpecificPart( ssp );
			return this;
		}

		@Override
		public UriComponentsBuilder userInfo( String userInfo ) {
			original.userInfo( userInfo );
			return this;
		}

		@Override
		public UriComponentsBuilder host( String host ) {
			original.host( host );
			return this;
		}

		@Override
		public UriComponentsBuilder port( int port ) {
			original.port( port );
			return this;
		}

		@Override
		public UriComponentsBuilder port( String port ) {
			original.port( port );
			return this;
		}

		@Override
		public UriComponentsBuilder path( String path ) {
			original.path( path );
			return this;
		}

		@Override
		public UriComponentsBuilder pathSegment( String... pathSegments ) throws IllegalArgumentException {
			original.pathSegment( pathSegments );
			return this;
		}

		@Override
		public UriComponentsBuilder replacePath( String path ) {
			original.replacePath( path );
			return this;
		}

		@Override
		public UriComponentsBuilder query( String query ) {
			original.query( query );
			return this;
		}

		@Override
		public UriComponentsBuilder replaceQuery( String query ) {
			original.replaceQuery( query );
			return this;
		}

		@Override
		public UriComponentsBuilder fragment( String fragment ) {
			original.fragment( fragment );
			return this;
		}

		@Override
		public UriComponentsBuilder uriVariables( Map<String, Object> uriVariables ) {
			return original.uriVariables( uriVariables );
		}

		@Override
		public Object clone() {
			return cloneBuilder();
		}

		@Override
		public UriComponentsBuilder cloneBuilder() {
			DelegatingUriComponentsBuilders builder = new DelegatingUriComponentsBuilders( original.cloneBuilder() );
			builder.queryParams.putAll( this.queryParams );
			return builder;
		}

		UriComponentsBuilder setQueryParam( String name ) {
			this.queryParams.remove( name );
			return this;
		}

		UriComponentsBuilder setQueryParam( String name, Object... values ) {
			this.queryParams.remove( name );
			for ( Object value : values ) {
				this.queryParams.add( name, value );
			}
			return this;
		}
	}
}
