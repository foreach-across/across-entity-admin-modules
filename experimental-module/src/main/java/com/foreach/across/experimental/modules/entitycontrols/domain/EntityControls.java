package com.foreach.across.experimental.modules.entitycontrols.domain;

import com.foreach.across.experimental.modules.entitycontrols.support.CustomizeBatchProperties;
import com.foreach.across.modules.entity.config.builders.EntityPropertyRegistryBuilder;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertySelector;
import com.foreach.across.modules.entity.views.DispatchingEntityViewFactory;
import com.foreach.across.modules.entity.views.EntityViewFactory;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.helpers.EntityViewElementBatch;
import com.foreach.across.modules.entity.views.processors.EntityPropertyRegistryViewProcessor;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * EntityControls are created by the {@link EntityControlFactory} and are used as a helper class to render controls
 * for a given entityConfiguration.
 * <p>
 * A lot of helper methods that allow the customization of the generated controls are available.
 * After customization, the controls can be build by calling the {@link #build(ViewElementBuilderContext)} method.
 *
 * @author Stijn Vanhoof
 */
@RequiredArgsConstructor
public class EntityControls<T> {
    private final EntityViewElementBatch<T> batch;
    private final EntityConfiguration entityConfiguration;
    private final HashMap<String, Object> builderHints = new HashMap<>();
    @Getter
    private final HashMap<String, ViewElementMode> propertyRenderModes = new HashMap<>();

    @Getter
    private T entity;
    @Getter
    private CustomizeBatchProperties customizeBatchProperties;
    @Getter
    private ViewElementMode globalViewElementMode = ViewElementMode.FORM_WRITE;

    /**
     * The instance of the entity for which the control will be build for.
     */
    public EntityControls<T> forInstance(T object) {
        entity = object;
        return this;
    }

    /**
     * The properties can be customized by providing a {@link EntityPropertyRegistryBuilder}
     */
    public EntityControls<T> properties(@NonNull Consumer<EntityPropertyRegistryBuilder> builder) {
        this.customizeBatchProperties = new CustomizeBatchProperties(builder);
        return this;
    }

    /**
     * The properties that need to be build
     *
     * @param propertyNames as a list of strings
     */
    public EntityControls<T> showProperties(String... propertyNames) {
        batch.setPropertySelector(EntityPropertySelector.of(propertyNames));
        return this;
    }

    /**
     * The properties that need to be build
     *
     * @param entityPropertySelector that defined the properties
     */
    public EntityControls<T> showProperties(EntityPropertySelector entityPropertySelector) {
        batch.setPropertySelector(entityPropertySelector);
        return this;
    }

    /**
     * The {@link ViewElementMode} that will be used to render the given properties by default. The {@link ViewElementMode}}
     * of each property can be individually overriden by using the {@link #renderModeForProperties(ViewElementMode, String...)}
     */
    public EntityControls<T> defaultRenderMode(ViewElementMode viewElementMode) {
        globalViewElementMode = viewElementMode;
        return this;
    }

    /**
     * Will define the render mode for all given properties
     * Note that this will overrride the {@link #defaultRenderMode(ViewElementMode)}
     */
    public EntityControls<T> renderModeForProperties(ViewElementMode viewElementMode, String... propertyNames) {
        Stream.of(propertyNames).forEach(propertyName -> propertyRenderModes.put(propertyName, viewElementMode));
        return this;
    }

    /**
     * Builderhints that can be supplied to the underlying {@link EntityViewElementBatch}
     */
    public EntityControls<T> builderHints(@NonNull Map<String, Object> builderHints) {
        this.builderHints.putAll(builderHints);
        return this;
    }

    /**
     * Load the configuration that was done for a specific view.
     */
    public EntityControls<T> loadViewProperties(String viewName) {
        EntityViewFactory viewToLoad = entityConfiguration.getViewFactory(viewName);
        EntityPropertyRegistry propertyRegistryFromView = Objects.requireNonNull(((DispatchingEntityViewFactory) viewToLoad)
                .getProcessorRegistry()
                .getProcessorRegistration(
                        EntityPropertyRegistryViewProcessor.class
                                .getName())
                .orElse(null))
                .getProcessor(EntityPropertyRegistryViewProcessor.class)
                .getPropertyRegistry();

        batch.setPropertyRegistry(propertyRegistryFromView);
        return this;
    }

    /**
     * Build the {@link EntityControls}. This method should not be used and you should always try to
     * call {@link #build(ViewElementBuilderContext)}
     */
    public Map<String, ViewElement> build() {
        return build(new DefaultViewElementBuilderContext());
    }

    /**
     * Build the entityControls controls and return a map with the control name as key and the view element as value
     */
    public Map<String, ViewElement> build(ViewElementBuilderContext builderContext) {
        if (getCustomizeBatchProperties() != null) {
            getCustomizeBatchProperties().apply(batch);
        }

        if (getEntity() != null) {
            batch.setEntity(getEntity());
        }

        if (batch.getPropertiesBinder().getEntity() == null) {
            throw new IllegalStateException("You must set an entity before building the entity controls. This can be done using the method forInstance.");
        }

        propertyRenderModes.forEach(builderHints::put);
        batch.setBuilderHints(builderHints);

        batch.setViewElementMode(getGlobalViewElementMode());

        return batch.build(builderContext);
    }
}
