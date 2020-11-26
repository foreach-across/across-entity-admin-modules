package com.foreach.across.modules.experimental.webutility.support;

import com.foreach.across.modules.entity.config.builders.EntityConfigurationBuilder;
import com.foreach.across.modules.entity.views.DispatchingEntityViewFactory;
import com.foreach.across.modules.entity.views.EntityViewFactory;
import com.foreach.across.modules.experimental.webutility.viewelements.editablevalues.EditableValueViewActionsViewProcessor;
import lombok.experimental.UtilityClass;

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Utility class that contains helpers to apply configuration on properties, views etc.
 *
 * @see MultiPropertyConfigurer
 */
@UtilityClass
public class WebUtilityConfigurers
{
	/**
	 * Apply one or more configurations to one or more properties.
	 */
	public static MultiPropertyConfigurer onProperties( String... propertyNames ) {
		return new MultiPropertyConfigurer( propertyNames );
	}

	/**
	 * Configure some depends-on rules on a property. Can be used to show/hide property values.
	 */
	public static DependsOnAttribute dependsOn( Consumer<DependsOnAttribute> consumer ) {
		return new DependsOnAttribute( consumer );
	}

	public static <T> Consumer<EntityConfigurationBuilder<T>> cancelEditableValueViewActionsCustomization( @NotNull String viewName ) {
		return cancelEditableValueViewActionsCustomization( null, viewName );
	}

	public static <T> Consumer<EntityConfigurationBuilder<T>> cancelEditableValueViewActionsCustomization( String associationName,
	                                                                                                       @NotNull String viewName ) {
		return builder -> builder.postProcessor(
				mec -> {
					Optional<EntityViewFactory> viewFactory;
					if ( associationName != null ) {
						viewFactory = mec.getAssociations()
						                 .stream()
						                 .filter( a -> associationName.equals( a.getName() ) )
						                 .findFirst()
						                 .map( a -> a.getViewFactory( viewName ) );
					}
					else {
						viewFactory = Optional.ofNullable( mec.getViewFactory( viewName ) );
					}

					viewFactory.ifPresent(
							vf -> {
								if ( vf instanceof DispatchingEntityViewFactory ) {
									( (DispatchingEntityViewFactory) vf ).getProcessorRegistry()
									                                     .remove( EditableValueViewActionsViewProcessor.class.getName() );
								}
							}
					);
				}
		);
	}
}
