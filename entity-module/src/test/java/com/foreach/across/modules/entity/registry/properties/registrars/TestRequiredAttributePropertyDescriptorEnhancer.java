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

package com.foreach.across.modules.entity.registry.properties.registrars;

import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.registry.properties.DefaultEntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptorFactory;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptorFactoryImpl;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyDescriptor;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.internal.engine.ValidatorFactoryImpl;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.validation.MessageInterpolatorFactory;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.constraints.NotNull;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
public class TestRequiredAttributePropertyDescriptorEnhancer
{
	private RequiredAttributePropertyDescriptorEnhancer enhancer = new RequiredAttributePropertyDescriptorEnhancer();

	private DefaultEntityPropertyRegistry propertyRegistry = new DefaultEntityPropertyRegistry();

	@Before
	public void before() {
		EntityPropertyDescriptorFactory descriptorFactory = new EntityPropertyDescriptorFactoryImpl();
		DefaultPropertiesRegistrar propertiesRegistrar = new DefaultPropertiesRegistrar( descriptorFactory );
		LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean();
		MessageInterpolatorFactory interpolatorFactory = new MessageInterpolatorFactory();
		factoryBean.setMessageInterpolator( interpolatorFactory.getObject() );
		factoryBean.afterPropertiesSet();

		ValidationMetadataPropertiesRegistrar metadataPropertiesRegistrar = new ValidationMetadataPropertiesRegistrar( factoryBean );
		propertiesRegistrar.accept( SomeProps.class, propertyRegistry );
		metadataPropertiesRegistrar.accept( SomeProps.class, propertyRegistry );
	}

	@Test
	public void doNothingIfAlreadyPresent() {
		MutableEntityPropertyDescriptor notNull = prop( "notNull" );
		notNull.setAttribute( EntityAttributes.PROPERTY_REQUIRED, false );
		enhancer.enhance( SomeProps.class, notNull );

		assertThat( (Boolean) notNull.getAttribute( EntityAttributes.PROPERTY_REQUIRED, Boolean.class ) ).isFalse();
	}

	@Test
	public void requiredStatus() {
		assertThat( isRequired( "simple" ) ).isNull();
		assertThat( isRequired( "notNull" ) ).isTrue();
		assertThat( isRequired( "notBlank" ) ).isTrue();
		assertThat( isRequired( "notEmpty" ) ).isTrue();
	}

	private Boolean isRequired( String propertyName ) {
		MutableEntityPropertyDescriptor descriptor = prop( propertyName );
		enhancer.enhance( SomeProps.class, descriptor );
		return descriptor.getAttribute( EntityAttributes.PROPERTY_REQUIRED, Boolean.class );
	}

	private MutableEntityPropertyDescriptor prop( String propertyName ) {
		return propertyRegistry.getProperty( propertyName );
	}

	@Data
	static class SomeProps
	{
		public String simple;

		@NotNull
		public String notNull;

		@NotBlank
		public String notBlank;

		@NotEmpty
		public String notEmpty;
	}
}

