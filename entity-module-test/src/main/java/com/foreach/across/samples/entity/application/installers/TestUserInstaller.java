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

package com.foreach.across.samples.entity.application.installers;

import com.foreach.across.core.annotations.Installer;
import com.foreach.across.core.annotations.InstallerMethod;
import com.foreach.across.core.installers.InstallerPhase;
import com.foreach.across.samples.entity.application.business.Group;
import com.foreach.across.samples.entity.application.business.User;
import com.foreach.across.samples.entity.application.repositories.GroupRepository;
import com.foreach.across.samples.entity.application.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.Assert;

import java.util.stream.Stream;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Order(2)
@Installer(description = "Installs a number of test users for filtering", phase = InstallerPhase.AfterModuleBootstrap)
public class TestUserInstaller
{
	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private UserRepository userRepository;

	@InstallerMethod
	public void installTestUsersInGroup() {
		Group group = groupRepository.findOne( -1L );

		Stream.of( "john", "joey", "jane", "paul" )
		      .forEach( s -> {
			      for ( int i = 0; i < 15; i++ ) {
				      createUserInGroup( s + " " + i, group );
			      }
		      } );

		// verify installed groups
		Assert.isTrue( userRepository.findByGroup( group, new PageRequest( 0, 30 ) ).getTotalElements() == 60 );
		Assert.isTrue( userRepository.findByGroupAndNameContaining( group, "j", new PageRequest( 0, 30 ) )
		                             .getTotalElements() == 45 );
	}

	private void createUserInGroup( String name, Group group ) {
		User user = new User();
		user.setName( name );
		user.setGroup( group );

		userRepository.save( user );
	}
}
