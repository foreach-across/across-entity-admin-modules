package com.foreach.across.modules.experimental.modals.support.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.http.HttpMethod;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

/**
 * {@link ActionAttribute} that is used to perform a request when the specified event is triggered on the corresponding element.
 * A request can optionally define a {@link RequestActionHandlerAttribute#partial(String)} parameter and form that should be submitted.
 * <p>
 * Currently, a request supports handling successful calls ({@link #success(ActionHandlerAttribute[])}) and redirects ({@link #redirect(ActionHandlerAttribute[])})
 */
@Getter
@Setter
@Accessors(chain = true, fluent = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestActionAttribute extends ActionAttribute<RequestActionAttribute>
{
	public static final String ACTION = "exm:request";

	/**
	 * Name of the view element that should be rendered as partial.
	 */
	@JsonProperty
	private String partial;

	/**
	 * CSS3 selector to the form whose data should be sent to the {@link #url(String)}.
	 * If no url property is configured, the form action will also be used as url.
	 */
	@JsonProperty
	private String form;

	/**
	 * HTTP method that should be used for the AJAX request.
	 * Defaults to {@link HttpMethod#GET}.
	 * <strong>Note that any client-side form method will be ignored as this property takes precedence.</strong>
	 */
	@NonNull
	@JsonProperty
	private HttpMethod method = HttpMethod.GET;

	/**
	 * URL to which the partial request should go. If not set, the url of the current page will be reused.
	 */
	@Getter
	@JsonProperty
	private String url;

	/**
	 * A collection of attributes that can be added to the request.
	 * Can be used for example to add additional headers to a request.
	 */
	@Getter
	@JsonProperty
	private Map<String, Object> requestConfig;

	/**
	 * A collection of {@link ActionHandlerAttribute}s that should be executed when the response was succesful (statuscode 200-299).
	 * The provided handlers are executed in order.
	 */
	@JsonProperty
	private LinkedList<ActionHandlerAttribute> success = new LinkedList<>();

	/**
	 * A collection of {@link ActionHandlerAttribute}s that should be executed when the response is a redirect.
	 * The provided handlers are executed in order.
	 */
	@JsonProperty
	private LinkedList<ActionHandlerAttribute> redirect = new LinkedList<>();

	public RequestActionAttribute success( Collection<ActionHandlerAttribute> success ) {
		this.success = new LinkedList<>( success );
		return this;
	}

	public RequestActionAttribute success( ActionHandlerAttribute... success ) {
		this.success.addAll( Arrays.asList( success ) );
		return this;
	}

	public RequestActionAttribute redirect( Collection<ActionHandlerAttribute> redirect ) {
		this.redirect = new LinkedList<>( redirect );
		return this;
	}

	public RequestActionAttribute redirect( ActionHandlerAttribute... redirect ) {
		this.redirect.addAll( Arrays.asList( redirect ) );
		return this;
	}

	public RequestActionAttribute() {
		action( ACTION );
		event( "click" );
	}

	public static RequestActionAttribute requestAction() {
		return new RequestActionAttribute();
	}
}
