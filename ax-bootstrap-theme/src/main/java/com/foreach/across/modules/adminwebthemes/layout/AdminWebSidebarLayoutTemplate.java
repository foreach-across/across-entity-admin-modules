package com.foreach.across.modules.adminwebthemes.layout;

import com.foreach.across.modules.adminweb.AdminWeb;
import com.foreach.across.modules.adminweb.events.UserContextAdminMenuGroup;
import com.foreach.across.modules.adminweb.menu.AdminMenu;
import com.foreach.across.modules.adminweb.menu.AdminMenuEvent;
import com.foreach.across.modules.adminweb.ui.AdminWebLayoutTemplate;
import com.foreach.across.modules.bootstrapui.components.builder.NavComponentBuilder;
import com.foreach.across.modules.bootstrapui.resource.BootstrapUiWebResources;
import com.foreach.across.modules.web.menu.Menu;
import com.foreach.across.modules.web.resource.WebResource;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.resource.WebResourceRule;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements.bootstrap;
import static com.foreach.across.modules.web.resource.WebResource.css;

public class AdminWebSidebarLayoutTemplate extends AdminWebLayoutTemplate
{
	public AdminWebSidebarLayoutTemplate() {
		super( AdminWeb.NAME, "th/adminweb-themes/sidebar-layout" );
	}

	@EventListener
	public void customizeTopNavigation( AdminMenuEvent adminMenu ) {
		adminMenu.builder().group( UserContextAdminMenuGroup.MENU_PATH ).attribute( NavComponentBuilder.ATTR_ICON_ONLY, true );
	}

	@Override
	protected void registerWebResources( WebResourceRegistry registry ) {
		super.registerWebResources( registry );

		registry.apply(
				WebResourceRule.add( css( "@static:/adminweb-themes/css/adminweb-sidebar-bootstrap.css" ) )
				               .withKey( BootstrapUiWebResources.NAME )
				               .replaceIfPresent( true )
				               .toBucket( WebResource.CSS ),
				WebResourceRule.add( css( "@static:/adminweb-themes/css/adminweb-sidebar-theme.css" ) )
				               .withKey( "adminweb-theme" )
				               .order( Ordered.LOWEST_PRECEDENCE )
				               .toBucket( WebResource.CSS ),
				WebResourceRule.remove().withKey( BootstrapUiWebResources.ACROSS_BOOTSTRAP_UTILITIES )
		);
	}

	@Override
	public void applyTemplate( HttpServletRequest request,
	                           HttpServletResponse response,
	                           Object handler,
	                           ModelAndView modelAndView ) {
		if ( modelAndView != null ) {
			Map<String, Object> model = modelAndView.getModel();
			model.put( "adminWebSidebarFixed", false );

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
						                         .filter( navPosition( NAVBAR, true ) )
						                         .build()
				);
				model.computeIfAbsent(
						MODEL_ATTR_NAVBAR_RIGHT,
						key -> bootstrap.builders.nav()
						                         .menu( adminMenu )
						                         .with( css.of( "top-nav-menu" ) )
						                         .keepGroupsAsGroup( true )
						                         .replaceGroupBySelectedItem( false )
						                         .includePathAsDataAttribute( isIncludeNavPathAsDataAttribute() )
						                         .filter( navPosition( NAVBAR_RIGHT, false ) )
						                         .build()
				);
				model.computeIfAbsent(
						MODEL_ATTR_SIDEBAR,
						key -> bootstrap.builders.panels()
						                         .menu( adminMenu )
						                         .keepGroupsAsGroup( true )
						                         .includePathAsDataAttribute( isIncludeNavPathAsDataAttribute() )
						                         .filter( navPosition( SIDEBAR, true ) )
						                         .build()
				);
				model.computeIfAbsent(
						MODEL_ATTR_BREADCRUMB,
						key -> bootstrap.builders
								.breadcrumb()
								.with( css.of( "top-nav-breadcrumb" ) )
								.menu( adminMenu )
								.includePathAsDataAttribute( isIncludeNavPathAsDataAttribute() )
								.filter( item -> !Boolean.FALSE.equals( item.getAttribute( AdminMenu.ATTR_BREADCRUMB ) ) )
								.build()
				);
			}

			super.applyTemplate( request, response, handler, modelAndView );
		}
	}

	@SuppressWarnings("all")
	static Predicate<Menu> navPosition( String position, boolean defaultInclude ) {
		return menu ->
				Optional.ofNullable( menu.getAttribute( AdminMenu.ATTR_NAV_POSITION ) )
				        .map(
						        value -> value instanceof String[]
								        ? ArrayUtils.contains( (String[]) value, position )
								        : position.equals( value )
				        )
				        .orElse( defaultInclude || menu.getLevel() > 1 );
	}
}
