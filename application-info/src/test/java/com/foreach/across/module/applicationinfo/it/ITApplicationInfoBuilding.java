package com.foreach.across.module.applicationinfo.it;

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.module.applicationinfo.ApplicationInfoModule;
import com.foreach.across.module.applicationinfo.business.AcrossApplicationInfo;
import com.foreach.across.test.AcrossTestContext;
import org.junit.Test;

import static org.junit.Assert.*;

public class ITApplicationInfoBuilding
{
	@Test
	public void noPropertiesConfigured() {
		long startOfTest = System.currentTimeMillis();

		try (AcrossTestContext ctx = new AcrossTestContext( new ModuleConfig() )) {
			AcrossApplicationInfo applicationInfo = ctx.beanRegistry().getBeanOfType( AcrossApplicationInfo.class );

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
			assertTrue( applicationInfo.getBootstrapStartDate().after( applicationInfo.getStartupDate() ) );
			assertTrue( applicationInfo.getBootstrapEndDate().after( applicationInfo.getBootstrapStartDate() ) );
		}
	}

	protected static class ModuleConfig implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			context.addModule( new ApplicationInfoModule() );
		}
	}
}
