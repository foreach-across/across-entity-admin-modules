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

package com.foreach.across.modules.entity.bind;

import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyHandlingType;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * Helper for generating a control name that can be used to bind to a property, both directly or by
 * using an {@link EntityPropertiesBinder}. Supports multiple types of control name generation.
 * <p/>
 * The actual control name to use can be retrieved by simply calling {@link #toString()}.
 * <p/>
 * Simple control name examples:
 * <ul>
 * <li>{@code forProperty("user")} -> {@code user}</li>
 * <li>{@code forProperty("user").asBinderItem()} -> {@code properties[user].value}</li>
 * <li>{@code forProperty("user").forChildProperty("address")} -> {@code user.address}</li>
 * <li>{@code forProperty("user").forChildProperty("address").asBinderItem()} -> {@code properties[user].properties[address].value}</li>
 * <li>{@code forProperty("user").asBinderItem().forChildProperty("address")} -> {@code properties[user].value.address}</li>
 * <li>{@code forProperty("user").asBinderItem().withInitializedValue()} -> {@code properties[user].initializedValue}</li>
 * </ul>
 * To work with collection items and map entry control names, see the {@link ForProperty.BinderProperty#asCollectionItem()} and
 * {@link ForProperty.BinderProperty#asMapEntry()} javadoc.
 * <p/>
 * It is also possible to specify a root control name that should prefix generated properties, see {@link #root(String)} for more details.
 *
 * @author Arne Vandamme
 * @see EntityPropertiesBinder
 * @since 3.2.0
 */
@EqualsAndHashCode
public abstract class EntityPropertyControlName
{
	/**
	 * Default {@link EntityPropertiesBinder} prefix: this is the property path where the binder can be found if it
	 * is the direct target of a {@link org.springframework.validation.DataBinder}. The default value links to {@link EntityPropertiesBinder#getProperties()}.
	 */
	public static final String DEFAULT_BINDER_PREFIX = "properties";

	/**
	 * Create a control name for the property with the given name.
	 *
	 * @param propertyName name of the property
	 * @return control name for property
	 */
	public static ForProperty forProperty( @NonNull String propertyName ) {
		return forProperty( propertyName, DEFAULT_BINDER_PREFIX );
	}

	/**
	 * Create a control name for the property with the given name, and a custom binder prefix.
	 * The binder prefix is the base binding path where the {@link EntityPropertiesBinder} is available.
	 * If you bind directly on the {@code EntityPropertiesBinder}, the default is binder prefix ({@code properties})
	 * which will call {@link EntityPropertiesBinder#getProperties()}.
	 * <p/>
	 * In some cases (with an {@link com.foreach.across.modules.entity.views.request.EntityViewCommand} for example),
	 * the actual {@code EntityPropertiesBinder} is registered under a different property. For example, assume you have
	 * a custom binder target:
	 * <pre>{@code
	 * class BinderTarget {
	 *     EntityPropertiesBinder getPropertyData();
	 * }
	 * }</pre>
	 * In this case {@code propertyData} would be the correct binder prefix to use.
	 *
	 * @param propertyName name of the property
	 * @param binderPrefix binder prefix to use
	 * @return control name for the property
	 */
	public static ForProperty forProperty( @NonNull String propertyName, @NonNull String binderPrefix ) {
		return create( binderPrefix, propertyName, null );
	}

	/**
	 * Create a control name for the property descriptor.
	 * <p/>
	 * In case of a nested descriptor, a control name
	 * hierarchy will be built, taking into account the {@link EntityPropertyHandlingType} of the parent descriptors.
	 * If a parent descriptor has specified that it should bind values using the {@link EntityPropertiesBinder}
	 * instead of directly, the control name returned will use the binder path for the parent item.
	 * <p/>
	 * Likewise, if the descriptor explicitly specifies a {@link EntityPropertyHandlingType#MANUAL} handling type,
	 * the parents will be ignored and the descriptor name is expected to be absolute. If an {@link EntityAttributes#CONTROL_NAME}
	 * is registered, that value will be used as property name instead.
	 *
	 * @param descriptor for which to generate the control name
	 * @return control name for the property
	 */
	public static ForProperty forProperty( @NonNull EntityPropertyDescriptor descriptor ) {
		return forProperty( descriptor, DEFAULT_BINDER_PREFIX );
	}

	/**
	 * Create a control name for the property descriptor.
	 * <p/>
	 * In case of a nested descriptor, a control name hierarchy will be built, taking into account
	 * the {@link EntityPropertyHandlingType} of the parent descriptors.
	 * If a parent descriptor has specified that it should bind values using the {@link EntityPropertiesBinder}
	 * instead of directly, the control name returned will use the binder path for the parent item.
	 * <p/>
	 * Likewise, if the descriptor explicitly specifies a {@link EntityPropertyHandlingType#MANUAL} handling type,
	 * the parents will be ignored and the descriptor name is expected to be absolute. If an {@link EntityAttributes#CONTROL_NAME}
	 * is registered, that value will be used as property name instead.
	 *
	 * @param descriptor   for which to generate the control name
	 * @param binderPrefix binder prefix to use
	 * @return control name for the property
	 */
	public static ForProperty forProperty( @NonNull EntityPropertyDescriptor descriptor, @NonNull String binderPrefix ) {
		if ( EntityPropertyHandlingType.forProperty( descriptor ) == EntityPropertyHandlingType.MANUAL ) {
			String propertyName = Objects.toString( descriptor.getAttribute( EntityAttributes.CONTROL_NAME ), descriptor.getName() );
			return (ForProperty) forProperty( propertyName, binderPrefix ).forHandlingType( EntityPropertyHandlingType.MANUAL );
		}

		if ( descriptor.isNestedProperty() ) {
			EntityPropertyDescriptor parentDescriptor = descriptor.getParentDescriptor();
			ForProperty parent = forProperty( parentDescriptor, binderPrefix );

			return parent.forHandlingType( EntityPropertyHandlingType.forProperty( parentDescriptor ) ).forChildProperty( descriptor );
		}

		return forProperty( descriptor.getName(), binderPrefix );
	}

	/**
	 * Create a control name for the property descriptor, using a parent {@link EntityPropertyControlName} if one is registered on the
	 * {@link ViewElementBuilderContext}. If the builder context contains a {@link EntityPropertyControlName} attribute, the actual control
	 * name will be generated using {@link EntityPropertyControlName#forChildProperty(EntityPropertyDescriptor)}, else it will be
	 * the equivalent as a call to {@link EntityPropertyControlName#forProperty(EntityPropertyDescriptor)}.
	 * <p/>
	 * This approach can be used for nested control name building, where you need to ensure that a control name is a child of a specific
	 * collection item path for example. In the same way it can be use to register a {@link #root(String)} control name, and ensure that
	 * all direct property paths are generated with a base prefix, or a custom prefix for the main {@link EntityPropertiesBinder} is being used.
	 *
	 * @param descriptor     for which to generate the control name
	 * @param builderContext to inspect for a parent {@code EntityPropertyControlName}
	 * @return control name for the property
	 */
	public static ForProperty forProperty( @NonNull EntityPropertyDescriptor descriptor, @NonNull ViewElementBuilderContext builderContext ) {
		EntityPropertyControlName parentControlName = builderContext.getAttribute( EntityPropertyControlName.class );

		if ( parentControlName != null ) {
			return parentControlName.forChildProperty( descriptor );
		}

		return forProperty( descriptor );
	}

	/**
	 * Create a root control name which only behaves a direct path prefix, but does not impact transitive binding.
	 * For example, executing: {@code root("entity").forChildProperty("user") } would return {@code entity.user}
	 * as the direct property path, but would still return {@code properties[user]} as binder item.
	 * <p/>
	 * Comparatively: {@code forProperty("entity").forChildProperty("user")} would also return {@code entity.user}
	 * as direct property path, but would return {@code properties[entity].properties[user]} as binder item.
	 * <p/>
	 * A root control name can be registered on the {@link ViewElementBuilderContext} to ensure that all control names
	 * follow a base path, by generating them using {@link #forProperty(EntityPropertyDescriptor, ViewElementBuilderContext)}.
	 *
	 * @param prefix direct property path prefix that should always be added
	 * @return root control name
	 * @see #root(String, String)
	 */
	public static EntityPropertyControlName root( @NonNull String prefix ) {
		return root( prefix, DEFAULT_BINDER_PREFIX );
	}

	/**
	 * Create a root control name which only behaves a direct path prefix, but does not impact transitive binding.
	 * For example, executing: {@code root("entity").forChildProperty("user") } would return {@code entity.user}
	 * as the direct property path, but would still return {@code properties[user]} as binder item.
	 * <p/>
	 * Comparatively: {@code forProperty("entity").forChildProperty("user")} would also return {@code entity.user}
	 * as direct property path, but would return {@code properties[entity].properties[user]} as binder item.
	 * <p/>
	 * A root control name can be registered on the {@link ViewElementBuilderContext} to ensure that all control names
	 * follow a base path, by generating them using {@link #forProperty(EntityPropertyDescriptor, ViewElementBuilderContext)}.
	 *
	 * @param prefix direct property path prefix that should always be added
	 * @return root control name
	 * @see #root(String, String)
	 */
	public static EntityPropertyControlName root( @NonNull String prefix, @NonNull String binderPrefix ) {
		return new Root( prefix, binderPrefix );
	}

	/**
	 * Create a control name for a child property of the current control name.
	 * How exactly the control name gets generated depends on the current {@link EntityPropertyControlName} implementation.
	 * <p/>
	 * In its most simple form, suppose the current control name has a direct path {@code user}.
	 * Calling {@code forChildProperty("address")} will return a control name with the direct path {@code user.address}.
	 *
	 * @param propertyName name of the property
	 * @return property control name
	 */
	public ForProperty forChildProperty( @NonNull String propertyName ) {
		return create( DEFAULT_BINDER_PREFIX, propertyName, this );
	}

	/**
	 * Create a control name for a child property of the current control name, with the {@link EntityPropertyDescriptor}
	 * representing the child property. Both the current control name implementation and the descriptor settings will determine
	 * how exactly the control name gets generated.
	 * <p/>
	 * If the descriptor explicitly specifies a {@link EntityPropertyHandlingType#MANUAL} handling type,
	 * the parents will be ignored and the descriptor name is expected to be absolute. If an {@link EntityAttributes#CONTROL_NAME}
	 * is registered, that value will be used as property name instead.
	 * <p/>
	 * If the descriptor is a nested descriptor, the parent control name will be matched against the property name
	 * and stripped off as a prefix. Suppose the parent control name is {@code user} and the descriptor has name {@code user.address}.
	 * A non-nested descriptor would generate control name {@code user.user.address} whereas a nested descriptor will return {@code user.address}.
	 *
	 * @param descriptor for the property
	 * @return property control name
	 */
	public ForProperty forChildProperty( @NonNull EntityPropertyDescriptor descriptor ) {
		if ( EntityPropertyHandlingType.forProperty( descriptor ) == EntityPropertyHandlingType.MANUAL ) {
			String propertyName = Objects.toString( descriptor.getAttribute( EntityAttributes.CONTROL_NAME ), descriptor.getName() );
			return (ForProperty.SingleValue) forChildProperty( propertyName ).forHandlingType( EntityPropertyHandlingType.MANUAL );
		}

		if ( descriptor.isNestedProperty() ) {
			String pathPrefix = resolvePathPrefix();
			EntityPropertyDescriptor parent = descriptor.getParentDescriptor();

			if ( parent.getName().length() > pathPrefix.length() ) {
				return forChildProperty( parent ).forHandlingType( EntityPropertyHandlingType.forProperty( parent ) )
				                                 .forChildProperty( descriptor );
			}

			return forChildProperty( removePrefix( descriptor.getName(), pathPrefix ) );
		}

		return forChildProperty( descriptor.getName() );
	}

	/**
	 * Return the base property control name that was used to create the current one.
	 * If the current control name is a specific type (for example a {@link ForProperty.BinderProperty}) then this
	 * will return the {@link ForProperty} instance that was used to create it.
	 *
	 * @return the property control name that was the basis for the current one
	 */
	public abstract ForProperty asProperty();

	private static ForProperty create( String binderPrefix, String propertyName, EntityPropertyControlName parent ) {
		if ( propertyName.endsWith( EntityPropertyRegistry.INDEXER ) ) {
			return new ForProperty.CollectionItem( binderPrefix, propertyName, parent );
		}
		else if ( propertyName.endsWith( EntityPropertyRegistry.MAP_KEY ) || propertyName.endsWith( EntityPropertyRegistry.MAP_VALUE ) ) {
			return new ForProperty.MapEntry( binderPrefix, propertyName, parent );
		}
		return new ForProperty.SingleValue( binderPrefix, propertyName, parent );
	}

	private static String dotJoin( String prefix, String suffix ) {
		return suffix.startsWith( "[" ) || StringUtils.isEmpty( prefix ) ? prefix + suffix : prefix + "." + suffix;
	}

	private static String removePrefix( String path, String prefix ) {
		return StringUtils.removeStart( StringUtils.removeStart( path, prefix ), "." );
	}

	abstract String resolvePathPrefix();

	/**
	 * Represents a root control name, mainly meant for the creation of child properties.
	 * Prefixes all direct property paths and sets a default
	 * binder prefix, but does not otherwise influence the binder item path of children.
	 */
	@RequiredArgsConstructor
	@EqualsAndHashCode(callSuper = true)
	private static class Root extends EntityPropertyControlName
	{
		private final String prefix;
		private final String binderPrefix;

		@Override
		protected String resolvePathPrefix() {
			return "";
		}

		@Override
		public ForProperty forChildProperty( EntityPropertyDescriptor descriptor ) {
			if ( EntityPropertyHandlingType.forProperty( descriptor ) == EntityPropertyHandlingType.MANUAL ) {
				String propertyName = Objects.toString( descriptor.getAttribute( EntityAttributes.CONTROL_NAME ), descriptor.getName() );
				return (ForProperty.SingleValue) forChildProperty( propertyName ).forHandlingType( EntityPropertyHandlingType.MANUAL );
			}

			if ( descriptor.isNestedProperty() ) {
				EntityPropertyDescriptor parentDescriptor = descriptor.getParentDescriptor();
				ForProperty parent = forChildProperty( parentDescriptor );

				String childPropertyName = EntityPropertyControlName.removePrefix( descriptor.getName(), parent.resolvePathPrefix() );

				return parent.forHandlingType( EntityPropertyHandlingType.forProperty( parentDescriptor ) ).forChildProperty( childPropertyName );
			}

			return forChildProperty( descriptor.getName() );
		}

		@Override
		public ForProperty forChildProperty( String propertyName ) {
			return new ForProperty.SingleValue( binderPrefix, propertyName, this );
		}

		@Override
		public ForProperty asProperty() {
			return new ForProperty.SingleValue( binderPrefix, prefix, null );
		}

		@Override
		public String toString() {
			return prefix;
		}
	}

	/**
	 * Represents a general property termination endpoint, usually represented by its direct path.
	 * Calling {@code toString()} will usually yield the direct property path. Eg. the property named
	 * {@code user} will have a direct path of {@code user}.
	 * <p/>
	 * Separate methods can be used to convert the current endpoint to a different base type.
	 *
	 * @see #asBinderItem()
	 * @see #asCollectionItem()
	 * @see #asMapEntry()
	 * @see #asSingleValue()
	 */
	@EqualsAndHashCode(callSuper = true)
	public abstract static class ForProperty extends EntityPropertyControlName
	{
		final String binderPrefix;
		final String propertyPath;
		final EntityPropertyControlName parent;

		@Setter(value = AccessLevel.PACKAGE)
		String propertyName;

		private ForProperty( String binderPrefix, String propertyPath, EntityPropertyControlName parent ) {
			this.binderPrefix = binderPrefix;
			this.propertyPath = propertyPath;
			this.parent = parent;
			this.propertyName = propertyPath;
		}

		/**
		 * Convenience method (for readability) which actually returns the same reference.
		 *
		 * @return same reference
		 */
		public ForProperty asProperty() {
			return this;
		}

		/**
		 * Create a binder item reference for this property.
		 * Where the current instance would return {@code user} as control name, the {@link BinderProperty} would
		 * be something like {@code properties[user].value}.
		 *
		 * @return binder property
		 */
		public BinderProperty asBinderItem() {
			return new BinderProperty( "" );
		}

		/**
		 * Return the current control name adjusted for the property handling type.
		 * <ul>
		 * <li>In case of {@link EntityPropertyHandlingType#MANUAL}, a {@link SingleValue} with the property path will be returned.</li>
		 * <li>In case of {@link EntityPropertyHandlingType#BINDER}, a {@link BinderProperty} will be returned</li>
		 * <li>In case of {@link EntityPropertyHandlingType#DIRECT}, the current control name will be returned</li>
		 * </ul>
		 *
		 * @return possibly adjusted control name
		 */
		public EntityPropertyControlName forHandlingType( @NonNull EntityPropertyHandlingType handlingType ) {
			switch ( handlingType ) {
				case BINDER:
					return asBinderItem();
				case MANUAL:
					return new SingleValue( propertyPath );
			}

			return asProperty();
		}

		/**
		 * Explicitly convert the current control name to a single value control name for the same property.
		 * If the current control name is already a {@link SingleValue}, the same reference will be returned,
		 * else a new instance will be created.
		 *
		 * @return single value control name
		 */
		public SingleValue asSingleValue() {
			if ( this instanceof SingleValue ) {
				return (SingleValue) this;
			}

		/*	if ( StringUtils.isEmpty( propertyName ) && parent != null ) {
				return parent.asProperty().asSingleValue();
			}
*/
			return new SingleValue( binderPrefix, propertyName, parent );
		}

		/**
		 * Explicitly convert the current control name to a map entry control name.
		 * If the current control name is already a {@link MapEntry}, the same reference will be returned,
		 * else a new instance will be created.
		 *
		 * @return map entry control name
		 */
		public MapEntry asMapEntry() {
			return this instanceof MapEntry ? (MapEntry) this : new MapEntry( binderPrefix, propertyPath, parent );
		}

		/**
		 * Explicitly convert the current control name to a collection item control name.
		 * If the current control name is already a {@link CollectionItem}, the same reference will be returned,
		 * else a new instance will be created.
		 *
		 * @return collection item control name
		 */
		public CollectionItem asCollectionItem() {
			return this instanceof CollectionItem ? (CollectionItem) this : new CollectionItem( binderPrefix, propertyPath, parent );
		}

		public String toString() {
			String path = resolvePropertyPathSegment();

			if ( parent != null ) {
				if ( parent instanceof BinderProperty && !StringUtils.isEmpty( path ) ) {
					BinderProperty parentProperty = (BinderProperty) parent;
					return EntityPropertyControlName.dotJoin( parentProperty.withInitializedValue().toString(), path );
				}
				else {
					return StringUtils.removeEnd( dotJoin( parent.toString(), path ), "." );
				}
			}

			return path;
		}

		abstract String binderSuffix();

		abstract String resolvePropertyPathSegment();

		@Override
		protected String resolvePathPrefix() {
			return parent != null ? dotJoin( parent.resolvePathPrefix(), propertyPath ) : propertyPath;
		}

		/**
		 * Represents a property as a single value property. This is usually the default property representation.
		 * A single value direct property path is usually something like {@code user} and the {@link BinderProperty}
		 * variation something like {@code properties[user].value}.
		 */
		@EqualsAndHashCode(callSuper = true)
		public static final class SingleValue extends ForProperty
		{
			private SingleValue( String binderPrefix, String propertyPath, EntityPropertyControlName parent ) {
				super( binderPrefix, propertyPath, parent );
			}

			private SingleValue( String propertyPath ) {
				super( DEFAULT_BINDER_PREFIX, propertyPath, null );
			}

			@Override
			String binderSuffix() {
				return "";
			}

			@Override
			String resolvePropertyPathSegment() {
				return propertyName;
			}
		}

		/**
		 * Represents a property as a collection item. A collection item optionally takes two parameters:
		 * <ul>
		 * <li>an index value ({@link #withIndex(Integer)} which is used for the direct property path.</li>
		 * <li>a binder item key ({@link #withBinderItemKey(Object)}) which is used as key in the binder property path</li>
		 * </ul>
		 * Suppose you have a property {@code user}, depending on those parameters the result will be:
		 * <ul>
		 * <li>without either index or binder item key: the direct property path would be {@code user[]} and the binder property {@code properties[user].items[].value}</li>
		 * <li>with an index: the direct property path will become {@code user[INDEX]}</li>
		 * <li>with a binder item key: the binder property will become {@code properties[user].items[ITEM_KEY].value}</li>
		 * </ul>
		 */
		@EqualsAndHashCode(callSuper = true)
		public static final class CollectionItem extends ForProperty
		{
			private Integer index;
			private Object itemKey;

			private CollectionItem( String binderPrefix, String propertyPath, EntityPropertyControlName parent ) {
				super( binderPrefix, StringUtils.removeEnd( propertyPath, EntityPropertyRegistry.INDEXER ) + EntityPropertyRegistry.INDEXER, parent );
				setPropertyName( StringUtils.removeEnd( propertyPath, EntityPropertyRegistry.INDEXER ) );
			}

			/**
			 * Create a new instance with the specified index value assigned.
			 *
			 * @param index value
			 * @return new collection item
			 */
			public CollectionItem withIndex( Integer index ) {
				CollectionItem controlName = new CollectionItem( binderPrefix, propertyPath, parent );
				controlName.index = index;
				controlName.itemKey = itemKey;
				return controlName;
			}

			/**
			 * Create a new instance with the specified binder item key assigned.
			 *
			 * @param itemKey item key
			 * @return new collection item
			 */
			public CollectionItem withBinderItemKey( Object itemKey ) {
				CollectionItem controlName = new CollectionItem( binderPrefix, propertyPath, parent );
				controlName.index = index;
				controlName.itemKey = itemKey;
				return controlName;
			}

			@Override
			String binderSuffix() {
				Object ix = itemKey != null ? itemKey : index;
				return ".items[" + Objects.toString( ix, "" ) + "]";
			}

			@Override
			String resolvePropertyPathSegment() {
				return propertyName + "[" + Objects.toString( index, "" ) + "]";
			}
		}

		/**
		 * Represents a property as a map entry, which optionally takes two parameters:
		 * <ul>
		 * <li>a map key ({@link #withMapKey(Object)}) which is used as the map key in the direct property path</li>
		 * <li>a binder entry key ({@link #withBinderEntryKey(Object)}) which is used as the entry key in the binder property path</li>
		 * </ul>
		 * If you want to use the map entry as a binder property, you usually have to create the specialized {@link #asBinderEntryKey()}
		 * or {@link #asBinderEntryValue()} first to access either key or value related data.
		 * <p/>
		 * Suppose you have a property {@code user}, depending on the parameters the result will be:
		 * <ul>
		 * <li>without map key or binder entry: the direct property path would be {@code user[]},
		 * the binder property for the entry key would be {@code properties[user].entries[].key.value},
		 * for the entry value it would be {@code properties[user].entries[].value.value}
		 * </li>
		 * <li>with a map key: direct property path would be {@code user[MAP_KEY]}</li>
		 * <li>with a binder entry key: the binder property for the entry key would be {@code properties[user].entries[ENTRY_KEY].key.value}
		 * and for the entry value would be {@code properties[user].entries[ENTRY_KEY].value.value}</li>
		 * </ul>
		 */
		@EqualsAndHashCode(callSuper = true)
		public static final class MapEntry extends ForProperty
		{
			private Object mapKey;
			private Object entryKey;

			private MapEntry( String binderPrefix, String propertyPath, EntityPropertyControlName parent ) {
				super( binderPrefix, propertyPath, parent );
				if ( propertyPath.endsWith( EntityPropertyRegistry.MAP_KEY ) ) {
					setPropertyName( StringUtils.removeEnd( propertyPath, EntityPropertyRegistry.MAP_KEY ) );
				}
				else {
					setPropertyName( StringUtils.removeEnd( propertyPath, EntityPropertyRegistry.MAP_VALUE ) );
				}
			}

			/**
			 * Create a new instance with the specified map key value assigned.
			 *
			 * @param key value
			 * @return new map entry
			 */
			public MapEntry withMapKey( Object key ) {
				MapEntry controlName = new MapEntry( binderPrefix, propertyPath, parent );
				controlName.mapKey = key;
				controlName.entryKey = entryKey;
				return controlName;
			}

			/**
			 * Create a new instance with the specified binder entry key assigned.
			 *
			 * @param entryKey binder entry key
			 * @return new map entry
			 */
			public MapEntry withBinderEntryKey( Object entryKey ) {
				MapEntry controlName = new MapEntry( binderPrefix, propertyPath, parent );
				controlName.mapKey = mapKey;
				controlName.entryKey = entryKey;
				return controlName;
			}

			/**
			 * @return the same as {@link #asBinderEntryValue()}
			 */
			@Override
			public BinderProperty asBinderItem() {
				return asBinderEntryValue();
			}

			/**
			 * @return a control name for the entry key
			 */
			public BinderProperty asBinderEntryKey() {
				return new BinderProperty( ".key" );
			}

			/**
			 * @return a control name for the entry value
			 */
			public BinderProperty asBinderEntryValue() {
				return new BinderProperty( ".value" );
			}

			/**
			 * @return binder item control name for the entry, terminating on {@code .deleted}
			 */
			public String toBinderEntryDeleted() {
				return new BinderProperty( "" ).toDeleted();
			}

			/**
			 * @return binder item control name for the entry, terminating on {@code .sortIndex}
			 */
			public String toBinderEntrySortIndex() {
				return new BinderProperty( "" ).toSortIndex();
			}

			/**
			 * @return binder item control name for the entry path, without termination
			 */
			public String toBinderEntryPath() {
				return new BinderProperty( "" ).toItemPath();
			}

			@Override
			String binderSuffix() {
				return ".entries[" + Objects.toString( entryKey, "" ) + "]";
			}

			@Override
			String resolvePropertyPathSegment() {
				return propertyName + "[" + Objects.toString( mapKey, "" ) + "]";
			}
		}

		/**
		 * Represents the control name for a property via the {@link EntityPropertiesBinder}.
		 * By default the control name will use the {@code value} accessor, but {@link #withValue()} and {@link #withInitializedValue()}
		 * value can be used to specify the actual termination point.
		 * <p/>
		 * Suppose you have a property {@code user} and a binder prefix with value {@code properties},
		 * then the {@code BinderProperty} path representation would be {@code properties[user].value}
		 */
		@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
		public final class BinderProperty extends EntityPropertyControlName
		{
			private final String suffix;

			/**
			 * Create a new control name instance with an explicit termination on {@code value},
			 * for example {@code properties[user].value}.
			 *
			 * @return new control name instance
			 */
			public BinderPropertyValue withValue() {
				return new BinderPropertyValue( "value" );
			}

			/**
			 * Create a new control name instance with an explicit termination on {@code initializedValue},
			 * for example {@code properties[user].initializedValue}.
			 *
			 * @return new control name instance
			 */
			public BinderPropertyValue withInitializedValue() {
				return new BinderPropertyValue( "initializedValue" );
			}

			/**
			 * @return binder item control name terminating on {@code .deleted}
			 */
			public String toDeleted() {
				return binderItemPath() + ".deleted";
			}

			/**
			 * @return binder item control name termination on {@code .bound}
			 */
			public String toBound() {
				return binderItemPath() + ".bound";
			}

			/**
			 * @return binder item control name termination on {@code .bound}
			 */
			public String toSortIndex() {
				return binderItemPath() + ".sortIndex";
			}

			/**
			 * @return binder item control name path (only the path segment without an actual termination)
			 */
			public String toItemPath() {
				return binderItemPath();
			}

			@Override
			public ForProperty asProperty() {
				return ForProperty.this;
			}

			@Override
			String resolvePathPrefix() {
				return ForProperty.this.resolvePathPrefix();
			}

			private String binderItemPath() {
				ForProperty current = getProperty();
				EntityPropertyControlName parent = current.parent;

				if ( parent != null ) {
					if ( parent instanceof ForProperty ) {
						ForProperty parentPath = (ForProperty) parent;
						return parentPath.asBinderItem().binderItemPath() + propertiesBinderSegment( current ) + current.binderSuffix() + suffix;
					}
					else if ( parent instanceof BinderProperty ) {
						BinderProperty parentProperty = (BinderProperty) parent;
						return parentProperty.binderItemPath() + propertiesBinderSegment( current ) + current.binderSuffix() + suffix;
					}
					else if ( parent instanceof BinderPropertyValue ) {
						BinderPropertyValue parentPropertyValue = (BinderPropertyValue) parent;
						return parentPropertyValue.getBinderProperty().binderItemPath() + propertiesBinderSegment( current ) + current.binderSuffix() + suffix;
					}
				}

				return StringUtils.removeStart( propertiesBinderSegment( current ), "." ) + current.binderSuffix() + suffix;
			}

			private String propertiesBinderSegment( ForProperty property ) {
				return StringUtils.isEmpty( property.propertyName ) ? "" : "." + property.binderPrefix + "[" + property.propertyName + "]";
			}

			private ForProperty getProperty() {
				return ForProperty.this;
			}

			@Override
			public String toString() {
				return withValue().toString();
			}

			@Override
			public boolean equals( Object o ) {
				if ( this == o ) {
					return true;
				}
				if ( o == null || getClass() != o.getClass() ) {
					return false;
				}
				if ( !super.equals( o ) ) {
					return false;
				}
				BinderProperty that = (BinderProperty) o;
				return Objects.equals( suffix, that.suffix ) && Objects.equals( getProperty(), that.getProperty() );
			}

			@Override
			public int hashCode() {
				return Objects.hash( super.hashCode(), suffix );
			}

			/**
			 * Represents a binder property termination endpoint (eg. {@code value} or {@code initializedValue}.
			 */
			@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
			public class BinderPropertyValue extends EntityPropertyControlName
			{
				private final String suffix;

				@Override
				public ForProperty asProperty() {
					return ForProperty.this;
				}

				public String toString() {
					return EntityPropertyControlName.dotJoin( BinderProperty.this.binderItemPath(), suffix );
				}

				@Override
				String resolvePathPrefix() {
					return ForProperty.this.resolvePathPrefix();
				}

				private BinderProperty getBinderProperty() {
					return BinderProperty.this;
				}

				@Override
				public boolean equals( Object o ) {
					if ( this == o ) {
						return true;
					}
					if ( o == null || getClass() != o.getClass() ) {
						return false;
					}
					if ( !super.equals( o ) ) {
						return false;
					}

					BinderPropertyValue other = (BinderPropertyValue) o;
					return Objects.equals( other.suffix, suffix ) && Objects.equals( other.getBinderProperty(), getBinderProperty() );
				}

				@Override
				public int hashCode() {
					return Objects.hash( super.hashCode(), suffix, getBinderProperty() );
				}
			}
		}
	}
}
