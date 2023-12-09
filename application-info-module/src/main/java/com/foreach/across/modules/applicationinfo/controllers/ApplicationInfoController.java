package com.foreach.across.modules.applicationinfo.controllers;

import com.foreach.across.modules.applicationinfo.business.AcrossApplicationInfo;
import com.foreach.across.modules.debugweb.mvc.DebugWebController;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@DebugWebController
public class ApplicationInfoController
{
	public static final String PATH = "/applicationInfo";

	@Autowired
	private AcrossApplicationInfo applicationInfo;

	@RequestMapping(PATH)
	public String dashboard( Model model ) {
		model.addAttribute( "applicationInfo", applicationInfo );
		model.addAttribute(
				"bootstrapDurationFormatted",
				DurationFormatUtils.formatDuration( applicationInfo.getBootstrapDuration(), "m'm' s's'" )
		);
		model.addAttribute(
				"uptimeFormatted",
				DurationFormatUtils.formatDuration( applicationInfo.getUptime(), "d 'days' HH:mm:ss" )
		);

		return "th/applicationinfo/dashboard";
	}
}
