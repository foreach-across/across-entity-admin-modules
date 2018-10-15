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

import com.foreach.across.core.support.AttributeOverridingSupport;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.Assert;

public class SimpleEntityPropertyDescriptor extends AttributeOverridingSupport implements MutableEntityPropertyDescriptor
{
	/**
	 * Original descriptor that the current descriptor is shadowing.
	 */
	private final EntityPropertyDescriptor original;

	/**
	 * Parent descriptor that this one represents a nested property of.
	 * Will make this a nested property descriptor.
	 */
	@Setter
	private EntityPropertyDescriptor parentDescriptor;

	private String name, displayName;
	private Boolean readable, writable, hidden;

	private Class<?> propertyType;
	private TypeDescriptor propertyTypeDescriptor;
	private EntityPropertyRegistry propertyRegistry;

	@Getter
	@Setter
	@NonNull
	private EntityPropertyController controller;

	public SimpleEntityPropertyDescriptor( String name ) {
		this( name, null );
	}

	@SuppressWarnings("unchecked")
	public SimpleEntityPropertyDescriptor( String name, EntityPropertyDescriptor original ) {
		Assert.notNull( name, "name is required" );
		this.name = name;
		this.original = original;

		super.setParent( original );

		if ( original != null ) {
			controller = new GenericEntityPropertyController( original.getController() );
		}
		else {
			GenericEntityPropertyController genericController = new GenericEntityPropertyController();
			genericController.createValueSupplier( () -> BeanUtils.instantiate( this.getPropertyType() ) );

			this.controller = genericController;
		}
	}

	/**
	 * @return Property name.
	 */
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getTargetPropertyName() {
		return isNestedProperty() ? StringUtils.removeStart( getName(), getParentDescriptor().getName() + "." ) : getName();
	}

	@Override
	public String getDisplayName() {
		return displayName != null ? displayName : ( original != null ? original.getDisplayName() : null );
	}

	@Override
	public void setDisplayName( String displayName ) {
		this.displayName = displayName;
	}

	@Override
	public boolean isReadable() {
		return readable != null ? readable : ( original != null && original.isReadable() );
	}

	@Override
	public void setReadable( boolean readable ) {
		this.readable = readable;
	}

	@Override
	public boolean isWritable() {
		return writable != null ? writable : ( original != null && original.isWritable() );
	}

	@Override
	public void setWritable( boolean writable ) {
		this.writable = writable;
	}

	@Override
	public boolean isHidden() {
		return hidden != null ? hidden : ( original != null && original.isHidden() );
	}

	@Override
	public void setHidden( boolean hidden ) {
		this.hidden = hidden;
	}

	@Override
	public Class<?> getPropertyType() {
		return propertyType != null ? propertyType : ( original != null ? original.getPropertyType() : null );
	}

	@Override
	public void setPropertyType( Class<?> propertyType ) {
		this.propertyType = propertyType;
		if ( propertyType != null ) {
			propertyTypeDescriptor = TypeDescriptor.valueOf( propertyType );
		}
	}

	public TypeDescriptor getPropertyTypeDescriptor() {
		return propertyTypeDescriptor != null
				? propertyTypeDescriptor : ( original != null ? original.getPropertyTypeDescriptor() : null );
	}

	public void setPropertyTypeDescriptor( TypeDescriptor propertyTypeDescriptor ) {
		this.propertyTypeDescriptor = propertyTypeDescriptor;
		if ( propertyTypeDescriptor != null ) {
			propertyType = propertyTypeDescriptor.getType();
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object getPropertyValue( Object entity ) {
		if ( entity != null ) {
			return controller.fetchValue( EntityPropertyBindingContext.forReading( entity ) );
		}
		return null;
	}

	@Override
	public ValueFetcher getValueFetcher() {
		return this::getPropertyValue;
	}

	@Override
	@Deprecated
	@SuppressWarnings("all")
	public void setValueFetcher( ValueFetcher<?> valueFetcher ) {
		if ( controller instanceof ConfigurableEntityPropertyController ) {
			( (ConfigurableEntityPropertyController) controller ).withTarget( Object.class, Object.class )
			                                                     .valueFetcher( ( (ValueFetcher) valueFetcher )::getValue );
		}
		else {
			throw new IllegalStateException( "Unable to set value fetcher on a non-ConfigurableEntityPropertyController" );
		}

		if ( readable == null && original == null && valueFetcher != null ) {
			readable = true;
		}
	}

	@Override
	public EntityPropertyRegistry getPropertyRegistry() {
		return propertyRegistry;
	}

	@Override
	public void setPropertyRegistry( EntityPropertyRegistry propertyRegistry ) {
		this.propertyRegistry = propertyRegistry;
	}

	@Override
	public boolean isNestedProperty() {
		return parentDescriptor != null || ( original != null && original.isNestedProperty() );
	}

	@Override
	public EntityPropertyDescriptor getParentDescriptor() {
		return parentDescriptor != null ? parentDescriptor : ( original != null ? original.getParentDescriptor() : null );
	}

	@Override
	public String toString() {
		return name;
	}
}
