package com.foreach.across.modules.bootstrapui.elements.builder;

import com.foreach.across.modules.bootstrapui.elements.TextareaFormElement;
import com.foreach.across.modules.bootstrapui.elements.TextboxFormElement;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;

import java.util.Map;

/**
 * Reponsible for building both {@link com.foreach.across.modules.bootstrapui.elements.TextboxFormElement}
 * and {@link com.foreach.across.modules.bootstrapui.elements.TextareaFormElement}.
 */
public class TextboxFormElementBuilder extends FormControlElementBuilderSupport<TextboxFormElement, TextboxFormElementBuilder>
{
	private boolean multiLine = false;

	private TextboxFormElement.Type type;
	private String placeholder, text;
	private Integer rows;

	/**
	 * Will create a textarea element.
	 *
	 * @return current builder
	 */
	public TextboxFormElementBuilder multiLine() {
		multiLine = true;
		return this;
	}

	/**
	 * Will create a password type element.
	 *
	 * @return current builder
	 */
	public TextboxFormElementBuilder password() {
		return type( TextboxFormElement.Type.PASSWORD );
	}

	/**
	 * Will create a textarea element with the set number of rows.
	 *
	 * @return current builder
	 */
	public TextboxFormElementBuilder multiLine( int rows ) {
		return rows( rows );
	}

	public TextboxFormElementBuilder type( TextboxFormElement.Type type ) {
		this.type = type;
		return this;
	}

	public TextboxFormElementBuilder placeholder( String placeholder ) {
		this.placeholder = placeholder;
		return this;
	}

	public TextboxFormElementBuilder text( String text ) {
		this.text = text;
		return this;
	}

	/**
	 * Will switch to creating a textarea element with the set number of rows.
	 *
	 * @param rows Number of rows to display.
	 * @return current builder
	 */
	public TextboxFormElementBuilder rows( int rows ) {
		multiLine = true;
		this.rows = rows;
		return this;
	}

	@Override
	public TextboxFormElementBuilder controlName( String controlName ) {
		return super.controlName( controlName );
	}

	@Override
	public TextboxFormElementBuilder disabled() {
		return super.disabled();
	}

	@Override
	public TextboxFormElementBuilder disabled( boolean disabled ) {
		return super.disabled( disabled );
	}

	@Override
	public TextboxFormElementBuilder readonly() {
		return super.readonly();
	}

	@Override
	public TextboxFormElementBuilder readonly( boolean readonly ) {
		return super.readonly( readonly );
	}

	@Override
	public TextboxFormElementBuilder htmlId( String htmlId ) {
		return super.htmlId( htmlId );
	}

	@Override
	public TextboxFormElementBuilder attribute( String name, Object value ) {
		return super.attribute( name, value );
	}

	@Override
	public TextboxFormElementBuilder attributes( Map<String, Object> attributes ) {
		return super.attributes( attributes );
	}

	@Override
	public TextboxFormElementBuilder removeAttribute( String name ) {
		return super.removeAttribute( name );
	}

	@Override
	public TextboxFormElementBuilder clearAttributes() {
		return super.clearAttributes();
	}

	@Override
	public TextboxFormElementBuilder add( ViewElement... viewElements ) {
		return super.add( viewElements );
	}

	@Override
	public TextboxFormElementBuilder add( ViewElementBuilder... viewElements ) {
		return super.add( viewElements );
	}

	@Override
	public TextboxFormElementBuilder sort( String... elementNames ) {
		return super.sort( elementNames );
	}

	@Override
	public TextboxFormElementBuilder name( String name ) {
		return super.name( name );
	}

	@Override
	public TextboxFormElementBuilder customTemplate( String template ) {
		return super.customTemplate( template );
	}

	@Override
	public TextboxFormElement build( ViewElementBuilderContext builderContext ) {
		TextboxFormElement textbox;

		if ( multiLine ) {
			TextareaFormElement textarea = new TextareaFormElement();
			if ( rows != null ) {
				textarea.setRows( rows );
			}

			textbox = textarea;
		}
		else {
			textbox = new TextboxFormElement();
		}

		if ( text != null ) {
			textbox.setText( text );
		}
		if ( type != null ) {
			textbox.setType( type );
		}
		if ( placeholder != null ) {
			textbox.setPlaceholder( placeholder );
		}

		return apply( textbox, builderContext );
	}
}
