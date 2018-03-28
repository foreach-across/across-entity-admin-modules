/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.foreach.across.modules.entity.views;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collection;
import java.util.Map;

/**
 * Represents both the view and the backing model for a generated entity output page.
 * The {@link Model} implementation is extended with some type-safe methods for passing data
 * between {@link EntityViewProcessor} instances if so used.
 * <p/>
 * Supports different types of resulting view:
 * <ul>
 * <li>{@link #getRedirectUrl()} for a redirect</li>
 * <li>{@link #getTemplate()} for a view name</li>
 * <li>{@link #getResponseEntity()} for a response entity</li>
 * <li>{@link #getCustomView()} for a custom {@link org.springframework.web.servlet.View}</li>
 * </ul>
 *
 * @author Arne Vandamme
 * @see EntityViewFactory
 * @see DefaultEntityViewFactory
 */
@Data
public class EntityView implements Model
{
	// default view names
	public static final String CREATE_VIEW_NAME = "createView";
	public static final String UPDATE_VIEW_NAME = "updateView";
	public static final String DELETE_VIEW_NAME = "deleteView";
	public static final String LIST_VIEW_NAME = "listView";
	public static final String GENERIC_VIEW_NAME = "genericView";
	public static final String SUMMARY_VIEW_NAME = "listSummaryView";

	private final ModelMap model;
	private final RedirectAttributes redirectAttributes;

	private String template;
	private String redirectUrl;
	private Object customView;

	@Getter(AccessLevel.NONE)
	private boolean shouldRender = true;

	public EntityView( @NonNull ModelMap model, @NonNull RedirectAttributes redirectAttributes ) {
		this.model = model;
		this.redirectAttributes = redirectAttributes;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate( String template ) {
		this.template = template;
	}

	/**
	 * Get an attribute from the model and coerce it to the expected type.
	 *
	 * @param attributeName name of the attribute
	 * @param expectedType  type the attribute value should have
	 * @param <V>           type the attribute value should have
	 * @param <Y>           return type specification for generics
	 * @return value or {@code null}
	 */
	@SuppressWarnings("unchecked")
	public <V, Y extends V> Y getAttribute( String attributeName, Class<V> expectedType ) {
		return (Y) expectedType.cast( getAttribute( attributeName ) );
	}

	public Object getAttribute( String attributeName ) {
		return model.get( attributeName );
	}

	/**
	 * Remove the attribute with the given name from the model and coerce
	 * the attribute value returned to the expected type.
	 * <p/>
	 * Note that the type coercing will only occur <u>after</u> the attribute has been removed!
	 *
	 * @param attributeName name of the attribute
	 * @param expectedType  type the attribute value should have
	 * @param <V>           type the attribute value should have
	 * @param <Y>           return type specification for generics
	 * @return value or {@code null}
	 */
	@SuppressWarnings("unchecked")
	public <V, Y extends V> Y removeAttribute( String attributeName, Class<V> expectedType ) {
		return (Y) expectedType.cast( removeAttribute( attributeName ) );
	}

	public Object removeAttribute( String attributeName ) {
		return model.remove( attributeName );
	}

	/**
	 * Set the redirect url, if the redirect url is not {@code null}, this will also disable rendering.
	 *
	 * @param redirectUrl to use
	 */
	public void setRedirectUrl( String redirectUrl ) {
		this.redirectUrl = redirectUrl;
		shouldRender = redirectUrl == null;
	}

	/**
	 * Set a {@link ResponseEntity} that should be used for the view..
	 * Short-hand for {@link #setCustomView(Object)}.
	 *
	 * @param responseEntity to return
	 */
	public void setResponseEntity( ResponseEntity<?> responseEntity ) {
		customView = responseEntity;
	}

	/**
	 * Get the response entity that will be used as view.
	 *
	 * @return {@code null} if not a response entity
	 */
	@SuppressWarnings("unchecked")
	public <V> ResponseEntity<V> getResponseEntity() {
		return (ResponseEntity<V>) ResponseEntity.class.cast( customView );
	}

	/**
	 * @return {@code true} if {@link #redirectUrl} is set
	 */
	public boolean isRedirect() {
		return redirectUrl != null;
	}

	/**
	 * @return {@code true} if the view returns a {@link ResponseEntity}
	 */
	public boolean isResponseEntity() {
		return customView instanceof ResponseEntity;
	}

	/**
	 * @return {@code true} if the view is neither a redirect, nor a view name;
	 * this method will also return {@code true} if the view is a response entity
	 */
	public boolean isCustomView() {
		return customView != null;
	}

	/**
	 * @return {@code true} if the view renders visual output
	 */
	public boolean shouldRender() {
		return shouldRender;
	}

	@Override
	public Model addAttribute( String attributeName, Object attributeValue ) {
		model.addAttribute( attributeName, attributeValue );
		return this;
	}

	@Override
	public Model addAttribute( Object attributeValue ) {
		model.addAttribute( attributeValue );
		return this;
	}

	@Override
	public Model addAllAttributes( Collection<?> attributeValues ) {
		model.addAllAttributes( attributeValues );
		return this;
	}

	@Override
	public Model addAllAttributes( Map<String, ?> attributes ) {
		model.addAllAttributes( attributes );
		return this;
	}

	@Override
	public Model mergeAttributes( Map<String, ?> attributes ) {
		model.mergeAttributes( attributes );
		return this;
	}

	@Override
	public boolean containsAttribute( String attributeName ) {
		return model.containsAttribute( attributeName );
	}

	@Override
	public Map<String, Object> asMap() {
		return model;
	}

	@Override
	public String toString() {
		return "EntityView{" +
				"template='" + template + '\'' +
				", redirectUrl='" + redirectUrl + '\'' +
				", customView=" + customView +
				", shouldRender=" + shouldRender +
				'}';
	}
}
