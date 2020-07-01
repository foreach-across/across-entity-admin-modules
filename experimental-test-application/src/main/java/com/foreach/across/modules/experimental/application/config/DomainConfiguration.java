package com.foreach.across.modules.experimental.application.config;

import com.foreach.across.modules.experimental.application.domain.AbstractDomain;
import com.foreach.across.modules.hibernate.jpa.repositories.config.EnableAcrossJpaRepositories;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAcrossJpaRepositories(basePackageClasses = AbstractDomain.class)
public class DomainConfiguration {
}
