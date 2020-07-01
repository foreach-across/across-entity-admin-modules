package com.foreach.across.modules.experimental.entitycontrols.support;

import com.foreach.across.modules.entity.config.builders.EntityPropertyRegistryBuilder;
import com.foreach.across.modules.entity.registry.properties.DefaultEntityPropertyRegistryProvider;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptorFactoryImpl;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.MergingEntityPropertyRegistry;
import com.foreach.across.modules.entity.views.helpers.EntityViewElementBatch;
import com.foreach.across.modules.experimental.entitycontrols.domain.EntityControls;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.function.Consumer;

/**
 * A helper class used by {@link EntityControls} to
 * customize the properties of the batch controls to render.
 *
 * @author Stijn Vanhoof
 */
@RequiredArgsConstructor
public class CustomizeBatchProperties {
    @Getter
    private final Consumer<EntityPropertyRegistryBuilder> builder;

    @SneakyThrows
    public void apply(EntityViewElementBatch batch) {
        Field field = ReflectionUtils.findField(EntityViewElementBatch.class, "propertyRegistry");
        field.setAccessible(true);

        EntityPropertyRegistry props = (EntityPropertyRegistry) field.get(batch);
        MergingEntityPropertyRegistry newProps = new MergingEntityPropertyRegistry(props,
                DefaultEntityPropertyRegistryProvider.INSTANCE,
                new EntityPropertyDescriptorFactoryImpl());

        EntityPropertyRegistryBuilder propsBuilder = new EntityPropertyRegistryBuilder();
        builder.accept(propsBuilder);
        propsBuilder.apply(newProps);

        batch.setPropertyRegistry(newProps);
    }
}
