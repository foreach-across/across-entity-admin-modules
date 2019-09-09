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

import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.AbstractNodeViewElement;
import com.foreach.across.modules.web.ui.elements.AbstractTextNodeViewElement;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Represents a HTML table, supporting head, body, foot, caption and colgroup section.
 *
 * @author Arne Vandamme
 */
@Accessors(chain = true)
@Getter
@Setter
public class TableViewElement extends AbstractNodeViewElement
{
	public static final String ELEMENT_TYPE = BootstrapUiElements.TABLE;
	private final Set<Style> styles = new LinkedHashSet<>();
	private Caption caption;
	private ColumnGroup columnGroup;
	private Header header;
	private Footer footer;
	private Body body;

	/**
	 * Set the table as a responsive table.  This will wrap the table in a single div with
	 * table-responsive class.
	 */
	private boolean responsive;

	public TableViewElement() {
		super( "table" );
		setElementType( ELEMENT_TYPE );
	}

	public TableViewElement setStyles( Collection<Style> styles ) {
		this.styles.addAll( styles );
		return this;
	}

	public TableViewElement addStyle( Style style ) {
		styles.add( style );
		return this;
	}

	public TableViewElement removeStyle( Style style ) {
		styles.remove( style );
		return this;
	}

	public TableViewElement clearStyles() {
		styles.clear();
		return this;
	}

	@Override
	public TableViewElement addCssClass( String... cssClass ) {
		super.addCssClass( cssClass );
		return this;
	}

	@Override
	public TableViewElement removeCssClass( String... cssClass ) {
		super.removeCssClass( cssClass );
		return this;
	}

	@Override
	public TableViewElement setAttributes( Map<String, Object> attributes ) {
		super.setAttributes( attributes );
		return this;
	}

	@Override
	public TableViewElement setAttribute( String attributeName, Object attributeValue ) {
		super.setAttribute( attributeName, attributeValue );
		return this;
	}

	@Override
	public TableViewElement addAttributes( Map<String, Object> attributes ) {
		super.addAttributes( attributes );
		return this;
	}

	@Override
	public TableViewElement removeAttribute( String attributeName ) {
		super.removeAttribute( attributeName );
		return this;
	}

	@Override
	public TableViewElement setName( String name ) {
		super.setName( name );
		return this;
	}

	@Override
	public TableViewElement setCustomTemplate( String customTemplate ) {
		super.setCustomTemplate( customTemplate );
		return this;
	}

	@Override
	protected TableViewElement setElementType( String elementType ) {
		super.setElementType( elementType );
		return this;
	}

	@Override
	public TableViewElement addChild( ViewElement element ) {
		super.addChild( element );
		return this;
	}

	@Override
	public TableViewElement addChildren( Collection<? extends ViewElement> elements ) {
		super.addChildren( elements );
		return this;
	}

	@Override
	public TableViewElement addFirstChild( ViewElement element ) {
		super.addFirstChild( element );
		return this;
	}

	@Override
	public TableViewElement clearChildren() {
		super.clearChildren();
		return this;
	}

	@Override
	public TableViewElement apply( Consumer<ContainerViewElement> consumer ) {
		super.apply( consumer );
		return this;
	}

	@Override
	public <U extends ViewElement> TableViewElement applyUnsafe( Consumer<U> consumer ) {
		super.applyUnsafe( consumer );
		return this;
	}

	@Override
	protected TableViewElement setTagName( String tagName ) {
		super.setTagName( tagName );
		return this;
	}

	@Override
	public TableViewElement setHtmlId( String htmlId ) {
		super.setHtmlId( htmlId );
		return this;
	}

	@Override
	public TableViewElement set( WitherSetter... setters ) {
		super.set( setters );
		return this;
	}

	@Override
	public TableViewElement remove( WitherRemover... functions ) {
		super.remove( functions );
		return this;
	}

	public static class Caption extends AbstractTextNodeViewElement
	{
		public Caption() {
			super( "caption" );
		}

		@Override
		public Caption set( WitherSetter... setters ) {
			super.set( setters );
			return this;
		}

		@Override
		public Caption remove( WitherRemover... functions ) {
			super.remove( functions );
			return this;
		}
	}

	public static class ColumnGroup extends AbstractNodeViewElement
	{
		public ColumnGroup() {
			super( "colgroup" );
		}

		public static class Column extends AbstractNodeViewElement
		{
			public Column() {
				super( "col" );
			}

			public Column setSpan( Integer span ) {
				if ( span == null ) {
					removeAttribute( "span" );
				}
				else {
					setAttribute( "span", span );
				}
				return this;
			}
		}

		@Override
		public ColumnGroup set( WitherSetter... setters ) {
			super.set( setters );
			return this;
		}

		@Override
		public ColumnGroup remove( WitherRemover... functions ) {
			super.remove( functions );
			return this;
		}
	}

	public static class Header extends AbstractNodeViewElement
	{
		public Header() {
			super( "thead" );
		}

		@Override
		public Header set( WitherSetter... setters ) {
			super.set( setters );
			return this;
		}

		@Override
		public Header remove( WitherRemover... functions ) {
			super.remove( functions );
			return this;
		}
	}

	public static class Footer extends AbstractNodeViewElement
	{
		public Footer() {
			super( "tfoot" );
		}

		@Override
		public Footer set( WitherSetter... setters ) {
			super.set( setters );
			return this;
		}

		@Override
		public Footer remove( WitherRemover... functions ) {
			super.remove( functions );
			return this;
		}
	}

	public static class Body extends AbstractNodeViewElement
	{
		public Body() {
			super( "tbody" );
		}

		@Override
		public Body set( WitherSetter... setters ) {
			super.set( setters );
			return this;
		}

		@Override
		public Body remove( WitherRemover... functions ) {
			super.remove( functions );
			return this;
		}
	}

	public static class Row extends AbstractNodeViewElement
	{
		private Style style;

		public Row() {
			super( "tr" );
			setElementType( ELEMENT_TYPE + ".row" );
		}

		public Style getStyle() {
			return style;
		}

		public Row setStyle( Style style ) {
			this.style = style;
			return this;
		}

		@Override
		public Row set( WitherSetter... setters ) {
			super.set( setters );
			return this;
		}

		@Override
		public Row remove( WitherRemover... functions ) {
			super.remove( functions );
			return this;
		}
	}

	@Accessors(chain = true)
	@Getter
	@Setter
	public static class Cell extends AbstractTextNodeViewElement
	{
		private boolean heading;
		private Style style;
		private Integer columnSpan;

		public Cell() {
			super( "td" );
			setElementType( ELEMENT_TYPE + ".cell" );
		}

		@Override
		public Cell set( WitherSetter... setters ) {
			super.set( setters );
			return this;
		}

		@Override
		public Cell remove( WitherRemover... functions ) {
			super.remove( functions );
			return this;
		}
	}
}
