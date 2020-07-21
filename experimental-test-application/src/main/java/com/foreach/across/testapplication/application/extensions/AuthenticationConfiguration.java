package com.foreach.across.testapplication.application.extensions;

import com.foreach.across.core.annotations.ModuleConfiguration;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@ModuleConfiguration(SpringSecurityModule.NAME)
@EnableGlobalAuthentication
public class AuthenticationConfiguration
{
	@Autowired
	public void configureGlobal( AuthenticationManagerBuilder auth ) throws Exception {
		auth.inMemoryAuthentication()
		    .withUser( "admin" ).password( "{noop}admin" )
		    .authorities( new SimpleGrantedAuthority( "access administration" ) );
	}
}
