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

package com.foreach.across.samples.entity.application.controllers;

import com.foreach.across.modules.web.mvc.condition.AbstractCustomRequestCondition;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Arne Vandamme
 * @since 4.0.0
 */
public class TestCondition extends AbstractCustomRequestCondition<TestCondition>
{
	@Override
	public void setAnnotatedElement( AnnotatedElement annotatedElement ) {

	}

	@Override
	protected Collection<?> getContent() {
		return Collections.singleton("url");
	}

	@Override
	protected String getToStringInfix() {
		return " && ";
	}

	@Override
	public TestCondition combine( TestCondition testCondition ) {
		return null;
	}

	@Override
	public TestCondition getMatchingCondition( HttpServletRequest httpServletRequest ) {
		if ( new AntPathRequestMatcher( "/hello").matches( httpServletRequest ) ) {
			return this;
		}
		return null;
	}

	@Override
	public int compareTo( TestCondition testCondition, HttpServletRequest httpServletRequest ) {
		return 0;
	}
}
