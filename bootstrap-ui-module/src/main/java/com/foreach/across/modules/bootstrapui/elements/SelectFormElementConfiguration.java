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

import com.foreach.across.modules.web.support.LocalizedTextResolver;
import org.springframework.util.Assert;

import java.util.Locale;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Stream;

/**
 * Configuration class for <a href="https://silviomoreto.github.io/bootstrap-select/">bootstrap-select</a>.
 * Instances of this class can be set on a {@link SelectFormElement}, in which case it will be converted to a
 * bootstrap-select if the {@link com.foreach.across.modules.bootstrapui.resource.BootstrapUiFormElementsWebResources} are registered.
 * <p/>
 * See <a href="https://silviomoreto.github.io/bootstrap-select/options/">bootstrap-select Options</a> for the different properties that this class exposes.
 *
 * @author Arne Vandamme
 * @see SelectFormElement
 * @since 1.1.0
 */
public class SelectFormElementConfiguration extends TreeMap<String, Object>
{
	public enum LiveSearchStyle
	{
		CONTAINS( "contains" ),
		STARTS_WITH( "startsWith" );

		private final String optionValue;

		LiveSearchStyle( String optionValue ) {
			this.optionValue = optionValue;
		}

		public String getOptionValue() {
			return optionValue;
		}
	}

	public SelectFormElementConfiguration() {
		setDropupAuto( false );
	}

	/**
	 * When set to true, adds two buttons to the top of the dropdown menu (Select All & Deselect All).
	 */
	public SelectFormElementConfiguration setActionsBox( boolean actionsBox ) {
		put( "actionsBox", actionsBox );
		return this;
	}

	/**
	 * Checks to see which has more room, above or below. If the dropup has enough room to fully open normally,
	 * but there is more room above, the dropup still opens normally.
	 * Otherwise, it becomes a dropup. If dropupAuto is set to false, dropups must be called manually.
	 */
	public SelectFormElementConfiguration setDropupAuto( boolean dropupAuto ) {
		put( "dropupAuto", dropupAuto );
		return this;
	}

	/**
	 * Adds a header to the top of the menu; includes a close button by default.
	 */
	public SelectFormElementConfiguration setHeader( String header ) {
		put( "header", header );
		return this;
	}

	/**
	 * Removes disabled options and optgroups from the menu.
	 */
	public SelectFormElementConfiguration setHideDisabled( boolean hideDisabled ) {
		put( "hideDisabled", hideDisabled );
		return this;
	}

	/**
	 * Set the base to use a different icon font instead of Glyphicons. If changing iconBase, you might also want to change tickIcon,
	 * in case the new icon font uses a different naming scheme.
	 */
	public SelectFormElementConfiguration setIconBase( String iconBase ) {
		put( "iconBase", iconBase );
		return this;
	}

	/**
	 * When set to true, adds a search box to the top of the selectpicker dropdown.
	 */
	public SelectFormElementConfiguration setLiveSearch( boolean liveSearch ) {
		put( "liveSearch", liveSearch );
		return this;
	}

	/**
	 * Setting liveSearchNormalize to true allows for accent-insensitive searching.
	 */
	public SelectFormElementConfiguration setLiveSearchNormalize( boolean liveSearchNormalize ) {
		put( "liveSearchNormalize", liveSearchNormalize );
		return this;
	}

	/**
	 * When set to 'contains', searching will reveal options that contain the searched text. For example, searching for pl with return both Apple, Plum, and
	 * Plantain. When set to 'startsWith', searching for pl will return only Plum and Plantain.
	 */
	public SelectFormElementConfiguration setLiveSearchStyle( LiveSearchStyle liveSearchStyle ) {
		put( "liveSearchStyle", liveSearchStyle != null ? liveSearchStyle.getOptionValue() : null );
		return this;
	}

	/**
	 * When set to an integer and in a multi-select, the number of selected options cannot exceed the given value.
	 * If the value if 0 or negative, there is no maximum.
	 */
	public SelectFormElementConfiguration setMaxOptions( int maxOptions ) {
		put( "maxOptions", maxOptions <= 0 ? false : maxOptions );
		return this;
	}

	/**
	 * The text that is displayed when maxOptions is enabled and the maximum number of options for the given scenario have been selected.
	 */
	public SelectFormElementConfiguration setMaxOptionsText( String maxOptionsText ) {
		put( "maxOptionsText", maxOptionsText );
		return this;
	}

	/**
	 * When set to true, enables the device's native menu for select menus.
	 */
	public SelectFormElementConfiguration setMobile( boolean mobile ) {
		put( "mobile", mobile );
		return this;
	}

	/**
	 * Set the character displayed in the button that separates selected options.
	 */
	public SelectFormElementConfiguration setMultipleSeparator( String separator ) {
		put( "multipleSeparator", separator );
		return this;
	}

	/**
	 * Specifies how the selection is displayed with a multiple select.
	 * <p>
	 * <ul>
	 * <li><strong>values</strong> displays a list of the selected options (separated by multipleSeparator).</li>
	 * <li><strong>static</strong></li> simply displays the select element's title.</li>
	 * <li><strong>count</strong> displays the total number of selected options.</li>
	 * <li><strong>count > x</strong> behaves like <strong>values</strong> until the number of selected options is greater than x;
	 * after that, it behaves like <strong>count</strong>.</li>
	 * </ul>
	 */
	public SelectFormElementConfiguration setSelectedTextFormat( String selectedTextFormat ) {
		put( "selectedTextFormat", selectedTextFormat );
		return this;
	}

