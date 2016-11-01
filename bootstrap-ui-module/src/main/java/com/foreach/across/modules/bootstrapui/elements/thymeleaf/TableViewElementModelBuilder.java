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
import com.foreach.across.modules.web.thymeleaf.ThymeleafModelBuilder;
import com.foreach.across.modules.web.ui.elements.thymeleaf.AbstractHtmlViewElementModelWriter;

import static com.foreach.across.modules.bootstrapui.elements.thymeleaf.BootstrapModelWriterUtils.addStyle;
import static com.foreach.across.modules.bootstrapui.elements.thymeleaf.BootstrapModelWriterUtils.addStylesForPrefix;

/**
 * @author Arne Vandamme
 * @since 1.0.0
 */
public class TableViewElementModelBuilder extends AbstractHtmlViewElementModelWriter<TableViewElement>
{
	public static class RowElementThymeleafBuilder extends AbstractHtmlViewElementModelWriter<TableViewElement.Row>
	{
		@Override
		protected void writeOpenElement( TableViewElement.Row row, ThymeleafModelBuilder model ) {
			super.writeOpenElement( row, model );
			addStyle( model, row.getStyle() );
		}
	}

	public static class CellElementThymeleafBuilder extends AbstractHtmlViewElementModelWriter<TableViewElement.Cell>
	{
		@Override
		protected void writeOpenElement( TableViewElement.Cell cell, ThymeleafModelBuilder model ) {
			super.writeOpenElement( cell, model );

			if ( cell.isHeading() ) {
				model.changeOpenElement( "th" );
			}

			addStyle( model, cell.getStyle() );
			model.addAttribute( "colspan", cell.getColumnSpan() );

			model.addText( cell.getText() );
		}
	}

	@Override
	protected void writeOpenElement( TableViewElement table, ThymeleafModelBuilder model ) {
		// write responsive wrapper
		if ( table.isResponsive() ) {
			model.addOpenElement( "div" );
			model.addAttribute( "class", "table-responsive" );
		}

		super.writeOpenElement( table, model );
		model.addAttributeValue( "class", "table" );
		addStylesForPrefix( model, table.getStyles(), "table" );

		model.addViewElement( table.getCaption() );
		model.addViewElement( table.getColumnGroup() );
		model.addViewElement( table.getHeader() );
		model.addViewElement( table.getBody() );
		model.addViewElement( table.getFooter() );
	}

	@Override
	protected void writeCloseElement( TableViewElement table, ThymeleafModelBuilder modelBuilder ) {
		super.writeCloseElement( table, modelBuilder );

		if ( table.isResponsive() ) {
			modelBuilder.addCloseElement();
		}
	}
}
