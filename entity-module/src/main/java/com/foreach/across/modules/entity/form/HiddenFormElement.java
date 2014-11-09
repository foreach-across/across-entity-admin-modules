package com.foreach.across.modules.entity.form;

import com.foreach.across.modules.entity.business.FormElement;

import java.beans.PropertyDescriptor;

public class HiddenFormElement implements FormElement
{
	private final PropertyDescriptor propertyDescriptor;
	private Object entity;

	private String name, label;
	private Object value;

	public HiddenFormElement( PropertyDescriptor propertyDescriptor ) {
		this.propertyDescriptor = propertyDescriptor;

		setName( propertyDescriptor.getName() );
		setLabel( propertyDescriptor.getDisplayName() );
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel( String label ) {
		this.label = label;
	}

	public Object getValue() {
		return value;
	}

	@Override
	public void setValue( Object value ) {
		this.value = value;
	}

	@Override
	public void setEntity( Object entity ) {
		this.entity = entity;
	}

	@Override
	public String getElementType() {
		return "hidden";
	}

}
