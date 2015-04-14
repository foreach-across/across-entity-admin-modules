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
package com.foreach.across.modules.bootstrapui.elements;

import com.foreach.across.modules.web.ui.elements.AbstractNodeViewElement;
import com.foreach.across.modules.web.ui.elements.ConfigurableTextViewElement;
import com.foreach.across.modules.web.ui.elements.NodeViewElementSupport;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Represents a HTML table, supporting head, body, foot, caption and colgroup section.
 *
 * @author Arne Vandamme
 */
public class TableViewElement extends NodeViewElementSupport
{
	public static final String ELEMENT_TYPE = BootstrapUiElements.TABLE;

	public static class Caption extends AbstractNodeViewElement implements ConfigurableTextViewElement
	{
		private String text;

		public Caption() {
			super( "caption" );
		}

		@Override
		public String getText() {
			return text;
		}

		@Override
		public void setText( String text ) {
			this.text = text;
		}
	}

	public static class ColumnGroup extends AbstractNodeViewElement
	{
		public static class Column extends AbstractNodeViewElement
		{
			public Column() {
				super( "col" );
			}

			public void setSpan( Integer span ) {
				if ( span == null ) {
					removeAttribute( "span" );
				}
				else {
					setAttribute( "span", span );
				}
			}
		}

		public ColumnGroup() {
			super( "colgroup" );
		}
	}

	public static class Header extends AbstractNodeViewElement
	{
		public Header() {
			super( "thead" );
		}
	}

	public static class Footer extends AbstractNodeViewElement
	{
		public Footer() {
			super( "tfoot" );
		}
	}

	public static class Body extends AbstractNodeViewElement
	{
		public Body() {
			super( "tbody" );
		}
	}

	public static class Row extends NodeViewElementSupport
	{
		private Style style;

		public Row() {
			super( ELEMENT_TYPE + ".row" );
		}

		public Style getStyle() {
			return style;
		}

		public void setStyle( Style style ) {
			this.style = style;
		}
	}

	public static class Cell extends NodeViewElementSupport implements ConfigurableTextViewElement
	{
		private boolean heading;
		private String text;
		private Style style;
		private Integer columnSpan;

		public Cell() {
			super( ELEMENT_TYPE + ".cell" );
		}

		public Style getStyle() {
			return style;
		}

		public void setStyle( Style style ) {
			this.style = style;
		}

		public boolean isHeading() {
			return heading;
		}

		public void setHeading( boolean heading ) {
			this.heading = heading;
		}

		public Integer getColumnSpan() {
			return columnSpan;
		}

		public void setColumnSpan( Integer columnSpan ) {
			this.columnSpan = columnSpan;
		}

		@Override
		public String getText() {
			return text;
		}

		@Override
		public void setText( String text ) {
			this.text = text;
		}
	}

	private final Set<Style> styles = new LinkedHashSet<>();

	private Caption caption;
	private ColumnGroup columnGroup;
	private Header header;
	private Footer footer;
	private Body body;

	public TableViewElement() {
		super( ELEMENT_TYPE );
	}

	public Caption getCaption() {
		return caption;
	}

	public void setCaption( Caption caption ) {
		this.caption = caption;
	}

	public ColumnGroup getColumnGroup() {
		return columnGroup;
	}

	public void setColumnGroup( ColumnGroup columnGroup ) {
		this.columnGroup = columnGroup;
	}

	public Header getHeader() {
		return header;
	}

	public void setHeader( Header header ) {
		this.header = header;
	}

	public Footer getFooter() {
		return footer;
	}

	public void setFooter( Footer footer ) {
		this.footer = footer;
	}

	public Body getBody() {
		return body;
	}

	public void setBody( Body body ) {
		this.body = body;
	}

	public Set<Style> getStyles() {
		return styles;
	}

	public void setStyles( Collection<Style> styles ) {
		this.styles.addAll( styles );
	}

	public void addStyle( Style style ) {
		styles.add( style );
	}

	public void removeStyle( Style style ) {
		styles.remove( style );
	}

	public void clearStyles() {
		styles.clear();
	}
}
