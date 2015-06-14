package com.foreach.across.modules.bootstrapui.elements.builder;

import com.foreach.across.modules.bootstrapui.elements.LabelFormElement;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementSupportBuilder;

import java.util.Map;

public class LabelFormElementBuilder extends NodeViewElementSupportBuilder<LabelFormElement, LabelFormElementBuilder>
{
	private String text;
	private Object target;

	@Override
	public LabelFormElementBuilder htmlId( String htmlId ) {
		return super.htmlId( htmlId );
	}

	@Override
	public LabelFormElementBuilder attribute( String name, Object value ) {
		return super.attribute( name, value );
	}

	@Override
	public LabelFormElementBuilder attributes( Map<String, Object> attributes ) {
		return super.attributes( attributes );
	}

	@Override
	public LabelFormElementBuilder removeAttribute( String name ) {
		return super.removeAttribute( name );
	}

	@Override
	public LabelFormElementBuilder clearAttributes() {
		return super.clearAttributes();
	}

	@Override
	public LabelFormElementBuilder add( ViewElement... viewElements ) {
		return super.add( viewElements );
	}

	@Override
	public LabelFormElementBuilder add( ViewElementBuilder... viewElements ) {
		return super.add( viewElements );
	}

	@Override
	public LabelFormElementBuilder addAll( Iterable<?> viewElements ) {
		return super.addAll( viewElements );
	}

	@Override
	public LabelFormElementBuilder sort( String... elementNames ) {
		return super.sort( elementNames );
	}

	@Override
	public LabelFormElementBuilder name( String name ) {
		return super.name( name );
	}

	@Override
	public LabelFormElementBuilder customTemplate( String template ) {
		return super.customTemplate( template );
	}

	@Override
	public LabelFormElementBuilder postProcessor( ViewElementPostProcessor<LabelFormElement> postProcessor ) {
		return super.postProcessor( postProcessor );
	}

	public LabelFormElementBuilder text( String text ) {
		this.text = text;
		return this;
	}

	public LabelFormElementBuilder target( String htmlId ) {
		this.target = htmlId;
		return this;
	}

	public LabelFormElementBuilder target( ViewElement element ) {
		this.target = element;
		return this;
	}

	public LabelFormElementBuilder target( ViewElementBuilder elementBuilder ) {
		this.target = elementBuilder;
		return this;
	}

	@Override
	protected LabelFormElement createElement( ViewElementBuilderContext viewElementBuilderContext ) {
		LabelFormElement label = new LabelFormElement();

		if ( text != null ) {
			label.setText( text );
		}

		if ( target != null ) {
			if ( target instanceof String ) {
				label.setTarget( (String) target );
			}
			else if ( target instanceof ViewElement ) {
				label.setTarget( (ViewElement) target );
			}
			else {
				label.setTarget( ( (ViewElementBuilder) target ).build( viewElementBuilderContext ) );
			}
		}
		return apply( label, viewElementBuilderContext );
	}
}
