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
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

/**
 * <p>Represents a Bootstrap column div, supporting {@link Grid.DeviceGridLayout}
 * configuration using {@link #setLayouts(Collection)} and {@link #addLayout(Grid.DeviceGridLayout...)}.</p>
 * <p>
 * Example:
 * <pre>new ColumnViewElement().addLayout( Grid.Device.MD.width(6) );</pre>
 * </p>
 *
 * @author Arne Vandamme
 */
@Deprecated
public class ColumnViewElement extends AbstractNodeViewElement
{
	private Grid.Position position = new Grid.Position();

	public ColumnViewElement() {
		super( "div" );
	}

	/**
	 * Set all device related layouts for this column.
	 * Replaces any previously registered, use {@link #addLayout(Grid.DeviceGridLayout...)} if you want to add
	 * a single layout.
	 *
	 * @param layouts to be used
	 */
	public ColumnViewElement setLayouts( Grid.DeviceGridLayout... layouts ) {
		return setLayouts( Arrays.asList( layouts ) );
	}

	/**
	 * Set all device related layouts for this column.
	 *
	 * @param layouts to be used
	 */
	public ColumnViewElement setLayouts( Collection<Grid.DeviceGridLayout> layouts ) {
		removePositionClass();
		position.clear();
		position.addAll( layouts );

		updatePositionClass();
		return this;
	}

	/**
	 * Get all device layout configurations attached to this column.
	 *
	 * @return copy of the layout collection
	 */
	public Collection<Grid.DeviceGridLayout> getLayouts() {
		return Collections.unmodifiableCollection( position );
	}

	/**
	 * Add a device layout configuration for this column.
	 *
	 * @param layouts to add
	 */
	public ColumnViewElement addLayout( Grid.DeviceGridLayout... layouts ) {
		removePositionClass();
		Collections.addAll( position, layouts );
		updatePositionClass();
		return this;
	}

	/**
	 * Remove one or more layouts from this column.
	 *
	 * @param layouts to remove
	 */
	public ColumnViewElement removeLayout( Grid.DeviceGridLayout... layouts ) {
		removePositionClass();
		position.removeAll( Arrays.asList( layouts ) );
		updatePositionClass();
		return this;
	}

	public ColumnViewElement clearLayouts() {
		removePositionClass();
		position.clear();
		updatePositionClass();
		return this;
	}

	private void removePositionClass() {
		removeCssClass( position.toString() );
	}

	private void updatePositionClass() {
		addCssClass( position.toString() );
	}

	@Override
	public ColumnViewElement addCssClass( String... cssClass ) {
		super.addCssClass( cssClass );
		return this;
	}

	@Override
	public ColumnViewElement removeCssClass( String... cssClass ) {
		super.removeCssClass( cssClass );
		return this;
	}

	@Override
	public ColumnViewElement setAttributes( Map<String, Object> attributes ) {
		super.setAttributes( attributes );
		return this;
	}

	@Override
	public ColumnViewElement setAttribute( String attributeName, Object attributeValue ) {
		super.setAttribute( attributeName, attributeValue );
		return this;
	}

	@Override
	public ColumnViewElement addAttributes( Map<String, Object> attributes ) {
		super.addAttributes( attributes );
		return this;
	}

	@Override
	public ColumnViewElement removeAttribute( String attributeName ) {
		super.removeAttribute( attributeName );
		return this;
	}

	@Override
	public ColumnViewElement setCustomTemplate( String customTemplate ) {
		super.setCustomTemplate( customTemplate );
		return this;
	}

	@Override
	protected ColumnViewElement setElementType( String elementType ) {
		super.setElementType( elementType );
		return this;
	}

	@Override
	public ColumnViewElement addChild( ViewElement element ) {
		super.addChild( element );
		return this;
	}

	@Override
	public ColumnViewElement addChildren( Collection<? extends ViewElement> elements ) {
		super.addChildren( elements );
		return this;
	}

	@Override
	public ColumnViewElement addFirstChild( ViewElement element ) {
		super.addFirstChild( element );
		return this;
	}

	@Override
	public ColumnViewElement clearChildren() {
		super.clearChildren();
		return this;
	}

	@Override
	public ColumnViewElement apply( Consumer<ContainerViewElement> consumer ) {
		super.apply( consumer );
		return this;
	}

	@Override
	public <U extends ViewElement> ColumnViewElement applyUnsafe( Consumer<U> consumer ) {
		super.applyUnsafe( consumer );
		return this;
	}

	@Override
	protected ColumnViewElement setTagName( String tagName ) {
		super.setTagName( tagName );
		return this;
	}

	@Override
	public ColumnViewElement setHtmlId( String htmlId ) {
		super.setHtmlId( htmlId );
		return this;
	}
}
