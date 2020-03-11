package com.foreach.across.modules.adminwebthemes.layout;

import com.foreach.across.modules.adminweb.ui.AdminWebLayoutTemplate;
import com.foreach.across.modules.bootstrapui.resource.BootstrapUiWebResources;
import com.foreach.across.modules.web.resource.WebResource;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.resource.WebResourceRule;
import org.springframework.core.Ordered;

import static com.foreach.across.modules.web.resource.WebResource.css;

/**
 * Configures the pre-Across 5 layout. This is basically the default layout as generated by
 * AdminWebModule itself, with the classic stylesheet applied.
 *
 * @author Arne Vandamme
 * @since 0.0.1
 */
public class AdminWebClassicLayoutTemplate extends AdminWebLayoutTemplate {
    @Override
    protected void registerWebResources(WebResourceRegistry registry) {
        super.registerWebResources(registry);

        registry.apply(
                WebResourceRule.add(css("@static:/adminweb-themes/css/adminweb-classic-bootstrap.css"))
                        .withKey(BootstrapUiWebResources.NAME)
                        .replaceIfPresent(true)
                        .toBucket(WebResource.CSS),
                WebResourceRule.add(css("@static:/adminweb-themes/css/adminweb-classic-theme.css"))
                        .withKey("adminweb-theme")
                        .order(Ordered.LOWEST_PRECEDENCE)
                        .toBucket(WebResource.CSS),
                WebResourceRule.remove().withKey(BootstrapUiWebResources.NAME + "ui-ax-utils")
        );
    }
}
