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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

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
	public void setLayouts( Grid.DeviceGridLayout... layouts ) {
		setLayouts( Arrays.asList( layouts ) );
	}

	/**
	 * Set all device related layouts for this column.
	 *
	 * @param layouts to be used
	 */
	public void setLayouts( Collection<Grid.DeviceGridLayout> layouts ) {
		removePositionClass();
		position.clear();
		position.addAll( layouts );

		updatePositionClass();
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
	public void addLayout( Grid.DeviceGridLayout... layouts ) {
		removePositionClass();
		Collections.addAll( position, layouts );
		updatePositionClass();
	}

	/**
	 * Remove one or more layouts from this column.
	 *
	 * @param layouts to remove
	 */
	public void removeLayout( Grid.DeviceGridLayout... layouts ) {
		removePositionClass();
		position.removeAll( Arrays.asList( layouts ) );
		updatePositionClass();
	}

	public void clearLayouts() {
		removePositionClass();
		position.clear();
		updatePositionClass();
	}

	private void removePositionClass() {
		removeCssClass( position.toString() );
	}

	private void updatePositionClass() {
		addCssClass( position.toString() );
	}
}
