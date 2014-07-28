package com.foreach.across.modules.properties.business;

import com.foreach.spring.util.DirectPropertiesSource;

import java.util.Map;

/**
 * @author Arne Vandamme
 */
public class StringPropertiesSource extends DirectPropertiesSource<String>
{
	public StringPropertiesSource( Map<String, String> map ) {
		super( map );
	}
}
