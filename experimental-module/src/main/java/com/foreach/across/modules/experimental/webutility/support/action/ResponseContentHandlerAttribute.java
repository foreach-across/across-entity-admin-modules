package com.foreach.across.modules.experimental.webutility.support.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseContentHandlerAttribute<SELF extends ResponseContentHandlerAttribute<SELF>> extends SimpleActionHandlerAttribute<SELF>
{
	/**
	 * When true, the target will be cleared before inserting the source
	 */
	@JsonProperty
	private boolean replace = false;

	protected ResponseContentHandlerAttribute() {
		this.type( Type.RESPONSE_CONTENT );
	}

	/**
	 * {@link SimpleActionHandlerAttribute} that replaces the content of the target element with the content of the response.
	 * If a source is defined, the target content is replaced by the content of the source instead.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends ResponseContentHandlerAttribute<T>> ResponseContentHandlerAttribute<T> responseContentHandler() {
		return (T) new ResponseContentHandlerAttribute<>();
	}

	public SELF replace() {
		replace = true;
		return self();
	}

	public SELF insert() {
		replace = false;
		return self();
	}
}