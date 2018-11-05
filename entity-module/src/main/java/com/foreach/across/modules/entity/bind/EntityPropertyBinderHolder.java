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

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.validation.Errors;

/**
 * Simple proxy for ad hoc replacement of binders. Used to switch from {@link DirectPropertyEntityPropertyBinder}
 * to an actual {@link AbstractEntityPropertyBinder}. Which should happen as soon as a binder is explicitly requested.
 *
 * @author Arne Vandamme
 * @since 3.2.0
 */
final class EntityPropertyBinderHolder implements EntityPropertyBinder
{
	@Setter
	@Getter
	@NonNull
	private EntityPropertyBinder target;

	@Override
	public int getSortIndex() {
		return target.getSortIndex();
	}

	@Override
	public void setSortIndex( int sortIndex ) {
		target.setSortIndex( sortIndex );
	}

	@Override
	public void setBound( boolean bound ) {
		target.setBound( bound );
	}

	@Override
	public boolean isBound() {
		return target.isBound();
	}

	@Override
	public int getControllerOrder() {
		return target.getControllerOrder();
	}

	@Override
	public Object getOriginalValue() {
		return target.getOriginalValue();
	}

	@Override
	public Object getValue() {
		return target.getValue();
	}

	@Override
	public Object getInitializedValue() {
		return target.getInitializedValue();
	}

	@Override
	public void setValue( Object value ) {
		target.setValue( value );
	}

	@Override
	public void setDeleted( boolean deleted ) {
		target.setDeleted( deleted );
	}

	@Override
	public boolean isDeleted() {
		return target.isDeleted();
	}

	@Override
	public boolean isModified() {
		return target.isModified();
	}

	@Override
	public boolean isDirty() {
		return target.isDirty();
	}

	@Override
	public Object createNewValue() {
		return target.createNewValue();
	}

	@Override
	public boolean applyValue() {
		return target.applyValue();
	}

	@Override
	public boolean save() {
		return target.save();
	}

	@Override
	public boolean validate( Errors errors, Object... validationHints ) {
		return target.validate( errors, validationHints );
	}

	@Override
	public EntityPropertyBinder resolvePropertyBinder( EntityPropertyDescriptor descriptor ) {
		return target.resolvePropertyBinder( descriptor );
	}

	@Override
	public void enableBinding( boolean enabled ) {
		target.enableBinding( enabled );
	}

	boolean isActualBinder() {
		return target instanceof AbstractEntityPropertyBinder;
	}
}
