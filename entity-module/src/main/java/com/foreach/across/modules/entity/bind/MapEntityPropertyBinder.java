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

import com.foreach.across.modules.entity.registry.properties.EntityPropertyController;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.validation.Errors;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a property value that is a {@link java.util.Map} implementation.
 *
 * @author Arne Vandamme
 * @see SingleEntityPropertyBinder
 * @see ListEntityPropertyBinder
 * @since 3.2.0
 */
@SuppressWarnings("Duplicates")
public final class MapEntityPropertyBinder extends AbstractEntityPropertyBinder
{
	private final EntityPropertiesBinder binder;
	private final EntityPropertyDescriptor collectionDescriptor;
	private final TypeDescriptor collectionTypeDescriptor;
	private final EntityPropertyController collectionController;

	private final EntityPropertyDescriptor valueDescriptor;
	private final TypeDescriptor valueTypeDescriptor;

	private final EntityPropertyDescriptor keyDescriptor;
	private final TypeDescriptor keyTypeDescriptor;

	private boolean bindingBusy;
	private boolean entriesInitialized;
	private boolean initializedValuePathWasUsed;

	private Entry template;
	private Map<String, Entry> entries;

	/**
	 * If set to {@code true}, the existing entries will always be returned when performing data binding,
	 * and every entry separately can be removed/modified. If {@code false}, then it is expected that all
	 * entries are passed when data binding (replacing them). The latter is the default mode.
	 */
	@Getter
	@Setter
	private boolean updateEntriesOnBinding;

	MapEntityPropertyBinder( EntityPropertiesBinder binder,
	                         EntityPropertyDescriptor collectionDescriptor,
	                         EntityPropertyDescriptor keyDescriptor,
	                         EntityPropertyDescriptor valueDescriptor ) {
		super( binder, collectionDescriptor.getController() );
		this.binder = binder;
		this.collectionDescriptor = collectionDescriptor;
		this.valueDescriptor = valueDescriptor;
		this.keyDescriptor = keyDescriptor;

		collectionTypeDescriptor = collectionDescriptor.getPropertyTypeDescriptor();
		valueTypeDescriptor = valueDescriptor != null
				? valueDescriptor.getPropertyTypeDescriptor()
				: collectionTypeDescriptor.getMapValueTypeDescriptor();
		keyTypeDescriptor = keyDescriptor != null
				? keyDescriptor.getPropertyTypeDescriptor()
				: collectionTypeDescriptor.getMapKeyTypeDescriptor();

		collectionController = collectionDescriptor.getController();
	}

	/**
	 * Initialized template entry for a single member of this map.
	 * Not meant to be modified but can be used to create blank values for a new member.
	 *
	 * @return template entry controller
	 */
	public Entry getTemplate() {
		if ( template == null ) {
			template = createEntry( "" );
			Object keyValue = binder.getTemplateValueResolver().resolveTemplateValue( binder.asBindingContext(), keyDescriptor );
			template.getKeyInternal().setOriginalValue( keyValue );
			Object valueValue = binder.getTemplateValueResolver().resolveTemplateValue( binder.asBindingContext(), valueDescriptor );
			template.getValueInternal().setOriginalValue( valueValue );
		}
		return template;
	}

	@Override
	public boolean isDirty() {
		if ( entries != null ) {
			for ( Entry value : entries.values() ) {
				if ( value.isDirty() ) {
					return true;
				}
			}
		}

		return super.isDirty();
	}

	@Override
	public Object getInitializedValue() {
		initializedValuePathWasUsed = true;

		Object originalValue = loadOriginalValue();

		if ( !entriesInitialized && originalValue == null ) {
			setValue( createNewValue() );
		}

		return getValue();
	}

	@Override
	public boolean isModified() {
		return isDeleted() || ( ( isBound() || entriesInitialized ) && !Objects.equals( loadOriginalValue(), getValue() ) );
	}

	public Map<String, Entry> getEntries() {
		if ( entries == null ) {
			val originalValue = loadOriginalValue();

			entries = new Entries();

			if ( ( !bindingBusy || updateEntriesOnBinding ) && collectionController != null ) {
				setValueInternal( originalValue );
			}
		}

		entriesInitialized = true;

		return entries;
	}

	/**
	 * Get an ordered - unmodifiable - list of all the entries in the map.
	 * These will be sorted according to the value of {@link Entry#getSortIndex()}.
	 * <p/>
	 * Includes all entries, including ones where {@link Entry#isDeleted()} is {@code true}.
	 *
	 * @return unmodifiable list of all entries in order
	 */
	public List<Entry> getEntryList() {
		return Collections.unmodifiableList(
				getEntries()
						.values()
						.stream()
						.sorted( Comparator.comparingLong( Entry::getSortIndex ) )
						.collect( Collectors.toList() )
		);
	}

	@Override
	public Object getValue() {
		LinkedHashMap<Object, Object> map = new LinkedHashMap<>();

		if ( !isDeleted() ) {
			// not using Collectors.toMap() here as that one does not allow null values
			getEntries()
					.values()
					.stream()
					.filter( e -> !e.isDeleted() )
					.sorted( Comparator.comparingLong( Entry::getSortIndex ) )
					.forEach( entry -> map.put( entry.getKey().getValue(), entry.getValue().getValue() ) );
		}

		return binder.convertIfNecessary( map, collectionTypeDescriptor, getBinderPath( "entries" ) );
	}

