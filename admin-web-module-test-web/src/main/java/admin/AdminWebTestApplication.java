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

package admin;

import com.foreach.across.config.AcrossApplication;
import com.foreach.across.core.development.AcrossDevelopmentMode;
import com.foreach.across.modules.adminweb.AdminWebModule;
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
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * @author Arne Vandamme
 */
//@AcrossApplication(modules = AdminWebModule.NAME)
@Configuration
@EnableWebSecurity
@EnableWebMvc
@SpringBootApplication(scanBasePackageClasses = {
		AcrossWebModule.class,
		//SpringSecurityModule.class,
		AdminWebModule.class,
		AdminWebTestApplication.class,
})
@ConfigurationPropertiesScan(basePackageClasses = {
		AcrossWebModule.class,
		//SpringSecurityModule.class,
		AdminWebModule.class,
})
//@EntityScan(basePackageClasses = {
//		AdminWebTestApplication.class,
//})
//@Import({
//		ResourceConfigurationProperties.class,
//		UrlPrefixingConfiguration.class,
//		ThymeleafViewSupportConfiguration.class,
//		AcrossWebConfiguration.class,
//})
public class AdminWebTestApplication
{
	public static void main( String[] args ) {
		SpringApplication.run( AdminWebTestApplication.class, args );
	}

	@Bean
	//@Primary
	public AcrossDevelopmentMode acrossDevelopmentMode() {
		return new AcrossDevelopmentMode();
	}

	@Bean
	MenuStore menuStore() {
		return new RequestMenuStore();
	}

}
