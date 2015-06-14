package com.foreach.across.modules.bootstrapui.elements.builder;

import com.foreach.across.modules.bootstrapui.elements.FormViewElement;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementSupportBuilder;

import java.util.Map;

public class FormViewElementBuilder extends NodeViewElementSupportBuilder<FormViewElement, FormViewElementBuilder>
{
	@Override
	public FormViewElementBuilder htmlId( String htmlId ) {
		return super.htmlId( htmlId );
	}

	@Override
	public FormViewElementBuilder attribute( String name, Object value ) {
		return super.attribute( name, value );
	}

	@Override
	public FormViewElementBuilder attributes( Map<String, Object> attributes ) {
		return super.attributes( attributes );
	}

	@Override
	public FormViewElementBuilder removeAttribute( String name ) {
		return super.removeAttribute( name );
	}

	@Override
	public FormViewElementBuilder clearAttributes() {
		return super.clearAttributes();
	}

	@Override
	public FormViewElementBuilder add( ViewElement... viewElements ) {
		return super.add( viewElements );
	}

	@Override
	public FormViewElementBuilder add( ViewElementBuilder... viewElements ) {
		return super.add( viewElements );
	}

	@Override
	public FormViewElementBuilder addAll( Iterable<?> viewElements ) {
		return super.addAll( viewElements );
	}

	@Override
	public FormViewElementBuilder sort( String... elementNames ) {
		return super.sort( elementNames );
	}

	@Override
	public FormViewElementBuilder name( String name ) {
		return super.name( name );
	}

	@Override
	public FormViewElementBuilder customTemplate( String template ) {
		return super.customTemplate( template );
	}

	@Override
	public FormViewElementBuilder postProcessor( ViewElementPostProcessor<FormViewElement> postProcessor ) {
		return super.postProcessor( postProcessor );
	}

	@Override
	protected FormViewElement createElement( ViewElementBuilderContext viewElementBuilderContext ) {
		return apply( new FormViewElement(), viewElementBuilderContext );
	}
}
