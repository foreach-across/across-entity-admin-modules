package com.across.samples.bootstrap;

import com.foreach.across.config.AcrossApplication;
import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.bootstrapui.BootstrapUiModule;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.filemanager.FileManagerModule;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.properties.PropertiesModule;
import com.foreach.across.modules.web.AcrossWebModule;
import org.springframework.boot.SpringApplication;

import java.util.Collections;

@AcrossApplication(
		modules = {
				AcrossWebModule.NAME,
				AcrossHibernateJpaModule.NAME,
				AdminWebModule.NAME,
				BootstrapUiModule.NAME,
				EntityModule.NAME,
				PropertiesModule.NAME,
				FileManagerModule.NAME
		}
)
public class BootstrapSampleApplication
{
	public static void main( String[] args ) {
		SpringApplication springApplication = new SpringApplication( BootstrapSampleApplication.class );
		springApplication.setDefaultProperties(
				Collections.singletonMap( "spring.config.additional-location", "${user.home}/dev-configs/bootstrapSample-application.yml" ) );
		springApplication.run( args );
	}
}