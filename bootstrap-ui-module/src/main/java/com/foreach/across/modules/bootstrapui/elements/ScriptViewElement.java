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

package com.foreach.across.modules.bootstrapui.elements;

import com.foreach.across.modules.web.ui.elements.AbstractNodeViewElement;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.http.MediaType;

import java.util.Optional;

/**
 * Represents a {@code <script>} element. A script tag may not be safely nested, using a {@code ScriptViewElement} will ensure that
 * any nested {@code ScriptViewElement} will get written after the original instead of inside it. In the original location a reference
 * node with all the same non-script-specific attributes will be written instead. A special attribute {@code data-bum-ref-id} will be added
 * holding the HTML id of the actual {@code <script>} that has been written in a valid location. Client-side you can use
 * {@code BootstrapUiModule.refTarget(node)} on a selector to ensure you transparently fetch the actual script node.
 * <p/>
 * The replacement tag written inside the original script is by default an {@code i} element with explicit hidden styling. You can use a
 * different tag if necessary by setting it using {@link #setTagName(String)}.
 * <p/>
 * If you really want to nest script elements, use a regular {@link com.foreach.across.modules.web.ui.elements.NodeViewElement} instead.
 *
 * @author Arne Vandamme
 * @since 2.1.1
 */
public class ScriptViewElement extends AbstractNodeViewElement
{
	/**
	 * Value to set on {@link #setRefTagName(String)} to avoid a reference tag from being written.
	 */
	public static final String NO_REF_TAG = "";

	/**
	 * The tag which should be rendered if the script element is deferred,
	 * in case of a nested script tag.
	 */
	@Getter
	@Setter
	@NonNull
	private String refTagName = "i";

	public ScriptViewElement() {
		super( "script" );
	}

	/**
	 * Specifies the URL of an external script file.
	 *
	 * @param src url
	 */
	public void setSource( String src ) {
		setAttribute( "src", src );
	}

	/**
	 * @return the URL of an external script file
	 */
	public String getSource() {
		return getAttribute( "src", String.class );
	}

	/**
	 * Specifies the character encoding used in an external script file.
	 *
	 * @param charset encoding
	 */
	public void setCharset( String charset ) {
		setAttribute( "charset", charset );
	}

	/**
	 * @return the character encoding used in an external script file
	 */
	public String getCharset() {
		return getAttribute( "charset", String.class );
	}

	/**
	 * Set the script media type.
	 *
	 * @param mediaType to set
	 */
	public void setType( String mediaType ) {
		setAttribute( "type", mediaType );
	}

	/**
	 * Set the script media type.
	 *
	 * @param mediaType to set
	 */
	public void setType( @NonNull MediaType mediaType ) {
		setAttribute( "type", mediaType.toString() );
	}

	/**
	 * @return the script media type
	 */
	public MediaType getType() {
		return Optional.ofNullable( getAttribute( "type", String.class ) ).map( MediaType::valueOf ).orElse( null );
	}

	/**
	 * Specifies that the script is executed asynchronously (only for external scripts).
	 *
	 * @param async true if async
	 */
	public void setAsync( boolean async ) {
		if ( async ) {
			setAttribute( "async", "async" );
		}
		else {
			removeAttribute( "async" );
		}
	}

	/**
	 * @return true if the script is executed asynchronously (only for external scripts)
	 */
	public boolean isAsync() {
		return hasAttribute( "async" );
	}

	/**
	 * Specifies that the script is executed when the page has finished parsing (only for external scripts).
	 *
	 * @param defer true if script is deferred
	 */
	public void setDefer( boolean defer ) {
		if ( defer ) {
			setAttribute( "defer", "defer" );
		}
		else {
			removeAttribute( "defer" );
		}
	}

	/**
	 * @return true if the script is executed when the page has finished parsing (only for external scripts)
	 */
	public boolean isDefer() {
		return hasAttribute( "defer" );
	}
}
