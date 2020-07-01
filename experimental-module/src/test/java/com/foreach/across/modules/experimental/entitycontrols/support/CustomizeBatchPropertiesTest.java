package com.foreach.across.modules.experimental.entitycontrols.support;

import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.config.builders.EntityPropertyRegistryBuilder;
import com.foreach.across.modules.entity.registry.properties.DefaultEntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyRegistry;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderService;
import com.foreach.across.modules.entity.views.helpers.EntityViewElementBatch;
import com.foreach.across.test.AcrossTestConfiguration;
import com.foreach.across.test.AcrossWebAppConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.function.Consumer;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ExtendWith(SpringExtension.class)
@AcrossWebAppConfiguration(classes = CustomizeBatchPropertiesTest.Config.class)
class CustomizeBatchPropertiesTest {
    private CustomizeBatchProperties customizeBatchProperties;

    @Mock
    private Consumer<EntityPropertyRegistryBuilder> builder;

    private EntityViewElementBuilderService builderService;

    @BeforeEach
    void setup() {
        Consumer<EntityPropertyRegistryBuilder> builder = props -> props.property("email")
                .propertyType(String.class)
                .and()
                .property("fakeComment")
                .propertyType(String.class)
                .readable(true)
                .writable(true);

        customizeBatchProperties = new CustomizeBatchProperties(builder);
    }

    @Test
    void canCustomizeBatchProperties() throws IllegalAccessException {
        EntityViewElementBatch<Object> batchForEntity = new EntityViewElementBatch<>(builderService);
        MutableEntityPropertyRegistry propertyRegistry = DefaultEntityPropertyRegistry.forClass(Config.User.class);
        batchForEntity.setPropertyRegistry(propertyRegistry);
        customizeBatchProperties.apply(batchForEntity);

        Field field = ReflectionUtils.findField(EntityViewElementBatch.class, "propertyRegistry");
        field.setAccessible(true);

        EntityPropertyRegistry props = (EntityPropertyRegistry) field.get(batchForEntity);

        assertThat(props.getProperties()).hasSize(3);
    }

    @AcrossTestConfiguration(modules = EntityModule.NAME, expose = EntityViewElementBuilderService.class)
    protected static class Config {

        @NoArgsConstructor
        public class User {
            @Setter
            @Getter
            private String name;
        }
    }
}