	@Override
	void setValueInternal( Object value ) {
		entriesInitialized = true;

		if ( entries == null ) {
			loadOriginalValue();
			entries = new Entries();
		}
		entries.clear();

		if ( value != null ) {
			Map<?, ?> values = (Map) binder.convertIfNecessary( value,
			                                                    TypeDescriptor.map( LinkedHashMap.class, keyTypeDescriptor, valueTypeDescriptor ),
			                                                    LinkedHashMap.class,
			                                                    getBinderPath( "value" ) );

			int index = 0;
			for ( val originalEntry : values.entrySet() ) {
				String key = "" + index;
				Entry newEntry = new Entry( key, binder.createPropertyBinder( keyDescriptor ), binder.createPropertyBinder( valueDescriptor ) );
				newEntry.getKeyInternal().setOriginalValue( originalEntry.getKey() );
				newEntry.getValueInternal().setOriginalValue( originalEntry.getValue() );
				if ( super.isDirty() ) {
					newEntry.getKey().setValue( originalEntry.getKey() );
					newEntry.getValue().setValue( originalEntry.getValue() );
				}
				else {
					newEntry.getKeyInternal().setValueInternal( originalEntry.getKey() );
					newEntry.getValueInternal().setValueInternal( originalEntry.getValue() );
				}
				newEntry.setSortIndexInternal( index++ );
				entries.put( key, newEntry );
			}
		}
	}

	@Override
	public boolean validate( Errors errors, Object... validationHints ) {
		int beforeValidate = errors.getErrorCount();

		getEntries()
				.forEach( ( key, entry ) -> {
					try {
						errors.pushNestedPath( "entries[" + key + "].key" );
						entry.getKey().validate( errors, validationHints );
					}
					finally {
						errors.popNestedPath();
					}
					try {
						errors.pushNestedPath( "entries[" + key + "].value" );
						entry.getValue().validate( errors, validationHints );
					}
					finally {
						errors.popNestedPath();
					}
				} );

		if ( collectionController != null ) {
			try {
				errors.pushNestedPath( initializedValuePathWasUsed ? "initializedValue" : "value" );
				collectionController.validate(
						binder.getValueBindingContext(), new EntityPropertyValue<>( loadOriginalValue(), getValue(), isDeleted() ), errors, validationHints
				);
			}
			finally {
				errors.popNestedPath();
			}
		}

		return beforeValidate >= errors.getErrorCount();
	}

	@Override
	public boolean save() {
		boolean saved = false;

		if ( !isDeleted() ) {
			for ( Entry entry : getEntryList() ) {
				saved |= entry.getKey().save();
				saved |= entry.getValue().save();
			}
		}

		saved |= super.save();

		return saved;
	}

	/**
	 * While binding is enabled, the entries collection will not remove any (possibly) deleted entries.
	 * When explicitly disabling binding, the entries will be cleared of any deleted entries and will be
	 * fully cleared if the property itself is deleted.
	 *
	 * @param enabled true if in binding mode, false if not expecting any more changes
	 */
	@Override
	public void enableBinding( boolean enabled ) {
		bindingBusy = enabled;

		if ( !enabled ) {
			if ( isDeleted() ) {
				if ( entries == null ) {
					getEntries();
				}
				entries.clear();
			}
			else if ( entries != null ) {
				entries.entrySet().removeIf( e -> e.getValue().isDeleted() );
			}
		}
	}

	@Override
	public EntityPropertyBinder resolvePropertyBinder( EntityPropertyDescriptor descriptor ) {
		if ( StringUtils.equals( descriptor.getName(), collectionDescriptor.getName() ) ) {
			return this;
		}

		return null;
	}

	private Entry createEntry( String key ) {
		Entry entry = new Entry( key, binder.createPropertyBinder( keyDescriptor ), binder.createPropertyBinder( valueDescriptor ) );

		if ( String.class.equals( keyTypeDescriptor.getObjectType() ) ) {
			entry.getKeyInternal().setValueInternal( key );
		}

		return entry;
	}

	/**
	 * Creates a new entry for every key requested.
	 */
	class Entries extends TreeMap<String, com.foreach.across.modules.entity.bind.MapEntityPropertyBinder.Entry>
	{
		@Override
		public com.foreach.across.modules.entity.bind.MapEntityPropertyBinder.Entry get( Object key ) {
			String entryKey = (String) key;
			com.foreach.across.modules.entity.bind.MapEntityPropertyBinder.Entry entry = super.get( entryKey );

			if ( entry == null ) {
				entry = createEntry( entryKey );
				markDirty();
				super.put( entryKey, entry );
			}

			return entry;
		}
	}

	/**
	 * Single entry in a map property.
	 */
	@RequiredArgsConstructor
	@SuppressWarnings("all")
	public class Entry
	{
		@Getter
		private final String entryKey;

		@Getter
		private boolean deleted;

		@Getter
		private long sortIndex;

		@Getter
		private final EntityPropertyBinder key;

		@Getter
		private final EntityPropertyBinder value;

		public void setDeleted( boolean deleted ) {
			this.deleted = deleted;
			markDirty();
		}

		public void setSortIndex( long sortIndex ) {
			if ( sortIndex != this.sortIndex ) {
				this.sortIndex = sortIndex;
				markDirty();
			}
		}

		private void setSortIndexInternal( long sortIndex ) {
			this.sortIndex = sortIndex;
		}

		private boolean isDirty() {
			return key.isDirty() || value.isDirty();
		}

		private AbstractEntityPropertyBinder getKeyInternal() {
			return (AbstractEntityPropertyBinder) key;
		}

		private AbstractEntityPropertyBinder getValueInternal() {
			return (AbstractEntityPropertyBinder) value;
		}
	}
}
