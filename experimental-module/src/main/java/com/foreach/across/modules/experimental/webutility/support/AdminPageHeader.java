package com.foreach.across.modules.experimental.webutility.support;

import com.foreach.across.core.annotations.ConditionalOnAcrossModule;
import com.foreach.across.modules.adminweb.menu.AdminMenu;
import com.foreach.across.modules.bootstrapui.components.builder.NavComponentBuilder;
import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyHandlingType;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.views.DispatchingEntityViewFactory;
import com.foreach.across.modules.entity.views.EntityViewFactory;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.processors.SingleEntityPageStructureViewProcessor;
import com.foreach.across.modules.entity.views.processors.support.EntityPageStructureRenderedEvent;
import com.foreach.across.modules.entity.views.processors.support.EntityViewProcessorRegistry;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.entity.web.EntityViewModel;
import com.foreach.across.modules.experimental.entitycontrols.EntityControlsModule;
import com.foreach.across.modules.experimental.entitycontrols.domain.EntityControlFactory;
import com.foreach.across.modules.experimental.webutility.viewelements.EditableValuesUtils;
import com.foreach.across.modules.experimental.webutility.viewelements.WebUtilityViewElementMode;
import com.foreach.across.modules.web.menu.Menu;
import com.foreach.across.modules.web.menu.MenuFactory;
import com.foreach.across.modules.web.ui.ScopedAttributesViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.WebExpressionContext;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

import static com.foreach.across.modules.adminweb.ui.PageContentStructure.ELEMENT_PAGE_TITLE;
import static com.foreach.across.modules.adminweb.ui.PageContentStructure.ELEMENT_PAGE_TITLE_SUB_TEXT;
import static com.foreach.across.modules.experimental.webutility.support.WebUtilityModuleAttributes.EditableValue.DISABLE_ADMINPAGE_ASSOCIATION_HEADER_EDITABLE_VALUE;
import static com.foreach.across.modules.experimental.webutility.support.WebUtilityModuleAttributes.EditableValue.DISABLE_ADMINPAGE_HEADER_EDITABLE_VALUE;
import static com.foreach.across.modules.experimental.webutility.viewelements.refreshablevalues.RefreshableValueViewElementBuilderFactory.propertyId;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElement.Functions.css;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.html;
import static com.foreach.across.modules.web.ui.elements.TextViewElement.text;

/**
 * Configures the default admin page layout:
 * - sets the breadcrumb leaf to a refreshable control value
 * - sets the page title property as an inline control in the page header
 */
@Component
@RequiredArgsConstructor
@ConditionalOnAcrossModule(EntityControlsModule.NAME)
class AdminPageHeader
{
	private final MenuFactory menuFactory;
	private final EntityControlFactory entityControlFactory;
	private final EditableValuesUtils editableValuesUtils;
	private final SpringTemplateEngine templateEngine;

	@EventListener
	public void customizePageHeader( EntityPageStructureRenderedEvent<?> pageStructureRenderedEvent ) {
		alwaysRenderFeedbackSection( pageStructureRenderedEvent );

		EntityViewContext entityViewContext = pageStructureRenderedEvent.getEntityViewContext();
		EntityViewFactory viewFactory = pageStructureRenderedEvent.getEntityViewRequest().getViewFactory();
		if ( entityViewContext.holdsEntity() && viewFactory != null ) {
			configureRefreshableControlAsBreadcrumbValue( entityViewContext );

			ViewElementMode renderMode =
					Boolean.FALSE.equals( entityViewContext.getEntityConfiguration().getAttribute( DISABLE_ADMINPAGE_HEADER_EDITABLE_VALUE ) )
							? WebUtilityViewElementMode.EDITABLE_VALUE
							: WebUtilityViewElementMode.REFRESHABLE_VALUE;
			configurePageHeader( pageStructureRenderedEvent.getPageContentStructure().getHeader(), entityViewContext, viewFactory,
			                     pageStructureRenderedEvent.getBuilderContext(), renderMode );
		}

		EntityViewContext originalEntityViewContext = pageStructureRenderedEvent.getEntityViewRequest().getEntityViewContext();
		if ( originalEntityViewContext.isForAssociation() && originalEntityViewContext.holdsEntity() && viewFactory != null ) {
			configureRefreshableControlAsBreadcrumbValue( originalEntityViewContext );
			ViewElementMode renderMode =
					Boolean.FALSE.equals(
							originalEntityViewContext.getEntityConfiguration().getAttribute( DISABLE_ADMINPAGE_ASSOCIATION_HEADER_EDITABLE_VALUE ) )
							? WebUtilityViewElementMode.EDITABLE_VALUE
							: WebUtilityViewElementMode.REFRESHABLE_VALUE;

			configureAssociationPageHeader( pageStructureRenderedEvent.getPageContentStructure()
			                                                          .findAll( NodeViewElement.class, ve -> css( "tab-pane-header" ).test( ve ) )
			                                                          .findFirst(),
			                                originalEntityViewContext, viewFactory,
			                                pageStructureRenderedEvent.getBuilderContext(), renderMode );
		}
	}

