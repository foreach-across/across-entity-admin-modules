package com.foreach.across.modules.bootstrapui.config;

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.modules.bootstrapui.thymeleaf.BootstrapUiDialect;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import com.foreach.across.modules.web.ui.elements.thymeleaf.ContainerViewElementProcessor;
import com.foreach.across.modules.web.ui.elements.thymeleaf.NodeViewElementProcessor;
import com.foreach.across.modules.web.ui.elements.thymeleaf.TextViewElementProcessor;
import com.foreach.across.modules.web.ui.thymeleaf.ThymeleafViewElementProcessorRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.annotation.PostConstruct;

@Configuration
public class ThymeleafDialectConfiguration
{
	@Autowired
	private ApplicationContext applicationContext;

	@PostConstruct
	public void registerThymeleafDialect() {

		Object springTemplateEngine = applicationContext.getBean( "springTemplateEngine" );

		if ( springTemplateEngine instanceof SpringTemplateEngine ) {
			( (SpringTemplateEngine) springTemplateEngine ).addDialect( new BootstrapUiDialect() );
		}
		else {
			throw new RuntimeException( "Bad template engine" );
		}
	}

	@Bean
	@Exposed
	public ThymeleafViewElementProcessorRegistry thymeleafViewElementProcessorRegistry() {
		ThymeleafViewElementProcessorRegistry registry = new ThymeleafViewElementProcessorRegistry();
		registry.registerProcessor( TextViewElement.class, new TextViewElementProcessor() );
		registry.registerProcessor( ContainerViewElement.class, new ContainerViewElementProcessor() );
		registry.registerProcessor( NodeViewElement.class, new NodeViewElementProcessor() );

		return registry;
	}
}
