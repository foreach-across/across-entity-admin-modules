package com.foreach.across.experiments;

import com.foreach.across.AcrossApplicationRunner;
import com.foreach.across.config.AcrossApplication;
import com.foreach.across.experimental.ExperimentalModule;
import com.foreach.across.modules.entity.EntityModule;
import org.springframework.context.annotation.Bean;

@AcrossApplication(modules = {EntityModule.NAME})
public class ExperimentalModuleTestApplication {
    @Bean
    public ExperimentalModule experimentalModule() {
        return new ExperimentalModule();
    }

    public static void main(String[] args) {
        AcrossApplicationRunner.run(ExperimentalModuleTestApplication.class, args);
    }
}
