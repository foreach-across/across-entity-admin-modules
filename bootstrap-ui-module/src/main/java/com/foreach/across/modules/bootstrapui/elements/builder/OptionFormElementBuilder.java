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

import com.foreach.across.modules.bootstrapui.elements.*;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Map;

import static com.foreach.across.modules.bootstrapui.elements.builder.OptionsFormElementBuilder.Type.*;

/**
 * <p>Responsible for building a single {@link com.foreach.across.modules.bootstrapui.elements.CheckboxFormElement},
 * {@link com.foreach.across.modules.bootstrapui.elements.RadioFormElement} or
 * {@link com.foreach.across.modules.bootstrapui.elements.SelectFormElement.Option}.</p>
 * <p>If built within an {@link OptionsFormElementBuilder} the type of the option will be determined by
 * the options builder, as well as all properties not set on the individual option level.</p>
 *
 * @author Arne Vandamme
 */
public class OptionFormElementBuilder<T extends FormControlElementSupport>
		extends FormControlElementBuilderSupport<T, OptionFormElementBuilder<T>>
		implements Comparable<OptionFormElementBuilder>
{
	private OptionsFormElementBuilder.Type type = SELECT;

	private boolean selected, wrapped = true;
	private String text, label;
	private Object value;
	private Object rawValue;

	public boolean isSelected() {
		return selected;
	}

	public boolean isWrapped() {
		return wrapped;
	}

	public String getText() {
		return text;
	}

	public String getLabel() {
		return label;
	}

	public Object getValue() {
		return value;
	}

	/**
	 * @return the raw value (for example entity) that this option represents
	 */
	public Object getRawValue() {
		return rawValue;
	}

	public OptionsFormElementBuilder.Type getType() {
		return type;
	}

	public OptionFormElementBuilder<T> checkbox() {
		return type( CHECKBOX );
	}

	public OptionFormElementBuilder<T> radio() {
		return type( RADIO );
	}

	public OptionFormElementBuilder<T> toggle() {
		return type( TOGGLE );
	}

	public OptionFormElementBuilder<T> type( OptionsFormElementBuilder.Type type ) {
		this.type = type;
		return this;
	}

	public OptionFormElementBuilder<T> selected() {
		return selected( true );
	}

	public OptionFormElementBuilder<T> unwrapped() {
		return wrapped( false );
	}

	public OptionFormElementBuilder<T> wrapped( boolean wrapped ) {
		this.wrapped = wrapped;
		return this;
	}

	public OptionFormElementBuilder<T> selected( boolean selected ) {
		this.selected = selected;
		return this;
	}

	public OptionFormElementBuilder<T> text( String text ) {
		this.text = text;
		return this;
	}

	public OptionFormElementBuilder<T> label( String label ) {
		this.label = label;
		return this;
	}

	/**
	 * Set the raw value that this option represents.  This value will not be attached to the actual
	 * generated form element but can be used to bulk select options.
	 *
	 * @param value raw value (for example entity)
	 * @return current builder
	 */
	public OptionFormElementBuilder<T> rawValue( Object value ) {
		this.rawValue = value;
		return this;
	}

	public OptionFormElementBuilder<T> value( Object value ) {
		this.value = value;
		return this;
	}

	@Override
	public OptionFormElementBuilder<T> controlName( String controlName ) {
		return super.controlName( controlName );
	}

	@Override
	public OptionFormElementBuilder<T> disabled() {
		return super.disabled();
	}

	@Override
	public OptionFormElementBuilder<T> disabled( boolean disabled ) {
		return super.disabled( disabled );
	}

	@Override
	public OptionFormElementBuilder<T> required() {
		return super.required();
	}

	@Override
	public OptionFormElementBuilder<T> required( boolean required ) {
		return super.required( required );
	}

	@Override
	public OptionFormElementBuilder<T> readonly() {
		return super.readonly();
	}

	@Override
	public OptionFormElementBuilder<T> readonly( boolean readonly ) {
		return super.readonly( readonly );
	}

	@Override
	public OptionFormElementBuilder<T> htmlId( String htmlId ) {
		return super.htmlId( htmlId );
	}

	@Override
	public OptionFormElementBuilder<T> attribute( String name, Object value ) {
		return super.attribute( name, value );
	}

	@Override
	public OptionFormElementBuilder<T> attributes( Map<String, Object> attributes ) {
		return super.attributes( attributes );
	}

	@Override
	public OptionFormElementBuilder<T> removeAttribute( String name ) {
		return super.removeAttribute( name );
	}

	@Override
	public OptionFormElementBuilder<T> clearAttributes() {
		return super.clearAttributes();
	}

	@Override
	public OptionFormElementBuilder<T> add( ViewElement... viewElements ) {
		return super.add( viewElements );
	}

	@Override
	public OptionFormElementBuilder<T> add( ViewElementBuilder... viewElements ) {
		return super.add( viewElements );
	}

	@Override
	public OptionFormElementBuilder<T> addAll( Iterable<?> viewElements ) {
		return super.addAll( viewElements );
	}

	@Override
	public OptionFormElementBuilder<T> sort( String... elementNames ) {
		return super.sort( elementNames );
	}

	@Override
	public OptionFormElementBuilder<T> name( String name ) {
		return super.name( name );
	}

	@Override
	public OptionFormElementBuilder<T> customTemplate( String template ) {
		return super.customTemplate( template );
	}

	@Override
	public OptionFormElementBuilder<T> postProcessor( ViewElementPostProcessor<T> postProcessor ) {
		return super.postProcessor( postProcessor );
	}

	@Override
	public OptionFormElementBuilder<T> with( ViewElement.WitherSetter... setters ) {
		return super.with( setters );
	}

	@Override
	public int compareTo( OptionFormElementBuilder o ) {
		int comparison = ObjectUtils.compare( getLabel(), o.getLabel() );

		if ( comparison == 0 ) {
			comparison = ObjectUtils.compare( getText(), o.getText() );
		}

		return comparison;
	}

	@Override
	protected T createElement( ViewElementBuilderContext builderContext ) {
		OptionsFormElementBuilder options = builderContext.getAttribute( OptionsFormElementBuilder.class );
		OptionsFormElementBuilder.Type typeToUse = options != null ? options.getType() : type;

		T control = apply( (T) createControl( typeToUse, builderContext ), builderContext );

		if ( options != null ) {
			if ( getControlName() == null ) {
				control.setControlName( options.getControlName() );
			}
			if ( getRequired() == null ) {
				control.setRequired( options.isRequired() );
			}
			if ( getDisabled() == null ) {
				control.setDisabled( options.isDisabled() );
			}
			if ( getRequired() == null ) {
				control.setReadonly( options.isReadonly() );
			}
		}

		return control;
	}

	private FormControlElementSupport createControl( OptionsFormElementBuilder.Type type, ViewElementBuilderContext builderContext ) {
		switch ( type ) {
			case CHECKBOX:
				CheckboxFormElement checkbox = new CheckboxFormElement();
				checkbox.setText( builderContext.resolveText( label != null ? label : text ) );
				checkbox.setValue( value );
				checkbox.setChecked( selected );
				checkbox.setWrapped( wrapped );
				return checkbox;
			case RADIO:
				RadioFormElement radio = new RadioFormElement();
				radio.setText( builderContext.resolveText( label != null ? label : text ) );
				radio.setValue( value );
				radio.setChecked( selected );
				radio.setWrapped( wrapped );
				return radio;
			case TOGGLE:
				ToggleFormElement toggle = new ToggleFormElement();
				toggle.setText( builderContext.resolveText( label != null ? label : text ) );
				toggle.setValue( value );
				toggle.setChecked( selected );
				toggle.setWrapped( wrapped );
				return toggle;
			default:
				SelectFormElement.Option option = new SelectFormElement.Option();
				option.setLabel( builderContext.resolveText( label ) );
				option.setText( builderContext.resolveText( text ) );
				option.setValue( value );
				option.setSelected( selected );
				return option;
		}
	}
}
