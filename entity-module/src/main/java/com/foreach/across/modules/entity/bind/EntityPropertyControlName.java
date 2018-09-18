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

import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * Helper that holds control names that can be used to bind correctly to a property
 * using an {@link EntityPropertiesBinder}. Supports multiple type of control name generation,
 * and correctly builds a binding path for various scenarios.
 *
 * @author Arne Vandamme
 * @see EntityPropertiesBinder
 * @since 3.2.0
 */
public abstract class EntityPropertyControlName
{
	public static final String DEFAULT_BINDER_PREFIX = "properties";

	public static ForPath create( @NonNull String propertyName ) {
		return create( propertyName, DEFAULT_BINDER_PREFIX );
	}

	public static ForPath create( @NonNull String propertyName, @NonNull String binderPrefix ) {
		return new ForPath.SingleValue( binderPrefix, propertyName, null );
	}

	public ForPath asChildProperty( String propertyName ) {
		return new ForPath.SingleValue( DEFAULT_BINDER_PREFIX, propertyName, this );
	}

	public abstract static class ForPath extends EntityPropertyControlName
	{
		protected final String binderPrefix;
		protected final String propertyPath;
		protected final String propertyName;
		protected final EntityPropertyControlName parent;

		public ForPath( String binderPrefix, String propertyPath, EntityPropertyControlName parent ) {
			this.binderPrefix = binderPrefix;
			this.propertyPath = propertyPath;
			this.parent = parent;
			this.propertyName = StringUtils.removeEnd( propertyPath, EntityPropertyRegistry.INDEXER );
		}

		public ForPath asDirectProperty() {
			return this;
		}

		public BinderProperty asBinderItem() {
			return new BinderProperty( "" );
		}

		public String toString() {
			String path = resolveFullPropertyPath();

			if ( parent != null ) {
				if ( parent instanceof BinderProperty ) {
					BinderProperty parentProperty = (BinderProperty) parent;
					return parentProperty.asValue() + "." + path;
				}
				else {
					return parent.toString() + "." + path;
				}
			}

			return path;
		}

		protected abstract String binderSuffix();

		protected abstract String resolveFullPropertyPath();

		public SingleValue asSingleValue() {
			return this instanceof SingleValue ? (SingleValue) this : new SingleValue( binderPrefix, propertyPath, parent );
		}

		public MapEntry asMapEntry() {
			return this instanceof MapEntry ? (MapEntry) this : new MapEntry( binderPrefix, propertyPath, parent );
		}

		public CollectionItem asCollectionItem() {
			return this instanceof CollectionItem ? (CollectionItem) this : new CollectionItem( binderPrefix, propertyPath, parent );
		}

		public static class SingleValue extends ForPath
		{
			public SingleValue( String binderPrefix, String propertyPath, EntityPropertyControlName parent ) {
				super( binderPrefix, propertyPath, parent );
			}

			@Override
			protected String binderSuffix() {
				return "";
			}

			@Override
			protected String resolveFullPropertyPath() {
				return propertyName;
			}
		}

		public static class CollectionItem extends ForPath
		{
			private Integer index;
			private Object itemKey;

			public CollectionItem( String binderPrefix, String propertyPath, EntityPropertyControlName parent ) {
				super( binderPrefix, propertyPath, parent );
			}

			public CollectionItem withIndex( Integer index ) {
				CollectionItem controlName = new CollectionItem( binderPrefix, propertyPath, parent );
				controlName.index = index;
				controlName.itemKey = itemKey;
				return controlName;
			}

			public CollectionItem withBinderItemKey( Object itemKey ) {
				CollectionItem controlName = new CollectionItem( binderPrefix, propertyPath, parent );
				controlName.index = index;
				controlName.itemKey = itemKey;
				return controlName;
			}

			@Override
			protected String binderSuffix() {
				Object ix = itemKey != null ? itemKey : index;
				return ".items[" + Objects.toString( ix, "" ) + "]";
			}

			@Override
			protected String resolveFullPropertyPath() {
				return propertyName + "[" + Objects.toString( index, "" ) + "]";
			}
		}

		public static class MapEntry extends ForPath
		{
			private Object mapKey;
			private Object entryKey;

			public MapEntry( String binderPrefix, String propertyPath, EntityPropertyControlName parent ) {
				super( binderPrefix, propertyPath, parent );
			}

			public MapEntry withMapKey( Object key ) {
				MapEntry controlName = new MapEntry( binderPrefix, propertyPath, parent );
				controlName.mapKey = key;
				controlName.entryKey = entryKey;
				return controlName;
			}

			public MapEntry withBinderEntryKey( Object entryKey ) {
				MapEntry controlName = new MapEntry( binderPrefix, propertyPath, parent );
				controlName.mapKey = mapKey;
				controlName.entryKey = entryKey;
				return controlName;
			}

			@Override
			public BinderProperty asBinderItem() {
				return asBinderEntryValue();
			}

			public BinderProperty asBinderEntryKey() {
				return new BinderProperty( ".key" );
			}

			public BinderProperty asBinderEntryValue() {
				return new BinderProperty( ".value" );
			}

			@Override
			protected String binderSuffix() {
				return ".entries[" + Objects.toString( entryKey, "" ) + "]";
			}

			@Override
			protected String resolveFullPropertyPath() {
				return propertyName + "[" + Objects.toString( mapKey, "" ) + "]";
			}
		}

		@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
		public class BinderProperty extends EntityPropertyControlName
		{
			private final String suffix;

			public BinderPropertyValue asValue() {
				return new BinderPropertyValue( "value" );
			}

			public BinderPropertyValue asInitializedValue() {
				return new BinderPropertyValue( "initializedValue" );
			}

			public String toString() {
				ForPath current = ForPath.this;
				EntityPropertyControlName parent = current.parent;

				if ( parent != null ) {
					if ( parent instanceof ForPath ) {
						ForPath parentPath = (ForPath) parent;
						return parentPath.asBinderItem() + "." + current.binderPrefix + "[" + current.propertyName + "]" + current.binderSuffix() + suffix;
					}
					else if ( parent instanceof BinderProperty ) {
						BinderProperty parentProperty = (BinderProperty) parent;
						return parentProperty.toString() + "." + current.binderPrefix + "[" + current.propertyName + "]" + current.binderSuffix() + suffix;
					}
					else if ( parent instanceof BinderPropertyValue ) {
						BinderPropertyValue parentPropertyValue = (BinderPropertyValue) parent;
						return parentPropertyValue.getBinderProperty() + "." + current.binderPrefix + "[" + current.propertyName + "]" + current
								.binderSuffix() + suffix;
					}
				}

				return current.binderPrefix + "[" + current.propertyName + "]" + current.binderSuffix() + suffix;
			}

			@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
			public class BinderPropertyValue extends EntityPropertyControlName
			{
				private final String suffix;

				public String toString() {
					return BinderProperty.this.toString() + "." + suffix;
				}

				private BinderProperty getBinderProperty() {
					return BinderProperty.this;
				}
			}
		}
	}
}
