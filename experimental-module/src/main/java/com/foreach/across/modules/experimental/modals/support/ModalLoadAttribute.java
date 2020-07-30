package com.foreach.across.modules.experimental.modals.support;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.foreach.across.modules.experimental.modals.support.action.ActionAttribute;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import com.foreach.across.modules.web.ui.elements.HtmlViewElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true, fluent = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModalLoadAttribute implements ViewElement.WitherSetter<HtmlViewElement>, ViewElementPostProcessor<HtmlViewElement>
{
	/**
	 * Selector for the target modal element that should be used.
	 */
	@NonNull
	@JsonProperty
	private String target;

	@NonNull
	@JsonProperty
	private List<ActionAttribute> content = new ArrayList<>();

	public ModalLoadAttribute content( ActionAttribute... actions ) {
		this.content.addAll( Arrays.asList( actions ) );
		return this;
	}

	public static ModalLoadAttribute modalLoadAttribute() {
		return new ModalLoadAttribute();
	}
//
//	@NonNull
//	private Supplier<DataRequestAttribute> contentConfiguration;
//	/**
//	 * Url from which the content to be loaded into the modal should be fetched.
//	 * The data loaded for the content will be rendered using
//	 */
//	@Getter(value = AccessLevel.NONE)
//	@Setter(value = AccessLevel.NONE)
//	@JsonProperty
//	private DataRequestAttribute content = null;
//
//	/**
//	 * Selector for the form within the modal body that should be submitted.
//	 * <p>
//	 * If not present, defaults to the first form within the modal.
//	 */
//	@JsonProperty
//	private String formToSubmit;
//
//	private Supplier<DataRequestAttribute> submissionConfiguration;
//
//	/**
//	 * Request configuration to which the content should be submitted upon submission of the modal.
//	 * Upon submit the form, referenced by {@link #formToSubmit}, will be serialized and send.
//	 * <p>
//	 * If not present, defaults to {@link #content}, with a {@link HttpMethod#POST} method instead.
//	 */
//	@Getter(value = AccessLevel.NONE)
//	@Setter(value = AccessLevel.NONE)
//	@JsonProperty
//	private DataRequestAttribute submission = null;
//
//	@JsonProperty
//	private ContentLoadAttribute refreshOnClose;
//
//	@Getter
//	@Setter
//	@JsonIgnore
//	private String modalTitle;
//
//	@Getter
//	@Setter
//	@JsonIgnore
//	private String modalConfirmButton;
//
//	@Getter
//	@Setter
//	@JsonIgnore
//	private String modalCancelButton;

	@Override
	public void applyTo( HtmlViewElement target ) {
//		content = contentConfiguration.get();
//		if ( submissionConfiguration != null ) {
//			submission = submissionConfiguration.get();
//		}
//		else {
//			submission = new DataRequestAttribute()
//					.url( content.url() )
//					.method( HttpMethod.POST );
//		}
		// set the attribute to this object, which will be serialized as json
		target.setAttribute( "data-modal-load", this );
	}

	@Override
	public void postProcess( ViewElementBuilderContext builderContext, HtmlViewElement element ) {
		element.set( this );
	}
}
