/*
 * Copyright 2019 the original author or authors
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

import com.foreach.across.modules.bootstrapui.elements.ButtonViewElement;
import com.foreach.across.modules.bootstrapui.elements.Size;
import com.foreach.across.modules.bootstrapui.elements.Style;
import com.foreach.across.modules.bootstrapui.styles.BootstrapStyles;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;

import static com.foreach.across.modules.web.ui.MutableViewElement.Functions.remove;

public class ButtonViewElementBuilder extends AbstractLinkSupportingNodeViewElementBuilder<ButtonViewElement, ButtonViewElementBuilder>
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

	@Override
	public ButtonViewElementBuilder css( String... cssClasses ) {
		return super.with( BootstrapStyles.css.of( cssClasses ) );
	}

	@Override
	public ButtonViewElementBuilder removeCss( String... cssClasses ) {
		return super.with( remove( BootstrapStyles.css.of( cssClasses ) ) );
	}

	public ButtonViewElementBuilder size( Size size ) {
		this.size = size;
		return this;
	}

	@Override
	protected ButtonViewElement createElement( ViewElementBuilderContext builderContext ) {
		ButtonViewElement button = new ButtonViewElement();

		if ( url != null ) {
			button.setUrl( buildLink( url, builderContext ) );
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
			button.setTitle( builderContext.resolveText( title ) );
		}

		String resolvedText = builderContext.resolveText( text );

		if ( iconOnly && icon != null ) {
			if ( text != null ) {
				if ( this.title == null ) {
					button.setTitle( resolvedText );
				}
				else {
					button.setAttribute( "aria-label", resolvedText );
				}
			}
		}
		else {
			if ( text != null ) {
				button.setText( resolvedText );
			}
		}

		if ( icon != null ) {
			if ( iconRight ) {
				button.addChild( icon.get( builderContext ) );
			}
			else {
				button.setIcon( icon.get( builderContext ) );
			}
		}

		return apply( button, builderContext );
	}
}
