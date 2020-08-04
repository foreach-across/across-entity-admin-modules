package com.foreach.across.modules.experimental.modals.support.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Handles the result of an {@link ActionAttribute}.
 *
 * @param <T> inheriting type
 */
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class ActionHandlerAttribute<T extends ActionHandlerAttribute>
{
	@NonNull
	@JsonProperty
	private String type;

	public T type( String type ) {
		this.type = type;
		return (T) this;
	}
}
