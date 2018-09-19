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
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
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

	public static ForProperty forProperty( @NonNull String propertyName ) {
		return forProperty( propertyName, DEFAULT_BINDER_PREFIX );
	}

	public static ForProperty forProperty( @NonNull String propertyName, @NonNull String binderPrefix ) {
		return new ForProperty.SingleValue( binderPrefix, propertyName, null );
	}

	public static ForProperty forProperty( @NonNull EntityPropertyDescriptor descriptor ) {
		return forProperty( descriptor, DEFAULT_BINDER_PREFIX );
	}

	public static ForProperty forProperty( @NonNull EntityPropertyDescriptor descriptor, @NonNull String binderPrefix ) {
		if ( EntityPropertyHandlingType.forProperty( descriptor ) == EntityPropertyHandlingType.MANUAL ) {
			String propertyName = Objects.toString( descriptor.getAttribute( EntityAttributes.CONTROL_NAME ), descriptor.getName() );
			return (ForProperty.SingleValue) forProperty( propertyName, binderPrefix ).forHandlingType( EntityPropertyHandlingType.MANUAL );
		}

		if ( descriptor.isNestedProperty() ) {
			EntityPropertyDescriptor parentDescriptor = descriptor.getParentDescriptor();
			ForProperty parent = forProperty( parentDescriptor, binderPrefix );

			return parent.forHandlingType( EntityPropertyHandlingType.forProperty( parentDescriptor ) ).asChildProperty( descriptor );
		}

		return forProperty( descriptor.getName(), binderPrefix );
	}

	public static ForProperty forProperty( @NonNull EntityPropertyDescriptor descriptor, @NonNull ViewElementBuilderContext builderContext ) {
		EntityPropertyControlName parentControlName = builderContext.getAttribute( EntityPropertyControlName.class );

		if ( parentControlName != null ) {
			return parentControlName.asChildProperty( descriptor );
		}

		return forProperty( descriptor );
	}

	public ForProperty asChildProperty( @NonNull String propertyName ) {
		return new ForProperty.SingleValue( DEFAULT_BINDER_PREFIX, propertyName, this );
	}

	public ForProperty asChildProperty( @NonNull EntityPropertyDescriptor descriptor ) {
		if ( EntityPropertyHandlingType.forProperty( descriptor ) == EntityPropertyHandlingType.MANUAL ) {
			String propertyName = Objects.toString( descriptor.getAttribute( EntityAttributes.CONTROL_NAME ), descriptor.getName() );
			return (ForProperty.SingleValue) asChildProperty( propertyName ).forHandlingType( EntityPropertyHandlingType.MANUAL );
		}

		if ( descriptor.isNestedProperty() ) {
			String currentPropertyPrefix = resolveFullPropertyPath() + ".";
			return asChildProperty( StringUtils.removeStart( descriptor.getName(), currentPropertyPrefix ) );
		}

		return asChildProperty( descriptor.getName() );
	}

	protected abstract String resolveFullPropertyPath();

	@EqualsAndHashCode(callSuper = true)
	public abstract static class ForProperty extends EntityPropertyControlName
	{
		protected final String binderPrefix;
		protected final String propertyPath;
		protected final String propertyName;
		protected final EntityPropertyControlName parent;

		public ForProperty( String binderPrefix, String propertyPath, boolean manualProperty, EntityPropertyControlName parent ) {
			this.binderPrefix = binderPrefix;
			this.propertyPath = propertyPath;
			this.parent = parent;
			this.propertyName = !manualProperty ? StringUtils.removeEnd( propertyPath, EntityPropertyRegistry.INDEXER ) : propertyPath;
		}

		public ForProperty asDirectProperty() {
			return this;
		}

		public BinderProperty asBinderItem() {
			return new BinderProperty( "" );
		}

		/**
		 * @return
		 */
		public EntityPropertyControlName forHandlingType( @NonNull EntityPropertyHandlingType handlingType ) {
			switch ( handlingType ) {
				case EXTENSION:
					return asBinderItem();
				case MANUAL:
					return new SingleValue( propertyPath );
			}

			return asDirectProperty();
		}

		public String toString() {
			String path = resolvePropertyPathSegment();

			if ( parent != null ) {
				if ( parent instanceof BinderProperty ) {
					BinderProperty parentProperty = (BinderProperty) parent;
					return parentProperty.withInitializedValue() + "." + path;
				}
				else {
					return parent.toString() + "." + path;
				}
			}

			return path;
		}

		protected abstract String binderSuffix();

		protected abstract String resolvePropertyPathSegment();

		@Override
		protected String resolveFullPropertyPath() {
			return parent != null ? parent.resolveFullPropertyPath() + "." + propertyPath : propertyPath;
		}

		public SingleValue asSingleValue() {
			return this instanceof SingleValue ? (SingleValue) this : new SingleValue( binderPrefix, propertyPath, parent );
		}

		public MapEntry asMapEntry() {
			return this instanceof MapEntry ? (MapEntry) this : new MapEntry( binderPrefix, propertyPath, parent );
		}

		public CollectionItem asCollectionItem() {
			return this instanceof CollectionItem ? (CollectionItem) this : new CollectionItem( binderPrefix, propertyPath, parent );
		}

		@EqualsAndHashCode(callSuper = true)
		public static class SingleValue extends ForProperty
		{
			public SingleValue( String binderPrefix, String propertyPath, EntityPropertyControlName parent ) {
				super( binderPrefix, propertyPath, false, parent );
			}

			private SingleValue( String propertyPath ) {
				super( DEFAULT_BINDER_PREFIX, propertyPath, true, null );
			}

			@Override
			protected String binderSuffix() {
				return "";
			}

			@Override
			protected String resolvePropertyPathSegment() {
				return propertyName;
			}
		}

		@EqualsAndHashCode(callSuper = true)
		public static class CollectionItem extends ForProperty
		{
			private Integer index;
			private Object itemKey;

			public CollectionItem( String binderPrefix, String propertyPath, EntityPropertyControlName parent ) {
				super( binderPrefix, propertyPath, false, parent );
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
			protected String resolvePropertyPathSegment() {
				return propertyName + "[" + Objects.toString( index, "" ) + "]";
			}
		}

		@EqualsAndHashCode(callSuper = true)
		public static class MapEntry extends ForProperty
		{
			private Object mapKey;
			private Object entryKey;

			public MapEntry( String binderPrefix, String propertyPath, EntityPropertyControlName parent ) {
				super( binderPrefix, propertyPath, false, parent );
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
			protected String resolvePropertyPathSegment() {
				return propertyName + "[" + Objects.toString( mapKey, "" ) + "]";
			}
		}

		@EqualsAndHashCode(callSuper = true)
		@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
		public class BinderProperty extends EntityPropertyControlName
		{
			private final String suffix;

			public BinderPropertyValue withValue() {
				return new BinderPropertyValue( "value" );
			}

			public BinderPropertyValue withInitializedValue() {
				return new BinderPropertyValue( "initializedValue" );
			}

			@Override
			protected String resolveFullPropertyPath() {
				return ForProperty.this.resolveFullPropertyPath();
			}

			private String binderItemPath() {
				ForProperty current = ForProperty.this;
				EntityPropertyControlName parent = current.parent;

				if ( parent != null ) {
					if ( parent instanceof ForProperty ) {
						ForProperty parentPath = (ForProperty) parent;
						return parentPath.asBinderItem().binderItemPath() + "." + current.binderPrefix + "[" + current.propertyName + "]" + current
								.binderSuffix() + suffix;
					}
					else if ( parent instanceof BinderProperty ) {
						BinderProperty parentProperty = (BinderProperty) parent;
						return parentProperty.binderItemPath() + "." + current.binderPrefix + "[" + current.propertyName + "]" + current
								.binderSuffix() + suffix;
					}
					else if ( parent instanceof BinderPropertyValue ) {
						BinderPropertyValue parentPropertyValue = (BinderPropertyValue) parent;
						return parentPropertyValue.getBinderProperty().binderItemPath() + "." + current.binderPrefix
								+ "[" + current.propertyName + "]" + current.binderSuffix() + suffix;
					}
				}

				return current.binderPrefix + "[" + current.propertyName + "]" + current.binderSuffix() + suffix;
			}

			@Override
			public String toString() {
				return withValue().toString();
			}

			@EqualsAndHashCode(callSuper = true)
			@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
			public class BinderPropertyValue extends EntityPropertyControlName
			{
				private final String suffix;

				public String toString() {
					return BinderProperty.this.binderItemPath() + "." + suffix;
				}

				@Override
				protected String resolveFullPropertyPath() {
					return ForProperty.this.resolveFullPropertyPath();
				}

				private BinderProperty getBinderProperty() {
					return BinderProperty.this;
				}
			}
		}
	}
}
