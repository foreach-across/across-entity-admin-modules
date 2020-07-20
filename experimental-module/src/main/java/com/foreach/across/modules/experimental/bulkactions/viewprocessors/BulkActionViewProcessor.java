package com.foreach.across.modules.experimental.bulkactions.viewprocessors;

import com.foreach.across.modules.bootstrapui.elements.TableViewElement;
import com.foreach.across.modules.bootstrapui.resource.BootstrapUiFormElementsWebResources;
import com.foreach.across.modules.bootstrapui.resource.BootstrapUiWebResources;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.bootstrapui.util.SortableTableBuilder;
import com.foreach.across.modules.entity.views.processors.ExtensionViewProcessorAdapter;
import com.foreach.across.modules.entity.views.processors.SortableTableRenderingViewProcessor;
import com.foreach.across.modules.entity.views.processors.support.ViewElementBuilderMap;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.modules.experimental.bulkactions.support.BulkActionItemConfigurer;
import com.foreach.across.modules.web.resource.WebResource;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.resource.WebResourceRule;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.VoidNodeViewElement;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementBuilder;
import com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static com.foreach.across.modules.bootstrapui.elements.BootstrapUiViewElementAttributes.CONTROL_ADAPTER_TYPE;
import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements.bootstrap;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElement.Functions.attribute;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.html;

@Getter
@Setter
@Accessors(fluent = true)
public class BulkActionViewProcessor<T> extends ExtensionViewProcessorAdapter<BulkActionViewProcessor.SelectedItemsHolder>
{
	public static final String BULK_ACTION_FORM_NAME = "bulkActionForm";

	private BiFunction<EntityViewRequest, T, String> submitUrlConfigurer = null;
	private BulkActionItemConfigurer<T> bulkActionItemConfigurer = null;
	private Supplier<String> controlNameSupplier = () -> controlPrefix() + ".selectedItems";

	@Override
	protected void registerWebResources( EntityViewRequest entityViewRequest, EntityView entityView, WebResourceRegistry webResourceRegistry ) {
		webResourceRegistry.apply(
				WebResourceRule.add( WebResource.javascript( "@static:/experimental/web/bulk-actions.js" ) ).toBucket( WebResource.JAVASCRIPT_PAGE_END )
				               .before( BootstrapUiFormElementsWebResources.NAME )
				               .after( BootstrapUiWebResources.NAME ),
				WebResourceRule.add( WebResource.css( "@static:/experimental/web/bulk-actions.css" ) ).toBucket( WebResource.CSS ),
				WebResourceRule.addPackage( BootstrapUiFormElementsWebResources.NAME )
		);
	}

	@Override
	protected SelectedItemsHolder createExtension( EntityViewRequest entityViewRequest, EntityViewCommand command, WebDataBinder dataBinder ) {
		return new SelectedItemsHolder();
	}

	@Override
	protected void createViewElementBuilders( SelectedItemsHolder extension,
	                                          EntityViewRequest entityViewRequest,
	                                          EntityView entityView,
	                                          ViewElementBuilderMap builderMap ) {
		SortableTableBuilder sortableTableBuilder = builderMap.get( SortableTableRenderingViewProcessor.TABLE_BUILDER, SortableTableBuilder.class );

		if ( sortableTableBuilder != null ) {
			Assert.notNull( bulkActionItemConfigurer, "A BulkActionItemConfigurer is required for a bulk actions view" );
			sortableTableBuilder.headerRowProcessor( ( viewElementBuilderContext, row ) -> {
				row.addFirstChild(
						bootstrap.builders.table.headerCell( css.of( "bulk-action-column" ) )
						                        .add( checkbox( "exm-bulk-actions-all", "exm-bulk-actions-all", "",
						                                        ( ctx, element ) -> element.set( css.of( "js-exm-bulk-select-all" ) ) ) )
						                        .build( viewElementBuilderContext )
				);
			} );

			Assert.notNull( controlNameSupplier, "A valid control name is required to register bulk action items" );
			AtomicInteger i = new AtomicInteger( 0 );
			String controlName = controlNameSupplier.get();

			sortableTableBuilder.valueRowProcessor( ( context, row ) -> {
				Object entity = EntityViewElementUtils.currentEntity( context, Object.class );

				if ( entity != null ) {
					Object id = bulkActionItemConfigurer.getIdentifier( context, (T) entity );
					NodeViewElementBuilder checkbox = checkbox( controlName, controlName + i.incrementAndGet(), id,
					                                            ( ctx, element ) -> element.set( css.of( "js-exm-bulk-select-item" ) ) );
					bulkActionItemConfigurer.configureControl( context, (T) entity, checkbox );
					row.addFirstChild( bootstrap.builders.table.cell( css.of( "bulk-action-column" ) )
					                                           .add( checkbox )
					                                           .build( context ) );
				}
			} );
		}
	}

	@Override
	protected void doControl( SelectedItemsHolder extension,
	                          BindingResult bindingResult,
	                          HttpMethod httpMethod,
	                          EntityView entityView,
	                          EntityViewRequest entityViewRequest ) {
		super.doControl( extension, bindingResult, httpMethod, entityView, entityViewRequest );
	}

	@Override
	protected void postRender( SelectedItemsHolder extension,
	                           EntityViewRequest entityViewRequest,
	                           EntityView entityView,
	                           ContainerViewElement container,
	                           ViewElementBuilderContext builderContext ) {
		ContainerViewElementUtils.find( container, "itemsTable-table", TableViewElement.class )
		                         .ifPresent( tableViewElement -> tableViewElement.set( attribute( CONTROL_ADAPTER_TYPE, "bulk-actions-container" ) ) );

		ContainerViewElementUtils.find( container, "itemsTable" )
		                         .ifPresent( table -> ContainerViewElementUtils.findParent( container, table ).ifPresent( tableParent -> {
			                         tableParent.clearChildren();
			                         tableParent.addChild( bootstrap.builders.form()
			                                                                 .post()
			                                                                 .name( BULK_ACTION_FORM_NAME )
			                                                                 .formName( BULK_ACTION_FORM_NAME )
			                                                                 .add( table )
			                                                                 .build( builderContext ) );

		                         } ) );
	}

	private NodeViewElementBuilder checkbox( String attrName,
	                                         String htmlId,
	                                         Object value,
	                                         ViewElementPostProcessor<VoidNodeViewElement> checkboxPostProcessor ) {
		return html.builders.div( css.form.check )
		                    .add(
				                    html.builders.input( css.form.check.input, css.position.cssStatic, attribute( "type", "checkbox" ) )
				                                 .with( attribute( "name", attrName ), attribute( "value", value ) )
				                                 .with( attribute( CONTROL_ADAPTER_TYPE, "checkbox" ) )
				                                 .htmlId( htmlId )
				                                 .andThen( checkboxPostProcessor )
		                    )
		                    .add( bootstrap.builders.hidden().value( "on" ).controlName( "_" + attrName ) );
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SelectedItemsHolder
	{
		private Set<String> selectedItems = new HashSet<>();
	}
}
