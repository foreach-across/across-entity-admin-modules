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
package com.foreach.across.modules.entity.registry;

import com.foreach.across.core.support.AttributeSupport;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.EntityViewFactory;

import java.util.stream.Stream;

/**
 * @author Arne Vandamme
 */
public class EntityAssociationImpl extends AttributeSupport implements MutableEntityAssociation
{
	private final String name;
	private final MutableEntityConfiguration sourceEntityConfiguration;

	private Boolean hidden;
	private EntityConfiguration targetEntityConfiguration;
	private EntityPropertyDescriptor sourceProperty, targetProperty;
	private ParentDeleteMode parentDeleteMode = ParentDeleteMode.SUPPRESS;
	private Type associationType = Type.LINKED;

	public EntityAssociationImpl( String name,
	                              MutableEntityConfiguration sourceEntityConfiguration ) {
		this.name = name;
		this.sourceEntityConfiguration = sourceEntityConfiguration;
	}

	@Override
	public Type getAssociationType() {
		return associationType;
	}

	@Override
	public void setAssociationType( Type associationType ) {
		this.associationType = associationType;
	}

	@Override
	public void setHidden( Boolean hidden ) {
		this.hidden = hidden;
	}

	@Override
	public boolean isHidden() {
		if ( hidden != null ) {
			return hidden;
		}

		if ( sourceEntityConfiguration.isHidden() ) {
			return true;
		}

		return targetEntityConfiguration != null && targetEntityConfiguration.isHidden();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class<?> getEntityType() {
		return targetEntityConfiguration.getEntityType();
	}

	@Override
	public EntityConfiguration getSourceEntityConfiguration() {
		return sourceEntityConfiguration;
	}

	@Override
	public void setTargetEntityConfiguration( EntityConfiguration targetEntityConfiguration ) {
		this.targetEntityConfiguration = targetEntityConfiguration;
	}

	@Override
	public EntityConfiguration getTargetEntityConfiguration() {
		return targetEntityConfiguration;
	}

	@Override
	public EntityPropertyDescriptor getSourceProperty() {
		return sourceProperty;
	}

	@Override
	public void setSourceProperty( EntityPropertyDescriptor sourceProperty ) {
		this.sourceProperty = sourceProperty;
	}

	@Override
	public EntityPropertyDescriptor getTargetProperty() {
		return targetProperty;
	}

	@Override
	public void setTargetProperty( EntityPropertyDescriptor targetProperty ) {
		this.targetProperty = targetProperty;
	}

	@Override
	public ParentDeleteMode getParentDeleteMode() {
		return parentDeleteMode;
	}

	@Override
	public void setParentDeleteMode( ParentDeleteMode parentDeleteMode ) {
		this.parentDeleteMode = parentDeleteMode;
	}

	@Override
	public boolean hasView( String name ) {
		return sourceEntityConfiguration.hasView( buildAssociatedViewName( name ) );
	}

	@Override
	@SuppressWarnings("unchecked")
	public <Y extends EntityViewFactory> Y getViewFactory( String viewName ) {
		return (Y) sourceEntityConfiguration.getViewFactory( buildAssociatedViewName( viewName ) );
	}

	@Override
	public void registerView( String viewName, EntityViewFactory viewFactory ) {
		sourceEntityConfiguration.registerView( buildAssociatedViewName( viewName ), viewFactory );
	}

	@Override
	public String[] getViewNames() {
		String prefix = getName() + "_";
		return Stream.of( sourceEntityConfiguration.getViewNames() )
		             .filter( viewName -> viewName.startsWith( prefix ) )
		             .map( viewName -> viewName.substring( prefix.length() ) )
		             .toArray( String[]::new );
	}

	private String buildAssociatedViewName( String viewName ) {
		return getName() + "_" + viewName;
	}
}
