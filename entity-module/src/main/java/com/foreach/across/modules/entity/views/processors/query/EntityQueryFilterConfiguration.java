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

package com.foreach.across.modules.entity.views.processors.query;

import com.foreach.across.modules.entity.config.builders.EntityPropertyRegistryBuilder;
import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.registry.properties.EntityPropertySelector;
import com.foreach.across.modules.entity.views.ViewElementMode;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Class that represents the configuration for an {@link com.foreach.across.modules.entity.views.processors.EntityQueryFilterProcessor}.
 * <p/>
 * Note: this implementation has a {@link #toBuilder()} method, however, there is a limitation that the same {@link EntityPropertyRegistryBuilder}
 * will be attached to the builder.  Hence the {@link #toBuilder()} is not creating a fully detached copy.
 *
 * @author Arne Vandamme
 * @since 2.2.0
 */
@SuppressWarnings("all")
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EntityQueryFilterConfiguration
{
	private EntityPropertyRegistryBuilder propertyRegistryBuilder;

	/**
	 * Selector for the properties to show in basic filter mode.
	 * Only used if basic is allowed.
	 */
	private EntityPropertySelector propertySelector;

	/**
	 * Base {@link EntityQuery} that should always be applied to the query being executed.
	 * This query predicate can never be removed.
	 */
	private EntityQuery basePredicate;

	/**
	 * The default {@link EntityQuery} that should be executed if none is specified.
	 * The {@link #getBasePredicate()} will always be added to the default query.
	 */
	private EntityQuery defaultQuery;

	/**
	 * Is basic mode allowed on the filter (default to {@code true}).
	 * Basic mode will only be shown if there are actual properties configured for it.
	 */
	@Builder.Default
	private boolean basicMode = true;

	/**
	 * Is advanced mode allowed on the filter (default to {@code true}.
	 */
	@Builder.Default
	private boolean advancedMode = true;

	/**
	 * Set of properties that should allow a single value to be filtered on.
	 */
	@Getter(AccessLevel.NONE)
	private Set<String> singleValueProperties = new HashSet<>();

	/**
	 * Set of properties that should allow multiple values to be filtered on.
	 */
	@Getter(AccessLevel.NONE)
	private Set<String> multiValueProperties = new HashSet<>();

	/**
	 * Should the default filter controls allow multiple values to be selected.
	 * Defaults to {@code false} meaning only a single value can be selected.
	 */
	private boolean defaultToMultiValue;

	/**
	 * Returns {@code true} if a control for that property should allow multiple values to be selected.
	 * This is the case if the property is manually registered for multi value support or if default
	 * control mode is multi-value and the property was not set manually to single value.
	 *
	 * @param propertyName to get the {@link ViewElementMode} for
	 * @return fixed mode
	 */
	public boolean isMultiValue( String propertyName ) {
		if ( singleValueProperties.contains( propertyName ) ) {
			return false;
		}
		if ( multiValueProperties.contains( propertyName ) ) {
			return true;
		}
		return defaultToMultiValue;
	}

	public boolean hasBasePredicate() {
		return basePredicate != null && !EntityQuery.all().equals( basePredicate );
	}

	@SuppressWarnings("all")
	public static class EntityQueryFilterConfigurationBuilder
	{
		private Set<String> singleValueProperties = new HashSet<>();
		private Set<String> multiValueProperties = new HashSet<>();

		private EntityPropertyRegistryBuilder propertyRegistryBuilder = new EntityPropertyRegistryBuilder();

		/**
		 * Set the default {@link EntityQuery} that should be executed if none is specified.
		 *
		 * @param eql representing the query
		 * @return current builder
		 */
		public EntityQueryFilterConfigurationBuilder defaultQuery( String eql ) {
			return defaultQuery( StringUtils.isEmpty( eql ) ? (EntityQuery) null : EntityQuery.parse( eql ) );
		}

		/**
		 * Set the default {@link EntityQuery} that should be executed if none is specified.
		 *
		 * @param defaultQuery query
		 * @return current builder
		 */
		public EntityQueryFilterConfigurationBuilder defaultQuery( EntityQuery defaultQuery ) {
			this.defaultQuery = defaultQuery;
			return this;
		}

		/**
		 * Set the base {@link EntityQuery} predicate that should always be applied.
		 * This will replace any previously configured predicate.
		 *
		 * @param eql representing the predicate
		 * @return current builder
		 */
		public EntityQueryFilterConfigurationBuilder basePredicate( String eql ) {
			return basePredicate( StringUtils.isEmpty( eql ) ? (EntityQuery) null : EntityQuery.parse( eql ) );
		}

		/**
		 * Set the base {@link EntityQuery} predicate that should always be applied.
		 * This will replace any previously configured predicate.
		 *
		 * @param basePredicate predicate
		 * @return current builder
		 */
		public EntityQueryFilterConfigurationBuilder basePredicate( EntityQuery basePredicate ) {
			this.basePredicate = basePredicate;
			return this;
		}

		/**
		 * Append a predicate to the base {@link EntityQuery} predicate.  If no previous predicate has been
		 * configured, this method behaves the same as {@link #basePredicate(EntityQuery)}.  If a previous
		 * predicate has been added, the new predicate will be added to it.
		 * <p/>
		 * If the new predicate defines a sort value, it will replace the previously configured sort.
		 *
		 * @param eql representing the predicate
		 * @return current builder
		 */
		public EntityQueryFilterConfigurationBuilder appendBasePredicate( String eql ) {
			if ( StringUtils.isNotEmpty( eql ) ) {
				appendBasePredicate( EntityQuery.parse( eql ) );
			}
			return this;
		}

		/**
		 * Append a predicate to the base {@link EntityQuery} predicate.  If no previous predicate has been
		 * configured, this method behaves the same as {@link #basePredicate(EntityQuery)}.  If a previous
		 * predicate has been added, the new predicate will be added to it.
		 * <p/>
		 * If the new predicate defines a sort value, it will replace the previously configured sort.
		 *
		 * @param eql representing the predicate
		 * @return current builder
		 */
		public EntityQueryFilterConfigurationBuilder appendBasePredicate( EntityQuery basePredicate ) {
			this.basePredicate = EntityQuery.and( basePredicate, this.basePredicate );
			return this;
		}

		/**
		 * Configure the different properties to be shown in basic mode.
		 * The order of the properties specified will determine the order in which they are rendered.
		 *
		 * @param propertyNames in a property selector format
		 * @return current builder
		 */
		public EntityQueryFilterConfigurationBuilder showProperties( String... propertyNames ) {
			propertySelector( EntityPropertySelector.of( propertyNames ) );
			return this;
		}

		/**
		 * Customize the property configuration specifically for the filter.
		 *
		 * @param consumer for the properties
		 * @return current builder
		 */
		public EntityQueryFilterConfigurationBuilder properties( Consumer<EntityPropertyRegistryBuilder> consumer ) {
			consumer.accept( propertyRegistryBuilder );
			return this;
		}

		/**
		 * Configures properties that should only allow a single value to be filtered on.
		 *
		 * @param propertyNames to configure
		 * @return current builder
		 */
		public EntityQueryFilterConfigurationBuilder singleValue( String... propertyNames ) {
			Stream.of( propertyNames ).forEach( p -> {
				singleValueProperties.add( p );
				multiValueProperties.remove( p );
			} );
			return this;
		}

		/**
		 * Configures properties that should allow multiple values to be filtered on.
		 *
		 * @param propertyNames
		 * @return current builder
		 */
		public EntityQueryFilterConfigurationBuilder multiValue( String... propertyNames ) {
			Stream.of( propertyNames ).forEach( p -> {
				singleValueProperties.remove( p );
				multiValueProperties.add( p );
			} );
			return this;
		}
	}
}