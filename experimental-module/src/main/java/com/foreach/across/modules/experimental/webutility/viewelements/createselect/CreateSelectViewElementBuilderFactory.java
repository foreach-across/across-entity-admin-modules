package com.foreach.across.modules.experimental.webutility.viewelements.createselect;

import com.foreach.across.core.annotations.RefreshableCollection;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderFactory;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderService;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.ViewElementTypeLookupStrategy;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Objects;

/**
 * Adds support for the {@link CreateSelectViewElementBuilder} that adds a plus button besides a control of property 'x'.
 * When clicking the plus button a modal opens where you can create a new instance of property 'x'.
 */
@Component
@RequiredArgsConstructor
public class CreateSelectViewElementBuilderFactory implements EntityViewElementBuilderFactory {
    public final static String CREATE_SELECT = CreateSelectViewElementBuilderFactory.class.getName() + ".createSelectControl";
    private Collection<ViewElementTypeLookupStrategy> elementTypeLookupStrategies;

    private final EntityViewElementBuilderService entityViewElementBuilderService;

    @Override
    public boolean supports(String viewElementType) {
        return viewElementType.equals(CREATE_SELECT);
    }

    /**
     * Create a {@link CreateSelectViewElementBuilder} and pass in the original {@link ViewElementBuilder}
     */
    @Override
    public ViewElementBuilder createBuilder(EntityPropertyDescriptor propertyDescriptor, ViewElementMode viewElementMode, String viewElementType) {
        String originalViewElementType = findOriginalViewElementType(propertyDescriptor, viewElementMode);

        ViewElementBuilder originalViewElementBuilder = entityViewElementBuilderService.createElementBuilder(propertyDescriptor, viewElementMode,
                originalViewElementType);

        return new CreateSelectViewElementBuilder(originalViewElementBuilder);
    }

    /**
     * Find the viewElement type of the current property
     */
    private String findOriginalViewElementType(EntityPropertyDescriptor descriptor, ViewElementMode mode) {
        return elementTypeLookupStrategies.stream()
                .map(strategy -> strategy.findElementType(descriptor, mode))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    @Autowired
    void setElementTypeLookupStrategies(@RefreshableCollection(incremental = true, includeModuleInternals = true) Collection<ViewElementTypeLookupStrategy> elementTypeLookupStrategies) {
        this.elementTypeLookupStrategies = elementTypeLookupStrategies;
    }
}
