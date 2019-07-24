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
package com.foreach.across.modules.entity.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author Arne Vandamme
 */
@Getter
@Setter
@Accessors(chain = true)
public class EntityQueryCondition implements EntityQueryExpression
{
	private String property;
	@NonNull
	private EntityQueryOps operand;
	private Object[] arguments = new Object[0];
	private boolean translated;

	protected EntityQueryCondition() {
	}

	public EntityQueryCondition( String property, EntityQueryOps operand, @NonNull Object... arguments ) {
		this.property = property;
		this.operand = operand;
		this.arguments = arguments;
	}

	public EntityQueryCondition setArguments( @NonNull Object[] arguments ) {
		this.arguments = arguments.clone();
		return this;
	}

	public boolean hasArguments() {
		return arguments.length > 0;
	}

	@JsonIgnore
	public Object getFirstArgument() {
		return arguments.length > 0 ? arguments[0] : null;
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}

		EntityQueryCondition that = (EntityQueryCondition) o;

		// Probably incorrect - comparing Object[] arrays with Arrays.equals
		return Arrays.equals( arguments, that.arguments ) && Objects.equals( operand, that.operand ) && Objects.equals( property, that.property );
	}

	@Override
	public int hashCode() {
		return Objects.hash( property, operand, arguments );
	}

	@Override
	public String toString() {
		return operand.toString( property, arguments );
	}
}
