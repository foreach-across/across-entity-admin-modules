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

import com.foreach.across.modules.bootstrapui.elements.SelectFormElement;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.NodeViewElementSupport;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementSupportBuilder;

import java.util.Map;

/**
 * Responsible for building option lists as SELECT, RADIO or MULTI CHECKBOX.
 * Takes a collection of {@link OptionFormElementBuilder}s and will apply the global type to them, no matter what
 * type they were originally defined with.
 *
 * @author Arne Vandamme
 * @see OptionFormElementBuilder
 */
public class OptionsFormElementBuilder extends NodeViewElementSupportBuilder<NodeViewElementSupport, OptionsFormElementBuilder>
{
	private boolean disabled, readonly, required;

	private String controlName;
	private boolean multiple = false;
	private Type type = Type.SELECT;

	public boolean isDisabled() {
		return disabled;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public boolean isRequired() {
		return required;
	}

	public String getControlName() {
		return controlName;
	}

	public boolean isMultiple() {
		return multiple;
	}

	public Type getType() {
		return type;
	}

	/**
	 * Will allow multiple options to be selected.
	 *
	 * @return current builder
	 */
	public OptionsFormElementBuilder multiple() {
		return multiple( true );
	}

	/**
	 * Will allow only a single option to be selected.
	 *
	 * @return current builder
	 */
	public OptionsFormElementBuilder single() {
		return multiple( false );
	}

	/**
	 * Set if the builder should allow multiple or single options.
	 *
	 * @param multiple true if multiple options can be selected
	 * @return current builder
	 */
	public OptionsFormElementBuilder multiple( boolean multiple ) {
		this.multiple = multiple;
		return this;
	}

	/**
	 * Will generate a multiple checkbox control.  Checkbox always allows
	 * multiple values.
	 *
	 * @return current builder
	 */
	public OptionsFormElementBuilder checkbox() {
		type = Type.CHECKBOX;
		return this;
	}

	/**
	 * Will generate a radio button list.  Radio button list never allows multiple values.
	 *
	 * @return current builder
	 */
	public OptionsFormElementBuilder radio() {
		type = Type.RADIO;
		return this;
	}

	/**
	 * Will generate a select box.
	 *
	 * @return current builder
	 */
	public OptionsFormElementBuilder select() {
		type = Type.SELECT;
		return this;
	}

	public OptionsFormElementBuilder controlName( String controlName ) {
		this.controlName = controlName;
		return this;
	}

	public OptionsFormElementBuilder disabled() {
		return disabled( true );
	}

	public OptionsFormElementBuilder disabled( boolean disabled ) {
		this.disabled = disabled;
		return this;
	}

	public OptionsFormElementBuilder required() {
		return required( true );
	}

	public OptionsFormElementBuilder required( boolean required ) {
		this.required = required;
		return this;
	}

	public OptionsFormElementBuilder readonly() {
		return readonly( true );
	}

	public OptionsFormElementBuilder readonly( boolean readonly ) {
		this.readonly = readonly;
		return this;
	}

	@Override
	public OptionsFormElementBuilder htmlId( String htmlId ) {
		return super.htmlId( htmlId );
	}

	@Override
	public OptionsFormElementBuilder attribute( String name, Object value ) {
		return super.attribute( name, value );
	}

	@Override
	public OptionsFormElementBuilder attributes( Map<String, Object> attributes ) {
		return super.attributes( attributes );
	}

	@Override
	public OptionsFormElementBuilder removeAttribute( String name ) {
		return super.removeAttribute( name );
	}

	@Override
	public OptionsFormElementBuilder clearAttributes() {
		return super.clearAttributes();
	}

	@Override
	public OptionsFormElementBuilder add( ViewElement... viewElements ) {
		return super.add( viewElements );
	}

	@Override
	public OptionsFormElementBuilder add( ViewElementBuilder... viewElements ) {
		return super.add( viewElements );
	}

	@Override
	public OptionsFormElementBuilder addAll( Iterable<?> viewElements ) {
		return super.addAll( viewElements );
	}

	@Override
	public OptionsFormElementBuilder sort( String... elementNames ) {
		return super.sort( elementNames );
	}

	@Override
	public OptionsFormElementBuilder name( String name ) {
		return super.name( name );
	}

	@Override
	public OptionsFormElementBuilder customTemplate( String template ) {
		return super.customTemplate( template );
	}

	@Override
	public OptionsFormElementBuilder postProcessor( ViewElementPostProcessor<NodeViewElementSupport> postProcessor ) {
		return super.postProcessor( postProcessor );
	}

	@Override
	protected NodeViewElementSupport createElement( ViewElementBuilderContext builderContext ) {
		if ( builderContext.hasAttribute( OptionsFormElementBuilder.class ) ) {
			throw new IllegalStateException( "Nesting multiple OptionsFormElementBuilder instances is not supported." );
		}

		builderContext.setAttribute( OptionsFormElementBuilder.class, this );

		try {
			NodeViewElementSupport control;

			if ( type == Type.CHECKBOX || type == Type.RADIO ) {
				control = createBoxDiv();
			}
			else {
				control = createSelect();
			}

			if ( controlName != null ) {
				control.setHtmlId( controlName );
			}

			return apply( control, builderContext );
		}
		finally {
			builderContext.removeAttribute( OptionsFormElementBuilder.class );
		}
	}

	private NodeViewElementSupport createSelect() {
		SelectFormElement select = new SelectFormElement();
		select.setMultiple( multiple );

		if ( controlName != null ) {
			select.setControlName( controlName );
		}
		select.setDisabled( disabled );
		select.setReadonly( readonly );
		select.setRequired( required );

		return select;
	}

	private NodeViewElementSupport createBoxDiv() {
		return NodeViewElement.forTag( "div" );
	}

	public enum Type
	{
		SELECT,
		CHECKBOX,
		RADIO
	}
}
