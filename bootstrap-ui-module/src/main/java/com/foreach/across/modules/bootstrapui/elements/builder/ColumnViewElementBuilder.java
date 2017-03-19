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
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.builder.AbstractNodeViewElementBuilder;
import org.springframework.util.Assert;

import java.util.Arrays;

/**
 * @author Arne Vandamme
 */
public class ColumnViewElementBuilder extends AbstractNodeViewElementBuilder<ColumnViewElement, ColumnViewElementBuilder>
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
	protected ColumnViewElement createElement( ViewElementBuilderContext builderContext ) {
		ColumnViewElement column =  apply( new ColumnViewElement(), builderContext );
		column.setLayouts( position );

		return column;
	}
}
