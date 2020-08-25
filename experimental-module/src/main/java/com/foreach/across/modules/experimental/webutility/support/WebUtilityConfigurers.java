package com.foreach.across.modules.experimental.webutility.support;

import com.foreach.across.modules.experimental.webutility.support.DependsOnAttribute;
import lombok.experimental.UtilityClass;

import java.util.function.Consumer;

/**
 * Utility class that contains helpers to apply configuration on properties, views etc.
 *
 * @see MultiPropertyConfigurer
 */
@UtilityClass
public class WebUtilityConfigurers {
    /**
     * Apply one or more configurations to one or more properties.
     */
    public static MultiPropertyConfigurer onProperties( String... propertyNames ) {
        return new MultiPropertyConfigurer( propertyNames );
    }

    /**
     * Configure some depends-on rules on a property. Can be used to show/hide property values.
     */
    public static DependsOnAttribute dependsOn(Consumer<DependsOnAttribute> consumer ) {
        return new DependsOnAttribute( consumer );
    }
}
