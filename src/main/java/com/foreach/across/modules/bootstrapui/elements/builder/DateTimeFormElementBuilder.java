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
package com.foreach.across.modules.bootstrapui.elements.builder;

import com.foreach.across.modules.bootstrapui.elements.DateTimeFormElement;
import com.foreach.across.modules.bootstrapui.elements.InputGroupFormElement;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;

import java.util.Map;

/**
 * @author Arne Vandamme
 */
public class DateTimeFormElementBuilder extends InputGroupFormElementBuilder
{
	@Override
	public DateTimeFormElementBuilder addonBefore( ViewElement element ) {
		return (DateTimeFormElementBuilder) super.addonBefore( element );
	}

	@Override
	public DateTimeFormElementBuilder addonBefore( ViewElementBuilder element ) {
		return (DateTimeFormElementBuilder) super.addonBefore( element );
	}

	@Override
	public DateTimeFormElementBuilder addonAfter( ViewElement element ) {
		return (DateTimeFormElementBuilder) super.addonAfter( element );
	}

	@Override
	public DateTimeFormElementBuilder addonAfter( ViewElementBuilder element ) {
		return (DateTimeFormElementBuilder) super.addonAfter( element );
	}

	@Override
	public DateTimeFormElementBuilder control( ViewElement element ) {
		return (DateTimeFormElementBuilder) super.control( element );
	}

	@Override
	public DateTimeFormElementBuilder control( ViewElementBuilder element ) {
		return (DateTimeFormElementBuilder) super.control( element );
	}

	@Override
	public DateTimeFormElementBuilder htmlId( String htmlId ) {
		return (DateTimeFormElementBuilder) super.htmlId( htmlId );
	}

	@Override
	public DateTimeFormElementBuilder attribute( String name, Object value ) {
		return (DateTimeFormElementBuilder) super.attribute( name, value );
	}

	@Override
	public DateTimeFormElementBuilder attributes( Map<String, Object> attributes ) {
		return (DateTimeFormElementBuilder) super.attributes( attributes );
	}

	@Override
	public DateTimeFormElementBuilder removeAttribute( String name ) {
		return (DateTimeFormElementBuilder) super.removeAttribute( name );
	}

	@Override
	public DateTimeFormElementBuilder clearAttributes() {
		return (DateTimeFormElementBuilder) super.clearAttributes();
	}

	@Override
	public DateTimeFormElementBuilder add( ViewElement... viewElements ) {
		return (DateTimeFormElementBuilder) super.add( viewElements );
	}

	@Override
	public DateTimeFormElementBuilder add( ViewElementBuilder... viewElements ) {
		return (DateTimeFormElementBuilder) super.add( viewElements );
	}

	@Override
	public DateTimeFormElementBuilder addAll( Iterable<?> viewElements ) {
		return (DateTimeFormElementBuilder) super.addAll( viewElements );
	}

	@Override
	public DateTimeFormElementBuilder sort( String... elementNames ) {
		return (DateTimeFormElementBuilder) super.sort( elementNames );
	}

	@Override
	public DateTimeFormElementBuilder name( String name ) {
		return (DateTimeFormElementBuilder) super.name( name );
	}

	@Override
	public DateTimeFormElementBuilder customTemplate( String template ) {
		return (DateTimeFormElementBuilder) super.customTemplate( template );
	}

	@Override
	public DateTimeFormElementBuilder postProcessor( ViewElementPostProcessor<InputGroupFormElement> postProcessor ) {
		return (DateTimeFormElementBuilder) super.postProcessor( postProcessor );
	}

	@Override
	protected DateTimeFormElement createElement( ViewElementBuilderContext builderContext ) {
		return (DateTimeFormElement) super.createElement( builderContext );
	}

	@Override
	protected DateTimeFormElement create() {
		return new DateTimeFormElement();
	}
}
