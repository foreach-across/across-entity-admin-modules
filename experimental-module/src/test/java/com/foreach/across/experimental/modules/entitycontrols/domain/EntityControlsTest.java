package com.foreach.across.experimental.modules.entitycontrols.domain;

import com.foreach.across.modules.entity.bind.EntityPropertiesBinder;
import com.foreach.across.modules.entity.config.builders.EntityPropertyRegistryBuilder;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertySelector;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.helpers.EntityViewElementBatch;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EntityControlsTest {
    @Mock
    private EntityConfiguration entityConfiguration;

    @Mock
    private EntityViewElementBatch batch;

    @Mock
    private EntityPropertiesBinder entityPropertiesBinder;

    @InjectMocks
    private EntityControls entityControls;

    @Test
    void setEntity() {
        Object object = mock(Object.class);
        entityControls.forInstance(object);

        assertThat(entityControls.getEntity()).isEqualTo(object);
    }

    @Test
    void customizeProperties() {
        Consumer<EntityPropertyRegistryBuilder> builder = props -> props.property("email");

        entityControls.properties(builder);
        assertThat(entityControls.getCustomizeBatchProperties().getBuilder()).isEqualTo(builder);
    }

    @Test
    void customizePropertiesToShowWithStrings() {
        entityControls.showProperties("a", "b");
        verify(batch).setPropertySelector(EntityPropertySelector.of("a", "b"));
    }

    @Test
    void customizePropertiesToShowWithPropertySelectors() {
        entityControls.showProperties(EntityPropertySelector.of("a", "b"));
        verify(batch).setPropertySelector(EntityPropertySelector.of("a", "b"));
    }

    @Test
    void changeDefaultRenderMode() {
        assertThat(entityControls.getGlobalViewElementMode()).isEqualTo(ViewElementMode.FORM_WRITE);

        entityControls.defaultRenderMode(ViewElementMode.FORM_READ);
        assertThat(entityControls.getGlobalViewElementMode()).isEqualTo(ViewElementMode.FORM_READ);
    }

    @Test
    void changeRenderModelForSingleProperty() {
        entityControls.renderModeForProperties(ViewElementMode.FORM_READ, "a", "b");
        assertThat(entityControls.getPropertyRenderModes().size()).isEqualTo(2);
    }

    @Nested
    @DisplayName("Build entity controls")
    class Build {
        @Test
        void throwErrorWhenNoPropertiesBinderIsConfigured() {
            when(batch.getPropertiesBinder()).thenReturn(entityPropertiesBinder);
            when(entityPropertiesBinder.getEntity()).thenReturn(null);

            assertThatExceptionOfType(IllegalStateException.class)
                    .isThrownBy(() -> entityControls.build())
                    .satisfies(e -> Assertions.assertThat(e.getMessage()).isEqualTo("You must set an entity before building the entity controls. This can be done using the method forInstance."));
        }
    }
}