	private void alwaysRenderFeedbackSection( EntityPageStructureRenderedEvent<?> pageStructureRenderedEvent ) {
		// required for unpoly form updates
		pageStructureRenderedEvent.getPageContentStructure().addToFeedback( text( "" ) );

	}

	@SuppressWarnings("unchecked")
	private void configurePageHeader( NodeViewElement header,
	                                  EntityViewContext entityViewContext,
	                                  EntityViewFactory viewFactory,
	                                  ViewElementBuilderContext builderContext,
	                                  ViewElementMode renderMode ) {
		EntityPropertyDescriptor labelProperty = entityViewContext.getPropertyRegistry().getProperty( EntityPropertyRegistry.LABEL );

		if ( labelProperty.hasAttribute( EntityAttributes.LABEL_TARGET_PROPERTY ) ) {
			configurePageHeader( entityViewContext, viewFactory, builderContext, renderMode, header, labelProperty, this::createHeaderViewElement );
		}
	}

	private void configureAssociationPageHeader( Optional<NodeViewElement> oHeader,
	                                             EntityViewContext entityViewContext,
	                                             EntityViewFactory viewFactory,
	                                             ViewElementBuilderContext builderContext,
	                                             ViewElementMode renderMode ) {
		EntityPropertyDescriptor labelProperty = entityViewContext.getPropertyRegistry().getProperty( EntityPropertyRegistry.LABEL );
		if ( labelProperty.hasAttribute( EntityAttributes.LABEL_TARGET_PROPERTY ) && oHeader.isPresent() ) {
			NodeViewElement header = oHeader.get();
			configurePageHeader( entityViewContext, viewFactory, builderContext, renderMode, header, labelProperty,
			                     this::createAssociationHeaderViewElement );
		}
	}

	private void configurePageHeader( EntityViewContext entityViewContext,
	                                  EntityViewFactory viewFactory,
	                                  ViewElementBuilderContext builderContext,
	                                  ViewElementMode renderMode, NodeViewElement header, EntityPropertyDescriptor labelProperty,
	                                  BiFunction<String, String, ViewElement> headerViewElementResolver ) {
		String targetProperty = labelProperty.getAttribute( EntityAttributes.LABEL_TARGET_PROPERTY, String.class );

		Class<Object> entityType = entityViewContext.getEntityConfiguration().getEntityType();
		try (ScopedAttributesViewElementBuilderContext ignore = builderContext.withAttributeOverride( EntityViewModel.VIEW_CONTEXT, entityViewContext )) {
			Map<String, ViewElement> controls = entityControlFactory
					.createControlsForClass( entityType )
					.showProperties( targetProperty )
					.properties( props -> props.property( targetProperty )
					                           .attribute( EntityPropertyHandlingType.class, EntityPropertyHandlingType.BINDER ) )
					.defaultRenderMode( renderMode )
					.forInstance( entityViewContext.getEntity() )
					.build( builderContext );

			String propertyAsHtml = renderViewElement( controls.get( targetProperty ) );

			String titleMessageCode = resolveTitleMessageCode( viewFactory );
			String title = resolveMessageCode( entityViewContext, titleMessageCode, propertyAsHtml );
			String subTitle = resolveMessageCode( entityViewContext, titleMessageCode + ".subText", propertyAsHtml );

			// Replace the header with the inline control
			header.clearChildren();
			header.addChild( headerViewElementResolver.apply( title, subTitle ) );
		}
	}

