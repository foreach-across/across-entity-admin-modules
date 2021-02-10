package com.foreach.across.modules.experimental.modals.support.configurers;

import com.foreach.across.modules.entity.config.builders.AbstractWritableAttributesAndViewsBuilder;
import com.foreach.across.modules.entity.config.builders.EntityAssociationBuilder;
import com.foreach.across.modules.entity.config.builders.EntityConfigurationBuilder;
import com.foreach.across.modules.experimental.modals.ui.processors.ModalItemActionViewProcessor;
import com.foreach.across.modules.experimental.modals.ui.processors.ModalSubmitAndRefreshTableViewProcessor;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Consumer;

import static com.foreach.across.modules.entity.views.util.EntityViewElementUtils.currentEntity;

public class DeleteModalConfigurer<T extends DeleteModalConfigurer<T>> extends BaseModalConfigurer<T>
{
	@Setter(AccessLevel.PROTECTED)
	@Getter(AccessLevel.PROTECTED)
	private Consumer<? extends ModalItemActionViewProcessor> modalCustomizer = vp -> {
	};

	public T modal( Consumer<? extends ModalItemActionViewProcessor> customizer ) {
		this.modalCustomizer = customizer;
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
		String modalId = "deleteModal";
		String modalSelector = "#" + modalId;
		configuration.listView( lvb -> lvb.viewProcessor(
				vp -> vp.createBean( ModalItemActionViewProcessor.class )
				        .withName( "deleteModalItemActionViewProcessor" )
				        .skipIfPresent()
				        .configure(
						        mclvp -> mclvp.modalId( modalId )
						                      .url( ( linkBuilder, ctx ) -> linkBuilder.forInstance( currentEntity( ctx ) )
						                                                               .deleteView()
						                                                               .toUriString() )
						                      .partial( "content" )
						                      .actionRole( "delete" )
				        )
				        .configure( getModalCustomizer() )
		) );
		configuration.deleteFormView(
				fvb -> fvb.viewProcessor(
						vp -> vp.createBean( ModalSubmitAndRefreshTableViewProcessor.class )
						        .skipIfPresent()
						        .configure(
								        mfvp -> mfvp.modalSelector( modalSelector )
								                    .elementName( "btn-delete" )
								                    .url( ( linkBuilder, ctx ) -> linkBuilder.forInstance( currentEntity( ctx ) )
								                                                             .deleteView()
								                                                             .toUriString() )
						        )
						        .configure( getSubmissionCustomizer() )
				).viewProcessor( customizeFormViewCancelButton( modalSelector ) )
		);
	}
}