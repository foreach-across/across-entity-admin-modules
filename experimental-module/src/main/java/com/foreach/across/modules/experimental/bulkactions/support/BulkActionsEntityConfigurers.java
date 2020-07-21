package com.foreach.across.modules.experimental.bulkactions.support;

import com.foreach.across.modules.entity.config.builders.EntityViewFactoryBuilder;
import com.foreach.across.modules.experimental.bulkactions.ui.viewprocessors.BulkActionViewProcessor;

import java.util.function.Consumer;

/**
 * Utility configurers to register an {@link BulkActionViewProcessor}.
 */
public class BulkActionsEntityConfigurers
{
	public static <T> Consumer<EntityViewFactoryBuilder> configureBulkActions( BulkActionsConfigurer<T> bulkActionItemConfigurer ) {
		return view -> view.viewProcessor(
				vp -> vp.createBean( BulkActionViewProcessor.class )
				        .configure(
						        bavp -> {
							        BulkActionViewProcessor<T> viewProcessor = bavp;
							        viewProcessor.itemValueResolver( bulkActionItemConfigurer.itemValueProvider() )
							                     .controlNameProvider( bulkActionItemConfigurer::itemControlName )
							                     .formAttributeProvider( bulkActionItemConfigurer::formAttributeName );

							        if ( bulkActionItemConfigurer.itemSelectorControlPostProcessor() != null ) {
								        viewProcessor.itemSelectorControlPostProcessor( bulkActionItemConfigurer.itemSelectorControlPostProcessor() );
							        }
							        if ( bulkActionItemConfigurer.submitUrlProvider() != null ) {
								        viewProcessor.submitUrlResolver( bulkActionItemConfigurer.submitUrlProvider() );
							        }
						        }
				        )
		);
	}
}
