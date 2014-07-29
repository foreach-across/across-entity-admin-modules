package com.foreach.across.modules.it.properties;

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.context.info.AcrossContextInfo;
import com.foreach.across.core.context.info.AcrossModuleInfo;
import com.foreach.across.modules.it.properties.definingmodule.DefiningModule;
import com.foreach.across.modules.it.properties.definingmodule.business.User;
import com.foreach.across.modules.it.properties.definingmodule.business.UserProperties;
import com.foreach.across.modules.it.properties.definingmodule.registry.UserPropertyRegistry;
import com.foreach.across.modules.it.properties.definingmodule.services.UserPropertyService;
import com.foreach.across.modules.it.properties.extendingmodule.ExtendingModule;
import com.foreach.across.modules.it.properties.extendingmodule.business.ClientProperties;
import com.foreach.across.modules.it.properties.extendingmodule.config.ClientPropertiesConfig;
import com.foreach.across.modules.it.properties.extendingmodule.config.UserPropertiesConfig;
import com.foreach.across.modules.it.properties.extendingmodule.services.ClientPropertyService;
import com.foreach.across.modules.properties.PropertiesModule;
import com.foreach.across.test.AcrossTestConfiguration;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = ITDefineAndExtendBusinessProperties.Config.class)
public class ITDefineAndExtendBusinessProperties
{
	@Autowired
	private UserPropertyService userPropertyService;

	@Autowired
	private ClientPropertyService clientPropertyService;

	@Autowired
	private UserPropertyRegistry userPropertyRegistry;

	@Autowired
	private AcrossContextInfo acrossContextInfo;

	@Test
	public void forceTrackingUpdate() throws InterruptedException {
		AcrossModuleInfo moduleInfo = acrossContextInfo.getModuleInfo( "ExtendingModule" );

		Thread.sleep( 1000 );

		userPropertyRegistry.register( moduleInfo, UserPropertiesConfig.BOOLEAN, Boolean.class );
	}

	@Test
	public void defaultPropertyValues() {
		User userOne = new User( 1, "one" );

		UserProperties propsOne = userPropertyService.getProperties( userOne.getId() );
		assertFalse( (boolean) propsOne.getValue( UserPropertiesConfig.BOOLEAN ) );
		assertNull( propsOne.getValue( UserPropertiesConfig.DATE ) );
		assertEquals( new BigDecimal( 0 ), propsOne.getValue( UserPropertiesConfig.DECIMAL ) );

		ClientProperties clientProps = clientPropertyService.getProperties( userOne.getId() );
		assertTrue( (boolean) clientProps.getValue( ClientPropertiesConfig.BOOLEAN ) );
	}

	@Test
	public void propertyMapPersistence() {
		User userTwo = new User( 2, "two" );

		UUID uuid = UUID.randomUUID();
		Date date = new Date();
		String randomLongString = RandomStringUtils.randomAscii( 4000 );

		UserProperties created = userPropertyService.getProperties( userTwo.getId() );
		created.set( UserPropertiesConfig.BOOLEAN, true );
		//created.set( UserPropertiesConfig.DATE, date );
		created.set( UserPropertiesConfig.DECIMAL, new BigDecimal( "31.268" ) );
		created.set( "some number", 995 );
		created.set( "uuid", uuid );
		created.set( "some string", randomLongString );

		userPropertyService.saveProperties( created );

		ClientProperties createdClient = clientPropertyService.getProperties( userTwo.getId() );
		createdClient.set( "some string", "other string value" );
		createdClient.set( "uuid", uuid );
		clientPropertyService.saveProperties( createdClient );

		UserProperties fetched = userPropertyService.getProperties( userTwo.getId() );
		ClientProperties fetchedClient = clientPropertyService.getProperties( userTwo.getId() );

		assertNotSame( created, fetched );
		assertFalse( fetched.isEmpty() );
		assertEquals( true, fetched.getValue( UserPropertiesConfig.BOOLEAN ) );
		//assertEquals( date, fetched.getValue( UserPropertiesConfig.DATE ) );
		assertEquals( new BigDecimal( "31.268" ), fetched.getValue( UserPropertiesConfig.DECIMAL ) );
		assertEquals( "995", fetched.getValue( "some number" ) );
		assertEquals( Integer.valueOf( 995 ), fetched.getValue( "some number", Integer.class ) );
		assertEquals( randomLongString, fetched.getValue( "some string" ) );
		assertEquals( uuid, fetched.getValue( "uuid", UUID.class ) );

		assertNotSame( createdClient, fetchedClient );
		assertEquals( 2, fetchedClient.size() );
		assertEquals( "other string value", fetchedClient.getValue( "some string" ) );
		assertEquals( uuid, fetchedClient.getValue( "uuid", UUID.class ) );

		userPropertyService.deleteProperties( userTwo.getId() );
		clientPropertyService.deleteProperties( userTwo.getId() );

		assertTrue( userPropertyService.getProperties( userTwo.getId() ).isEmpty() );
		assertTrue( clientPropertyService.getProperties( userTwo.getId() ).isEmpty() );
	}

	@AcrossTestConfiguration
	protected static class Config implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext acrossContext ) {
			acrossContext.addModule( new PropertiesModule() );
			acrossContext.addModule( new DefiningModule() );
			acrossContext.addModule( new ExtendingModule() );
		}
	}
}
