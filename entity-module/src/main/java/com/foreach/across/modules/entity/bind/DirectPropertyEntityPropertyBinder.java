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

import com.foreach.across.modules.entity.registry.properties.EntityPropertyBindingContext;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.support.EntityPropertyDescriptorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.Errors;

/**
 * Dummy implementation that wraps a {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyHandlingType#DIRECT} property
 * as a {@link EntityPropertyBinder}, but does not allow any interaction. Entirely meant for internal use, instances of this class should
 * never be exposed outside of the {@link EntityPropertiesBinder}.
 *
 * @author Arne Vandamme
 * @since 3.2.0
 */
@SuppressWarnings({ "OptionalAssignedToNull", "OptionalUsedAsFieldOrParameterType" })
@RequiredArgsConstructor
final class DirectPropertyEntityPropertyBinder implements EntityPropertyBinder
{
	private final EntityPropertyBindingContext bindingContext;
	private final EntityPropertyDescriptor descriptor;

	@Override
	public long getSortIndex() {
		return 0;
	}

	@Override
	public void setSortIndex( long sortIndex ) {

	}

	@Override
	public void setBound( boolean bound ) {

	}

	@Override
	public boolean isBound() {
		return false;
	}

	@Override
	public int getControllerOrder() {
		return 0;
	}

	@Override
	public Object getOriginalValue() {
		return getValue();
	}

	@Override
	public Object getInitializedValue() {
		return getValue();
	}

	@Override
	public Object getValue() {
		return descriptor.getController().fetchValue( bindingContext );
	}

	@Override
	public void setValue( Object value ) {
		throw new UnsupportedOperationException( "setValue" );
	}

	@Override
	public void setDeleted( boolean deleted ) {
		throw new UnsupportedOperationException( "setDeleted" );
	}

	@Override
	public boolean isDeleted() {
		return false;
	}

	@Override
	public boolean isModified() {
		return false;
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public Object createNewValue() {
		return null;
	}

	@Override
	public boolean applyValue() {
		return false;
	}

	@Override
	public boolean save() {
		return false;
	}

	@Override
	public boolean validate( Errors errors, Object... validationHints ) {
		return false;
	}

	@Override
	public EntityPropertyBinder resolvePropertyBinder( EntityPropertyDescriptor descriptor ) {
		EntityPropertyDescriptor directChildProperty = EntityPropertyDescriptorUtils.findDirectChild( descriptor, this.descriptor );

		if ( directChildProperty != null ) {
			EntityPropertyBinder childBinder = new DirectPropertyEntityPropertyBinder( bindingContext, directChildProperty );
			return descriptor == directChildProperty ? childBinder : childBinder.resolvePropertyBinder( descriptor );
		}

		return null;
	}

	@Override
	public void enableBinding( boolean enabled ) {
		throw new UnsupportedOperationException( "enableBinding" );
	}
}
