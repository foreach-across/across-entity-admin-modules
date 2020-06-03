package com.across.samples.bootstrap;

import com.foreach.across.AcrossApplicationRunner;
import com.foreach.across.config.AcrossApplication;
import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.adminwebthemes.AdminWebThemesModule;
import com.foreach.across.modules.bootstrapui.BootstrapUiModule;
import com.foreach.across.modules.dynamicforms.DynamicFormsModule;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.filemanager.FileManagerModule;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.properties.PropertiesModule;

@AcrossApplication(
		modules = {
				AdminWebThemesModule.NAME,
				AcrossHibernateJpaModule.NAME,
				AdminWebModule.NAME,
				BootstrapUiModule.NAME,
				EntityModule.NAME,
				PropertiesModule.NAME,
				FileManagerModule.NAME,
				DynamicFormsModule.NAME
		}
)
public class BootstrapSampleApplication
{
	public static void main( String[] args ) {
		AcrossApplicationRunner.run( BootstrapSampleApplication.class );
	}
}