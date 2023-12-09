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
import com.foreach.across.samples.entity.application.repositories.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.stream.Stream;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Order(1)
@Installer(description = "Installs a number of test groups for filtering", phase = InstallerPhase.AfterModuleBootstrap)
@RequiredArgsConstructor
public class TestGroupInstaller
{
	private long groupId = -1;

	private final GroupRepository groupRepository;

	@InstallerMethod
	public void installTestGroups() {
		Stream.of( "animals", "small people", "people without shoes", "large people" )
		      .forEach( s -> {
			      for ( int i = 0; i < 15; i++ ) {
				      createGroup( s + " " + i );
			      }
		      } );

		// verify installed groups
		Page<Group> filteredGroups = groupRepository.findByNameContaining( "people", PageRequest.of( 0, 30 ) );
		if ( filteredGroups.getTotalElements() != 45 ) {
			throw new RuntimeException( "Incorrect results returned for groups containing 'people'" );
		}
		if ( filteredGroups.getTotalPages() != 2 ) {
			throw new RuntimeException( "Incorrect pages returned for groups containing 'people'" );
		}
	}

	private void createGroup( String name ) {
		Group group = new Group();
		group.setNewEntityId( groupId-- );
		group.setName( name );
		groupRepository.save( group );
	}
}
