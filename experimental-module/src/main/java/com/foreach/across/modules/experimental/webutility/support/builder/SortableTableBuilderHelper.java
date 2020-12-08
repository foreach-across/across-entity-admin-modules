package com.foreach.across.modules.experimental.webutility.support.builder;

import com.foreach.across.core.annotations.ConditionalOnAcrossModule;
import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.bootstrapui.elements.ButtonViewElement;
import com.foreach.across.modules.bootstrapui.elements.LinkViewElement;
import com.foreach.across.modules.bootstrapui.elements.TableViewElement;
import com.foreach.across.modules.bootstrapui.elements.builder.TableViewElementBuilder;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderHelper;
import com.foreach.across.modules.entity.views.bootstrapui.util.SortableTableBuilder;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.modules.entity.web.links.EntityViewLinks;
import com.foreach.across.modules.experimental.webutility.support.MessageCodeHelper;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.Arrays;
import java.util.Optional;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements.bootstrap;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.html;

@Component
@Exposed
@RequiredArgsConstructor
@ConditionalOnAcrossModule(allOf = {EntityModule.NAME, AdminWebModule.NAME})
public class SortableTableBuilderHelper
{
	private final EntityViewLinks entityViewLinks;
	private final EntityViewElementBuilderHelper entityViewElementBuilderHelper;

	public <V> SortableTableBuilder createSortableTableBuilder( @NonNull Class<V> entityType ) {
		return entityViewElementBuilderHelper.createSortableTableBuilder( entityType );
	}

	public void addHeaderColumn( ViewElementBuilderContext builderContext, TableViewElement.Row row ) {
		row.addChild( new TableViewElementBuilder.Cell().heading( true ).name( "listView-actions" ).build() );
	}

	public void addActionButtons( ViewElementBuilderContext context, TableViewElement.Row row ) {
		addActionButtons( context, row, Actions.EDIT, Actions.DELETE );
	}

	public void addActionButtonForModal( ViewElementBuilderContext context, TableViewElement.Row row, String modalClass, Actions actions ) {
		addActionButtonsInternal( context, row, modalClass, actions );
	}

	public void addActionButtons( ViewElementBuilderContext context, TableViewElement.Row row, Actions... actions ) {
		addActionButtonsInternal( context, row, null, actions );
	}

	private void addActionButtonsInternal( ViewElementBuilderContext context, TableViewElement.Row row, String modalClass, Actions... actions ) {
		String from = getCurrentUrl( context );
		Object rowItem = EntityViewElementUtils.currentEntity( context );

		TableViewElement.Cell cell = new TableViewElement.Cell();

		if ( Arrays.asList( actions ).contains( Actions.EDIT ) ) {
			addEdit( from, rowItem, cell, modalClass, context );
		}

		if ( Arrays.asList( actions ).contains( Actions.DELETE ) ) {
			addDelete( from, rowItem, cell, modalClass, context );
		}

		row.addChild( cell );
	}

	private void addEdit( String from, Object rowItem, TableViewElement.Cell cell, String modalClass, ViewElementBuilderContext context ) {
		if ( modalClass != null ) {
			ButtonViewElement edit = bootstrap.builders.button( css.button.primary )
			                                           .name( modalClass )
			                                           .type( ButtonViewElement.Type.BUTTON_SUBMIT )
			                                           .text( MessageCodeHelper.getMessage( "EntityModule.entities.actions.update", "", "" ) )
			                                           .build( context );
			cell.addChild( edit );
		}
		else {
			LinkViewElement edit = bootstrap.builders.link( css.button.link )
			                                         .url( entityViewLinks.linkTo( rowItem ).updateView()
			                                                              .withQueryParam( "from", from )
			                                                              .toUriString() )
			                                         .title( MessageCodeHelper.getMessage( "EntityModule.entities.actions.update", "", "" ) )
			                                         .name( modalClass )
			                                         .add( html.i( css.fa.solid( "edit" ) ) )
			                                         .build( context );
			cell.addChild( edit );
		}
	}

	private void addDelete( String from, Object rowItem, TableViewElement.Cell cell, String modalClass, ViewElementBuilderContext context ) {
		if ( modalClass != null ) {
			ButtonViewElement delete = bootstrap.builders.button( css.button.primary )
			                                             .name( modalClass )
			                                             .type( ButtonViewElement.Type.BUTTON_SUBMIT )
			                                             .text( MessageCodeHelper.getMessage( "EntityModule.entities.actions.delete", "", "" ) )
			                                             .build( context );
			cell.addChild( delete );
		}
		else {
			LinkViewElement delete = bootstrap.builders.link( css.button.link )
			                                           .url( entityViewLinks.linkTo( rowItem ).deleteView()
			                                                                .withQueryParam( "from", from )
			                                                                .toUriString() )
			                                           .title( MessageCodeHelper.getMessage( "EntityModule.entities.actions.delete", "", "" ) )
			                                           .name( modalClass )
			                                           .add( html.i( css.fa.solid( "times" ), css.text.danger.prefix( "axu" ) ) )
			                                           .build( context );
			cell.addChild( delete );
		}

	}

	public static String getCurrentUrl( EntityViewRequest entityViewRequest ) {
		return String.format( "%s%s", ( (ServletWebRequest) entityViewRequest.getWebRequest() ).getRequest().getRequestURI(),
		                      Optional.ofNullable( entityViewRequest.getWebRequest().getParameter( "view" ) ).map( vn -> String.format( "?view=%s", vn ) )
		                              .orElse( "" ) );
	}

	public static String getCurrentUrl( ViewElementBuilderContext context ) {
		return getCurrentUrl( (EntityViewRequest) context.getAttribute( "entityViewRequest" ) );
	}

	public enum Actions
	{
		EDIT,
		DELETE;
	}

}
