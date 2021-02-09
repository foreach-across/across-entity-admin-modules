package com.foreach.across.modules.experimental.webutility.support.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link ActionHandlerAttribute} that supports refreshing fetching and replacing an element.
 * This will trigger a client-side request based on the configured properties.
 * When the request is resolved, the content of the response will replace the {@link #target} element.
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuppressWarnings("unused")
public class RequestActionHandlerAttribute<SELF extends RequestActionHandlerAttribute<SELF>> extends ActionHandlerAttribute<SELF> {
    protected RequestActionHandlerAttribute() {
        type("exm:request");
    }

    /**
     * Predefined value used the #additionalQueryParameters to get the id from the redirect request
     * to the update view.
     */
    public static final String UPDATE_ID_VALUE = "$updateId";

    /**
     * Name of the view element that should be rendered as partial.
     * Only the output of this view element will be then be set on the {@link #target(String)} element.
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
    @JsonProperty
    private HttpMethod method = HttpMethod.GET;

    /**
     * URL to which the partial request should go. If not set, the url from the {@link #form(String)} will be used
     * (or from the closest form if {@link #form(String)} was also not specified.
     */
    @JsonProperty
    private String url;

    /**
     * CSS3 selector of the element that should be replaced by the partial output.
     * The target element in its entirety will be replaced by the partial output.
     */
    @JsonProperty
    private String target;

    /**
     * A collection of attributes that can be added to the request.
     * Can be used for example to add additional headers to a request.
     */
    @JsonProperty
    private Map<String, Object> requestConfig;

    /**
     * Holds a map of additional queryParameters
     */
    @JsonProperty
    private Map<String, Object> additionalQueryParameters = new HashMap<>();

    /**
     * Determines whether or not the request parameters of the current view are reused when building the url.
     */
    @JsonProperty
    private boolean copyOriginalRequestParameters;

    @SuppressWarnings("unchecked")
    public static <T extends RequestActionHandlerAttribute<T>> RequestActionHandlerAttribute<T> requestActionHandler() {
        return (T) new RequestActionHandlerAttribute();
    }

    public SELF partial(String partial) {
        this.partial = partial;
        return self();
    }

    public SELF form(String form) {
        this.form = form;
        return self();
    }

    public SELF method(HttpMethod method) {
        this.method = method;
        return self();
    }

    public SELF url(String url) {
        this.url = url;
        return self();
    }

    public SELF target(String target) {
        this.target = target;
        return self();
    }

    public SELF additionalQueryParameter(String key, Object value) {
        this.additionalQueryParameters.put(key, value);
        return self();
    }

    public SELF copyOriginalRequestParameters( boolean copy ) {
        this.copyOriginalRequestParameters = copy;
        return self();
    }
}
