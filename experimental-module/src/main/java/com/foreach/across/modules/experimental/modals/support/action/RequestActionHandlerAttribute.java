package com.foreach.across.modules.experimental.modals.support.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true, fluent = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestActionHandlerAttribute extends ActionHandlerAttribute<RequestActionHandlerAttribute>
{
	@JsonProperty
	private String source;

	@JsonProperty
	private String target;

	public interface Type
	{
		String REQUEST_CONTENT = "exm:request-content";
		String MOVE = "exm:move";
		String CLEAR = "exm:clear";
		String REMOVE = "exm:remove";
		String CLOSE = "exm:close";
	}
}
