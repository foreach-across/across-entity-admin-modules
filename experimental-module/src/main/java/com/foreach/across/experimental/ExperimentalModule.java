package com.foreach.across.experimental;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.context.configurer.ApplicationContextConfigurer;
import com.foreach.across.core.context.configurer.ComponentScanConfigurer;
import com.foreach.across.experimental.modules.entitycontrols.EntityControlsModule;
import org.springframework.context.annotation.Bean;

import java.util.Set;

public class ExperimentalModule extends AcrossModule {
    public static final String NAME = "ExperimentalModule";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return "ExperimentalModule contains a bunch of experimental features. ";
    }

    @Override
    protected void registerDefaultApplicationContextConfigurers(Set<ApplicationContextConfigurer> contextConfigurers) {
        contextConfigurers.add(ComponentScanConfigurer.forAcrossModule(EntityControlsModule.class));
    }
}
