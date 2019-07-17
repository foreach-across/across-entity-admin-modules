/*
 * Copyright 2014 the original author or authors
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
package com.foreach.across.modules.properties.config;

import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.Module;
import com.foreach.across.core.context.info.AcrossModuleInfo;
import com.foreach.across.modules.properties.PropertiesModule;
import com.foreach.across.modules.properties.registries.EntityPropertiesRegistry;
import com.foreach.across.modules.properties.repositories.PropertyTrackingRepository;
import com.foreach.across.modules.properties.services.EntityPropertiesServiceBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;

import javax.sql.DataSource;

/**
 * Base configuration for EntityProperties tables, using the primary DataSource, configured ConversionService of
 * the PropertiesModule and allowing table rename if the module defines a SchemaConfiguration.
 *
 * @author Arne Vandamme
 */
public abstract class AbstractEntityPropertiesConfiguration implements EntityPropertiesDescriptor {
    private final boolean allowTableConfiguration;

    @Autowired
    @Qualifier(AcrossContext.DATASOURCE)
    private DataSource primaryDataSource;

    @Autowired
    @Module(PropertiesModule.NAME)
    private AcrossModuleInfo propertiesModule;

    @Autowired
    @Module(AcrossModule.CURRENT_MODULE)
    protected AcrossModule currentModule;

    protected AbstractEntityPropertiesConfiguration() {
        this(true);
    }

    protected AbstractEntityPropertiesConfiguration(boolean allowTableConfiguration) {
        this.allowTableConfiguration = allowTableConfiguration;
    }

    @Override
    public DataSource dataSource() {
        return primaryDataSource;
    }

    /**
     * @return The ConversionService attached to the properties module.
     */
    public ConversionService conversionService() {
        return (ConversionService) propertiesModule.getApplicationContext().getBean(
                ConversionServiceConfiguration.CONVERSION_SERVICE_BEAN);
    }

    @Override
    public String tableName() {
        return originalTableName();
    }

    @Override
    public PropertyTrackingRepository trackingRepository() {
        return propertiesModule.getApplicationContext().getBean(PropertyTrackingRepository.class);
    }

    protected abstract String originalTableName();

    /**
     * Override and annotate with @Bean(name=X) to create the repository bean.
     */
    @Override
    public abstract EntityPropertiesServiceBase service();

    /**
     * Override and annotate with @Bean(name=X) to create the registry bean.
     */
    @Override
    public abstract EntityPropertiesRegistry registry();
}
