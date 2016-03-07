/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.foreach.across.modules.adminweb.controllers;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.Module;
import com.foreach.across.modules.adminweb.AdminWeb;
import com.foreach.across.modules.adminweb.AdminWebModuleSettings;
import com.foreach.across.modules.adminweb.annotations.AdminWebController;
import com.foreach.across.modules.adminweb.config.LocaleProperties;
import com.foreach.across.modules.adminweb.config.RememberMeProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

@AdminWebController
public class AuthenticationController
{
	@Value( "${adminWebModule.login.template:}" )
	private String loginTemplate;

	@Autowired
	private AdminWeb adminWeb;

	@Autowired
	@Module(AcrossModule.CURRENT_MODULE)
	private AdminWebModuleSettings settings;

	@Autowired
	private RememberMeProperties rememberMeProperties;

	@Autowired
	private LocaleProperties localeProperties;

	@RequestMapping(value = { "", "/" })
	public String dashboard() {
		String path = settings.getDashboard();

		if ( !StringUtils.equals( path, "/" ) ) {
			return adminWeb.redirect( path );
		}

		return "th/adminweb/dashboard";
	}

	@RequestMapping("/login")
	public String login( Model model ) {
		model.addAttribute( "isRememberMeEnabled", rememberMeProperties.isEnabled() );
		model.addAttribute( "localeOptions", buildLocaleOptions() );

		if ( StringUtils.isNotBlank( loginTemplate ) ) {
			return loginTemplate;
		}

		return "th/adminweb/login";
	}

	@RequestMapping("/logout")
	public String logout( HttpServletRequest request ) throws ServletException {
		request.logout();

		return adminWeb.redirect( "/login?logout" );
	}

	private List<LocaleOption> buildLocaleOptions() {
		List<LocaleOption> options = new LinkedList<>();
		Locale currentLocale = LocaleContextHolder.getLocale();

		for ( Locale candidate : localeProperties.getOptions() ) {
			LocaleOption option = new LocaleOption();
			option.setLocale( candidate );
			option.setSelected( currentLocale.equals( option.getLocale() ) );
			option.setLabel( StringUtils.upperCase( option.getLocale().getLanguage() ) );

			options.add( option );
		}

		return options;
	}

	public static class LocaleOption
	{
		private Locale locale;
		private String label;
		private boolean selected;

		public Locale getLocale() {
			return locale;
		}

		public void setLocale( Locale locale ) {
			this.locale = locale;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel( String label ) {
			this.label = label;
		}

		public boolean isSelected() {
			return selected;
		}

		public void setSelected( boolean selected ) {
			this.selected = selected;
		}
	}
}
