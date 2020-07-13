package com.foreach.across.modules.experimental.bulkactions.configurers;

import com.foreach.across.modules.entity.config.builders.EntityViewFactoryBuilder;
import com.foreach.across.modules.entity.config.builders.EntityViewProcessorConfigurer;
import com.foreach.across.modules.entity.views.EntityViewProcessor;
import com.foreach.across.modules.experimental.bulkactions.viewprocessors.BulkActionViewProcessor;

import java.util.function.Consumer;

public class BulkActionsEntityConfigurer
{
	public static void configureBulkActions( EntityViewProcessorConfigurer<? extends EntityViewProcessor> vb ) {
		vb.provideBean( new BulkActionViewProcessor() )
		  .order( 200 );
	}

	public static Consumer<EntityViewFactoryBuilder> configureBulkActions( BulkActionsConfigurer bulkActionItemConfigurer ) {
		return view -> view.viewProcessor(
				vp -> vp.createBean( BulkActionViewProcessor.class )
				        .configure( bavp -> bavp.submitUrlConfigurer( bulkActionItemConfigurer.submitUrlConfigurer() )
				                                .bulkActionItemConfigurer( bulkActionItemConfigurer.itemConfigurer() ) )
		);
	}

	public static Consumer<EntityViewFactoryBuilder> configureBulkActions( BulkActionItemConfigurer<?> bulkActionItemConfigurer ) {
		return view -> view.viewProcessor(
				vp -> vp.createBean( BulkActionViewProcessor.class )
				        .configure( bavp -> bavp.bulkActionItemConfigurer( bulkActionItemConfigurer ) )
		);
	}
}
