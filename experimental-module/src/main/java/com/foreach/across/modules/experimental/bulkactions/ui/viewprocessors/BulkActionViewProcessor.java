package com.foreach.across.modules.experimental.bulkactions.ui.viewprocessors;

import com.foreach.across.modules.bootstrapui.elements.CheckboxFormElement;
import com.foreach.across.modules.bootstrapui.elements.FormViewElement;
import com.foreach.across.modules.bootstrapui.elements.TableViewElement;
import com.foreach.across.modules.bootstrapui.elements.builder.OptionFormElementBuilder;
import com.foreach.across.modules.bootstrapui.elements.builder.TableViewElementBuilder;
import com.foreach.across.modules.bootstrapui.resource.BootstrapUiFormElementsWebResources;
import com.foreach.across.modules.bootstrapui.resource.BootstrapUiWebResources;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.bootstrapui.util.SortableTableBuilder;
import com.foreach.across.modules.entity.views.processors.EntityViewProcessorAdapter;
import com.foreach.across.modules.entity.views.processors.SortableTableRenderingViewProcessor;
import com.foreach.across.modules.entity.views.processors.support.ViewElementBuilderMap;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.modules.web.resource.WebResource;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.resource.WebResourceRule;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.foreach.across.modules.bootstrapui.elements.BootstrapUiViewElementAttributes.CONTROL_ADAPTER_TYPE;
import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements.bootstrap;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElement.Functions.attribute;

@Getter
@Setter
@Accessors(fluent = true)
public class BulkActionViewProcessor<T> extends EntityViewProcessorAdapter
{
	/**
	 * Name of the {@link FormViewElement} that wraps the table and supports submitting the bulk action selection.
	 */
	public static final String BULK_ACTION_FORM_NAME = "bulkActionForm";

	/**
	 * Provides the control name prefix for the bulk action item controls.
	 */
	private Supplier<String> controlNameProvider = null;

	/**
	 * Specifies the form object on the bulk action {@link FormViewElement}
	 *
	 * @see FormViewElement#setCommandAttribute(String)
	 */
	private Supplier<String> formAttributeProvider = () -> "";

	/**
	 * Resolves the value that should be applied to the control and as such should be submitted when the form is saved.
	 */
	private BiFunction<T, ViewElementBuilderContext, Object> itemValueResolver = null;

	/**
	 * Optional {@link ViewElementPostProcessor} for the item controls.
	 */
	@Nullable
	private ViewElementPostProcessor<CheckboxFormElement> itemSelectorControlPostProcessor;

	/**
	 * Optional url provider to which the form should submit.
	 */
	@Nullable
	private Function<EntityViewRequest, String> submitUrlResolver = null;

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
	protected void createViewElementBuilders( EntityViewRequest entityViewRequest, EntityView entityView, ViewElementBuilderMap builderMap ) {
		SortableTableBuilder sortableTableBuilder = builderMap.get( SortableTableRenderingViewProcessor.TABLE_BUILDER, SortableTableBuilder.class );

		if ( sortableTableBuilder != null ) {
			sortableTableBuilder.headerRowProcessor( ( viewElementBuilderContext, row ) -> {
				TableViewElementBuilder.Cell allItemsSelector = bootstrap.builders.table.headerCell( css.of( "bulk-action-column" ) )
				                                                                        .add( createAllItemsSelector() );
				row.addFirstChild( allItemsSelector.build( viewElementBuilderContext ) );
			} );

			Assert.notNull( controlNameProvider, "A valid control name is required to register bulk action items" );
			String controlName = controlNameProvider.get();

			Assert.notNull( itemValueResolver, "A value resolver must be provided" );
			sortableTableBuilder.valueRowProcessor( ( context, row ) -> {
				Object entity = EntityViewElementUtils.currentEntity( context, Object.class );
				TableViewElementBuilder.Cell itemSelector = bootstrap.builders.table.cell( css.of( "bulk-action-column" ) );
				if ( entity != null ) {
					itemSelector.add( createItemSelector( controlName, entity, context ) );
				}

				row.addFirstChild( itemSelector.build( context ) );
			} );
		}
	}

	private ViewElement createAllItemsSelector() {
		return bootstrap.builders.checkbox()
		                         .value( "" )
		                         .controlName( "exm-bulk-actions-all" )
		                         .with( css.of( "js-exm-bulk-select-all" ) )
		                         .build()
		                         .setRenderAsCustomControl( false )
		                         .setText( "" );
	}

	private CheckboxFormElement createItemSelector( String controlName, Object entity, ViewElementBuilderContext context ) {
		Object id = itemValueResolver.apply( (T) entity, context );
		OptionFormElementBuilder<CheckboxFormElement> checkboxBuilder = bootstrap.builders.checkbox()
		                                                                                  .controlName( controlName )
		                                                                                  .rawValue( entity )
		                                                                                  .value( id )
		                                                                                  .with( css.of( "js-exm-bulk-select-item" ) );
		if ( itemSelectorControlPostProcessor != null ) {
			checkboxBuilder.postProcessor( itemSelectorControlPostProcessor );
		}

		return checkboxBuilder
				.build( context )
				.setRenderAsCustomControl( false )
				.setText( "" );
	}

	@Override
	protected void postRender( EntityViewRequest entityViewRequest,
	                           EntityView entityView,
	                           ContainerViewElement container,
	                           ViewElementBuilderContext builderContext ) {
		ContainerViewElementUtils.find( container, "itemsTable-table", TableViewElement.class )
		                         .ifPresent( tableViewElement -> tableViewElement.set( attribute( CONTROL_ADAPTER_TYPE, "bulk-actions-container" ) ) );

		ContainerViewElementUtils.find( container, "itemsTable" )
		                         .ifPresent( table -> ContainerViewElementUtils.findParent( container, table ).ifPresent( tableParent -> {
			                         List<ViewElement> children = new ArrayList<>( tableParent.getChildren() );
			                         tableParent.clearChildren();
			                         children.forEach(
					                         childElement -> {
						                         if ( childElement != table ) {
							                         tableParent.addChild( childElement );
						                         }
						                         else {
							                         tableParent.addChild( createFormAndWrap( childElement, entityViewRequest, builderContext ) );
						                         }
					                         }
			                         );
		                         } ) );
	}

	private FormViewElement createFormAndWrap( ViewElement childElement,
	                                           EntityViewRequest entityViewRequest,
	                                           ViewElementBuilderContext builderContext ) {
		FormViewElement bulkActionForm = bootstrap.builders.form()
		                                                   .post()
		                                                   .name( BULK_ACTION_FORM_NAME )
		                                                   .formName( BULK_ACTION_FORM_NAME )
		                                                   .add( childElement )
		                                                   .build( builderContext )
		                                                   .setCommandAttribute( formAttributeProvider.get() );
		if ( submitUrlResolver != null ) {
			bulkActionForm.setAction( submitUrlResolver.apply( entityViewRequest ) );
		}
		return bulkActionForm;
	}
}
