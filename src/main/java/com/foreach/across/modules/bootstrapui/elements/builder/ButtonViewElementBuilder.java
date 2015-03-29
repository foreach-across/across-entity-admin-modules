package com.foreach.across.modules.bootstrapui.elements.builder;

import com.foreach.across.modules.bootstrapui.elements.ButtonViewElement;
import com.foreach.across.modules.bootstrapui.elements.Size;
import com.foreach.across.modules.bootstrapui.elements.Style;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementSupportBuilder;

import java.util.Map;

public class ButtonViewElementBuilder extends NodeViewElementSupportBuilder<ButtonViewElement, ButtonViewElementBuilder>
{
	private String text, url;
	private ButtonViewElement.Type type;
	private ButtonViewElement.State state;
	private Style style;
	private Size size;

	public ButtonViewElementBuilder text( String text ) {
		this.text = text;
		return this;
	}

	public ButtonViewElementBuilder url( String url ) {
		this.url = url;
		return this;
	}

	/**
	 * Changes to submit button.
	 *
	 * @return builder instance
	 */
	public ButtonViewElementBuilder submit() {
		return type( ButtonViewElement.Type.BUTTON_SUBMIT );
	}

	/**
	 * Changes type and style to link and sets the url.
	 *
	 * @return builder instance
	 */
	public ButtonViewElementBuilder link( String url ) {
		return link().url( url );
	}

	/**
	 * Changes type and style to link.
	 *
	 * @return builder instance
	 */
	public ButtonViewElementBuilder link() {
		return type( ButtonViewElement.Type.LINK ).style( Style.Button.LINK );
	}

	public ButtonViewElementBuilder type( ButtonViewElement.Type type ) {
		this.type = type;
		return this;
	}

	/**
	 * Changes state to disabled.
	 *
	 * @return builder instance
	 */
	public ButtonViewElementBuilder disable() {
		return state( ButtonViewElement.State.DISABLED );
	}

	public ButtonViewElementBuilder state( ButtonViewElement.State state ) {
		this.state = state;
		return this;
	}

	public ButtonViewElementBuilder style( Style style ) {
		this.style = style;
		return this;
	}

	public ButtonViewElementBuilder size( Size size ) {
		this.size = size;
		return this;
	}

	@Override
	public ButtonViewElementBuilder htmlId( String htmlId ) {
		return super.htmlId( htmlId );
	}

	@Override
	public ButtonViewElementBuilder attribute( String name, Object value ) {
		return super.attribute( name, value );
	}

	@Override
	public ButtonViewElementBuilder attributes( Map<String, Object> attributes ) {
		return super.attributes( attributes );
	}

	@Override
	public ButtonViewElementBuilder removeAttribute( String name ) {
		return super.removeAttribute( name );
	}

	@Override
	public ButtonViewElementBuilder clearAttributes() {
		return super.clearAttributes();
	}

	@Override
	public ButtonViewElementBuilder add( ViewElement... viewElements ) {
		return super.add( viewElements );
	}

	@Override
	public ButtonViewElementBuilder add( ViewElementBuilder... viewElements ) {
		return super.add( viewElements );
	}

	@Override
	public ButtonViewElementBuilder sort( String... elementNames ) {
		return super.sort( elementNames );
	}

	@Override
	public ButtonViewElementBuilder name( String name ) {
		return super.name( name );
	}

	@Override
	public ButtonViewElementBuilder customTemplate( String template ) {
		return super.customTemplate( template );
	}

	@Override
	public ButtonViewElement build( ViewElementBuilderContext builderContext ) {
		ButtonViewElement button = new ButtonViewElement();

		if ( text != null ) {
			button.setText( text );
		}
		if ( url != null ) {
			button.setUrl( url );
		}
		if ( type != null ) {
			button.setType( type );
		}
		if ( state != null ) {
			button.setState( state );
		}
		if ( style != null ) {
			button.setStyle( style );
		}
		if ( size != null ) {
			button.setSize( size );
		}

		return apply( button, builderContext );
	}
}
