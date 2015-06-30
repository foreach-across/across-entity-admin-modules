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

import com.foreach.across.modules.bootstrapui.elements.CheckboxFormElement;
import com.foreach.across.modules.bootstrapui.elements.RadioFormElement;
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
 *
 * @author Arne Vandamme
 */
public class OptionsFormElementBuilder extends NodeViewElementSupportBuilder<NodeViewElementSupport, OptionsFormElementBuilder>
{
	public enum Type
	{
		SELECT,
		CHECKBOX,
		RADIO
	}

	public static class Option extends NodeViewElementSupportBuilder<NodeViewElementSupport, Option>
	{
		private Boolean disabled, selected;
		private String text, label;
		private Object value;

		public Option disabled() {
			return disabled( true );
		}

		public Option disabled( boolean disabled ) {
			this.disabled = disabled;
			return this;
		}

		public Option selected() {
			return selected( true );
		}

		public Option selected( boolean selected ) {
			this.selected = selected;
			return this;
		}

		public Option text( String text ) {
			this.text = text;
			return this;
		}

		public Option label( String label ) {
			this.label = label;
			return this;
		}

		public Option value( Object value ) {
			this.value = value;
			return this;
		}

		@Override
		protected NodeViewElementSupport createElement( ViewElementBuilderContext builderContext ) {
			OptionsFormElementBuilder options = builderContext.getAttribute( OptionsFormElementBuilder.class );

			if ( options == null ) {
				throw new IllegalStateException(
						"OptionsFormElementBuilder.Option builders can only be used in the context of an OptionsFormElementBuilder. " +
								"No attribute OptionsFormElementBuilder found on the builder context." );
			}

			NodeViewElementSupport option;

			if ( options.type == Type.CHECKBOX ) {
				option = createCheckboxOption( options );
			}
			else if ( options.type == Type.RADIO ) {
				option = createRadioOption( options );
			}
			else {
				option = createSelectOption( options );
			}

			return apply( option, builderContext );
		}

		private NodeViewElementSupport createCheckboxOption( OptionsFormElementBuilder builder ) {
			return applyCheckboxAttributes( builder, new CheckboxFormElement() );
		}

		private NodeViewElementSupport createRadioOption( OptionsFormElementBuilder builder ) {
			return applyCheckboxAttributes( builder, new RadioFormElement() );
		}

		private CheckboxFormElement applyCheckboxAttributes( OptionsFormElementBuilder builder,
		                                                     CheckboxFormElement checkbox ) {
			checkbox.setControlName( builder.controlName );

			if ( text != null ) {
				checkbox.setLabel( text );
			}
			if ( label != null ) {
				checkbox.setLabel( label );
			}
			if ( value != null ) {
				checkbox.setValue( value );
			}
			if ( selected != null ) {
				checkbox.setChecked( selected );
			}
			if ( disabled != null ) {
				checkbox.setDisabled( disabled );
			}

			return apply( checkbox );
		}

		private NodeViewElementSupport createSelectOption( OptionsFormElementBuilder builder ) {
			SelectFormElement.Option option = new SelectFormElement.Option();

			if ( text != null ) {
				option.setText( text );
			}
			if ( label != null ) {
				option.setLabel( label );
			}
			if ( value != null ) {
				option.setValue( value );
			}
			if ( selected != null ) {
				option.setSelected( selected );
			}
			if ( disabled != null ) {
				option.setDisabled( disabled );
			}

			return option;
		}
	}

	private Boolean disabled, readonly, required;
	private String controlName;

	private boolean multiple = false;
	private Type type = Type.SELECT;

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

	/**
	 * Create a new option builder.
	 *
	 * @return option builder
	 */
	public Option createOption() {
		return new Option();
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
		if ( disabled != null ) {
			select.setDisabled( disabled );
		}
		if ( readonly != null ) {
			select.setReadonly( readonly );
		}
		if ( required != null ) {
			select.setRequired( required );
		}

		return select;
	}

	private NodeViewElementSupport createBoxDiv() {
		return NodeViewElement.forTag( "div" );
	}
}
