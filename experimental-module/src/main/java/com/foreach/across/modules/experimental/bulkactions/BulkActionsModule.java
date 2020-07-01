package com.foreach.across.modules.experimental.bulkactions;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.core.context.configurer.ApplicationContextConfigurer;
import com.foreach.across.core.context.configurer.ComponentScanConfigurer;

import java.util.Set;

@AcrossDepends(required = "EntityModule")
public class BulkActionsModule extends AcrossModule {
    public static final String NAME = "BulkActionsModule";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected void registerDefaultApplicationContextConfigurers(Set<ApplicationContextConfigurer> contextConfigurers) {
        contextConfigurers.add(ComponentScanConfigurer.forAcrossModule(BulkActionsModule.class));
    }
}
