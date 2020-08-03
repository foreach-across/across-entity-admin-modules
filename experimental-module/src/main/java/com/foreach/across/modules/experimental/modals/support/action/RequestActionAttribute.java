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
	 * URL to which the partial request should go. If not set, the url from the {@link #form(String)} will be used
	 * (or from the closest form if {@link #form(String)} was also not specified.
	 */
	@Getter
	@JsonProperty
	private String url;

	@JsonProperty
	private LinkedList<ActionHandlerAttribute> success = new LinkedList<>();

	@JsonProperty
	private LinkedList<ActionHandlerAttribute> redirect = new LinkedList<>();

	@JsonProperty
	private LinkedList<ActionHandlerAttribute> error = new LinkedList<>();

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

	public RequestActionAttribute error( Collection<ActionHandlerAttribute> error ) {
		this.error = new LinkedList<>( error );
		return this;
	}

	public RequestActionAttribute error( ActionHandlerAttribute... error ) {
		this.error.addAll( Arrays.asList( error ) );
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
