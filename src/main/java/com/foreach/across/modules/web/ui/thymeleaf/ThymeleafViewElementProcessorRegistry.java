package com.foreach.across.modules.web.ui.thymeleaf;

import com.foreach.across.modules.web.ui.ViewElement;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

@Service
public class ThymeleafViewElementProcessorRegistry
{
	private final Map<Class<? extends ViewElement>, ThymeleafViewElementProcessor> processors = new HashMap<>();

	public void registerProcessor( Class<? extends ViewElement> viewElementType,
	                               ThymeleafViewElementProcessor processor ) {
		processors.put( viewElementType, processor );
	}

	public ThymeleafViewElementProcessor getProcessor( ViewElement viewElement ) {
		Assert.notNull( viewElement );
		return processors.get( viewElement.getClass() );
	}
}
