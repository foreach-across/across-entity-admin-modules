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
public class SimpleActionHandlerAttribute extends ActionHandlerAttribute<SimpleActionHandlerAttribute>
{
	@JsonProperty
	private String source;

	@JsonProperty
	private String target;

	public static SimpleActionHandlerAttribute simpleActionHandler() {
		return new SimpleActionHandlerAttribute();
	}

	public static SimpleActionHandlerAttribute requestContentHandler() {
		return simpleActionHandler()
				.type( Type.REQUEST_CONTENT );
	}

	public static SimpleActionHandlerAttribute moveHandler() {
		return simpleActionHandler()
				.type( Type.MOVE );
	}

	public static SimpleActionHandlerAttribute removeHandler() {
		return simpleActionHandler()
				.type( Type.REMOVE );
	}

	public static SimpleActionHandlerAttribute clearHandler() {
		return simpleActionHandler()
				.type( Type.CLEAR );
	}

	public static SimpleActionHandlerAttribute closeModalHandler() {
		return simpleActionHandler()
				.type( Type.CLOSE_MODAL );
	}

	public interface Type
	{
		String REQUEST_CONTENT = "exm:request-content";
		String MOVE = "exm:move";
		String CLEAR = "exm:clear";
		String REMOVE = "exm:remove";
		String CLOSE_MODAL = "exm:modal:close";
	}
}
