package com.foreach.across.modules.experimental.webutility.support;

import com.foreach.across.modules.entity.config.builders.EntityPropertyRegistryBuilder;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import com.foreach.across.modules.web.ui.elements.HtmlViewElement;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Very basic implementation for dependsOn dependency rules between entity properties.
 * Currently limited to checkboxes and applies itself to the FORM_WRITE form group.
 * Additional support for many scenarios can easily be added.
 */
@Setter
@Accessors(chain = true, fluent = true)
public class DependsOnAttribute implements ViewElement.WitherSetter<HtmlViewElement>, ViewElementPostProcessor<HtmlViewElement>, Consumer<EntityPropertyRegistryBuilder.PropertyDescriptorBuilder> {
    private final Map<String, Map<String, Object>> dependencies = new LinkedHashMap<>();
    private final Map<String, Object> options = new LinkedHashMap<>() {{
        put("hide", true);
    }};

    DependsOnAttribute(Consumer<DependsOnAttribute> consumer) {
        consumer.accept(this);
    }

    /**
     * Set the source field using the name of the viewElement
     */
    public Dependency viewElementName(@NonNull String viewElementName) {
        return new Dependency(ruleSet(viewElementNameToDependencyId(viewElementName)), options);
    }

    /**
     * Set the source field using a jQuery selector
     */
    public Dependency selector(@NonNull String selector) {
        return new Dependency(ruleSet(selector), options);
    }

    /**
     * Set the source field using the property selector
     */
    public Dependency property(@NonNull String propertyName) {
        return new Dependency(ruleSet(propertyToDependencyId(propertyName)), options);
    }

    private Map<String, Object> ruleSet(String id) {
        return dependencies.computeIfAbsent(id, key -> new LinkedHashMap<>());
    }

    private String propertyToDependencyId(String propertyName) {
        return "[data-em-property=\"" + propertyName + "\"]";
    }

    private String viewElementNameToDependencyId(String viewElementName) {
        return "[name=\"" + viewElementName + "\"]";
    }

    @Override
    public void applyTo(HtmlViewElement node) {
        Map<String, Map<String, Object>> attributeValue = new LinkedHashMap<>(dependencies);
        attributeValue.put("options", options);
        node.setAttribute("style", "display: none;");
        node.setAttribute("data-dependson", attributeValue);
    }

    @Override
    public void postProcess(ViewElementBuilderContext builderContext, HtmlViewElement node) {
        node.set(this);
    }

    @Override
    public void accept(EntityPropertyRegistryBuilder.PropertyDescriptorBuilder property) {
        property.viewElementPostProcessor(ViewElementMode.FORM_WRITE, this);
    }

    @SuppressWarnings("unused")
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public class Dependency {
        private final Map<String, Object> rules;
        private final Map<String, Object> settings;

        public Dependency values(Object... values) {
            rules.put("values", values);
            return this;
        }

        public Dependency isChecked() {
            return isChecked(true);
        }

        public Dependency isNotChecked() {
            return isChecked(false);
        }

        public Dependency isChecked(boolean checked) {
            rules.put("checked", checked);
            return this;
        }

        public Dependency toggleClass(@NonNull String classToToggle) {
            settings.put("toggleClass", classToToggle);
            return this;
        }

        /**
         * @param valueTarget is the jQuery selector for the target
         */
        public Dependency valueTarget(@NonNull String valueTarget) {
            settings.put("valueTarget", valueTarget);
            return this;
        }

        public DependsOnAttribute and() {
            return DependsOnAttribute.this;
        }
    }
}