	private String resolveTitleMessageCode( EntityViewFactory viewFactory ) {
		if ( viewFactory instanceof DispatchingEntityViewFactory ) {
			EntityViewProcessorRegistry processorRegistry = ( (DispatchingEntityViewFactory) viewFactory ).getProcessorRegistry();
			String code = processorRegistry.getProcessor( SingleEntityPageStructureViewProcessor.class.getName(),
			                                              SingleEntityPageStructureViewProcessor.class )
			                               .map( p -> {
				                               Field titleMessageCode = ReflectionUtils.findField( SingleEntityPageStructureViewProcessor.class,
				                                                                                   "titleMessageCode",
				                                                                                   String.class );
				                               if ( titleMessageCode != null ) {
					                               ReflectionUtils.makeAccessible( titleMessageCode );
					                               try {
						                               return titleMessageCode.get( p );
					                               }
					                               catch ( IllegalAccessException e ) {
						                               e.printStackTrace();
					                               }
				                               }
				                               return null;
			                               } )
			                               .map( Object::toString )
			                               .orElse( null );
			if ( code != null ) {
				return code;
			}
		}
		return EntityMessages.PAGE_TITLE_VIEW;
	}

	private String renderViewElement( ViewElement viewElement ) {
		ServletRequestAttributes ra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		IEngineConfiguration configuration = templateEngine.getConfiguration();
		WebExpressionContext context =
				new WebExpressionContext(
						configuration,
						ra.getRequest(),
						ra.getResponse(),
						ra.getRequest().getServletContext(),
						LocaleContextHolder.getLocale(),
						Collections.singletonMap( "element", viewElement )
				);

		// todo custom template engine so we don't have to provide a template that holds next to no html?
		return templateEngine.process( "th/experimental/inline-view-element", context );
	}

	private String resolveMessageCode( EntityViewContext entityViewContext, String messageCode, Object... arguments ) {
		EntityMessages entityMessages = entityViewContext.getEntityMessages();
		return StringUtils.defaultIfEmpty( entityMessages.withNameSingular( messageCode, arguments ), null );
	}

	private ViewElement createHeaderViewElement( String title, String subTitle ) {
		NodeViewElement heading = new NodeViewElement( ELEMENT_PAGE_TITLE, "h3" );
		heading.addCssClass( "page-header" );
		heading.addChild( html.unescapedText( title ) );
		heading.addChild( new TextViewElement( " " ) );

		Optional.ofNullable( subTitle )
		        .ifPresent( st -> {
			        NodeViewElement actionsElement = new NodeViewElement( ELEMENT_PAGE_TITLE_SUB_TEXT, "small" );
			        actionsElement.addCssClass( "axu-text-muted" );
			        actionsElement.addChild( html.unescapedText( subTitle ) );
			        heading.addChild( actionsElement );
		        } );

		return heading;
	}

	private ViewElement createAssociationHeaderViewElement( String title, String subTitle ) {
		NodeViewElement heading = new NodeViewElement( "tab-pane-title", "h4" );
		heading.addChild( html.unescapedText( title ) );

		Optional.ofNullable( subTitle )
		        .ifPresent( st -> {
			        NodeViewElement actionsElement = new NodeViewElement( "tab-pane-title-subtext", "small" );
			        actionsElement.addChild( html.unescapedText( subTitle ) );
			        heading.addChild( actionsElement );
		        } );

		return heading;
	}

	private void configureRefreshableControlAsBreadcrumbValue( EntityViewContext entityViewContext ) {
		Menu adminMenu = menuFactory.getMenuWithName( AdminMenu.NAME );
		if ( adminMenu != null ) {
			Menu breadcrumbLeaf = adminMenu.getLowestSelectedItem();
			EntityPropertyDescriptor labelProperty = entityViewContext.getPropertyRegistry().getProperty( EntityPropertyRegistry.LABEL );
			editableValuesUtils.resolveEntityPropertyId( entityViewContext, labelProperty )
			                   .ifPresent( propertyId ->
					                               breadcrumbLeaf.setAttribute(
							                               NavComponentBuilder.ATTR_LINK_VIEW_ELEMENT,
							                               html.span( propertyId( propertyId ), html.text( breadcrumbLeaf.getTitle() ) )
					                               )
			                   );
		}
	}
}
