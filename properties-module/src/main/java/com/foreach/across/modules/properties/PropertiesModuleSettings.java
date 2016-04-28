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
package com.foreach.across.modules.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.convert.ConversionService;

/**
 * @author Arne Vandamme
 */
@ConfigurationProperties(prefix = "propertiesModule")
public class PropertiesModuleSettings
{
	/**
	 * Explicitly specify the default ConversionService instance to use for property maps.
	 * <p/>
	 * Value: {@link org.springframework.core.convert.ConversionService}
	 */
	public static final String CONVERSION_SERVICE = "propertiesModule.conversionService";

	/**
	 * Specify the name fo the ConversionService bean to use for the property maps.
	 * If a bean name is specified but the bean is not found, the module will not bootstrap.
	 * <p/>
	 * Value: String
	 */
	public static final String CONVERSION_SERVICE_BEAN = "propertiesModule.conversionServiceBean";

	private ConversionService conversionService;
	private String conversionServiceBean;

	public ConversionService getConversionService() {
		return conversionService;
	}

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public String getConversionServiceBean() {
		return conversionServiceBean;
	}

	public void setConversionServiceBean(String conversionServiceBean) {
		this.conversionServiceBean = conversionServiceBean;
	}
}