	/**
	 * When set to true, treats the tab character like the enter or space characters within the selectpicker dropdown.
	 */
	public SelectFormElementConfiguration setSelectOnTab( boolean selectOnTab ) {
		put( "selectOnTab", selectOnTab );
		return this;
	}

	/**
	 * When set to true, display custom HTML associated with selected option(s) in the button. When set to false, the option value will be displayed instead.
	 */
	public SelectFormElementConfiguration setShowContent( boolean showContent ) {
		put( "showContent", showContent );
		return this;
	}

	/**
	 * When set to true, display icon(s) associated with selected option(s) in the button.
	 */
	public SelectFormElementConfiguration setShowIcon( boolean showIcon ) {
		put( "showIcon", showIcon );
		return this;
	}

	/**
	 * When set to true, display subtext associated with a selected option in the button.
	 */
	public SelectFormElementConfiguration setShowSubText( boolean showSubText ) {
		put( "showSubText", showSubText );
		return this;
	}

	/**
	 * Show checkmark on selected option (for items without multiple attribute).
	 */
	public SelectFormElementConfiguration setShowTick( boolean showTick ) {
		put( "showTick", showTick );
		return this;
	}

	/**
	 * Set the items to show in the drop-down:
	 * <ul>
	 * <li>0: corresponds with 'auto'</li>
	 * <li>x: number of items to show</li>
	 * <li>-1: corresponds with {@code false}</li>
	 * </ul>
	 *
	 * @param size number
	 */
	public SelectFormElementConfiguration setSize( int size ) {
		put( "size", size == 0 ? "auto" : ( size < 0 ? false : size ) );
		return this;
	}

	/**
	 * When set to a string, add the value to the button's style.
	 */
	public SelectFormElementConfiguration setStyle( String style ) {
		put( "style", style );
		return this;
	}

	/**
	 * Set which icon to use to display as the "tick" next to selected options.
	 */
	public SelectFormElementConfiguration setTickIcon( String tickIcon ) {
		put( "tickIcon", tickIcon );
		return this;
	}

	/**
	 * Sets the format for the text displayed when selectedTextFormat is count or count > #. {0} is the selected amount. {1} is total available for selection.
	 */
	public SelectFormElementConfiguration setCountSelectedText( String countSelectedText ) {
		put( "countSelectedText", countSelectedText );
		return this;
	}

	/**
	 * The text on the button that deselects all options when actionsBox is enabled.
	 */
	public SelectFormElementConfiguration setDeselectAllText( String deselectAllText ) {
		put( "deselectAllText", deselectAllText );
		return this;
	}

	/**
	 * The text that is displayed when a multiple select has no selected options.
	 */
	public SelectFormElementConfiguration setNoneSelectedText( String noneSelectedText ) {
		put( "noneSelectedText", noneSelectedText );
		return this;
	}

	/**
	 * The text on the button that selects all options when actionsBox is enabled.
	 */
	public SelectFormElementConfiguration setSelectAllText( String selectAllText ) {
		put( "selectAllText", selectAllText );
		return this;
	}

	/**
	 * Create a localized copy of the current configuration.
	 * Will add default messages for the text properties if there are none set.
	 *
	 * @param locale                for which to create a localized instance
	 * @param localizedTextResolver to use for localizing the text variables
	 * @return clone
	 */
	public SelectFormElementConfiguration localize( Locale locale, LocalizedTextResolver localizedTextResolver ) {
		Assert.notNull( locale );

		SelectFormElementConfiguration clone = new SelectFormElementConfiguration();
		clone.putAll( this );

		putIfKeyAbsent( clone, "selectAllText", "#{BootstrapUiModule.SelectFormElementConfiguration.selectAllText=Select all}" );
		putIfKeyAbsent( clone, "noneSelectedText", "#{BootstrapUiModule.SelectFormElementConfiguration.noneSelectedText=Nothing selected}" );
		putIfKeyAbsent( clone, "maxOptionsText", "#{BootstrapUiModule.SelectFormElementConfiguration.maxOptionsText=Limit reached ({0} items max)}" );
		putIfKeyAbsent( clone, "countSelectedText", "#{BootstrapUiModule.SelectFormElementConfiguration.countSelectedText={0} items selected}" );
		putIfKeyAbsent( clone, "deselectAllText", "#{BootstrapUiModule.SelectFormElementConfiguration.countSelectedText=Deselect all}" );

		if ( localizedTextResolver != null ) {
			Stream.of( "countSelectedText", "deselectAllText", "maxOptionsText", "noneSelectedText", "selectAllText" )
			      .forEach( key -> clone.compute( key, ( k, v ) -> localizedTextResolver.resolveText( v != null ? Objects.toString( v ) : null, locale ) ) );
		}

		return clone;
	}

	private void putIfKeyAbsent( SelectFormElementConfiguration configuration, String key, String value ) {
		if ( !configuration.containsKey( key ) ) {
			configuration.put( key, value );
		}
	}

	/**
	 * Creates a default bootstrap-select configuration with live search enabled.
	 *
	 * @return configuration
	 */
	public static SelectFormElementConfiguration liveSearch() {
		return simple().setLiveSearch( true ).setLiveSearchStyle( LiveSearchStyle.CONTAINS );
	}

	/**
	 * Create a new simple bootstrap-select configuration.
	 *
	 * @return configuration
	 */
	public static SelectFormElementConfiguration simple() {
		return new SelectFormElementConfiguration().setSelectOnTab( true );
	}
}
