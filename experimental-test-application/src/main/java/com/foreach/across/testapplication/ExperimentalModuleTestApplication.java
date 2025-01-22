package com.foreach.across.testapplication;

import com.foreach.across.AcrossApplicationRunner;
import com.foreach.across.config.AcrossApplication;
import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.bootstrapui.BootstrapUiModule;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.experimental.ExperimentalModule;
import com.foreach.across.modules.experimental.daterange.DateRangeModule;
import com.foreach.across.modules.experimental.entitycontrols.EntityControlsModule;
import com.foreach.across.modules.experimental.webutility.WebUtilityModule;
import com.foreach.across.modules.filemanager.FileManagerModule;
import com.foreach.across.modules.filemanager.services.ExpiringFileRepository;
import com.foreach.across.modules.filemanager.services.FileManager;
import com.foreach.across.modules.filemanager.services.FileRepository;
import com.foreach.across.modules.filemanager.services.LocalFileRepository;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.properties.PropertiesModule;
import org.springframework.context.annotation.Bean;

@AcrossApplication(modules = { EntityModule.NAME, BootstrapUiModule.NAME, AdminWebModule.NAME, AcrossHibernateJpaModule.NAME, ExperimentalModule.NAME,
                               WebUtilityModule.NAME, FileManagerModule.NAME, PropertiesModule.NAME, EntityControlsModule.NAME,
                               DateRangeModule.NAME
})
public class ExperimentalModuleTestApplication
{
	public static void main( String[] args ) {
		AcrossApplicationRunner.run( ExperimentalModuleTestApplication.class, args );
	}

	@Bean
	public FileRepository tempRepository() {
		return ExpiringFileRepository.builder()
		                             .targetFileRepository(
				                             LocalFileRepository.builder().repositoryId( FileManager.TEMP_REPOSITORY )
				                                                .rootFolder( "../local-data/storage/temp" )
				                                                .build()
		                             )
		                             .expireOnEvict( true )
		                             .expireOnShutdown( true )
		                             .build();
	}
}
