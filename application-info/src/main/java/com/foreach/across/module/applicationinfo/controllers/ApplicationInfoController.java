package com.foreach.across.module.applicationinfo.controllers;

import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.module.applicationinfo.business.ApplicationInfo;
import com.foreach.across.modules.debugweb.mvc.DebugWebController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@AcrossDepends(required = "DebugWebModule")
@DebugWebController
public class ApplicationInfoController
{
	@Autowired
	private ApplicationInfo applicationInfo;

	@RequestMapping({ "", "/" })
	public String dashboard( Model model ) {
		model.addAttribute( "applicationInfo", applicationInfo );

		return "th/applicationinfo/dashboard";
	}
}
