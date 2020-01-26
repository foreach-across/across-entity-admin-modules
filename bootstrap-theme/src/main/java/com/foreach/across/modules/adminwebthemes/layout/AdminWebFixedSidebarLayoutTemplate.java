package com.foreach.across.modules.adminwebthemes.layout;

import com.foreach.across.modules.adminweb.AdminWeb;
import com.foreach.across.modules.adminweb.menu.AdminMenu;
import com.foreach.across.modules.adminweb.ui.AdminWebLayoutTemplate;
import com.foreach.across.modules.bootstrapui.resource.BootstrapUiWebResources;
import com.foreach.across.modules.web.resource.WebResource;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.resource.WebResourceRule;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements.bootstrap;
import static com.foreach.across.modules.web.resource.WebResource.css;

public class AdminWebFixedSidebarLayoutTemplate extends AdminWebLayoutTemplate
{
	public AdminWebFixedSidebarLayoutTemplate() {
		super( AdminWeb.NAME, "th/adminweb-themes/sidebar-layout" );
	}

	@Override
	protected void registerWebResources( WebResourceRegistry registry ) {
		super.registerWebResources( registry );

		registry.apply(
				WebResourceRule.add( css( "@static:/adminweb-themes/css/adminweb-sidebar-fixed-bootstrap.css" ) )
				               .withKey( BootstrapUiWebResources.NAME )
				               .replaceIfPresent( true )
				               .toBucket( WebResource.CSS ),
				WebResourceRule.add( css( "@static:/adminweb-themes/css/adminweb-sidebar-fixed-theme.css" ) )
				               .withKey( "adminweb-theme" )
				               .order( Ordered.LOWEST_PRECEDENCE )
				               .toBucket( WebResource.CSS )
		);
	}

	@Override
	public void applyTemplate( HttpServletRequest request,
	                           HttpServletResponse response,
	                           Object handler,
	                           ModelAndView modelAndView ) {
		if ( modelAndView != null ) {
			Map<String, Object> model = modelAndView.getModel();
			model.put( "adminWebSidebarFixed", true );

			AdminMenu adminMenu = (AdminMenu) request.getAttribute( AdminMenu.NAME );

			if ( adminMenu != null ) {
				model.computeIfAbsent(
						MODEL_ATTR_NAVBAR,
						key -> bootstrap.builders.nav()
						                         .menu( adminMenu )
						                         .stacked()
						                         .keepGroupsAsGroup( true )
						                         .replaceGroupBySelectedItem( false )
						                         .includePathAsDataAttribute( isIncludeNavPathAsDataAttribute() )
						                         .filter( AdminWebSidebarLayoutTemplate.navPosition( NAVBAR, true ) )
						                         .build()
				);
				model.computeIfAbsent(
						MODEL_ATTR_NAVBAR_RIGHT,
						key -> bootstrap.builders.nav()
						                         .menu( adminMenu )
						                         .stacked()
						                         .keepGroupsAsGroup( true )
						                         .replaceGroupBySelectedItem( false )
						                         .includePathAsDataAttribute( isIncludeNavPathAsDataAttribute() )
						                         .filter( AdminWebSidebarLayoutTemplate.navPosition( NAVBAR_RIGHT, false ) )
						                         .build()
				);
				model.computeIfAbsent(
						MODEL_ATTR_SIDEBAR,
						key -> bootstrap.builders.panels()
						                         .menu( adminMenu )
						                         .keepGroupsAsGroup( true )
						                         .includePathAsDataAttribute( isIncludeNavPathAsDataAttribute() )
						                         .filter( AdminWebSidebarLayoutTemplate.navPosition( SIDEBAR, true ) )
						                         .build()
				);
				model.computeIfAbsent(
						MODEL_ATTR_BREADCRUMB,
						key -> bootstrap.builders
								.breadcrumb()
								.menu( adminMenu )
								.includePathAsDataAttribute( isIncludeNavPathAsDataAttribute() )
								.filter( item -> !Boolean.FALSE.equals( item.getAttribute( AdminMenu.ATTR_BREADCRUMB ) ) )
								.build()
				);
			}

			super.applyTemplate( request, response, handler, modelAndView );
		}
	}
}
