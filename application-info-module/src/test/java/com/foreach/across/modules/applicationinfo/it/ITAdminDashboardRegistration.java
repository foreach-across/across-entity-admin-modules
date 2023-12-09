package com.foreach.across.modules.applicationinfo.it;

import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.adminweb.AdminWebModuleSettings;
import com.foreach.across.modules.adminweb.config.RememberMeProperties;
import com.foreach.across.modules.applicationinfo.ApplicationInfoModule;
import com.foreach.across.modules.applicationinfo.ApplicationInfoModuleSettings;
import com.foreach.across.test.AcrossTestWebContext;
import com.foreach.across.test.support.AcrossTestWebContextBuilder;
import org.junit.jupiter.api.Test;

import static com.foreach.across.test.support.AcrossTestBuilders.web;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author niels
 * @since 25/02/2015
 */
public class ITAdminDashboardRegistration
{
	@Test
	public void adminWebBeforeApplicationInfo() {
		assertAdminWebSettings(
				"My App", "rm-admin-myapp",
				web( false )
						.property( ApplicationInfoModuleSettings.APPLICATION_ID, "myapp" )
						.property( ApplicationInfoModuleSettings.APPLICATION_NAME, "My App" )
						.modules( ApplicationInfoModule.NAME, AdminWebModule.NAME )
		);
	}

	@Test
	public void specificSettingsOnParentAreKept() {
		assertAdminWebSettings(
				"My App", "my-remember-me-cookie",
				web( false )
						.property( AdminWebModuleSettings.REMEMBER_ME_COOKIE, "my-remember-me-cookie" )
						.property( ApplicationInfoModuleSettings.APPLICATION_ID, "myapp" )
						.property( ApplicationInfoModuleSettings.APPLICATION_NAME, "My App" )
						.modules( ApplicationInfoModule.NAME, AdminWebModule.NAME )
		);

		assertAdminWebSettings(
				"Admin Title", "rm-admin-myapp",
				web( false )
						.property( AdminWebModuleSettings.TITLE, "Admin Title" )
						.property( ApplicationInfoModuleSettings.APPLICATION_ID, "myapp" )
						.property( ApplicationInfoModuleSettings.APPLICATION_NAME, "My App" )
						.modules( ApplicationInfoModule.NAME, AdminWebModule.NAME )
		);
	}

	@Test
	public void specificSettingsOnAdminWebModuleDirectlyAreKept() {
		AdminWebModule adminWebModule = new AdminWebModule();
		adminWebModule.setProperty( AdminWebModuleSettings.REMEMBER_ME_COOKIE, "my-other-remember-me-cookie" );

		assertAdminWebSettings(
				"My App", "my-other-remember-me-cookie",
				web( false )
						.property( ApplicationInfoModuleSettings.APPLICATION_ID, "myapp" )
						.property( ApplicationInfoModuleSettings.APPLICATION_NAME, "My App" )
						.modules( ApplicationInfoModule.NAME )
						.modules( adminWebModule )
		);

		adminWebModule = new AdminWebModule();
		adminWebModule.setProperty( AdminWebModuleSettings.TITLE, "Other Admin Title" );

		assertAdminWebSettings(
				"Other Admin Title", "rm-admin-myapp",
				web( false )
						.property( ApplicationInfoModuleSettings.APPLICATION_ID, "myapp" )
						.property( ApplicationInfoModuleSettings.APPLICATION_NAME, "My App" )
						.modules( ApplicationInfoModule.NAME )
						.modules( adminWebModule )
		);
	}

	private void assertAdminWebSettings( String expectedTitle,
	                                     String cookieName,
	                                     AcrossTestWebContextBuilder builder ) {
		try (AcrossTestWebContext ctx = builder.build()) {
			AdminWebModuleSettings settings
					= ctx.getBeanOfTypeFromModule( AdminWebModule.NAME, AdminWebModuleSettings.class );
			assertEquals( expectedTitle, settings.getTitle() );

			RememberMeProperties rememberMeProperties
					= ctx.getBeanOfTypeFromModule( AdminWebModule.NAME, RememberMeProperties.class );
			assertEquals( cookieName, rememberMeProperties.getCookie() );
		}
	}
}
