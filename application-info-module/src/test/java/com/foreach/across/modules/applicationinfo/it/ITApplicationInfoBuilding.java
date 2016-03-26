package com.foreach.across.modules.applicationinfo.it;

import com.foreach.across.modules.applicationinfo.ApplicationInfoModule;
import com.foreach.across.modules.applicationinfo.ApplicationInfoModuleSettings;
import com.foreach.across.modules.applicationinfo.business.AcrossApplicationInfo;
import com.foreach.across.test.AcrossTestContext;
import org.apache.commons.lang3.time.FastDateFormat;
import org.junit.Test;

import java.util.Date;

import static com.foreach.across.test.support.AcrossTestBuilders.standard;
import static org.junit.Assert.*;

public class ITApplicationInfoBuilding
{
	@Test
	public void noPropertiesConfigured() {
		long startOfTest = System.currentTimeMillis();

		try (
				AcrossTestContext ctx = standard()
						.useTestDataSource( false )
						.modules( ApplicationInfoModule.NAME )
						.build()
		) {
			AcrossApplicationInfo applicationInfo = ctx.getBeanOfType( AcrossApplicationInfo.class );

			assertNotNull( applicationInfo );
			assertEquals( "unknown", applicationInfo.getApplicationId() );
			assertEquals( "unknown", applicationInfo.getApplicationName() );
			assertEquals( "unknown", applicationInfo.getEnvironmentId() );
			assertEquals( "unknown", applicationInfo.getEnvironmentName() );

			assertEquals( "unknown", applicationInfo.getBuildId() );
			assertNull( applicationInfo.getBuildDate() );

			assertNotNull( applicationInfo.getHostName() );
			assertEquals(
					String.format(
							"%s-%s-%s",
							applicationInfo.getApplicationId(),
							applicationInfo.getEnvironmentId(),
							applicationInfo.getHostName()
					),
					applicationInfo.getInstanceId()
			);

			assertNotNull( applicationInfo.getStartupDate() );
			assertNotNull( applicationInfo.getBootstrapStartDate() );
			assertNotNull( applicationInfo.getBootstrapEndDate() );

			assertTrue( applicationInfo.getStartupDate().getTime() >= startOfTest );
			assertTrue(
					applicationInfo.getBootstrapStartDate().getTime() >= applicationInfo.getStartupDate().getTime() );
			assertTrue( applicationInfo.getBootstrapEndDate().getTime() >= applicationInfo.getBootstrapStartDate()
			                                                                              .getTime() );
		}
	}

	@Test
	public void propertiesSet() {
		long startOfTest = System.currentTimeMillis();

		try (
				AcrossTestContext ctx = standard()
						.property( ApplicationInfoModuleSettings.APPLICATION_ID, "myapp" )
						.property( ApplicationInfoModuleSettings.APPLICATION_NAME, "My Application" )
						.property( ApplicationInfoModuleSettings.ENVIRONMENT_ID, "test" )
						.property( ApplicationInfoModuleSettings.ENVIRONMENT_NAME, "Test Environment" )
						.property( ApplicationInfoModuleSettings.HOSTNAME, "someserver.test.com" )
						.property( ApplicationInfoModuleSettings.BUILD_ID, "release-1" )
						.property( ApplicationInfoModuleSettings.BUILD_DATE, new Date() )
						.property( ApplicationInfoModuleSettings.STARTUP_DATE, "2014-10-15 13:30:15" )
						.useTestDataSource( false )
						.modules( ApplicationInfoModule.NAME )
						.build()
		) {
			AcrossApplicationInfo applicationInfo = ctx.getBeanOfType( AcrossApplicationInfo.class );

			assertNotNull( applicationInfo );
			assertEquals( "myapp", applicationInfo.getApplicationId() );
			assertEquals( "My Application", applicationInfo.getApplicationName() );
			assertEquals( "test", applicationInfo.getEnvironmentId() );
			assertEquals( "Test Environment", applicationInfo.getEnvironmentName() );
			assertEquals( "someserver.test.com", applicationInfo.getHostName() );

			assertEquals( "release-1", applicationInfo.getBuildId() );
			assertNotNull( applicationInfo.getBuildDate() );
			assertTrue( applicationInfo.getBuildDate().getTime() >= startOfTest );

			assertNotNull( applicationInfo.getHostName() );
			assertEquals( "myapp-test-someserver.test.com", applicationInfo.getInstanceId() );

			assertNotNull( applicationInfo.getStartupDate() );
			assertNotNull( applicationInfo.getBootstrapStartDate() );
			assertNotNull( applicationInfo.getBootstrapEndDate() );

			assertEquals( "2014-10-15 13:30:15",
			              FastDateFormat.getInstance( "yyyy-MM-dd HH:mm:ss" )
			                            .format( applicationInfo.getStartupDate() )
			);

			assertTrue( applicationInfo.getBootstrapStartDate().after( new Date( startOfTest ) ) );
			assertTrue( applicationInfo.getBootstrapEndDate().after( applicationInfo.getBootstrapStartDate() ) );
		}
	}
}
