package com.foreach.across.modules.experimental.entitycontrols.support;

import com.foreach.across.core.annotations.ConditionalOnAcrossModule;
import com.foreach.across.core.context.info.AcrossContextInfo;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.config.builders.EntityRegistryConfigurer;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.MutableEntityRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

/**
 * Resolve an {@link EntityConfiguration} for the given {@link Class<>} entityType.
 *
 * @author Stijn Vanhoof
 */
@Component
@ConditionalOnAcrossModule(EntityModule.NAME)
@RequiredArgsConstructor
public class EntityConfigurationResolver {
    private final EntityRegistry entityRegistry;
    private final AcrossContextInfo acrossContextInfo;

    public <V> EntityConfiguration<V> resolve(Class<V> entityType) {
        EntityConfiguration<V> entityConfiguration = entityRegistry.getEntityConfiguration(entityType);

        if (entityConfiguration == null) {
            AutowireCapableBeanFactory beanFactory = acrossContextInfo.getModuleInfo(EntityModule.NAME).getApplicationContext()
                    .getAutowireCapableBeanFactory();
            EntityRegistryConfigurer registryConfigurer = new EntityRegistryConfigurer(beanFactory);
            registryConfigurer.add(entities -> entities.create().entityType(entityType, true));
            registryConfigurer.applyTo((MutableEntityRegistry) entityRegistry);
            entityConfiguration = entityRegistry.getEntityConfiguration(entityType);
        }

        return entityConfiguration;
    }
}
