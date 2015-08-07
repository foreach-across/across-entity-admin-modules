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
package com.foreach.across.modules.bootstrapui.elements.thymeleaf;

import com.foreach.across.modules.bootstrapui.elements.TableViewElement;
import com.foreach.across.modules.web.thymeleaf.ViewElementNodeFactory;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;

/**
 * @author Arne Vandamme
 */
public class TableViewElementThymeleafBuilder extends BootstrapThymeleafBuilderSupport<TableViewElement>
{
	public static class RowElementThymeleafBuilder extends BootstrapThymeleafBuilderSupport<TableViewElement.Row>
	{
		@Override
		protected Element createNode( TableViewElement.Row row,
		                              Arguments arguments,
		                              ViewElementNodeFactory viewElementNodeFactory ) {
			Element element = new Element( "tr" );
			style( element, row.getStyle() );

			return element;
		}
	}

	public static class CellElementThymeleafBuilder extends BootstrapThymeleafBuilderSupport<TableViewElement.Cell>
	{
		@Override
		protected Element createNode( TableViewElement.Cell cell,
		                              Arguments arguments,
		                              ViewElementNodeFactory viewElementNodeFactory ) {
			Element element = new Element( cell.isHeading() ? "th" : "td" );
			style( element, cell.getStyle() );
			attribute( element, "colspan", cell.getColumnSpan(), viewElementNodeFactory );
			text( element, cell.getText() );

			return element;
		}
	}

	@Override
	protected Element createNode( TableViewElement table,
	                              Arguments arguments,
	                              ViewElementNodeFactory viewElementNodeFactory ) {
		Element element = new Element( "table" );
		element.setAttribute( "class", "table" );
		style( element, "table", table.getStyles() );

		addChild( element, table.getCaption(), arguments, viewElementNodeFactory );
		addChild( element, table.getColumnGroup(), arguments, viewElementNodeFactory );
		addChild( element, table.getHeader(), arguments, viewElementNodeFactory );
		addChild( element, table.getBody(), arguments, viewElementNodeFactory );
		addChild( element, table.getFooter(), arguments, viewElementNodeFactory );

		return element;
	}

	@Override
	protected Element postProcess( Element element,
	                               TableViewElement table,
	                               Arguments arguments,
	                               ViewElementNodeFactory viewElementNodeFactory ) {
		if ( table.isResponsive() ) {
			Element responsiveWrapper = new Element( "div" );
			responsiveWrapper.setAttribute( "class", "table-responsive" );
			responsiveWrapper.addChild( element );

			return responsiveWrapper;
		}

		return element;
	}
}
