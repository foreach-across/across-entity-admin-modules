package com.foreach.across.modules.it.properties;

import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.AcrossException;
import com.foreach.across.core.context.AcrossContextUtils;
import com.foreach.across.core.installers.InstallerAction;
import com.foreach.across.modules.properties.PropertiesModule;
import com.foreach.across.modules.properties.PropertiesModuleSettings;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = ITConversionService.Config.class)
public class ITConversionService
{
	@Autowired
	@Qualifier("conversionServiceOne")
	private ConversionService conversionServiceOne;

	@Autowired
	@Qualifier("conversionServiceTwo")
	private ConversionService conversionServiceTwo;

	@Autowired
	private ApplicationContext parent;

	private AcrossContext context;
	private PropertiesModule propertiesModule;

	@Before
	public void prepare() {
		propertiesModule = new PropertiesModule();
	}

	@After
	public void teardown() {
		if ( context != null ) {
			context.shutdown();
		}
	}

	@Test
	public void conversionServiceIsCreatedIfNoneIsDefined() {
		ConversionService actual = bootstrapWithoutParent();

		assertNotNull( actual );
		assertNotSame( conversionServiceOne, actual );
		assertNotSame( conversionServiceTwo, actual );
	}

	@Test
	public void availableConversionServiceIsUsedIfPossible() {
		ConversionService actual = bootstrapWithParent();
		assertSame( conversionServiceOne, actual );
	}

	@Test
	public void conversionServiceByNameOverridesDefaultExposed() {
		propertiesModule.setProperty( PropertiesModuleSettings.CONVERSION_SERVICE_BEAN,
		                              "conversionServiceTwo" );

		ConversionService actual = bootstrapWithParent();

		assertSame( conversionServiceTwo, actual );
	}

	@Test
	public void explicitConversionServiceSettingTrumpsAll() {
		ConversionService third = mock( ConversionService.class );

		propertiesModule.setProperty( PropertiesModuleSettings.CONVERSION_SERVICE_BEAN,
		                              "conversionServiceTwo" );
		propertiesModule.setProperty( PropertiesModuleSettings.CONVERSION_SERVICE, third );

		ConversionService actual = bootstrapWithParent();
		assertEquals( third, actual );
	}

	@Test(expected = AcrossException.class)
	public void bootstrapFailsIfConversionServiceBeanNotFound() {
		propertiesModule.setProperty( PropertiesModuleSettings.CONVERSION_SERVICE_BEAN,
		                              "unexistingConversionServiceBean" );

		bootstrapWithParent();
	}

	private ConversionService bootstrapWithoutParent() {
		return bootstrap( false );
	}

	private ConversionService bootstrapWithParent() {
		return bootstrap( true );
	}

	private ConversionService bootstrap( boolean useParent ) {
		context = new AcrossContext( useParent ? parent : null );
		context.setDataSource( mock( DataSource.class ) );
		context.setInstallerAction( InstallerAction.DISABLED );
		context.addModule( propertiesModule );
		context.bootstrap();

		return (ConversionService) AcrossContextUtils.getApplicationContext( propertiesModule ).getBean(
				"propertiesConversionService" );
	}

	@Configuration
	protected static class Config
	{
		@Bean
		@Primary
		public ConversionService conversionServiceOne() {
			return mock( ConversionService.class );
		}

		@Bean
		public ConversionService conversionServiceTwo() {
			return mock( ConversionService.class );
		}
	}
}
