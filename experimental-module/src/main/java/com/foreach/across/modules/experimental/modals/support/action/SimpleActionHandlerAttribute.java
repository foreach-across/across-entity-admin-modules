package com.foreach.across.modules.experimental.modals.support.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true, fluent = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SimpleActionHandlerAttribute extends ActionHandlerAttribute<SimpleActionHandlerAttribute>
{
	/**
	 * Optional CSS3 selector of the source element holding the input for the handler.
	 */
	@JsonProperty
	private String source;

	/**
	 * CSS3 selector of the target element to which the handler should be applied.
	 */
	@JsonProperty
	@NonNull
	private String target;

	public static SimpleActionHandlerAttribute simpleActionHandler() {
		return new SimpleActionHandlerAttribute();
	}

	/**
	 * {@link SimpleActionHandlerAttribute} that replaces the content of the target element with the content of the response.
	 * If a source is defined, the target content is replaced by the content of the source instead.
	 */
	public static SimpleActionHandlerAttribute responseContentHandler() {
		return simpleActionHandler()
				.type( Type.RESPONSE_CONTENT );
	}

	/**
	 * {@link SimpleActionHandlerAttribute} that moves the source element into the target element.
	 */
	public static SimpleActionHandlerAttribute moveHandler() {
		return simpleActionHandler()
				.type( Type.MOVE );
	}

	/**
	 * {@link SimpleActionHandlerAttribute} that removes the target element from the DOM.
	 */
	public static SimpleActionHandlerAttribute removeHandler( String target ) {
		return simpleActionHandler()
				.type( Type.REMOVE )
				.target( target );
	}

	/**
	 * {@link SimpleActionHandlerAttribute} that removes the content of the target element from the DOM.
	 */
	public static SimpleActionHandlerAttribute clearHandler( String target ) {
		return simpleActionHandler()
				.type( Type.CLEAR )
				.target( target );
	}

	/**
	 * {@link SimpleActionHandlerAttribute} that closes the bootstrap modal. The target refers to the CSS3 selector of the modal element, usually it's id.
	 */
	public static SimpleActionHandlerAttribute closeModalHandler( String target ) {
		return simpleActionHandler()
				.type( Type.CLOSE_MODAL )
				.target( target );
	}

	/**
	 * {@link SimpleActionHandlerAttribute} that executes {@code EntityModule#initializeFormElements} on the target element.
	 */
	public static SimpleActionHandlerAttribute initializeFormElements( String target ) {
		return simpleActionHandler()
				.type( Type.INITIALIZE_ELEMENTS )
				.target( target );
	}

	public interface Type
	{
		String RESPONSE_CONTENT = "exm:response-content";
		String MOVE = "exm:move";
		String CLEAR = "exm:clear";
		String REMOVE = "exm:remove";
		String CLOSE_MODAL = "exm:modal:close";
		String INITIALIZE_ELEMENTS = "exm:initialize-elements";
	}
}
