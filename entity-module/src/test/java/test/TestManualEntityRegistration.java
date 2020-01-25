/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package test;

import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.test.AcrossTestConfiguration;
import com.foreach.across.test.AcrossWebAppConfiguration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arne Vandamme
 * @since 4.0.0
 */
@AcrossWebAppConfiguration(classes = TestManualEntityRegistration.Config.class)
@ExtendWith(SpringExtension.class)
class TestManualEntityRegistration
{
	@Test
	void withTypeAutomaticallyRegistersUnknownType( @Autowired EntityRegistry entityRegistry ) {
		EntityConfiguration<MyClass> entityConfiguration = entityRegistry.getEntityConfiguration( MyClass.class );
		assertThat( entityConfiguration ).isNotNull();
		assertThat( entityConfiguration.getDisplayName() ).isEqualTo( "Test manual entity registration my class" );

		EntityPropertyRegistry propertyRegistry = entityConfiguration.getPropertyRegistry();
		assertThat( propertyRegistry ).isNotNull();

		MyClass instance = new MyClass( "name", 123 );
		assertThat( propertyRegistry.getProperty( "name" ).getPropertyValue( instance ) ).isEqualTo( "name" );
		assertThat( propertyRegistry.getProperty( "value" ).getPropertyValue( instance ) ).isEqualTo( 123 );
		assertThat( propertyRegistry.getProperty( EntityPropertyRegistry.LABEL ).getAttribute( EntityAttributes.LABEL_TARGET_PROPERTY ) ).isEqualTo( "name" );
	}

	@Test
	void registerAddsNewType( @Autowired EntityRegistry entityRegistry ) {
		EntityConfiguration<OtherClass> entityConfiguration = entityRegistry.getEntityConfiguration( OtherClass.class );
		assertThat( entityConfiguration ).isNotNull();
		assertThat( entityConfiguration.getDisplayName() ).isEqualTo( "Test manual entity registration other class" );

		EntityPropertyRegistry propertyRegistry = entityConfiguration.getPropertyRegistry();
		assertThat( propertyRegistry ).isNotNull();

		OtherClass instance = new OtherClass( "name", 123 );
		assertThat( propertyRegistry.getProperty( "name" ).getPropertyValue( instance ) ).isEqualTo( "name" );
		assertThat( propertyRegistry.getProperty( "value" ).getPropertyValue( instance ) ).isEqualTo( 123 );
		assertThat( propertyRegistry.getProperty( EntityPropertyRegistry.LABEL ).getAttribute( EntityAttributes.LABEL_TARGET_PROPERTY ) ).isEqualTo( "name" );
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	static class MyClass
	{
		private String name;
		private int value;
	}

	static class OtherClass extends MyClass
	{
		public OtherClass( String name, int value ) {
			super( name, value );
		}
	}

	@AcrossTestConfiguration(modules = { EntityModule.NAME, AdminWebModule.NAME })
	protected static class Config implements EntityConfigurer
	{
		@Override
		public void configure( EntitiesConfigurationBuilder entities ) {
			entities.withType( MyClass.class );
			entities.register( OtherClass.class );
		}
	}

}
