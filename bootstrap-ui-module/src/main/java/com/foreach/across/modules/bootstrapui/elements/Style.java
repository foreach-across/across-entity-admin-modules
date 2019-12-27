/*
 * Copyright 2019 the original author or authors
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

import com.foreach.across.modules.bootstrapui.styles.BootstrapStyleRule;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;

/**
 * Contains the common Bootstrap styles and provides ability to define new styles as constants.
 *
 * @author Arne Vandamme
 */
public class Style implements Serializable
{
	private static final long serialVersionUID = 1186095031223055070L;

	public static class Button
	{
		@Deprecated
		public static final Style DEFAULT = Style.LIGHT;

		public static final Style LIGHT = Style.LIGHT;
		public static final Style DARK = Style.DARK;
		public static final Style PRIMARY = Style.PRIMARY;
		public static final Style SECONDARY = Style.SECONDARY;
		public static final Style SUCCESS = Style.SUCCESS;
		public static final Style INFO = Style.INFO;
		public static final Style WARNING = Style.WARNING;
		public static final Style DANGER = Style.DANGER;
		public static final Style LINK = new Style( "link", true );

		private static final Map<Style, BootstrapStyleRule> styleToStyleRuleMapping = new HashMap<>( 10 );
		private static final Map<BootstrapStyleRule, Style> styleRuleToStyleMapping = new HashMap<>( 20 );

		static {
			styleToStyleRuleMapping.put( LIGHT, css.button.light );
			styleToStyleRuleMapping.put( DARK, css.button.dark );
			styleToStyleRuleMapping.put( PRIMARY, css.button.primary );
			styleToStyleRuleMapping.put( SECONDARY, css.button.secondary );
			styleToStyleRuleMapping.put( SUCCESS, css.button.success );
			styleToStyleRuleMapping.put( INFO, css.button.info );
			styleToStyleRuleMapping.put( WARNING, css.button.warning );
			styleToStyleRuleMapping.put( DANGER, css.button.danger );
			styleToStyleRuleMapping.put( LINK, css.button.link );

			styleRuleToStyleMapping.put( css.button.light, LIGHT );
			styleRuleToStyleMapping.put( css.button.outline.light, LIGHT );
			styleRuleToStyleMapping.put( css.button.dark, DARK );
			styleRuleToStyleMapping.put( css.button.outline.dark, DARK );
			styleRuleToStyleMapping.put( css.button.primary, PRIMARY );
			styleRuleToStyleMapping.put( css.button.outline.primary, PRIMARY );
			styleRuleToStyleMapping.put( css.button.secondary, SECONDARY );
			styleRuleToStyleMapping.put( css.button.outline.secondary, SECONDARY );
			styleRuleToStyleMapping.put( css.button.success, SUCCESS );
			styleRuleToStyleMapping.put( css.button.outline.success, SUCCESS );
			styleRuleToStyleMapping.put( css.button.info, INFO );
			styleRuleToStyleMapping.put( css.button.outline.info, INFO );
			styleRuleToStyleMapping.put( css.button.warning, WARNING );
			styleRuleToStyleMapping.put( css.button.outline.warning, WARNING );
			styleRuleToStyleMapping.put( css.button.danger, DANGER );
			styleRuleToStyleMapping.put( css.button.outline.danger, DANGER );
			styleRuleToStyleMapping.put( css.button.link, LINK );
		}

		/**
		 * Compatibility between the old Style enum approach and the new BootstrapStyleRule,
		 * attempts to resolve a Style enum from the bootstrap style rule.
		 *
		 * @param styleRule style rule
		 * @return matching Style (can be {@code null})
		 */
		@Nullable
		public static Style fromBootstrapStyleRule( BootstrapStyleRule styleRule ) {
			return styleRuleToStyleMapping.get( styleRule );
		}

		/**
		 * Converts a Style enum value to the equivalent style rule.
		 *
		 * @param style enum value
		 * @return equivalent bootstrap style rule (can be {@code null})
		 */
		@Nullable
		public static BootstrapStyleRule toBootstrapStyleRule( Style style ) {
			return styleToStyleRuleMapping.get( style );
		}
	}

	public static class Table
	{
		public static final Style STRIPED = new Style( "striped", true );
		public static final Style BORDERED = new Style( "bordered", true );
		public static final Style HOVER = new Style( "hover", true );
		public static final Style CONDENSED = new Style( "condensed", true );
	}

	public static class TableCell
	{
		public static final Style ACTIVE = Style.ACTIVE;
		public static final Style SUCCESS = Style.SUCCESS;
		public static final Style INFO = Style.INFO;
		public static final Style WARNING = Style.WARNING;
		public static final Style DANGER = Style.DANGER;
	}

	public static final Style DEFAULT = new Style( "default", true );
	public static final Style ACTIVE = new Style( "active", true );

	public static final Style LIGHT = new Style( "light", true );
	public static final Style DARK = new Style( "dark", true );
	public static final Style PRIMARY = new Style( "primary", true );
	public static final Style SECONDARY = new Style( "secondary", true );
	public static final Style SUCCESS = new Style( "success", true );
	public static final Style INFO = new Style( "info", true );
	public static final Style WARNING = new Style( "warning", true );
	public static final Style DANGER = new Style( "danger", true );

	private final boolean isDefault;
	private final String name;

	public Style( String name ) {
		this( name, false );
	}

	private Style( @NonNull String name, boolean isDefault ) {
		this.name = name;
		this.isDefault = isDefault;
	}

	public String getName() {
		return name;
	}

	public String forPrefix( String prefix ) {
		return isDefault && !StringUtils.isBlank( prefix ) ? prefix + "-" + name : name;
	}

	/**
	 * @return True if should behave as a default style, meaning it will be prefixed depending on the controls
	 * it is used on.
	 */
	public boolean isDefaultStyle() {
		return isDefault;
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}

		Style style = (Style) o;

		if ( isDefault != style.isDefault ) {
			return false;
		}
		if ( name != null ? !name.equals( style.name ) : style.name != null ) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = ( isDefault ? 1 : 0 );
		result = 31 * result + ( name != null ? name.hashCode() : 0 );
		return result;
	}
}
