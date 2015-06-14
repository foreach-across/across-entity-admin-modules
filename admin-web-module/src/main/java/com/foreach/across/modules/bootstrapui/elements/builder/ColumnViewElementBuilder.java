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

import com.foreach.across.modules.bootstrapui.elements.ColumnViewElement;
import com.foreach.across.modules.bootstrapui.elements.Grid;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementSupportBuilder;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Map;

/**
 * @author Arne Vandamme
 */
public class ColumnViewElementBuilder extends NodeViewElementSupportBuilder<ColumnViewElement, ColumnViewElementBuilder>
{
	private Grid.Position position = new Grid.Position();

	public ColumnViewElementBuilder layout( Grid.Position position ) {
		Assert.notNull( position );
		this.position = position;
		return this;
	}

	public ColumnViewElementBuilder layout( Grid.DeviceGridLayout... layouts ) {
		position.addAll( Arrays.asList( layouts ) );
		return this;
	}

	@Override
	public ColumnViewElementBuilder htmlId( String htmlId ) {
		return super.htmlId( htmlId );
	}

	@Override
	public ColumnViewElementBuilder attribute( String name, Object value ) {
		return super.attribute( name, value );
	}

	@Override
	public ColumnViewElementBuilder attributes( Map<String, Object> attributes ) {
		return super.attributes( attributes );
	}

	@Override
	public ColumnViewElementBuilder removeAttribute( String name ) {
		return super.removeAttribute( name );
	}

	@Override
	public ColumnViewElementBuilder clearAttributes() {
		return super.clearAttributes();
	}

	@Override
	public ColumnViewElementBuilder add( ViewElement... viewElements ) {
		return super.add( viewElements );
	}

	@Override
	public ColumnViewElementBuilder add( ViewElementBuilder... viewElements ) {
		return super.add( viewElements );
	}

	@Override
	public ColumnViewElementBuilder addAll( Iterable<?> viewElements ) {
		return super.addAll( viewElements );
	}

	@Override
	public ColumnViewElementBuilder sort( String... elementNames ) {
		return super.sort( elementNames );
	}

	@Override
	public ColumnViewElementBuilder name( String name ) {
		return super.name( name );
	}

	@Override
	public ColumnViewElementBuilder customTemplate( String template ) {
		return super.customTemplate( template );
	}

	@Override
	public ColumnViewElementBuilder postProcessor( ViewElementPostProcessor<ColumnViewElement> postProcessor ) {
		return super.postProcessor( postProcessor );
	}

	@Override
	protected ColumnViewElement createElement( ViewElementBuilderContext builderContext ) {
		ColumnViewElement column = new ColumnViewElement();
		column.setLayouts( position );

		return apply( column, builderContext );
	}
}
