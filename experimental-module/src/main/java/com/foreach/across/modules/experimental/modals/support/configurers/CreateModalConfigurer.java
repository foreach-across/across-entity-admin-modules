package com.foreach.across.modules.experimental.modals.support.configurers;

import com.foreach.across.modules.entity.config.builders.AbstractWritableAttributesAndViewsBuilder;
import com.foreach.across.modules.entity.config.builders.EntityAssociationBuilder;
import com.foreach.across.modules.entity.config.builders.EntityConfigurationBuilder;
import com.foreach.across.modules.experimental.modals.ui.processors.ModalCreateButtonListViewProcessor;
import com.foreach.across.modules.experimental.modals.ui.processors.ModalSubmitAndRefreshTableViewProcessor;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.function.Consumer;

@Setter(AccessLevel.PROTECTED)
@Getter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateModalConfigurer<T extends CreateModalConfigurer<T>> extends BaseModalConfigurer<T>
{
	private Consumer<? extends ModalCreateButtonListViewProcessor> modalCustomizer = vp -> {
	};

	@SuppressWarnings("unchecked")
	public static <T extends CreateModalConfigurer<T>> T instance() {
		return (T) new CreateModalConfigurer<T>();
	}

	public T modal( Consumer<? extends ModalCreateButtonListViewProcessor> customizer ) {
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
		String modalId = "createModal";
		String modalSelector = "#" + modalId;
		configuration.listView( lvb -> lvb.viewProcessor(
				vp -> vp.createBean( ModalCreateButtonListViewProcessor.class )
				        .configure(
						        mcblvp -> mcblvp.modalId( modalId )
						                        .url( ( linkBuilder, ctx ) -> linkBuilder.createView().toUriString() )
						                        .partial( "content" )
				        )
				        .configure( getModalCustomizer() )
		) );
		configuration.createFormView( fvb -> fvb.viewProcessor(
				vp -> vp.createBean( ModalSubmitAndRefreshTableViewProcessor.class )
				        .configure(
						        mfvp -> mfvp.modalSelector( modalSelector )
						                    .elementName( "btn-save" )
						                    .url( ( linkBuilder, ctx ) -> linkBuilder.createView().toUriString() )
				        )
				        .configure( getSubmissionCustomizer() )
		                              ).viewProcessor( customizeFormViewCancelButton( modalSelector ) )
		);
	}

}
