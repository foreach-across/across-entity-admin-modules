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

package com.foreach.across.samples.entity.application.web;

import com.blazebit.persistence.view.EntityViewManager;
import com.foreach.across.samples.entity.application.repositories.SimpleUserRepository;
import com.foreach.across.samples.entity.application.view.GroupView;
import com.foreach.across.samples.entity.application.view.UserSimpleView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.stream.Collectors;

@Controller
public class TestController
{
	@Autowired
	private SimpleUserRepository userRepository;
	@Autowired
	private EntityViewManager entityViewManager;

	@GetMapping("/tes")
	@ResponseBody
	public String test() {
		UserSimpleView userSimpleView = entityViewManager.create( UserSimpleView.class );
		//userSimpleView.setName( RandomStringUtils.randomAlphanumeric( 30 ) );
		GroupView groupView = entityViewManager.create( GroupView.class );
		//groupView.setName( RandomStringUtils.randomAlphanumeric( 30 ) );
		//userSimpleView.setGroup( groupView );
		userRepository.save( userSimpleView );
		return userRepository.findAll().stream().map( u -> u.getName() ).collect( Collectors.joining( ", " ) );
	}
}
