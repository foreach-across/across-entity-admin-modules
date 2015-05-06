package com.foreach.across.modules.bootstrapui.elements.builder;

import com.foreach.across.modules.bootstrapui.elements.ButtonViewElement;
import com.foreach.across.modules.bootstrapui.elements.Size;
import com.foreach.across.modules.bootstrapui.elements.Style;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementSupportBuilder;

import java.util.Map;

public class ButtonViewElementBuilder extends NodeViewElementSupportBuilder<ButtonViewElement, ButtonViewElementBuilder>
{
	private String text, title, url;
	private ButtonViewElement.Type type;
	private ButtonViewElement.State state;
	private Style style;
	private Size size;
	private ElementOrBuilder icon;
	private boolean iconOnly = false;
	private boolean iconRight = false;

	/**
	 * Set the icon as a {@link ViewElement} to use for the button
	 * and sets the button as only an icon.
	 *
	 * @param icon ViewElement
	 * @return builder instance
	 */
	public ButtonViewElementBuilder iconOnly( ViewElement icon ) {
		return icon( icon ).iconOnly();
	}

	/**
	 * Set the icon as a {@link ViewElementBuilder} to use for the button
	 * and sets the button as only an icon.
	 *
	 * @param icon ViewElementBuilder
	 * @return builder instance
	 */
	public ButtonViewElementBuilder iconOnly( ViewElementBuilder icon ) {
		return icon( icon ).iconOnly();
	}

	/**
	 * Set the icon as a {@link ViewElement} to use for the button.
	 *
	 * @param icon ViewElement
	 * @return builder instance
	 */
	public ButtonViewElementBuilder icon( ViewElement icon ) {
		this.icon = ElementOrBuilder.wrap( icon );
		return this;
	}

	/**
	 * Set the icon as a {@link ViewElementBuilder} to use for the button.
	 *
	 * @param icon ViewElementBuilder
	 * @return builder instance
	 */
	public ButtonViewElementBuilder icon( ViewElementBuilder icon ) {
		this.icon = ElementOrBuilder.wrap( icon );
		return this;
	}

	/**
	 * Converts the button to a button with only an icon.  Any text set will be used as title attribute or as
	 * aria-label if a title attribute it set explicitly.
	 *
	 * @return builder instance
	 */
	public ButtonViewElementBuilder iconOnly() {
		return iconOnly( true );
	}

	/**
	 * Set if the button is only an icon or not.
	 *
	 * @param iconOnly true if button should only display an icon
	 * @return builder instance
	 */
	public ButtonViewElementBuilder iconOnly( boolean iconOnly ) {
		this.iconOnly = iconOnly;
		return this;
	}

	/**
	 * Show the icon on the right side of the text (if an icon is used).
	 *
	 * @return builder instance
	 */
	public ButtonViewElementBuilder iconRight() {
		this.iconRight = true;
		return this;
	}

	/**
	 * Show the icon the left side of the text (if an icon is used).
	 *
	 * @return builder instance
	 */
	public ButtonViewElementBuilder iconLeft() {
		this.iconRight = false;
		return this;
	}

	/**
	 * Set the button text.
	 *
	 * @param text for the button
	 * @return builder instance
	 */
	public ButtonViewElementBuilder text( String text ) {
		this.text = text;
		return this;
	}

	/**
	 * Set the button title attribute.
	 *
	 * @param title attribute
	 * @return builder instance
	 */
	public ButtonViewElementBuilder title( String title ) {
		this.title = title;
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
	public ButtonViewElementBuilder postProcessor( ViewElementPostProcessor<ButtonViewElement> postProcessor ) {
		return super.postProcessor( postProcessor );
	}

	@Override
	protected ButtonViewElement createElement( ViewElementBuilderContext builderContext ) {
		ButtonViewElement button = new ButtonViewElement();

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

		if ( title != null ) {
			button.setTitle( title );
		}

		if ( iconOnly && icon != null ) {
			if ( text != null ) {
				if ( title == null ) {
					button.setTitle( text );
				}
				else {
					button.setAttribute( "aria-label", text );
				}
			}
		}
		else {
			if ( text != null ) {
				button.setText( text );
			}
		}

		if ( icon != null ) {
			if ( iconRight ) {
				button.add( icon.get( builderContext ) );
			}
			else {
				button.setIcon( icon.get( builderContext ) );
			}
		}

		return apply( button, builderContext );
	}
}
