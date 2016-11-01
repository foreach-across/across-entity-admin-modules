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

package com.foreach.across.modules.bootstrapui.elements.thymeleaf;

import com.foreach.across.modules.bootstrapui.elements.Size;
import com.foreach.across.modules.bootstrapui.elements.Style;
import com.foreach.across.modules.web.thymeleaf.ThymeleafModelBuilder;

import java.util.Collection;

/**
 * Utility methods for working with bootstrap specific classes like {@link Style}
 * when building model output for a {@link com.foreach.across.modules.web.thymeleaf.ThymeleafModelBuilder}.
 *
 * @author Arne Vandamme
 * @since 1.0.0
 */
public abstract class BootstrapModelWriterUtils
{
	private BootstrapModelWriterUtils() {
	}

	public static void addStyles( ThymeleafModelBuilder writer, Collection<Style> styles ) {
		styles.forEach( s -> addStyle( writer, s ) );
	}

	public static void addStylesForPrefix( ThymeleafModelBuilder writer, Collection<Style> styles, String prefix ) {
		styles.forEach( s -> addStyleForPrefix( writer, s, prefix ) );
	}

	public static void addStyle( ThymeleafModelBuilder writer, Style style ) {
		addStyleForPrefix( writer, style, "" );
	}

	public static void addStyleForPrefix( ThymeleafModelBuilder writer, Style style, String prefix ) {
		if ( style != null ) {
			writer.addAttributeValue( "class", style.forPrefix( prefix ) );
		}
	}

	public static void addSizeForPrefix( ThymeleafModelBuilder writer, Size size, String prefix ) {
		if ( size != null && !Size.DEFAULT.equals( size ) ) {
			writer.addAttributeValue( "class", size.forPrefix( prefix ) );
		}
	}
}
