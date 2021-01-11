package com.foreach.across.modules.experimental.webutility.support;

import com.foreach.across.modules.web.thymeleaf.AcrossWebDialect;
import org.thymeleaf.postprocessor.IPostProcessor;

import java.util.HashSet;
import java.util.Set;

public class AcrossWebDialectWithoutPartials extends AcrossWebDialect {
    @Override
    public Set<IPostProcessor> getPostProcessors() {
        return new HashSet<>();
    }
}
