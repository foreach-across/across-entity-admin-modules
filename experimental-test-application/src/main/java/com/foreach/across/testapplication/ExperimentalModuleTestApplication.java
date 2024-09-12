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
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.properties.PropertiesModule;
import com.foreach.common.filemanager.services.ExpiringFileRepository;
import com.foreach.common.filemanager.services.FileManager;
import com.foreach.common.filemanager.services.FileRepository;
import com.foreach.common.filemanager.services.LocalFileRepository;
import org.springframework.context.annotation.Bean;

@AcrossApplication(modules = { EntityModule.NAME, BootstrapUiModule.NAME, AdminWebModule.NAME, AcrossHibernateJpaModule.NAME, ExperimentalModule.NAME,
                               WebUtilityModule.NAME, PropertiesModule.NAME, EntityControlsModule.NAME,
                               DateRangeModule.NAME//, SpringSecurityModule.NAME
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
