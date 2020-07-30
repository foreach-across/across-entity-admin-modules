package com.foreach.across.modules.experimental.modals.support;

import com.foreach.across.modules.entity.config.builders.EntityConfigurationBuilder;
import com.foreach.across.modules.experimental.modals.ui.processors.CreateModalViewProcessor;
import com.foreach.across.modules.experimental.modals.ui.processors.CreateViewModalViewProcessor;

import java.util.function.Consumer;

public class ModalConfigurers
{
	public static <U extends EntityConfigurationBuilder<?>> Consumer<U> createViewAsModal() {
		return configuration ->
				configuration.listView( lvb -> lvb.viewProcessor( vp -> vp.createBean( CreateModalViewProcessor.class ) ) )
				             .createFormView( fvb -> fvb.viewProcessor( vp -> vp.createBean( CreateViewModalViewProcessor.class ) ) );
	}
}
