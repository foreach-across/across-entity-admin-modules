package com.foreach.across.modules.experimental.modals.support.configurers;

import com.foreach.across.modules.entity.config.builders.AbstractWritableAttributesAndViewsBuilder;
import com.foreach.across.modules.entity.config.builders.EntityAssociationBuilder;
import com.foreach.across.modules.entity.config.builders.EntityConfigurationBuilder;
import com.foreach.across.modules.experimental.modals.ui.processors.ModalDeleteViewProcessor;
import com.foreach.across.modules.experimental.modals.ui.processors.ModalItemActionViewProcessor;
import com.foreach.across.modules.experimental.modals.ui.processors.ModalSubmitAndRefreshTableViewProcessor;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Consumer;

import static com.foreach.across.modules.entity.views.util.EntityViewElementUtils.currentEntity;

public class UpdateModalConfigurer<T extends UpdateModalConfigurer<T>> extends BaseModalConfigurer<T>
{
	@Setter(AccessLevel.PROTECTED)
	@Getter(AccessLevel.PROTECTED)
	private Consumer<? extends ModalItemActionViewProcessor> modalCustomizer = vp -> {
	};

	@Setter(AccessLevel.PROTECTED)
	@Getter(AccessLevel.PROTECTED)
	private Consumer<? extends ModalDeleteViewProcessor> deleteCustomizer = vp -> {
	};

	public T modal( Consumer<? extends ModalItemActionViewProcessor> customizer ) {
		this.modalCustomizer = customizer;
		return self();
	}

	public T delete( Consumer<? extends ModalDeleteViewProcessor> customizer ) {
		this.deleteCustomizer = customizer;
		return self();
	}

	@Override
	public <U extends EntityConfigurationBuilder<?>> void consume( U configuration ) {
		configure( configuration );
	}

	@Override
	public <U extends EntityAssociationBuilder> void consume( U association ) {
		configure( association );
	}

	public <U extends AbstractWritableAttributesAndViewsBuilder<?>> void configure( U configuration ) {
		String modalId = "updateModal";
		String modalSelector = "#" + modalId;
		configuration.listView( lvb -> lvb.viewProcessor(
				vp -> vp.createBean( ModalItemActionViewProcessor.class )
				        .withName( "updateModalItemActionViewProcessor" )
				        .skipIfPresent()
				        .configure(
						        mclvp -> mclvp.modalId( modalId )
						                      .url( ( linkBuilder, ctx ) -> linkBuilder.forInstance( currentEntity( ctx ) )
						                                                               .updateView()
						                                                               .toUriString() )
						                      .partial( "content" )
						                      .actionRole( "edit" )
				        )
				        .configure( getModalCustomizer() )
		) );
		configuration.updateFormView(
				fvb -> fvb.viewProcessor(
						vp -> vp.createBean( ModalSubmitAndRefreshTableViewProcessor.class )
						        .skipIfPresent()
						        .configure(
								        mfvp -> mfvp.modalSelector( modalSelector )
								                    .elementName( "btn-save" )
								                    .url( ( linkBuilder, ctx ) -> linkBuilder.forInstance( currentEntity( ctx ) )
								                                                             .updateView()
								                                                             .toUriString() )
						        )
						        .configure( getSubmissionCustomizer() )
				).viewProcessor(
						vp -> vp.provideBean( new ModalDeleteViewProcessor() )
						        .skipIfPresent()
						        .configure( mdvp -> mdvp.elementName( "btn-delete" )
						                                .modalSelector( modalSelector ) )
						        .configure( getDeleteCustomizer() )
				).viewProcessor( customizeFormViewCancelButton( modalSelector ) )
		);
	}
}
