package com.across.samples.bootstrap.application.config;

import com.foreach.across.modules.web.AcrossWebModule;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.stereotype.Component;

import static com.across.samples.bootstrap.application.config.ManufacturerConfiguration.manufacturers;

@Component
@RequiredArgsConstructor
public class StringToManufactererConverter implements Converter<String, ManufacturerConfiguration.Manufacturer>
{
	@Override
	public ManufacturerConfiguration.Manufacturer convert( String name ) {
		return manufacturers.getOrDefault( name, null );
	}

	@Autowired
	void registerToMvcConversionService( @Qualifier(AcrossWebModule.CONVERSION_SERVICE_BEAN) ConverterRegistry mvcConversionService ) {
		mvcConversionService.addConverter( this );
	}
}
