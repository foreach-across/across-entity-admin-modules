package com.foreach.across.testapplication.application.config;

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.core.annotations.ModuleConfiguration;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.List;

import static org.springframework.security.crypto.factory.PasswordEncoderFactories.createDelegatingPasswordEncoder;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ModuleConfiguration(SpringSecurityModule.NAME)
@EnableGlobalAuthentication
@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig
{

	@Autowired
	public void configureGlobal( AuthenticationManagerBuilder auth ) throws Exception {
		PasswordEncoder userPasswordEncoder = createDelegatingPasswordEncoder();
		UserDetailsService userDetailsService = new InMemoryUserDetailsManager(
				new User( "admin", "{noop}admin", List.of() )
		);

		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider( userPasswordEncoder );
		authenticationProvider.setUserDetailsService( userDetailsService );
		authenticationProvider.afterPropertiesSet();
		auth.authenticationProvider( authenticationProvider );
		// Configure the global user details for remember me functionality
		auth.userDetailsService( userDetailsService ).passwordEncoder( userPasswordEncoder );
	}

	@Bean
	@Order(-1)
	@Exposed
	public SecurityFilterChain securityFilterChain( HttpSecurity http ) throws Exception {
		CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
		// set the name of the attribute the CsrfToken will be populated on
		requestHandler.setCsrfRequestAttributeName( null );
		http.authorizeHttpRequests( httpMatcher -> {
			    httpMatcher.requestMatchers( "/" ).permitAll();
			    httpMatcher.requestMatchers( "/admin/login" ).permitAll();
			    httpMatcher.requestMatchers( "/webjars/**" ).permitAll();
			    httpMatcher.requestMatchers( "/across/resources/static/**" ).permitAll();
			    httpMatcher.anyRequest().authenticated();
		    } )
		    .csrf( ( csrf ) -> csrf
				    .csrfTokenRepository( CookieCsrfTokenRepository.withHttpOnlyFalse() )
				    .csrfTokenRequestHandler( requestHandler )
		    )
		    .formLogin( ( form ) -> {
			    form.defaultSuccessUrl( "/admin" ).loginPage( "/admin/login" );
		    } ).logout( ( logout ) -> {
			    logout.logoutUrl( "/admin/logout" ).logoutRequestMatcher( new AntPathRequestMatcher( "/admin/logout" ) );
		    } );
		return http.build();
	}
}
