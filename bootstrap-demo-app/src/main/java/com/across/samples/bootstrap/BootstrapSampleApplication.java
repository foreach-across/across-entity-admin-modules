package com.across.samples.bootstrap;

import com.foreach.across.core.development.AcrossDevelopmentMode;
import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.modules.web.AcrossWebModule;
import com.foreach.across.modules.web.config.AcrossWebConfiguration;
import com.foreach.across.modules.web.config.ThymeleafViewSupportConfiguration;
import com.foreach.across.modules.web.config.UrlPrefixingConfiguration;
import com.foreach.across.modules.web.config.resources.ResourceConfigurationProperties;
import com.foreach.across.modules.web.menu.MenuStore;
import com.foreach.across.modules.web.menu.RequestMenuStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

//@AcrossApplication(
//		modules = {
//				AdminWebThemesModule.NAME,
//				AcrossHibernateJpaModule.NAME,
//				AdminWebModule.NAME,
//				BootstrapUiModule.NAME,
//				EntityModule.NAME,
//				PropertiesModule.NAME,
//		}
//)
@Configuration
@EnableWebSecurity
@EnableWebMvc
@SpringBootApplication(scanBasePackageClasses = {
        //AcrossWebModule.class,
        //SpringSecurityModule.class,
        AdminWebModule.class,
        EntityModule.class,
        BootstrapSampleApplication.class,
})
@ConfigurationPropertiesScan(basePackageClasses = {
        AcrossWebModule.class,
        //SpringSecurityModule.class,
        AdminWebModule.class,
        EntityModule.class,
})
@EntityScan(basePackageClasses = {
        BootstrapSampleApplication.class,
})
@Import({
        ResourceConfigurationProperties.class,
        UrlPrefixingConfiguration.class,
        ThymeleafViewSupportConfiguration.class,
        AcrossWebConfiguration.class,
})

public class BootstrapSampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(BootstrapSampleApplication.class);
    }

    @Bean
    @Primary
    public AcrossDevelopmentMode acrossDevelopmentMode() {
        return new AcrossDevelopmentMode();
    }

    @Bean
    MenuStore menuStore() {
        return new RequestMenuStore();
    }

}