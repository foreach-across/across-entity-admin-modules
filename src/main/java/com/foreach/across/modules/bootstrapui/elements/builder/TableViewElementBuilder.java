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

import com.foreach.across.modules.bootstrapui.elements.Style;
import com.foreach.across.modules.bootstrapui.elements.TableViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.builder.AbstractNodeViewElementBuilder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Arne Vandamme
 */
public class TableViewElementBuilder extends AbstractNodeViewElementBuilder<TableViewElement, TableViewElementBuilder>
{
	private Boolean responsive;
	private Set<Style> styles = new HashSet<>();
	private TableViewElementBuilder.Header header;
	private TableViewElementBuilder.Body body;
	private TableViewElementBuilder.Footer footer;
	private TableViewElementBuilder.Caption caption;

	public TableViewElementBuilder style( Style... styles ) {
		Collections.addAll( this.styles, styles );
		return this;
	}

	public TableViewElementBuilder clearStyles() {
		styles.clear();
		return this;
	}

	/**
	 * Will make the table responsive.
	 *
	 * @return current builder
	 */
	public TableViewElementBuilder responsive() {
		return responsive( true );
	}

	/**
	 * Determine if the table should be responsive or not.
	 *
	 * @param responsive true if the table should be responsive
	 * @return current builder
	 */
	public TableViewElementBuilder responsive( boolean responsive ) {
		this.responsive = responsive;
		return this;
	}

	/**
	 * Creates a header builder and adds it to the table.  If a header builder is already attached, the
	 * existing instance is returned.  Use {@link #createHeader()} if you want to create a new detached
	 * header builder.
	 *
	 * @return header builder
	 */
	public TableViewElementBuilder.Header header() {
		if ( header == null ) {
			header = createHeader();
		}

		return header;
	}

	/**
	 * Set the header builder to an existing instance.
	 *
	 * @param header builder to use
	 * @return current table builder
	 */
	public TableViewElementBuilder header( TableViewElementBuilder.Header header ) {
		this.header = header;
		return this;
	}

	/**
	 * Creates a body builder and adds it to the table.  If a body builder is already attached, the
	 * existing instance is returned.  Use {@link #createBody()} if you want to create a new detached
	 * body builder.
	 *
	 * @return body builder
	 */
	public TableViewElementBuilder.Body body() {
		if ( body == null ) {
			body = createBody();
		}

		return body;
	}

	/**
	 * Set the body builder to an existing instance.
	 *
	 * @param body builder to use
	 * @return current table builder
	 */
	public TableViewElementBuilder body( TableViewElementBuilder.Body body ) {
		this.body = body;
		return this;
	}

	/**
	 * Creates a footer builder and adds it to the table.  If a footer builder is already attached, the
	 * existing instance is returned.  Use {@link #createFooter()} if you want to create a new detached
	 * footer builder.
	 *
	 * @return footer builder
	 */
	public TableViewElementBuilder.Footer footer() {
		if ( footer == null ) {
			footer = createFooter();
		}

		return footer;
	}

	/**
	 * Set the footer builder to an existing instance.
	 *
	 * @param footer builder to use
	 * @return current table builder
	 */
	public TableViewElementBuilder footer( TableViewElementBuilder.Footer footer ) {
		this.footer = footer;
		return this;
	}

	/**
	 * Creates a caption builder and adds it to the table.  If a caption builder is already attached, the
	 * existing instance is returned.  Use {@link #createCaption()} if you want to create a new detached
	 * caption builder.
	 *
	 * @return caption builder
	 */
	public TableViewElementBuilder.Caption caption() {
		if ( caption == null ) {
			caption = createCaption();
		}

		return caption;
	}

	public TableViewElementBuilder caption( String captionText ) {
		return caption( createCaption().text( captionText ) );
	}

	/**
	 * Set the caption builder to an existing instance.
	 *
	 * @param caption builder to use
	 * @return current table builder
	 */
	public TableViewElementBuilder caption( TableViewElementBuilder.Caption caption ) {
		this.caption = caption;
		return this;
	}

	/**
	 * @return new row builder
	 */
	public TableViewElementBuilder.Row row() {
		return new TableViewElementBuilder.Row();
	}

