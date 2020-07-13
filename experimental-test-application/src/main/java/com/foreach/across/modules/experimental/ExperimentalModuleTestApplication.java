package com.foreach.across.modules.experimental;

import com.foreach.across.AcrossApplicationRunner;
import com.foreach.across.config.AcrossApplication;
import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.bootstrapui.BootstrapUiModule;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;

@AcrossApplication(modules = { EntityModule.NAME, BootstrapUiModule.NAME, AdminWebModule.NAME, AcrossHibernateJpaModule.NAME, ExperimentalModule.NAME })
public class ExperimentalModuleTestApplication
{
	public static void main( String[] args ) {
		AcrossApplicationRunner.run( ExperimentalModuleTestApplication.class, args );
	}
}
