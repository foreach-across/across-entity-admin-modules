package com.foreach.across.modules.experimental.modals.support.configurers;

import com.foreach.across.modules.entity.config.builders.EntityAssociationBuilder;
import com.foreach.across.modules.entity.config.builders.EntityConfigurationBuilder;
import com.foreach.across.modules.entity.config.builders.EntityViewProcessorConfigurer;
import com.foreach.across.modules.entity.views.EntityViewProcessor;
import com.foreach.across.modules.experimental.modals.ui.processors.ModalCancelViewProcessor;
import com.foreach.across.modules.experimental.modals.ui.processors.ModalSubmitAndRefreshTableViewProcessor;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Consumer;

public abstract class BaseModalConfigurer<T extends BaseModalConfigurer<T>>
{
	@Setter(AccessLevel.PROTECTED)
	@Getter(AccessLevel.PROTECTED)
	private Consumer<? extends ModalSubmitAndRefreshTableViewProcessor> submissionCustomizer = vp -> {
	};

	@Setter(AccessLevel.PROTECTED)
	@Getter(AccessLevel.PROTECTED)
	private Consumer<? extends ModalCancelViewProcessor> cancelCustomizer = vp -> {
	};

	public T submit( Consumer<? extends ModalSubmitAndRefreshTableViewProcessor> customizer ) {
		this.submissionCustomizer = customizer;
		return self();
	}

	public T cancel( Consumer<? extends ModalCancelViewProcessor> customizer ) {
		this.cancelCustomizer = customizer;
		return self();
	}

	protected Consumer<EntityViewProcessorConfigurer<? extends EntityViewProcessor>> customizeFormViewCancelButton( String modalSelector ) {
		return vp -> vp.createBean( ModalCancelViewProcessor.class )
		               .skipIfPresent()
		               .configure( mfvp -> mfvp.elementName( "btn-cancel" ).modalSelector( modalSelector ) )
		               .configure( getCancelCustomizer() );
	}

	public abstract <U extends EntityConfigurationBuilder<?>> void consume( U configuration );

	public abstract <U extends EntityAssociationBuilder> void consume( U association );

	protected T self() {
		return (T) this;
	}
}
