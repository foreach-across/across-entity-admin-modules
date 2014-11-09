package com.foreach.across.modules.entity.business;

public interface FormElement
{
	void setEntity( Object entity );

	String getName();

	void setValue( Object value );

	String getElementType();
}
