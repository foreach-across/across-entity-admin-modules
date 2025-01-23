package com.foreach.across.modules.experimental.webutility.support.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.http.HttpMethod;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

/**
 * {@link ActionAttribute} that is used to perform a request when the specified event is triggered on the corresponding element.
 * A request can optionally define a {@link RequestActionHandlerAttribute#partial(String)} parameter and form that should be submitted.
 * <p>
 * Currently, a request supports handling successful calls ({@link #success(ActionHandlerAttribute[])}) and redirects ({@link #redirect(ActionHandlerAttribute[])}).
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestActionAttribute<SELF extends RequestActionAttribute<SELF>> extends ActionAttribute<SELF>
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
	@JsonProperty
	private String url;

	/**
	 * A collection of attributes that can be added to the request.
	 * Can be used for example to add additional headers to a request.
	 */
	@JsonProperty
	private Map<String, Object> requestConfig;

	/**
	 * A collection of {@link ActionHandlerAttribute}s that should be executed before the request gets executed
	 * The provided handlers are executed in order.
	 */
	@JsonProperty
	private LinkedList<ActionHandlerAttribute> before = new LinkedList<>();

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

	/**
	 * A collection of {@link ActionHandlerAttribute}s that should be executed when the response is not in the 200 - 399 (inclusive) http status range.
	 * The provided handlers are executed in order.
	 */
	@JsonProperty
	private LinkedList<ActionHandlerAttribute> failure = new LinkedList<>();

	/**
	 * A collection of {@link ActionHandlerAttribute}s that should be executed when an error occurs during handling of the request or one of the action handlers.
	 * The provided handlers are executed in order.
	 */
	@JsonProperty
	private LinkedList<ActionHandlerAttribute> error = new LinkedList<>();

	/**
	 * Determines whether or not the request parameters of the current view are reused when building the url.
	 */
	@JsonProperty
	private boolean copyOriginalRequestParameters;

	public SELF partial( String partial ) {
		this.partial = partial;
		return self();
	}

	public SELF form( String form ) {
		this.form = form;
		return self();
	}

	public SELF method( HttpMethod method ) {
		this.method = method;
		return self();
	}

	public SELF url( String url ) {
		this.url = url;
		return self();
	}

	public SELF requestConfig( Map<String, Object> requestConfig ) {
		this.requestConfig = requestConfig;
		return self();
	}

	public SELF success( Collection<ActionHandlerAttribute> success ) {
		this.success = new LinkedList<>( success );
		return self();
	}

	public SELF success( ActionHandlerAttribute... success ) {
		return success( Arrays.asList( success ) );
	}

	public SELF before( Collection<ActionHandlerAttribute> before ) {
		this.before = new LinkedList<>( before );
		return self();
	}
	public SELF before( ActionHandlerAttribute... before ) {
		return before( Arrays.asList( before ) );
	}

	public SELF redirect( Collection<ActionHandlerAttribute> redirect ) {
		this.redirect = new LinkedList<>( redirect );
		return self();
	}

	public SELF redirect( ActionHandlerAttribute... redirect ) {
		return redirect( Arrays.asList( redirect ) );
	}

	public SELF error( Collection<ActionHandlerAttribute> error ) {
		this.error = new LinkedList<>( error );
		return self();
	}

	public SELF error( ActionHandlerAttribute... error ) {
		return error( Arrays.asList( error ) );
	}

	public SELF failure( Collection<ActionHandlerAttribute> failure ) {
		this.failure = new LinkedList<>( failure );
		return self();
	}

	public SELF failure( ActionHandlerAttribute... failure ) {
		return failure( Arrays.asList( failure ) );
	}

	public SELF copyOriginalRequestParameters( boolean copy ) {
		this.copyOriginalRequestParameters = copy;
		return self();
	}

	protected RequestActionAttribute() {
		action( ACTION );
		event( "click" );
	}

	@SuppressWarnings("unchecked")
	public static <T extends RequestActionAttribute<T>> RequestActionAttribute<T> requestAction() {
		return (T) new RequestActionAttribute();
	}
}
