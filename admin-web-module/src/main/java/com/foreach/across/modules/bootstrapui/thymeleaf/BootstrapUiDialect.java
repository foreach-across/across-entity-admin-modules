package com.foreach.across.modules.bootstrapui.thymeleaf;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.processor.IProcessor;

import java.util.HashSet;
import java.util.Set;

public class BootstrapUiDialect extends AbstractDialect
{
	public static final String PREFIX = "bootstrap";

	@Override
	public String getPrefix() {
		return PREFIX;
	}

	@Override
	public Set<IProcessor> getProcessors() {
		Set<IProcessor> processors = new HashSet<>();
		processors.add( new ComponentElementProcessor() );

		return processors;
	}
}
