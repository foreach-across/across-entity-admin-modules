package com.foreach.across.modules.adminweb;

import com.foreach.across.modules.web.context.PrefixingPathContext;
import org.springframework.beans.factory.annotation.Autowired;

public final class AdminWeb extends PrefixingPathContext
{
	public static final String MODULE = "AdminWebModule";

	public static final String LAYOUT_TEMPLATE_CSS = "/css/adminweb/adminweb.css";
	public static final String LAYOUT_TEMPLATE = "th/adminweb/layouts/adminPage";

	@Autowired
	private AdminWebModuleSettings settings;

	private final String title;

	public AdminWeb( String prefix, String title ) {
		super( prefix );

		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public AdminWebModuleSettings getSettings() {
		return settings;
	}
}