	/**
	 * @return new cell builder
	 */
	public TableViewElementBuilder.Cell cell() {
		return new TableViewElementBuilder.Cell();
	}

	/**
	 * @return new cell builder with heading set to true
	 */
	public TableViewElementBuilder.Cell heading() {
		return new TableViewElementBuilder.Cell().heading( true );
	}

	@Override
	protected TableViewElement createElement( ViewElementBuilderContext builderContext ) {
		TableViewElement table = apply( new TableViewElement(), builderContext );
		table.setStyles( styles );

		if ( responsive != null ) {
			table.setResponsive( responsive );
		}
		if ( caption != null ) {
			table.setCaption( caption.build( builderContext ) );
		}
		if ( header != null ) {
			table.setHeader( header.build( builderContext ) );
		}
		if ( body != null ) {
			table.setBody( body.build( builderContext ) );
		}
		if ( footer != null ) {
			table.setFooter( footer.build( builderContext ) );
		}

		return table;
	}

	/**
	 * @return new header builder
	 */
	public static TableViewElementBuilder.Header createHeader() {
		return new TableViewElementBuilder.Header();
	}

	/**
	 * @return new body builder
	 */
	public static TableViewElementBuilder.Body createBody() {
		return new TableViewElementBuilder.Body();
	}

	/**
	 * @return new footer builder
	 */
	public static TableViewElementBuilder.Footer createFooter() {
		return new TableViewElementBuilder.Footer();
	}

	/**
	 * @return new caption builder
	 */
	public static TableViewElementBuilder.Caption createCaption() {
		return new TableViewElementBuilder.Caption();
	}

	public static class Caption extends AbstractNodeViewElementBuilder<TableViewElement.Caption, Caption>
	{
		private String text;

		public Caption text( String text ) {
			this.text = text;
			return this;
		}

		@Override
		protected TableViewElement.Caption createElement( ViewElementBuilderContext builderContext ) {
			TableViewElement.Caption caption = apply( new TableViewElement.Caption(), builderContext );

			if ( text != null ) {
				caption.setText( text );
			}

			return caption;
		}
	}

	public static class Body extends AbstractNodeViewElementBuilder<TableViewElement.Body, Body>
	{
		@Override
		protected TableViewElement.Body createElement( ViewElementBuilderContext builderContext ) {
			return apply( new TableViewElement.Body(), builderContext );
		}
	}

	public static class Header extends AbstractNodeViewElementBuilder<TableViewElement.Header, Header>
	{
		@Override
		protected TableViewElement.Header createElement( ViewElementBuilderContext builderContext ) {
			return apply( new TableViewElement.Header(), builderContext );
		}
	}

	public static class Footer extends AbstractNodeViewElementBuilder<TableViewElement.Footer, Footer>
	{
		@Override
		protected TableViewElement.Footer createElement( ViewElementBuilderContext builderContext ) {
			return apply( new TableViewElement.Footer(), builderContext );
		}
	}

	public static class Row extends AbstractNodeViewElementBuilder<TableViewElement.Row, Row>
	{
		@Override
		protected TableViewElement.Row createElement( ViewElementBuilderContext builderContext ) {
			return apply( new TableViewElement.Row(), builderContext );
		}
	}

	public static class Cell extends AbstractNodeViewElementBuilder<TableViewElement.Cell, Cell>
	{
		private boolean heading;
		private Integer columnSpan;
		private String text;

		public Cell heading( boolean heading ) {
			this.heading = heading;
			return this;
		}

		public Cell text( String text ) {
			this.text = text;
			return this;
		}

		public Cell columnSpan( Integer columnSpan ) {
			this.columnSpan = columnSpan;
			return this;
		}

		@Override
		protected TableViewElement.Cell createElement( ViewElementBuilderContext builderContext ) {
			TableViewElement.Cell cell = apply( new TableViewElement.Cell(), builderContext );
			cell.setHeading( heading );

			if ( columnSpan != null ) {
				cell.setColumnSpan( columnSpan );
			}
			if ( text != null ) {
				cell.setText( text );
			}

			return cell;
		}
	}
}
