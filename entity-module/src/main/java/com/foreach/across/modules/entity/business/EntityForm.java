package com.foreach.across.modules.entity.business;

import com.foreach.across.modules.entity.util.EntityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;

import java.util.Collection;
import java.util.LinkedList;

public class EntityForm
{
	private Collection<FormElement> elements = new LinkedList<>();

	public void setEntity( Object instance ) {
		for ( FormElement element : elements ) {
			element.setValue(
					EntityUtils.getPropertyValue(
							BeanUtils.getPropertyDescriptor( ClassUtils.getUserClass( instance.getClass() ),
							                                 element.getName() ),
							instance
					)
			);
		}
	}

	public void addElement( FormElement formElement ) {
		elements.add( formElement );
	}

	public Collection<FormElement> getElements() {
		return elements;
	}
}
