package com.foreach.across.modules.experimental.modals.web.components;

import com.foreach.across.modules.bootstrapui.elements.icons.IconSet;
import com.foreach.across.modules.bootstrapui.styles.AcrossBootstrapStyles;
import com.foreach.across.modules.bootstrapui.styles.BootstrapStyleRule;
import com.foreach.across.modules.bootstrapui.styles.BootstrapStyles;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.AbstractNodeViewElement;
import com.foreach.across.modules.web.ui.elements.builder.AbstractNodeViewElementBuilder;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import static com.foreach.across.modules.bootstrapui.BootstrapUiModuleIcons.ICON_SET_FONT_AWESOME_SOLID;
import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.html;
import static com.foreach.across.modules.web.ui.elements.TextViewElement.text;

public class ModalViewElementBuilder extends AbstractNodeViewElementBuilder<AbstractNodeViewElement, ModalViewElementBuilder>
{
	private ElementOrBuilder header, body, footer = null;
	private String name;
	private String id;
	private ModalSize size = ModalSize.MEDIUM;
	private boolean centered = false;
	private boolean scrollableBody = false;
	private boolean animated = true;

	public ModalViewElementBuilder name( String name ) {
		this.name = name;
		if ( StringUtils.isBlank( id ) ) {
			id = name;
		}
		return this;
	}

	public ModalViewElementBuilder id( String id ) {
		this.id = id;
		return this;
	}

	public ModalViewElementBuilder centered() {
		return centered( true );
	}

	public ModalViewElementBuilder centered( boolean centered ) {
		this.centered = centered;
		return this;
	}

	public ModalViewElementBuilder scrollableBody() {
		return scrollableBody( true );
	}

	public ModalViewElementBuilder scrollableBody( boolean scrollableBody ) {
		this.scrollableBody = scrollableBody;
		return this;
	}

	public ModalViewElementBuilder animated() {
		return animated( true );
	}

	private ModalViewElementBuilder animated( boolean animated ) {
		this.animated = animated;
		return this;
	}

	public ModalViewElementBuilder header( ViewElementBuilder header ) {
		this.header = ElementOrBuilder.wrap( header );
		return this;
	}

	public ModalViewElementBuilder header( ViewElement header ) {
		this.header = ElementOrBuilder.wrap( header );
		return this;
	}

	public ModalViewElementBuilder body( ViewElementBuilder body ) {
		this.body = ElementOrBuilder.wrap( body );
		return this;
	}

	public ModalViewElementBuilder body( ViewElement body ) {
		this.body = ElementOrBuilder.wrap( body );
		return this;
	}

	public ModalViewElementBuilder footer( ViewElementBuilder footer ) {
		this.footer = ElementOrBuilder.wrap( footer );
		return this;
	}

	public ModalViewElementBuilder footer( ViewElement footer ) {
		this.footer = ElementOrBuilder.wrap( footer );
		return this;
	}

	public ModalViewElementBuilder title( String title ) {
		header = ElementOrBuilder.wrap(
				html.builders.container()
				             .add( html.builders.h3( css.modal.title, text( title ) ) )
				             .add( html.builders.button()
				                                .attribute( "type", "button" )
				                                .with( css.close )
				                                .data( "dismiss", "modal" )
				                                .attribute( "aria-label", "Close" )
				                                .add( IconSet.iconSet( ICON_SET_FONT_AWESOME_SOLID ).icon( "times" )
				                                             .set( AcrossBootstrapStyles.css.text.danger )
				                                             .setAttribute( "aria-hidden", true ) )
				             )
		);
		return this;
	}

	@Override
	protected AbstractNodeViewElement createElement( ViewElementBuilderContext builderContext ) {
		NodeViewElementBuilder modal = html.builders.div()
		                                            .with( css.modal )
		                                            .attribute( "tabindex", -1 )
		                                            .attribute( "role", "dialog" )
		                                            .name( name )
		                                            .htmlId( id )
		                                            .attribute( "aria-hidden", true );

		if ( animated ) {
			modal.with( css.fade );
		}

		NodeViewElementBuilder dialog = html.builders.div( css.modal.dialog, size.getSize() ).attribute( "role", "document" )
		                                             .name( nestedElementName( "dialog" ) );
		modal.add( dialog );

		if ( scrollableBody ) {
			dialog.with( css.modal.dialog.scrollable );
		}
		if ( centered ) {
			dialog.with( css.modal.dialog.centered );
		}

		NodeViewElementBuilder content = html.builders.div( css.modal.content );
		dialog.add( content );

		if ( header != null ) {
			String titleId = StringUtils.join( id, "Title" );
			content.add(
					html.builders.div( css.modal.header )
					             .name( nestedElementName( "header" ) )
					             .htmlId( titleId )
					             .add( header.get( builderContext ) )
			);
			modal.attribute( "aria-labelledby", titleId );
		}

		if ( body != null ) {
			content.add(
					html.builders.div( css.modal.body )
					             .name( nestedElementName( "body" ) )
					             .add( body.get( builderContext ) )
			);
		}

		if ( footer != null ) {
			content.add(
					html.builders.div( css.modal.footer )
					             .name( nestedElementName( "footer" ) )
					             .add( footer.get( builderContext ) )
			);
		}

		return apply( modal.build( builderContext ), builderContext );
	}

	private String nestedElementName( String nestedElement ) {
		return StringUtils.isNotBlank( name )
				? StringUtils.join( name, "-", nestedElement )
				: "";
	}

	@Getter
	@RequiredArgsConstructor
	public enum ModalSize
	{
		SMALL( BootstrapStyles.css.modal.small ),
		MEDIUM( BootstrapStyleRule.empty() ),
		LARGE( BootstrapStyles.css.modal.large ),
		EXTRA_LARGE( BootstrapStyles.css.modal.extraLarge );

		private final BootstrapStyleRule size;
	}
}
