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
package com.foreach.across.modules.entity.registry.properties;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Selection predicate for a number of properties.  Allows incremental building of the selector.
 * Property names can be added with the following options:
 * <ul>
 * <li>.: keep all configured rules</li>
 * <li>*: represents all properties returned when applying the default filter</li>
 * <li>**: represents all properties registered, ignoring any default filter</li>
 * <li>:readable: all readable properties</li>
 * <li>:writable: all writable properties</li>
 * <li>propertyName: exactly that property</li>
 * <li>~propertyName: not that property</li>
 * </ul>
 * Selecting properties happens in 3 stages:
 * <ol>
 * <li>selecting all properties using the include selectors</li>
 * <li>applying the {@link Predicate} to those properties if there is one</li>
 * <li>excluding all explicitly excluded properties</li>
 * </ol>
 *
 * @author Arne Vandamme
 * @see Builder
 */
public class EntityPropertySelector
{
	/**
	 * Keep the previously configured property rules when another selector combines with this one.
	 */
	public static final String CONFIGURED = ".";

	/**
	 * All properties in the property registry, but applying any default filtering that the registry might apply.
	 */
	public static final String ALL = "*";

	/**
	 * All properties known in the property registry, ignoring any default filtering that the registry might apply.
	 */
	public static final String ALL_REGISTERED = "**";

	/**
	 * All properties that are readable.
	 */
	public static final String READABLE = ":readable";

	/**
	 * All properties that are writable.
	 */
	public static final String WRITABLE = ":writable";

	private boolean keepConfiguredRules;
	private final Map<String, Boolean> propertiesToSelect;

	private Predicate<EntityPropertyDescriptor> predicate;

	public EntityPropertySelector() {
		this( new LinkedHashMap<>() );
	}

	public EntityPropertySelector( String... propertyNames ) {
		this();
		configure( propertyNames );
	}

	private EntityPropertySelector( Map<String, Boolean> propertiesToSelect ) {
		this.propertiesToSelect = new LinkedHashMap<>( propertiesToSelect );
	}

	/**
	 * @return optional predicate that should apply
	 */
	Predicate<EntityPropertyDescriptor> getPredicate() {
		return predicate;
	}

	boolean hasPredicate() {
		return predicate != null;
	}

	/**
	 * @return map of property names with boolean indicated if they should be selected or not
	 */
	public Map<String, Boolean> propertiesToSelect() {
		return propertiesToSelect;
	}

	private void configure( String... propertyNames ) {
		boolean keepAlreadyConfigured = false;
		Map<String, Boolean> newProperties = new LinkedHashMap<>();

		for ( String propertyName : propertyNames ) {
			if ( CONFIGURED.equals( propertyName ) ) {
				keepAlreadyConfigured = true;
				keepConfiguredRules = true;
			}
			else {
				if ( propertyName.startsWith( "~" ) ) {
					String actualName = StringUtils.substring( propertyName, 1 );
					if ( isReservedKeyWord( actualName ) ) {
						throw new IllegalArgumentException( "Illegal property selector: " + propertyName );
					}
					newProperties.put( actualName, false );
				}
				else {
					newProperties.put( propertyName, true );
				}
			}
		}

		if ( !keepAlreadyConfigured ) {
			propertiesToSelect.clear();
		}

		propertiesToSelect.putAll( newProperties );
	}

	private boolean isReservedKeyWord( String propertyName ) {
		return ALL.equals( propertyName ) || ALL_REGISTERED.equals( propertyName ) || CONFIGURED.equals( propertyName );
	}

	/**
	 * Combines the current selector with another selector.  If the other selector contains the {@link #CONFIGURED} property, then
	 * the result will be a merged selector.  In any other case, the other selector will in fact replace the current selector.
	 *
	 * @param other selector to combine with
	 * @return other selector or a union of current and other selector if the other contains the {@link #CONFIGURED} rule
	 */
	public EntityPropertySelector combine( EntityPropertySelector other ) {
		if ( other.keepConfiguredRules ) {
			EntityPropertySelector selector = new EntityPropertySelector( propertiesToSelect );
			// combination keeps configured rules if original keeps them
			selector.keepConfiguredRules = keepConfiguredRules;
			selector.predicate = predicate;

			if ( other.hasPredicate() ) {
				if ( selector.hasPredicate() ) {
					selector.predicate = selector.predicate.and( other.predicate );
				}
				else {
					selector.predicate = other.predicate;
				}
			}

			other.propertiesToSelect.forEach(
					( propertyName, include ) -> {
						if ( include ) {
							selector.propertiesToSelect.remove( propertyName );
						}
						selector.propertiesToSelect.put( propertyName, include );
					}
			);

			return selector;
		}

		return other;
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( !( o instanceof EntityPropertySelector ) ) {
			return false;
		}
		EntityPropertySelector that = (EntityPropertySelector) o;
		return Objects.equals( propertiesToSelect, that.propertiesToSelect )
				&& Objects.equals( new ArrayList<>( propertiesToSelect.keySet() ), new ArrayList<>( that.propertiesToSelect.keySet() ) )
				&& Objects.equals( predicate, that.predicate );
	}

	@Override
	public int hashCode() {
		return Objects.hash( propertiesToSelect, predicate );
	}

	@Override
	public String toString() {
		return "EntityPropertySelector{" +
				"keepConfiguredRules=" + keepConfiguredRules +
				", propertiesToSelect=" + propertiesToSelect +
				", predicate=" + predicate +
				'}';
	}

	public static EntityPropertySelector of( String... propertyNames ) {
		return new EntityPropertySelector( propertyNames );
	}

	public static EntityPropertySelector all() {
		return new EntityPropertySelector( ALL );
	}

	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder for a combination of selected properties and a predicate they should additionally match.
	 */
	public static final class Builder
	{
		private String[] propertyNames = null;
		private Predicate<EntityPropertyDescriptor> predicate = null;

		private Builder() {
		}

		public Builder properties( String... propertyNames ) {
			this.propertyNames = propertyNames;
			return this;
		}

		public Builder predicate( Predicate<EntityPropertyDescriptor> predicate ) {
			this.predicate = predicate;
			return this;
		}

		public EntityPropertySelector build() {
			EntityPropertySelector selector = EntityPropertySelector.of( propertyNames != null ? propertyNames : new String[0] );
			selector.keepConfiguredRules = propertyNames == null;
			selector.predicate = predicate;
			return selector;
		}
	}
}
