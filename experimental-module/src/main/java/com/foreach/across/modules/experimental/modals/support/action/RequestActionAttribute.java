package com.foreach.across.modules.experimental.modals.support.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.http.HttpMethod;

@Getter
@Setter
@Accessors(chain = true, fluent = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestActionAttribute extends ActionAttribute<RequestActionAttribute>
{
	public static final String ACTION = "exm:request";

	@NonNull
	@JsonProperty
	private HttpMethod method = HttpMethod.GET;

	@JsonProperty
	private String url;

	@JsonProperty
	private String form;

	public RequestActionAttribute() {
		action( ACTION );
	}
}
